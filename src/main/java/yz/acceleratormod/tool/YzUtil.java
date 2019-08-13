package yz.acceleratormod.tool;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yz.acceleratormod.armor.ArmorChoker;
import yz.acceleratormod.item.ItemBattery;

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
            nbt.setInteger(ItemBattery.battRemainTag, ItemBattery.battery_capacity);
    }
}
