package io.netty.channel.nio;

import io.netty.channel.IoHandle;
import java.nio.channels.SelectableChannel;

public interface NioIoHandle extends IoHandle {
   SelectableChannel selectableChannel();
}
