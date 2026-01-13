package io.netty.channel.epoll;

import io.netty.channel.unix.Buffer;
import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class EpollEventArray {
   private static final int EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
   private static final int EPOLL_DATA_OFFSET = Native.offsetofEpollData();
   private CleanableDirectBuffer cleanable;
   private ByteBuffer memory;
   private long memoryAddress;
   private int length;

   EpollEventArray(int length) {
      if (length < 1) {
         throw new IllegalArgumentException("length must be >= 1 but was " + length);
      } else {
         this.length = length;
         this.cleanable = Buffer.allocateDirectBufferWithNativeOrder(calculateBufferCapacity(length));
         this.memory = this.cleanable.buffer();
         this.memoryAddress = Buffer.memoryAddress(this.memory);
      }
   }

   long memoryAddress() {
      return this.memoryAddress;
   }

   int length() {
      return this.length;
   }

   void increase() {
      this.length <<= 1;
      CleanableDirectBuffer buffer = Buffer.allocateDirectBufferWithNativeOrder(calculateBufferCapacity(this.length));
      this.cleanable.clean();
      this.cleanable = buffer;
      this.memory = buffer.buffer();
      this.memoryAddress = Buffer.memoryAddress(buffer.buffer());
   }

   void free() {
      this.cleanable.clean();
      this.memoryAddress = 0L;
   }

   int events(int index) {
      return this.getInt(index, 0);
   }

   int fd(int index) {
      return this.getInt(index, EPOLL_DATA_OFFSET);
   }

   private int getInt(int index, int offset) {
      if (PlatformDependent.hasUnsafe()) {
         long n = (long)index * EPOLL_EVENT_SIZE;
         return PlatformDependent.getInt(this.memoryAddress + n + offset);
      } else {
         return this.memory.getInt(index * EPOLL_EVENT_SIZE + offset);
      }
   }

   private static int calculateBufferCapacity(int capacity) {
      return capacity * EPOLL_EVENT_SIZE;
   }
}
