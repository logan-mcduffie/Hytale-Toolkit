package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

public interface RecvByteBufAllocator {
   RecvByteBufAllocator.Handle newHandle();

   public static class DelegatingHandle implements RecvByteBufAllocator.Handle {
      private final RecvByteBufAllocator.Handle delegate;

      public DelegatingHandle(RecvByteBufAllocator.Handle delegate) {
         this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
      }

      protected final RecvByteBufAllocator.Handle delegate() {
         return this.delegate;
      }

      @Override
      public ByteBuf allocate(ByteBufAllocator alloc) {
         return this.delegate.allocate(alloc);
      }

      @Override
      public int guess() {
         return this.delegate.guess();
      }

      @Override
      public void reset(ChannelConfig config) {
         this.delegate.reset(config);
      }

      @Override
      public void incMessagesRead(int numMessages) {
         this.delegate.incMessagesRead(numMessages);
      }

      @Override
      public void lastBytesRead(int bytes) {
         this.delegate.lastBytesRead(bytes);
      }

      @Override
      public int lastBytesRead() {
         return this.delegate.lastBytesRead();
      }

      @Override
      public boolean continueReading() {
         return this.delegate.continueReading();
      }

      @Override
      public int attemptedBytesRead() {
         return this.delegate.attemptedBytesRead();
      }

      @Override
      public void attemptedBytesRead(int bytes) {
         this.delegate.attemptedBytesRead(bytes);
      }

      @Override
      public void readComplete() {
         this.delegate.readComplete();
      }
   }

   public interface ExtendedHandle extends RecvByteBufAllocator.Handle {
      boolean continueReading(UncheckedBooleanSupplier var1);
   }

   @Deprecated
   public interface Handle {
      ByteBuf allocate(ByteBufAllocator var1);

      int guess();

      void reset(ChannelConfig var1);

      void incMessagesRead(int var1);

      void lastBytesRead(int var1);

      int lastBytesRead();

      void attemptedBytesRead(int var1);

      int attemptedBytesRead();

      boolean continueReading();

      void readComplete();
   }
}
