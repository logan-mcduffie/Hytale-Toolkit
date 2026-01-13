package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class SocketWritableByteChannel implements WritableByteChannel {
   protected final FileDescriptor fd;

   protected SocketWritableByteChannel(FileDescriptor fd) {
      this.fd = ObjectUtil.checkNotNull(fd, "fd");
   }

   protected int write(ByteBuffer buf, int pos, int limit) throws IOException {
      return this.fd.write(buf, pos, limit);
   }

   @Override
   public final int write(ByteBuffer src) throws IOException {
      int position = src.position();
      int limit = src.limit();
      int written;
      if (src.isDirect()) {
         written = this.write(src, position, src.limit());
      } else {
         int readableBytes = limit - position;
         ByteBuf buffer = null;

         try {
            if (readableBytes == 0) {
               buffer = Unpooled.EMPTY_BUFFER;
            } else {
               ByteBufAllocator alloc = this.alloc();
               if (alloc.isDirectBufferPooled()) {
                  buffer = alloc.directBuffer(readableBytes);
               } else {
                  buffer = ByteBufUtil.threadLocalDirectBuffer();
                  if (buffer == null) {
                     buffer = Unpooled.directBuffer(readableBytes);
                  }
               }
            }

            buffer.writeBytes(src.duplicate());
            ByteBuffer nioBuffer = buffer.internalNioBuffer(buffer.readerIndex(), readableBytes);
            written = this.write(nioBuffer, nioBuffer.position(), nioBuffer.limit());
         } finally {
            if (buffer != null) {
               buffer.release();
            }
         }
      }

      if (written > 0) {
         ((java.nio.Buffer)src).position(position + written);
      }

      return written;
   }

   @Override
   public final boolean isOpen() {
      return this.fd.isOpen();
   }

   @Override
   public final void close() throws IOException {
      this.fd.close();
   }

   protected abstract ByteBufAllocator alloc();
}
