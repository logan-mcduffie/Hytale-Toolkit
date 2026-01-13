package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

final class IterableByteBufferInputStream extends InputStream {
   private Iterator<ByteBuffer> iterator;
   private ByteBuffer currentByteBuffer;
   private int dataSize;
   private int currentIndex;
   private int currentByteBufferPos;
   private boolean hasArray;
   private byte[] currentArray;
   private int currentArrayOffset;
   private long currentAddress;

   IterableByteBufferInputStream(Iterable<ByteBuffer> data) {
      this.iterator = data.iterator();
      this.dataSize = 0;

      for (ByteBuffer unused : data) {
         this.dataSize++;
      }

      this.currentIndex = -1;
      if (!this.getNextByteBuffer()) {
         this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
         this.currentIndex = 0;
         this.currentByteBufferPos = 0;
         this.currentAddress = 0L;
      }
   }

   private boolean getNextByteBuffer() {
      do {
         this.currentIndex++;
         if (!this.iterator.hasNext()) {
            return false;
         }

         this.currentByteBuffer = this.iterator.next();
      } while (!this.currentByteBuffer.hasRemaining());

      this.currentByteBufferPos = this.currentByteBuffer.position();
      if (this.currentByteBuffer.hasArray()) {
         this.hasArray = true;
         this.currentArray = this.currentByteBuffer.array();
         this.currentArrayOffset = this.currentByteBuffer.arrayOffset();
      } else {
         this.hasArray = false;
         this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
         this.currentArray = null;
      }

      return true;
   }

   private void updateCurrentByteBufferPos(int numberOfBytesRead) {
      this.currentByteBufferPos += numberOfBytesRead;
      if (this.currentByteBufferPos == this.currentByteBuffer.limit()) {
         this.getNextByteBuffer();
      }
   }

   @Override
   public int read() throws IOException {
      if (this.currentIndex == this.dataSize) {
         return -1;
      } else if (this.hasArray) {
         int result = this.currentArray[this.currentByteBufferPos + this.currentArrayOffset] & 255;
         this.updateCurrentByteBufferPos(1);
         return result;
      } else {
         int result = UnsafeUtil.getByte(this.currentByteBufferPos + this.currentAddress) & 255;
         this.updateCurrentByteBufferPos(1);
         return result;
      }
   }

   @Override
   public int read(byte[] output, int offset, int length) throws IOException {
      if (this.currentIndex == this.dataSize) {
         return -1;
      } else {
         int remaining = this.currentByteBuffer.limit() - this.currentByteBufferPos;
         if (length > remaining) {
            length = remaining;
         }

         if (this.hasArray) {
            System.arraycopy(this.currentArray, this.currentByteBufferPos + this.currentArrayOffset, output, offset, length);
            this.updateCurrentByteBufferPos(length);
         } else {
            int prevPos = this.currentByteBuffer.position();
            Java8Compatibility.position(this.currentByteBuffer, this.currentByteBufferPos);
            this.currentByteBuffer.get(output, offset, length);
            Java8Compatibility.position(this.currentByteBuffer, prevPos);
            this.updateCurrentByteBufferPos(length);
         }

         return length;
      }
   }
}
