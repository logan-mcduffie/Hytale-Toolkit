package io.netty.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.lang.Thread.State;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.jetbrains.annotations.VisibleForTesting;

public abstract class Recycler<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
   private static final Recycler.EnhancedHandle<?> NOOP_HANDLE = new Recycler.LocalPoolHandle(null);
   private static final Recycler.UnguardedLocalPool<?> NOOP_LOCAL_POOL = new Recycler.UnguardedLocalPool(0);
   private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
   private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
   private static final int RATIO;
   private static final int DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD;
   private static final boolean BLOCKING_POOL;
   private static final boolean BATCH_FAST_TL_ONLY;
   private final Recycler.LocalPool<?, T> localPool;
   private final FastThreadLocal<Recycler.LocalPool<?, T>> threadLocalPool;

   protected Recycler(int maxCapacity, boolean unguarded) {
      if (maxCapacity <= 0) {
         maxCapacity = 0;
      } else {
         maxCapacity = Math.max(4, maxCapacity);
      }

      this.threadLocalPool = null;
      if (maxCapacity == 0) {
         this.localPool = NOOP_LOCAL_POOL;
      } else {
         this.localPool = (Recycler.LocalPool<?, T>)(unguarded ? new Recycler.UnguardedLocalPool(maxCapacity) : new Recycler.GuardedLocalPool<>(maxCapacity));
      }
   }

   protected Recycler(boolean unguarded) {
      this(DEFAULT_MAX_CAPACITY_PER_THREAD, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD, unguarded);
   }

   protected Recycler(Thread owner, boolean unguarded) {
      this(DEFAULT_MAX_CAPACITY_PER_THREAD, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD, owner, unguarded);
   }

   protected Recycler(int maxCapacityPerThread) {
      this(maxCapacityPerThread, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   protected Recycler() {
      this(DEFAULT_MAX_CAPACITY_PER_THREAD);
   }

   protected Recycler(int chunksSize, int maxCapacityPerThread, boolean unguarded) {
      this(maxCapacityPerThread, RATIO, chunksSize, unguarded);
   }

   protected Recycler(int chunkSize, int maxCapacityPerThread, Thread owner, boolean unguarded) {
      this(maxCapacityPerThread, RATIO, chunkSize, owner, unguarded);
   }

   @Deprecated
   protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor) {
      this(maxCapacityPerThread, RATIO, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   @Deprecated
   protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread) {
      this(maxCapacityPerThread, ratio, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   @Deprecated
   protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread, int delayedQueueRatio) {
      this(maxCapacityPerThread, ratio, DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
   }

   protected Recycler(int maxCapacityPerThread, int interval, int chunkSize) {
      this(maxCapacityPerThread, interval, chunkSize, true, null, false);
   }

   protected Recycler(int maxCapacityPerThread, int interval, int chunkSize, boolean unguarded) {
      this(maxCapacityPerThread, interval, chunkSize, true, null, unguarded);
   }

   protected Recycler(int maxCapacityPerThread, int interval, int chunkSize, Thread owner, boolean unguarded) {
      this(maxCapacityPerThread, interval, chunkSize, false, owner, unguarded);
   }

   private Recycler(int maxCapacityPerThread, int ratio, int chunkSize, boolean useThreadLocalStorage, Thread owner, final boolean unguarded) {
      final int interval = Math.max(0, ratio);
      if (maxCapacityPerThread <= 0) {
         maxCapacityPerThread = 0;
         chunkSize = 0;
      } else {
         maxCapacityPerThread = Math.max(4, maxCapacityPerThread);
         chunkSize = Math.max(2, Math.min(chunkSize, maxCapacityPerThread >> 1));
      }

      if (maxCapacityPerThread > 0 && useThreadLocalStorage) {
         final int finalMaxCapacityPerThread = maxCapacityPerThread;
         final int finalChunkSize = chunkSize;
         this.threadLocalPool = new FastThreadLocal<Recycler.LocalPool<?, T>>() {
            protected Recycler.LocalPool<?, T> initialValue() {
               return (Recycler.LocalPool<?, T>)(unguarded
                  ? new Recycler.UnguardedLocalPool(finalMaxCapacityPerThread, interval, finalChunkSize)
                  : new Recycler.GuardedLocalPool<>(finalMaxCapacityPerThread, interval, finalChunkSize));
            }

            protected void onRemoval(Recycler.LocalPool<?, T> value) throws Exception {
               super.onRemoval(value);
               MessagePassingQueue<?> handles = value.pooledHandles;
               value.pooledHandles = null;
               value.owner = null;
               if (handles != null) {
                  handles.clear();
               }
            }
         };
         this.localPool = null;
      } else {
         this.threadLocalPool = null;
         if (maxCapacityPerThread == 0) {
            this.localPool = NOOP_LOCAL_POOL;
         } else {
            Objects.requireNonNull(owner, "owner");
            this.localPool = (Recycler.LocalPool<?, T>)(unguarded
               ? new Recycler.UnguardedLocalPool(owner, maxCapacityPerThread, interval, chunkSize)
               : new Recycler.GuardedLocalPool<>(owner, maxCapacityPerThread, interval, chunkSize));
         }
      }
   }

   public final T get() {
      if (this.localPool != null) {
         return this.localPool.getWith(this);
      } else {
         return PlatformDependent.isVirtualThread(Thread.currentThread()) && !FastThreadLocalThread.currentThreadHasFastThreadLocal()
            ? this.newObject((Recycler.Handle<T>)NOOP_HANDLE)
            : this.threadLocalPool.get().getWith(this);
      }
   }

   public static void unpinOwner(Recycler<?> recycler) {
      if (recycler.localPool != null) {
         recycler.localPool.owner = null;
      }
   }

   @Deprecated
   public final boolean recycle(T o, Recycler.Handle<T> handle) {
      if (handle == NOOP_HANDLE) {
         return false;
      } else {
         handle.recycle(o);
         return true;
      }
   }

   @VisibleForTesting
   final int threadLocalSize() {
      if (this.localPool != null) {
         return this.localPool.size();
      } else if (PlatformDependent.isVirtualThread(Thread.currentThread()) && !FastThreadLocalThread.currentThreadHasFastThreadLocal()) {
         return 0;
      } else {
         Recycler.LocalPool<?, T> pool = this.threadLocalPool.getIfExists();
         return pool == null ? 0 : pool.size();
      }
   }

   protected abstract T newObject(Recycler.Handle<T> var1);

   static {
      int maxCapacityPerThread = SystemPropertyUtil.getInt(
         "io.netty.recycler.maxCapacityPerThread", SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity", 4096)
      );
      if (maxCapacityPerThread < 0) {
         maxCapacityPerThread = 4096;
      }

      DEFAULT_MAX_CAPACITY_PER_THREAD = maxCapacityPerThread;
      DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD = SystemPropertyUtil.getInt("io.netty.recycler.chunkSize", 32);
      RATIO = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.ratio", 8));
      BLOCKING_POOL = SystemPropertyUtil.getBoolean("io.netty.recycler.blocking", false);
      BATCH_FAST_TL_ONLY = SystemPropertyUtil.getBoolean("io.netty.recycler.batchFastThreadLocalOnly", true);
      if (logger.isDebugEnabled()) {
         if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
            logger.debug("-Dio.netty.recycler.ratio: disabled");
            logger.debug("-Dio.netty.recycler.chunkSize: disabled");
            logger.debug("-Dio.netty.recycler.blocking: disabled");
            logger.debug("-Dio.netty.recycler.batchFastThreadLocalOnly: disabled");
         } else {
            logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", DEFAULT_MAX_CAPACITY_PER_THREAD);
            logger.debug("-Dio.netty.recycler.ratio: {}", RATIO);
            logger.debug("-Dio.netty.recycler.chunkSize: {}", DEFAULT_QUEUE_CHUNK_SIZE_PER_THREAD);
            logger.debug("-Dio.netty.recycler.blocking: {}", BLOCKING_POOL);
            logger.debug("-Dio.netty.recycler.batchFastThreadLocalOnly: {}", BATCH_FAST_TL_ONLY);
         }
      }
   }

   private static final class BlockingMessageQueue<T> implements MessagePassingQueue<T> {
      private final Queue<T> deque;
      private final int maxCapacity;

      BlockingMessageQueue(int maxCapacity) {
         this.maxCapacity = maxCapacity;
         this.deque = new ArrayDeque<>();
      }

      @Override
      public synchronized boolean offer(T e) {
         return this.deque.size() == this.maxCapacity ? false : this.deque.offer(e);
      }

      @Override
      public synchronized T poll() {
         return this.deque.poll();
      }

      @Override
      public synchronized T peek() {
         return this.deque.peek();
      }

      @Override
      public synchronized int size() {
         return this.deque.size();
      }

      @Override
      public synchronized void clear() {
         this.deque.clear();
      }

      @Override
      public synchronized boolean isEmpty() {
         return this.deque.isEmpty();
      }

      @Override
      public int capacity() {
         return this.maxCapacity;
      }

      @Override
      public boolean relaxedOffer(T e) {
         return this.offer(e);
      }

      @Override
      public T relaxedPoll() {
         return this.poll();
      }

      @Override
      public T relaxedPeek() {
         return this.peek();
      }

      @Override
      public int drain(MessagePassingQueue.Consumer<T> c, int limit) {
         T obj;
         int i;
         for (i = 0; i < limit && (obj = this.poll()) != null; i++) {
            c.accept(obj);
         }

         return i;
      }

      @Override
      public int fill(MessagePassingQueue.Supplier<T> s, int limit) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int drain(MessagePassingQueue.Consumer<T> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int fill(MessagePassingQueue.Supplier<T> s) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void drain(MessagePassingQueue.Consumer<T> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void fill(MessagePassingQueue.Supplier<T> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class DefaultHandle<T> extends Recycler.EnhancedHandle<T> {
      private static final int STATE_CLAIMED = 0;
      private static final int STATE_AVAILABLE = 1;
      private static final AtomicIntegerFieldUpdater<Recycler.DefaultHandle<?>> STATE_UPDATER;
      private volatile int state;
      private final Recycler.GuardedLocalPool<T> localPool;
      private T value;

      DefaultHandle(Recycler.GuardedLocalPool<T> localPool) {
         this.localPool = localPool;
      }

      @Override
      public void recycle(Object object) {
         if (object != this.value) {
            throw new IllegalArgumentException("object does not belong to handle");
         } else {
            this.toAvailable();
            this.localPool.release(this);
         }
      }

      @Override
      public void unguardedRecycle(Object object) {
         if (object != this.value) {
            throw new IllegalArgumentException("object does not belong to handle");
         } else {
            this.unguardedToAvailable();
            this.localPool.release(this);
         }
      }

      T claim() {
         assert this.state == 1;

         STATE_UPDATER.lazySet(this, 0);
         return this.value;
      }

      void set(T value) {
         this.value = value;
      }

      private void toAvailable() {
         int prev = STATE_UPDATER.getAndSet(this, 1);
         if (prev == 1) {
            throw new IllegalStateException("Object has been recycled already.");
         }
      }

      private void unguardedToAvailable() {
         int prev = this.state;
         if (prev == 1) {
            throw new IllegalStateException("Object has been recycled already.");
         } else {
            STATE_UPDATER.lazySet(this, 1);
         }
      }

      static {
         AtomicIntegerFieldUpdater<?> updater = AtomicIntegerFieldUpdater.newUpdater(Recycler.DefaultHandle.class, "state");
         STATE_UPDATER = (AtomicIntegerFieldUpdater<Recycler.DefaultHandle<?>>)updater;
      }
   }

   public abstract static class EnhancedHandle<T> implements Recycler.Handle<T> {
      public abstract void unguardedRecycle(Object var1);

      private EnhancedHandle() {
      }
   }

   private static final class GuardedLocalPool<T> extends Recycler.LocalPool<Recycler.DefaultHandle<T>, T> {
      GuardedLocalPool(int maxCapacity) {
         super(maxCapacity);
      }

      GuardedLocalPool(Thread owner, int maxCapacity, int ratioInterval, int chunkSize) {
         super(owner, maxCapacity, ratioInterval, chunkSize);
      }

      GuardedLocalPool(int maxCapacity, int ratioInterval, int chunkSize) {
         super(maxCapacity, ratioInterval, chunkSize);
      }

      @Override
      public T getWith(Recycler<T> recycler) {
         Recycler.DefaultHandle<T> handle = this.acquire();
         T obj;
         if (handle == null) {
            handle = this.canAllocatePooled() ? new Recycler.DefaultHandle<>(this) : null;
            if (handle != null) {
               obj = recycler.newObject(handle);
               handle.set(obj);
            } else {
               obj = recycler.newObject((Recycler.Handle<T>)Recycler.NOOP_HANDLE);
            }
         } else {
            obj = handle.claim();
         }

         return obj;
      }
   }

   public interface Handle<T> extends ObjectPool.Handle<T> {
   }

   private abstract static class LocalPool<H, T> {
      private final int ratioInterval;
      private final H[] batch;
      private int batchSize;
      private Thread owner;
      private MessagePassingQueue<H> pooledHandles;
      private int ratioCounter;

      LocalPool(int maxCapacity) {
         this.ratioInterval = maxCapacity == 0 ? -1 : 0;
         this.owner = null;
         this.batch = null;
         this.batchSize = 0;
         this.pooledHandles = createExternalMcPool(maxCapacity);
         this.ratioCounter = 0;
      }

      LocalPool(Thread owner, int maxCapacity, int ratioInterval, int chunkSize) {
         this.ratioInterval = ratioInterval;
         this.owner = owner;
         this.batch = (H[])(owner != null ? new Object[chunkSize] : null);
         this.batchSize = 0;
         this.pooledHandles = createExternalScPool(chunkSize, maxCapacity);
         this.ratioCounter = ratioInterval;
      }

      private static <H> MessagePassingQueue<H> createExternalMcPool(int maxCapacity) {
         if (maxCapacity == 0) {
            return null;
         } else {
            return (MessagePassingQueue<H>)(Recycler.BLOCKING_POOL
               ? new Recycler.BlockingMessageQueue(maxCapacity)
               : (MessagePassingQueue)PlatformDependent.newFixedMpmcQueue(maxCapacity));
         }
      }

      private static <H> MessagePassingQueue<H> createExternalScPool(int chunkSize, int maxCapacity) {
         if (maxCapacity == 0) {
            return null;
         } else {
            return (MessagePassingQueue<H>)(Recycler.BLOCKING_POOL
               ? new Recycler.BlockingMessageQueue(maxCapacity)
               : (MessagePassingQueue)PlatformDependent.newMpscQueue(chunkSize, maxCapacity));
         }
      }

      LocalPool(int maxCapacity, int ratioInterval, int chunkSize) {
         this(
            Recycler.BATCH_FAST_TL_ONLY && !FastThreadLocalThread.currentThreadHasFastThreadLocal() ? null : Thread.currentThread(),
            maxCapacity,
            ratioInterval,
            chunkSize
         );
      }

      protected final H acquire() {
         int size = this.batchSize;
         if (size == 0) {
            MessagePassingQueue<H> handles = this.pooledHandles;
            return handles == null ? null : handles.relaxedPoll();
         } else {
            int top = size - 1;
            H h = this.batch[top];
            this.batchSize = top;
            this.batch[top] = null;
            return h;
         }
      }

      protected final void release(H handle) {
         Thread owner = this.owner;
         if (owner != null && Thread.currentThread() == owner && this.batchSize < this.batch.length) {
            this.batch[this.batchSize] = handle;
            this.batchSize++;
         } else if (owner != null && isTerminated(owner)) {
            this.pooledHandles = null;
            this.owner = null;
         } else {
            MessagePassingQueue<H> handles = this.pooledHandles;
            if (handles != null) {
               handles.relaxedOffer(handle);
            }
         }
      }

      private static boolean isTerminated(Thread owner) {
         return PlatformDependent.isJ9Jvm() ? !owner.isAlive() : owner.getState() == State.TERMINATED;
      }

      boolean canAllocatePooled() {
         if (this.ratioInterval < 0) {
            return false;
         } else if (this.ratioInterval == 0) {
            return true;
         } else if (++this.ratioCounter >= this.ratioInterval) {
            this.ratioCounter = 0;
            return true;
         } else {
            return false;
         }
      }

      abstract T getWith(Recycler<T> var1);

      int size() {
         MessagePassingQueue<H> handles = this.pooledHandles;
         int externalSize = handles != null ? handles.size() : 0;
         return externalSize + (this.batch != null ? this.batchSize : 0);
      }
   }

   private static final class LocalPoolHandle<T> extends Recycler.EnhancedHandle<T> {
      private final Recycler.UnguardedLocalPool<T> pool;

      private LocalPoolHandle(Recycler.UnguardedLocalPool<T> pool) {
         this.pool = pool;
      }

      @Override
      public void recycle(T object) {
         Recycler.UnguardedLocalPool<T> pool = this.pool;
         if (pool != null) {
            pool.release(object);
         }
      }

      @Override
      public void unguardedRecycle(Object object) {
         Recycler.UnguardedLocalPool<T> pool = this.pool;
         if (pool != null) {
            pool.release((T)object);
         }
      }
   }

   private static final class UnguardedLocalPool<T> extends Recycler.LocalPool<T, T> {
      private final Recycler.EnhancedHandle<T> handle;

      UnguardedLocalPool(int maxCapacity) {
         super(maxCapacity);
         this.handle = maxCapacity == 0 ? null : new Recycler.LocalPoolHandle<>(this);
      }

      UnguardedLocalPool(Thread owner, int maxCapacity, int ratioInterval, int chunkSize) {
         super(owner, maxCapacity, ratioInterval, chunkSize);
         this.handle = new Recycler.LocalPoolHandle<>(this);
      }

      UnguardedLocalPool(int maxCapacity, int ratioInterval, int chunkSize) {
         super(maxCapacity, ratioInterval, chunkSize);
         this.handle = new Recycler.LocalPoolHandle<>(this);
      }

      @Override
      public T getWith(Recycler<T> recycler) {
         T obj = this.acquire();
         if (obj == null) {
            obj = recycler.newObject(this.canAllocatePooled() ? this.handle : Recycler.NOOP_HANDLE);
         }

         return obj;
      }
   }
}
