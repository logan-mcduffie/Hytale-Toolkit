package io.netty.handler.codec.http3;

public interface Http3HeadersFrame extends Http3RequestStreamFrame, Http3PushStreamFrame {
   @Override
   default long type() {
      return 1L;
   }

   Http3Headers headers();
}
