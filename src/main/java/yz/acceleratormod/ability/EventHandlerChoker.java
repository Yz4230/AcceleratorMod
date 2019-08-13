package yz.acceleratormod.ability;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.network.PacketHandler;
import yz.acceleratormod.network.ability.ReflectionSyncer;
import yz.acceleratormod.network.keymgr.KeyManager;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;

import java.io.File;
import java.util.*;

public class EventHandlerChoker {
    private static Set<String> entityList = new HashSet<>();
    private static final String[] defaultEntityToReflect = {
            "Arrow",
            "Snowball",
            "WitherSkull",
            "ThrownPotion",
            "SmallFireball",
            "Fireball"};
    private Set<Entity> entityToReflect = new HashSet<>();
    private MovingObjectPosition position = null;
    private final List<String> invalidDamageSource = Arrays.asList(
            DamageSource.fall.damageType,
            DamageSource.inFire.damageType,
            DamageSource.onFire.damageType,
            DamageSource.lava.damageType,
            DamageSource.cactus.damageType,
            DamageSource.inWall.damageType,
            DamageSource.anvil.damageType);

    public static void loadConfig(File configFile) {
        Configuration cfg = new Configuration(configFile, ACCL.MOD_VERSION, true);
        cfg.load();
        String[] entityIdRaw = cfg.getStringList("entity_IDs", "Choker Settings",
                defaultEntityToReflect, "Entity IDs to reflect");
        entityList.addAll(Arrays.asList(defaultEntityToReflect));
        entityList.addAll(Arrays.asList(entityIdRaw));
        cfg.save();
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.world.isRemote)
            return;
        event.getAffectedEntities().removeIf(entity -> entity instanceof EntityPlayer && ChokerUtil.isActivated(entity));

        List<ChunkPosition> positionList = new ArrayList<>();
        for (ChunkPosition position : event.getAffectedBlocks())
            for (Object o : event.world.playerEntities) {
                EntityPlayer player = (EntityPlayer) o;
                if (!ChokerUtil.isActivated(player))
                    break;
                if (Math.floor(player.posX) == position.chunkPosX && Math.floor(player.posZ) == position.chunkPosZ)
                    positionList.add(position);
            }
        event.getAffectedBlocks().removeAll(positionList);
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        if (event.entity.worldObj.isRemote)
            return;
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (receiveEntity instanceof EntityPlayer && ChokerUtil.isActivated(receiveEntity)) {
            if (this.invalidDamageSource.contains(event.source.damageType))
                event.setCanceled(true);
        } else if (giveEntity instanceof EntityPlayer && event.ammount != receiveEntity.getHealth() && ChokerUtil.isActivated(giveEntity)) {
            receiveEntity.attackEntityFrom(DamageSource.causePlayerDamage(
                    (EntityPlayer) giveEntity).setDamageBypassesArmor().setDamageIsAbsolute(), receiveEntity.getHealth());
            receiveEntity.motionX = giveEntity.getLookVec().xCoord * 2;
            receiveEntity.motionY = giveEntity.getLookVec().yCoord * 2;
            receiveEntity.motionZ = giveEntity.getLookVec().zCoord * 2;
            SoundManager.Play(new SoundAtEntity(ACCL.strongPunchSnd, giveEntity, 1.F, 1.F));
        }
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (receiveEntity instanceof EntityPlayer && ChokerUtil.isActivated(receiveEntity)) {
            if (giveEntity instanceof EntityLivingBase) {
                event.setCanceled(true);
                if (!event.source.isProjectile()) {
                    giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                    giveEntity.motionX = -giveEntity.getLookVec().xCoord * 2;
                    giveEntity.motionY = -giveEntity.getLookVec().yCoord * 2;
                    giveEntity.motionZ = -giveEntity.getLookVec().zCoord * 2;
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.world.isRemote)
            return;
        if (event.entity instanceof EntitySlime) {
            event.setCanceled(true);
            return;
        }
        if (event.entity instanceof EntityFallingBlock || entityList.contains(EntityList.getEntityString(event.entity)))
            this.entityToReflect.add(event.entity);
    }

    @SubscribeEvent
    public void chokerAbility(ChokerEvent event) {
        if (!event.world.isRemote)
            if (event.player.isInWater())
                event.player.setAir(300);
    }

    @SubscribeEvent
    public void chokerRender(ChokerEvent event) {
        if (event.world.isRemote && event.active) {
            event.world.spawnParticle("portal", event.player.posX, event.player.posY, event.player.posZ,
                    Math.random() * 6. - 3., Math.random() * 6. - 3., Math.random() * 6. - 3.);
        }
    }

    @SubscribeEvent
    public void reflectEntity(ChokerEvent event) {
        if (event.world.isRemote)
            return;
        List<Entity> refList = new ArrayList<>();
        for (Entity entity : this.entityToReflect)
            if (event.player.boundingBox.expand(1.4F, 1.4F, 1.4F).intersectsWith(entity.boundingBox))
                refList.add(entity);
        this.entityToReflect.removeIf(e -> e.isDead);
        if (!event.active)
            return;
        for (Entity entity : refList) {
            Vec3 v = Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ).normalize();
            if (entity instanceof EntityFallingBlock) {
                EntityFallingBlock fallingBlock = (EntityFallingBlock) entity;
                this.entityToReflect.remove(fallingBlock);
                fallingBlock.entityDropItem(new ItemStack(fallingBlock.func_145805_f(), 1, 0), 0.F);
                fallingBlock.setDead();
                SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 0.6F, 1.F));
            } else if (v.dotProduct(event.player.getLookVec()) < 0) {
                ChokerUtil.flipVelocity(entity);
                PacketHandler.INST_refSync.sendToAll(new ReflectionSyncer(event.player, entity));
                SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 0.6F, 1.F));
            }
        }
    }

    @SubscribeEvent
    public void strongStep(ChokerEvent event) {
        if (!ACCL.keyManager.isStepKeyDown(event.player) || !event.active || !event.player.onGround) {
            return;
        }
        if (!event.world.isRemote) {
            List entityList = event.world.getEntitiesWithinAABB(EntityLivingBase.class, event.player.boundingBox.expand(10, 10, 10));
            for (Object o : entityList) {
                if (o instanceof EntityPlayer)
                    continue;
                Entity entity = (Entity) o;
                entity.motionY = 3;
            }
            SoundManager.Play(new SoundAtEntity(ACCL.strongStepSnd, event.player, 1.F, 1.F));
        } else {
            for (int i = 0; i < 600; i++) {
                event.world.spawnParticle("explode", event.player.posX, event.player.posY - event.player.getEyeHeight(), event.player.posZ,
                        Math.random() * 5 - 2.5, Math.random() * 5 - 2.5, Math.random() * 5 - 2.5);
            }
        }
    }

    @SubscribeEvent
    public void changeGravity(ChokerEvent event) {
        if (ACCL.keyManager.isChangeGravityKeyDown(event.player) && event.active) {
            if (this.position == null) {
                Vec3 lookVec = event.player.getLookVec();
                Vec3 startVec = event.player.getPosition(1.F);
                Vec3 goalVec = startVec.addVector(lookVec.xCoord * 512, lookVec.yCoord * 512, lookVec.zCoord * 512);
                MovingObjectPosition objectPosition = event.world.rayTraceBlocks(startVec, goalVec);
                if (objectPosition == null)
                    return;
                this.position = objectPosition;
            }
            Vec3 route = Vec3.createVectorHelper(this.position.blockX - event.player.posX,
                    this.position.blockY - event.player.posY, this.position.blockZ - event.player.posZ);
            if (route.lengthVector() > 1.6F) {
                route = route.normalize();
                event.player.motionX += route.xCoord * 0.1;
                event.player.motionY += route.yCoord * 0.1;
                event.player.motionZ += route.zCoord * 0.1;
            } else {
                event.player.motionX = 0;
                event.player.motionY = 0;
                event.player.motionZ = 0;
            }
        } else {
            this.position = null;
        }
    }
}

