package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;

public class EpollChannelConfig extends DefaultChannelConfig {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EpollChannelConfig.class);
   private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

   protected EpollChannelConfig(Channel channel) {
      super(checkAbstractEpollChannel(channel));
   }

   protected EpollChannelConfig(Channel channel, RecvByteBufAllocator recvByteBufAllocator) {
      super(checkAbstractEpollChannel(channel), recvByteBufAllocator);
   }

   protected LinuxSocket socket() {
      return ((AbstractEpollChannel)this.channel).socket;
   }

   private static Channel checkAbstractEpollChannel(Channel channel) {
      if (!(channel instanceof AbstractEpollChannel)) {
         throw new IllegalArgumentException("channel is not AbstractEpollChannel: " + channel.getClass());
      } else {
         return channel;
      }
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), EpollChannelOption.EPOLL_MODE);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == EpollChannelOption.EPOLL_MODE) {
         return (T)this.getEpollMode();
      } else {
         try {
            if (option instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
               return (T)((AbstractEpollChannel)this.channel).socket.getIntOpt(opt.level(), opt.optname());
            }

            if (option instanceof RawUnixChannelOption) {
               RawUnixChannelOption opt = (RawUnixChannelOption)option;
               ByteBuffer out = ByteBuffer.allocate(opt.length());
               ((AbstractEpollChannel)this.channel).socket.getRawOpt(opt.level(), opt.optname(), out);
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
      if (option == EpollChannelOption.EPOLL_MODE) {
         this.setEpollMode((EpollMode)value);
         return true;
      } else {
         try {
            if (option instanceof IntegerUnixChannelOption) {
               IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
               ((AbstractEpollChannel)this.channel).socket.setIntOpt(opt.level(), opt.optname(), (Integer)value);
               return true;
            }

            if (option instanceof RawUnixChannelOption) {
               RawUnixChannelOption opt = (RawUnixChannelOption)option;
               ((AbstractEpollChannel)this.channel).socket.setRawOpt(opt.level(), opt.optname(), (ByteBuffer)value);
               return true;
            }
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }

         return super.setOption(option, value);
      }
   }

   public EpollChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public EpollChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
         throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
      } else {
         super.setRecvByteBufAllocator(allocator);
         return this;
      }
   }

   public EpollChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   @Deprecated
   public EpollChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   @Deprecated
   public EpollMode getEpollMode() {
      return EpollMode.LEVEL_TRIGGERED;
   }

   @Deprecated
   public EpollChannelConfig setEpollMode(EpollMode mode) {
      LOGGER.debug("Changing the EpollMode is not supported anymore, this is just a no-op");
      return this;
   }

   private void checkChannelNotRegistered() {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("EpollMode can only be changed before channel is registered");
      }
   }

   @Override
   protected final void autoReadCleared() {
      ((AbstractEpollChannel)this.channel).clearEpollIn();
   }

   protected final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
      this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
   }

   protected final long getMaxBytesPerGatheringWrite() {
      return this.maxBytesPerGatheringWrite;
   }
}
