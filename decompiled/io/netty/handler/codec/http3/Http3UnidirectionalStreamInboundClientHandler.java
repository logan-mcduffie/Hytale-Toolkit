package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

final class Http3UnidirectionalStreamInboundClientHandler extends Http3UnidirectionalStreamInboundHandler {
   private final LongFunction<ChannelHandler> pushStreamHandlerFactory;

   Http3UnidirectionalStreamInboundClientHandler(
      Http3FrameCodec.Http3FrameCodecFactory codecFactory,
      Http3ControlStreamInboundHandler localControlStreamHandler,
      Http3ControlStreamOutboundHandler remoteControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> unknownStreamHandlerFactory,
      @Nullable LongFunction<ChannelHandler> pushStreamHandlerFactory,
      Supplier<ChannelHandler> qpackEncoderHandlerFactory,
      Supplier<ChannelHandler> qpackDecoderHandlerFactory
   ) {
      super(
         codecFactory,
         localControlStreamHandler,
         remoteControlStreamHandler,
         unknownStreamHandlerFactory,
         qpackEncoderHandlerFactory,
         qpackDecoderHandlerFactory
      );
      this.pushStreamHandlerFactory = pushStreamHandlerFactory == null
         ? __ -> Http3UnidirectionalStreamInboundHandler.ReleaseHandler.INSTANCE
         : pushStreamHandlerFactory;
   }

   @Override
   void initPushStream(ChannelHandlerContext ctx, long pushId) {
      Long maxPushId = this.remoteControlStreamHandler.sentMaxPushId();
      if (maxPushId == null) {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_ID_ERROR, "Received push stream before sending MAX_PUSH_ID frame.", false);
      } else if (maxPushId < pushId) {
         Http3CodecUtils.connectionError(
            ctx, Http3ErrorCode.H3_ID_ERROR, "Received push stream with ID " + pushId + " greater than the max push ID " + maxPushId + '.', false
         );
      } else {
         ChannelHandler pushStreamHandler = this.pushStreamHandlerFactory.apply(pushId);
         ctx.pipeline().replace(this, null, pushStreamHandler);
      }
   }
}
