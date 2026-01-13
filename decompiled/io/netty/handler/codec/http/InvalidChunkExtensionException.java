package io.netty.handler.codec.http;

import io.netty.handler.codec.CorruptedFrameException;

public final class InvalidChunkExtensionException extends CorruptedFrameException {
   private static final long serialVersionUID = 536224937231200736L;

   public InvalidChunkExtensionException() {
      super("Line Feed must be preceded by Carriage Return when terminating HTTP chunk header lines");
   }

   public InvalidChunkExtensionException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidChunkExtensionException(String message) {
      super(message);
   }

   public InvalidChunkExtensionException(Throwable cause) {
      super(cause);
   }
}
