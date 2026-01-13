package io.netty.channel.unix;

import io.netty.channel.Channel;

public interface UnixChannel extends Channel {
   FileDescriptor fd();
}
