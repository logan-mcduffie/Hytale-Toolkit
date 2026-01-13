package io.netty.handler.codec.http3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.internal.StringUtil;

public abstract class Http3RequestStreamInitializer extends ChannelInitializer<QuicStreamChannel> {
   protected final void initChannel(QuicStreamChannel ch) {
      ChannelPipeline pipeline = ch.pipeline();
      Http3ConnectionHandler connectionHandler = ch.parent().pipeline().get(Http3ConnectionHandler.class);
      if (connectionHandler == null) {
         throw new IllegalStateException("Couldn't obtain the " + StringUtil.simpleClassName(Http3ConnectionHandler.class) + " of the parent Channel");
      } else {
         Http3RequestStreamEncodeStateValidator encodeStateValidator = new Http3RequestStreamEncodeStateValidator();
         Http3RequestStreamDecodeStateValidator decodeStateValidator = new Http3RequestStreamDecodeStateValidator();
         pipeline.addLast(connectionHandler.newCodec(encodeStateValidator, decodeStateValidator));
         pipeline.addLast(encodeStateValidator);
         pipeline.addLast(decodeStateValidator);
         pipeline.addLast(connectionHandler.newRequestStreamValidationHandler(ch, encodeStateValidator, decodeStateValidator));
         this.initRequestStream(ch);
      }
   }

   protected abstract void initRequestStream(QuicStreamChannel var1);
}
