package io.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface Ticker {
   static Ticker systemTicker() {
      return SystemTicker.INSTANCE;
   }

   static MockTicker newMockTicker() {
      return new DefaultMockTicker();
   }

   long initialNanoTime();

   long nanoTime();

   void sleep(long var1, TimeUnit var3) throws InterruptedException;

   default void sleepMillis(long delayMillis) throws InterruptedException {
      this.sleep(delayMillis, TimeUnit.MILLISECONDS);
   }
}
