package io.netty.handler.ssl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLException;

public abstract class ApplicationProtocolNegotiationHandler extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
   private final String fallbackProtocol;
   private final RecyclableArrayList bufferedMessages = RecyclableArrayList.newInstance();
   private ChannelHandlerContext ctx;
   private boolean sslHandlerChecked;

   protected ApplicationProtocolNegotiationHandler(String fallbackProtocol) {
      this.fallbackProtocol = ObjectUtil.checkNotNull(fallbackProtocol, "fallbackProtocol");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
      super.handlerAdded(ctx);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.fireBufferedMessages();
      this.bufferedMessages.recycle();
      super.handlerRemoved(ctx);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      this.bufferedMessages.add(msg);
      if (!this.sslHandlerChecked) {
         this.sslHandlerChecked = true;
         if (ctx.pipeline().get(SslHandler.class) == null) {
            this.removeSelfIfPresent(ctx);
         }
      }
   }

   private void fireBufferedMessages() {
      if (!this.bufferedMessages.isEmpty()) {
         for (int i = 0; i < this.bufferedMessages.size(); i++) {
            this.ctx.fireChannelRead(this.bufferedMessages.get(i));
         }

         this.ctx.fireChannelReadComplete();
         this.bufferedMessages.clear();
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof SslHandshakeCompletionEvent) {
         SslHandshakeCompletionEvent handshakeEvent = (SslHandshakeCompletionEvent)evt;

         try {
            if (handshakeEvent.isSuccess()) {
               SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
               if (sslHandler == null) {
                  throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)");
               }

               String protocol = sslHandler.applicationProtocol();
               this.configurePipeline(ctx, protocol != null ? protocol : this.fallbackProtocol);
            }
         } catch (Throwable var9) {
            this.exceptionCaught(ctx, var9);
         } finally {
            if (handshakeEvent.isSuccess()) {
               this.removeSelfIfPresent(ctx);
            }
         }
      }

      if (evt instanceof ChannelInputShutdownEvent) {
         this.fireBufferedMessages();
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.fireBufferedMessages();
      super.channelInactive(ctx);
   }

   private void removeSelfIfPresent(ChannelHandlerContext ctx) {
      ChannelPipeline pipeline = ctx.pipeline();
      if (!ctx.isRemoved()) {
         pipeline.remove(this);
      }
   }

   protected abstract void configurePipeline(ChannelHandlerContext var1, String var2) throws Exception;

   protected void handshakeFailure(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      logger.warn("{} TLS handshake failed:", ctx.channel(), cause);
      ctx.close();
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      Throwable wrapped;
      if (cause instanceof DecoderException && (wrapped = cause.getCause()) instanceof SSLException) {
         try {
            this.handshakeFailure(ctx, wrapped);
         } finally {
            this.removeSelfIfPresent(ctx);
         }
      } else {
         logger.warn("{} Failed to select the application-level protocol:", ctx.channel(), cause);
         ctx.fireExceptionCaught(cause);
         ctx.close();
      }
   }
}
