package io.netty.channel.unix;

import io.netty.channel.Channel;

public interface DomainDatagramChannel extends UnixChannel, Channel {
   DomainDatagramChannelConfig config();

   boolean isConnected();

   DomainSocketAddress localAddress();

   DomainSocketAddress remoteAddress();
}
