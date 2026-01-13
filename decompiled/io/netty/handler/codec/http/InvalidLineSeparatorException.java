package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderException;

public final class InvalidLineSeparatorException extends DecoderException {
   private static final long serialVersionUID = 536224937231200736L;

   public InvalidLineSeparatorException() {
      super("Line Feed must be preceded by Carriage Return when terminating HTTP start- and header field-lines");
   }

   public InvalidLineSeparatorException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidLineSeparatorException(String message) {
      super(message);
   }

   public InvalidLineSeparatorException(Throwable cause) {
      super(cause);
   }
}
