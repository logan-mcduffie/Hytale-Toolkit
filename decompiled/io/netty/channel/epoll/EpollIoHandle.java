package io.netty.channel.epoll;

import io.netty.channel.IoHandle;
import io.netty.channel.unix.FileDescriptor;

public interface EpollIoHandle extends IoHandle {
   FileDescriptor fd();
}
