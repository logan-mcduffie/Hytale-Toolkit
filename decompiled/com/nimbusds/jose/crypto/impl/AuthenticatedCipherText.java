package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.Objects;

@Immutable
public final class AuthenticatedCipherText {
   private final byte[] cipherText;
   private final byte[] authenticationTag;

   public AuthenticatedCipherText(byte[] cipherText, byte[] authenticationTag) {
      this.cipherText = Objects.requireNonNull(cipherText);
      this.authenticationTag = Objects.requireNonNull(authenticationTag);
   }

   public byte[] getCipherText() {
      return this.cipherText;
   }

   public byte[] getAuthenticationTag() {
      return this.authenticationTag;
   }
}
