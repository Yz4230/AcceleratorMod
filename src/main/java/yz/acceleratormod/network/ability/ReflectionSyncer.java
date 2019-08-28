package yz.acceleratormod.network.ability;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import yz.acceleratormod.choker.ChokerUtil;

public class ReflectionSyncer implements IMessage, IMessageHandler<ReflectionSyncer, IMessage> {
    private int playerID;
    private int entityID;

    public ReflectionSyncer(){}

    public ReflectionSyncer(EntityPlayer player, Entity entity) {
        this.playerID = player.getEntityId();
        this.entityID = entity.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerID = buf.readInt();
        this.entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeInt(this.entityID);
    }

    @Override
    public IMessage onMessage(ReflectionSyncer message, MessageContext ctx) {
        Entity targetEntity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
        ChokerUtil.flipVelocity(targetEntity);
        return null;
    }
}
