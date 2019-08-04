package yz.acceleratormod.armor;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;
import yz.acceleratormod.tool.YzUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandlerChoker {
    public static Set<String> entityList = new HashSet<>();
    public static Set<Entity> entityToReflect = new HashSet<>();
    public static final String[] defaultEntityToReflect =
            {"Arrow", "Snowball", "WitherSkull", "ThrownPotion", "SmallFireball", "PrimedTnt", "Fireball"};

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> entity instanceof EntityPlayer && ACCL.chokerFunc.isActivated(((EntityPlayer) entity)));

        List<ChunkPosition> positionList = new ArrayList<>();
        for (ChunkPosition position : event.getAffectedBlocks())
            for (Object o : event.world.playerEntities) {
                EntityPlayer player = ((EntityPlayer) o);
                if (player.posX == position.chunkPosX && player.posZ == position.chunkPosZ && ACCL.chokerFunc.isActivated(player))
                    positionList.add(position);
            }
        event.getAffectedBlocks().removeAll(positionList);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttackEvent(LivingAttackEvent event) {
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();

        if (receiveEntity instanceof EntityPlayer && ACCL.chokerFunc.isActivated(((EntityPlayer) receiveEntity))) {
            if (event.source.equals(DamageSource.fall))
                event.setCanceled(true);
        } else if (giveEntity instanceof EntityPlayer && event.ammount != receiveEntity.getHealth() && ACCL.chokerFunc.isActivated(((EntityPlayer) giveEntity))) {
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
        if (receiveEntity instanceof EntityPlayer && ACCL.chokerFunc.isActivated(((EntityPlayer) receiveEntity))) {
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
}
