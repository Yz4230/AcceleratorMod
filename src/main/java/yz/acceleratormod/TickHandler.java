package yz.acceleratormod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import yz.acceleratormod.keymgr.KeyManagerClient;

public class TickHandler {
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        ((KeyManagerClient) ACCL.keyManager).sendKeyUpdate();
    }
}
