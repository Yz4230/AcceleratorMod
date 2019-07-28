package yz.acceleratormod;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("AcceleratorMod");
    public static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(MessageKeyPressedHandler.class, MessageKeyPressed.class, id, Side.SERVER);
    }
}
