package com.hypixel.hytale.protocol.io.netty;

import com.hypixel.hytale.protocol.PacketRegistry;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import com.hypixel.hytale.protocol.io.ProtocolException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import javax.annotation.Nonnull;

public class PacketDecoder extends ByteToMessageDecoder {
   private static final int LENGTH_PREFIX_SIZE = 4;
   private static final int PACKET_ID_SIZE = 4;
   private static final int MIN_FRAME_SIZE = 8;

   @Override
   protected void decode(@Nonnull ChannelHandlerContext ctx, @Nonnull ByteBuf in, @Nonnull List<Object> out) {
      if (in.readableBytes() >= 8) {
         in.markReaderIndex();
         int payloadLength = in.readIntLE();
         if (payloadLength >= 0 && payloadLength <= 1677721600) {
            int packetId = in.readIntLE();
            PacketRegistry.PacketInfo packetInfo = PacketRegistry.getById(packetId);
            if (packetInfo == null) {
               in.skipBytes(in.readableBytes());
               ProtocolUtil.closeConnection(ctx.channel());
            } else if (payloadLength > packetInfo.maxSize()) {
               in.skipBytes(in.readableBytes());
               ProtocolUtil.closeConnection(ctx.channel());
            } else if (in.readableBytes() < payloadLength) {
               in.resetReaderIndex();
            } else {
               PacketStatsRecorder statsRecorder = ctx.channel().attr(PacketStatsRecorder.CHANNEL_KEY).get();
               if (statsRecorder == null) {
                  statsRecorder = PacketStatsRecorder.NOOP;
               }

               try {
                  out.add(PacketIO.readFramedPacketWithInfo(in, payloadLength, packetInfo, statsRecorder));
               } catch (ProtocolException var9) {
                  in.skipBytes(in.readableBytes());
                  ProtocolUtil.closeConnection(ctx.channel());
               } catch (IndexOutOfBoundsException var10) {
                  in.skipBytes(in.readableBytes());
                  ProtocolUtil.closeConnection(ctx.channel());
               }
            }
         } else {
            in.skipBytes(in.readableBytes());
            ProtocolUtil.closeConnection(ctx.channel());
         }
      }
   }
}
