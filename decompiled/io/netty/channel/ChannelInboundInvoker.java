package io.netty.channel;

public interface ChannelInboundInvoker {
   ChannelInboundInvoker fireChannelRegistered();

   ChannelInboundInvoker fireChannelUnregistered();

   ChannelInboundInvoker fireChannelActive();

   ChannelInboundInvoker fireChannelInactive();

   ChannelInboundInvoker fireExceptionCaught(Throwable var1);

   ChannelInboundInvoker fireUserEventTriggered(Object var1);

   ChannelInboundInvoker fireChannelRead(Object var1);

   ChannelInboundInvoker fireChannelReadComplete();

   ChannelInboundInvoker fireChannelWritabilityChanged();
}
