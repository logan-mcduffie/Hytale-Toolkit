package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.quic.QuicException;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.jetbrains.annotations.Nullable;

public abstract class Http3RequestStreamInboundHandler extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http3RequestStreamInboundHandler.class);

   @Override
   public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof Http3UnknownFrame) {
         this.channelRead(ctx, (Http3UnknownFrame)msg);
      } else if (msg instanceof Http3HeadersFrame) {
         this.channelRead(ctx, (Http3HeadersFrame)msg);
      } else if (msg instanceof Http3DataFrame) {
         this.channelRead(ctx, (Http3DataFrame)msg);
      } else {
         super.channelRead(ctx, msg);
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt == ChannelInputShutdownEvent.INSTANCE) {
         this.channelInputClosed(ctx);
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (cause instanceof QuicException) {
         this.handleQuicException(ctx, (QuicException)cause);
      } else if (cause instanceof Http3Exception) {
         this.handleHttp3Exception(ctx, (Http3Exception)cause);
      } else {
         ctx.fireExceptionCaught(cause);
      }
   }

   protected abstract void channelRead(ChannelHandlerContext var1, Http3HeadersFrame var2) throws Exception;

   protected abstract void channelRead(ChannelHandlerContext var1, Http3DataFrame var2) throws Exception;

   protected abstract void channelInputClosed(ChannelHandlerContext var1) throws Exception;

   protected void channelRead(ChannelHandlerContext ctx, Http3UnknownFrame frame) {
      frame.release();
   }

   protected void handleQuicException(ChannelHandlerContext ctx, QuicException exception) {
      logger.debug("Caught QuicException on channel {}", ctx.channel(), exception);
   }

   protected void handleHttp3Exception(ChannelHandlerContext ctx, Http3Exception exception) {
      logger.error("Caught Http3Exception on channel {}", ctx.channel(), exception);
   }

   @Nullable
   protected final QuicStreamChannel controlStream(ChannelHandlerContext ctx) {
      return Http3.getLocalControlStream(ctx.channel().parent());
   }
}
