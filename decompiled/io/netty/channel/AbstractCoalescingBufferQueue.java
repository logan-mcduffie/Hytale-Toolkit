package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;

public abstract class AbstractCoalescingBufferQueue {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
   private final ArrayDeque<Object> bufAndListenerPairs;
   private final PendingBytesTracker tracker;
   private int readableBytes;

   protected AbstractCoalescingBufferQueue(Channel channel, int initSize) {
      this.bufAndListenerPairs = new ArrayDeque<>(initSize);
      this.tracker = channel == null ? null : PendingBytesTracker.newTracker(channel);
   }

   public final void addFirst(ByteBuf buf, ChannelPromise promise) {
      this.addFirst(buf, toChannelFutureListener(promise));
   }

   private void addFirst(ByteBuf buf, ChannelFutureListener listener) {
      buf.touch();
      if (listener != null) {
         this.bufAndListenerPairs.addFirst(listener);
      }

      this.bufAndListenerPairs.addFirst(buf);
      this.incrementReadableBytes(buf.readableBytes());
   }

   public final void add(ByteBuf buf) {
      this.add(buf, (ChannelFutureListener)null);
   }

   public final void add(ByteBuf buf, ChannelPromise promise) {
      this.add(buf, toChannelFutureListener(promise));
   }

   public final void add(ByteBuf buf, ChannelFutureListener listener) {
      buf.touch();
      this.bufAndListenerPairs.add(buf);
      if (listener != null) {
         this.bufAndListenerPairs.add(listener);
      }

      this.incrementReadableBytes(buf.readableBytes());
   }

   public final ByteBuf removeFirst(ChannelPromise aggregatePromise) {
      Object entry = this.bufAndListenerPairs.poll();
      if (entry == null) {
         return null;
      } else {
         assert entry instanceof ByteBuf;

         ByteBuf result = (ByteBuf)entry;
         this.decrementReadableBytes(result.readableBytes());
         entry = this.bufAndListenerPairs.peek();
         if (entry instanceof ChannelFutureListener) {
            aggregatePromise.addListener((ChannelFutureListener)entry);
            this.bufAndListenerPairs.poll();
         }

         return result;
      }
   }

   public final ByteBuf remove(ByteBufAllocator alloc, int bytes, ChannelPromise aggregatePromise) {
      ObjectUtil.checkPositiveOrZero(bytes, "bytes");
      ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
      if (this.bufAndListenerPairs.isEmpty()) {
         assert this.readableBytes == 0;

         return this.removeEmptyValue();
      } else {
         bytes = Math.min(bytes, this.readableBytes);
         ByteBuf toReturn = null;
         ByteBuf entryBuffer = null;
         int originalBytes = bytes;
         Object entry = null;

         try {
            while (true) {
               entry = this.bufAndListenerPairs.poll();
               if (entry == null) {
                  break;
               }

               if (entry instanceof ByteBuf) {
                  entryBuffer = (ByteBuf)entry;
                  int bufferBytes = entryBuffer.readableBytes();
                  if (bufferBytes > bytes) {
                     this.bufAndListenerPairs.addFirst(entryBuffer);
                     if (bytes > 0) {
                        entryBuffer = entryBuffer.readRetainedSlice(bytes);
                        toReturn = toReturn == null ? entryBuffer : this.compose(alloc, toReturn, entryBuffer);
                        bytes = 0;
                     }
                     break;
                  }

                  bytes -= bufferBytes;
                  if (toReturn == null) {
                     toReturn = bytes == 0 ? entryBuffer : this.composeFirst(alloc, entryBuffer, bufferBytes + bytes);
                  } else {
                     toReturn = this.compose(alloc, toReturn, entryBuffer);
                  }

                  entryBuffer = null;
               } else if (entry instanceof DelegatingChannelPromiseNotifier) {
                  aggregatePromise.addListener((DelegatingChannelPromiseNotifier)entry);
               } else if (entry instanceof ChannelFutureListener) {
                  aggregatePromise.addListener((ChannelFutureListener)entry);
               }
            }
         } catch (Throwable var9) {
            this.decrementReadableBytes(originalBytes - bytes);
            entry = this.bufAndListenerPairs.peek();
            if (entry instanceof ChannelFutureListener) {
               aggregatePromise.addListener((ChannelFutureListener)entry);
               this.bufAndListenerPairs.poll();
            }

            ReferenceCountUtil.safeRelease(entryBuffer);
            ReferenceCountUtil.safeRelease(toReturn);
            aggregatePromise.setFailure(var9);
            PlatformDependent.throwException(var9);
         }

         this.decrementReadableBytes(originalBytes - bytes);
         return toReturn;
      }
   }

   public final int readableBytes() {
      return this.readableBytes;
   }

   public final boolean isEmpty() {
      return this.bufAndListenerPairs.isEmpty();
   }

   public final void releaseAndFailAll(ChannelOutboundInvoker invoker, Throwable cause) {
      this.releaseAndCompleteAll(invoker.newFailedFuture(cause));
   }

   public final void copyTo(AbstractCoalescingBufferQueue dest) {
      dest.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
      dest.incrementReadableBytes(this.readableBytes);
   }

   public final void writeAndRemoveAll(ChannelHandlerContext ctx) {
      Throwable pending = null;
      ByteBuf previousBuf = null;

      while (true) {
         Object entry = this.bufAndListenerPairs.poll();

         try {
            if (entry == null) {
               if (previousBuf != null) {
                  this.decrementReadableBytes(previousBuf.readableBytes());
                  ctx.write(previousBuf, ctx.voidPromise());
               }
               break;
            }

            if (entry instanceof ByteBuf) {
               if (previousBuf != null) {
                  this.decrementReadableBytes(previousBuf.readableBytes());
                  ctx.write(previousBuf, ctx.voidPromise());
               }

               previousBuf = (ByteBuf)entry;
            } else if (entry instanceof ChannelPromise) {
               this.decrementReadableBytes(previousBuf.readableBytes());
               ctx.write(previousBuf, (ChannelPromise)entry);
               previousBuf = null;
            } else {
               this.decrementReadableBytes(previousBuf.readableBytes());
               ctx.write(previousBuf).addListener((ChannelFutureListener)entry);
               previousBuf = null;
            }
         } catch (Throwable var6) {
            if (pending == null) {
               pending = var6;
            } else {
               logger.info("Throwable being suppressed because Throwable {} is already pending", pending, var6);
            }
         }
      }

      if (pending != null) {
         throw new IllegalStateException(pending);
      }
   }

   @Override
   public String toString() {
      return "bytes: " + this.readableBytes + " buffers: " + (this.size() >> 1);
   }

   protected abstract ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);

   protected final ByteBuf composeIntoComposite(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
      CompositeByteBuf composite = alloc.compositeBuffer(this.size() + 2);

      try {
         composite.addComponent(true, cumulation);
         composite.addComponent(true, next);
      } catch (Throwable var6) {
         composite.release();
         ReferenceCountUtil.safeRelease(next);
         PlatformDependent.throwException(var6);
      }

      return composite;
   }

   protected final ByteBuf copyAndCompose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
      ByteBuf newCumulation = alloc.ioBuffer(cumulation.readableBytes() + next.readableBytes());

      try {
         newCumulation.writeBytes(cumulation).writeBytes(next);
      } catch (Throwable var6) {
         newCumulation.release();
         ReferenceCountUtil.safeRelease(next);
         PlatformDependent.throwException(var6);
      }

      cumulation.release();
      next.release();
      return newCumulation;
   }

   protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first, int bufferSize) {
      return this.composeFirst(allocator, first);
   }

   @Deprecated
   protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
      return first;
   }

   protected abstract ByteBuf removeEmptyValue();

   protected final int size() {
      return this.bufAndListenerPairs.size();
   }

   private void releaseAndCompleteAll(ChannelFuture future) {
      Throwable pending = null;

      while (true) {
         Object entry = this.bufAndListenerPairs.poll();
         if (entry == null) {
            if (pending != null) {
               throw new IllegalStateException(pending);
            }

            return;
         }

         try {
            if (entry instanceof ByteBuf) {
               ByteBuf buffer = (ByteBuf)entry;
               this.decrementReadableBytes(buffer.readableBytes());
               ReferenceCountUtil.safeRelease(buffer);
            } else {
               ((ChannelFutureListener)entry).operationComplete(future);
            }
         } catch (Throwable var5) {
            if (pending == null) {
               pending = var5;
            } else {
               logger.info("Throwable being suppressed because Throwable {} is already pending", pending, var5);
            }
         }
      }
   }

   private void incrementReadableBytes(int increment) {
      int nextReadableBytes = this.readableBytes + increment;
      if (nextReadableBytes < this.readableBytes) {
         throw new IllegalStateException("buffer queue length overflow: " + this.readableBytes + " + " + increment);
      } else {
         this.readableBytes = nextReadableBytes;
         if (this.tracker != null) {
            this.tracker.incrementPendingOutboundBytes(increment);
         }
      }
   }

   private void decrementReadableBytes(int decrement) {
      this.readableBytes -= decrement;

      assert this.readableBytes >= 0;

      if (this.tracker != null) {
         this.tracker.decrementPendingOutboundBytes(decrement);
      }
   }

   private static ChannelFutureListener toChannelFutureListener(ChannelPromise promise) {
      return promise.isVoid() ? null : new DelegatingChannelPromiseNotifier(promise);
   }
}
