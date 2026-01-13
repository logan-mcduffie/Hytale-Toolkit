package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor extends AbstractEventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ImmediateEventExecutor.class);
   public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
   private static final FastThreadLocal<Queue<Runnable>> DELAYED_RUNNABLES = new FastThreadLocal<Queue<Runnable>>() {
      protected Queue<Runnable> initialValue() throws Exception {
         return new ArrayDeque<>();
      }
   };
   private static final FastThreadLocal<Boolean> RUNNING = new FastThreadLocal<Boolean>() {
      protected Boolean initialValue() throws Exception {
         return false;
      }
   };
   private final Future<?> terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());

   private ImmediateEventExecutor() {
   }

   @Override
   public boolean inEventLoop() {
      return true;
   }

   @Override
   public boolean inEventLoop(Thread thread) {
      return true;
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
   }

   @Override
   public boolean isShuttingDown() {
      return false;
   }

   @Override
   public boolean isShutdown() {
      return false;
   }

   @Override
   public boolean isTerminated() {
      return false;
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) {
      return false;
   }

   @Override
   public void execute(Runnable command) {
      ObjectUtil.checkNotNull(command, "command");
      if (!RUNNING.get()) {
         RUNNING.set(true);

         try {
            command.run();
         } catch (Throwable var15) {
            logger.info("Throwable caught while executing Runnable {}", command, var15);
         } finally {
            Queue<Runnable> delayedRunnables = DELAYED_RUNNABLES.get();

            Runnable runnable;
            while ((runnable = delayedRunnables.poll()) != null) {
               try {
                  runnable.run();
               } catch (Throwable var14) {
                  logger.info("Throwable caught while executing Runnable {}", runnable, var14);
               }
            }

            RUNNING.set(false);
         }
      } else {
         DELAYED_RUNNABLES.get().add(command);
      }
   }

   @Override
   public <V> Promise<V> newPromise() {
      return new ImmediateEventExecutor.ImmediatePromise<>(this);
   }

   @Override
   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new ImmediateEventExecutor.ImmediateProgressivePromise<>(this);
   }

   static class ImmediateProgressivePromise<V> extends DefaultProgressivePromise<V> {
      ImmediateProgressivePromise(EventExecutor executor) {
         super(executor);
      }

      @Override
      protected void checkDeadLock() {
      }
   }

   static class ImmediatePromise<V> extends DefaultPromise<V> {
      ImmediatePromise(EventExecutor executor) {
         super(executor);
      }

      @Override
      protected void checkDeadLock() {
      }
   }
}
