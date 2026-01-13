package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

public interface SocketChannelConfig extends DuplexChannelConfig {
   boolean isTcpNoDelay();

   SocketChannelConfig setTcpNoDelay(boolean var1);

   int getSoLinger();

   SocketChannelConfig setSoLinger(int var1);

   int getSendBufferSize();

   SocketChannelConfig setSendBufferSize(int var1);

   int getReceiveBufferSize();

   SocketChannelConfig setReceiveBufferSize(int var1);

   boolean isKeepAlive();

   SocketChannelConfig setKeepAlive(boolean var1);

   int getTrafficClass();

   SocketChannelConfig setTrafficClass(int var1);

   boolean isReuseAddress();

   SocketChannelConfig setReuseAddress(boolean var1);

   SocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

   SocketChannelConfig setAllowHalfClosure(boolean var1);

   SocketChannelConfig setConnectTimeoutMillis(int var1);

   @Deprecated
   SocketChannelConfig setMaxMessagesPerRead(int var1);

   SocketChannelConfig setWriteSpinCount(int var1);

   SocketChannelConfig setAllocator(ByteBufAllocator var1);

   SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   SocketChannelConfig setAutoRead(boolean var1);

   SocketChannelConfig setAutoClose(boolean var1);

   SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   SocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);
}
