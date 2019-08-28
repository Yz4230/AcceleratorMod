package yz.acceleratormod.choker.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.choker.ChokerEvent;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;

import java.util.List;

public class StrongStep extends AbilityBase {
    @Override
    public void onUpdate(ChokerEvent event) {
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
}
