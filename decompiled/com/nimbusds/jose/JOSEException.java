package com.nimbusds.jose;

public class JOSEException extends Exception {
   private static final long serialVersionUID = 1L;

   public JOSEException(String message) {
      super(message);
   }

   public JOSEException(String message, Throwable cause) {
      super(message, cause);
   }

   public JOSEException(Throwable cause) {
      super(cause);
   }

   public JOSEException() {
   }
}
