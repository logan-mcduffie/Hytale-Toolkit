package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.ObjectUtil;

class Http3FrameTypeInboundValidationHandler<T extends Http3Frame> extends ChannelInboundHandlerAdapter {
   protected final Class<T> frameType;

   Http3FrameTypeInboundValidationHandler(Class<T> frameType) {
      this.frameType = ObjectUtil.checkNotNull(frameType, "frameType");
   }

   @Override
   public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      T frame = Http3FrameValidationUtils.validateFrameRead(this.frameType, msg);
      if (frame != null) {
         this.channelRead(ctx, frame);
      } else {
         this.readFrameDiscarded(ctx, msg);
      }
   }

   void channelRead(ChannelHandlerContext ctx, T frame) throws Exception {
      ctx.fireChannelRead(frame);
   }

   void readFrameDiscarded(ChannelHandlerContext ctx, Object discardedFrame) {
      Http3FrameValidationUtils.frameTypeUnexpected(ctx, discardedFrame);
   }
}
