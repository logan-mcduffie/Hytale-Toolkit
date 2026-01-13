package io.netty.channel;

public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler {
   @ChannelHandlerMask.Skip
   @Override
   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelRegistered();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelUnregistered();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelActive();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelInactive();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ctx.fireChannelRead(msg);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelReadComplete();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      ctx.fireUserEventTriggered(evt);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelWritabilityChanged();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      ctx.fireExceptionCaught(cause);
   }
}
