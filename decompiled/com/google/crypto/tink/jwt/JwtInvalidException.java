package com.google.crypto.tink.jwt;

import java.security.GeneralSecurityException;

public final class JwtInvalidException extends GeneralSecurityException {
   public JwtInvalidException(String message) {
      super(message);
   }
}
