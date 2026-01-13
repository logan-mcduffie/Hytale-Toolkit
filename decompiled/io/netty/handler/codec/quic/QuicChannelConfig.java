package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface QuicChannelConfig extends ChannelConfig {
   @Deprecated
   QuicChannelConfig setMaxMessagesPerRead(int var1);

   QuicChannelConfig setConnectTimeoutMillis(int var1);

   QuicChannelConfig setWriteSpinCount(int var1);

   QuicChannelConfig setAllocator(ByteBufAllocator var1);

   QuicChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   QuicChannelConfig setAutoRead(boolean var1);

   QuicChannelConfig setAutoClose(boolean var1);

   QuicChannelConfig setWriteBufferHighWaterMark(int var1);

   QuicChannelConfig setWriteBufferLowWaterMark(int var1);

   QuicChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

   QuicChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
