package com.nimbusds.jose.jwk.source;

public class JWKSetRetrievalException extends JWKSetUnavailableException {
   private static final long serialVersionUID = 1L;

   public JWKSetRetrievalException(String message, Throwable cause) {
      super(message, cause);
   }
}
