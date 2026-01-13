package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;

public interface ChannelHandlerContext extends AttributeMap, ChannelInboundInvoker, ChannelOutboundInvoker {
   Channel channel();

   EventExecutor executor();

   String name();

   ChannelHandler handler();

   boolean isRemoved();

   ChannelHandlerContext fireChannelRegistered();

   ChannelHandlerContext fireChannelUnregistered();

   ChannelHandlerContext fireChannelActive();

   ChannelHandlerContext fireChannelInactive();

   ChannelHandlerContext fireExceptionCaught(Throwable var1);

   ChannelHandlerContext fireUserEventTriggered(Object var1);

   ChannelHandlerContext fireChannelRead(Object var1);

   ChannelHandlerContext fireChannelReadComplete();

   ChannelHandlerContext fireChannelWritabilityChanged();

   ChannelHandlerContext read();

   ChannelHandlerContext flush();

   ChannelPipeline pipeline();

   ByteBufAllocator alloc();

   @Deprecated
   @Override
   <T> Attribute<T> attr(AttributeKey<T> var1);

   @Deprecated
   @Override
   <T> boolean hasAttr(AttributeKey<T> var1);
}
