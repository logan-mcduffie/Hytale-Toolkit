package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

final class Http3UnidirectionalStreamInboundServerHandler extends Http3UnidirectionalStreamInboundHandler {
   Http3UnidirectionalStreamInboundServerHandler(
      Http3FrameCodec.Http3FrameCodecFactory codecFactory,
      Http3ControlStreamInboundHandler localControlStreamHandler,
      Http3ControlStreamOutboundHandler remoteControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> unknownStreamHandlerFactory,
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
   }

   @Override
   void initPushStream(ChannelHandlerContext ctx, long id) {
      Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Server received push stream.", false);
   }
}
