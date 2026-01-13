package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class DatagramSocketAddress extends InetSocketAddress {
   private static final long serialVersionUID = 3094819287843178401L;
   private final int receivedAmount;
   private final DatagramSocketAddress localAddress;

   DatagramSocketAddress(byte[] addr, int scopeId, int port, int receivedAmount, DatagramSocketAddress local) throws UnknownHostException {
      super(newAddress(addr, scopeId), port);
      this.receivedAmount = receivedAmount;
      this.localAddress = local;
   }

   public DatagramSocketAddress localAddress() {
      return this.localAddress;
   }

   public int receivedAmount() {
      return this.receivedAmount;
   }

   private static InetAddress newAddress(byte[] bytes, int scopeId) throws UnknownHostException {
      return (InetAddress)(bytes.length == 4 ? InetAddress.getByAddress(bytes) : Inet6Address.getByAddress(null, bytes, scopeId));
   }
}
