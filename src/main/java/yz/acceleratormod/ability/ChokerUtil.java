package yz.acceleratormod.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yz.acceleratormod.armor.ArmorChoker;
import yz.acceleratormod.tool.YzUtil;

public class ChokerUtil {
    /**
     * @return Whether Player's choker is enabled
     */
    public static boolean isActivated(Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return false;
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getCurrentArmor(3) == null)
            return false;
        NBTTagCompound nbt = YzUtil.getNBTTag(player.getCurrentArmor(3));
        if (nbt.getInteger(ArmorChoker.battRemainTag) == 0)
            return false;
        return nbt.getBoolean(ArmorChoker.activeTag);
    }

    /**
     * Change entity::motionXYZ's signs
     *
     * @param world  Executing world
     * @param entity Target entity
     */
    public static void flipVelocity(Entity entity) {
        entity.motionX = -entity.motionX;
        entity.motionY = -entity.motionY;
        entity.motionZ = -entity.motionZ;
    }
}
