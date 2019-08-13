package yz.acceleratormod.ability;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yz.acceleratormod.armor.ArmorChoker;
import yz.acceleratormod.tool.YzUtil;

public class ChokerEvent extends Event {
    public final World world;
    public final EntityPlayer player;
    public final ItemStack armorStack;
    public final boolean active;

    public ChokerEvent(World world, EntityPlayer player, ItemStack stack) {
        this.world = world;
        this.player = player;
        this.armorStack = stack;
        NBTTagCompound nbt = YzUtil.getNBTTag(stack);
        this.active = nbt.getBoolean(ArmorChoker.activeTag) && nbt.getInteger(ArmorChoker.battRemainTag) > 0;
    }
}
