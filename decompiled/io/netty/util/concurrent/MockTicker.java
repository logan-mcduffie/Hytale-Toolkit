package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface MockTicker extends Ticker {
   @Override
   default long initialNanoTime() {
      return 0L;
   }

   void advance(long var1, TimeUnit var3);

   default void advanceMillis(long amountMillis) {
      this.advance(amountMillis, TimeUnit.MILLISECONDS);
   }
}
