package io.netty.util.internal;

import java.nio.ByteBuffer;

final class DirectCleaner implements Cleaner {
   @Override
   public CleanableDirectBuffer allocate(int capacity) {
      return new DirectCleaner.CleanableDirectBufferImpl(PlatformDependent.allocateDirectNoCleaner(capacity));
   }

   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      PlatformDependent.freeDirectNoCleaner(buffer);
   }

   CleanableDirectBuffer reallocate(CleanableDirectBuffer buffer, int capacity) {
      ByteBuffer newByteBuffer = PlatformDependent.reallocateDirectNoCleaner(buffer.buffer(), capacity);
      return new DirectCleaner.CleanableDirectBufferImpl(newByteBuffer);
   }

   private static final class CleanableDirectBufferImpl implements CleanableDirectBuffer {
      private final ByteBuffer buffer;

      private CleanableDirectBufferImpl(ByteBuffer buffer) {
         this.buffer = buffer;
      }

      @Override
      public ByteBuffer buffer() {
         return this.buffer;
      }

      @Override
      public void clean() {
         PlatformDependent.freeDirectNoCleaner(this.buffer);
      }
   }
}
