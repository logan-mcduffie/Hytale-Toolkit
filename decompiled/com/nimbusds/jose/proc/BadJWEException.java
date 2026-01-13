package com.nimbusds.jose.proc;

public class BadJWEException extends BadJOSEException {
   public BadJWEException(String message) {
      super(message);
   }

   public BadJWEException(String message, Throwable cause) {
      super(message, cause);
   }
}
