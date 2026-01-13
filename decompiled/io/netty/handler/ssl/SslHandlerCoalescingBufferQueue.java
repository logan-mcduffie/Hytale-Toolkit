package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;

abstract class SslHandlerCoalescingBufferQueue extends AbstractCoalescingBufferQueue {
   private final boolean wantsDirectBuffer;

   SslHandlerCoalescingBufferQueue(Channel channel, int initSize, boolean wantsDirectBuffer) {
      super(channel, initSize);
      this.wantsDirectBuffer = wantsDirectBuffer;
   }

   protected abstract int wrapDataSize();

   @Override
   protected ByteBuf compose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
      return attemptCopyToCumulation(cumulation, next, this.wrapDataSize()) ? cumulation : this.copyAndCompose(alloc, cumulation, next);
   }

   @Override
   protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first, int bufferSize) {
      ByteBuf newFirst;
      if (this.wantsDirectBuffer) {
         newFirst = allocator.directBuffer(bufferSize);
      } else {
         newFirst = allocator.heapBuffer(bufferSize);
      }

      try {
         newFirst.writeBytes(first);
      } catch (Throwable var6) {
         newFirst.release();
         PlatformDependent.throwException(var6);
      }

      assert !first.isReadable();

      first.release();
      return newFirst;
   }

   @Override
   protected ByteBuf removeEmptyValue() {
      return null;
   }

   private static boolean attemptCopyToCumulation(ByteBuf cumulation, ByteBuf next, int wrapDataSize) {
      int inReadableBytes = next.readableBytes();
      if (inReadableBytes == 0) {
         next.release();
         return true;
      } else {
         int cumulationCapacity = cumulation.capacity();
         if (wrapDataSize - cumulation.readableBytes() < inReadableBytes
            || (!cumulation.isWritable(inReadableBytes) || cumulationCapacity < wrapDataSize)
               && (cumulationCapacity >= wrapDataSize || !ByteBufUtil.ensureWritableSuccess(cumulation.ensureWritable(inReadableBytes, false)))) {
            return false;
         } else {
            cumulation.writeBytes(next);
            next.release();
            return true;
         }
      }
   }
}
