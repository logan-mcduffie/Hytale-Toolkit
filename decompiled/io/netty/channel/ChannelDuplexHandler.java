package io.netty.channel;

import java.net.SocketAddress;

public class ChannelDuplexHandler extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler {
   @ChannelHandlerMask.Skip
   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.bind(localAddress, promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.disconnect(promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.close(promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.deregister(promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void read(ChannelHandlerContext ctx) throws Exception {
      ctx.read();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      ctx.write(msg, promise);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void flush(ChannelHandlerContext ctx) throws Exception {
      ctx.flush();
   }
}
