package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

class Http3FrameTypeDuplexValidationHandler<T extends Http3Frame> extends Http3FrameTypeInboundValidationHandler<T> implements ChannelOutboundHandler {
   Http3FrameTypeDuplexValidationHandler(Class<T> frameType) {
      super(frameType);
   }

   @Override
   public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      T frame = Http3FrameValidationUtils.validateFrameWritten(this.frameType, msg);
      if (frame != null) {
         this.write(ctx, frame, promise);
      } else {
         this.writeFrameDiscarded(msg, promise);
      }
   }

   void write(ChannelHandlerContext ctx, T msg, ChannelPromise promise) {
      ctx.write(msg, promise);
   }

   void writeFrameDiscarded(Object discardedFrame, ChannelPromise promise) {
      Http3FrameValidationUtils.frameTypeUnexpected(promise, discardedFrame);
   }

   @Override
   public void flush(ChannelHandlerContext ctx) {
      ctx.flush();
   }

   @Override
   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
      ctx.bind(localAddress, promise);
   }

   @Override
   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
      ctx.disconnect(promise);
   }

   @Override
   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.close(promise);
   }

   @Override
   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.deregister(promise);
   }

   @Override
   public void read(ChannelHandlerContext ctx) throws Exception {
      ctx.read();
   }
}
