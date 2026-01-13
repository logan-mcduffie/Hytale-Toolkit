package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface HpkeAead {
   default byte[] seal(byte[] key, byte[] nonce, byte[] plaintext, byte[] associatedData) throws GeneralSecurityException {
      return this.seal(key, nonce, plaintext, 0, associatedData);
   }

   byte[] seal(byte[] key, byte[] nonce, byte[] plaintext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException;

   default byte[] open(byte[] key, byte[] nonce, byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException {
      return this.open(key, nonce, ciphertext, 0, associatedData);
   }

   byte[] open(byte[] key, byte[] nonce, byte[] ciphertext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException;

   byte[] getAeadId() throws GeneralSecurityException;

   int getKeyLength();

   int getNonceLength();
}
