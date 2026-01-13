package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public class JZlibEncoder extends ZlibEncoder {
   private final int wrapperOverhead;
   private final Deflater z = new Deflater();
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;
   private static final int THREAD_POOL_DELAY_SECONDS = 10;

   public JZlibEncoder() {
      this(6);
   }

   public JZlibEncoder(int compressionLevel) {
      this(ZlibWrapper.ZLIB, compressionLevel);
   }

   public JZlibEncoder(ZlibWrapper wrapper) {
      this(wrapper, 6);
   }

   public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
      this(wrapper, compressionLevel, 15, 8);
   }

   public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
      ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
      ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
      ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
      ObjectUtil.checkNotNull(wrapper, "wrapper");
      if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
         throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
      } else {
         int resultCode = this.z.init(compressionLevel, windowBits, memLevel, ZlibUtil.convertWrapperType(wrapper));
         if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
         }

         this.wrapperOverhead = ZlibUtil.wrapperOverhead(wrapper);
      }
   }

   public JZlibEncoder(byte[] dictionary) {
      this(6, dictionary);
   }

   public JZlibEncoder(int compressionLevel, byte[] dictionary) {
      this(compressionLevel, 15, 8, dictionary);
   }

   public JZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
      ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
      ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
      ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
      ObjectUtil.checkNotNull(dictionary, "dictionary");
      int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
      if (resultCode != 0) {
         ZlibUtil.fail(this.z, "initialization failure", resultCode);
      } else {
         resultCode = this.z.deflateSetDictionary(dictionary, dictionary.length);
         if (resultCode != 0) {
            ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
         }
      }

      this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
   }

   @Override
   public ChannelFuture close() {
      return this.close(this.ctx().channel().newPromise());
   }

   @Override
   public ChannelFuture close(final ChannelPromise promise) {
      ChannelHandlerContext ctx = this.ctx();
      EventExecutor executor = ctx.executor();
      if (executor.inEventLoop()) {
         return this.finishEncode(ctx, promise);
      } else {
         final ChannelPromise p = ctx.newPromise();
         executor.execute(new Runnable() {
            @Override
            public void run() {
               ChannelFuture f = JZlibEncoder.this.finishEncode(JZlibEncoder.this.ctx(), p);
               PromiseNotifier.cascade(f, promise);
            }
         });
         return p;
      }
   }

   private ChannelHandlerContext ctx() {
      ChannelHandlerContext ctx = this.ctx;
      if (ctx == null) {
         throw new IllegalStateException("not added to a pipeline");
      } else {
         return ctx;
      }
   }

   @Override
   public boolean isClosed() {
      return this.finished;
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
      if (this.finished) {
         out.writeBytes(in);
      } else {
         int inputLength = in.readableBytes();
         if (inputLength != 0) {
            try {
               boolean inHasArray = in.hasArray();
               this.z.avail_in = inputLength;
               if (inHasArray) {
                  this.z.next_in = in.array();
                  this.z.next_in_index = in.arrayOffset() + in.readerIndex();
               } else {
                  byte[] array = new byte[inputLength];
                  in.getBytes(in.readerIndex(), array);
                  this.z.next_in = array;
                  this.z.next_in_index = 0;
               }

               int oldNextInIndex = this.z.next_in_index;
               int maxOutputLength = (int)Math.ceil(inputLength * 1.001) + 12 + this.wrapperOverhead;
               out.ensureWritable(maxOutputLength);
               this.z.avail_out = maxOutputLength;
               this.z.next_out = out.array();
               this.z.next_out_index = out.arrayOffset() + out.writerIndex();
               int oldNextOutIndex = this.z.next_out_index;

               int resultCode;
               try {
                  resultCode = this.z.deflate(2);
               } finally {
                  in.skipBytes(this.z.next_in_index - oldNextInIndex);
               }

               if (resultCode != 0) {
                  ZlibUtil.fail(this.z, "compression failure", resultCode);
               }

               int outputLength = this.z.next_out_index - oldNextOutIndex;
               if (outputLength > 0) {
                  out.writerIndex(out.writerIndex() + outputLength);
               }
            } finally {
               this.z.next_in = null;
               this.z.next_out = null;
            }
         }
      }
   }

   @Override
   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) {
      ChannelFuture f = this.finishEncode(ctx, ctx.newPromise());
      if (!f.isDone()) {
         final Future<?> future = ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
               if (!promise.isDone()) {
                  ctx.close(promise);
               }
            }
         }, 10L, TimeUnit.SECONDS);
         f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) {
               future.cancel(true);
               if (!promise.isDone()) {
                  ctx.close(promise);
               }
            }
         });
      } else {
         ctx.close(promise);
      }
   }

   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
      if (this.finished) {
         promise.setSuccess();
         return promise;
      } else {
         this.finished = true;

         ChannelPromise var6;
         try {
            this.z.next_in = EmptyArrays.EMPTY_BYTES;
            this.z.next_in_index = 0;
            this.z.avail_in = 0;
            byte[] out = new byte[32];
            this.z.next_out = out;
            this.z.next_out_index = 0;
            this.z.avail_out = out.length;
            int resultCode = this.z.deflate(4);
            if (resultCode == 0 || resultCode == 1) {
               ByteBuf footer;
               if (this.z.next_out_index != 0) {
                  footer = Unpooled.wrappedBuffer(out, 0, this.z.next_out_index);
               } else {
                  footer = Unpooled.EMPTY_BUFFER;
               }

               return ctx.writeAndFlush(footer, promise);
            }

            promise.setFailure(ZlibUtil.deflaterException(this.z, "compression failure", resultCode));
            var6 = promise;
         } finally {
            this.z.deflateEnd();
            this.z.next_in = null;
            this.z.next_out = null;
         }

         return var6;
      }
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
   }
}
