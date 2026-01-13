package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import java.util.function.BooleanSupplier;

final class Http3RequestStreamValidationUtils {
   static final long CONTENT_LENGTH_NOT_MODIFIED = -1L;
   static final long INVALID_FRAME_READ = -2L;

   private Http3RequestStreamValidationUtils() {
   }

   static boolean validateClientWrite(
      Http3RequestStreamFrame frame,
      ChannelPromise promise,
      ChannelHandlerContext ctx,
      BooleanSupplier goAwayReceivedSupplier,
      Http3RequestStreamCodecState encodeState
   ) {
      if (goAwayReceivedSupplier.getAsBoolean() && !encodeState.started()) {
         String type = StringUtil.simpleClassName(frame);
         ReferenceCountUtil.release(frame);
         promise.setFailure(new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Frame of type " + type + " unexpected as we received a GOAWAY already."));
         ctx.close();
         return false;
      } else if (frame instanceof Http3PushPromiseFrame) {
         Http3FrameValidationUtils.frameTypeUnexpected(promise, frame);
         return false;
      } else {
         return true;
      }
   }

   static long validateHeaderFrameRead(Http3HeadersFrame headersFrame, ChannelHandlerContext ctx, Http3RequestStreamCodecState decodeState) {
      if (headersFrame.headers().contains(HttpHeaderNames.CONNECTION)) {
         headerUnexpected(ctx, headersFrame, "connection header included");
         return -2L;
      } else {
         CharSequence value = headersFrame.headers().get(HttpHeaderNames.TE);
         if (value != null && !HttpHeaderValues.TRAILERS.equals(value)) {
            headerUnexpected(ctx, headersFrame, "te header field included with invalid value: " + value);
            return -2L;
         } else if (decodeState.receivedFinalHeaders()) {
            long length = HttpUtil.normalizeAndGetContentLength(headersFrame.headers().getAll(HttpHeaderNames.CONTENT_LENGTH), false, true);
            if (length != -1L) {
               headersFrame.headers().setLong(HttpHeaderNames.CONTENT_LENGTH, length);
            }

            return length;
         } else {
            return -1L;
         }
      }
   }

   static long validateDataFrameRead(Http3DataFrame dataFrame, ChannelHandlerContext ctx, long expectedLength, long seenLength, boolean clientHeadRequest) {
      try {
         return verifyContentLength(dataFrame.content().readableBytes(), expectedLength, seenLength, false, clientHeadRequest);
      } catch (Http3Exception var8) {
         ReferenceCountUtil.release(dataFrame);
         failStream(ctx, var8);
         return -2L;
      }
   }

   static boolean validateOnStreamClosure(ChannelHandlerContext ctx, long expectedLength, long seenLength, boolean clientHeadRequest) {
      try {
         verifyContentLength(0, expectedLength, seenLength, true, clientHeadRequest);
         return true;
      } catch (Http3Exception var7) {
         ctx.fireExceptionCaught(var7);
         Http3CodecUtils.streamError(ctx, var7.errorCode());
         return false;
      }
   }

   static void sendStreamAbandonedIfRequired(
      ChannelHandlerContext ctx, QpackAttributes qpackAttributes, QpackDecoder qpackDecoder, Http3RequestStreamCodecState decodeState
   ) {
      if (!qpackAttributes.dynamicTableDisabled() && !decodeState.terminated()) {
         long streamId = ((QuicStreamChannel)ctx.channel()).streamId();
         if (qpackAttributes.decoderStreamAvailable()) {
            qpackDecoder.streamAbandoned(qpackAttributes.decoderStream(), streamId);
         } else {
            qpackAttributes.whenDecoderStreamAvailable(future -> {
               if (future.isSuccess()) {
                  qpackDecoder.streamAbandoned(qpackAttributes.decoderStream(), streamId);
               }
            });
         }
      }
   }

   private static void headerUnexpected(ChannelHandlerContext ctx, Http3RequestStreamFrame frame, String msg) {
      ReferenceCountUtil.release(frame);
      failStream(ctx, new Http3Exception(Http3ErrorCode.H3_MESSAGE_ERROR, msg));
   }

   private static void failStream(ChannelHandlerContext ctx, Http3Exception cause) {
      ctx.fireExceptionCaught(cause);
      Http3CodecUtils.streamError(ctx, cause.errorCode());
   }

   private static long verifyContentLength(int length, long expectedLength, long seenLength, boolean end, boolean clientHeadRequest) throws Http3Exception {
      seenLength += length;
      if (expectedLength == -1L || seenLength <= expectedLength && (clientHeadRequest || !end || seenLength == expectedLength)) {
         return seenLength;
      } else {
         throw new Http3Exception(Http3ErrorCode.H3_MESSAGE_ERROR, "Expected content-length " + expectedLength + " != " + seenLength + ".");
      }
   }
}
