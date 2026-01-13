package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.util.internal.SocketUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;
import java.util.Map;

class NioDatagramChannelConfig extends DefaultDatagramChannelConfig {
   private final DatagramChannel javaChannel;

   NioDatagramChannelConfig(NioDatagramChannel channel, DatagramChannel javaChannel) {
      super(channel, javaChannel.socket());
      this.javaChannel = javaChannel;
   }

   @Override
   public int getTimeToLive() {
      return this.getOption0(StandardSocketOptions.IP_MULTICAST_TTL);
   }

   @Override
   public DatagramChannelConfig setTimeToLive(int ttl) {
      this.setOption0(StandardSocketOptions.IP_MULTICAST_TTL, ttl);
      return this;
   }

   @Override
   public InetAddress getInterface() {
      NetworkInterface inf = this.getNetworkInterface();
      if (inf != null) {
         Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface(inf);
         if (addresses.hasMoreElements()) {
            return addresses.nextElement();
         }
      }

      return null;
   }

   @Override
   public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
      try {
         this.setNetworkInterface(NetworkInterface.getByInetAddress(interfaceAddress));
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   @Override
   public NetworkInterface getNetworkInterface() {
      return this.getOption0(StandardSocketOptions.IP_MULTICAST_IF);
   }

   @Override
   public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
      this.setOption0(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
      return this;
   }

   @Override
   public boolean isLoopbackModeDisabled() {
      return this.getOption0(StandardSocketOptions.IP_MULTICAST_LOOP);
   }

   @Override
   public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
      this.setOption0(StandardSocketOptions.IP_MULTICAST_LOOP, loopbackModeDisabled);
      return this;
   }

   @Override
   public DatagramChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   @Override
   protected void autoReadCleared() {
      ((NioDatagramChannel)this.channel).clearReadPending0();
   }

   private <T> T getOption0(SocketOption<T> option) {
      try {
         return this.javaChannel.getOption(option);
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   private <T> void setOption0(SocketOption<T> option, T value) {
      try {
         this.javaChannel.setOption(option, value);
      } catch (IOException var4) {
         throw new ChannelException(var4);
      }
   }

   @Override
   public <T> boolean setOption(ChannelOption<T> option, T value) {
      return option instanceof NioChannelOption
         ? NioChannelOption.setOption(this.javaChannel, (NioChannelOption<T>)option, value)
         : super.setOption(option, value);
   }

   @Override
   public <T> T getOption(ChannelOption<T> option) {
      return option instanceof NioChannelOption ? NioChannelOption.getOption(this.javaChannel, (NioChannelOption<T>)option) : super.getOption(option);
   }

   @Override
   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), NioChannelOption.getOptions(this.javaChannel));
   }
}
