package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.quic.QuicStreamChannel;
import java.util.function.LongFunction;
import org.jetbrains.annotations.Nullable;

public final class Http3ClientConnectionHandler extends Http3ConnectionHandler {
   private final LongFunction<ChannelHandler> pushStreamHandlerFactory;

   public Http3ClientConnectionHandler() {
      this(null, null, null, null, true);
   }

   public Http3ClientConnectionHandler(
      @Nullable ChannelHandler inboundControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> pushStreamHandlerFactory,
      @Nullable LongFunction<ChannelHandler> unknownInboundStreamHandlerFactory,
      @Nullable Http3SettingsFrame localSettings,
      boolean disableQpackDynamicTable
   ) {
      super(false, inboundControlStreamHandler, unknownInboundStreamHandlerFactory, localSettings, disableQpackDynamicTable);
      this.pushStreamHandlerFactory = pushStreamHandlerFactory;
   }

   @Override
   void initBidirectionalStream(ChannelHandlerContext ctx, QuicStreamChannel channel) {
      Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Server initiated bidirectional streams are not allowed", true);
   }

   @Override
   void initUnidirectionalStream(ChannelHandlerContext ctx, QuicStreamChannel streamChannel) {
      long maxTableCapacity = this.maxTableCapacity();
      streamChannel.pipeline()
         .addLast(
            new Http3UnidirectionalStreamInboundClientHandler(
               this.codecFactory,
               this.localControlStreamHandler,
               this.remoteControlStreamHandler,
               this.unknownInboundStreamHandlerFactory,
               this.pushStreamHandlerFactory,
               () -> new QpackEncoderHandler(maxTableCapacity, this.qpackDecoder),
               () -> new QpackDecoderHandler(this.qpackEncoder)
            )
         );
   }
}
