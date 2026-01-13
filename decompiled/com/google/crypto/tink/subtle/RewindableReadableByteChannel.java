package com.google.crypto.tink.subtle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import javax.annotation.concurrent.GuardedBy;

public final class RewindableReadableByteChannel implements ReadableByteChannel {
   @GuardedBy("this")
   final ReadableByteChannel baseChannel;
   @GuardedBy("this")
   ByteBuffer buffer;
   @GuardedBy("this")
   boolean canRewind;
   @GuardedBy("this")
   boolean directRead;

   public RewindableReadableByteChannel(ReadableByteChannel baseChannel) {
      this.baseChannel = baseChannel;
      this.buffer = null;
      this.canRewind = true;
      this.directRead = false;
   }

   public synchronized void disableRewinding() {
      this.canRewind = false;
   }

   public synchronized void rewind() throws IOException {
      if (!this.canRewind) {
         throw new IOException("Cannot rewind anymore.");
      } else {
         if (this.buffer != null) {
            this.buffer.position(0);
         }
      }
   }

   private synchronized void setBufferLimit(int newLimit) {
      if (this.buffer.capacity() < newLimit) {
         int pos = this.buffer.position();
         int newBufferCapacity = Math.max(2 * this.buffer.capacity(), newLimit);
         ByteBuffer newBuffer = ByteBuffer.allocate(newBufferCapacity);
         this.buffer.rewind();
         newBuffer.put(this.buffer);
         newBuffer.position(pos);
         this.buffer = newBuffer;
      }

      this.buffer.limit(newLimit);
   }

   @Override
   public synchronized int read(ByteBuffer dst) throws IOException {
      if (this.directRead) {
         return this.baseChannel.read(dst);
      } else {
         int bytesToReadCount = dst.remaining();
         if (bytesToReadCount == 0) {
            return 0;
         } else if (this.buffer == null) {
            if (!this.canRewind) {
               this.directRead = true;
               return this.baseChannel.read(dst);
            } else {
               this.buffer = ByteBuffer.allocate(bytesToReadCount);
               int baseReadResult = this.baseChannel.read(this.buffer);
               this.buffer.flip();
               if (baseReadResult > 0) {
                  dst.put(this.buffer);
               }

               return baseReadResult;
            }
         } else if (this.buffer.remaining() >= bytesToReadCount) {
            int limit = this.buffer.limit();
            this.buffer.limit(this.buffer.position() + bytesToReadCount);
            dst.put(this.buffer);
            this.buffer.limit(limit);
            if (!this.canRewind && !this.buffer.hasRemaining()) {
               this.buffer = null;
               this.directRead = true;
            }

            return bytesToReadCount;
         } else {
            int bytesFromBufferCount = this.buffer.remaining();
            int stillToReadCount = bytesToReadCount - bytesFromBufferCount;
            int currentReadPos = this.buffer.position();
            int contentLimit = this.buffer.limit();
            this.setBufferLimit(contentLimit + stillToReadCount);
            this.buffer.position(contentLimit);
            int baseReadResult = this.baseChannel.read(this.buffer);
            this.buffer.flip();
            this.buffer.position(currentReadPos);
            dst.put(this.buffer);
            if (bytesFromBufferCount == 0 && baseReadResult < 0) {
               return -1;
            } else {
               int bytesCount = this.buffer.position() - currentReadPos;
               if (!this.canRewind && !this.buffer.hasRemaining()) {
                  this.buffer = null;
                  this.directRead = true;
               }

               return bytesCount;
            }
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.canRewind = false;
      this.directRead = true;
      this.baseChannel.close();
   }

   @Override
   public synchronized boolean isOpen() {
      return this.baseChannel.isOpen();
   }
}
