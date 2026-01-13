package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import java.io.IOException;
import java.util.Map;

public final class KQueueDomainDatagramChannelConfig extends KQueueChannelConfig implements DomainDatagramChannelConfig {
   private boolean activeOnOpen;

   KQueueDomainDatagramChannelConfig(KQueueDomainDatagramChannel channel) {
      super(channel, new FixedRecvByteBufAllocator(2048));
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, ChannelOption.SO_SNDBUF);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         return (T)this.activeOnOpen;
      } else {
         return (T)(option == ChannelOption.SO_SNDBUF ? this.getSendBufferSize() : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen((Boolean)value);
      } else {
         if (option != ChannelOption.SO_SNDBUF) {
            return super.setOption(option, value);
         }

         this.setSendBufferSize((Integer)value);
      }

      return true;
   }

   private void setActiveOnOpen(boolean activeOnOpen) {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("Can only changed before channel was registered");
      } else {
         this.activeOnOpen = activeOnOpen;
      }
   }

   boolean getActiveOnOpen() {
      return this.activeOnOpen;
   }

   public KQueueDomainDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public KQueueDomainDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setMaxMessagesPerWrite(int maxMessagesPerWrite) {
      super.setMaxMessagesPerWrite(maxMessagesPerWrite);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
      super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         ((KQueueDomainDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getSendBufferSize() {
      try {
         return ((KQueueDomainDatagramChannel)this.channel).socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public KQueueDomainDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public KQueueDomainDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }
}
