package io.netty.channel.local;

import io.netty.channel.IoHandle;

interface LocalIoHandle extends IoHandle {
   void closeNow();
}
