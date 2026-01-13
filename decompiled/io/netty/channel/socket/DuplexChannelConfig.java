package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface DuplexChannelConfig extends ChannelConfig {
   boolean isAllowHalfClosure();

   DuplexChannelConfig setAllowHalfClosure(boolean var1);

   @Deprecated
   DuplexChannelConfig setMaxMessagesPerRead(int var1);

   DuplexChannelConfig setWriteSpinCount(int var1);

   DuplexChannelConfig setAllocator(ByteBufAllocator var1);

   DuplexChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   DuplexChannelConfig setAutoRead(boolean var1);

   DuplexChannelConfig setAutoClose(boolean var1);

   DuplexChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   DuplexChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);
}
