package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

class EpollRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle {
   private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
   private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      @Override
      public boolean get() {
         return EpollRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
   };
   private boolean receivedRdHup;

   EpollRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle handle) {
      super(handle);
   }

   final void receivedRdHup() {
      this.receivedRdHup = true;
   }

   final boolean isReceivedRdHup() {
      return this.receivedRdHup;
   }

   boolean maybeMoreDataToRead() {
      return this.lastBytesRead() == this.attemptedBytesRead();
   }

   @Override
   public final ByteBuf allocate(ByteBufAllocator alloc) {
      this.preferredDirectByteBufAllocator.updateAllocator(alloc);
      return this.delegate().allocate(this.preferredDirectByteBufAllocator);
   }

   @Override
   public final boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
      return this.isReceivedRdHup() || ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading(maybeMoreDataSupplier);
   }

   @Override
   public final boolean continueReading() {
      return this.continueReading(this.defaultMaybeMoreDataSupplier);
   }
}
