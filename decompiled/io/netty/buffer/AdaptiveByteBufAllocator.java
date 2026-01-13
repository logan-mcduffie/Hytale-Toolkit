package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class AdaptiveByteBufAllocator extends AbstractByteBufAllocator implements ByteBufAllocatorMetricProvider, ByteBufAllocatorMetric {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AdaptiveByteBufAllocator.class);
   private static final boolean DEFAULT_USE_CACHED_MAGAZINES_FOR_NON_EVENT_LOOP_THREADS = SystemPropertyUtil.getBoolean(
      "io.netty.allocator.useCachedMagazinesForNonEventLoopThreads", false
   );
   private final AdaptivePoolingAllocator direct;
   private final AdaptivePoolingAllocator heap;

   public AdaptiveByteBufAllocator() {
      this(!PlatformDependent.isExplicitNoPreferDirect());
   }

   public AdaptiveByteBufAllocator(boolean preferDirect) {
      this(preferDirect, DEFAULT_USE_CACHED_MAGAZINES_FOR_NON_EVENT_LOOP_THREADS);
   }

   public AdaptiveByteBufAllocator(boolean preferDirect, boolean useCacheForNonEventLoopThreads) {
      super(preferDirect);
      this.direct = new AdaptivePoolingAllocator(new AdaptiveByteBufAllocator.DirectChunkAllocator(this), useCacheForNonEventLoopThreads);
      this.heap = new AdaptivePoolingAllocator(new AdaptiveByteBufAllocator.HeapChunkAllocator(this), useCacheForNonEventLoopThreads);
   }

   @Override
   protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
      return toLeakAwareBuffer(this.heap.allocate(initialCapacity, maxCapacity));
   }

   @Override
   protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
      return toLeakAwareBuffer(this.direct.allocate(initialCapacity, maxCapacity));
   }

   @Override
   public boolean isDirectBufferPooled() {
      return true;
   }

   @Override
   public long usedHeapMemory() {
      return this.heap.usedMemory();
   }

   @Override
   public long usedDirectMemory() {
      return this.direct.usedMemory();
   }

   @Override
   public ByteBufAllocatorMetric metric() {
      return this;
   }

   static {
      logger.debug("-Dio.netty.allocator.useCachedMagazinesForNonEventLoopThreads: {}", DEFAULT_USE_CACHED_MAGAZINES_FOR_NON_EVENT_LOOP_THREADS);
   }

   private static final class DirectChunkAllocator implements AdaptivePoolingAllocator.ChunkAllocator {
      private final ByteBufAllocator allocator;

      private DirectChunkAllocator(ByteBufAllocator allocator) {
         this.allocator = allocator;
      }

      @Override
      public AbstractByteBuf allocate(int initialCapacity, int maxCapacity) {
         return (AbstractByteBuf)(PlatformDependent.hasUnsafe()
            ? UnsafeByteBufUtil.newUnsafeDirectByteBuf(this.allocator, initialCapacity, maxCapacity, false)
            : new UnpooledDirectByteBuf(this.allocator, initialCapacity, maxCapacity, false));
      }
   }

   private static final class HeapChunkAllocator implements AdaptivePoolingAllocator.ChunkAllocator {
      private final ByteBufAllocator allocator;

      private HeapChunkAllocator(ByteBufAllocator allocator) {
         this.allocator = allocator;
      }

      @Override
      public AbstractByteBuf allocate(int initialCapacity, int maxCapacity) {
         return (AbstractByteBuf)(PlatformDependent.hasUnsafe()
            ? new UnpooledUnsafeHeapByteBuf(this.allocator, initialCapacity, maxCapacity)
            : new UnpooledHeapByteBuf(this.allocator, initialCapacity, maxCapacity));
      }
   }
}
