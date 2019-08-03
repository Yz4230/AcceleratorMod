package yz.acceleratormod.sound;

import net.minecraft.client.Minecraft;

public class SoundManager {
    public static void Play (SoundAtEntity sound) {
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }
}
