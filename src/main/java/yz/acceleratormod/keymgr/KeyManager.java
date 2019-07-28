package yz.acceleratormod.keymgr;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import yz.acceleratormod.Debugtool;

public class KeyManager {
    public static final KeyBinding chokerButton = new KeyBinding("key.choker.button", Keyboard.KEY_R, "key.acceleratormod.chokerbutton");
    public static final KeyBinding function = new KeyBinding("key.choker.function", Keyboard.KEY_F, "key.acceleratormod.function");

    public static void init() {
        PacketHandler.init(0);
        ClientRegistry.registerKeyBinding(chokerButton);
        ClientRegistry.registerKeyBinding(function);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (chokerButton.isPressed())
            PacketHandler.INSTANCE.sendToServer(new MessageKeyPressed(chokerButton.getKeyCode()));
        if (function.isPressed())
            PacketHandler.INSTANCE.sendToServer(new MessageKeyPressed(function.getKeyCode()));
    }
}
