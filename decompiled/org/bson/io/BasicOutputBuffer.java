package org.bson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import org.bson.ByteBuf;
import org.bson.ByteBufNIO;

public class BasicOutputBuffer extends OutputBuffer {
   private byte[] buffer;
   private int position;

   public BasicOutputBuffer() {
      this(1024);
   }

   public BasicOutputBuffer(int initialSize) {
      this.buffer = new byte[initialSize];
   }

   public byte[] getInternalBuffer() {
      return this.buffer;
   }

   @Override
   public void write(byte[] b) {
      this.ensureOpen();
      this.write(b, 0, b.length);
   }

   @Override
   public void writeBytes(byte[] bytes, int offset, int length) {
      this.ensureOpen();
      this.ensure(length);
      System.arraycopy(bytes, offset, this.buffer, this.position, length);
      this.position += length;
   }

   @Override
   public void writeByte(int value) {
      this.ensureOpen();
      this.ensure(1);
      this.buffer[this.position++] = (byte)(0xFF & value);
   }

   @Override
   protected void write(int absolutePosition, int value) {
      this.ensureOpen();
      if (absolutePosition < 0) {
         throw new IllegalArgumentException(String.format("position must be >= 0 but was %d", absolutePosition));
      } else if (absolutePosition > this.position - 1) {
         throw new IllegalArgumentException(String.format("position must be <= %d but was %d", this.position - 1, absolutePosition));
      } else {
         this.buffer[absolutePosition] = (byte)(0xFF & value);
      }
   }

   @Override
   public int getPosition() {
      this.ensureOpen();
      return this.position;
   }

   @Override
   public int getSize() {
      this.ensureOpen();
      return this.position;
   }

   @Override
   public int pipe(OutputStream out) throws IOException {
      this.ensureOpen();
      out.write(this.buffer, 0, this.position);
      return this.position;
   }

   @Override
   public void truncateToPosition(int newPosition) {
      this.ensureOpen();
      if (newPosition <= this.position && newPosition >= 0) {
         this.position = newPosition;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public List<ByteBuf> getByteBuffers() {
      this.ensureOpen();
      return Arrays.asList(new ByteBufNIO(ByteBuffer.wrap(this.buffer, 0, this.position).duplicate().order(ByteOrder.LITTLE_ENDIAN)));
   }

   @Override
   public void close() {
      this.buffer = null;
   }

   private void ensureOpen() {
      if (this.buffer == null) {
         throw new IllegalStateException("The output is closed");
      }
   }

   private void ensure(int more) {
      int need = this.position + more;
      if (need > this.buffer.length) {
         int newSize = this.buffer.length * 2;
         if (newSize < need) {
            newSize = need + 128;
         }

         byte[] n = new byte[newSize];
         System.arraycopy(this.buffer, 0, n, 0, this.position);
         this.buffer = n;
      }
   }
}
