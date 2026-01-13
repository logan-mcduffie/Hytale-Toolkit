package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

final class FastThreadLocalRunnable implements Runnable {
   private final Runnable runnable;

   private FastThreadLocalRunnable(Runnable runnable) {
      this.runnable = ObjectUtil.checkNotNull(runnable, "runnable");
   }

   @Override
   public void run() {
      try {
         this.runnable.run();
      } finally {
         FastThreadLocal.removeAll();
      }
   }

   static Runnable wrap(Runnable runnable) {
      return (Runnable)(runnable instanceof FastThreadLocalRunnable ? runnable : new FastThreadLocalRunnable(runnable));
   }
}
