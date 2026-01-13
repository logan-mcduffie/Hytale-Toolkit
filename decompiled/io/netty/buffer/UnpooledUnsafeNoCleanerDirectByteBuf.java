package io.netty.buffer;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

class UnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
   UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity, boolean allowSectionedInternalNioBufferAccess) {
      super(alloc, initialCapacity, maxCapacity, allowSectionedInternalNioBufferAccess);
   }

   @Override
   protected CleanableDirectBuffer allocateDirectBuffer(int capacity) {
      return PlatformDependent.allocateDirectBufferNoCleaner(capacity);
   }

   @Override
   protected ByteBuffer allocateDirect(int initialCapacity) {
      throw new UnsupportedOperationException();
   }

   CleanableDirectBuffer reallocateDirect(CleanableDirectBuffer oldBuffer, int initialCapacity) {
      return PlatformDependent.reallocateDirectBufferNoCleaner(oldBuffer, initialCapacity);
   }

   @Override
   protected void freeDirect(ByteBuffer buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ByteBuf capacity(int newCapacity) {
      this.checkNewCapacity(newCapacity);
      int oldCapacity = this.capacity();
      if (newCapacity == oldCapacity) {
         return this;
      } else {
         this.trimIndicesToCapacity(newCapacity);
         this.setByteBuffer(this.reallocateDirect(this.cleanable, newCapacity), false);
         return this;
      }
   }
}
