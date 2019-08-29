package yz.acceleratormod.choker.abilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.choker.ChokerEvent;
import yz.acceleratormod.network.keymgr.KeyManager;

public class AbilityGravityJump {
    private boolean canJump = false;
    private int jumpKeyTime = 0;

    @SubscribeEvent
    public void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (event.entity instanceof EntityPlayer) {
            this.canJump = true;
            this.jumpKeyTime = 0;
        }
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
