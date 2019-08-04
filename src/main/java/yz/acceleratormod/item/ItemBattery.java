package yz.acceleratormod.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.armor.ArmorChoker;
import yz.acceleratormod.tool.YzUtil;

public class ItemBattery extends Item {
    public static final int battery_capacity = 72000;

    public static final String battRemainTag = "batt_remain";

    @SideOnly(Side.CLIENT)
    private IIcon[] iicon = new IIcon[6];

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.getCurrentArmor(3) == null || !(player.getCurrentArmor(3).getItem() instanceof ArmorChoker))
            return stack;
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        ItemStack chokerItemStack = player.getCurrentArmor(3);
        NBTTagCompound chokerNbt = YzUtil.getNBTTag(chokerItemStack);

        int chokerBatRemain = chokerNbt.getInteger(ArmorChoker.battRemainTag);
        int batRemain = nbt.getInteger(battRemainTag);

        int toCharge = ArmorChoker.battery_capacity - chokerBatRemain;
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        for (int i = 0; i < 6; i++)
            this.iicon[i] = register.registerIcon(ACCL.MOD_ID + ":battery/battery_" + i);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        return this.iicon[5 - (int) (((double) nbt.getInteger(battRemainTag) / (double) battery_capacity) * 5)];
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        return this.iicon[5 - (int) (((double) nbt.getInteger(battRemainTag) / (double) battery_capacity) * 5)];
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        return nbt.getInteger(battRemainTag) != battery_capacity;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        return (double) nbt.getInteger(battRemainTag) / (double) battery_capacity;
    }
}
