package io.netty.handler.codec.http3;

final class Http3PushStreamServerValidationHandler extends Http3FrameTypeOutboundValidationHandler<Http3PushStreamFrame> {
   static final Http3PushStreamServerValidationHandler INSTANCE = new Http3PushStreamServerValidationHandler();

   private Http3PushStreamServerValidationHandler() {
      super(Http3PushStreamFrame.class);
   }

   @Override
   public boolean isSharable() {
      return true;
   }
}
