package io.netty.buffer;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.LongAdder;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator implements ByteBufAllocatorMetricProvider {
   private final UnpooledByteBufAllocator.UnpooledByteBufAllocatorMetric metric = new UnpooledByteBufAllocator.UnpooledByteBufAllocatorMetric();
   private final boolean disableLeakDetector;
   private final boolean noCleaner;
   public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());

   public UnpooledByteBufAllocator(boolean preferDirect) {
      this(preferDirect, false);
   }

   public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector) {
      this(preferDirect, disableLeakDetector, PlatformDependent.useDirectBufferNoCleaner());
   }

   public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector, boolean tryNoCleaner) {
      super(preferDirect);
      this.disableLeakDetector = disableLeakDetector;
      this.noCleaner = tryNoCleaner && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor();
   }

   @Override
   protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
      return (ByteBuf)(PlatformDependent.hasUnsafe()
         ? new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity)
         : new UnpooledByteBufAllocator.InstrumentedUnpooledHeapByteBuf(this, initialCapacity, maxCapacity));
   }

   @Override
   protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
      ByteBuf buf;
      if (PlatformDependent.hasUnsafe()) {
         buf = (ByteBuf)(this.noCleaner
            ? new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(this, initialCapacity, maxCapacity)
            : new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeDirectByteBuf(this, initialCapacity, maxCapacity));
      } else {
         buf = new UnpooledByteBufAllocator.InstrumentedUnpooledDirectByteBuf(this, initialCapacity, maxCapacity);
      }

      return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
      CompositeByteBuf buf = new CompositeByteBuf(this, false, maxNumComponents);
      return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
      CompositeByteBuf buf = new CompositeByteBuf(this, true, maxNumComponents);
      return this.disableLeakDetector ? buf : toLeakAwareBuffer(buf);
   }

   @Override
   public boolean isDirectBufferPooled() {
      return false;
   }

   @Override
   public ByteBufAllocatorMetric metric() {
      return this.metric;
   }

   void incrementDirect(int amount) {
      this.metric.directCounter.add(amount);
   }

   void decrementDirect(int amount) {
      this.metric.directCounter.add(-amount);
   }

   void incrementHeap(int amount) {
      this.metric.heapCounter.add(amount);
   }

   void decrementHeap(int amount) {
      this.metric.heapCounter.add(-amount);
   }

   private static final class DecrementingCleanableDirectBuffer implements CleanableDirectBuffer {
      private final UnpooledByteBufAllocator alloc;
      private final CleanableDirectBuffer delegate;

      private DecrementingCleanableDirectBuffer(ByteBufAllocator alloc, CleanableDirectBuffer delegate) {
         this(alloc, delegate, delegate.buffer().capacity());
      }

      private DecrementingCleanableDirectBuffer(ByteBufAllocator alloc, CleanableDirectBuffer delegate, int capacityConsumed) {
         this.alloc = (UnpooledByteBufAllocator)alloc;
         this.alloc.incrementDirect(capacityConsumed);
         this.delegate = delegate;
      }

      @Override
      public ByteBuffer buffer() {
         return this.delegate.buffer();
      }

      @Override
      public void clean() {
         int capacity = this.delegate.buffer().capacity();
         this.delegate.clean();
         this.alloc.decrementDirect(capacity);
      }

      @Override
      public boolean hasMemoryAddress() {
         return this.delegate.hasMemoryAddress();
      }

      @Override
      public long memoryAddress() {
         return this.delegate.memoryAddress();
      }
   }

   private static final class InstrumentedUnpooledDirectByteBuf extends UnpooledDirectByteBuf {
      InstrumentedUnpooledDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
         super(alloc, initialCapacity, maxCapacity);
      }

      @Override
      protected CleanableDirectBuffer allocateDirectBuffer(int initialCapacity) {
         CleanableDirectBuffer buffer = super.allocateDirectBuffer(initialCapacity);
         return new UnpooledByteBufAllocator.DecrementingCleanableDirectBuffer(this.alloc(), buffer);
      }

      @Override
      protected ByteBuffer allocateDirect(int initialCapacity) {
         throw new UnsupportedOperationException();
      }

      @Override
      protected void freeDirect(ByteBuffer buffer) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class InstrumentedUnpooledHeapByteBuf extends UnpooledHeapByteBuf {
      InstrumentedUnpooledHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
         super(alloc, initialCapacity, maxCapacity);
      }

      @Override
      protected byte[] allocateArray(int initialCapacity) {
         byte[] bytes = super.allocateArray(initialCapacity);
         ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(bytes.length);
         return bytes;
      }

      @Override
      protected void freeArray(byte[] array) {
         int length = array.length;
         super.freeArray(array);
         ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(length);
      }
   }

   private static final class InstrumentedUnpooledUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      InstrumentedUnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
         super(alloc, initialCapacity, maxCapacity);
      }

      @Override
      protected CleanableDirectBuffer allocateDirectBuffer(int capacity) {
         CleanableDirectBuffer buffer = super.allocateDirectBuffer(capacity);
         return new UnpooledByteBufAllocator.DecrementingCleanableDirectBuffer(this.alloc(), buffer);
      }

      @Override
      protected ByteBuffer allocateDirect(int initialCapacity) {
         throw new UnsupportedOperationException();
      }

      @Override
      protected void freeDirect(ByteBuffer buffer) {
         throw new UnsupportedOperationException();
      }
   }

   private static final class InstrumentedUnpooledUnsafeHeapByteBuf extends UnpooledUnsafeHeapByteBuf {
      InstrumentedUnpooledUnsafeHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
         super(alloc, initialCapacity, maxCapacity);
      }

      @Override
      protected byte[] allocateArray(int initialCapacity) {
         byte[] bytes = super.allocateArray(initialCapacity);
         ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(bytes.length);
         return bytes;
      }

      @Override
      protected void freeArray(byte[] array) {
         int length = array.length;
         super.freeArray(array);
         ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(length);
      }
   }

   private static final class InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeNoCleanerDirectByteBuf {
      InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
         super(alloc, initialCapacity, maxCapacity, true);
      }

      @Override
      protected CleanableDirectBuffer allocateDirectBuffer(int capacity) {
         CleanableDirectBuffer buffer = super.allocateDirectBuffer(capacity);
         return new UnpooledByteBufAllocator.DecrementingCleanableDirectBuffer(this.alloc(), buffer);
      }

      @Override
      CleanableDirectBuffer reallocateDirect(CleanableDirectBuffer oldBuffer, int initialCapacity) {
         int capacity = oldBuffer.buffer().capacity();
         CleanableDirectBuffer buffer = super.reallocateDirect(oldBuffer, initialCapacity);
         return new UnpooledByteBufAllocator.DecrementingCleanableDirectBuffer(this.alloc(), buffer, buffer.buffer().capacity() - capacity);
      }
   }

   private static final class UnpooledByteBufAllocatorMetric implements ByteBufAllocatorMetric {
      final LongAdder directCounter = new LongAdder();
      final LongAdder heapCounter = new LongAdder();

      private UnpooledByteBufAllocatorMetric() {
      }

      @Override
      public long usedHeapMemory() {
         return this.heapCounter.sum();
      }

      @Override
      public long usedDirectMemory() {
         return this.directCounter.sum();
      }

      @Override
      public String toString() {
         return StringUtil.simpleClassName(this) + "(usedHeapMemory: " + this.usedHeapMemory() + "; usedDirectMemory: " + this.usedDirectMemory() + ')';
      }
   }
}
