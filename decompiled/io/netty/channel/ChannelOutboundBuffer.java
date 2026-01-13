package io.netty.channel;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {
   static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.outboundBufferEntrySizeOverhead", 96);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
   private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>() {
      protected ByteBuffer[] initialValue() throws Exception {
         return new ByteBuffer[1024];
      }
   };
   private final Channel channel;
   private ChannelOutboundBuffer.Entry flushedEntry;
   private ChannelOutboundBuffer.Entry unflushedEntry;
   private ChannelOutboundBuffer.Entry tailEntry;
   private int flushed;
   private int nioBufferCount;
   private long nioBufferSize;
   private boolean inFail;
   private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(
      ChannelOutboundBuffer.class, "totalPendingSize"
   );
   private volatile long totalPendingSize;
   private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      ChannelOutboundBuffer.class, "unwritable"
   );
   private volatile int unwritable;
   private volatile Runnable fireChannelWritabilityChangedTask;

   ChannelOutboundBuffer(AbstractChannel channel) {
      this.channel = channel;
   }

   public void addMessage(Object msg, int size, ChannelPromise promise) {
      ChannelOutboundBuffer.Entry entry = ChannelOutboundBuffer.Entry.newInstance(msg, size, total(msg), promise);
      if (this.tailEntry == null) {
         this.flushedEntry = null;
      } else {
         ChannelOutboundBuffer.Entry tail = this.tailEntry;
         tail.next = entry;
      }

      this.tailEntry = entry;
      if (this.unflushedEntry == null) {
         this.unflushedEntry = entry;
      }

      if (msg instanceof AbstractReferenceCountedByteBuf) {
         ((AbstractReferenceCountedByteBuf)msg).touch();
      } else {
         ReferenceCountUtil.touch(msg);
      }

      this.incrementPendingOutboundBytes(entry.pendingSize, false);
   }

   public void addFlush() {
      ChannelOutboundBuffer.Entry entry = this.unflushedEntry;
      if (entry != null) {
         if (this.flushedEntry == null) {
            this.flushedEntry = entry;
         }

         do {
            this.flushed++;
            if (!entry.promise.setUncancellable()) {
               int pending = entry.cancel();
               this.decrementPendingOutboundBytes(pending, false, true);
            }

            entry = entry.next;
         } while (entry != null);

         this.unflushedEntry = null;
      }
   }

   void incrementPendingOutboundBytes(long size) {
      this.incrementPendingOutboundBytes(size, true);
   }

   private void incrementPendingOutboundBytes(long size, boolean invokeLater) {
      if (size != 0L) {
         long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
         if (newWriteBufferSize > this.channel.config().getWriteBufferHighWaterMark()) {
            this.setUnwritable(invokeLater);
         }
      }
   }

   void decrementPendingOutboundBytes(long size) {
      this.decrementPendingOutboundBytes(size, true, true);
   }

   private void decrementPendingOutboundBytes(long size, boolean invokeLater, boolean notifyWritability) {
      if (size != 0L) {
         long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
         if (notifyWritability && newWriteBufferSize < this.channel.config().getWriteBufferLowWaterMark()) {
            this.setWritable(invokeLater);
         }
      }
   }

   private static long total(Object msg) {
      if (msg instanceof ByteBuf) {
         return ((ByteBuf)msg).readableBytes();
      } else if (msg instanceof FileRegion) {
         return ((FileRegion)msg).count();
      } else {
         return msg instanceof ByteBufHolder ? ((ByteBufHolder)msg).content().readableBytes() : -1L;
      }
   }

   public Object current() {
      ChannelOutboundBuffer.Entry entry = this.flushedEntry;
      return entry == null ? null : entry.msg;
   }

   public long currentProgress() {
      ChannelOutboundBuffer.Entry entry = this.flushedEntry;
      return entry == null ? 0L : entry.progress;
   }

   public void progress(long amount) {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;

      assert e != null;

      ChannelPromise p = e.promise;
      long progress = e.progress + amount;
      e.progress = progress;

      assert p != null;

      Class<?> promiseClass = p.getClass();
      if (promiseClass != VoidChannelPromise.class && promiseClass != DefaultChannelPromise.class) {
         if (p instanceof DefaultChannelProgressivePromise) {
            ((DefaultChannelProgressivePromise)p).tryProgress(progress, e.total);
         } else if (p instanceof ChannelProgressivePromise) {
            ((ChannelProgressivePromise)p).tryProgress(progress, e.total);
         }
      }
   }

   public boolean remove() {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;
      if (e == null) {
         this.clearNioBuffers();
         return false;
      } else {
         Object msg = e.msg;
         ChannelPromise promise = e.promise;
         int size = e.pendingSize;
         this.removeEntry(e);
         if (!e.cancelled) {
            if (msg instanceof AbstractReferenceCountedByteBuf) {
               try {
                  ((AbstractReferenceCountedByteBuf)msg).release();
               } catch (Throwable var6) {
                  logger.warn("Failed to release a ByteBuf: {}", msg, var6);
               }
            } else {
               ReferenceCountUtil.safeRelease(msg);
            }

            safeSuccess(promise);
            this.decrementPendingOutboundBytes(size, false, true);
         }

         e.unguardedRecycle();
         return true;
      }
   }

   public boolean remove(Throwable cause) {
      return this.remove0(cause, true);
   }

   private boolean remove0(Throwable cause, boolean notifyWritability) {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;
      if (e == null) {
         this.clearNioBuffers();
         return false;
      } else {
         Object msg = e.msg;
         ChannelPromise promise = e.promise;
         int size = e.pendingSize;
         this.removeEntry(e);
         if (!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            safeFail(promise, cause);
            this.decrementPendingOutboundBytes(size, false, notifyWritability);
         }

         e.unguardedRecycle();
         return true;
      }
   }

   private void removeEntry(ChannelOutboundBuffer.Entry e) {
      if (--this.flushed == 0) {
         this.flushedEntry = null;
         if (e == this.tailEntry) {
            this.tailEntry = null;
            this.unflushedEntry = null;
         }
      } else {
         this.flushedEntry = e.next;
      }
   }

   public void removeBytes(long writtenBytes) {
      while (true) {
         Object msg = this.current();
         if (!(msg instanceof ByteBuf)) {
            assert writtenBytes == 0L;
         } else {
            ByteBuf buf = (ByteBuf)msg;
            int readerIndex = buf.readerIndex();
            int readableBytes = buf.writerIndex() - readerIndex;
            if (readableBytes <= writtenBytes) {
               if (writtenBytes != 0L) {
                  this.progress(readableBytes);
                  writtenBytes -= readableBytes;
               }

               this.remove();
               continue;
            }

            if (writtenBytes != 0L) {
               buf.readerIndex(readerIndex + (int)writtenBytes);
               this.progress(writtenBytes);
            }
         }

         this.clearNioBuffers();
         return;
      }
   }

   private void clearNioBuffers() {
      int count = this.nioBufferCount;
      if (count > 0) {
         this.nioBufferCount = 0;
         Arrays.fill(NIO_BUFFERS.get(), 0, count, null);
      }
   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(Integer.MAX_VALUE, 2147483647L);
   }

   public ByteBuffer[] nioBuffers(int maxCount, long maxBytes) {
      assert maxCount > 0;

      assert maxBytes > 0L;

      long nioBufferSize = 0L;
      int nioBufferCount = 0;
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
      ByteBuffer[] nioBuffers = NIO_BUFFERS.get(threadLocalMap);

      for (ChannelOutboundBuffer.Entry entry = this.flushedEntry; this.isFlushedEntry(entry) && entry.msg instanceof ByteBuf; entry = entry.next) {
         if (!entry.cancelled) {
            ByteBuf buf = (ByteBuf)entry.msg;
            int readerIndex = buf.readerIndex();
            int readableBytes = buf.writerIndex() - readerIndex;
            if (readableBytes > 0) {
               if (maxBytes - readableBytes < nioBufferSize && nioBufferCount != 0) {
                  break;
               }

               nioBufferSize += readableBytes;
               int count = entry.count;
               if (count == -1) {
                  entry.count = count = buf.nioBufferCount();
               }

               int neededSpace = Math.min(maxCount, nioBufferCount + count);
               if (neededSpace > nioBuffers.length) {
                  nioBuffers = expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
                  NIO_BUFFERS.set(threadLocalMap, nioBuffers);
               }

               if (count == 1) {
                  ByteBuffer nioBuf = entry.buf;
                  if (nioBuf == null) {
                     entry.buf = nioBuf = buf.internalNioBuffer(readerIndex, readableBytes);
                  }

                  nioBuffers[nioBufferCount++] = nioBuf;
               } else {
                  nioBufferCount = nioBuffers(entry, buf, nioBuffers, nioBufferCount, maxCount);
               }

               if (nioBufferCount >= maxCount) {
                  break;
               }
            }
         }
      }

      this.nioBufferCount = nioBufferCount;
      this.nioBufferSize = nioBufferSize;
      return nioBuffers;
   }

   private static int nioBuffers(ChannelOutboundBuffer.Entry entry, ByteBuf buf, ByteBuffer[] nioBuffers, int nioBufferCount, int maxCount) {
      ByteBuffer[] nioBufs = entry.bufs;
      if (nioBufs == null) {
         entry.bufs = nioBufs = buf.nioBuffers();
      }

      for (int i = 0; i < nioBufs.length && nioBufferCount < maxCount; i++) {
         ByteBuffer nioBuf = nioBufs[i];
         if (nioBuf == null) {
            break;
         }

         if (nioBuf.hasRemaining()) {
            nioBuffers[nioBufferCount++] = nioBuf;
         }
      }

      return nioBufferCount;
   }

   private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
      int newCapacity = array.length;

      do {
         newCapacity <<= 1;
         if (newCapacity < 0) {
            throw new IllegalStateException();
         }
      } while (neededSpace > newCapacity);

      ByteBuffer[] newArray = new ByteBuffer[newCapacity];
      System.arraycopy(array, 0, newArray, 0, size);
      return newArray;
   }

   public int nioBufferCount() {
      return this.nioBufferCount;
   }

   public long nioBufferSize() {
      return this.nioBufferSize;
   }

   public boolean isWritable() {
      return this.unwritable == 0;
   }

   public boolean getUserDefinedWritability(int index) {
      return (this.unwritable & writabilityMask(index)) == 0;
   }

   public void setUserDefinedWritability(int index, boolean writable) {
      if (writable) {
         this.setUserDefinedWritability(index);
      } else {
         this.clearUserDefinedWritability(index);
      }
   }

   private void setUserDefinedWritability(int index) {
      int mask = ~writabilityMask(index);

      int oldValue;
      int newValue;
      do {
         oldValue = this.unwritable;
         newValue = oldValue & mask;
      } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));

      if (oldValue != 0 && newValue == 0) {
         this.fireChannelWritabilityChanged(true);
      }
   }

   private void clearUserDefinedWritability(int index) {
      int mask = writabilityMask(index);

      int oldValue;
      int newValue;
      do {
         oldValue = this.unwritable;
         newValue = oldValue | mask;
      } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));

      if (oldValue == 0 && newValue != 0) {
         this.fireChannelWritabilityChanged(true);
      }
   }

   private static int writabilityMask(int index) {
      if (index >= 1 && index <= 31) {
         return 1 << index;
      } else {
         throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
      }
   }

   private void setWritable(boolean invokeLater) {
      int oldValue;
      int newValue;
      do {
         oldValue = this.unwritable;
         newValue = oldValue & -2;
      } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));

      if (oldValue != 0 && newValue == 0) {
         this.fireChannelWritabilityChanged(invokeLater);
      }
   }

   private void setUnwritable(boolean invokeLater) {
      int oldValue;
      int newValue;
      do {
         oldValue = this.unwritable;
         newValue = oldValue | 1;
      } while (!UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));

      if (oldValue == 0) {
         this.fireChannelWritabilityChanged(invokeLater);
      }
   }

   private void fireChannelWritabilityChanged(boolean invokeLater) {
      final ChannelPipeline pipeline = this.channel.pipeline();
      if (invokeLater) {
         Runnable task = this.fireChannelWritabilityChangedTask;
         if (task == null) {
            this.fireChannelWritabilityChangedTask = task = new Runnable() {
               @Override
               public void run() {
                  pipeline.fireChannelWritabilityChanged();
               }
            };
         }

         this.channel.eventLoop().execute(task);
      } else {
         pipeline.fireChannelWritabilityChanged();
      }
   }

   public int size() {
      return this.flushed;
   }

   public boolean isEmpty() {
      return this.flushed == 0;
   }

   void failFlushed(Throwable cause, boolean notify) {
      if (!this.inFail) {
         try {
            this.inFail = true;

            while (this.remove0(cause, notify)) {
            }
         } finally {
            this.inFail = false;
         }
      }
   }

   void close(final Throwable cause, final boolean allowChannelOpen) {
      if (this.inFail) {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               ChannelOutboundBuffer.this.close(cause, allowChannelOpen);
            }
         });
      } else {
         this.inFail = true;
         if (!allowChannelOpen && this.channel.isOpen()) {
            throw new IllegalStateException("close() must be invoked after the channel is closed.");
         } else if (!this.isEmpty()) {
            throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
         } else {
            try {
               for (ChannelOutboundBuffer.Entry e = this.unflushedEntry; e != null; e = e.unguardedRecycleAndGetNext()) {
                  int size = e.pendingSize;
                  TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
                  if (!e.cancelled) {
                     ReferenceCountUtil.safeRelease(e.msg);
                     safeFail(e.promise, cause);
                  }
               }
            } finally {
               this.inFail = false;
            }

            this.clearNioBuffers();
         }
      }
   }

   void close(ClosedChannelException cause) {
      this.close(cause, false);
   }

   private static void safeSuccess(ChannelPromise promise) {
      PromiseNotificationUtil.trySuccess(promise, null, promise instanceof VoidChannelPromise ? null : logger);
   }

   private static void safeFail(ChannelPromise promise, Throwable cause) {
      PromiseNotificationUtil.tryFailure(promise, cause, promise instanceof VoidChannelPromise ? null : logger);
   }

   @Deprecated
   public void recycle() {
   }

   public long totalPendingWriteBytes() {
      return this.totalPendingSize;
   }

   public long bytesBeforeUnwritable() {
      long bytes = this.channel.config().getWriteBufferHighWaterMark() - this.totalPendingSize + 1L;
      return bytes > 0L && this.isWritable() ? bytes : 0L;
   }

   public long bytesBeforeWritable() {
      long bytes = this.totalPendingSize - this.channel.config().getWriteBufferLowWaterMark() + 1L;
      return bytes > 0L && !this.isWritable() ? bytes : 0L;
   }

   public void forEachFlushedMessage(ChannelOutboundBuffer.MessageProcessor processor) throws Exception {
      ObjectUtil.checkNotNull(processor, "processor");
      ChannelOutboundBuffer.Entry entry = this.flushedEntry;
      if (entry != null) {
         while (entry.cancelled || processor.processMessage(entry.msg)) {
            entry = entry.next;
            if (!this.isFlushedEntry(entry)) {
               return;
            }
         }
      }
   }

   private boolean isFlushedEntry(ChannelOutboundBuffer.Entry e) {
      return e != null && e != this.unflushedEntry;
   }

   static final class Entry {
      private static final Recycler<ChannelOutboundBuffer.Entry> RECYCLER = new Recycler<ChannelOutboundBuffer.Entry>() {
         protected ChannelOutboundBuffer.Entry newObject(Recycler.Handle<ChannelOutboundBuffer.Entry> handle) {
            return new ChannelOutboundBuffer.Entry(handle);
         }
      };
      private final Recycler.EnhancedHandle<ChannelOutboundBuffer.Entry> handle;
      ChannelOutboundBuffer.Entry next;
      Object msg;
      ByteBuffer[] bufs;
      ByteBuffer buf;
      ChannelPromise promise;
      long progress;
      long total;
      int pendingSize;
      int count = -1;
      boolean cancelled;

      private Entry(ObjectPool.Handle<ChannelOutboundBuffer.Entry> handle) {
         this.handle = (Recycler.EnhancedHandle<ChannelOutboundBuffer.Entry>)handle;
      }

      static ChannelOutboundBuffer.Entry newInstance(Object msg, int size, long total, ChannelPromise promise) {
         ChannelOutboundBuffer.Entry entry = RECYCLER.get();
         entry.msg = msg;
         entry.pendingSize = size + ChannelOutboundBuffer.CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD;
         entry.total = total;
         entry.promise = promise;
         return entry;
      }

      int cancel() {
         if (!this.cancelled) {
            this.cancelled = true;
            int pSize = this.pendingSize;
            ReferenceCountUtil.safeRelease(this.msg);
            this.msg = Unpooled.EMPTY_BUFFER;
            this.pendingSize = 0;
            this.total = 0L;
            this.progress = 0L;
            this.bufs = null;
            this.buf = null;
            return pSize;
         } else {
            return 0;
         }
      }

      void unguardedRecycle() {
         this.next = null;
         this.bufs = null;
         this.buf = null;
         this.msg = null;
         this.promise = null;
         this.progress = 0L;
         this.total = 0L;
         this.pendingSize = 0;
         this.count = -1;
         this.cancelled = false;
         this.handle.unguardedRecycle(this);
      }

      ChannelOutboundBuffer.Entry unguardedRecycleAndGetNext() {
         ChannelOutboundBuffer.Entry next = this.next;
         this.unguardedRecycle();
         return next;
      }
   }

   public interface MessageProcessor {
      boolean processMessage(Object var1) throws Exception;
   }
}
