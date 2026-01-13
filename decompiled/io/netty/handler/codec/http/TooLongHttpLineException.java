package io.netty.handler.codec.http;

import io.netty.handler.codec.TooLongFrameException;

public final class TooLongHttpLineException extends TooLongFrameException {
   private static final long serialVersionUID = 1614751125592211890L;

   public TooLongHttpLineException() {
   }

   public TooLongHttpLineException(String message, Throwable cause) {
      super(message, cause);
   }

   public TooLongHttpLineException(String message) {
      super(message);
   }

   public TooLongHttpLineException(Throwable cause) {
      super(cause);
   }
}
