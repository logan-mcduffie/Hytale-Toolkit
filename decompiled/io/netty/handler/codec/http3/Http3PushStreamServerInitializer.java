package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.internal.ObjectUtil;

public abstract class Http3PushStreamServerInitializer extends ChannelInitializer<QuicStreamChannel> {
   private final long pushId;

   protected Http3PushStreamServerInitializer(long pushId) {
      this.pushId = ObjectUtil.checkPositiveOrZero(pushId, "pushId");
   }

   protected final void initChannel(QuicStreamChannel ch) {
      if (!Http3CodecUtils.isServerInitiatedQuicStream(ch)) {
         throw new IllegalArgumentException("Using server push stream initializer for client stream: " + ch.streamId());
      } else {
         Http3CodecUtils.verifyIsUnidirectional(ch);
         ByteBuf buffer = ch.alloc().buffer(16);
         Http3CodecUtils.writeVariableLengthInteger(buffer, 1L);
         Http3CodecUtils.writeVariableLengthInteger(buffer, this.pushId);
         ch.write(buffer);
         Http3ConnectionHandler connectionHandler = Http3CodecUtils.getConnectionHandlerOrClose(ch.parent());
         if (connectionHandler != null) {
            ChannelPipeline pipeline = ch.pipeline();
            Http3RequestStreamEncodeStateValidator encodeStateValidator = new Http3RequestStreamEncodeStateValidator();
            pipeline.addLast(connectionHandler.newCodec(encodeStateValidator, Http3RequestStreamCodecState.NO_STATE));
            pipeline.addLast(encodeStateValidator);
            pipeline.addLast(connectionHandler.newPushStreamValidationHandler(ch, Http3RequestStreamCodecState.NO_STATE));
            this.initPushStream(ch);
         }
      }
   }

   protected abstract void initPushStream(QuicStreamChannel var1);
}
