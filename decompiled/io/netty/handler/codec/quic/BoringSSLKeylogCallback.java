package io.netty.handler.codec.quic;

import javax.net.ssl.SSLEngine;

final class BoringSSLKeylogCallback {
   private final QuicheQuicSslEngineMap engineMap;
   private final BoringSSLKeylog keylog;

   BoringSSLKeylogCallback(QuicheQuicSslEngineMap engineMap, BoringSSLKeylog keylog) {
      this.engineMap = engineMap;
      this.keylog = keylog;
   }

   void logKey(long ssl, String key) {
      SSLEngine engine = this.engineMap.get(ssl);
      if (engine != null) {
         this.keylog.logKey(engine, key);
      }
   }
}
