package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.armor.ACCLArmor;
import yz.acceleratormod.tool.YzUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ChokerFunction {
    public static Map<EntityPlayer, Boolean> playerStates = new WeakHashMap<>();
    public static List<String> entityList = new ArrayList<String>();
    public static List<Entity> entityToReflecting = new ArrayList<Entity>();
    public static String[] defalutEntityToReflect =
            {"Arrow", "Egg", "Snowball", "FireBall", "SmallFireBall", "Potion", "WitherSkull", "FallingBlock"};

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (this.isActivated()) {
            EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
            event.getAffectedEntities().removeIf(entity -> entity instanceof EntityPlayer);

            event.getAffectedBlocks().removeIf(current_block -> current_block.chunkPosX == Math.floor(entityPlayer.posX) &&
                    current_block.chunkPosZ == Math.floor(entityPlayer.posZ));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttackEvent(LivingAttackEvent event) {
        EntityLivingBase recieveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();

        if (this.isActivated()) {
            if (recieveEntity instanceof EntityPlayer) {
                if (event.source.equals(DamageSource.fall))
                    event.setCanceled(true);
            } else if (giveEntity instanceof EntityPlayer && event.ammount != recieveEntity.getHealth()) {
                recieveEntity.attackEntityFrom(DamageSource.causePlayerDamage(
                        (EntityPlayer) giveEntity).setDamageBypassesArmor().setDamageIsAbsolute(), recieveEntity.getHealth());
                recieveEntity.knockBack(giveEntity, 256, 256 * (giveEntity.posX - recieveEntity.posX), 256 * (giveEntity.posZ - recieveEntity.posZ));
                if (YzUtil.isServer())
                    giveEntity.worldObj.playSoundAtEntity(giveEntity, ACCL.MOD_ID + ":strong_punch", 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase recieveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (this.isActivated()) {
            if (recieveEntity instanceof EntityPlayer) {
                if (giveEntity instanceof EntityLivingBase) {
                    event.setCanceled(true);
                    if (!event.source.isProjectile()) {
                        giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                        ((EntityLivingBase) giveEntity).knockBack(null, 0, 2000, 2000);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntitySlime)
            event.setCanceled(true);
        for (String s : entityList)
            if (s.equals(EntityList.getEntityString(event.entity))) {
                entityToReflecting.add(event.entity);
                break;
            }
    }

    public void customTick(World world, EntityPlayer player, ItemStack itemStack) {
        //this.reflectEntity(world, player);
    }

    private void reflectEntity(World world, EntityPlayer player) {
        List<Entity> refList = new ArrayList<>();
        entityToReflecting.removeIf(e -> e.isDead);
        entityToReflecting.removeIf(e -> e.motionX == 0 && e.motionY == 0 && e.motionZ == 0);
        for (Entity entity : entityToReflecting) {
            if (entity.getDistanceToEntity(player) < 2.5)
                refList.add(entity);
        }
        if (this.isActivated()) {
            for (Entity entity : refList) {
                entity.motionX = -entity.motionX;
                entity.motionY = -entity.motionY;
                entity.motionZ = -entity.motionZ;
            }
            if (refList.size() > 0 && world.isRemote)
                world.playSoundAtEntity(player, ACCL.MOD_ID + ":reflection", 0.5F, 1.2F);
        }
        System.out.println(entityToReflecting.size());
    }

    private boolean isActivated() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null)
            return false;
        NBTTagCompound nbt = YzUtil.getNBTTag(getPlayerHeadArmor());
        if (nbt.getInteger(ACCLArmor.battRemainTag) == 0)
            return false;
        return nbt.getBoolean(ACCLArmor.activeTag);
    }

    public boolean isWearingChoker() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null)
            return false;
        return getPlayerHeadArmor().getItem() instanceof ACCLArmor;
    }

    private static ItemStack getPlayerHeadArmor() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        return entityPlayer.getCurrentArmor(3);
    }
}
