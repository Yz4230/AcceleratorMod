package yz.acceleratormod.keymgr;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageKeyPressed implements IMessage {
    public int key;
    public MessageKeyPressed(){}

    public MessageKeyPressed(int keyPressed) {
        this.key = keyPressed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.key);
    }
}
