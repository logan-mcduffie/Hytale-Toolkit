package io.netty.channel.unix;

import io.netty.channel.ServerChannel;

public interface ServerDomainSocketChannel extends ServerChannel, UnixChannel {
   DomainSocketAddress remoteAddress();

   DomainSocketAddress localAddress();
}
