package yz.acceleratormod.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.ChokerFunction;
import yz.acceleratormod.tool.YzUtil;

import java.util.List;

public class ACCLArmor extends ItemArmor {
    public static final int battery_capacity = 72000;

    public static final String activeTag = "active";
    public static final String battRemainTag = "batt_remain";
    public static final String toggleDelayTag = "toggle_delay";
    private final ChokerFunction chokerFunction = new ChokerFunction();

    @SideOnly(Side.CLIENT)
    private IIcon[] iicon = new IIcon[2];

    public ACCLArmor(ItemArmor.ArmorMaterial material, int armorType) {
        super(material, 0, armorType);
        this.setMaxDamage(0);
        this.setNoRepair();
    }

    @Override
    public String getArmorTexture(ItemStack itemStack, Entity entity, int slot, String type) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        if (nbt.getBoolean(activeTag) && nbt.getInteger(battRemainTag) > 0)
            return "acceleratormod:textures/armor/accelerator_armor_on.png";
        return "acceleratormod:textures/armor/accelerator_armor.png";
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
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        if (nbt.getBoolean(activeTag) && nbt.getInteger(battRemainTag) > 0)
            return this.iicon[1];
        return this.iicon[0];
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return YzUtil.getNBTTag(itemStack).getInteger(battRemainTag) < battery_capacity;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        return 1. - (double) nbt.getInteger(battRemainTag) / (double) battery_capacity;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        if (ACCL.keyManager.isPowerKeyDown(player) && nbt.getInteger(toggleDelayTag) == 0) {
            nbt.setBoolean(activeTag, !nbt.getBoolean(activeTag));
            nbt.setInteger(toggleDelayTag, 10);
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText("The choker was " + (nbt.getBoolean(activeTag) ? "enabled" : "disabled" + ".")));
                if (nbt.getBoolean(activeTag))
                    world.playSoundAtEntity(player, ACCL.MOD_ID + ":power_btn", 1.F, 1.F);
            }
        }

        if (player.isSprinting() && nbt.getBoolean(activeTag))
            player.capabilities.setPlayerWalkSpeed(0.18F);
        else
            player.capabilities.setPlayerWalkSpeed(0.1F);
        if (nbt.getInteger(toggleDelayTag) > 0)
            nbt.setInteger(toggleDelayTag, nbt.getInteger(toggleDelayTag) - 1);
        nbt.setInteger(battRemainTag, nbt.getInteger(battRemainTag) - (nbt.getBoolean(activeTag) ? 10 : 1));
        nbt.setInteger(battRemainTag, Math.max(nbt.getInteger(battRemainTag), 0));
        this.chokerFunction.customTick(world, player, itemStack);
        itemStack.setTagCompound(nbt);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        list.add("Battery Remain : " + nbt.getInteger(battRemainTag) + "/" + battery_capacity);
        if (nbt.getBoolean(activeTag))
            list.add("Active");
        else
            list.add("Not active");
    }
}