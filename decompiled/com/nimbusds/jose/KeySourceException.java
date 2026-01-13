package com.nimbusds.jose;

public class KeySourceException extends JOSEException {
   public KeySourceException(String message) {
      super(message);
   }

   public KeySourceException(String message, Throwable cause) {
      super(message, cause);
   }

   public KeySourceException(Throwable cause) {
      super(cause);
   }

   public KeySourceException() {
   }
}
