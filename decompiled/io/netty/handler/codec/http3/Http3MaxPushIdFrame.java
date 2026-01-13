package io.netty.handler.codec.http3;

public interface Http3MaxPushIdFrame extends Http3ControlStreamFrame {
   @Override
   default long type() {
      return 13L;
   }

   long id();
}
