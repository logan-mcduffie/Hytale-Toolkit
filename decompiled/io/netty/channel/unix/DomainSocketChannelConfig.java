package io.netty.channel.unix;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface DomainSocketChannelConfig extends ChannelConfig {
   @Deprecated
   DomainSocketChannelConfig setMaxMessagesPerRead(int var1);

   DomainSocketChannelConfig setConnectTimeoutMillis(int var1);

   DomainSocketChannelConfig setWriteSpinCount(int var1);

   DomainSocketChannelConfig setAllocator(ByteBufAllocator var1);

   DomainSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   DomainSocketChannelConfig setAutoRead(boolean var1);

   DomainSocketChannelConfig setAutoClose(boolean var1);

   @Deprecated
   DomainSocketChannelConfig setWriteBufferHighWaterMark(int var1);

   @Deprecated
   DomainSocketChannelConfig setWriteBufferLowWaterMark(int var1);

   DomainSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

   DomainSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   DomainSocketChannelConfig setReadMode(DomainSocketReadMode var1);

   DomainSocketReadMode getReadMode();
}
