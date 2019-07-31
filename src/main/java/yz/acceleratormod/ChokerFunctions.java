package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.armor.AcceleratorArmor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChokerFunctions {
    public static List<String> entityList = new ArrayList<String>();
    public static List<Entity> entityToReflecting = new ArrayList<Entity>();
    public static String[] defalutEntityToReflect =
            {"Arrow", "Egg", "Snowball", "FireBall", "SmallFireBall", "Potion", "WitherSkull", "FallingBlock"};

    @SubscribeEvent
    public void inputKey(InputEvent.KeyInputEvent event) {
        if (false) {
            AcceleratorArmor armor = (AcceleratorArmor) getPlayerHeadArmor();
            armor.switchMode();
            EntityClientPlayerMP entityPlayer = Minecraft.getMinecraft().thePlayer;
            //entityPlayer.sendChatMessage("Choker was " + (armor.isActivated() ? "Enabled" : "Disabled"));
        }
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (this.isActivated()) {
            EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
            Iterator<Entity> entity_iter = event.getAffectedEntities().iterator();
            while (entity_iter.hasNext()) {
                if (entity_iter.next() instanceof EntityPlayer)
                    entity_iter.remove();
            }

            Iterator<ChunkPosition> block_iter = event.getAffectedBlocks().iterator();
            while (block_iter.hasNext()) {
                ChunkPosition current_block = block_iter.next();
                if (current_block.chunkPosX == Math.floor(entityPlayer.posX) &&
                        current_block.chunkPosZ == Math.floor(entityPlayer.posZ)) {
                    block_iter.remove();
                }
            }
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
            } else if (giveEntity instanceof EntityPlayer)
                recieveEntity.attackEntityFrom(DamageSource.generic, recieveEntity.getHealth());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase recieveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (this.isActivated()) {
            if (recieveEntity instanceof EntityPlayer) {
                if (giveEntity instanceof EntityLivingBase) {
                    giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                    ((EntityLivingBase) giveEntity).knockBack(null, 0, 2000, 2000);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntitySlime)
            event.setCanceled(true);
        for (String s : entityList)
            if (s.equals(EntityList.getEntityString(event.entity))) {
                entityToReflecting.add(event.entity);
                break;
            }
    }

    public static void customTick() {
        List<Entity> refList = new ArrayList<>();
        entityToReflecting.removeIf(e -> e.isDead);
        for (Entity entity : entityToReflecting) {
            if (entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < 1)
                refList.add(entity);
        }
        if (/*KeyManager.functionKey.getIsKeyPressed() && */((AcceleratorArmor) getPlayerHeadArmor()).isActivated()) {
            entityToReflecting.removeAll(refList);
            for (Entity entity : refList) {
                entity.motionX = -entity.motionX;
                entity.motionY = -entity.motionY;
                entity.motionZ = -entity.motionZ;
            }
        }
    }

    public boolean isActivated() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null)
            return false;
        ItemArmor itemArmor = (ItemArmor) getPlayerHeadArmor();
        if (!(itemArmor instanceof AcceleratorArmor) || ((AcceleratorArmor) itemArmor).battery_remain == 0)
            return false;
        return ((AcceleratorArmor) itemArmor).isActivated();
    }

    public boolean isWearingChoker() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null)
            return false;
        return getPlayerHeadArmor() instanceof AcceleratorArmor;
    }

    public static Item getPlayerHeadArmor() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        return entityPlayer.getCurrentArmor(3).getItem();
    }
}
