package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import yz.acceleratormod.network.keymgr.KeyManagerClient;

public class TickHandler {
    private static int keyDelay = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ((KeyManagerClient) ACCL.keyManager).sendKeyUpdate();
    }
}
