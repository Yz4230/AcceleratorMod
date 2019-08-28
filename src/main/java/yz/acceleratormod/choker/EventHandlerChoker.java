package yz.acceleratormod.choker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Timer;
import net.minecraft.util.*;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.network.keymgr.KeyManager;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;
import yz.acceleratormod.tool.YzUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EventHandlerChoker {
    private static Set<String> reflectTargets = new HashSet<>();
    private static final String[] defaultEntityToReflect = {
            "Arrow",
            "Fireball",
            "SmallFireball",
            "Snowball",
            "ThrownPotion",
            "WitherSkull"};
    private final List<String> reflectDamageSource = Arrays.asList(
            DamageSource.fall.damageType,
            DamageSource.inFire.damageType,
            DamageSource.onFire.damageType,
            DamageSource.lava.damageType,
            DamageSource.cactus.damageType,
            DamageSource.inWall.damageType,
            DamageSource.anvil.damageType);
    private boolean canJump = false;
    private int jumpKeyTime = 0;
    private List<Entity> watchList = new ArrayList<>();

    public static void loadConfig(File configFile) {
        Configuration cfg = new Configuration(configFile, ACCL.MOD_VERSION, true);
        cfg.load();
        String[] entityIdRaw = cfg.getStringList("entity_IDs", "Choker Settings",
                defaultEntityToReflect, "Entity IDs to reflect");
        reflectTargets.addAll(Arrays.asList(defaultEntityToReflect));
        reflectTargets.addAll(Arrays.asList(entityIdRaw));
        cfg.save();
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!event.world.isRemote) {
            event.getAffectedEntities().removeIf(entity -> entity instanceof EntityPlayer && ChokerUtil.isActivated(entity));

            List<ChunkPosition> positionList = new ArrayList<>();
            for (ChunkPosition position : event.getAffectedBlocks())
                for (Object o : event.world.playerEntities) {
                    EntityPlayer player = (EntityPlayer) o;
                    if (!ChokerUtil.isActivated(player))
                        break;
                    if (Math.floor(player.posX) == position.chunkPosX && Math.floor(player.posY) >= position.chunkPosY &&
                            Math.floor(player.posZ) == position.chunkPosZ)
                        positionList.add(position);
                }
            event.getAffectedBlocks().removeAll(positionList);
        }
    }

    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        if (!event.entity.worldObj.isRemote) {
            EntityLivingBase receiveEntity = event.entityLiving;
            Entity giveEntity = event.source.getEntity();
            if (receiveEntity instanceof EntityPlayer && ChokerUtil.isActivated(receiveEntity)) {
                if (this.reflectDamageSource.contains(event.source.damageType))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!event.entity.worldObj.isRemote && ChokerUtil.isActivated(event.entityPlayer)) {
            event.target.attackEntityFrom(DamageSource.causePlayerDamage(
                    event.entityPlayer).setDamageIsAbsolute().setDamageBypassesArmor(),
                    ((EntityLivingBase) event.target).getHealth());
            SoundManager.Play(new SoundAtEntity(ACCL.strongPunchSnd, event.entityPlayer, 1.F, 1.F));
        }
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        EntityLivingBase receiveEntity = event.entityLiving;
        Entity giveEntity = event.source.getEntity();
        if (receiveEntity instanceof EntityPlayer && ChokerUtil.isActivated(receiveEntity)) {
            if (giveEntity instanceof EntityLivingBase) {
                if (!event.source.isProjectile()) {
                    giveEntity.attackEntityFrom(DamageSource.generic, event.ammount * 2);
                    giveEntity.motionX = -giveEntity.getLookVec().xCoord * 2;
                    giveEntity.motionY = -giveEntity.getLookVec().yCoord * 2;
                    giveEntity.motionZ = -giveEntity.getLookVec().zCoord * 2;
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (event.entity instanceof EntityPlayer) {
            this.canJump = true;
            this.jumpKeyTime = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && !event.world.isRemote &&
                ACCL.keyManager.isFunctionKeyDown(event.entityPlayer) && ChokerUtil.isActivated(event.entityPlayer)) {
            event.world.createExplosion(event.entityPlayer, event.x, event.y, event.z, 5, true);
        }
    }

    //@SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        List entityList = event.world.loadedEntityList;
        Entity entity;
        if (event.phase == TickEvent.Phase.START) {
            for (Object o : entityList) {
                entity = (Entity) o;
                if (entity instanceof EntityZombie)
                    this.watchList.add(entity);
            }
            entityList.removeAll(this.watchList);
        } else {
            entityList.addAll(this.watchList);
        }
    }

    //@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && !(event.entity instanceof EntityPlayer) && Minecraft.getMinecraft().theWorld != null) {
            List players = Minecraft.getMinecraft().theWorld.playerEntities;
            AxisAlignedBB entityBB = event
                    .entity.boundingBox.
                            addCoord(event.entity.motionX * 1.1, event.entity.motionY * 1.1, event.entity.motionZ * 1.1)
                    .expand(1, 1, 1);
            for (Object o : players) {
                EntityPlayer player = (EntityPlayer) o;
                if (ChokerUtil.isActivated(player) &&
                        player.boundingBox.intersectsWith(entityBB) &&
                        reflectTargets.contains(EntityList.getEntityString(event.entity))) {
                    event.entity.motionX = 0;
                    event.entity.motionY = -1;
                    event.entity.motionZ = 0;
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void chokerAbility(ChokerEvent event) {
        if (!event.world.isRemote)
            if (event.player.isInWater())
                event.player.setAir(300);
        if (event.player.isBurning())
            event.player.extinguish();
    }

    @SubscribeEvent
    public void chokerRender(ChokerEvent event) {
        if (event.world.isRemote && event.active) {
            event.world.spawnParticle("portal", event.player.posX, event.player.posY + event.player.eyeHeight, event.player.posZ,
                    Math.random() * 6. - 3., Math.random() * 6. - 3., Math.random() * 6. - 3.);
        }
    }

    //@SubscribeEvent
    public void reflectEntity(ChokerEvent event) {
        if (event.active) {
            for (Object o : event.world.getEntitiesWithinAABBExcludingEntity(event.player, event.player.boundingBox.expand(32, 32, 32))) {
                Entity entity = (Entity) o;
                Vec3 velocity = YzUtil.getVelocity(entity).normalize();
                Vec3 route = YzUtil.routeVec(event.player, entity).normalize();
                AxisAlignedBB entityBB = entity.boundingBox.addCoord(entity.motionX, entity.motionY, entity.motionZ).expand(1, 1, 1);
                if (entityBB.intersectsWith(event.player.boundingBox)) {
                    if (reflectTargets.contains(EntityList.getEntityString(entity)) && velocity.lengthVector() > 0) {
                        if (route.dotProduct(velocity) < 0) {
                            ChokerUtil.flipVelocity(entity);
                            SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 0.6F, 1.F));
                        }
                    } else if (entity instanceof EntityFallingBlock) {
                        EntityFallingBlock fallingBlock = (EntityFallingBlock) entity;
                        fallingBlock.entityDropItem(new ItemStack(fallingBlock.func_145805_f(), 1, 0), 0.F);
                        fallingBlock.setDead();
                        SoundManager.Play(new SoundAtEntity(ACCL.reflectionSnd, entity, 0.6F, 1.F));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void strongStep(ChokerEvent event) {
        if (ACCL.keyManager.isStepKeyDown(event.player) && event.active && event.player.onGround) {
            if (event.world.isRemote) {
                for (int i = 0; i < 600; i++) {
                    event.world.spawnParticle("explode", event.player.posX, event.player.posY - event.player.getEyeHeight(), event.player.posZ,
                            Math.random() * 5 - 2.5, Math.random() * 5 - 2.5, Math.random() * 5 - 2.5);
                }
            } else {
                List entityList = event.world.getEntitiesWithinAABB(EntityLivingBase.class, event.player.boundingBox.expand(10, 10, 10));
                for (Object o : entityList) {
                    Entity entity = (Entity) o;
                    if (entity.onGround) {
                        entity.motionY = 3;
                        entity.attackEntityFrom(DamageSource.inWall, 10);
                    }
                }
                SoundManager.Play(new SoundAtEntity(ACCL.strongStepSnd, event.player, 1.F, 1.F));
            }
        }
    }

    @SubscribeEvent
    public void teleport(ChokerEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (ACCL.keyManager.isTeleportKeyDown(event.player) && event.active) {
        }
//        Field clientTimer = null;
//        try {
//            for (Field f : mc.getClass().getDeclaredFields())
//                if (f.getType() == Timer.class) {
//                    clientTimer = f;
//                    clientTimer.setAccessible(true);
//                }
//            Objects.requireNonNull(clientTimer).set(mc, new Timer(10.F));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @SubscribeEvent
    public void gravityJump(ChokerEvent event) {
        if (event.world.isRemote && event.active) {
            if (ACCL.keyManager.andKeyDown(event.player, KeyManager.Key.jump, KeyManager.Key.function)) {
                if (this.canJump && this.jumpKeyTime < 20) {
                    this.jumpKeyTime++;
                    event.player.motionY = 1.2F;
                }
            } else {
                this.canJump = false;
            }
            if (ACCL.keyManager.isFunctionKeyDown(event.player) && !event.player.onGround && event.player.moveForward > 0) {
                float f = event.player.rotationYaw * 0.017453292F;
                event.player.motionX -= MathHelper.sin(f) * 0.2F;
                event.player.motionZ += MathHelper.cos(f) * 0.2F;
            }
        }
    }
}

