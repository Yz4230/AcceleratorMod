package yz.acceleratormod.network.keymgr;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import yz.acceleratormod.ACCL;

public class KeyStateSyncer implements IMessage, IMessageHandler<KeyStateSyncer, IMessage> {
    public int keyState;
    public int playerID;

    public KeyStateSyncer(){}

    public KeyStateSyncer(int keyPressed, int playerID) {
        this.keyState = keyPressed;
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.keyState = buf.readInt();
        this.playerID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.keyState);
        buf.writeInt(this.playerID);
    }

    @Override
    public IMessage onMessage(KeyStateSyncer message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().theWorld;
        ACCL.keyManager.processKeyUpdate(((EntityPlayer) world.getEntityByID(message.playerID)), message.keyState);
        return null;
    }
}
