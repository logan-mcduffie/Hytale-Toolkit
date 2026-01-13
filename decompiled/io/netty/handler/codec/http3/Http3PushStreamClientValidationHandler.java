package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;

final class Http3PushStreamClientValidationHandler extends Http3FrameTypeInboundValidationHandler<Http3RequestStreamFrame> {
   private final QpackAttributes qpackAttributes;
   private final QpackDecoder qpackDecoder;
   private final Http3RequestStreamCodecState decodeState;
   private long expectedLength = -1L;
   private long seenLength;

   Http3PushStreamClientValidationHandler(QpackAttributes qpackAttributes, QpackDecoder qpackDecoder, Http3RequestStreamCodecState decodeState) {
      super(Http3RequestStreamFrame.class);
      this.qpackAttributes = qpackAttributes;
      this.qpackDecoder = qpackDecoder;
      this.decodeState = decodeState;
   }

   void channelRead(ChannelHandlerContext ctx, Http3RequestStreamFrame frame) {
      if (frame instanceof Http3PushPromiseFrame) {
         ctx.fireChannelRead(frame);
      } else {
         if (frame instanceof Http3HeadersFrame) {
            Http3HeadersFrame headersFrame = (Http3HeadersFrame)frame;
            long maybeContentLength = Http3RequestStreamValidationUtils.validateHeaderFrameRead(headersFrame, ctx, this.decodeState);
            if (maybeContentLength >= 0L) {
               this.expectedLength = maybeContentLength;
            } else if (maybeContentLength == -2L) {
               return;
            }
         }

         if (frame instanceof Http3DataFrame) {
            Http3DataFrame dataFrame = (Http3DataFrame)frame;
            long maybeContentLength = Http3RequestStreamValidationUtils.validateDataFrameRead(dataFrame, ctx, this.expectedLength, this.seenLength, false);
            if (maybeContentLength >= 0L) {
               this.seenLength = maybeContentLength;
            } else if (maybeContentLength == -2L) {
               return;
            }
         }

         ctx.fireChannelRead(frame);
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
      if (evt == ChannelInputShutdownReadComplete.INSTANCE) {
         Http3RequestStreamValidationUtils.sendStreamAbandonedIfRequired(ctx, this.qpackAttributes, this.qpackDecoder, this.decodeState);
         if (!Http3RequestStreamValidationUtils.validateOnStreamClosure(ctx, this.expectedLength, this.seenLength, false)) {
            return;
         }
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public boolean isSharable() {
      return false;
   }
}
