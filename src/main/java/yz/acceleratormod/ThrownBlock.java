package yz.acceleratormod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ThrownBlock extends EntityThrowable {
    public ThrownBlock(World world) {
        super(world);
    }

    public ThrownBlock(World world, EntityLivingBase entity) {
        super(world, entity);
    }

    public ThrownBlock(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {

    }
}
