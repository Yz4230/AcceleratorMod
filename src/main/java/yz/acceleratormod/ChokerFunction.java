package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.armor.ACCLArmor;
import yz.acceleratormod.network.PacketHandler;
import yz.acceleratormod.network.reflection.ReflectionSyncer;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;
import yz.acceleratormod.tool.YzUtil;

import java.util.*;

public class ChokerFunction {
    private Map<EntityPlayer, Boolean> chokerActive = new HashMap<>();
    public static Set<String> entityList = new HashSet<>();
    private static Set<Entity> entityToReflect = new HashSet<>();
    public static final String[] defaultEntityToReflect =
            {"Arrow", "Snowball", "WitherSkull", "ThrownPotion", "SmallFireball", "PrimedTnt", "Fireball"};

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> entity instanceof EntityPlayer && this.isActivated(((EntityPlayer) entity)));

        List<ChunkPosition> positionList = new ArrayList<>();
        for (ChunkPosition position : event.getAffectedBlocks())
            for (Object o : event.world.playerEntities) {
                EntityPlayer player = ((EntityPlayer) o);
                if (player.posX == position.chunkPosX && player.posZ == position.chunkPosZ && this.isActivated(player))
                    positionList.add(position);
            }
        event.getAffectedBlocks().removeAll(positionList);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttackEvent(LivingAttackEvent event) {
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();

        if (receiveEntity instanceof EntityPlayer && this.isActivated(((EntityPlayer) receiveEntity))) {
            if (event.source.equals(DamageSource.fall))
                event.setCanceled(true);
        } else if (giveEntity instanceof EntityPlayer && event.ammount != receiveEntity.getHealth() && this.isActivated(((EntityPlayer) giveEntity))) {
            receiveEntity.attackEntityFrom(DamageSource.causePlayerDamage(
                    (EntityPlayer) giveEntity).setDamageBypassesArmor().setDamageIsAbsolute(), receiveEntity.getHealth());
            receiveEntity.knockBack(giveEntity, 256, 256 * (giveEntity.posX - receiveEntity.posX), 256 * (giveEntity.posZ - receiveEntity.posZ));
            if (YzUtil.isLgServer())
                SoundManager.Play(new SoundAtEntity(ACCL.strongPunchSnd, giveEntity, 1.F, 1.F));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (receiveEntity instanceof EntityPlayer && this.isActivated(((EntityPlayer) receiveEntity))) {
            if (giveEntity instanceof EntityLivingBase) {
                event.setCanceled(true);
                if (!event.source.isProjectile()) {
                    giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                    ((EntityLivingBase) giveEntity).knockBack(null, 0, 2000, 2000);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntitySlime) {
            event.setCanceled(true);
            return;
        }
        if (event.entity instanceof EntityFallingBlock || entityList.contains(EntityList.getEntityString(event.entity)))
            entityToReflect.add(event.entity);
    }

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
        for (Entity entity : entityToReflect) {
            if (player.boundingBox.expand(1.2F, 1.2F, 1.2F).intersectsWith(entity.boundingBox))
                refList.add(entity);
        }
        entityToReflect.removeIf(e -> e.isDead);
        if (this.isActivated(player)) {
            for (Entity entity : refList) {
                Vec3 v = Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ).normalize();
                if (entity instanceof EntityFallingBlock) {
                    EntityFallingBlock fallingBlock = ((EntityFallingBlock) entity);
                    entityToReflect.remove(fallingBlock);
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
    private boolean isActivated(EntityPlayer player) {
        if (player.getCurrentArmor(3) == null)
            return false;
        NBTTagCompound nbt = YzUtil.getNBTTag(this.getPlayerHeadArmor(player));
        if (nbt.getInteger(ACCLArmor.battRemainTag) == 0)
            return false;
        return nbt.getBoolean(ACCLArmor.activeTag);
    }

    /**
     * @return Weather Player is wearing choker
     */
    private boolean isWearingChoker(EntityPlayer player) {
        if (player.getCurrentArmor(3) == null)
            return false;
        return this.getPlayerHeadArmor(player).getItem() instanceof ACCLArmor;
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
    public static void flipVelocity(World world, Entity entity) {
        entity.motionX = -entity.motionX;
        entity.motionY = -entity.motionY;
        entity.motionZ = -entity.motionZ;
    }
}
