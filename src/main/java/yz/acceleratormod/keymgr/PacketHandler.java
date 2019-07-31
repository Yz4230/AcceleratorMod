package yz.acceleratormod.keymgr;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import yz.acceleratormod.ACCL;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ACCL.MOD_ID);
    private static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(KeyStateSyncer.class, KeyStateSyncer.class, id++, Side.SERVER);
    }
}
