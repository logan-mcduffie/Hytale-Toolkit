package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;

public class DefaultDatagramChannelConfig extends DefaultChannelConfig implements DatagramChannelConfig {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDatagramChannelConfig.class);
   private final DatagramSocket javaSocket;
   private volatile boolean activeOnOpen;

   public DefaultDatagramChannelConfig(DatagramChannel channel, DatagramSocket javaSocket) {
      super(channel, new FixedRecvByteBufAllocator(2048));
      this.javaSocket = ObjectUtil.checkNotNull(javaSocket, "javaSocket");
   }

   protected final DatagramSocket javaSocket() {
      return this.javaSocket;
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(
         super.getOptions(),
         ChannelOption.SO_BROADCAST,
         ChannelOption.SO_RCVBUF,
         ChannelOption.SO_SNDBUF,
         ChannelOption.SO_REUSEADDR,
         ChannelOption.IP_MULTICAST_LOOP_DISABLED,
         ChannelOption.IP_MULTICAST_ADDR,
         ChannelOption.IP_MULTICAST_IF,
         ChannelOption.IP_MULTICAST_TTL,
         ChannelOption.IP_TOS,
         ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION
      );
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      if (option == ChannelOption.SO_BROADCAST) {
         return (T)this.isBroadcast();
      } else if (option == ChannelOption.SO_RCVBUF) {
         return (T)this.getReceiveBufferSize();
      } else if (option == ChannelOption.SO_SNDBUF) {
         return (T)this.getSendBufferSize();
      } else if (option == ChannelOption.SO_REUSEADDR) {
         return (T)this.isReuseAddress();
      } else if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         return (T)this.isLoopbackModeDisabled();
      } else if (option == ChannelOption.IP_MULTICAST_ADDR) {
         return (T)this.getInterface();
      } else if (option == ChannelOption.IP_MULTICAST_IF) {
         return (T)this.getNetworkInterface();
      } else if (option == ChannelOption.IP_MULTICAST_TTL) {
         return (T)this.getTimeToLive();
      } else if (option == ChannelOption.IP_TOS) {
         return (T)this.getTrafficClass();
      } else {
         return (T)(option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION ? this.activeOnOpen : super.getOption(option));
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      this.validate(option, value);
      if (option == ChannelOption.SO_BROADCAST) {
         this.setBroadcast((Boolean)value);
      } else if (option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)value);
      } else if (option == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)value);
      } else if (option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)value);
      } else if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         this.setLoopbackModeDisabled((Boolean)value);
      } else if (option == ChannelOption.IP_MULTICAST_ADDR) {
         this.setInterface((InetAddress)value);
      } else if (option == ChannelOption.IP_MULTICAST_IF) {
         this.setNetworkInterface((NetworkInterface)value);
      } else if (option == ChannelOption.IP_MULTICAST_TTL) {
         this.setTimeToLive((Integer)value);
      } else if (option == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)value);
      } else {
         if (option != ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            return super.setOption(option, value);
         }

         this.setActiveOnOpen((Boolean)value);
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

   @Override
   public boolean isBroadcast() {
      try {
         return this.javaSocket.getBroadcast();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public DatagramChannelConfig setBroadcast(boolean broadcast) {
      try {
         if (broadcast && !this.javaSocket.getLocalAddress().isAnyLocalAddress() && !PlatformDependent.isWindows() && !PlatformDependent.maybeSuperUser()) {
            logger.warn(
               "A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; setting the SO_BROADCAST flag anyway as requested on the socket which is bound to "
                  + this.javaSocket.getLocalSocketAddress()
                  + '.'
            );
         }

         this.javaSocket.setBroadcast(broadcast);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public InetAddress getInterface() {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            return ((MulticastSocket)this.javaSocket).getInterface();
         } catch (SocketException var2) {
            throw new ChannelException(var2);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            ((MulticastSocket)this.javaSocket).setInterface(interfaceAddress);
            return this;
         } catch (SocketException var3) {
            throw new ChannelException(var3);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean isLoopbackModeDisabled() {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            return ((MulticastSocket)this.javaSocket).getLoopbackMode();
         } catch (SocketException var2) {
            throw new ChannelException(var2);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            ((MulticastSocket)this.javaSocket).setLoopbackMode(loopbackModeDisabled);
            return this;
         } catch (SocketException var3) {
            throw new ChannelException(var3);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public NetworkInterface getNetworkInterface() {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            return ((MulticastSocket)this.javaSocket).getNetworkInterface();
         } catch (SocketException var2) {
            throw new ChannelException(var2);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            ((MulticastSocket)this.javaSocket).setNetworkInterface(networkInterface);
            return this;
         } catch (SocketException var3) {
            throw new ChannelException(var3);
         }
      } else {
         throw new UnsupportedOperationException();
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
   public DatagramChannelConfig setReuseAddress(boolean reuseAddress) {
      try {
         this.javaSocket.setReuseAddress(reuseAddress);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
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
   public DatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         this.javaSocket.setReceiveBufferSize(receiveBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
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
   public DatagramChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         this.javaSocket.setSendBufferSize(sendBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getTimeToLive() {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            return ((MulticastSocket)this.javaSocket).getTimeToLive();
         } catch (IOException var2) {
            throw new ChannelException(var2);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public DatagramChannelConfig setTimeToLive(int ttl) {
      if (this.javaSocket instanceof MulticastSocket) {
         try {
            ((MulticastSocket)this.javaSocket).setTimeToLive(ttl);
            return this;
         } catch (IOException var3) {
            throw new ChannelException(var3);
         }
      } else {
         throw new UnsupportedOperationException();
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
   public DatagramChannelConfig setTrafficClass(int trafficClass) {
      try {
         this.javaSocket.setTrafficClass(trafficClass);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public DatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   @Override
   public DatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   @Override
   public DatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   @Override
   public DatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   @Override
   public DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   @Override
   public DatagramChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Override
   public DatagramChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public DatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public DatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   @Override
   public DatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   @Override
   public DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   public DatagramChannelConfig setMaxMessagesPerWrite(int maxMessagesPerWrite) {
      super.setMaxMessagesPerWrite(maxMessagesPerWrite);
      return this;
   }
}
