package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {
   private final ChannelHandler handler;

   DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
      super(pipeline, executor, name, (Class<? extends ChannelHandler>)handler.getClass());
      this.handler = handler;
   }

   @Override
   public ChannelHandler handler() {
      return this.handler;
   }
}
