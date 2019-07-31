package yz.acceleratormod.keymgr;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;

public class KeyInfoGetter implements IMessageHandler<MessageKeyPressed, IMessage> {
    @Override
    public IMessage onMessage(MessageKeyPressed message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().theWorld;
        ACCL.keyManager.processKeyUpdate(((EntityPlayer) world.getEntityByID(message.playerID)), message.keyState);
        return null;
    }
}
