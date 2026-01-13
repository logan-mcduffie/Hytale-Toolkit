package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public interface DuplexChannel extends Channel {
   boolean isInputShutdown();

   ChannelFuture shutdownInput();

   ChannelFuture shutdownInput(ChannelPromise var1);

   boolean isOutputShutdown();

   ChannelFuture shutdownOutput();

   ChannelFuture shutdownOutput(ChannelPromise var1);

   boolean isShutdown();

   ChannelFuture shutdown();

   ChannelFuture shutdown(ChannelPromise var1);
}
