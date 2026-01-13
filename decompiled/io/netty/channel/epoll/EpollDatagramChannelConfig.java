package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig extends EpollChannelConfig implements DatagramChannelConfig {
   private boolean activeOnOpen;
   private volatile int maxDatagramSize;
   private volatile boolean gro;

   EpollDatagramChannelConfig(EpollDatagramChannel channel) {
      super(channel, new FixedRecvByteBufAllocator(2048));
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
         ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION,
         EpollChannelOption.SO_REUSEPORT,
         EpollChannelOption.IP_FREEBIND,
         EpollChannelOption.IP_TRANSPARENT,
         EpollChannelOption.IP_RECVORIGDSTADDR,
         EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE,
         EpollChannelOption.UDP_GRO,
         EpollChannelOption.IP_MULTICAST_ALL
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
      } else if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         return (T)this.activeOnOpen;
      } else if (option == EpollChannelOption.SO_REUSEPORT) {
         return (T)this.isReusePort();
      } else if (option == EpollChannelOption.IP_TRANSPARENT) {
         return (T)this.isIpTransparent();
      } else if (option == EpollChannelOption.IP_FREEBIND) {
         return (T)this.isFreeBind();
      } else if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
         return (T)this.isIpRecvOrigDestAddr();
      } else if (option == EpollChannelOption.IP_MULTICAST_ALL) {
         return (T)this.isIpMulticastAll();
      } else if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
         return (T)this.getMaxDatagramPayloadSize();
      } else {
         return (T)(option == EpollChannelOption.UDP_GRO ? this.isUdpGro() : super.getOption(option));
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
      } else if (option == EpollChannelOption.IP_MULTICAST_ALL) {
         this.setIpMulticastAll((Boolean)value);
      } else if (option == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)value);
      } else if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen((Boolean)value);
      } else if (option == EpollChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)value);
      } else if (option == EpollChannelOption.IP_FREEBIND) {
         this.setFreeBind((Boolean)value);
      } else if (option == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)value);
      } else if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
         this.setIpRecvOrigDestAddr((Boolean)value);
      } else if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
         this.setMaxDatagramPayloadSize((Integer)value);
      } else {
         if (option != EpollChannelOption.UDP_GRO) {
            return super.setOption(option, value);
         }

         this.setUdpGro((Boolean)value);
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

   public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public EpollDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
      super.setWriteBufferWaterMark(writeBufferWaterMark);
      return this;
   }

   public EpollDatagramChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public EpollDatagramChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public EpollDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   @Deprecated
   public EpollDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   @Override
   public int getSendBufferSize() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getReceiveBufferSize() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getTrafficClass() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getTrafficClass();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setTrafficClass(int trafficClass) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setTrafficClass(trafficClass);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isReuseAddress() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReuseAddress(reuseAddress);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isBroadcast() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isBroadcast();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setBroadcast(boolean broadcast) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setBroadcast(broadcast);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public boolean isLoopbackModeDisabled() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isLoopbackModeDisabled();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setLoopbackModeDisabled(loopbackModeDisabled);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public int getTimeToLive() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getTimeToLive();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setTimeToLive(int ttl) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setTimeToLive(ttl);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public InetAddress getInterface() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getInterface();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setInterface(interfaceAddress);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public NetworkInterface getNetworkInterface() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.getNetworkInterface();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
      try {
         EpollDatagramChannel datagramChannel = (EpollDatagramChannel)this.channel;
         datagramChannel.socket.setNetworkInterface(networkInterface);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollDatagramChannelConfig setEpollMode(EpollMode mode) {
      super.setEpollMode(mode);
      return this;
   }

   public boolean isReusePort() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReusePort(boolean reusePort) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setReusePort(reusePort);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpTransparent() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpTransparent();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpTransparent(boolean ipTransparent) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpTransparent(ipTransparent);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isFreeBind() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpFreeBind();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setFreeBind(boolean freeBind) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpFreeBind(freeBind);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpRecvOrigDestAddr() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpRecvOrigDestAddr();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpRecvOrigDestAddr(boolean ipTransparent) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpRecvOrigDestAddr(ipTransparent);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpMulticastAll() {
      try {
         return ((EpollDatagramChannel)this.channel).socket.isIpMulticastAll();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpMulticastAll(boolean multicastAll) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setIpMulticastAll(multicastAll);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollDatagramChannelConfig setMaxDatagramPayloadSize(int maxDatagramSize) {
      this.maxDatagramSize = ObjectUtil.checkPositiveOrZero(maxDatagramSize, "maxDatagramSize");
      return this;
   }

   public int getMaxDatagramPayloadSize() {
      return this.maxDatagramSize;
   }

   public EpollDatagramChannelConfig setUdpGro(boolean gro) {
      try {
         ((EpollDatagramChannel)this.channel).socket.setUdpGro(gro);
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }

      this.gro = gro;
      return this;
   }

   public boolean isUdpGro() {
      return this.gro;
   }

   public EpollDatagramChannelConfig setMaxMessagesPerWrite(int maxMessagesPerWrite) {
      super.setMaxMessagesPerWrite(maxMessagesPerWrite);
      return this;
   }
}
