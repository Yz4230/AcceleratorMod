package yz.acceleratormod.keymgr;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageKeyPressed implements IMessage {
    public int keyState;
    public int playerID;
    public MessageKeyPressed(){}

    public MessageKeyPressed(int keyPressed, int playerID) {
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
}
