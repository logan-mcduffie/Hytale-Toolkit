package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicChannelConfig extends DefaultChannelConfig implements QuicChannelConfig {
   private volatile QLogConfiguration qLogConfiguration;
   private volatile SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator = SegmentedDatagramPacketAllocator.NONE;

   QuicheQuicChannelConfig(Channel channel) {
      super(channel);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), QuicChannelOption.QLOG, QuicChannelOption.SEGMENTED_DATAGRAM_PACKET_ALLOCATOR);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == QuicChannelOption.QLOG) {
         return (T)this.getQLogConfiguration();
      } else {
         return (T)(option == QuicChannelOption.SEGMENTED_DATAGRAM_PACKET_ALLOCATOR ? this.getSegmentedDatagramPacketAllocator() : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      if (option == QuicChannelOption.QLOG) {
         this.setQLogConfiguration((QLogConfiguration)value);
         return true;
      } else if (option == QuicChannelOption.SEGMENTED_DATAGRAM_PACKET_ALLOCATOR) {
         this.setSegmentedDatagramPacketAllocator((SegmentedDatagramPacketAllocator)value);
         return true;
      } else {
         return super.setOption(option, value);
      }
   }

   @Override
   public QuicChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   @Override
   public QuicChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   @Override
   public QuicChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   @Override
   public QuicChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   @Override
   public QuicChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   @Override
   public QuicChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Override
   public QuicChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   @Override
   public QuicChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   @Override
   public QuicChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   @Override
   public QuicChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   @Override
   public QuicChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   @Nullable
   QLogConfiguration getQLogConfiguration() {
      return this.qLogConfiguration;
   }

   private void setQLogConfiguration(QLogConfiguration qLogConfiguration) {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("QLOG can only be enabled before the Channel was registered");
      } else {
         this.qLogConfiguration = qLogConfiguration;
      }
   }

   SegmentedDatagramPacketAllocator getSegmentedDatagramPacketAllocator() {
      return this.segmentedDatagramPacketAllocator;
   }

   private void setSegmentedDatagramPacketAllocator(SegmentedDatagramPacketAllocator segmentedDatagramPacketAllocator) {
      this.segmentedDatagramPacketAllocator = segmentedDatagramPacketAllocator;
   }
}
