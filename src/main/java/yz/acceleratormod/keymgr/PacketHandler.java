package yz.acceleratormod.keymgr;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("AcceleratorMod");

    public static void init(int id) {
        INSTANCE.registerMessage(MessageKeyPressedHandler.class, MessageKeyPressed.class, id, Side.SERVER);
    }
}
