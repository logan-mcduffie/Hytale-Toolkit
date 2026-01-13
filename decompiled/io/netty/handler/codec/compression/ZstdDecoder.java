package io.netty.handler.codec.compression;

import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class ZstdDecoder extends ByteToMessageDecoder {
   private final int maximumAllocationSize;
   private final ZstdDecoder.MutableByteBufInputStream inputStream;
   private ZstdInputStreamNoFinalizer zstdIs;
   private boolean needsRead;
   private ZstdDecoder.State currentState;

   public ZstdDecoder() {
      this(4194304);
   }

   public ZstdDecoder(int maximumAllocationSize) {
      try {
         Zstd.ensureAvailability();
      } catch (Throwable var3) {
         throw new ExceptionInInitializerError(var3);
      }

      this.inputStream = new ZstdDecoder.MutableByteBufInputStream();
      this.currentState = ZstdDecoder.State.DECOMPRESS_DATA;
      this.maximumAllocationSize = ObjectUtil.checkPositiveOrZero(maximumAllocationSize, "maximumAllocationSize");
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      this.needsRead = true;

      try {
         if (this.currentState == ZstdDecoder.State.CORRUPTED) {
            in.skipBytes(in.readableBytes());
            return;
         }

         this.inputStream.current = in;
         ByteBuf outBuffer = null;
         int compressedLength = in.readableBytes();

         try {
            long uncompressedLength;
            if (in.isDirect()) {
               uncompressedLength = com.github.luben.zstd.Zstd.getFrameContentSize(CompressionUtil.safeNioBuffer(in, in.readerIndex(), in.readableBytes()));
            } else {
               uncompressedLength = com.github.luben.zstd.Zstd.getFrameContentSize(in.array(), in.readerIndex() + in.arrayOffset(), in.readableBytes());
            }

            if (uncompressedLength <= 0L) {
               uncompressedLength = compressedLength * 2L;
            }

            int w;
            do {
               if (outBuffer == null) {
                  outBuffer = ctx.alloc()
                     .heapBuffer((int)(this.maximumAllocationSize == 0 ? uncompressedLength : Math.min((long)this.maximumAllocationSize, uncompressedLength)));
               }

               do {
                  w = outBuffer.writeBytes(this.zstdIs, outBuffer.writableBytes());
               } while (w != -1 && outBuffer.isWritable());

               if (outBuffer.isReadable()) {
                  this.needsRead = false;
                  ctx.fireChannelRead(outBuffer);
                  outBuffer = null;
               }
            } while (w != -1);
         } finally {
            if (outBuffer != null) {
               outBuffer.release();
            }
         }
      } catch (Exception var18) {
         this.currentState = ZstdDecoder.State.CORRUPTED;
         throw new DecompressionException(var18);
      } finally {
         this.inputStream.current = null;
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      this.discardSomeReadBytes();
      if (this.needsRead && !ctx.channel().config().isAutoRead()) {
         ctx.read();
      }

      ctx.fireChannelReadComplete();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      super.handlerAdded(ctx);
      this.zstdIs = new ZstdInputStreamNoFinalizer(this.inputStream);
      this.zstdIs.setContinuous(true);
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      try {
         closeSilently(this.zstdIs);
      } finally {
         super.handlerRemoved0(ctx);
      }
   }

   private static void closeSilently(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }
   }

   private static final class MutableByteBufInputStream extends InputStream {
      ByteBuf current;

      private MutableByteBufInputStream() {
      }

      @Override
      public int read() {
         return this.current != null && this.current.isReadable() ? this.current.readByte() & 0xFF : -1;
      }

      @Override
      public int read(byte[] b, int off, int len) {
         int available = this.available();
         if (available == 0) {
            return -1;
         } else {
            len = Math.min(available, len);
            this.current.readBytes(b, off, len);
            return len;
         }
      }

      @Override
      public int available() {
         return this.current == null ? 0 : this.current.readableBytes();
      }
   }

   private static enum State {
      DECOMPRESS_DATA,
      CORRUPTED;
   }
}
