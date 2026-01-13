package io.netty.channel.nio;

import io.netty.channel.IoEvent;

public interface NioIoEvent extends IoEvent {
   NioIoOps ops();
}
