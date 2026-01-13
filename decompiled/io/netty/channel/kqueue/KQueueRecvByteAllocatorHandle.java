package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

final class KQueueRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle {
   private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
   private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      @Override
      public boolean get() {
         return KQueueRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
   };
   private boolean readEOF;
   private long numberBytesPending;

   KQueueRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle handle) {
      super(handle);
   }

   @Override
   public ByteBuf allocate(ByteBufAllocator alloc) {
      this.preferredDirectByteBufAllocator.updateAllocator(alloc);
      return this.delegate().allocate(this.preferredDirectByteBufAllocator);
   }

   @Override
   public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
      return this.readEOF || ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading(maybeMoreDataSupplier);
   }

   @Override
   public boolean continueReading() {
      return this.continueReading(this.defaultMaybeMoreDataSupplier);
   }

   void readEOF() {
      this.readEOF = true;
   }

   void numberBytesPending(long numberBytesPending) {
      this.numberBytesPending = numberBytesPending;
   }

   private boolean maybeMoreDataToRead() {
      return this.lastBytesRead() == this.attemptedBytesRead();
   }
}
