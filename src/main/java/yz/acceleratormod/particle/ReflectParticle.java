package yz.acceleratormod.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class ReflectParticle extends EntityFX {
    public ReflectParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        super(world, posX, posY, posZ);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
