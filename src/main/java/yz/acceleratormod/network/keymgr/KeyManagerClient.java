package yz.acceleratormod.network.keymgr;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import yz.acceleratormod.ACCL;
import yz.acceleratormod.network.PacketHandler;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class KeyManagerClient extends KeyManager {
    private final KeyBinding powerKey = new KeyBinding("key.choker.power", Keyboard.KEY_F, "key." + ACCL.MOD_ID);
    private final KeyBinding strongStepKey = new KeyBinding("key.choker.strong_step", Keyboard.KEY_R, "key." + ACCL.MOD_ID);
    private final KeyBinding changeGravityKey = new KeyBinding("key.choker.change_gravity", Keyboard.KEY_G, "key." + ACCL.MOD_ID);
    private final KeyBinding functionKey = Minecraft.getMinecraft().gameSettings.keyBindSprint;
    private final KeyBinding jumpKey = Minecraft.getMinecraft().gameSettings.keyBindJump;
    private int lastKeyState = 0;
    private Map<Key, Integer> keyDelay = new HashMap<>();

    @SideOnly(Side.CLIENT)

    public KeyManagerClient() {
        ClientRegistry.registerKeyBinding(this.powerKey);
        ClientRegistry.registerKeyBinding(this.strongStepKey);
        ClientRegistry.registerKeyBinding(this.functionKey);
        ClientRegistry.registerKeyBinding(this.changeGravityKey);
    }

    @SideOnly(Side.CLIENT)
    public void sendKeyUpdate() {
        Set<Key> keys = EnumSet.noneOf(Key.class);
        GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;
        if (guiScreen == null) {
            if (GameSettings.isKeyDown(this.powerKey)) this.delayCheck(Key.power, keys, 10);
            if (GameSettings.isKeyDown(this.strongStepKey)) this.delayCheck(Key.step, keys, 10);
            if (GameSettings.isKeyDown(this.functionKey)) this.delayCheck(Key.function, keys, 0);
            if (GameSettings.isKeyDown(this.changeGravityKey)) this.delayCheck(Key.change_gravity, keys, 0);
            if (GameSettings.isKeyDown(this.jumpKey)) this.delayCheck(Key.jump, keys, 0);

            this.keyDelay.replaceAll((k, v) -> v == 0 ? v : v - 1);
        }
        int currentKeyState = Key.toInt(keys);
        if (currentKeyState != this.lastKeyState) {
            this.processKeyUpdate(Minecraft.getMinecraft().thePlayer, currentKeyState, this.lastKeyState);
            PacketHandler.INST_keyState.sendToServer(
                    new KeyStateSyncer(currentKeyState, this.lastKeyState, Minecraft.getMinecraft().thePlayer.getEntityId()));
            this.lastKeyState = currentKeyState;
        }
    }

    private void delayCheck(Key key, Set<Key> keySet, int delay) {
        this.keyDelay.putIfAbsent(key, 0);
        if (this.keyDelay.get(key) == 0) {
            keySet.add(key);
            this.keyDelay.put(key, delay);
        }
    }
}
