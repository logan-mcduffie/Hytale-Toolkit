package io.netty.handler.codec.quic;

import java.net.InetSocketAddress;

public interface QuicConnectionPathStats {
   InetSocketAddress localAddress();

   InetSocketAddress peerAddress();

   long validationState();

   boolean active();

   long recv();

   long sent();

   long lost();

   long retrans();

   long rtt();

   long cwnd();

   long sentBytes();

   long recvBytes();

   long lostBytes();

   long streamRetransBytes();

   long pmtu();

   long deliveryRate();
}
