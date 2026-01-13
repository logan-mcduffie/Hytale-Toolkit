package io.netty.util.concurrent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor> {
   boolean isShuttingDown();

   Future<?> shutdownGracefully();

   Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5);

   Future<?> terminationFuture();

   @Deprecated
   @Override
   void shutdown();

   @Deprecated
   @Override
   List<Runnable> shutdownNow();

   EventExecutor next();

   @Override
   Iterator<EventExecutor> iterator();

   Future<?> submit(Runnable var1);

   <T> Future<T> submit(Runnable var1, T var2);

   <T> Future<T> submit(Callable<T> var1);

   default Ticker ticker() {
      return Ticker.systemTicker();
   }

   ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

   <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

   ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

   ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}
