package io.netty.channel;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class PendingWriteQueue {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
   private static final int PENDING_WRITE_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.pendingWriteSizeOverhead", 64);
   private final ChannelOutboundInvoker invoker;
   private final EventExecutor executor;
   private final PendingBytesTracker tracker;
   private PendingWriteQueue.PendingWrite head;
   private PendingWriteQueue.PendingWrite tail;
   private int size;
   private long bytes;

   public PendingWriteQueue(ChannelHandlerContext ctx) {
      this.tracker = PendingBytesTracker.newTracker(ctx.channel());
      this.invoker = ctx;
      this.executor = ctx.executor();
   }

   public PendingWriteQueue(Channel channel) {
      this.tracker = PendingBytesTracker.newTracker(channel);
      this.invoker = channel;
      this.executor = channel.eventLoop();
   }

   public boolean isEmpty() {
      assert this.executor.inEventLoop();

      return this.head == null;
   }

   public int size() {
      assert this.executor.inEventLoop();

      return this.size;
   }

   public long bytes() {
      assert this.executor.inEventLoop();

      return this.bytes;
   }

   private int size(Object msg) {
      int messageSize = this.tracker.size(msg);
      if (messageSize < 0) {
         messageSize = 0;
      }

      return messageSize + PENDING_WRITE_OVERHEAD;
   }

   public void add(Object msg, ChannelPromise promise) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(msg, "msg");
      ObjectUtil.checkNotNull(promise, "promise");
      int messageSize = this.size(msg);
      PendingWriteQueue.PendingWrite write = PendingWriteQueue.PendingWrite.newInstance(msg, messageSize, promise);
      PendingWriteQueue.PendingWrite currentTail = this.tail;
      if (currentTail == null) {
         this.tail = this.head = write;
      } else {
         currentTail.next = write;
         this.tail = write;
      }

      this.size++;
      this.bytes += messageSize;
      this.tracker.incrementPendingOutboundBytes(write.size);
      if (msg instanceof AbstractReferenceCountedByteBuf) {
         ((AbstractReferenceCountedByteBuf)msg).touch();
      } else {
         ReferenceCountUtil.touch(msg);
      }
   }

   public ChannelFuture removeAndWriteAll() {
      assert this.executor.inEventLoop();

      if (this.isEmpty()) {
         return null;
      } else {
         ChannelPromise p = this.invoker.newPromise();
         PromiseCombiner combiner = new PromiseCombiner(this.executor);

         try {
            for (PendingWriteQueue.PendingWrite write = this.head; write != null; write = this.head) {
               this.head = this.tail = null;
               this.size = 0;
               this.bytes = 0L;

               while (write != null) {
                  PendingWriteQueue.PendingWrite next = write.next;
                  Object msg = write.msg;
                  ChannelPromise promise = write.promise;
                  this.recycle(write, false);
                  if (!(promise instanceof VoidChannelPromise)) {
                     combiner.add((Promise)promise);
                  }

                  this.invoker.write(msg, promise);
                  write = next;
               }
            }

            combiner.finish(p);
         } catch (Throwable var7) {
            p.setFailure(var7);
         }

         this.assertEmpty();
         return p;
      }
   }

   public void removeAndFailAll(Throwable cause) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(cause, "cause");

      for (PendingWriteQueue.PendingWrite write = this.head; write != null; write = this.head) {
         this.head = this.tail = null;
         this.size = 0;
         this.bytes = 0L;

         while (write != null) {
            PendingWriteQueue.PendingWrite next = write.next;
            ReferenceCountUtil.safeRelease(write.msg);
            ChannelPromise promise = write.promise;
            this.recycle(write, false);
            safeFail(promise, cause);
            write = next;
         }
      }

      this.assertEmpty();
   }

   public void removeAndFail(Throwable cause) {
      assert this.executor.inEventLoop();

      ObjectUtil.checkNotNull(cause, "cause");
      PendingWriteQueue.PendingWrite write = this.head;
      if (write != null) {
         ReferenceCountUtil.safeRelease(write.msg);
         ChannelPromise promise = write.promise;
         safeFail(promise, cause);
         this.recycle(write, true);
      }
   }

   private void assertEmpty() {
      assert this.tail == null && this.head == null && this.size == 0;
   }

   public ChannelFuture removeAndWrite() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite write = this.head;
      if (write == null) {
         return null;
      } else {
         Object msg = write.msg;
         ChannelPromise promise = write.promise;
         this.recycle(write, true);
         return this.invoker.write(msg, promise);
      }
   }

   public ChannelPromise remove() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite write = this.head;
      if (write == null) {
         return null;
      } else {
         ChannelPromise promise = write.promise;
         ReferenceCountUtil.safeRelease(write.msg);
         this.recycle(write, true);
         return promise;
      }
   }

   public Object current() {
      assert this.executor.inEventLoop();

      PendingWriteQueue.PendingWrite write = this.head;
      return write == null ? null : write.msg;
   }

   private void recycle(PendingWriteQueue.PendingWrite write, boolean update) {
      PendingWriteQueue.PendingWrite next = write.next;
      long writeSize = write.size;
      if (update) {
         if (next == null) {
            this.head = this.tail = null;
            this.size = 0;
            this.bytes = 0L;
         } else {
            this.head = next;
            this.size--;
            this.bytes -= writeSize;

            assert this.size > 0 && this.bytes >= 0L;
         }
      }

      write.recycle();
      this.tracker.decrementPendingOutboundBytes(writeSize);
   }

   private static void safeFail(ChannelPromise promise, Throwable cause) {
      if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
         logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
      }
   }

   static final class PendingWrite {
      private static final Recycler<PendingWriteQueue.PendingWrite> RECYCLER = new Recycler<PendingWriteQueue.PendingWrite>() {
         protected PendingWriteQueue.PendingWrite newObject(Recycler.Handle<PendingWriteQueue.PendingWrite> handle) {
            return new PendingWriteQueue.PendingWrite(handle);
         }
      };
      private final ObjectPool.Handle<PendingWriteQueue.PendingWrite> handle;
      private PendingWriteQueue.PendingWrite next;
      private long size;
      private ChannelPromise promise;
      private Object msg;

      private PendingWrite(ObjectPool.Handle<PendingWriteQueue.PendingWrite> handle) {
         this.handle = handle;
      }

      static PendingWriteQueue.PendingWrite newInstance(Object msg, int size, ChannelPromise promise) {
         PendingWriteQueue.PendingWrite write = RECYCLER.get();
         write.size = size;
         write.msg = msg;
         write.promise = promise;
         return write;
      }

      private void recycle() {
         this.size = 0L;
         this.next = null;
         this.msg = null;
         this.promise = null;
         this.handle.recycle(this);
      }
   }
}
