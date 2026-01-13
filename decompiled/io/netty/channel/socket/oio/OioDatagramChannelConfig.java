package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import java.net.InetAddress;
import java.net.NetworkInterface;

@Deprecated
public interface OioDatagramChannelConfig extends DatagramChannelConfig {
   OioDatagramChannelConfig setSoTimeout(int var1);

   int getSoTimeout();

   OioDatagramChannelConfig setSendBufferSize(int var1);

   OioDatagramChannelConfig setReceiveBufferSize(int var1);

   OioDatagramChannelConfig setTrafficClass(int var1);

   OioDatagramChannelConfig setReuseAddress(boolean var1);

   OioDatagramChannelConfig setBroadcast(boolean var1);

   OioDatagramChannelConfig setLoopbackModeDisabled(boolean var1);

   OioDatagramChannelConfig setTimeToLive(int var1);

   OioDatagramChannelConfig setInterface(InetAddress var1);

   OioDatagramChannelConfig setNetworkInterface(NetworkInterface var1);

   OioDatagramChannelConfig setMaxMessagesPerRead(int var1);

   OioDatagramChannelConfig setWriteSpinCount(int var1);

   OioDatagramChannelConfig setConnectTimeoutMillis(int var1);

   OioDatagramChannelConfig setAllocator(ByteBufAllocator var1);

   OioDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   OioDatagramChannelConfig setAutoRead(boolean var1);

   OioDatagramChannelConfig setAutoClose(boolean var1);

   OioDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

   OioDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1);

   OioDatagramChannelConfig setWriteBufferHighWaterMark(int var1);

   OioDatagramChannelConfig setWriteBufferLowWaterMark(int var1);
}
