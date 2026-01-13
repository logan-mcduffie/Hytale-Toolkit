package io.netty.handler.codec.http3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.quic.QuicStreamChannel;

public abstract class Http3PushStreamClientInitializer extends ChannelInitializer<QuicStreamChannel> {
   protected final void initChannel(QuicStreamChannel ch) {
      if (Http3CodecUtils.isServerInitiatedQuicStream(ch)) {
         throw new IllegalArgumentException("Using client push stream initializer for server stream: " + ch.streamId());
      } else {
         Http3CodecUtils.verifyIsUnidirectional(ch);
         Http3ConnectionHandler connectionHandler = Http3CodecUtils.getConnectionHandlerOrClose(ch.parent());
         if (connectionHandler != null) {
            ChannelPipeline pipeline = ch.pipeline();
            Http3RequestStreamDecodeStateValidator decodeStateValidator = new Http3RequestStreamDecodeStateValidator();
            pipeline.addLast(connectionHandler.newCodec(Http3RequestStreamCodecState.NO_STATE, decodeStateValidator));
            pipeline.addLast(decodeStateValidator);
            pipeline.addLast(connectionHandler.newPushStreamValidationHandler(ch, decodeStateValidator));
            this.initPushStream(ch);
         }
      }
   }

   protected abstract void initPushStream(QuicStreamChannel var1);
}
