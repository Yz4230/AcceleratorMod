package yz.acceleratormod.tool;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import yz.acceleratormod.armor.ArmorChoker;
import yz.acceleratormod.item.ItemBattery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YzUtil {
    public static NBTTagCompound getNBTTag(ItemStack itemStack) {
        NBTTagCompound nbt = itemStack.stackTagCompound;
        if (nbt == null) {
            nbt = new NBTTagCompound();
            if (itemStack.getItem() instanceof ArmorChoker)
                setupChoker(nbt);
            if (itemStack.getItem() instanceof ItemBattery)
                setupBattery(nbt);
            itemStack.setTagCompound(nbt);
        }
        return nbt;
    }

    public static boolean isLgServer() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public static boolean isLgClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    private static void setupChoker(NBTTagCompound nbt) {
        if (!nbt.hasKey(ArmorChoker.activeTag))
            nbt.setBoolean(ArmorChoker.activeTag, false);
        if (!nbt.hasKey(ArmorChoker.battRemainTag))
            nbt.setInteger(ArmorChoker.battRemainTag, ArmorChoker.battery_capacity);
    }

    private static void setupBattery(NBTTagCompound nbt) {
        if (!nbt.hasKey(ItemBattery.battRemainTag))
            nbt.setInteger(ItemBattery.battRemainTag, 0);
    }

    public static Vec3 getVelocity(Entity entity) {
        return Vec3.createVectorHelper(entity.posX - entity.prevPosX, entity.posY - entity.prevPosY, entity.posZ - entity.prevPosZ);
    }

    public static Vec3 getMotionVelocity(Entity entity) {
        return Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ);
    }

    public static Vec3 scalarMultiple(Vec3 vec3, double scalar) {
        return Vec3.createVectorHelper(vec3.xCoord * scalar, vec3.yCoord * scalar, vec3.zCoord * scalar);
    }

    public static Vec3 addVec(Vec3 vec1, Vec3 vec2) {
        return Vec3.createVectorHelper(vec1.xCoord + vec2.xCoord, vec1.yCoord + vec2.yCoord, vec1.zCoord + vec2.zCoord);
    }

    public static Vec3 routeVec(Entity from, Entity to) {
        return Vec3.createVectorHelper(to.posX - from.posX, to.posY - from.posY, to.posZ - from.posZ);
    }

    public static Vec3 posVec(Entity entity) {
        return Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
    }

    public static Set<Entity> rayTraceEntity(World world, Vec3 from, Vec3 direction, double distance, double acc) {
        AxisAlignedBB axisAlignedBB = AxisAlignedBB.getBoundingBox(
                Math.min(from.xCoord + direction.xCoord * distance, from.xCoord),
                Math.min(from.yCoord + direction.yCoord * distance, from.yCoord),
                Math.min(from.zCoord + direction.zCoord * distance, from.zCoord),
                Math.max(from.xCoord + direction.xCoord * distance, from.xCoord),
                Math.max(from.yCoord + direction.yCoord * distance, from.yCoord),
                Math.max(from.zCoord + direction.zCoord * distance, from.zCoord)
        ).expand(1, 1, 1);
        List entityList = world.getEntitiesWithinAABB(Entity.class, axisAlignedBB);
        Set<Entity> ret = new HashSet<>();
        for (double d = 0; d <= distance; d += acc) {
            Vec3 checkingPos = addVec(from, scalarMultiple(direction, d));
            for (Object o : entityList)
                if (o instanceof Entity) {
                    Entity entity = (Entity) o;
                    if (entity.boundingBox.isVecInside(checkingPos)) ret.add(entity);
                }
        }
        return ret;
    }
}
