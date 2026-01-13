package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.BadJOSEException;

public class BadJWTException extends BadJOSEException {
   public BadJWTException(String message) {
      super(message);
   }

   public BadJWTException(String message, Throwable cause) {
      super(message, cause);
   }
}
