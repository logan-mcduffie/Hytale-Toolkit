package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;

public class KQueueChannelConfig extends DefaultChannelConfig {
   private volatile boolean transportProvidesGuess;
   private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

   KQueueChannelConfig(AbstractKQueueChannel channel) {
      super(channel);
   }

   KQueueChannelConfig(AbstractKQueueChannel channel, RecvByteBufAllocator recvByteBufAllocator) {
      super(channel, recvByteBufAllocator);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS) {
         return (T)this.getRcvAllocTransportProvidesGuess();
      } else {
         try {
            if (option instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
               return (T)((AbstractKQueueChannel)this.channel).socket.getIntOpt(opt.level(), opt.optname());
            }

            if (option instanceof RawUnixChannelOption) {
               RawUnixChannelOption opt = (RawUnixChannelOption)option;
               ByteBuffer out = ByteBuffer.allocate(opt.length());
               ((AbstractKQueueChannel)this.channel).socket.getRawOpt(opt.level(), opt.optname(), out);
               return (T)((Buffer)out).flip();
            }
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }

         return super.getOption(option);
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS) {
         this.setRcvAllocTransportProvidesGuess((Boolean)value);
         return true;
      } else {
         try {
            if (option instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
               ((AbstractKQueueChannel)this.channel).socket.setIntOpt(opt.level(), opt.optname(), (Integer)value);
               return true;
            }

            if (option instanceof RawUnixChannelOption) {
               RawUnixChannelOption opt = (RawUnixChannelOption)option;
               ((AbstractKQueueChannel)this.channel).socket.setRawOpt(opt.level(), opt.optname(), (ByteBuffer)value);
               return true;
            }
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }

         return super.setOption(option, value);
      }
   }

   @Deprecated
   public KQueueChannelConfig setRcvAllocTransportProvidesGuess(boolean transportProvidesGuess) {
      this.transportProvidesGuess = transportProvidesGuess;
      return this;
   }

   @Deprecated
   public boolean getRcvAllocTransportProvidesGuess() {
      return this.transportProvidesGuess;
   }

   public KQueueChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public KQueueChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public KQueueChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public KQueueChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public KQueueChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
         throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
      } else {
         super.setRecvByteBufAllocator(allocator);
         return this;
      }
   }

   public KQueueChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Deprecated
   public KQueueChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   @Deprecated
   public KQueueChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public KQueueChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public KQueueChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   @Override
   protected final void autoReadCleared() {
      ((AbstractKQueueChannel)this.channel).clearReadFilter();
   }

   final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
      this.maxBytesPerGatheringWrite = Math.min(Limits.SSIZE_MAX, maxBytesPerGatheringWrite);
   }

   final long getMaxBytesPerGatheringWrite() {
      return this.maxBytesPerGatheringWrite;
   }
}
