package com.nimbusds.jose.proc;

public class BadJOSEException extends Exception {
   public BadJOSEException(String message) {
      super(message);
   }

   public BadJOSEException(String message, Throwable cause) {
      super(message, cause);
   }
}
