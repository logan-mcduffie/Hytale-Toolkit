package com.google.common.flogger.backend;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LoggingException extends RuntimeException {
   public LoggingException(@NullableDecl String message) {
      super(message);
   }

   public LoggingException(@NullableDecl String message, @NullableDecl Throwable cause) {
      super(message, cause);
   }
}
