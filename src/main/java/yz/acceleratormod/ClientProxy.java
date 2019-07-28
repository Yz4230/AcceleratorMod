package yz.acceleratormod;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ClientProxy {
    public static final KeyBinding chokerButton = new KeyBinding("key.choker.button", Keyboard.KEY_R, "key.acceleratormod.chokerbutton");

    public void registerClientInfo() {
        ClientRegistry.registerKeyBinding(chokerButton);
    }
}
