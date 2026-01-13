package io.netty.handler.codec.quic;

import io.netty.util.Mapping;

final class BoringSSLTlsextServernameCallback {
   private final QuicheQuicSslEngineMap engineMap;
   private final Mapping<? super String, ? extends QuicSslContext> mapping;

   BoringSSLTlsextServernameCallback(QuicheQuicSslEngineMap engineMap, Mapping<? super String, ? extends QuicSslContext> mapping) {
      this.engineMap = engineMap;
      this.mapping = mapping;
   }

   long selectCtx(long ssl, String serverName) {
      QuicheQuicSslEngine engine = this.engineMap.get(ssl);
      if (engine == null) {
         return -1L;
      } else {
         QuicSslContext context = this.mapping.map(serverName);
         return context == null ? -1L : engine.moveTo(serverName, (QuicheQuicSslContext)context);
      }
   }
}
