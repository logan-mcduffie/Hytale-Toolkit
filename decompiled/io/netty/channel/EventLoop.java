package io.netty.channel;

import io.netty.util.concurrent.OrderedEventExecutor;

public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
   EventLoopGroup parent();
}
