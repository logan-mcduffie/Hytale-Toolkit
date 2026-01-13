package com.nimbusds.jose.jwk.source;

public class JWKSetParseException extends JWKSetUnavailableException {
   private static final long serialVersionUID = 1L;

   public JWKSetParseException(String message, Throwable cause) {
      super(message, cause);
   }
}
