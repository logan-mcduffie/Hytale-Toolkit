package com.nimbusds.jose.proc;

public class BadJWSException extends BadJOSEException {
   public BadJWSException(String message) {
      super(message);
   }

   public BadJWSException(String message, Throwable cause) {
      super(message, cause);
   }
}
