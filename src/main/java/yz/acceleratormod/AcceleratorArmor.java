package yz.acceleratormod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class AcceleratorArmor extends ItemArmor {
    public int battery_remain = 72000;
    public int battery_capacity = battery_remain;
    public boolean activated = false;

    public AcceleratorArmor(ItemArmor.ArmorMaterial material, int armorType) {
        super(material, 0, armorType);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        if (ChokerFunctions.activated)
            return "acceleratormod:textures/armor/accelerator_armor_on.png";
        return "acceleratormod:textures/armor/accelerator_armor.png";
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1. - (double)battery_remain/(double)battery_capacity;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (ChokerFunctions.activated)
            battery_remain -= 40;
        else
            battery_remain--;
        battery_remain = Math.max(0, battery_remain);
        ChokerFunctions.customTick();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        list.add("Battery Remain : " + battery_remain + "/" + battery_capacity);
    }
}