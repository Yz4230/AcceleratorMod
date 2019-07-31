package yz.acceleratormod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
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
import yz.acceleratormod.armor.ACCLArmor;
import yz.acceleratormod.keymgr.KeyManager;
import yz.acceleratormod.keymgr.PacketHandler;

import java.util.Arrays;

@Mod(modid = ACCL.MOD_ID, name = ACCL.MOD_NAME, version = ACCL.MOD_VERSION)
public class ACCL {
    public static final String MOD_ID = "acceleratormod";
    public static final String MOD_NAME = "Accelerator Mod";
    public static final String MOD_VERSION = "19.07.14";

    public static Item choker;
    public static Item battery;
    @SidedProxy(clientSide = "yz.acceleratormod.keymgr.KeyManagerClient", serverSide = "yz.acceleratormod.keymgr.KeyManager")
    public static KeyManager keyManager;

    public static final int HELMET = 0;
    public static final ItemArmor.ArmorMaterial CHOKER = EnumHelper.addArmorMaterial("ACC_ARM", 100, new int[]{1, 0, 0, 0}, 0);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        choker = new ACCLArmor(CHOKER, HELMET)
                .setMaxStackSize(1)
                .setCreativeTab(CreativeTabs.tabCombat)
                .setUnlocalizedName("choker");
        GameRegistry.registerItem(choker, "choker");
        battery = new Item()
                .setMaxStackSize(4)
                .setCreativeTab(CreativeTabs.tabRedstone)
                .setUnlocalizedName("battery")
                .setTextureName("acceleratormod:battery");
        GameRegistry.registerItem(battery, "battery");

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile(), ACCL.MOD_VERSION, true);
        try {
            cfg.load();
            String[] entityIdRaw = cfg.getStringList("entity_IDs", "Choker Settings",
                    ChokerFunction.defalutEntityToReflect, "Entity IDs to reflect");
            ChokerFunction.entityList.addAll(Arrays.asList(ChokerFunction.defalutEntityToReflect));
            ChokerFunction.entityList.addAll(Arrays.asList(entityIdRaw));
        } finally {
            cfg.save();
        }

        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(new ChokerFunction());
        FMLCommonHandler.instance().bus().register(new ChokerFunction());
        FMLCommonHandler.instance().bus().register(new TickHandler());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.addRecipe(new ItemStack(ACCL.choker),
                "ABC", "B D", "ABE",
                'A', Items.dye, 'B', Items.string, 'C', Items.iron_ingot,
                'D', Items.redstone, 'E', Blocks.stone_button);

        GameRegistry.addRecipe(new ItemStack(ACCL.battery, 4),
                " A ", "ABA", " A ",
                'A', Items.iron_ingot, 'B', Items.redstone);
    }
}

