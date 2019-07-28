package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.keymgr.KeyManager;

import java.util.Iterator;

public class ChokerFunctions {
    public static boolean activated = false;

    @SubscribeEvent
    public void inputKey(InputEvent.KeyInputEvent event) {
        if (KeyManager.chokerButton.isPressed() && isWearingChoker()) {
            activated = !activated;
            Minecraft.getMinecraft().thePlayer.sendChatMessage("Choker was " + (activated ? "Enabled" : "Disabled"));
        }
        if (KeyManager.function.isPressed()) {

        }
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (isActivated()) {
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

        if (isActivated()) {
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
        if (isActivated()) {
            if (recieveEntity instanceof EntityPlayer) {
                if (giveEntity instanceof EntityThrowable) {
                    EntityThrowable e = ((EntityThrowable) giveEntity);
                    e.setVelocity(-e.motionX, -e.motionY, -e.motionZ);
                    Debugtool.Log(String.valueOf(giveEntity.hashCode()));
                    event.setCanceled(true);
                } else if (giveEntity instanceof EntityLivingBase) {
                    giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                    ((EntityLivingBase) giveEntity).knockBack(null, 0, 2000, 2000);
                    event.setCanceled(true);
                }
            }
        }
    }

    public boolean isActivated() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null || !activated)
            return false;
        return entityPlayer.getCurrentArmor(3).getItem() instanceof AcceleratorArmor;
    }

    public boolean isWearingChoker() {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer.getCurrentArmor(3) == null)
            return false;
        return entityPlayer.getCurrentArmor(3).getItem() instanceof AcceleratorArmor;
    }
}
