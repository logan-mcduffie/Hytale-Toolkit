package io.netty.handler.codec.http3;

public interface Http3PushPromiseFrame extends Http3RequestStreamFrame {
   @Override
   default long type() {
      return 5L;
   }

   long id();

   Http3Headers headers();
}
