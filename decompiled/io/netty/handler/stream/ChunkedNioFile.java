package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

public class ChunkedNioFile implements ChunkedInput<ByteBuf> {
   private final FileChannel in;
   private final long startOffset;
   private final long endOffset;
   private final int chunkSize;
   private long offset;

   public ChunkedNioFile(File in) throws IOException {
      this(new RandomAccessFile(in, "r").getChannel());
   }

   public ChunkedNioFile(File in, int chunkSize) throws IOException {
      this(new RandomAccessFile(in, "r").getChannel(), chunkSize);
   }

   public ChunkedNioFile(FileChannel in) throws IOException {
      this(in, 8192);
   }

   public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
      this(in, 0L, in.size(), chunkSize);
   }

   public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
      ObjectUtil.checkNotNull(in, "in");
      ObjectUtil.checkPositiveOrZero(offset, "offset");
      ObjectUtil.checkPositiveOrZero(length, "length");
      ObjectUtil.checkPositive(chunkSize, "chunkSize");
      if (!in.isOpen()) {
         throw new ClosedChannelException();
      } else {
         this.in = in;
         this.chunkSize = chunkSize;
         this.offset = this.startOffset = offset;
         this.endOffset = offset + length;
      }
   }

   public long startOffset() {
      return this.startOffset;
   }

   public long endOffset() {
      return this.endOffset;
   }

   public long currentOffset() {
      return this.offset;
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      return this.offset >= this.endOffset || !this.in.isOpen();
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
      long offset = this.offset;
      if (offset >= this.endOffset) {
         return null;
      } else {
         int chunkSize = (int)Math.min((long)this.chunkSize, this.endOffset - offset);
         ByteBuf buffer = allocator.buffer(chunkSize);
         boolean release = true;

         ByteBuf var12;
         try {
            int readBytes = 0;

            do {
               int localReadBytes = buffer.writeBytes(this.in, offset + readBytes, chunkSize - readBytes);
               if (localReadBytes < 0) {
                  break;
               }

               readBytes += localReadBytes;
            } while (readBytes != chunkSize);

            this.offset += readBytes;
            release = false;
            var12 = buffer;
         } finally {
            if (release) {
               buffer.release();
            }
         }

         return var12;
      }
   }

   @Override
   public long length() {
      return this.endOffset - this.startOffset;
   }

   @Override
   public long progress() {
      return this.offset - this.startOffset;
   }
}
