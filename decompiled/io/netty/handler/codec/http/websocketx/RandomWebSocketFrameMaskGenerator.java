package io.netty.handler.codec.http.websocketx;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomWebSocketFrameMaskGenerator implements WebSocketFrameMaskGenerator {
   public static final RandomWebSocketFrameMaskGenerator INSTANCE = new RandomWebSocketFrameMaskGenerator();

   private RandomWebSocketFrameMaskGenerator() {
   }

   @Override
   public int nextMask() {
      return ThreadLocalRandom.current().nextInt();
   }
}
