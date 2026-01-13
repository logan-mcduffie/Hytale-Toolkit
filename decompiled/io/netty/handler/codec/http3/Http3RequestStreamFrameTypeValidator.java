package io.netty.handler.codec.http3;

final class Http3RequestStreamFrameTypeValidator implements Http3FrameTypeValidator {
   static final Http3RequestStreamFrameTypeValidator INSTANCE = new Http3RequestStreamFrameTypeValidator();

   private Http3RequestStreamFrameTypeValidator() {
   }

   @Override
   public void validate(long type, boolean first) throws Http3Exception {
      switch ((int)type) {
         case 3:
         case 4:
         case 7:
         case 13:
            throw new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Unexpected frame type '" + type + "' received");
      }
   }
}
