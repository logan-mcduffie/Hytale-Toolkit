package io.netty.handler.codec.quic;

import java.net.SocketAddress;

final class QuicheQuicChannelAddress extends SocketAddress {
   final QuicheQuicChannel channel;

   QuicheQuicChannelAddress(QuicheQuicChannel channel) {
      this.channel = channel;
   }
}
