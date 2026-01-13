package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class DefaultSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig {
   protected final Socket javaSocket;
   private volatile boolean allowHalfClosure;

   public DefaultSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
      super(channel);
      this.javaSocket = ObjectUtil.checkNotNull(javaSocket, "javaSocket");
      if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
         try {
            this.setTcpNoDelay(true);
         } catch (Exception var4) {
         }
      }
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(),
         ChannelOption.SO_RCVBUF,
         ChannelOption.SO_SNDBUF,
         ChannelOption.TCP_NODELAY,
         ChannelOption.SO_KEEPALIVE,
         ChannelOption.SO_REUSEADDR,
         ChannelOption.SO_LINGER,
         ChannelOption.IP_TOS,
         ChannelOption.ALLOW_HALF_CLOSURE
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == ChannelOption.SO_RCVBUF) {
         return (T)this.getReceiveBufferSize();
      } else if (option == ChannelOption.SO_SNDBUF) {
         return (T)this.getSendBufferSize();
      } else if (option == ChannelOption.TCP_NODELAY) {
         return (T)this.isTcpNoDelay();
      } else if (option == ChannelOption.SO_KEEPALIVE) {
         return (T)this.isKeepAlive();
      } else if (option == ChannelOption.SO_REUSEADDR) {
         return (T)this.isReuseAddress();
      } else if (option == ChannelOption.SO_LINGER) {
         return (T)this.getSoLinger();
      } else if (option == ChannelOption.IP_TOS) {
         return (T)this.getTrafficClass();
      } else {
         return (T)(option == ChannelOption.ALLOW_HALF_CLOSURE ? this.isAllowHalfClosure() : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)value);
      } else if (option == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)value);
      } else if (option == ChannelOption.TCP_NODELAY) {
         this.setTcpNoDelay((Boolean)value);
      } else if (option == ChannelOption.SO_KEEPALIVE) {
         this.setKeepAlive((Boolean)value);
      } else if (option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)value);
      } else if (option == ChannelOption.SO_LINGER) {
         this.setSoLinger((Integer)value);
      } else if (option == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)value);
      } else {
         if (option != ChannelOption.ALLOW_HALF_CLOSURE) {
            return super.setOption(option, value);
         }

         this.setAllowHalfClosure((Boolean)value);
      }

      return true;
   }

   @Override
   public int getReceiveBufferSize() {
      try {
         return this.javaSocket.getReceiveBufferSize();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public int getSendBufferSize() {
      try {
         return this.javaSocket.getSendBufferSize();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public int getSoLinger() {
      try {
         return this.javaSocket.getSoLinger();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public int getTrafficClass() {
      try {
         return this.javaSocket.getTrafficClass();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public boolean isKeepAlive() {
      try {
         return this.javaSocket.getKeepAlive();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public boolean isReuseAddress() {
      try {
         return this.javaSocket.getReuseAddress();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public boolean isTcpNoDelay() {
      try {
         return this.javaSocket.getTcpNoDelay();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public SocketChannelConfig setKeepAlive(boolean keepAlive) {
      try {
         this.javaSocket.setKeepAlive(keepAlive);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      this.javaSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
      return this;
   }

   @Override
   public SocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         this.javaSocket.setReceiveBufferSize(receiveBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setReuseAddress(boolean reuseAddress) {
      try {
         this.javaSocket.setReuseAddress(reuseAddress);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         this.javaSocket.setSendBufferSize(sendBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setSoLinger(int soLinger) {
      try {
         if (soLinger < 0) {
            this.javaSocket.setSoLinger(false, 0);
         } else {
            this.javaSocket.setSoLinger(true, soLinger);
         }

         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
      try {
         this.javaSocket.setTcpNoDelay(tcpNoDelay);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public SocketChannelConfig setTrafficClass(int trafficClass) {
      try {
         this.javaSocket.setTrafficClass(trafficClass);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   @Override
   public SocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
      this.allowHalfClosure = allowHalfClosure;
      return this;
   }

   @Override
   public SocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   @Override
   public SocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   @Override
   public SocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   @Override
   public SocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   @Override
   public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   @Override
   public SocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Override
   public SocketChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public SocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public SocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   @Override
   public SocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   @Override
   public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
