package yz.acceleratormod;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import org.lwjgl.input.Keyboard;

@Mod(modid = "AcceleratorMod", name = "Accelerator Mod", version = "19.07.14")
public class AcceleratorMod {
    public static Item choker;
    public static Item battery;

    public static final KeyBinding chokerButton = new KeyBinding("key.choker.button", Keyboard.KEY_R, "key.acceleratormod.chokerbutton");
    public static final KeyBinding function = new KeyBinding("key.choker.function", Keyboard.KEY_F, "key.acceleratormod.function");

    public static final int HELMET = 0;
    public static final ItemArmor.ArmorMaterial ACC_ARM = EnumHelper.addArmorMaterial("ACC_ARM", 100, new int[]{2, 0, 0, 0}, 0);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        choker = new AcceleratorArmor(ACC_ARM, HELMET)
                .setMaxStackSize(1)
                .setCreativeTab(CreativeTabs.tabCombat)
                .setUnlocalizedName("choker")
                .setTextureName("acceleratormod:choker");
        GameRegistry.registerItem(choker, "choker");
        battery = new Item()
                .setMaxStackSize(4)
                .setCreativeTab(CreativeTabs.tabRedstone)
                .setUnlocalizedName("battery")
                .setTextureName("acceleratormod:battery");
        GameRegistry.registerItem(battery, "battery");

        MinecraftForge.EVENT_BUS.register(new ChokerFunctions());
        FMLCommonHandler.instance().bus().register(new ChokerFunctions());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.addRecipe(new ItemStack(AcceleratorMod.choker),
                "ABC", "B D", "ABE",
                'A', Items.dye, 'B', Items.string, 'C', Items.iron_ingot,
                'D', Items.redstone, 'E', Blocks.stone_button);

        GameRegistry.addRecipe(new ItemStack(AcceleratorMod.battery, 4),
                " A ", "ABA", " A ",
                'A', Items.iron_ingot, 'B', Items.redstone);
    }
}

