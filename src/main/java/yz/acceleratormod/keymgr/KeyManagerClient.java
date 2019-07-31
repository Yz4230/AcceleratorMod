package yz.acceleratormod.keymgr;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class KeyManagerClient extends KeyManager {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding powerKey = new KeyBinding("key.choker.power", Keyboard.KEY_F, "key.acceleratormod");
    private final KeyBinding reflectKey = new KeyBinding("key.choker.reflect", Keyboard.KEY_R, "key.acceleratormod");
    private final KeyBinding functionKey = new KeyBinding("key.choker.function", Keyboard.KEY_LMENU, "key.acceleratormod");
    private int lastKeyState = 0;

    public KeyManagerClient() {
        ClientRegistry.registerKeyBinding(this.powerKey);
        ClientRegistry.registerKeyBinding(this.reflectKey);
        ClientRegistry.registerKeyBinding(this.functionKey);
    }

    public void sendKeyUpdate() {
        Set<KeyManager.Key> keys = EnumSet.noneOf(KeyManager.Key.class);
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (guiScreen == null) {
            if (GameSettings.isKeyDown(this.powerKey))
                keys.add(KeyManager.Key.power);
            if (GameSettings.isKeyDown(this.reflectKey))
                keys.add(KeyManager.Key.reflect);
            if (GameSettings.isKeyDown(this.functionKey))
                keys.add(KeyManager.Key.function);
        }
        int currentKeyState = KeyManager.Key.toInt(keys);
        if (currentKeyState != this.lastKeyState) {
            this.processKeyUpdate(Minecraft.getMinecraft().thePlayer, currentKeyState);
            PacketHandler.INSTANCE.sendToServer(
                    new KeyStateSyncer(currentKeyState, Minecraft.getMinecraft().thePlayer.getEntityId()));
            this.lastKeyState = currentKeyState;
        }
    }
}
