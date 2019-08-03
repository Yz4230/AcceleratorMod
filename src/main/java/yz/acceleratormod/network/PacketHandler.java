package yz.acceleratormod.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.network.keymgr.KeyStateSyncer;
import yz.acceleratormod.network.reflection.ReflectionSyncer;

public class PacketHandler {
    public static final SimpleNetworkWrapper INST_keyState = NetworkRegistry.INSTANCE.newSimpleChannel(ACCL.MOD_ID + ":keyState");
    public static final SimpleNetworkWrapper INST_refSync = NetworkRegistry.INSTANCE.newSimpleChannel(ACCL.MOD_ID + ":refSync");
    private static int id = 0;

    public static void init() {
        INST_keyState.registerMessage(KeyStateSyncer.class, KeyStateSyncer.class, id++, Side.SERVER);
        INST_refSync.registerMessage(ReflectionSyncer.class, ReflectionSyncer.class, id++, Side.SERVER);
    }
}
