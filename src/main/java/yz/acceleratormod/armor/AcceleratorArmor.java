package yz.acceleratormod.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.ChokerFunctions;
import yz.acceleratormod.tool.YzUtil;

import java.util.List;

public class AcceleratorArmor extends ItemArmor {
    public int battery_remain = 72000;
    public int battery_capacity = this.battery_remain;
    @SideOnly(Side.CLIENT)
    private IIcon[] iicon = new IIcon[2];

    public AcceleratorArmor(ItemArmor.ArmorMaterial material, int armorType) {
        super(material, 0, armorType);
        this.setMaxDamage(0);
        this.setNoRepair();
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (this.isActivated())
            return "acceleratormod:textures/armor/accelerator_armor_on.png";
        return "acceleratormod:textures/armor/accelerator_armor.png";
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1. - (double) this.battery_remain / (double) this.battery_capacity;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        System.out.println(ACCL.keyManager.playerKeys);
        if (this.isActivated())
            this.battery_remain -= 40;
        else
            this.battery_remain--;
        this.battery_remain = Math.max(0, this.battery_remain);
        ChokerFunctions.customTick();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iIconRegister) {
        this.iicon[0] = iIconRegister.registerIcon("acceleratormod:choker");
        this.iicon[1] = iIconRegister.registerIcon("acceleratormod:choker_on");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack itemStack) {
        if (this.isActivated())
            return this.iicon[1];
        return this.iicon[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add("Battery Remain : " + this.battery_remain + "/" + this.battery_capacity);
    }

    public void switchMode() {
        NBTTagCompound nbt = YzUtil.getNBTTag(new ItemStack(this));
        boolean isActiveted = nbt.getBoolean("activated");
        if (isActiveted)
            nbt.setBoolean("activated", false);
        else
            nbt.setBoolean("activated", true);
        new ItemStack(this).setTagCompound(nbt);
    }

    public boolean isActivated() {
        NBTTagCompound nbt = YzUtil.getNBTTag(new ItemStack(this));
        return nbt.getBoolean("activated");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World p_77659_2_, EntityPlayer p_77659_3_) {
        NBTTagCompound nbt;
        return super.onItemRightClick(itemStack, p_77659_2_, p_77659_3_);
    }
}