package io.netty.buffer;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

abstract class PoolArena<T> implements PoolArenaMetric {
   private static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
   final PooledByteBufAllocator parent;
   final PoolSubpage<T>[] smallSubpagePools;
   private final PoolChunkList<T> q050;
   private final PoolChunkList<T> q025;
   private final PoolChunkList<T> q000;
   private final PoolChunkList<T> qInit;
   private final PoolChunkList<T> q075;
   private final PoolChunkList<T> q100;
   private final List<PoolChunkListMetric> chunkListMetrics;
   private long allocationsNormal;
   private final LongAdder allocationsSmall = new LongAdder();
   private final LongAdder allocationsHuge = new LongAdder();
   private final LongAdder activeBytesHuge = new LongAdder();
   private long deallocationsSmall;
   private long deallocationsNormal;
   private long pooledChunkAllocations;
   private long pooledChunkDeallocations;
   private final LongAdder deallocationsHuge = new LongAdder();
   final AtomicInteger numThreadCaches = new AtomicInteger();
   private final ReentrantLock lock = new ReentrantLock();
   final SizeClasses sizeClass;

   protected PoolArena(PooledByteBufAllocator parent, SizeClasses sizeClass) {
      assert null != sizeClass;

      this.parent = parent;
      this.sizeClass = sizeClass;
      this.smallSubpagePools = this.newSubpagePoolArray(sizeClass.nSubpages);

      for (int i = 0; i < this.smallSubpagePools.length; i++) {
         this.smallSubpagePools[i] = this.newSubpagePoolHead(i);
      }

      this.q100 = new PoolChunkList<>(this, null, 100, Integer.MAX_VALUE, sizeClass.chunkSize);
      this.q075 = new PoolChunkList<>(this, this.q100, 75, 100, sizeClass.chunkSize);
      this.q050 = new PoolChunkList<>(this, this.q100, 50, 100, sizeClass.chunkSize);
      this.q025 = new PoolChunkList<>(this, this.q050, 25, 75, sizeClass.chunkSize);
      this.q000 = new PoolChunkList<>(this, this.q025, 1, 50, sizeClass.chunkSize);
      this.qInit = new PoolChunkList<>(this, this.q000, Integer.MIN_VALUE, 25, sizeClass.chunkSize);
      this.q100.prevList(this.q075);
      this.q075.prevList(this.q050);
      this.q050.prevList(this.q025);
      this.q025.prevList(this.q000);
      this.q000.prevList(null);
      this.qInit.prevList(this.qInit);
      List<PoolChunkListMetric> metrics = new ArrayList<>(6);
      metrics.add(this.qInit);
      metrics.add(this.q000);
      metrics.add(this.q025);
      metrics.add(this.q050);
      metrics.add(this.q075);
      metrics.add(this.q100);
      this.chunkListMetrics = Collections.unmodifiableList(metrics);
   }

   private PoolSubpage<T> newSubpagePoolHead(int index) {
      PoolSubpage<T> head = new PoolSubpage<>(index);
      head.prev = head;
      head.next = head;
      return head;
   }

   private PoolSubpage<T>[] newSubpagePoolArray(int size) {
      return new PoolSubpage[size];
   }

   abstract boolean isDirect();

   PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
      PooledByteBuf<T> buf = this.newByteBuf(maxCapacity);
      this.allocate(cache, buf, reqCapacity);
      return buf;
   }

   private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
      int sizeIdx = this.sizeClass.size2SizeIdx(reqCapacity);
      if (sizeIdx <= this.sizeClass.smallMaxSizeIdx) {
         this.tcacheAllocateSmall(cache, buf, reqCapacity, sizeIdx);
      } else if (sizeIdx < this.sizeClass.nSizes) {
         this.tcacheAllocateNormal(cache, buf, reqCapacity, sizeIdx);
      } else {
         int normCapacity = this.sizeClass.directMemoryCacheAlignment > 0 ? this.sizeClass.normalizeSize(reqCapacity) : reqCapacity;
         this.allocateHuge(buf, normCapacity);
      }
   }

   private void tcacheAllocateSmall(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
      if (!cache.allocateSmall(this, buf, reqCapacity, sizeIdx)) {
         PoolSubpage<T> head = this.smallSubpagePools[sizeIdx];
         head.lock();

         boolean needsNormalAllocation;
         try {
            PoolSubpage<T> s = head.next;
            needsNormalAllocation = s == head;
            if (!needsNormalAllocation) {
               assert s.doNotDestroy && s.elemSize == this.sizeClass.sizeIdx2size(sizeIdx) : "doNotDestroy="
                  + s.doNotDestroy
                  + ", elemSize="
                  + s.elemSize
                  + ", sizeIdx="
                  + sizeIdx;

               long handle = s.allocate();

               assert handle >= 0L;

               s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity, cache, false);
            }
         } finally {
            head.unlock();
         }

         if (needsNormalAllocation) {
            this.lock();

            try {
               this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            } finally {
               this.unlock();
            }
         }

         this.incSmallAllocation();
      }
   }

   private void tcacheAllocateNormal(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
      if (!cache.allocateNormal(this, buf, reqCapacity, sizeIdx)) {
         this.lock();

         try {
            this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            this.allocationsNormal++;
         } finally {
            this.unlock();
         }
      }
   }

   private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolThreadCache threadCache) {
      assert this.lock.isHeldByCurrentThread();

      if (!this.q050.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q025.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q000.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.qInit.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q075.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
         PoolChunk<T> c = this.newChunk(this.sizeClass.pageSize, this.sizeClass.nPSizes, this.sizeClass.pageShifts, this.sizeClass.chunkSize);
         PooledByteBufAllocator.onAllocateChunk(c, true);
         boolean success = c.allocate(buf, reqCapacity, sizeIdx, threadCache);

         assert success;

         this.qInit.add(c);
         this.pooledChunkAllocations++;
      }
   }

   private void incSmallAllocation() {
      this.allocationsSmall.increment();
   }

   private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
      PoolChunk<T> chunk = this.newUnpooledChunk(reqCapacity);
      PooledByteBufAllocator.onAllocateChunk(chunk, false);
      this.activeBytesHuge.add(chunk.chunkSize());
      buf.initUnpooled(chunk, reqCapacity);
      this.allocationsHuge.increment();
   }

   void free(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolThreadCache cache) {
      chunk.decrementPinnedMemory(normCapacity);
      if (chunk.unpooled) {
         int size = chunk.chunkSize();
         this.destroyChunk(chunk);
         this.activeBytesHuge.add(-size);
         this.deallocationsHuge.increment();
      } else {
         PoolArena.SizeClass sizeClass = sizeClass(handle);
         if (cache != null && cache.add(this, chunk, nioBuffer, handle, normCapacity, sizeClass)) {
            return;
         }

         this.freeChunk(chunk, handle, normCapacity, sizeClass, nioBuffer, false);
      }
   }

   private static PoolArena.SizeClass sizeClass(long handle) {
      return PoolChunk.isSubpage(handle) ? PoolArena.SizeClass.Small : PoolArena.SizeClass.Normal;
   }

   void freeChunk(PoolChunk<T> chunk, long handle, int normCapacity, PoolArena.SizeClass sizeClass, ByteBuffer nioBuffer, boolean finalizer) {
      this.lock();

      boolean destroyChunk;
      try {
         if (!finalizer) {
            switch (sizeClass) {
               case Normal:
                  this.deallocationsNormal++;
                  break;
               case Small:
                  this.deallocationsSmall++;
                  break;
               default:
                  throw new Error("Unexpected size class: " + sizeClass);
            }
         }

         destroyChunk = !chunk.parent.free(chunk, handle, normCapacity, nioBuffer);
         if (destroyChunk) {
            this.pooledChunkDeallocations++;
         }
      } finally {
         this.unlock();
      }

      if (destroyChunk) {
         this.destroyChunk(chunk);
      }
   }

   void reallocate(PooledByteBuf<T> buf, int newCapacity) {
      assert newCapacity >= 0 && newCapacity <= buf.maxCapacity();

      int oldCapacity;
      PoolChunk<T> oldChunk;
      ByteBuffer oldNioBuffer;
      long oldHandle;
      T oldMemory;
      int oldOffset;
      int oldMaxLength;
      PoolThreadCache oldCache;
      synchronized (buf) {
         oldCapacity = buf.length;
         if (oldCapacity == newCapacity) {
            return;
         }

         oldChunk = buf.chunk;
         oldNioBuffer = buf.tmpNioBuf;
         oldHandle = buf.handle;
         oldMemory = buf.memory;
         oldOffset = buf.offset;
         oldMaxLength = buf.maxLength;
         oldCache = buf.cache;
         this.allocate(this.parent.threadCache(), buf, newCapacity);
      }

      int bytesToCopy;
      if (newCapacity > oldCapacity) {
         bytesToCopy = oldCapacity;
      } else {
         buf.trimIndicesToCapacity(newCapacity);
         bytesToCopy = newCapacity;
      }

      this.memoryCopy(oldMemory, oldOffset, buf, bytesToCopy);
      this.free(oldChunk, oldNioBuffer, oldHandle, oldMaxLength, oldCache);
   }

   @Override
   public int numThreadCaches() {
      return this.numThreadCaches.get();
   }

   @Override
   public int numTinySubpages() {
      return 0;
   }

   @Override
   public int numSmallSubpages() {
      return this.smallSubpagePools.length;
   }

   @Override
   public int numChunkLists() {
      return this.chunkListMetrics.size();
   }

   @Override
   public List<PoolSubpageMetric> tinySubpages() {
      return Collections.emptyList();
   }

   @Override
   public List<PoolSubpageMetric> smallSubpages() {
      return subPageMetricList(this.smallSubpagePools);
   }

   @Override
   public List<PoolChunkListMetric> chunkLists() {
      return this.chunkListMetrics;
   }

   private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
      List<PoolSubpageMetric> metrics = new ArrayList<>();

      for (PoolSubpage<?> head : pages) {
         if (head.next != head) {
            PoolSubpage<?> s = head.next;

            while (true) {
               metrics.add(s);
               s = s.next;
               if (s == head) {
                  break;
               }
            }
         }
      }

      return metrics;
   }

   @Override
   public long numAllocations() {
      this.lock();

      long allocsNormal;
      try {
         allocsNormal = this.allocationsNormal;
      } finally {
         this.unlock();
      }

      return this.allocationsSmall.sum() + allocsNormal + this.allocationsHuge.sum();
   }

   @Override
   public long numTinyAllocations() {
      return 0L;
   }

   @Override
   public long numSmallAllocations() {
      return this.allocationsSmall.sum();
   }

   @Override
   public long numNormalAllocations() {
      this.lock();

      long var1;
      try {
         var1 = this.allocationsNormal;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numChunkAllocations() {
      this.lock();

      long var1;
      try {
         var1 = this.pooledChunkAllocations;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numDeallocations() {
      this.lock();

      long deallocs;
      try {
         deallocs = this.deallocationsSmall + this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return deallocs + this.deallocationsHuge.sum();
   }

   @Override
   public long numTinyDeallocations() {
      return 0L;
   }

   @Override
   public long numSmallDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.deallocationsSmall;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numNormalDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numChunkDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.pooledChunkDeallocations;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numHugeAllocations() {
      return this.allocationsHuge.sum();
   }

   @Override
   public long numHugeDeallocations() {
      return this.deallocationsHuge.sum();
   }

   @Override
   public long numActiveAllocations() {
      long val = this.allocationsSmall.sum() + this.allocationsHuge.sum() - this.deallocationsHuge.sum();
      this.lock();

      try {
         val += this.allocationsNormal - (this.deallocationsSmall + this.deallocationsNormal);
      } finally {
         this.unlock();
      }

      return Math.max(val, 0L);
   }

   @Override
   public long numActiveTinyAllocations() {
      return 0L;
   }

   @Override
   public long numActiveSmallAllocations() {
      return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
   }

   @Override
   public long numActiveNormalAllocations() {
      this.lock();

      long val;
      try {
         val = this.allocationsNormal - this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return Math.max(val, 0L);
   }

   @Override
   public long numActiveChunks() {
      this.lock();

      long val;
      try {
         val = this.pooledChunkAllocations - this.pooledChunkDeallocations;
      } finally {
         this.unlock();
      }

      return Math.max(val, 0L);
   }

   @Override
   public long numActiveHugeAllocations() {
      return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
   }

   @Override
   public long numActiveBytes() {
      long val = this.activeBytesHuge.sum();
      this.lock();

      try {
         for (int i = 0; i < this.chunkListMetrics.size(); i++) {
            for (PoolChunkMetric m : this.chunkListMetrics.get(i)) {
               val += m.chunkSize();
            }
         }
      } finally {
         this.unlock();
      }

      return Math.max(0L, val);
   }

   public long numPinnedBytes() {
      long val = this.activeBytesHuge.sum();

      for (int i = 0; i < this.chunkListMetrics.size(); i++) {
         for (PoolChunkMetric m : this.chunkListMetrics.get(i)) {
            val += ((PoolChunk)m).pinnedBytes();
         }
      }

      return Math.max(0L, val);
   }

   protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

   protected abstract PoolChunk<T> newUnpooledChunk(int var1);

   protected abstract PooledByteBuf<T> newByteBuf(int var1);

   protected abstract void memoryCopy(T var1, int var2, PooledByteBuf<T> var3, int var4);

   protected abstract void destroyChunk(PoolChunk<T> var1);

   @Override
   public String toString() {
      this.lock();

      String var2;
      try {
         StringBuilder buf = new StringBuilder()
            .append("Chunk(s) at 0~25%:")
            .append(StringUtil.NEWLINE)
            .append(this.qInit)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 0~50%:")
            .append(StringUtil.NEWLINE)
            .append(this.q000)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 25~75%:")
            .append(StringUtil.NEWLINE)
            .append(this.q025)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 50~100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q050)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 75~100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q075)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q100)
            .append(StringUtil.NEWLINE)
            .append("small subpages:");
         appendPoolSubPages(buf, this.smallSubpagePools);
         buf.append(StringUtil.NEWLINE);
         var2 = buf.toString();
      } finally {
         this.unlock();
      }

      return var2;
   }

   private static void appendPoolSubPages(StringBuilder buf, PoolSubpage<?>[] subpages) {
      for (int i = 0; i < subpages.length; i++) {
         PoolSubpage<?> head = subpages[i];
         if (head.next != head && head.next != null) {
            buf.append(StringUtil.NEWLINE).append(i).append(": ");
            PoolSubpage<?> s = head.next;

            while (s != null) {
               buf.append(s);
               s = s.next;
               if (s == head) {
                  break;
               }
            }
         }
      }
   }

   @Override
   protected final void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         destroyPoolSubPages(this.smallSubpagePools);
         this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
      }
   }

   private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
      for (PoolSubpage<?> page : pages) {
         page.destroy();
      }
   }

   private void destroyPoolChunkLists(PoolChunkList<T>... chunkLists) {
      for (PoolChunkList<T> chunkList : chunkLists) {
         chunkList.destroy(this);
      }
   }

   void lock() {
      this.lock.lock();
   }

   void unlock() {
      this.lock.unlock();
   }

   @Override
   public int sizeIdx2size(int sizeIdx) {
      return this.sizeClass.sizeIdx2size(sizeIdx);
   }

   @Override
   public int sizeIdx2sizeCompute(int sizeIdx) {
      return this.sizeClass.sizeIdx2sizeCompute(sizeIdx);
   }

   @Override
   public long pageIdx2size(int pageIdx) {
      return this.sizeClass.pageIdx2size(pageIdx);
   }

   @Override
   public long pageIdx2sizeCompute(int pageIdx) {
      return this.sizeClass.pageIdx2sizeCompute(pageIdx);
   }

   @Override
   public int size2SizeIdx(int size) {
      return this.sizeClass.size2SizeIdx(size);
   }

   @Override
   public int pages2pageIdx(int pages) {
      return this.sizeClass.pages2pageIdx(pages);
   }

   @Override
   public int pages2pageIdxFloor(int pages) {
      return this.sizeClass.pages2pageIdxFloor(pages);
   }

   @Override
   public int normalizeSize(int size) {
      return this.sizeClass.normalizeSize(size);
   }

   static final class DirectArena extends PoolArena<ByteBuffer> {
      DirectArena(PooledByteBufAllocator parent, SizeClasses sizeClass) {
         super(parent, sizeClass);
      }

      @Override
      boolean isDirect() {
         return true;
      }

      @Override
      protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
         if (this.sizeClass.directMemoryCacheAlignment == 0) {
            CleanableDirectBuffer cleanableDirectBuffer = allocateDirect(chunkSize);
            ByteBuffer memory = cleanableDirectBuffer.buffer();
            return new PoolChunk<>(this, cleanableDirectBuffer, memory, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
         } else {
            CleanableDirectBuffer cleanableDirectBuffer = allocateDirect(chunkSize + this.sizeClass.directMemoryCacheAlignment);
            ByteBuffer base = cleanableDirectBuffer.buffer();
            ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.sizeClass.directMemoryCacheAlignment);
            return new PoolChunk<>(this, cleanableDirectBuffer, base, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
         }
      }

      @Override
      protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
         if (this.sizeClass.directMemoryCacheAlignment == 0) {
            CleanableDirectBuffer cleanableDirectBuffer = allocateDirect(capacity);
            ByteBuffer memory = cleanableDirectBuffer.buffer();
            return new PoolChunk<>(this, cleanableDirectBuffer, memory, memory, capacity);
         } else {
            CleanableDirectBuffer cleanableDirectBuffer = allocateDirect(capacity + this.sizeClass.directMemoryCacheAlignment);
            ByteBuffer base = cleanableDirectBuffer.buffer();
            ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.sizeClass.directMemoryCacheAlignment);
            return new PoolChunk<>(this, cleanableDirectBuffer, base, memory, capacity);
         }
      }

      private static CleanableDirectBuffer allocateDirect(int capacity) {
         return PlatformDependent.allocateDirect(capacity);
      }

      @Override
      protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
         PooledByteBufAllocator.onDeallocateChunk(chunk, !chunk.unpooled);
         chunk.cleanable.clean();
      }

      @Override
      protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
         return (PooledByteBuf<ByteBuffer>)(PoolArena.HAS_UNSAFE
            ? PooledUnsafeDirectByteBuf.newInstance(maxCapacity)
            : PooledDirectByteBuf.newInstance(maxCapacity));
      }

      protected void memoryCopy(ByteBuffer src, int srcOffset, PooledByteBuf<ByteBuffer> dstBuf, int length) {
         if (length != 0) {
            if (PoolArena.HAS_UNSAFE) {
               PlatformDependent.copyMemory(
                  PlatformDependent.directBufferAddress(src) + srcOffset, PlatformDependent.directBufferAddress(dstBuf.memory) + dstBuf.offset, length
               );
            } else {
               src = src.duplicate();
               ByteBuffer dst = dstBuf.internalNioBuffer();
               ((Buffer)src).position(srcOffset).limit(srcOffset + length);
               ((Buffer)dst).position(dstBuf.offset);
               dst.put(src);
            }
         }
      }
   }

   static final class HeapArena extends PoolArena<byte[]> {
      private final AtomicReference<PoolChunk<byte[]>> lastDestroyedChunk = new AtomicReference<>();

      HeapArena(PooledByteBufAllocator parent, SizeClasses sizeClass) {
         super(parent, sizeClass);
      }

      private static byte[] newByteArray(int size) {
         return PlatformDependent.allocateUninitializedArray(size);
      }

      @Override
      boolean isDirect() {
         return false;
      }

      @Override
      protected PoolChunk<byte[]> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
         PoolChunk<byte[]> chunk = this.lastDestroyedChunk.getAndSet(null);
         if (chunk == null) {
            return new PoolChunk<>(this, null, null, newByteArray(chunkSize), pageSize, pageShifts, chunkSize, maxPageIdx);
         } else {
            assert chunk.chunkSize == chunkSize && chunk.pageSize == pageSize && chunk.maxPageIdx == maxPageIdx && chunk.pageShifts == pageShifts;

            return chunk;
         }
      }

      @Override
      protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
         return new PoolChunk<>(this, null, null, newByteArray(capacity), capacity);
      }

      @Override
      protected void destroyChunk(PoolChunk<byte[]> chunk) {
         PooledByteBufAllocator.onDeallocateChunk(chunk, !chunk.unpooled);
         if (!chunk.unpooled && this.lastDestroyedChunk.get() == null) {
            this.lastDestroyedChunk.set(chunk);
         }
      }

      @Override
      protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
         return (PooledByteBuf<byte[]>)(PoolArena.HAS_UNSAFE
            ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity)
            : PooledHeapByteBuf.newInstance(maxCapacity));
      }

      protected void memoryCopy(byte[] src, int srcOffset, PooledByteBuf<byte[]> dst, int length) {
         if (length != 0) {
            System.arraycopy(src, srcOffset, dst.memory, dst.offset, length);
         }
      }
   }

   static enum SizeClass {
      Small,
      Normal;
   }
}
