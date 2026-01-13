package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;

class Http3FrameTypeOutboundValidationHandler<T extends Http3Frame> extends ChannelOutboundHandlerAdapter {
   private final Class<T> frameType;

   Http3FrameTypeOutboundValidationHandler(Class<T> frameType) {
      this.frameType = ObjectUtil.checkNotNull(frameType, "frameType");
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
}
