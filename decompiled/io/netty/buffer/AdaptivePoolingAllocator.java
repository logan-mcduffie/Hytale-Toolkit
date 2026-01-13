package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.NettyRuntime;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.concurrent.MpscIntQueue;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RefCnt;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.StampedLock;
import java.util.function.IntSupplier;

final class AdaptivePoolingAllocator {
   private static final int LOW_MEM_THRESHOLD = 536870912;
   private static final boolean IS_LOW_MEM = Runtime.getRuntime().maxMemory() <= 536870912L;
   private static final boolean DISABLE_THREAD_LOCAL_MAGAZINES_ON_LOW_MEM = SystemPropertyUtil.getBoolean(
      "io.netty.allocator.disableThreadLocalMagazinesOnLowMemory", true
   );
   static final int MIN_CHUNK_SIZE = 131072;
   private static final int EXPANSION_ATTEMPTS = 3;
   private static final int INITIAL_MAGAZINES = 1;
   private static final int RETIRE_CAPACITY = 256;
   private static final int MAX_STRIPES = IS_LOW_MEM ? 1 : NettyRuntime.availableProcessors() * 2;
   private static final int BUFS_PER_CHUNK = 8;
   private static final int MAX_CHUNK_SIZE = IS_LOW_MEM ? 2097152 : 8388608;
   private static final int MAX_POOLED_BUF_SIZE = MAX_CHUNK_SIZE / 8;
   private static final int CHUNK_REUSE_QUEUE = Math.max(
      2, SystemPropertyUtil.getInt("io.netty.allocator.chunkReuseQueueCapacity", NettyRuntime.availableProcessors() * 2)
   );
   private static final int MAGAZINE_BUFFER_QUEUE_CAPACITY = SystemPropertyUtil.getInt("io.netty.allocator.magazineBufferQueueCapacity", 1024);
   private static final int[] SIZE_CLASSES = new int[]{32, 64, 128, 256, 512, 640, 1024, 1152, 2048, 2304, 4096, 4352, 8192, 8704, 16384, 16896, 32768, 65536};
   private static final int SIZE_CLASSES_COUNT = SIZE_CLASSES.length;
   private static final byte[] SIZE_INDEXES = new byte[SIZE_CLASSES[SIZE_CLASSES_COUNT - 1] / 32 + 1];
   private final AdaptivePoolingAllocator.ChunkAllocator chunkAllocator;
   private final AdaptivePoolingAllocator.ChunkRegistry chunkRegistry;
   private final AdaptivePoolingAllocator.MagazineGroup[] sizeClassedMagazineGroups;
   private final AdaptivePoolingAllocator.MagazineGroup largeBufferMagazineGroup;
   private final FastThreadLocal<AdaptivePoolingAllocator.MagazineGroup[]> threadLocalGroup;

   AdaptivePoolingAllocator(AdaptivePoolingAllocator.ChunkAllocator chunkAllocator, final boolean useCacheForNonEventLoopThreads) {
      this.chunkAllocator = ObjectUtil.checkNotNull(chunkAllocator, "chunkAllocator");
      this.chunkRegistry = new AdaptivePoolingAllocator.ChunkRegistry();
      this.sizeClassedMagazineGroups = createMagazineGroupSizeClasses(this, false);
      this.largeBufferMagazineGroup = new AdaptivePoolingAllocator.MagazineGroup(
         this, chunkAllocator, new AdaptivePoolingAllocator.HistogramChunkControllerFactory(true), false
      );
      boolean disableThreadLocalGroups = IS_LOW_MEM && DISABLE_THREAD_LOCAL_MAGAZINES_ON_LOW_MEM;
      this.threadLocalGroup = disableThreadLocalGroups
         ? null
         : new FastThreadLocal<AdaptivePoolingAllocator.MagazineGroup[]>() {
            protected AdaptivePoolingAllocator.MagazineGroup[] initialValue() {
               return !useCacheForNonEventLoopThreads && ThreadExecutorMap.currentExecutor() == null
                  ? null
                  : AdaptivePoolingAllocator.createMagazineGroupSizeClasses(AdaptivePoolingAllocator.this, true);
            }

            protected void onRemoval(AdaptivePoolingAllocator.MagazineGroup[] groups) throws Exception {
               if (groups != null) {
                  for (AdaptivePoolingAllocator.MagazineGroup group : groups) {
                     group.free();
                  }
               }
            }
         };
   }

   private static AdaptivePoolingAllocator.MagazineGroup[] createMagazineGroupSizeClasses(AdaptivePoolingAllocator allocator, boolean isThreadLocal) {
      AdaptivePoolingAllocator.MagazineGroup[] groups = new AdaptivePoolingAllocator.MagazineGroup[SIZE_CLASSES.length];

      for (int i = 0; i < SIZE_CLASSES.length; i++) {
         int segmentSize = SIZE_CLASSES[i];
         groups[i] = new AdaptivePoolingAllocator.MagazineGroup(
            allocator, allocator.chunkAllocator, new AdaptivePoolingAllocator.SizeClassChunkControllerFactory(segmentSize), isThreadLocal
         );
      }

      return groups;
   }

   private static Queue<AdaptivePoolingAllocator.Chunk> createSharedChunkQueue() {
      return PlatformDependent.newFixedMpmcQueue(CHUNK_REUSE_QUEUE);
   }

   ByteBuf allocate(int size, int maxCapacity) {
      return this.allocate(size, maxCapacity, Thread.currentThread(), null);
   }

   private AdaptivePoolingAllocator.AdaptiveByteBuf allocate(int size, int maxCapacity, Thread currentThread, AdaptivePoolingAllocator.AdaptiveByteBuf buf) {
      AdaptivePoolingAllocator.AdaptiveByteBuf allocated = null;
      if (size <= MAX_POOLED_BUF_SIZE) {
         int index = sizeClassIndexOf(size);
         AdaptivePoolingAllocator.MagazineGroup[] magazineGroups;
         if (!FastThreadLocalThread.currentThreadWillCleanupFastThreadLocals() || IS_LOW_MEM || (magazineGroups = this.threadLocalGroup.get()) == null) {
            magazineGroups = this.sizeClassedMagazineGroups;
         }

         if (index < magazineGroups.length) {
            allocated = magazineGroups[index].allocate(size, maxCapacity, currentThread, buf);
         } else if (!IS_LOW_MEM) {
            allocated = this.largeBufferMagazineGroup.allocate(size, maxCapacity, currentThread, buf);
         }
      }

      if (allocated == null) {
         allocated = this.allocateFallback(size, maxCapacity, currentThread, buf);
      }

      return allocated;
   }

   private static int sizeIndexOf(int size) {
      return size + 31 >> 5;
   }

   static int sizeClassIndexOf(int size) {
      int sizeIndex = sizeIndexOf(size);
      return sizeIndex < SIZE_INDEXES.length ? SIZE_INDEXES[sizeIndex] : SIZE_CLASSES_COUNT;
   }

   static int[] getSizeClasses() {
      return (int[])SIZE_CLASSES.clone();
   }

   private AdaptivePoolingAllocator.AdaptiveByteBuf allocateFallback(
      int size, int maxCapacity, Thread currentThread, AdaptivePoolingAllocator.AdaptiveByteBuf buf
   ) {
      AdaptivePoolingAllocator.Magazine magazine;
      if (buf != null) {
         AdaptivePoolingAllocator.Chunk chunk = buf.chunk;
         if (chunk == null || chunk == AdaptivePoolingAllocator.Magazine.MAGAZINE_FREED || (magazine = chunk.currentMagazine()) == null) {
            magazine = this.getFallbackMagazine(currentThread);
         }
      } else {
         magazine = this.getFallbackMagazine(currentThread);
         buf = magazine.newBuffer();
      }

      AbstractByteBuf innerChunk = this.chunkAllocator.allocate(size, maxCapacity);
      AdaptivePoolingAllocator.Chunk chunk = new AdaptivePoolingAllocator.Chunk(innerChunk, magazine, false, chunkSize -> true);
      this.chunkRegistry.add(chunk);

      try {
         chunk.readInitInto(buf, size, size, maxCapacity);
      } finally {
         chunk.release();
      }

      return buf;
   }

   private AdaptivePoolingAllocator.Magazine getFallbackMagazine(Thread currentThread) {
      AdaptivePoolingAllocator.Magazine[] mags = this.largeBufferMagazineGroup.magazines;
      return mags[(int)currentThread.getId() & mags.length - 1];
   }

   void reallocate(int size, int maxCapacity, AdaptivePoolingAllocator.AdaptiveByteBuf into) {
      AdaptivePoolingAllocator.AdaptiveByteBuf result = this.allocate(size, maxCapacity, Thread.currentThread(), into);

      assert result == into : "Re-allocation created separate buffer instance";
   }

   long usedMemory() {
      return this.chunkRegistry.totalCapacity();
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         this.free();
      }
   }

   private void free() {
      this.largeBufferMagazineGroup.free();
   }

   static int sizeToBucket(int size) {
      return AdaptivePoolingAllocator.HistogramChunkController.sizeToBucket(size);
   }

   static {
      if (MAGAZINE_BUFFER_QUEUE_CAPACITY < 2) {
         throw new IllegalArgumentException("MAGAZINE_BUFFER_QUEUE_CAPACITY: " + MAGAZINE_BUFFER_QUEUE_CAPACITY + " (expected: >= " + 2 + ')');
      } else {
         int lastIndex = 0;

         for (int i = 0; i < SIZE_CLASSES_COUNT; i++) {
            int sizeClass = SIZE_CLASSES[i];

            assert (sizeClass & 5) == 0 : "Size class must be a multiple of 32";

            int sizeIndex = sizeIndexOf(sizeClass);
            Arrays.fill(SIZE_INDEXES, lastIndex + 1, sizeIndex + 1, (byte)i);
            lastIndex = sizeIndex;
         }
      }
   }

   static final class AdaptiveByteBuf extends AbstractReferenceCountedByteBuf {
      private final ObjectPool.Handle<AdaptivePoolingAllocator.AdaptiveByteBuf> handle;
      private int startIndex;
      private AbstractByteBuf rootParent;
      AdaptivePoolingAllocator.Chunk chunk;
      private int length;
      private int maxFastCapacity;
      private ByteBuffer tmpNioBuf;
      private boolean hasArray;
      private boolean hasMemoryAddress;

      AdaptiveByteBuf(ObjectPool.Handle<AdaptivePoolingAllocator.AdaptiveByteBuf> recyclerHandle) {
         super(0);
         this.handle = ObjectUtil.checkNotNull(recyclerHandle, "recyclerHandle");
      }

      void init(
         AbstractByteBuf unwrapped,
         AdaptivePoolingAllocator.Chunk wrapped,
         int readerIndex,
         int writerIndex,
         int startIndex,
         int size,
         int capacity,
         int maxCapacity
      ) {
         this.startIndex = startIndex;
         this.chunk = wrapped;
         this.length = size;
         this.maxFastCapacity = capacity;
         this.maxCapacity(maxCapacity);
         this.setIndex0(readerIndex, writerIndex);
         this.hasArray = unwrapped.hasArray();
         this.hasMemoryAddress = unwrapped.hasMemoryAddress();
         this.rootParent = unwrapped;
         this.tmpNioBuf = null;
         if (PlatformDependent.isJfrEnabled() && AllocateBufferEvent.isEventEnabled()) {
            AllocateBufferEvent event = new AllocateBufferEvent();
            if (event.shouldCommit()) {
               event.fill(this, AdaptiveByteBufAllocator.class);
               event.chunkPooled = wrapped.pooled;
               AdaptivePoolingAllocator.Magazine m = wrapped.magazine;
               event.chunkThreadLocal = m != null && m.allocationLock == null;
               event.commit();
            }
         }
      }

      private AbstractByteBuf rootParent() {
         AbstractByteBuf rootParent = this.rootParent;
         if (rootParent != null) {
            return rootParent;
         } else {
            throw new IllegalReferenceCountException();
         }
      }

      @Override
      public int capacity() {
         return this.length;
      }

      @Override
      public int maxFastWritableBytes() {
         return Math.min(this.maxFastCapacity, this.maxCapacity()) - this.writerIndex;
      }

      @Override
      public ByteBuf capacity(int newCapacity) {
         if (this.length <= newCapacity && newCapacity <= this.maxFastCapacity) {
            this.ensureAccessible();
            this.length = newCapacity;
            return this;
         } else {
            this.checkNewCapacity(newCapacity);
            if (newCapacity < this.capacity()) {
               this.length = newCapacity;
               this.trimIndicesToCapacity(newCapacity);
               return this;
            } else {
               if (PlatformDependent.isJfrEnabled() && ReallocateBufferEvent.isEventEnabled()) {
                  ReallocateBufferEvent event = new ReallocateBufferEvent();
                  if (event.shouldCommit()) {
                     event.fill(this, AdaptiveByteBufAllocator.class);
                     event.newCapacity = newCapacity;
                     event.commit();
                  }
               }

               AdaptivePoolingAllocator.Chunk chunk = this.chunk;
               AdaptivePoolingAllocator allocator = chunk.allocator;
               int readerIndex = this.readerIndex;
               int writerIndex = this.writerIndex;
               int baseOldRootIndex = this.startIndex;
               int oldCapacity = this.length;
               AbstractByteBuf oldRoot = this.rootParent();
               allocator.reallocate(newCapacity, this.maxCapacity(), this);
               oldRoot.getBytes(baseOldRootIndex, this, 0, oldCapacity);
               chunk.releaseSegment(baseOldRootIndex);
               this.readerIndex = readerIndex;
               this.writerIndex = writerIndex;
               return this;
            }
         }
      }

      @Override
      public ByteBufAllocator alloc() {
         return this.rootParent().alloc();
      }

      @Override
      public ByteOrder order() {
         return this.rootParent().order();
      }

      @Override
      public ByteBuf unwrap() {
         return null;
      }

      @Override
      public boolean isDirect() {
         return this.rootParent().isDirect();
      }

      @Override
      public int arrayOffset() {
         return this.idx(this.rootParent().arrayOffset());
      }

      @Override
      public boolean hasMemoryAddress() {
         return this.hasMemoryAddress;
      }

      @Override
      public long memoryAddress() {
         this.ensureAccessible();
         return this._memoryAddress();
      }

      @Override
      long _memoryAddress() {
         AbstractByteBuf root = this.rootParent;
         return root != null ? root._memoryAddress() + this.startIndex : 0L;
      }

      @Override
      public ByteBuffer nioBuffer(int index, int length) {
         this.checkIndex(index, length);
         return this.rootParent().nioBuffer(this.idx(index), length);
      }

      @Override
      public ByteBuffer internalNioBuffer(int index, int length) {
         this.checkIndex(index, length);
         return (ByteBuffer)((Buffer)this.internalNioBuffer()).position(index).limit(index + length);
      }

      private ByteBuffer internalNioBuffer() {
         if (this.tmpNioBuf == null) {
            this.tmpNioBuf = this.rootParent().nioBuffer(this.startIndex, this.maxFastCapacity);
         }

         return (ByteBuffer)((Buffer)this.tmpNioBuf).clear();
      }

      @Override
      public ByteBuffer[] nioBuffers(int index, int length) {
         this.checkIndex(index, length);
         return this.rootParent().nioBuffers(this.idx(index), length);
      }

      @Override
      public boolean hasArray() {
         return this.hasArray;
      }

      @Override
      public byte[] array() {
         this.ensureAccessible();
         return this.rootParent().array();
      }

      @Override
      public ByteBuf copy(int index, int length) {
         this.checkIndex(index, length);
         return this.rootParent().copy(this.idx(index), length);
      }

      @Override
      public int nioBufferCount() {
         return this.rootParent().nioBufferCount();
      }

      @Override
      protected byte _getByte(int index) {
         return this.rootParent()._getByte(this.idx(index));
      }

      @Override
      protected short _getShort(int index) {
         return this.rootParent()._getShort(this.idx(index));
      }

      @Override
      protected short _getShortLE(int index) {
         return this.rootParent()._getShortLE(this.idx(index));
      }

      @Override
      protected int _getUnsignedMedium(int index) {
         return this.rootParent()._getUnsignedMedium(this.idx(index));
      }

      @Override
      protected int _getUnsignedMediumLE(int index) {
         return this.rootParent()._getUnsignedMediumLE(this.idx(index));
      }

      @Override
      protected int _getInt(int index) {
         return this.rootParent()._getInt(this.idx(index));
      }

      @Override
      protected int _getIntLE(int index) {
         return this.rootParent()._getIntLE(this.idx(index));
      }

      @Override
      protected long _getLong(int index) {
         return this.rootParent()._getLong(this.idx(index));
      }

      @Override
      protected long _getLongLE(int index) {
         return this.rootParent()._getLongLE(this.idx(index));
      }

      @Override
      public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
         this.checkIndex(index, length);
         this.rootParent().getBytes(this.idx(index), dst, dstIndex, length);
         return this;
      }

      @Override
      public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
         this.checkIndex(index, length);
         this.rootParent().getBytes(this.idx(index), dst, dstIndex, length);
         return this;
      }

      @Override
      public ByteBuf getBytes(int index, ByteBuffer dst) {
         this.checkIndex(index, dst.remaining());
         this.rootParent().getBytes(this.idx(index), dst);
         return this;
      }

      @Override
      protected void _setByte(int index, int value) {
         this.rootParent()._setByte(this.idx(index), value);
      }

      @Override
      protected void _setShort(int index, int value) {
         this.rootParent()._setShort(this.idx(index), value);
      }

      @Override
      protected void _setShortLE(int index, int value) {
         this.rootParent()._setShortLE(this.idx(index), value);
      }

      @Override
      protected void _setMedium(int index, int value) {
         this.rootParent()._setMedium(this.idx(index), value);
      }

      @Override
      protected void _setMediumLE(int index, int value) {
         this.rootParent()._setMediumLE(this.idx(index), value);
      }

      @Override
      protected void _setInt(int index, int value) {
         this.rootParent()._setInt(this.idx(index), value);
      }

      @Override
      protected void _setIntLE(int index, int value) {
         this.rootParent()._setIntLE(this.idx(index), value);
      }

      @Override
      protected void _setLong(int index, long value) {
         this.rootParent()._setLong(this.idx(index), value);
      }

      @Override
      protected void _setLongLE(int index, long value) {
         this.rootParent().setLongLE(this.idx(index), value);
      }

      @Override
      public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
         this.checkIndex(index, length);
         ByteBuffer tmp = (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(index);
         tmp.put(src, srcIndex, length);
         return this;
      }

      @Override
      public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
         this.checkIndex(index, length);
         ByteBuffer tmp = (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(index);
         tmp.put(src.nioBuffer(srcIndex, length));
         return this;
      }

      @Override
      public ByteBuf setBytes(int index, ByteBuffer src) {
         this.checkIndex(index, src.remaining());
         ByteBuffer tmp = (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(index);
         tmp.put(src);
         return this;
      }

      @Override
      public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
         this.checkIndex(index, length);
         if (length != 0) {
            ByteBufUtil.readBytes(this.alloc(), this.internalNioBuffer().duplicate(), index, length, out);
         }

         return this;
      }

      @Override
      public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
         ByteBuffer buf = this.internalNioBuffer().duplicate();
         ((Buffer)buf).clear().position(index).limit(index + length);
         return out.write(buf);
      }

      @Override
      public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
         ByteBuffer buf = this.internalNioBuffer().duplicate();
         ((Buffer)buf).clear().position(index).limit(index + length);
         return out.write(buf, position);
      }

      @Override
      public int setBytes(int index, InputStream in, int length) throws IOException {
         this.checkIndex(index, length);
         AbstractByteBuf rootParent = this.rootParent();
         if (rootParent.hasArray()) {
            return rootParent.setBytes(this.idx(index), in, length);
         } else {
            byte[] tmp = ByteBufUtil.threadLocalTempArray(length);
            int readBytes = in.read(tmp, 0, length);
            if (readBytes <= 0) {
               return readBytes;
            } else {
               this.setBytes(index, tmp, 0, readBytes);
               return readBytes;
            }
         }
      }

      @Override
      public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
         try {
            return in.read(this.internalNioBuffer(index, length));
         } catch (ClosedChannelException var5) {
            return -1;
         }
      }

      @Override
      public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
         try {
            return in.read(this.internalNioBuffer(index, length), position);
         } catch (ClosedChannelException var7) {
            return -1;
         }
      }

      @Override
      public int setCharSequence(int index, CharSequence sequence, Charset charset) {
         return this.setCharSequence0(index, sequence, charset, false);
      }

      private int setCharSequence0(int index, CharSequence sequence, Charset charset, boolean expand) {
         if (charset.equals(CharsetUtil.UTF_8)) {
            int length = ByteBufUtil.utf8MaxBytes(sequence);
            if (expand) {
               this.ensureWritable0(length);
               this.checkIndex0(index, length);
            } else {
               this.checkIndex(index, length);
            }

            return ByteBufUtil.writeUtf8(this, index, length, sequence, sequence.length());
         } else if (!charset.equals(CharsetUtil.US_ASCII) && !charset.equals(CharsetUtil.ISO_8859_1)) {
            byte[] bytes = sequence.toString().getBytes(charset);
            if (expand) {
               this.ensureWritable0(bytes.length);
            }

            this.setBytes(index, bytes);
            return bytes.length;
         } else {
            int length = sequence.length();
            if (expand) {
               this.ensureWritable0(length);
               this.checkIndex0(index, length);
            } else {
               this.checkIndex(index, length);
            }

            return ByteBufUtil.writeAscii(this, index, sequence, length);
         }
      }

      @Override
      public int writeCharSequence(CharSequence sequence, Charset charset) {
         int written = this.setCharSequence0(this.writerIndex, sequence, charset, true);
         this.writerIndex += written;
         return written;
      }

      @Override
      public int forEachByte(int index, int length, ByteProcessor processor) {
         this.checkIndex(index, length);
         int ret = this.rootParent().forEachByte(this.idx(index), length, processor);
         return this.forEachResult(ret);
      }

      @Override
      public int forEachByteDesc(int index, int length, ByteProcessor processor) {
         this.checkIndex(index, length);
         int ret = this.rootParent().forEachByteDesc(this.idx(index), length, processor);
         return this.forEachResult(ret);
      }

      @Override
      public ByteBuf setZero(int index, int length) {
         this.checkIndex(index, length);
         this.rootParent().setZero(this.idx(index), length);
         return this;
      }

      @Override
      public ByteBuf writeZero(int length) {
         this.ensureWritable(length);
         this.rootParent().setZero(this.idx(this.writerIndex), length);
         this.writerIndex += length;
         return this;
      }

      private int forEachResult(int ret) {
         return ret < this.startIndex ? -1 : ret - this.startIndex;
      }

      @Override
      public boolean isContiguous() {
         return this.rootParent().isContiguous();
      }

      private int idx(int index) {
         return index + this.startIndex;
      }

      @Override
      protected void deallocate() {
         if (PlatformDependent.isJfrEnabled() && FreeBufferEvent.isEventEnabled()) {
            FreeBufferEvent event = new FreeBufferEvent();
            if (event.shouldCommit()) {
               event.fill(this, AdaptiveByteBufAllocator.class);
               event.commit();
            }
         }

         if (this.chunk != null) {
            this.chunk.releaseSegment(this.startIndex);
         }

         this.tmpNioBuf = null;
         this.chunk = null;
         this.rootParent = null;
         if (this.handle instanceof Recycler.EnhancedHandle) {
            Recycler.EnhancedHandle<AdaptivePoolingAllocator.AdaptiveByteBuf> enhancedHandle = (Recycler.EnhancedHandle<AdaptivePoolingAllocator.AdaptiveByteBuf>)this.handle;
            enhancedHandle.unguardedRecycle(this);
         } else {
            this.handle.recycle(this);
         }
      }
   }

   private static class Chunk implements ChunkInfo {
      protected final AbstractByteBuf delegate;
      protected AdaptivePoolingAllocator.Magazine magazine;
      private final AdaptivePoolingAllocator allocator;
      private final AdaptivePoolingAllocator.ChunkReleasePredicate chunkReleasePredicate;
      private final RefCnt refCnt = new RefCnt();
      private final int capacity;
      private final boolean pooled;
      protected int allocatedBytes;

      Chunk() {
         this.delegate = null;
         this.magazine = null;
         this.allocator = null;
         this.chunkReleasePredicate = null;
         this.capacity = 0;
         this.pooled = false;
      }

      Chunk(
         AbstractByteBuf delegate,
         AdaptivePoolingAllocator.Magazine magazine,
         boolean pooled,
         AdaptivePoolingAllocator.ChunkReleasePredicate chunkReleasePredicate
      ) {
         this.delegate = delegate;
         this.pooled = pooled;
         this.capacity = delegate.capacity();
         this.attachToMagazine(magazine);
         this.allocator = magazine.group.allocator;
         this.chunkReleasePredicate = chunkReleasePredicate;
         if (PlatformDependent.isJfrEnabled() && AllocateChunkEvent.isEventEnabled()) {
            AllocateChunkEvent event = new AllocateChunkEvent();
            if (event.shouldCommit()) {
               event.fill(this, AdaptiveByteBufAllocator.class);
               event.pooled = pooled;
               event.threadLocal = magazine.allocationLock == null;
               event.commit();
            }
         }
      }

      AdaptivePoolingAllocator.Magazine currentMagazine() {
         return this.magazine;
      }

      void detachFromMagazine() {
         if (this.magazine != null) {
            this.magazine = null;
         }
      }

      void attachToMagazine(AdaptivePoolingAllocator.Magazine magazine) {
         assert this.magazine == null;

         this.magazine = magazine;
      }

      boolean releaseFromMagazine() {
         return this.release();
      }

      boolean releaseSegment(int ignoredSegmentId) {
         return this.release();
      }

      private void retain() {
         RefCnt.retain(this.refCnt);
      }

      protected boolean release() {
         boolean deallocate = RefCnt.release(this.refCnt);
         if (deallocate) {
            this.deallocate();
         }

         return deallocate;
      }

      protected void deallocate() {
         AdaptivePoolingAllocator.Magazine mag = this.magazine;
         int chunkSize = this.delegate.capacity();
         if (this.pooled && !this.chunkReleasePredicate.shouldReleaseChunk(chunkSize) && mag != null) {
            RefCnt.resetRefCnt(this.refCnt);
            this.delegate.setIndex(0, 0);
            this.allocatedBytes = 0;
            if (!mag.trySetNextInLine(this)) {
               this.detachFromMagazine();
               if (!mag.offerToQueue(this)) {
                  boolean released = RefCnt.release(this.refCnt);
                  this.onRelease();
                  this.allocator.chunkRegistry.remove(this);
                  this.delegate.release();

                  assert released;
               } else {
                  this.onReturn(false);
               }
            } else {
               this.onReturn(true);
            }
         } else {
            this.detachFromMagazine();
            this.onRelease();
            this.allocator.chunkRegistry.remove(this);
            this.delegate.release();
         }
      }

      private void onReturn(boolean returnedToMagazine) {
         if (PlatformDependent.isJfrEnabled() && ReturnChunkEvent.isEventEnabled()) {
            ReturnChunkEvent event = new ReturnChunkEvent();
            if (event.shouldCommit()) {
               event.fill(this, AdaptiveByteBufAllocator.class);
               event.returnedToMagazine = returnedToMagazine;
               event.commit();
            }
         }
      }

      private void onRelease() {
         if (PlatformDependent.isJfrEnabled() && FreeChunkEvent.isEventEnabled()) {
            FreeChunkEvent event = new FreeChunkEvent();
            if (event.shouldCommit()) {
               event.fill(this, AdaptiveByteBufAllocator.class);
               event.pooled = this.pooled;
               event.commit();
            }
         }
      }

      public void readInitInto(AdaptivePoolingAllocator.AdaptiveByteBuf buf, int size, int startingCapacity, int maxCapacity) {
         int startIndex = this.allocatedBytes;
         this.allocatedBytes = startIndex + startingCapacity;
         AdaptivePoolingAllocator.Chunk chunk = this;
         this.retain();

         try {
            buf.init(this.delegate, chunk, 0, 0, startIndex, size, startingCapacity, maxCapacity);
            chunk = null;
         } finally {
            if (chunk != null) {
               this.allocatedBytes = startIndex;
               chunk.release();
            }
         }
      }

      public int remainingCapacity() {
         return this.capacity - this.allocatedBytes;
      }

      @Override
      public int capacity() {
         return this.capacity;
      }

      @Override
      public boolean isDirect() {
         return this.delegate.isDirect();
      }

      @Override
      public long memoryAddress() {
         return this.delegate._memoryAddress();
      }
   }

   interface ChunkAllocator {
      AbstractByteBuf allocate(int var1, int var2);
   }

   private interface ChunkController {
      int computeBufferCapacity(int var1, int var2, boolean var3);

      void initializeSharedStateIn(AdaptivePoolingAllocator.ChunkController var1);

      AdaptivePoolingAllocator.Chunk newChunkAllocation(int var1, AdaptivePoolingAllocator.Magazine var2);
   }

   private interface ChunkControllerFactory {
      AdaptivePoolingAllocator.ChunkController create(AdaptivePoolingAllocator.MagazineGroup var1);
   }

   private static final class ChunkRegistry {
      private final LongAdder totalCapacity = new LongAdder();

      private ChunkRegistry() {
      }

      public long totalCapacity() {
         return this.totalCapacity.sum();
      }

      public void add(AdaptivePoolingAllocator.Chunk chunk) {
         this.totalCapacity.add(chunk.capacity());
      }

      public void remove(AdaptivePoolingAllocator.Chunk chunk) {
         this.totalCapacity.add(-chunk.capacity());
      }
   }

   private interface ChunkReleasePredicate {
      boolean shouldReleaseChunk(int var1);
   }

   private static final class HistogramChunkController implements AdaptivePoolingAllocator.ChunkController, AdaptivePoolingAllocator.ChunkReleasePredicate {
      private static final int MIN_DATUM_TARGET = 1024;
      private static final int MAX_DATUM_TARGET = 65534;
      private static final int INIT_DATUM_TARGET = 9;
      private static final int HISTO_BUCKET_COUNT = 16;
      private static final int[] HISTO_BUCKETS = new int[]{
         16384, 24576, 32768, 49152, 65536, 98304, 131072, 196608, 262144, 393216, 524288, 786432, 1048576, 1835008, 2097152, 3145728
      };
      private final AdaptivePoolingAllocator.MagazineGroup group;
      private final boolean shareable;
      private final short[][] histos = new short[][]{new short[16], new short[16], new short[16], new short[16]};
      private final AdaptivePoolingAllocator.ChunkRegistry chunkRegistry;
      private short[] histo = this.histos[0];
      private final int[] sums = new int[16];
      private int histoIndex;
      private int datumCount;
      private int datumTarget = 9;
      private boolean hasHadRotation;
      private volatile int sharedPrefChunkSize = 131072;
      private volatile int localPrefChunkSize = 131072;
      private volatile int localUpperBufSize;

      private HistogramChunkController(AdaptivePoolingAllocator.MagazineGroup group, boolean shareable) {
         this.group = group;
         this.shareable = shareable;
         this.chunkRegistry = group.allocator.chunkRegistry;
      }

      @Override
      public int computeBufferCapacity(int requestedSize, int maxCapacity, boolean isReallocation) {
         if (!isReallocation) {
            this.recordAllocationSize(requestedSize);
         }

         int startCapLimits;
         if (requestedSize <= 32768) {
            startCapLimits = 65536;
         } else {
            startCapLimits = requestedSize * 2;
         }

         int startingCapacity = Math.min(startCapLimits, this.localUpperBufSize);
         return Math.max(requestedSize, Math.min(maxCapacity, startingCapacity));
      }

      private void recordAllocationSize(int bufferSizeToRecord) {
         if (bufferSizeToRecord != 0) {
            int bucket = sizeToBucket(bufferSizeToRecord);
            this.histo[bucket]++;
            if (this.datumCount++ == this.datumTarget) {
               this.rotateHistograms();
            }
         }
      }

      static int sizeToBucket(int size) {
         int index = binarySearchInsertionPoint(Arrays.binarySearch(HISTO_BUCKETS, size));
         return index >= HISTO_BUCKETS.length ? HISTO_BUCKETS.length - 1 : index;
      }

      private static int binarySearchInsertionPoint(int index) {
         if (index < 0) {
            index = -(index + 1);
         }

         return index;
      }

      static int bucketToSize(int sizeBucket) {
         return HISTO_BUCKETS[sizeBucket];
      }

      private void rotateHistograms() {
         short[][] hs = this.histos;

         for (int i = 0; i < 16; i++) {
            this.sums[i] = (hs[0][i] & '\uffff') + (hs[1][i] & '\uffff') + (hs[2][i] & '\uffff') + (hs[3][i] & '\uffff');
         }

         int sum = 0;

         for (int count : this.sums) {
            sum += count;
         }

         int targetPercentile = (int)(sum * 0.99);

         int sizeBucket;
         for (sizeBucket = 0; sizeBucket < this.sums.length && this.sums[sizeBucket] <= targetPercentile; sizeBucket++) {
            targetPercentile -= this.sums[sizeBucket];
         }

         this.hasHadRotation = true;
         int percentileSize = bucketToSize(sizeBucket);
         int prefChunkSize = Math.max(percentileSize * 8, 131072);
         this.localUpperBufSize = percentileSize;
         this.localPrefChunkSize = prefChunkSize;
         if (this.shareable) {
            for (AdaptivePoolingAllocator.Magazine mag : this.group.magazines) {
               AdaptivePoolingAllocator.HistogramChunkController statistics = (AdaptivePoolingAllocator.HistogramChunkController)mag.chunkController;
               prefChunkSize = Math.max(prefChunkSize, statistics.localPrefChunkSize);
            }
         }

         if (this.sharedPrefChunkSize != prefChunkSize) {
            this.datumTarget = Math.max(this.datumTarget >> 1, 1024);
            this.sharedPrefChunkSize = prefChunkSize;
         } else {
            this.datumTarget = Math.min(this.datumTarget << 1, 65534);
         }

         this.histoIndex = this.histoIndex + 1 & 3;
         this.histo = this.histos[this.histoIndex];
         this.datumCount = 0;
         Arrays.fill(this.histo, (short)0);
      }

      int preferredChunkSize() {
         return this.sharedPrefChunkSize;
      }

      @Override
      public void initializeSharedStateIn(AdaptivePoolingAllocator.ChunkController chunkController) {
         AdaptivePoolingAllocator.HistogramChunkController statistics = (AdaptivePoolingAllocator.HistogramChunkController)chunkController;
         int sharedPrefChunkSize = this.sharedPrefChunkSize;
         statistics.localPrefChunkSize = sharedPrefChunkSize;
         statistics.sharedPrefChunkSize = sharedPrefChunkSize;
      }

      @Override
      public AdaptivePoolingAllocator.Chunk newChunkAllocation(int promptingSize, AdaptivePoolingAllocator.Magazine magazine) {
         int size = Math.max(promptingSize * 8, this.preferredChunkSize());
         int minChunks = size / 131072;
         if (131072 * minChunks < size) {
            size = 131072 * (1 + minChunks);
         }

         size = Math.min(size, AdaptivePoolingAllocator.MAX_CHUNK_SIZE);
         if (!this.hasHadRotation && this.sharedPrefChunkSize == 131072) {
            this.sharedPrefChunkSize = size;
         }

         AdaptivePoolingAllocator.ChunkAllocator chunkAllocator = this.group.chunkAllocator;
         AdaptivePoolingAllocator.Chunk chunk = new AdaptivePoolingAllocator.Chunk(chunkAllocator.allocate(size, size), magazine, true, this);
         this.chunkRegistry.add(chunk);
         return chunk;
      }

      @Override
      public boolean shouldReleaseChunk(int chunkSize) {
         int preferredSize = this.preferredChunkSize();
         int givenChunks = chunkSize / 131072;
         int preferredChunks = preferredSize / 131072;
         int deviation = Math.abs(givenChunks - preferredChunks);
         return deviation != 0 && ThreadLocalRandom.current().nextDouble() * 20.0 < deviation;
      }
   }

   private static final class HistogramChunkControllerFactory implements AdaptivePoolingAllocator.ChunkControllerFactory {
      private final boolean shareable;

      private HistogramChunkControllerFactory(boolean shareable) {
         this.shareable = shareable;
      }

      @Override
      public AdaptivePoolingAllocator.ChunkController create(AdaptivePoolingAllocator.MagazineGroup group) {
         return new AdaptivePoolingAllocator.HistogramChunkController(group, this.shareable);
      }
   }

   private static final class Magazine {
      private static final AtomicReferenceFieldUpdater<AdaptivePoolingAllocator.Magazine, AdaptivePoolingAllocator.Chunk> NEXT_IN_LINE = AtomicReferenceFieldUpdater.newUpdater(
         AdaptivePoolingAllocator.Magazine.class, AdaptivePoolingAllocator.Chunk.class, "nextInLine"
      );
      private static final AdaptivePoolingAllocator.Chunk MAGAZINE_FREED = new AdaptivePoolingAllocator.Chunk();
      private static final Recycler<AdaptivePoolingAllocator.AdaptiveByteBuf> EVENT_LOOP_LOCAL_BUFFER_POOL = new Recycler<AdaptivePoolingAllocator.AdaptiveByteBuf>() {
         protected AdaptivePoolingAllocator.AdaptiveByteBuf newObject(Recycler.Handle<AdaptivePoolingAllocator.AdaptiveByteBuf> handle) {
            return new AdaptivePoolingAllocator.AdaptiveByteBuf(handle);
         }
      };
      private AdaptivePoolingAllocator.Chunk current;
      private volatile AdaptivePoolingAllocator.Chunk nextInLine;
      private final AdaptivePoolingAllocator.MagazineGroup group;
      private final AdaptivePoolingAllocator.ChunkController chunkController;
      private final StampedLock allocationLock;
      private final Queue<AdaptivePoolingAllocator.AdaptiveByteBuf> bufferQueue;
      private final ObjectPool.Handle<AdaptivePoolingAllocator.AdaptiveByteBuf> handle;
      private final Queue<AdaptivePoolingAllocator.Chunk> sharedChunkQueue;

      Magazine(
         AdaptivePoolingAllocator.MagazineGroup group,
         boolean shareable,
         Queue<AdaptivePoolingAllocator.Chunk> sharedChunkQueue,
         AdaptivePoolingAllocator.ChunkController chunkController
      ) {
         this.group = group;
         this.chunkController = chunkController;
         if (shareable) {
            this.allocationLock = new StampedLock();
            this.bufferQueue = PlatformDependent.newFixedMpmcQueue(AdaptivePoolingAllocator.MAGAZINE_BUFFER_QUEUE_CAPACITY);
            this.handle = new ObjectPool.Handle<AdaptivePoolingAllocator.AdaptiveByteBuf>() {
               public void recycle(AdaptivePoolingAllocator.AdaptiveByteBuf self) {
                  Magazine.this.bufferQueue.offer(self);
               }
            };
         } else {
            this.allocationLock = null;
            this.bufferQueue = null;
            this.handle = null;
         }

         this.sharedChunkQueue = sharedChunkQueue;
      }

      public boolean tryAllocate(int size, int maxCapacity, AdaptivePoolingAllocator.AdaptiveByteBuf buf, boolean reallocate) {
         if (this.allocationLock == null) {
            return this.allocate(size, maxCapacity, buf, reallocate);
         } else {
            long writeLock = this.allocationLock.tryWriteLock();
            if (writeLock != 0L) {
               boolean var7;
               try {
                  var7 = this.allocate(size, maxCapacity, buf, reallocate);
               } finally {
                  this.allocationLock.unlockWrite(writeLock);
               }

               return var7;
            } else {
               return this.allocateWithoutLock(size, maxCapacity, buf);
            }
         }
      }

      private boolean allocateWithoutLock(int size, int maxCapacity, AdaptivePoolingAllocator.AdaptiveByteBuf buf) {
         AdaptivePoolingAllocator.Chunk curr = NEXT_IN_LINE.getAndSet(this, null);
         if (curr == MAGAZINE_FREED) {
            this.restoreMagazineFreed();
            return false;
         } else {
            if (curr == null) {
               curr = this.sharedChunkQueue.poll();
               if (curr == null) {
                  return false;
               }

               curr.attachToMagazine(this);
            }

            boolean allocated = false;
            int remainingCapacity = curr.remainingCapacity();
            int startingCapacity = this.chunkController.computeBufferCapacity(size, maxCapacity, true);
            if (remainingCapacity >= size) {
               curr.readInitInto(buf, size, Math.min(remainingCapacity, startingCapacity), maxCapacity);
               allocated = true;
            }

            try {
               if (remainingCapacity >= 256) {
                  this.transferToNextInLineOrRelease(curr);
                  curr = null;
               }
            } finally {
               if (curr != null) {
                  curr.releaseFromMagazine();
               }
            }

            return allocated;
         }
      }

      private boolean allocate(int size, int maxCapacity, AdaptivePoolingAllocator.AdaptiveByteBuf buf, boolean reallocate) {
         int startingCapacity = this.chunkController.computeBufferCapacity(size, maxCapacity, reallocate);
         AdaptivePoolingAllocator.Chunk curr = this.current;
         if (curr != null) {
            int remainingCapacity = curr.remainingCapacity();
            if (remainingCapacity > startingCapacity) {
               curr.readInitInto(buf, size, startingCapacity, maxCapacity);
               return true;
            }

            this.current = null;
            if (remainingCapacity >= size) {
               boolean var29;
               try {
                  curr.readInitInto(buf, size, remainingCapacity, maxCapacity);
                  var29 = true;
               } finally {
                  curr.releaseFromMagazine();
               }

               return var29;
            }

            if (remainingCapacity < 256) {
               curr.releaseFromMagazine();
            } else {
               this.transferToNextInLineOrRelease(curr);
            }
         }

         assert this.current == null;

         curr = NEXT_IN_LINE.getAndSet(this, null);
         if (curr != null) {
            if (curr == MAGAZINE_FREED) {
               this.restoreMagazineFreed();
               return false;
            }

            int remainingCapacityx = curr.remainingCapacity();
            if (remainingCapacityx > startingCapacity) {
               curr.readInitInto(buf, size, startingCapacity, maxCapacity);
               this.current = curr;
               return true;
            }

            if (remainingCapacityx >= size) {
               boolean var8;
               try {
                  curr.readInitInto(buf, size, remainingCapacityx, maxCapacity);
                  var8 = true;
               } finally {
                  curr.releaseFromMagazine();
               }

               return var8;
            }

            curr.releaseFromMagazine();
         }

         curr = this.sharedChunkQueue.poll();
         if (curr == null) {
            curr = this.chunkController.newChunkAllocation(size, this);
         } else {
            curr.attachToMagazine(this);
            int remainingCapacityxx = curr.remainingCapacity();
            if (remainingCapacityxx == 0 || remainingCapacityxx < size) {
               if (remainingCapacityxx < 256) {
                  curr.releaseFromMagazine();
               } else {
                  this.transferToNextInLineOrRelease(curr);
               }

               curr = this.chunkController.newChunkAllocation(size, this);
            }
         }

         this.current = curr;

         try {
            int remainingCapacityxxx = curr.remainingCapacity();

            assert remainingCapacityxxx >= size;

            if (remainingCapacityxxx > startingCapacity) {
               curr.readInitInto(buf, size, startingCapacity, maxCapacity);
               curr = null;
            } else {
               curr.readInitInto(buf, size, remainingCapacityxxx, maxCapacity);
            }
         } finally {
            if (curr != null) {
               curr.releaseFromMagazine();
               this.current = null;
            }
         }

         return true;
      }

      private void restoreMagazineFreed() {
         AdaptivePoolingAllocator.Chunk next = NEXT_IN_LINE.getAndSet(this, MAGAZINE_FREED);
         if (next != null && next != MAGAZINE_FREED) {
            next.releaseFromMagazine();
         }
      }

      private void transferToNextInLineOrRelease(AdaptivePoolingAllocator.Chunk chunk) {
         if (!NEXT_IN_LINE.compareAndSet(this, null, chunk)) {
            AdaptivePoolingAllocator.Chunk nextChunk = NEXT_IN_LINE.get(this);
            if (nextChunk != null
               && nextChunk != MAGAZINE_FREED
               && chunk.remainingCapacity() > nextChunk.remainingCapacity()
               && NEXT_IN_LINE.compareAndSet(this, nextChunk, chunk)) {
               nextChunk.releaseFromMagazine();
            } else {
               chunk.releaseFromMagazine();
            }
         }
      }

      boolean trySetNextInLine(AdaptivePoolingAllocator.Chunk chunk) {
         return NEXT_IN_LINE.compareAndSet(this, null, chunk);
      }

      void free() {
         this.restoreMagazineFreed();
         long stamp = this.allocationLock != null ? this.allocationLock.writeLock() : 0L;

         try {
            if (this.current != null) {
               this.current.releaseFromMagazine();
               this.current = null;
            }
         } finally {
            if (this.allocationLock != null) {
               this.allocationLock.unlockWrite(stamp);
            }
         }
      }

      public AdaptivePoolingAllocator.AdaptiveByteBuf newBuffer() {
         AdaptivePoolingAllocator.AdaptiveByteBuf buf;
         if (this.handle == null) {
            buf = EVENT_LOOP_LOCAL_BUFFER_POOL.get();
         } else {
            buf = this.bufferQueue.poll();
            if (buf == null) {
               buf = new AdaptivePoolingAllocator.AdaptiveByteBuf(this.handle);
            }
         }

         buf.resetRefCnt();
         buf.discardMarks();
         return buf;
      }

      boolean offerToQueue(AdaptivePoolingAllocator.Chunk chunk) {
         return this.group.offerToQueue(chunk);
      }

      public void initializeSharedStateIn(AdaptivePoolingAllocator.Magazine other) {
         this.chunkController.initializeSharedStateIn(other.chunkController);
      }
   }

   private static final class MagazineGroup {
      private final AdaptivePoolingAllocator allocator;
      private final AdaptivePoolingAllocator.ChunkAllocator chunkAllocator;
      private final AdaptivePoolingAllocator.ChunkControllerFactory chunkControllerFactory;
      private final Queue<AdaptivePoolingAllocator.Chunk> chunkReuseQueue;
      private final StampedLock magazineExpandLock;
      private final AdaptivePoolingAllocator.Magazine threadLocalMagazine;
      private volatile AdaptivePoolingAllocator.Magazine[] magazines;
      private volatile boolean freed;

      MagazineGroup(
         AdaptivePoolingAllocator allocator,
         AdaptivePoolingAllocator.ChunkAllocator chunkAllocator,
         AdaptivePoolingAllocator.ChunkControllerFactory chunkControllerFactory,
         boolean isThreadLocal
      ) {
         this.allocator = allocator;
         this.chunkAllocator = chunkAllocator;
         this.chunkControllerFactory = chunkControllerFactory;
         this.chunkReuseQueue = AdaptivePoolingAllocator.createSharedChunkQueue();
         if (isThreadLocal) {
            this.magazineExpandLock = null;
            this.threadLocalMagazine = new AdaptivePoolingAllocator.Magazine(this, false, this.chunkReuseQueue, chunkControllerFactory.create(this));
         } else {
            this.magazineExpandLock = new StampedLock();
            this.threadLocalMagazine = null;
            AdaptivePoolingAllocator.Magazine[] mags = new AdaptivePoolingAllocator.Magazine[1];

            for (int i = 0; i < mags.length; i++) {
               mags[i] = new AdaptivePoolingAllocator.Magazine(this, true, this.chunkReuseQueue, chunkControllerFactory.create(this));
            }

            this.magazines = mags;
         }
      }

      public AdaptivePoolingAllocator.AdaptiveByteBuf allocate(int size, int maxCapacity, Thread currentThread, AdaptivePoolingAllocator.AdaptiveByteBuf buf) {
         boolean reallocate = buf != null;
         AdaptivePoolingAllocator.Magazine tlMag = this.threadLocalMagazine;
         if (tlMag != null) {
            if (buf == null) {
               buf = tlMag.newBuffer();
            }

            boolean allocated = tlMag.tryAllocate(size, maxCapacity, buf, reallocate);

            assert allocated : "Allocation of threadLocalMagazine must always succeed";

            return buf;
         } else {
            long threadId = currentThread.getId();
            int expansions = 0;

            AdaptivePoolingAllocator.Magazine[] mags;
            do {
               mags = this.magazines;
               int mask = mags.length - 1;
               int index = (int)(threadId & mask);
               int i = 0;

               for (int m = mags.length << 1; i < m; i++) {
                  AdaptivePoolingAllocator.Magazine mag = mags[index + i & mask];
                  if (buf == null) {
                     buf = mag.newBuffer();
                  }

                  if (mag.tryAllocate(size, maxCapacity, buf, reallocate)) {
                     return buf;
                  }
               }
            } while (++expansions <= 3 && this.tryExpandMagazines(mags.length));

            if (!reallocate && buf != null) {
               buf.release();
            }

            return null;
         }
      }

      private boolean tryExpandMagazines(int currentLength) {
         if (currentLength >= AdaptivePoolingAllocator.MAX_STRIPES) {
            return true;
         } else {
            long writeLock = this.magazineExpandLock.tryWriteLock();
            if (writeLock != 0L) {
               AdaptivePoolingAllocator.Magazine[] mags;
               try {
                  mags = this.magazines;
                  if (mags.length >= AdaptivePoolingAllocator.MAX_STRIPES || mags.length > currentLength || this.freed) {
                     return true;
                  }

                  AdaptivePoolingAllocator.Magazine firstMagazine = mags[0];
                  AdaptivePoolingAllocator.Magazine[] expanded = new AdaptivePoolingAllocator.Magazine[mags.length * 2];
                  int i = 0;

                  for (int l = expanded.length; i < l; i++) {
                     AdaptivePoolingAllocator.Magazine m = new AdaptivePoolingAllocator.Magazine(
                        this, true, this.chunkReuseQueue, this.chunkControllerFactory.create(this)
                     );
                     firstMagazine.initializeSharedStateIn(m);
                     expanded[i] = m;
                  }

                  this.magazines = expanded;
               } finally {
                  this.magazineExpandLock.unlockWrite(writeLock);
               }

               for (AdaptivePoolingAllocator.Magazine magazine : mags) {
                  magazine.free();
               }

               return true;
            } else {
               return true;
            }
         }
      }

      boolean offerToQueue(AdaptivePoolingAllocator.Chunk buffer) {
         if (this.freed) {
            return false;
         } else {
            boolean isAdded = this.chunkReuseQueue.offer(buffer);
            if (this.freed && isAdded) {
               this.freeChunkReuseQueue();
            }

            return isAdded;
         }
      }

      private void free() {
         this.freed = true;
         if (this.threadLocalMagazine != null) {
            this.threadLocalMagazine.free();
         } else {
            long stamp = this.magazineExpandLock.writeLock();

            try {
               AdaptivePoolingAllocator.Magazine[] mags = this.magazines;

               for (AdaptivePoolingAllocator.Magazine magazine : mags) {
                  magazine.free();
               }
            } finally {
               this.magazineExpandLock.unlockWrite(stamp);
            }
         }

         this.freeChunkReuseQueue();
      }

      private void freeChunkReuseQueue() {
         while (true) {
            AdaptivePoolingAllocator.Chunk chunk = this.chunkReuseQueue.poll();
            if (chunk == null) {
               return;
            }

            chunk.release();
         }
      }
   }

   private static final class SizeClassChunkController implements AdaptivePoolingAllocator.ChunkController {
      private final AdaptivePoolingAllocator.ChunkAllocator chunkAllocator;
      private final int segmentSize;
      private final int chunkSize;
      private final AdaptivePoolingAllocator.ChunkRegistry chunkRegistry;
      private final int[] segmentOffsets;

      private SizeClassChunkController(AdaptivePoolingAllocator.MagazineGroup group, int segmentSize, int chunkSize, int[] segmentOffsets) {
         this.chunkAllocator = group.chunkAllocator;
         this.segmentSize = segmentSize;
         this.chunkSize = chunkSize;
         this.chunkRegistry = group.allocator.chunkRegistry;
         this.segmentOffsets = segmentOffsets;
      }

      @Override
      public int computeBufferCapacity(int requestedSize, int maxCapacity, boolean isReallocation) {
         return Math.min(this.segmentSize, maxCapacity);
      }

      @Override
      public void initializeSharedStateIn(AdaptivePoolingAllocator.ChunkController chunkController) {
      }

      @Override
      public AdaptivePoolingAllocator.Chunk newChunkAllocation(int promptingSize, AdaptivePoolingAllocator.Magazine magazine) {
         AbstractByteBuf chunkBuffer = this.chunkAllocator.allocate(this.chunkSize, this.chunkSize);

         assert chunkBuffer.capacity() == this.chunkSize;

         AdaptivePoolingAllocator.SizeClassedChunk chunk = new AdaptivePoolingAllocator.SizeClassedChunk(
            chunkBuffer, magazine, true, this.segmentSize, this.segmentOffsets, size -> false
         );
         this.chunkRegistry.add(chunk);
         return chunk;
      }
   }

   private static final class SizeClassChunkControllerFactory implements AdaptivePoolingAllocator.ChunkControllerFactory {
      private static final int MIN_SEGMENTS_PER_CHUNK = 32;
      private final int segmentSize;
      private final int chunkSize;
      private final int[] segmentOffsets;

      private SizeClassChunkControllerFactory(int segmentSize) {
         this.segmentSize = ObjectUtil.checkPositive(segmentSize, "segmentSize");
         this.chunkSize = Math.max(131072, segmentSize * 32);
         int segmentsCount = this.chunkSize / segmentSize;
         this.segmentOffsets = new int[segmentsCount];

         for (int i = 0; i < segmentsCount; i++) {
            this.segmentOffsets[i] = i * segmentSize;
         }
      }

      @Override
      public AdaptivePoolingAllocator.ChunkController create(AdaptivePoolingAllocator.MagazineGroup group) {
         return new AdaptivePoolingAllocator.SizeClassChunkController(group, this.segmentSize, this.chunkSize, this.segmentOffsets);
      }
   }

   private static final class SizeClassedChunk extends AdaptivePoolingAllocator.Chunk {
      private static final int FREE_LIST_EMPTY = -1;
      private final int segmentSize;
      private final MpscIntQueue freeList;

      SizeClassedChunk(
         AbstractByteBuf delegate,
         AdaptivePoolingAllocator.Magazine magazine,
         boolean pooled,
         int segmentSize,
         final int[] segmentOffsets,
         AdaptivePoolingAllocator.ChunkReleasePredicate shouldReleaseChunk
      ) {
         super(delegate, magazine, pooled, shouldReleaseChunk);
         this.segmentSize = segmentSize;
         int segmentCount = segmentOffsets.length;

         assert delegate.capacity() / segmentSize == segmentCount;

         assert segmentCount > 0 : "Chunk must have a positive number of segments";

         this.freeList = MpscIntQueue.create(segmentCount, -1);
         this.freeList.fill(segmentCount, new IntSupplier() {
            int counter;

            @Override
            public int getAsInt() {
               return segmentOffsets[this.counter++];
            }
         });
      }

      @Override
      public void readInitInto(AdaptivePoolingAllocator.AdaptiveByteBuf buf, int size, int startingCapacity, int maxCapacity) {
         int startIndex = this.freeList.poll();
         if (startIndex == -1) {
            throw new IllegalStateException("Free list is empty");
         } else {
            this.allocatedBytes = this.allocatedBytes + this.segmentSize;
            AdaptivePoolingAllocator.Chunk chunk = this;
            super.retain();

            try {
               buf.init(this.delegate, chunk, 0, 0, startIndex, size, startingCapacity, maxCapacity);
               chunk = null;
            } finally {
               if (chunk != null) {
                  this.allocatedBytes = this.allocatedBytes - this.segmentSize;
                  chunk.releaseSegment(startIndex);
               }
            }
         }
      }

      @Override
      public int remainingCapacity() {
         int remainingCapacity = super.remainingCapacity();
         if (remainingCapacity > this.segmentSize) {
            return remainingCapacity;
         } else {
            int updatedRemainingCapacity = this.freeList.size() * this.segmentSize;
            if (updatedRemainingCapacity == remainingCapacity) {
               return remainingCapacity;
            } else {
               this.allocatedBytes = this.capacity() - updatedRemainingCapacity;
               return updatedRemainingCapacity;
            }
         }
      }

      @Override
      boolean releaseFromMagazine() {
         AdaptivePoolingAllocator.Magazine mag = this.magazine;
         this.detachFromMagazine();
         return !mag.offerToQueue(this) ? super.releaseFromMagazine() : false;
      }

      @Override
      boolean releaseSegment(int startIndex) {
         boolean released = this.release();
         boolean segmentReturned = this.freeList.offer(startIndex);

         assert segmentReturned : "Unable to return segment " + startIndex + " to free list";

         return released;
      }
   }
}
