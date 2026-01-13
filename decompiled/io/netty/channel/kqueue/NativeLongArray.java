package io.netty.channel.kqueue;

import io.netty.channel.unix.Buffer;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class NativeLongArray {
   private CleanableDirectBuffer memoryCleanable;
   private ByteBuffer memory;
   private long memoryAddress;
   private int capacity;
   private int size;

   NativeLongArray(int capacity) {
      this.capacity = ObjectUtil.checkPositive(capacity, "capacity");
      this.memoryCleanable = Buffer.allocateDirectBufferWithNativeOrder(calculateBufferCapacity(capacity));
      this.memory = this.memoryCleanable.buffer();
      this.memoryAddress = Buffer.memoryAddress(this.memory);
   }

   private static int idx(int index) {
      return index * Limits.SIZEOF_JLONG;
   }

   private static int calculateBufferCapacity(int capacity) {
      return capacity * Limits.SIZEOF_JLONG;
   }

   void add(long value) {
      this.reallocIfNeeded();
      if (PlatformDependent.hasUnsafe()) {
         PlatformDependent.putLong(this.memoryOffset(this.size), value);
      } else {
         this.memory.putLong(idx(this.size), value);
      }

      this.size++;
   }

   void clear() {
      this.size = 0;
   }

   boolean isEmpty() {
      return this.size == 0;
   }

   int size() {
      return this.size;
   }

   void free() {
      this.memoryCleanable.clean();
      this.memoryAddress = 0L;
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   long memoryAddressEnd() {
      return this.memoryOffset(this.size);
   }

   private long memoryOffset(int index) {
      return this.memoryAddress + idx(index);
   }

   private void reallocIfNeeded() {
      if (this.size == this.capacity) {
         int newLength = this.capacity <= 65536 ? this.capacity << 1 : this.capacity + this.capacity >> 1;
         int newCapacity = calculateBufferCapacity(newLength);
         CleanableDirectBuffer buffer = Buffer.allocateDirectBufferWithNativeOrder(newCapacity);
         ((java.nio.Buffer)this.memory).position(0).limit(this.size);
         buffer.buffer().put(this.memory);
         ((java.nio.Buffer)buffer.buffer()).position(0);
         this.memoryCleanable.clean();
         this.memoryCleanable = buffer;
         this.memory = buffer.buffer();
         this.memoryAddress = Buffer.memoryAddress(this.memory);
         this.capacity = newLength;
      }
   }

   @Override
   public String toString() {
      return "memoryAddress: " + this.memoryAddress + " capacity: " + this.capacity + " size: " + this.size;
   }
}
