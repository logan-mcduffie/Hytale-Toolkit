package io.netty.handler.codec.http3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpStatusClass;
import org.jetbrains.annotations.Nullable;

final class Http3RequestStreamEncodeStateValidator extends ChannelOutboundHandlerAdapter implements Http3RequestStreamCodecState {
   private Http3RequestStreamEncodeStateValidator.State state = Http3RequestStreamEncodeStateValidator.State.None;

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (!(msg instanceof Http3RequestStreamFrame)) {
         super.write(ctx, msg, promise);
      } else {
         Http3RequestStreamFrame frame = (Http3RequestStreamFrame)msg;
         Http3RequestStreamEncodeStateValidator.State nextState = evaluateFrame(this.state, frame);
         if (nextState == null) {
            Http3FrameValidationUtils.frameTypeUnexpected(ctx, msg);
         } else {
            this.state = nextState;
            super.write(ctx, msg, promise);
         }
      }
   }

   @Override
   public boolean started() {
      return isStreamStarted(this.state);
   }

   @Override
   public boolean receivedFinalHeaders() {
      return isFinalHeadersReceived(this.state);
   }

   @Override
   public boolean terminated() {
      return isTrailersReceived(this.state);
   }

   @Nullable
   static Http3RequestStreamEncodeStateValidator.State evaluateFrame(Http3RequestStreamEncodeStateValidator.State state, Http3RequestStreamFrame frame) {
      if (!(frame instanceof Http3PushPromiseFrame) && !(frame instanceof Http3UnknownFrame)) {
         switch (state) {
            case None:
            case Headers:
               if (!(frame instanceof Http3HeadersFrame)) {
                  return null;
               }

               return isInformationalResponse((Http3HeadersFrame)frame)
                  ? Http3RequestStreamEncodeStateValidator.State.Headers
                  : Http3RequestStreamEncodeStateValidator.State.FinalHeaders;
            case FinalHeaders:
               if (frame instanceof Http3HeadersFrame) {
                  if (isInformationalResponse((Http3HeadersFrame)frame)) {
                     return null;
                  }

                  return Http3RequestStreamEncodeStateValidator.State.Trailers;
               }

               return state;
            case Trailers:
               return null;
            default:
               throw new Error("Unexpected frame state: " + state);
         }
      } else {
         return state;
      }
   }

   static boolean isStreamStarted(Http3RequestStreamEncodeStateValidator.State state) {
      return state != Http3RequestStreamEncodeStateValidator.State.None;
   }

   static boolean isFinalHeadersReceived(Http3RequestStreamEncodeStateValidator.State state) {
      return isStreamStarted(state) && state != Http3RequestStreamEncodeStateValidator.State.Headers;
   }

   static boolean isTrailersReceived(Http3RequestStreamEncodeStateValidator.State state) {
      return state == Http3RequestStreamEncodeStateValidator.State.Trailers;
   }

   private static boolean isInformationalResponse(Http3HeadersFrame headersFrame) {
      return HttpStatusClass.valueOf(headersFrame.headers().status()) == HttpStatusClass.INFORMATIONAL;
   }

   static enum State {
      None,
      Headers,
      FinalHeaders,
      Trailers;
   }
}
