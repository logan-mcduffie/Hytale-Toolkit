package io.netty.handler.codec.quic;

import io.netty.channel.ChannelHandler;
import java.util.concurrent.Executor;
import java.util.function.Function;

public final class QuicClientCodecBuilder extends QuicCodecBuilder<QuicClientCodecBuilder> {
   public QuicClientCodecBuilder() {
      super(false);
   }

   private QuicClientCodecBuilder(QuicCodecBuilder<QuicClientCodecBuilder> builder) {
      super(builder);
   }

   public QuicClientCodecBuilder clone() {
      return new QuicClientCodecBuilder(this);
   }

   @Override
   ChannelHandler build(
      QuicheConfig config,
      Function<QuicChannel, ? extends QuicSslEngine> sslEngineProvider,
      Executor sslTaskExecutor,
      int localConnIdLength,
      FlushStrategy flushStrategy
   ) {
      return new QuicheQuicClientCodec(config, sslEngineProvider, sslTaskExecutor, localConnIdLength, flushStrategy);
   }
}
