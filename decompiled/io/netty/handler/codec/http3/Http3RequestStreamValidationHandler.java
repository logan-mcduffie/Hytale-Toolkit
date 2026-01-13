package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.codec.http.HttpMethod;
import java.util.function.BooleanSupplier;

final class Http3RequestStreamValidationHandler extends Http3FrameTypeDuplexValidationHandler<Http3RequestStreamFrame> {
   private final boolean server;
   private final BooleanSupplier goAwayReceivedSupplier;
   private final QpackAttributes qpackAttributes;
   private final QpackDecoder qpackDecoder;
   private final Http3RequestStreamCodecState decodeState;
   private final Http3RequestStreamCodecState encodeState;
   private boolean clientHeadRequest;
   private long expectedLength = -1L;
   private long seenLength;

   static ChannelHandler newServerValidator(
      QpackAttributes qpackAttributes, QpackDecoder decoder, Http3RequestStreamCodecState encodeState, Http3RequestStreamCodecState decodeState
   ) {
      return new Http3RequestStreamValidationHandler(true, () -> false, qpackAttributes, decoder, encodeState, decodeState);
   }

   static ChannelHandler newClientValidator(
      BooleanSupplier goAwayReceivedSupplier,
      QpackAttributes qpackAttributes,
      QpackDecoder decoder,
      Http3RequestStreamCodecState encodeState,
      Http3RequestStreamCodecState decodeState
   ) {
      return new Http3RequestStreamValidationHandler(false, goAwayReceivedSupplier, qpackAttributes, decoder, encodeState, decodeState);
   }

   private Http3RequestStreamValidationHandler(
      boolean server,
      BooleanSupplier goAwayReceivedSupplier,
      QpackAttributes qpackAttributes,
      QpackDecoder qpackDecoder,
      Http3RequestStreamCodecState encodeState,
      Http3RequestStreamCodecState decodeState
   ) {
      super(Http3RequestStreamFrame.class);
      this.server = server;
      this.goAwayReceivedSupplier = goAwayReceivedSupplier;
      this.qpackAttributes = qpackAttributes;
      this.qpackDecoder = qpackDecoder;
      this.decodeState = decodeState;
      this.encodeState = encodeState;
   }

   void write(ChannelHandlerContext ctx, Http3RequestStreamFrame frame, ChannelPromise promise) {
      if (!this.server) {
         if (!Http3RequestStreamValidationUtils.validateClientWrite(frame, promise, ctx, this.goAwayReceivedSupplier, this.encodeState)) {
            return;
         }

         if (frame instanceof Http3HeadersFrame) {
            this.clientHeadRequest = HttpMethod.HEAD.asciiName().equals(((Http3HeadersFrame)frame).headers().method());
         }
      }

      ctx.write(frame, promise);
   }

   void channelRead(ChannelHandlerContext ctx, Http3RequestStreamFrame frame) {
      if (frame instanceof Http3PushPromiseFrame) {
         if (this.server) {
            Http3FrameValidationUtils.frameTypeUnexpected(ctx, frame);
         } else {
            ctx.fireChannelRead(frame);
         }
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
            long maybeContentLength = Http3RequestStreamValidationUtils.validateDataFrameRead(
               dataFrame, ctx, this.expectedLength, this.seenLength, this.clientHeadRequest
            );
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
         if (!Http3RequestStreamValidationUtils.validateOnStreamClosure(ctx, this.expectedLength, this.seenLength, this.clientHeadRequest)) {
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
