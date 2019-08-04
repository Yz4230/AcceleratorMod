package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import yz.acceleratormod.network.keymgr.KeyManagerClient;

public class TickHandlerCommon {
    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.side.isClient()) {
            ((KeyManagerClient) ACCL.keyManager).sendKeyUpdate();
        }
    }
}
