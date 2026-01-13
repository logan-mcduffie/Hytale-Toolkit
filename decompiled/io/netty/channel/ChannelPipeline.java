package io.netty.channel;

import io.netty.util.concurrent.EventExecutorGroup;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface ChannelPipeline extends ChannelInboundInvoker, ChannelOutboundInvoker, Iterable<Entry<String, ChannelHandler>> {
   ChannelPipeline addFirst(String var1, ChannelHandler var2);

   @Deprecated
   ChannelPipeline addFirst(EventExecutorGroup var1, String var2, ChannelHandler var3);

   ChannelPipeline addLast(String var1, ChannelHandler var2);

   @Deprecated
   ChannelPipeline addLast(EventExecutorGroup var1, String var2, ChannelHandler var3);

   ChannelPipeline addBefore(String var1, String var2, ChannelHandler var3);

   @Deprecated
   ChannelPipeline addBefore(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

   ChannelPipeline addAfter(String var1, String var2, ChannelHandler var3);

   @Deprecated
   ChannelPipeline addAfter(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

   ChannelPipeline addFirst(ChannelHandler... var1);

   @Deprecated
   ChannelPipeline addFirst(EventExecutorGroup var1, ChannelHandler... var2);

   ChannelPipeline addLast(ChannelHandler... var1);

   @Deprecated
   ChannelPipeline addLast(EventExecutorGroup var1, ChannelHandler... var2);

   ChannelPipeline remove(ChannelHandler var1);

   ChannelHandler remove(String var1);

   <T extends ChannelHandler> T remove(Class<T> var1);

   ChannelHandler removeFirst();

   ChannelHandler removeLast();

   ChannelPipeline replace(ChannelHandler var1, String var2, ChannelHandler var3);

   ChannelHandler replace(String var1, String var2, ChannelHandler var3);

   <T extends ChannelHandler> T replace(Class<T> var1, String var2, ChannelHandler var3);

   ChannelHandler first();

   ChannelHandlerContext firstContext();

   ChannelHandler last();

   ChannelHandlerContext lastContext();

   ChannelHandler get(String var1);

   <T extends ChannelHandler> T get(Class<T> var1);

   ChannelHandlerContext context(ChannelHandler var1);

   ChannelHandlerContext context(String var1);

   ChannelHandlerContext context(Class<? extends ChannelHandler> var1);

   Channel channel();

   List<String> names();

   Map<String, ChannelHandler> toMap();

   ChannelPipeline fireChannelRegistered();

   ChannelPipeline fireChannelUnregistered();

   ChannelPipeline fireChannelActive();

   ChannelPipeline fireChannelInactive();

   ChannelPipeline fireExceptionCaught(Throwable var1);

   ChannelPipeline fireUserEventTriggered(Object var1);

   ChannelPipeline fireChannelRead(Object var1);

   ChannelPipeline fireChannelReadComplete();

   ChannelPipeline fireChannelWritabilityChanged();

   ChannelPipeline flush();

   @Override
   default ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel());
   }

   @Override
   default ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel());
   }

   @Override
   default ChannelFuture newFailedFuture(Throwable cause) {
      return new FailedChannelFuture(this.channel(), null, cause);
   }
}
