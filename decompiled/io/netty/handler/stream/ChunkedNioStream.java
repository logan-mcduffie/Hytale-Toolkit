package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream implements ChunkedInput<ByteBuf> {
   private final ReadableByteChannel in;
   private final int chunkSize;
   private long offset;
   private final ByteBuffer byteBuffer;

   public ChunkedNioStream(ReadableByteChannel in) {
      this(in, 8192);
   }

   public ChunkedNioStream(ReadableByteChannel in, int chunkSize) {
      this.in = ObjectUtil.checkNotNull(in, "in");
      this.chunkSize = ObjectUtil.checkPositive(chunkSize, "chunkSize");
      this.byteBuffer = ByteBuffer.allocate(chunkSize);
   }

   public long transferredBytes() {
      return this.offset;
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      if (this.byteBuffer.position() > 0) {
         return false;
      } else if (this.in.isOpen()) {
         int b = this.in.read(this.byteBuffer);
         if (b < 0) {
            return true;
         } else {
            this.offset += b;
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public void close() throws Exception {
      this.in.close();
   }

   @Deprecated
   public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
      return this.readChunk(ctx.alloc());
   }

   public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
      if (this.isEndOfInput()) {
         return null;
      } else {
         int readBytes = this.byteBuffer.position();

         do {
            int localReadBytes = this.in.read(this.byteBuffer);
            if (localReadBytes < 0) {
               break;
            }

            readBytes += localReadBytes;
            this.offset += localReadBytes;
         } while (readBytes != this.chunkSize);

         ((Buffer)this.byteBuffer).flip();
         boolean release = true;
         ByteBuf buffer = allocator.buffer(this.byteBuffer.remaining());

         ByteBuf var5;
         try {
            buffer.writeBytes(this.byteBuffer);
            ((Buffer)this.byteBuffer).clear();
            release = false;
            var5 = buffer;
         } finally {
            if (release) {
               buffer.release();
            }
         }

         return var5;
      }
   }

   @Override
   public long length() {
      return -1L;
   }

   @Override
   public long progress() {
      return this.offset;
   }
}
