package yz.acceleratormod.choker.abilities;

import net.minecraftforge.common.MinecraftForge;

public class AbilityRegistry {
    public static void registerAbilities() {
        MinecraftForge.EVENT_BUS.register(new AbilityStrongStep());
        MinecraftForge.EVENT_BUS.register(new AbilityGravityJump());
    }
}
