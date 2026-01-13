package io.netty.channel.unix;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface DomainDatagramChannelConfig extends ChannelConfig {
   DomainDatagramChannelConfig setAllocator(ByteBufAllocator var1);

   DomainDatagramChannelConfig setAutoClose(boolean var1);

   DomainDatagramChannelConfig setAutoRead(boolean var1);

   DomainDatagramChannelConfig setConnectTimeoutMillis(int var1);

   @Deprecated
   DomainDatagramChannelConfig setMaxMessagesPerRead(int var1);

   DomainDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   DomainDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   DomainDatagramChannelConfig setSendBufferSize(int var1);

   int getSendBufferSize();

   DomainDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

   DomainDatagramChannelConfig setWriteSpinCount(int var1);
}
