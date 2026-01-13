package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollServerSocketChannelConfig extends EpollServerChannelConfig implements ServerSocketChannelConfig {
   EpollServerSocketChannelConfig(EpollServerSocketChannel channel) {
      super(channel);
      this.setReuseAddress(true);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(),
         EpollChannelOption.SO_REUSEPORT,
         EpollChannelOption.IP_FREEBIND,
         EpollChannelOption.IP_TRANSPARENT,
         EpollChannelOption.TCP_DEFER_ACCEPT
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == EpollChannelOption.SO_REUSEPORT) {
         return (T)this.isReusePort();
      } else if (option == EpollChannelOption.IP_FREEBIND) {
         return (T)this.isFreeBind();
      } else if (option == EpollChannelOption.IP_TRANSPARENT) {
         return (T)this.isIpTransparent();
      } else {
         return (T)(option == EpollChannelOption.TCP_DEFER_ACCEPT ? this.getTcpDeferAccept() : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == EpollChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)value);
      } else if (option == EpollChannelOption.IP_FREEBIND) {
         this.setFreeBind((Boolean)value);
      } else if (option == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)value);
      } else if (option == EpollChannelOption.TCP_MD5SIG) {
         Map<InetAddress, byte[]> m = (Map<InetAddress, byte[]>)value;
         this.setTcpMd5Sig(m);
      } else {
         if (option != EpollChannelOption.TCP_DEFER_ACCEPT) {
            return super.setOption(option, value);
         }

         this.setTcpDeferAccept((Integer)value);
      }

      return true;
   }

   public EpollServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      super.setReuseAddress(reuseAddress);
      return this;
   }

   public EpollServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      super.setReceiveBufferSize(receiveBufferSize);
      return this;
   }

   public EpollServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      return this;
   }

   public EpollServerSocketChannelConfig setBacklog(int backlog) {
      super.setBacklog(backlog);
      return this;
   }

   public EpollServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public EpollServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public EpollServerSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Deprecated
   public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   @Deprecated
   public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public EpollServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   public EpollServerSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> keys) {
      try {
         ((EpollServerSocketChannel)this.channel).setTcpMd5Sig(keys);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isReusePort() {
      try {
         return ((EpollServerSocketChannel)this.channel).socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerSocketChannelConfig setReusePort(boolean reusePort) {
      try {
         ((EpollServerSocketChannel)this.channel).socket.setReusePort(reusePort);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isFreeBind() {
      try {
         return ((EpollServerSocketChannel)this.channel).socket.isIpFreeBind();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerSocketChannelConfig setFreeBind(boolean freeBind) {
      try {
         ((EpollServerSocketChannel)this.channel).socket.setIpFreeBind(freeBind);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpTransparent() {
      try {
         return ((EpollServerSocketChannel)this.channel).socket.isIpTransparent();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerSocketChannelConfig setIpTransparent(boolean transparent) {
      try {
         ((EpollServerSocketChannel)this.channel).socket.setIpTransparent(transparent);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollServerSocketChannelConfig setTcpDeferAccept(int deferAccept) {
      try {
         ((EpollServerSocketChannel)this.channel).socket.setTcpDeferAccept(deferAccept);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getTcpDeferAccept() {
      try {
         return ((EpollServerSocketChannel)this.channel).socket.getTcpDeferAccept();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }
}
