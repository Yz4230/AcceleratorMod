package yz.acceleratormod.armor;

import cofh.api.energy.IEnergyContainerItem;
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
import net.minecraftforge.common.MinecraftForge;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.choker.ChokerEvent;
import yz.acceleratormod.choker.ChokerUtil;
import yz.acceleratormod.sound.SoundAtEntity;
import yz.acceleratormod.sound.SoundManager;
import yz.acceleratormod.tool.YzUtil;

import java.util.List;

public class ArmorChoker extends ItemArmor implements IEnergyContainerItem {

    public static final int battery_capacity = 200_000;

    public static final String activeTag = "active";
    public static final String battRemainTag = "batt_remain";

    @SideOnly(Side.CLIENT)
    private IIcon[] iicon = new IIcon[2];

    public ArmorChoker(ItemArmor.ArmorMaterial material, int armorType) {
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
    public void registerIcons(IIconRegister register) {
        this.iicon[0] = register.registerIcon(ACCL.MOD_ID + ":choker/choker");
        this.iicon[1] = register.registerIcon(ACCL.MOD_ID + ":choker/choker_on");
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
    public IIcon getIcon(ItemStack stack, int pass) {
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        if (nbt.getBoolean(activeTag) && nbt.getInteger(battRemainTag) > 0)
            return this.iicon[1];
        return this.iicon[0];
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        return 1. - (double) nbt.getInteger(battRemainTag) / (double) battery_capacity;
    }

    @Override
    public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        NBTTagCompound nbt = YzUtil.getNBTTag(itemStack);
        if (ACCL.keyManager.isPowerKeyDown(player) && nbt.getInteger(battRemainTag) > 0) {
            nbt.setBoolean(activeTag, !nbt.getBoolean(activeTag));
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText("The choker was " + (nbt.getBoolean(activeTag) ? "enabled" : "disabled" + ".")));
                if (nbt.getBoolean(activeTag))
                    SoundManager.Play(new SoundAtEntity(ACCL.powerBtnSnd, player, 1.F, 1.F));
            }
        }
        if (player.isSprinting() && ChokerUtil.isActivated(player) && !world.isRemote)
            player.capabilities.setPlayerWalkSpeed(0.18F);
        else
            player.capabilities.setPlayerWalkSpeed(0.1F);
        nbt.setInteger(battRemainTag, nbt.getInteger(battRemainTag) - (nbt.getBoolean(activeTag) ? 100 : 1));
        nbt.setInteger(battRemainTag, Math.max(nbt.getInteger(battRemainTag), 0));
        MinecraftForge.EVENT_BUS.post(new ChokerEvent(world, player, itemStack));
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

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        NBTTagCompound nbt = YzUtil.getNBTTag(container);
        nbt.setInteger(battRemainTag, Math.min(battery_capacity, nbt.getInteger(battRemainTag) + Math.min(2000, maxReceive)));
        return Math.min(2000, maxReceive);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        NBTTagCompound nbt = YzUtil.getNBTTag(container);
        return nbt.getInteger(battRemainTag);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return battery_capacity;
    }
}