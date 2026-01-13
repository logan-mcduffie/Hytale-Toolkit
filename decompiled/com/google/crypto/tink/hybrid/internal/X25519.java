package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface X25519 {
   X25519.KeyPair generateKeyPair() throws GeneralSecurityException;

   byte[] computeSharedSecret(byte[] privateValue, byte[] peersPublicValue) throws GeneralSecurityException;

   public static final class KeyPair {
      public final byte[] privateKey;
      public final byte[] publicKey;

      public KeyPair(byte[] privateKey, byte[] publicKey) {
         this.privateKey = privateKey;
         this.publicKey = publicKey;
      }
   }
}
