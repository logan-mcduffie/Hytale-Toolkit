package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.UnixChannelOption;
import java.io.IOException;
import java.util.Map;

public class KQueueServerSocketChannelConfig extends KQueueServerChannelConfig {
   KQueueServerSocketChannelConfig(KQueueServerSocketChannel channel) {
      super(channel);
      this.setReuseAddress(true);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), UnixChannelOption.SO_REUSEPORT, KQueueChannelOption.SO_ACCEPTFILTER);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == UnixChannelOption.SO_REUSEPORT) {
         return (T)this.isReusePort();
      } else {
         return (T)(option == KQueueChannelOption.SO_ACCEPTFILTER ? this.getAcceptFilter() : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == UnixChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)value);
      } else {
         if (option != KQueueChannelOption.SO_ACCEPTFILTER) {
            return super.setOption(option, value);
         }

         this.setAcceptFilter((AcceptFilter)value);
      }

      return true;
   }

   public KQueueServerSocketChannelConfig setReusePort(boolean reusePort) {
      try {
         ((KQueueServerSocketChannel)this.channel).socket.setReusePort(reusePort);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isReusePort() {
      try {
         return ((KQueueServerSocketChannel)this.channel).socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public KQueueServerSocketChannelConfig setAcceptFilter(AcceptFilter acceptFilter) {
      try {
         ((KQueueServerSocketChannel)this.channel).socket.setAcceptFilter(acceptFilter);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public AcceptFilter getAcceptFilter() {
      try {
         return ((KQueueServerSocketChannel)this.channel).socket.getAcceptFilter();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public KQueueServerSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
      super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
      return this;
   }

   public KQueueServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      super.setReuseAddress(reuseAddress);
      return this;
   }

   public KQueueServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      super.setReceiveBufferSize(receiveBufferSize);
      return this;
   }

   public KQueueServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      return this;
   }

   public KQueueServerSocketChannelConfig setBacklog(int backlog) {
      super.setBacklog(backlog);
      return this;
   }

   public KQueueServerSocketChannelConfig setTcpFastOpen(boolean enableTcpFastOpen) {
      super.setTcpFastOpen(enableTcpFastOpen);
      return this;
   }

   public KQueueServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public KQueueServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public KQueueServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public KQueueServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public KQueueServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public KQueueServerSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Deprecated
   public KQueueServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   @Deprecated
   public KQueueServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public KQueueServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public KQueueServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
