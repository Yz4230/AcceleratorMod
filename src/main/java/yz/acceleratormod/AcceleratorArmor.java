package yz.acceleratormod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

public class AcceleratorArmor extends ItemArmor {
    public int battery_remain = 10000;
    public int battery_capacity = 10000;

    public AcceleratorArmor(ItemArmor.ArmorMaterial material, int armorType) {
        super(material, 0, armorType);
        setMaxDamage(battery_capacity);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "acceleratormod:textures/armor/accelerator_armor.png";
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double)battery_remain / (double)battery_capacity;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (world.isRemote)
            Debugtool.Log(String.valueOf(world.getLoadedEntityList().size()));
    }
}