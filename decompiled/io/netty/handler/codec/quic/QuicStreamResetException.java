package io.netty.handler.codec.quic;

public final class QuicStreamResetException extends QuicException {
   private final long applicationProtocolCode;

   public QuicStreamResetException(String message, long applicationProtocolCode) {
      super(message);
      this.applicationProtocolCode = applicationProtocolCode;
   }

   public long applicationProtocolCode() {
      return this.applicationProtocolCode;
   }
}
