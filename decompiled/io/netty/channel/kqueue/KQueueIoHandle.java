package io.netty.channel.kqueue;

import io.netty.channel.IoHandle;

public interface KQueueIoHandle extends IoHandle {
   int ident();
}
