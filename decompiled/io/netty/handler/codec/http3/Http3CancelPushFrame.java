package io.netty.handler.codec.http3;

public interface Http3CancelPushFrame extends Http3ControlStreamFrame {
   @Override
   default long type() {
      return 3L;
   }

   long id();
}
