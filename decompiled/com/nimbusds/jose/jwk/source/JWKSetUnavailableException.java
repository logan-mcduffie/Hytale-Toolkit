package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;

public class JWKSetUnavailableException extends KeySourceException {
   private static final long serialVersionUID = 1L;

   public JWKSetUnavailableException(String message) {
      super(message);
   }

   public JWKSetUnavailableException(String message, Throwable cause) {
      super(message, cause);
   }
}
