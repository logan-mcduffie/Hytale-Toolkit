package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface ServerSocketChannelConfig extends ChannelConfig {
   int getBacklog();

   ServerSocketChannelConfig setBacklog(int var1);

   boolean isReuseAddress();

   ServerSocketChannelConfig setReuseAddress(boolean var1);

   int getReceiveBufferSize();

   ServerSocketChannelConfig setReceiveBufferSize(int var1);

   ServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

   ServerSocketChannelConfig setConnectTimeoutMillis(int var1);

   @Deprecated
   ServerSocketChannelConfig setMaxMessagesPerRead(int var1);

   ServerSocketChannelConfig setWriteSpinCount(int var1);

   ServerSocketChannelConfig setAllocator(ByteBufAllocator var1);

   ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   ServerSocketChannelConfig setAutoRead(boolean var1);

   ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   ServerSocketChannelConfig setWriteBufferHighWaterMark(int var1);

   ServerSocketChannelConfig setWriteBufferLowWaterMark(int var1);

   ServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);
}
