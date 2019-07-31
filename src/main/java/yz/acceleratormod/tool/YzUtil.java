package yz.acceleratormod.tool;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yz.acceleratormod.armor.ACCLArmor;

public class YzUtil {
    public static NBTTagCompound getNBTTag(ItemStack itemStack) {
        NBTTagCompound nbt = itemStack.stackTagCompound;
        if (nbt == null) {
            nbt = new NBTTagCompound();
            if (itemStack.getItem() instanceof ACCLArmor)
                nbt = setupChoker(nbt);
            itemStack.setTagCompound(nbt);
        }
        return nbt;
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public static boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    private static NBTTagCompound setupChoker(NBTTagCompound nbt) {
        if (!nbt.hasKey(ACCLArmor.activeTag))
            nbt.setBoolean(ACCLArmor.activeTag, false);
        if (!nbt.hasKey(ACCLArmor.battRemainTag))
            nbt.setInteger(ACCLArmor.battRemainTag, ACCLArmor.battery_capacity);
        if (!nbt.hasKey(ACCLArmor.toggleDelayTag))
            nbt.setInteger(ACCLArmor.toggleDelayTag, 10);
        return nbt;
    }
}
