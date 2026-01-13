package io.netty.channel.epoll;

import io.netty.channel.IoEvent;

public interface EpollIoEvent extends IoEvent {
   EpollIoOps ops();
}
