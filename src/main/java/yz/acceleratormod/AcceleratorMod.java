package yz.acceleratormod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import yz.acceleratormod.keymgr.KeyManager;

import java.util.Arrays;

@Mod(modid = AcceleratorMod.MOD_ID, name = AcceleratorMod.MOD_NAME, version = AcceleratorMod.MOD_VERSION)
public class AcceleratorMod {
    public static final String MOD_ID = "AcceleratorMod";
    public static final String MOD_NAME = "Accelerator Mod";
    public static final String MOD_VERSION = "19.07.14";

    public static Item choker;
    public static Item battery;

    public static final int HELMET = 0;
    public static final ItemArmor.ArmorMaterial CHOKER = EnumHelper.addArmorMaterial("ACC_ARM", 100, new int[]{1, 0, 0, 0}, 0);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        choker = new AcceleratorArmor(CHOKER, HELMET)
                .setMaxStackSize(1)
                .setCreativeTab(CreativeTabs.tabCombat)
                .setUnlocalizedName("choker_off")
                .setTextureName("acceleratormod:choker");
        GameRegistry.registerItem(choker, "choker");
        battery = new Item()
                .setMaxStackSize(4)
                .setCreativeTab(CreativeTabs.tabRedstone)
                .setUnlocalizedName("battery")
                .setTextureName("acceleratormod:battery");
        GameRegistry.registerItem(battery, "battery");
        

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile(), AcceleratorMod.MOD_VERSION, true);
        try{
            cfg.load();
            String[] entityIdRaw = cfg.getStringList("entity_IDs", "Choker Settings",
                    ChokerFunctions.defalutEntityToReflect, "Entity IDs to reflect");
            ChokerFunctions.entityList.addAll(Arrays.asList(ChokerFunctions.defalutEntityToReflect));
            ChokerFunctions.entityList.addAll(Arrays.asList(entityIdRaw));
        } finally {
            cfg.save();
        }

        KeyManager.init();
        MinecraftForge.EVENT_BUS.register(new ChokerFunctions());
        FMLCommonHandler.instance().bus().register(new ChokerFunctions());
        FMLCommonHandler.instance().bus().register(new KeyManager());
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

