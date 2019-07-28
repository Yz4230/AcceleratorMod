package yz.acceleratormod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Debugtool {
    public static void Log(String Message) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        entityPlayer.addChatComponentMessage(new ChatComponentText(Message));
    }
}
