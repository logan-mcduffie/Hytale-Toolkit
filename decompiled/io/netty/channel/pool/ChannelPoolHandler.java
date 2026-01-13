package io.netty.channel.pool;

import io.netty.channel.Channel;

public interface ChannelPoolHandler {
   void channelReleased(Channel var1) throws Exception;

   void channelAcquired(Channel var1) throws Exception;

   void channelCreated(Channel var1) throws Exception;
}
