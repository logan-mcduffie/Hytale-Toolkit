package io.netty.channel;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
   private final Set<ChannelHandlerContext> initMap = ConcurrentHashMap.newKeySet();

   protected abstract void initChannel(C var1) throws Exception;

   @Override
   public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      if (this.initChannel(ctx)) {
         ctx.pipeline().fireChannelRegistered();
         this.removeState(ctx);
      } else {
         ctx.fireChannelRegistered();
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (logger.isWarnEnabled()) {
         logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
      }

      ctx.close();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      if (ctx.channel().isRegistered() && this.initChannel(ctx)) {
         this.removeState(ctx);
      }
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.initMap.remove(ctx);
   }

   private boolean initChannel(ChannelHandlerContext ctx) throws Exception {
      if (this.initMap.add(ctx)) {
         try {
            this.initChannel((C)ctx.channel());
         } catch (Throwable var6) {
            this.exceptionCaught(ctx, var6);
         } finally {
            if (!ctx.isRemoved()) {
               ctx.pipeline().remove(this);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void removeState(final ChannelHandlerContext ctx) {
      if (ctx.isRemoved()) {
         this.initMap.remove(ctx);
      } else {
         ctx.executor().execute(new Runnable() {
            @Override
            public void run() {
               ChannelInitializer.this.initMap.remove(ctx);
            }
         });
      }
   }
}
