package yz.acceleratormod.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.network.PacketHandler;
import yz.acceleratormod.network.reflection.ReflectionSyncer;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;
import yz.acceleratormod.tool.YzUtil;

import java.util.ArrayList;
import java.util.List;

public class ChokerFunction {

    /**
     * This function should be called from ACCLArmor::onArmor().
     *
     * @param world     Executing world
     * @param player    Player who called this function
     * @param itemStack ItemStack of choker which is equipped by Player
     */
    public void customTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (YzUtil.isLgClient())
            this.reflectEntity(world, player);
    }

    /**
     * This function should be called from Chokerfunction::customTick
     *
     * @param world  Executing world
     * @param player Reflecting player
     */
    private void reflectEntity(World world, EntityPlayer player) {
        List<Entity> refList = new ArrayList<>();
        for (Entity entity : EventHandlerChoker.entityToReflect) {
            if (player.boundingBox.expand(1.2F, 1.2F, 1.2F).intersectsWith(entity.boundingBox))
                refList.add(entity);
        }
        EventHandlerChoker.entityToReflect.removeIf(e -> e.isDead);
        if (this.isActivated(player)) {
            for (Entity entity : refList) {
                Vec3 v = Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ).normalize();
                if (entity instanceof EntityFallingBlock) {
                    EntityFallingBlock fallingBlock = ((EntityFallingBlock) entity);
                    EventHandlerChoker.entityToReflect.remove(fallingBlock);
                    if (world.isRemote) {
                        fallingBlock.entityDropItem((new ItemStack(fallingBlock.func_145805_f(), 1, 0)), 0.F);
                        fallingBlock.setDead();
                    }
                    SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 1.F, 1.F));
                } else if (v.dotProduct(player.getLookVec()) < 0) {
                    flipVelocity(world, entity);
                    PacketHandler.INST_refSync.sendToServer(new ReflectionSyncer(player, entity));
                    SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 1.F, 1.F));
                }
            }
        }
    }

    /**
     * @return Whether Player's choker is enabled
     */
    public boolean isActivated(EntityPlayer player) {
        if (player.getCurrentArmor(3) == null)
            return false;
        NBTTagCompound nbt = YzUtil.getNBTTag(this.getPlayerHeadArmor(player));
        if (nbt.getInteger(ArmorChoker.battRemainTag) == 0)
            return false;
        return nbt.getBoolean(ArmorChoker.activeTag);
    }

    /**
     * @return Weather Player is wearing choker
     */
    public boolean isWearingChoker(EntityPlayer player) {
        if (player.getCurrentArmor(3) == null)
            return false;
        return this.getPlayerHeadArmor(player).getItem() instanceof ArmorChoker;
    }

    /**
     * @return Helmet which was equipped by player
     */
    private ItemStack getPlayerHeadArmor(EntityPlayer player) {
        return player.getCurrentArmor(3);
    }

    /**
     * Change entity::motionXYZ's signs
     *
     * @param world  Executing world
     * @param entity Target entity
     */
    public void flipVelocity(World world, Entity entity) {
        entity.motionX = -entity.motionX;
        entity.motionY = -entity.motionY;
        entity.motionZ = -entity.motionZ;
    }
}
