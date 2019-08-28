package yz.acceleratormod.choker.abilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import yz.acceleratormod.choker.ChokerEvent;

public abstract class AbilityBase {
    @SubscribeEvent
    public abstract void onUpdate(ChokerEvent event);
}
