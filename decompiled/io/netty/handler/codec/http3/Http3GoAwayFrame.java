package io.netty.handler.codec.http3;

public interface Http3GoAwayFrame extends Http3ControlStreamFrame {
   @Override
   default long type() {
      return 7L;
   }

   long id();
}
