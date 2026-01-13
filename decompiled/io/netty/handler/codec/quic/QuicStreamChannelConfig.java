package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DuplexChannelConfig;

public interface QuicStreamChannelConfig extends DuplexChannelConfig {
   QuicStreamChannelConfig setReadFrames(boolean var1);

   boolean isReadFrames();

   QuicStreamChannelConfig setAllowHalfClosure(boolean var1);

   QuicStreamChannelConfig setMaxMessagesPerRead(int var1);

   QuicStreamChannelConfig setWriteSpinCount(int var1);

   QuicStreamChannelConfig setAllocator(ByteBufAllocator var1);

   QuicStreamChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   QuicStreamChannelConfig setAutoRead(boolean var1);

   QuicStreamChannelConfig setAutoClose(boolean var1);

   QuicStreamChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   QuicStreamChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

   QuicStreamChannelConfig setConnectTimeoutMillis(int var1);

   QuicStreamChannelConfig setWriteBufferHighWaterMark(int var1);

   QuicStreamChannelConfig setWriteBufferLowWaterMark(int var1);
}
