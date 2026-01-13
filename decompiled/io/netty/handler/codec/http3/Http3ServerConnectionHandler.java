package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.internal.ObjectUtil;
import java.util.function.LongFunction;
import org.jetbrains.annotations.Nullable;

public final class Http3ServerConnectionHandler extends Http3ConnectionHandler {
   private final ChannelHandler requestStreamHandler;

   public Http3ServerConnectionHandler(ChannelHandler requestStreamHandler) {
      this(requestStreamHandler, null, null, null, true);
   }

   public Http3ServerConnectionHandler(
      ChannelHandler requestStreamHandler,
      @Nullable ChannelHandler inboundControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> unknownInboundStreamHandlerFactory,
      @Nullable Http3SettingsFrame localSettings,
      boolean disableQpackDynamicTable
   ) {
      super(true, inboundControlStreamHandler, unknownInboundStreamHandlerFactory, localSettings, disableQpackDynamicTable);
      this.requestStreamHandler = ObjectUtil.checkNotNull(requestStreamHandler, "requestStreamHandler");
   }

   @Override
   void initBidirectionalStream(ChannelHandlerContext ctx, QuicStreamChannel streamChannel) {
      ChannelPipeline pipeline = streamChannel.pipeline();
      Http3RequestStreamEncodeStateValidator encodeStateValidator = new Http3RequestStreamEncodeStateValidator();
      Http3RequestStreamDecodeStateValidator decodeStateValidator = new Http3RequestStreamDecodeStateValidator();
      pipeline.addLast(this.newCodec(encodeStateValidator, decodeStateValidator));
      pipeline.addLast(encodeStateValidator);
      pipeline.addLast(decodeStateValidator);
      pipeline.addLast(this.newRequestStreamValidationHandler(streamChannel, encodeStateValidator, decodeStateValidator));
      pipeline.addLast(this.requestStreamHandler);
   }

   @Override
   void initUnidirectionalStream(ChannelHandlerContext ctx, QuicStreamChannel streamChannel) {
      long maxTableCapacity = this.maxTableCapacity();
      streamChannel.pipeline()
         .addLast(
            new Http3UnidirectionalStreamInboundServerHandler(
               this.codecFactory,
               this.localControlStreamHandler,
               this.remoteControlStreamHandler,
               this.unknownInboundStreamHandlerFactory,
               () -> new QpackEncoderHandler(maxTableCapacity, this.qpackDecoder),
               () -> new QpackDecoderHandler(this.qpackEncoder)
            )
         );
   }
}
