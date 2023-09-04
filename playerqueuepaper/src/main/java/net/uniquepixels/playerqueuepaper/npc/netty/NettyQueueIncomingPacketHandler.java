package net.uniquepixels.playerqueuepaper.npc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;

import java.util.List;

public class NettyQueueIncomingPacketHandler extends MessageToMessageDecoder<ClientboundEntityEventPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ClientboundEntityEventPacket msg, List<Object> out) throws Exception {

    }
}
