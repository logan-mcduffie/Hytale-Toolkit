package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

abstract class Http3UnidirectionalStreamInboundHandler extends ByteToMessageDecoder {
   private static final AttributeKey<Boolean> REMOTE_CONTROL_STREAM = AttributeKey.valueOf("H3_REMOTE_CONTROL_STREAM");
   private static final AttributeKey<Boolean> REMOTE_QPACK_DECODER_STREAM = AttributeKey.valueOf("H3_REMOTE_QPACK_DECODER_STREAM");
   private static final AttributeKey<Boolean> REMOTE_QPACK_ENCODER_STREAM = AttributeKey.valueOf("H3_REMOTE_QPACK_ENCODER_STREAM");
   final Http3FrameCodec.Http3FrameCodecFactory codecFactory;
   final Http3ControlStreamInboundHandler localControlStreamHandler;
   final Http3ControlStreamOutboundHandler remoteControlStreamHandler;
   final Supplier<ChannelHandler> qpackEncoderHandlerFactory;
   final Supplier<ChannelHandler> qpackDecoderHandlerFactory;
   final LongFunction<ChannelHandler> unknownStreamHandlerFactory;

   Http3UnidirectionalStreamInboundHandler(
      Http3FrameCodec.Http3FrameCodecFactory codecFactory,
      Http3ControlStreamInboundHandler localControlStreamHandler,
      Http3ControlStreamOutboundHandler remoteControlStreamHandler,
      @Nullable LongFunction<ChannelHandler> unknownStreamHandlerFactory,
      Supplier<ChannelHandler> qpackEncoderHandlerFactory,
      Supplier<ChannelHandler> qpackDecoderHandlerFactory
   ) {
      this.codecFactory = codecFactory;
      this.localControlStreamHandler = localControlStreamHandler;
      this.remoteControlStreamHandler = remoteControlStreamHandler;
      this.qpackEncoderHandlerFactory = qpackEncoderHandlerFactory;
      this.qpackDecoderHandlerFactory = qpackDecoderHandlerFactory;
      if (unknownStreamHandlerFactory == null) {
         unknownStreamHandlerFactory = type -> Http3UnidirectionalStreamInboundHandler.ReleaseHandler.INSTANCE;
      }

      this.unknownStreamHandlerFactory = unknownStreamHandlerFactory;
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
      if (in.isReadable()) {
         int len = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
         if (in.readableBytes() >= len) {
            long type = Http3CodecUtils.readVariableLengthInteger(in, len);
            switch ((int)type) {
               case 0:
                  this.initControlStream(ctx);
                  break;
               case 1:
                  int pushIdLen = Http3CodecUtils.numBytesForVariableLengthInteger(in.getByte(in.readerIndex()));
                  if (in.readableBytes() < pushIdLen) {
                     return;
                  }

                  long pushId = Http3CodecUtils.readVariableLengthInteger(in, pushIdLen);
                  this.initPushStream(ctx, pushId);
                  break;
               case 2:
                  this.initQpackEncoderStream(ctx);
                  break;
               case 3:
                  this.initQpackDecoderStream(ctx);
                  break;
               default:
                  this.initUnknownStream(ctx, type);
            }
         }
      }
   }

   private void initControlStream(ChannelHandlerContext ctx) {
      if (ctx.channel().parent().attr(REMOTE_CONTROL_STREAM).setIfAbsent(true) == null) {
         ctx.pipeline().addLast(this.localControlStreamHandler);
         ctx.pipeline()
            .replace(
               this,
               null,
               this.codecFactory
                  .newCodec(Http3ControlStreamFrameTypeValidator.INSTANCE, Http3RequestStreamCodecState.NO_STATE, Http3RequestStreamCodecState.NO_STATE)
            );
      } else {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Received multiple control streams.", false);
      }
   }

   private boolean ensureStreamNotExistsYet(ChannelHandlerContext ctx, AttributeKey<Boolean> key) {
      return ctx.channel().parent().attr(key).setIfAbsent(true) == null;
   }

   abstract void initPushStream(ChannelHandlerContext var1, long var2);

   private void initQpackEncoderStream(ChannelHandlerContext ctx) {
      if (this.ensureStreamNotExistsYet(ctx, REMOTE_QPACK_ENCODER_STREAM)) {
         ctx.pipeline().replace(this, null, this.qpackEncoderHandlerFactory.get());
      } else {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Received multiple QPACK encoder streams.", false);
      }
   }

   private void initQpackDecoderStream(ChannelHandlerContext ctx) {
      if (this.ensureStreamNotExistsYet(ctx, REMOTE_QPACK_DECODER_STREAM)) {
         ctx.pipeline().replace(this, null, this.qpackDecoderHandlerFactory.get());
      } else {
         Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_STREAM_CREATION_ERROR, "Received multiple QPACK decoder streams.", false);
      }
   }

   private void initUnknownStream(ChannelHandlerContext ctx, long streamType) {
      ctx.pipeline().replace(this, null, this.unknownStreamHandlerFactory.apply(streamType));
   }

   static final class ReleaseHandler extends ChannelInboundHandlerAdapter {
      static final Http3UnidirectionalStreamInboundHandler.ReleaseHandler INSTANCE = new Http3UnidirectionalStreamInboundHandler.ReleaseHandler();

      @Override
      public boolean isSharable() {
         return true;
      }

      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         ReferenceCountUtil.release(msg);
      }
   }
}
