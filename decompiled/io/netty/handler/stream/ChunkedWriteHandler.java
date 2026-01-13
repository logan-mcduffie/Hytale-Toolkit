package io.netty.handler.stream;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ChunkedWriteHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
   private Queue<ChunkedWriteHandler.PendingWrite> queue;
   private volatile ChannelHandlerContext ctx;

   public ChunkedWriteHandler() {
   }

   @Deprecated
   public ChunkedWriteHandler(int maxPendingWrites) {
      ObjectUtil.checkPositive(maxPendingWrites, "maxPendingWrites");
   }

   private void allocateQueue() {
      if (this.queue == null) {
         this.queue = new ArrayDeque<>();
      }
   }

   private boolean queueIsEmpty() {
      return this.queue == null || this.queue.isEmpty();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
   }

   public void resumeTransfer() {
      final ChannelHandlerContext ctx = this.ctx;
      if (ctx != null) {
         if (ctx.executor().inEventLoop()) {
            this.resumeTransfer0(ctx);
         } else {
            ctx.executor().execute(new Runnable() {
               @Override
               public void run() {
                  ChunkedWriteHandler.this.resumeTransfer0(ctx);
               }
            });
         }
      }
   }

   private void resumeTransfer0(ChannelHandlerContext ctx) {
      try {
         this.doFlush(ctx);
      } catch (Exception var3) {
         logger.warn("Unexpected exception while sending chunks.", (Throwable)var3);
      }
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (this.queueIsEmpty() && !(msg instanceof ChunkedInput)) {
         ctx.write(msg, promise);
      } else {
         this.allocateQueue();
         this.queue.add(new ChunkedWriteHandler.PendingWrite(msg, promise));
      }
   }

   @Override
   public void flush(ChannelHandlerContext ctx) throws Exception {
      this.doFlush(ctx);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.doFlush(ctx);
      ctx.fireChannelInactive();
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
      if (ctx.channel().isWritable()) {
         this.doFlush(ctx);
      }

      ctx.fireChannelWritabilityChanged();
   }

   private void discard(Throwable cause) {
      if (!this.queueIsEmpty()) {
         while (true) {
            ChunkedWriteHandler.PendingWrite currentWrite = this.queue.poll();
            if (currentWrite == null) {
               return;
            }

            Object message = currentWrite.msg;
            if (message instanceof ChunkedInput) {
               ChunkedInput<?> in = (ChunkedInput<?>)message;

               boolean endOfInput;
               long inputLength;
               try {
                  endOfInput = in.isEndOfInput();
                  inputLength = in.length();
                  closeInput(in);
               } catch (Exception var9) {
                  closeInput(in);
                  currentWrite.fail(var9);
                  logger.warn("ChunkedInput failed", (Throwable)var9);
                  continue;
               }

               if (!endOfInput) {
                  if (cause == null) {
                     cause = new ClosedChannelException();
                  }

                  currentWrite.fail(cause);
               } else {
                  currentWrite.success(inputLength);
               }
            } else {
               if (cause == null) {
                  cause = new ClosedChannelException();
               }

               currentWrite.fail(cause);
            }
         }
      }
   }

   private void doFlush(ChannelHandlerContext ctx) {
      Channel channel = ctx.channel();
      if (!channel.isActive()) {
         this.discard(null);
         ctx.flush();
      } else if (this.queueIsEmpty()) {
         ctx.flush();
      } else {
         boolean requiresFlush = true;
         ByteBufAllocator allocator = ctx.alloc();

         while (channel.isWritable()) {
            final ChunkedWriteHandler.PendingWrite currentWrite = this.queue.peek();
            if (currentWrite == null) {
               break;
            }

            if (currentWrite.promise.isDone()) {
               this.queue.remove();
            } else {
               Object pendingMessage = currentWrite.msg;
               if (pendingMessage instanceof ChunkedInput) {
                  final ChunkedInput<?> chunks = (ChunkedInput<?>)pendingMessage;
                  Object message = null;

                  boolean endOfInput;
                  boolean suspend;
                  try {
                     message = chunks.readChunk(allocator);
                     endOfInput = chunks.isEndOfInput();
                     suspend = message == null && !endOfInput;
                  } catch (Throwable var13) {
                     this.queue.remove();
                     if (message != null) {
                        ReferenceCountUtil.release(message);
                     }

                     closeInput(chunks);
                     currentWrite.fail(var13);
                     break;
                  }

                  if (suspend) {
                     break;
                  }

                  if (message == null) {
                     message = Unpooled.EMPTY_BUFFER;
                  }

                  if (endOfInput) {
                     this.queue.remove();
                  }

                  ChannelFuture f = ctx.writeAndFlush(message);
                  if (endOfInput) {
                     if (f.isDone()) {
                        handleEndOfInputFuture(f, chunks, currentWrite);
                     } else {
                        f.addListener(new ChannelFutureListener() {
                           public void operationComplete(ChannelFuture future) {
                              ChunkedWriteHandler.handleEndOfInputFuture(future, chunks, currentWrite);
                           }
                        });
                     }
                  } else {
                     final boolean resume = !channel.isWritable();
                     if (f.isDone()) {
                        this.handleFuture(f, chunks, currentWrite, resume);
                     } else {
                        f.addListener(new ChannelFutureListener() {
                           public void operationComplete(ChannelFuture future) {
                              ChunkedWriteHandler.this.handleFuture(future, chunks, currentWrite, resume);
                           }
                        });
                     }
                  }

                  requiresFlush = false;
               } else {
                  this.queue.remove();
                  ctx.write(pendingMessage, currentWrite.promise);
                  requiresFlush = true;
               }

               if (!channel.isActive()) {
                  this.discard(new ClosedChannelException());
                  break;
               }
            }
         }

         if (requiresFlush) {
            ctx.flush();
         }
      }
   }

   private static void handleEndOfInputFuture(ChannelFuture future, ChunkedInput<?> input, ChunkedWriteHandler.PendingWrite currentWrite) {
      if (!future.isSuccess()) {
         closeInput(input);
         currentWrite.fail(future.cause());
      } else {
         long inputProgress = input.progress();
         long inputLength = input.length();
         closeInput(input);
         currentWrite.progress(inputProgress, inputLength);
         currentWrite.success(inputLength);
      }
   }

   private void handleFuture(ChannelFuture future, ChunkedInput<?> input, ChunkedWriteHandler.PendingWrite currentWrite, boolean resume) {
      if (!future.isSuccess()) {
         closeInput(input);
         currentWrite.fail(future.cause());
      } else {
         currentWrite.progress(input.progress(), input.length());
         if (resume && future.channel().isWritable()) {
            this.resumeTransfer();
         }
      }
   }

   private static void closeInput(ChunkedInput<?> chunks) {
      try {
         chunks.close();
      } catch (Throwable var2) {
         logger.warn("Failed to close a ChunkedInput.", var2);
      }
   }

   private static final class PendingWrite {
      final Object msg;
      final ChannelPromise promise;

      PendingWrite(Object msg, ChannelPromise promise) {
         this.msg = msg;
         this.promise = promise;
      }

      void fail(Throwable cause) {
         ReferenceCountUtil.release(this.msg);
         this.promise.tryFailure(cause);
      }

      void success(long total) {
         if (!this.promise.isDone()) {
            this.progress(total, total);
            this.promise.trySuccess();
         }
      }

      void progress(long progress, long total) {
         if (this.promise instanceof ChannelProgressivePromise) {
            ((ChannelProgressivePromise)this.promise).tryProgress(progress, total);
         }
      }
   }
}
