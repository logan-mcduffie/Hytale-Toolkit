package io.netty.handler.codec.quic;

import org.jetbrains.annotations.Nullable;

public class QuicException extends Exception {
   private final QuicTransportError error;

   QuicException(String message) {
      super(message);
      this.error = null;
   }

   public QuicException(QuicTransportError error) {
      super(error.name());
      this.error = error;
   }

   public QuicException(String message, QuicTransportError error) {
      super(message);
      this.error = error;
   }

   public QuicException(Throwable cause, QuicTransportError error) {
      super(cause);
      this.error = error;
   }

   public QuicException(String message, Throwable cause, QuicTransportError error) {
      super(message, cause);
      this.error = error;
   }

   @Nullable
   public QuicTransportError error() {
      return this.error;
   }
}
