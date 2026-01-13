package io.netty.handler.codec.quic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicSslEngineMap {
   private final ConcurrentMap<Long, QuicheQuicSslEngine> engines = new ConcurrentHashMap<>();

   @Nullable
   QuicheQuicSslEngine get(long ssl) {
      return this.engines.get(ssl);
   }

   @Nullable
   QuicheQuicSslEngine remove(long ssl) {
      return this.engines.remove(ssl);
   }

   void put(long ssl, QuicheQuicSslEngine engine) {
      this.engines.put(ssl, engine);
   }
}
