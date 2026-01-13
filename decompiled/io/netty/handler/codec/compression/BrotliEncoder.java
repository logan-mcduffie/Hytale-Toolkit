package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.BrotliEncoderChannel;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

@ChannelHandler.Sharable
public final class BrotliEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final AttributeKey<BrotliEncoder.Writer> ATTR = AttributeKey.valueOf("BrotliEncoderWriter");
   private final Parameters parameters;
   private final boolean isSharable;
   private BrotliEncoder.Writer writer;

   public BrotliEncoder() {
      this(BrotliOptions.DEFAULT);
   }

   public BrotliEncoder(BrotliOptions brotliOptions) {
      this(brotliOptions.parameters());
   }

   public BrotliEncoder(Parameters parameters) {
      this(parameters, true);
   }

   public BrotliEncoder(Parameters parameters, boolean isSharable) {
      super(ByteBuf.class);
      this.parameters = ObjectUtil.checkNotNull(parameters, "Parameters");
      this.isSharable = isSharable;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      BrotliEncoder.Writer writer = new BrotliEncoder.Writer(this.parameters, ctx);
      if (this.isSharable) {
         ctx.channel().attr(ATTR).set(writer);
      } else {
         this.writer = writer;
      }

      super.handlerAdded(ctx);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.finish(ctx);
      super.handlerRemoved(ctx);
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
      if (!msg.isReadable()) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         BrotliEncoder.Writer writer;
         if (this.isSharable) {
            writer = ctx.channel().attr(ATTR).get();
         } else {
            writer = this.writer;
         }

         if (writer == null) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            writer.encode(msg, preferDirect);
            return writer.writableBuffer;
         }
      }
   }

   @Override
   public boolean isSharable() {
      return this.isSharable;
   }

   public void finish(ChannelHandlerContext ctx) throws IOException {
      this.finishEncode(ctx, ctx.newPromise());
   }

   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) throws IOException {
      BrotliEncoder.Writer writer;
      if (this.isSharable) {
         writer = ctx.channel().attr(ATTR).getAndSet(null);
      } else {
         writer = this.writer;
      }

      if (writer != null) {
         writer.close();
         this.writer = null;
      }

      return promise;
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ChannelFuture f = this.finishEncode(ctx, ctx.newPromise());
      EncoderUtil.closeAfterFinishEncode(ctx, f, promise);
   }

   private static final class Writer implements WritableByteChannel {
      private ByteBuf writableBuffer;
      private final BrotliEncoderChannel brotliEncoderChannel;
      private final ChannelHandlerContext ctx;
      private boolean isClosed;

      private Writer(Parameters parameters, ChannelHandlerContext ctx) throws IOException {
         this.brotliEncoderChannel = new BrotliEncoderChannel(this, parameters);
         this.ctx = ctx;
      }

      private void encode(ByteBuf msg, boolean preferDirect) throws Exception {
         try {
            this.allocate(preferDirect);
            ByteBuffer nioBuffer = CompressionUtil.safeReadableNioBuffer(msg);
            int position = nioBuffer.position();
            this.brotliEncoderChannel.write(nioBuffer);
            msg.skipBytes(nioBuffer.position() - position);
            this.brotliEncoderChannel.flush();
         } catch (Exception var5) {
            ReferenceCountUtil.release(msg);
            throw var5;
         }
      }

      private void allocate(boolean preferDirect) {
         if (preferDirect) {
            this.writableBuffer = this.ctx.alloc().ioBuffer();
         } else {
            this.writableBuffer = this.ctx.alloc().buffer();
         }
      }

      @Override
      public int write(ByteBuffer src) throws IOException {
         if (!this.isOpen()) {
            throw new ClosedChannelException();
         } else {
            return this.writableBuffer.writeBytes(src).readableBytes();
         }
      }

      @Override
      public boolean isOpen() {
         return !this.isClosed;
      }

      @Override
      public void close() {
         final ChannelPromise promise = this.ctx.newPromise();
         this.ctx.executor().execute(new Runnable() {
            @Override
            public void run() {
               try {
                  Writer.this.finish(promise);
               } catch (IOException var2) {
                  promise.setFailure(new IllegalStateException("Failed to finish encoding", var2));
               }
            }
         });
      }

      public void finish(ChannelPromise promise) throws IOException {
         if (!this.isClosed) {
            this.allocate(true);

            try {
               this.brotliEncoderChannel.close();
               this.isClosed = true;
            } catch (Exception var3) {
               promise.setFailure(var3);
               ReferenceCountUtil.release(this.writableBuffer);
               return;
            }

            this.ctx.writeAndFlush(this.writableBuffer, promise);
         }
      }
   }
}
