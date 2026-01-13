package io.netty.handler.codec.quic;

public final class SslEarlyDataReadyEvent {
   static final SslEarlyDataReadyEvent INSTANCE = new SslEarlyDataReadyEvent();

   private SslEarlyDataReadyEvent() {
   }
}
