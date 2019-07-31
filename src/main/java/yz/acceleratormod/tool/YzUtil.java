package yz.acceleratormod.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class YzUtil {
    public static NBTTagCompound getNBTTag(ItemStack itemStack) {
        NBTTagCompound nbt = itemStack.stackTagCompound;
        if (nbt == null) {
            nbt = new NBTTagCompound();
            itemStack.setTagCompound(nbt);
        }
        return nbt;
    }
}
