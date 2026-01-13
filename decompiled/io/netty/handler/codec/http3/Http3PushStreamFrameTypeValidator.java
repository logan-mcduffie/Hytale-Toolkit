package io.netty.handler.codec.http3;

final class Http3PushStreamFrameTypeValidator implements Http3FrameTypeValidator {
   static final Http3PushStreamFrameTypeValidator INSTANCE = new Http3PushStreamFrameTypeValidator();

   private Http3PushStreamFrameTypeValidator() {
   }

   @Override
   public void validate(long type, boolean first) throws Http3Exception {
      switch ((int)type) {
         case 3:
         case 4:
         case 5:
         case 7:
         case 13:
            throw new Http3Exception(Http3ErrorCode.H3_FRAME_UNEXPECTED, "Unexpected frame type '" + type + "' received");
         case 6:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
      }
   }
}
