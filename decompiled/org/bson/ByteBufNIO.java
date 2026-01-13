package org.bson;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteBufNIO implements ByteBuf {
   private ByteBuffer buf;
   private final AtomicInteger referenceCount = new AtomicInteger(1);

   public ByteBufNIO(ByteBuffer buf) {
      this.buf = buf.order(ByteOrder.LITTLE_ENDIAN);
   }

   @Override
   public int getReferenceCount() {
      return this.referenceCount.get();
   }

   public ByteBufNIO retain() {
      if (this.referenceCount.incrementAndGet() == 1) {
         this.referenceCount.decrementAndGet();
         throw new IllegalStateException("Attempted to increment the reference count when it is already 0");
      } else {
         return this;
      }
   }

   @Override
   public void release() {
      if (this.referenceCount.decrementAndGet() < 0) {
         this.referenceCount.incrementAndGet();
         throw new IllegalStateException("Attempted to decrement the reference count below 0");
      } else {
         if (this.referenceCount.get() == 0) {
            this.buf = null;
         }
      }
   }

   @Override
   public int capacity() {
      return this.buf.capacity();
   }

   @Override
   public ByteBuf put(int index, byte b) {
      this.buf.put(index, b);
      return this;
   }

   @Override
   public int remaining() {
      return this.buf.remaining();
   }

   @Override
   public ByteBuf put(byte[] src, int offset, int length) {
      this.buf.put(src, offset, length);
      return this;
   }

   @Override
   public boolean hasRemaining() {
      return this.buf.hasRemaining();
   }

   @Override
   public ByteBuf put(byte b) {
      this.buf.put(b);
      return this;
   }

   @Override
   public ByteBuf flip() {
      ((Buffer)this.buf).flip();
      return this;
   }

   @Override
   public byte[] array() {
      return this.buf.array();
   }

   @Override
   public int limit() {
      return this.buf.limit();
   }

   @Override
   public ByteBuf position(int newPosition) {
      ((Buffer)this.buf).position(newPosition);
      return this;
   }

   @Override
   public ByteBuf clear() {
      ((Buffer)this.buf).clear();
      return this;
   }

   @Override
   public ByteBuf order(ByteOrder byteOrder) {
      this.buf.order(byteOrder);
      return this;
   }

   @Override
   public byte get() {
      return this.buf.get();
   }

   @Override
   public byte get(int index) {
      return this.buf.get(index);
   }

   @Override
   public ByteBuf get(byte[] bytes) {
      this.buf.get(bytes);
      return this;
   }

   @Override
   public ByteBuf get(int index, byte[] bytes) {
      return this.get(index, bytes, 0, bytes.length);
   }

   @Override
   public ByteBuf get(byte[] bytes, int offset, int length) {
      this.buf.get(bytes, offset, length);
      return this;
   }

   @Override
   public ByteBuf get(int index, byte[] bytes, int offset, int length) {
      for (int i = 0; i < length; i++) {
         bytes[offset + i] = this.buf.get(index + i);
      }

      return this;
   }

   @Override
   public long getLong() {
      return this.buf.getLong();
   }

   @Override
   public long getLong(int index) {
      return this.buf.getLong(index);
   }

   @Override
   public double getDouble() {
      return this.buf.getDouble();
   }

   @Override
   public double getDouble(int index) {
      return this.buf.getDouble(index);
   }

   @Override
   public int getInt() {
      return this.buf.getInt();
   }

   @Override
   public int getInt(int index) {
      return this.buf.getInt(index);
   }

   @Override
   public int position() {
      return this.buf.position();
   }

   @Override
   public ByteBuf limit(int newLimit) {
      ((Buffer)this.buf).limit(newLimit);
      return this;
   }

   @Override
   public ByteBuf asReadOnly() {
      return new ByteBufNIO(this.buf.asReadOnlyBuffer());
   }

   @Override
   public ByteBuf duplicate() {
      return new ByteBufNIO(this.buf.duplicate());
   }

   @Override
   public ByteBuffer asNIO() {
      return this.buf;
   }
}
