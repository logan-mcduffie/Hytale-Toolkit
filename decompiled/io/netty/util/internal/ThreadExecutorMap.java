package io.netty.util.internal;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadExecutorMap {
   private static final FastThreadLocal<EventExecutor> mappings = new FastThreadLocal<>();

   private ThreadExecutorMap() {
   }

   public static EventExecutor currentExecutor() {
      return mappings.get();
   }

   public static EventExecutor setCurrentExecutor(EventExecutor executor) {
      return mappings.getAndSet(executor);
   }

   public static Executor apply(final Executor executor, final EventExecutor eventExecutor) {
      ObjectUtil.checkNotNull(executor, "executor");
      ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
      return new Executor() {
         @Override
         public void execute(Runnable command) {
            executor.execute(ThreadExecutorMap.apply(command, eventExecutor));
         }
      };
   }

   public static Runnable apply(final Runnable command, final EventExecutor eventExecutor) {
      ObjectUtil.checkNotNull(command, "command");
      ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
      return new Runnable() {
         @Override
         public void run() {
            EventExecutor old = ThreadExecutorMap.setCurrentExecutor(eventExecutor);

            try {
               command.run();
            } finally {
               ThreadExecutorMap.setCurrentExecutor(old);
            }
         }
      };
   }

   public static ThreadFactory apply(final ThreadFactory threadFactory, final EventExecutor eventExecutor) {
      ObjectUtil.checkNotNull(threadFactory, "threadFactory");
      ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
      return new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            return threadFactory.newThread(ThreadExecutorMap.apply(r, eventExecutor));
         }
      };
   }
}
