package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.LongLongHashMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.atomic.AtomicReference;

public class FastThreadLocalThread extends Thread {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(FastThreadLocalThread.class);
   private static final AtomicReference<FastThreadLocalThread.FallbackThreadSet> fallbackThreads = new AtomicReference<>(
      FastThreadLocalThread.FallbackThreadSet.EMPTY
   );
   private final boolean cleanupFastThreadLocals;
   private InternalThreadLocalMap threadLocalMap;

   public FastThreadLocalThread() {
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable target) {
      super(FastThreadLocalRunnable.wrap(target));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup group, Runnable target) {
      super(group, FastThreadLocalRunnable.wrap(target));
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(String name) {
      super(name);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(ThreadGroup group, String name) {
      super(group, name);
      this.cleanupFastThreadLocals = false;
   }

   public FastThreadLocalThread(Runnable target, String name) {
      super(FastThreadLocalRunnable.wrap(target), name);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup group, Runnable target, String name) {
      super(group, FastThreadLocalRunnable.wrap(target), name);
      this.cleanupFastThreadLocals = true;
   }

   public FastThreadLocalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
      super(group, FastThreadLocalRunnable.wrap(target), name, stackSize);
      this.cleanupFastThreadLocals = true;
   }

   public final InternalThreadLocalMap threadLocalMap() {
      if (this != Thread.currentThread() && logger.isWarnEnabled()) {
         logger.warn(new RuntimeException("It's not thread-safe to get 'threadLocalMap' which doesn't belong to the caller thread"));
      }

      return this.threadLocalMap;
   }

   public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
      if (this != Thread.currentThread() && logger.isWarnEnabled()) {
         logger.warn(new RuntimeException("It's not thread-safe to set 'threadLocalMap' which doesn't belong to the caller thread"));
      }

      this.threadLocalMap = threadLocalMap;
   }

   @Deprecated
   public boolean willCleanupFastThreadLocals() {
      return this.cleanupFastThreadLocals;
   }

   @Deprecated
   public static boolean willCleanupFastThreadLocals(Thread thread) {
      return thread instanceof FastThreadLocalThread && ((FastThreadLocalThread)thread).willCleanupFastThreadLocals();
   }

   public static boolean currentThreadWillCleanupFastThreadLocals() {
      Thread currentThread = currentThread();
      return currentThread instanceof FastThreadLocalThread
         ? ((FastThreadLocalThread)currentThread).willCleanupFastThreadLocals()
         : isFastThreadLocalVirtualThread();
   }

   public static boolean currentThreadHasFastThreadLocal() {
      return currentThread() instanceof FastThreadLocalThread || isFastThreadLocalVirtualThread();
   }

   private static boolean isFastThreadLocalVirtualThread() {
      return fallbackThreads.get().contains(currentThread().getId());
   }

   public static void runWithFastThreadLocal(Runnable runnable) {
      Thread current = currentThread();
      if (current instanceof FastThreadLocalThread) {
         throw new IllegalStateException("Caller is a real FastThreadLocalThread");
      } else {
         long id = current.getId();
         fallbackThreads.updateAndGet(set -> {
            if (set.contains(id)) {
               throw new IllegalStateException("Reentrant call to run()");
            } else {
               return set.add(id);
            }
         });

         try {
            runnable.run();
         } finally {
            fallbackThreads.getAndUpdate(set -> set.remove(id));
            FastThreadLocal.removeAll();
         }
      }
   }

   public boolean permitBlockingCalls() {
      return false;
   }

   private static final class FallbackThreadSet {
      static final FastThreadLocalThread.FallbackThreadSet EMPTY = new FastThreadLocalThread.FallbackThreadSet();
      private static final long EMPTY_VALUE = 0L;
      private final LongLongHashMap map;

      private FallbackThreadSet() {
         this.map = new LongLongHashMap(0L);
      }

      private FallbackThreadSet(LongLongHashMap map) {
         this.map = map;
      }

      public boolean contains(long threadId) {
         long key = threadId >>> 6;
         long bit = 1L << (int)(threadId & 63L);
         long bitmap = this.map.get(key);
         return (bitmap & bit) != 0L;
      }

      public FastThreadLocalThread.FallbackThreadSet add(long threadId) {
         long key = threadId >>> 6;
         long bit = 1L << (int)(threadId & 63L);
         LongLongHashMap newMap = new LongLongHashMap(this.map);
         long oldBitmap = newMap.get(key);
         long newBitmap = oldBitmap | bit;
         newMap.put(key, newBitmap);
         return new FastThreadLocalThread.FallbackThreadSet(newMap);
      }

      public FastThreadLocalThread.FallbackThreadSet remove(long threadId) {
         long key = threadId >>> 6;
         long bit = 1L << (int)(threadId & 63L);
         long oldBitmap = this.map.get(key);
         if ((oldBitmap & bit) == 0L) {
            return this;
         } else {
            LongLongHashMap newMap = new LongLongHashMap(this.map);
            long newBitmap = oldBitmap & ~bit;
            if (newBitmap != 0L) {
               newMap.put(key, newBitmap);
            } else {
               newMap.remove(key);
            }

            return new FastThreadLocalThread.FallbackThreadSet(newMap);
         }
      }
   }
}
