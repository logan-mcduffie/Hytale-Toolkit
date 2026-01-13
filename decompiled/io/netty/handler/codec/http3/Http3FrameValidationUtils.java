package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.Nullable;

final class Http3FrameValidationUtils {
   private Http3FrameValidationUtils() {
   }

   private static <T> T cast(Object msg) {
      return (T)msg;
   }

   private static <T> boolean isValid(Class<T> frameType, Object msg) {
      return frameType.isInstance(msg);
   }

   @Nullable
   static <T> T validateFrameWritten(Class<T> expectedFrameType, Object msg) {
      return isValid(expectedFrameType, msg) ? cast(msg) : null;
   }

   @Nullable
   static <T> T validateFrameRead(Class<T> expectedFrameType, Object msg) {
      return isValid(expectedFrameType, msg) ? cast(msg) : null;
   }

   static void frameTypeUnexpected(ChannelPromise promise, Object frame) {
      String type = StringUtil.simpleClassName(frame);
      ReferenceCountUtil.release(frame);
      promise.setFailure(new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Frame of type " + type + " unexpected"));
   }

   static void frameTypeUnexpected(ChannelHandlerContext ctx, Object frame) {
      ReferenceCountUtil.release(frame);
      Http3CodecUtils.connectionError(ctx, Http3ErrorCode.H3_FRAME_UNEXPECTED, "Frame type unexpected", true);
   }
}
