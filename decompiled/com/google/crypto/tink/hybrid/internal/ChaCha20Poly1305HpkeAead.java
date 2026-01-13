package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Poly1305;
import com.google.crypto.tink.aead.internal.InsecureNonceChaCha20Poly1305Jce;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;

@Immutable
final class ChaCha20Poly1305HpkeAead implements HpkeAead {
   @Override
   public byte[] seal(byte[] key, byte[] nonce, byte[] plaintext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      if (key.length != this.getKeyLength()) {
         throw new InvalidAlgorithmParameterException("Unexpected key length: " + this.getKeyLength());
      } else if (InsecureNonceChaCha20Poly1305Jce.isSupported()) {
         InsecureNonceChaCha20Poly1305Jce aead = InsecureNonceChaCha20Poly1305Jce.create(key);
         return aead.encrypt(nonce, plaintext, ciphertextOffset, associatedData);
      } else {
         InsecureNonceChaCha20Poly1305 aead = new InsecureNonceChaCha20Poly1305(key);
         byte[] aeadCiphertext = aead.encrypt(nonce, plaintext, associatedData);
         if (aeadCiphertext.length > Integer.MAX_VALUE - ciphertextOffset) {
            throw new InvalidAlgorithmParameterException("Plaintext too long");
         } else {
            byte[] ciphertext = new byte[ciphertextOffset + aeadCiphertext.length];
            System.arraycopy(aeadCiphertext, 0, ciphertext, ciphertextOffset, aeadCiphertext.length);
            return ciphertext;
         }
      }
   }

   @Override
   public byte[] open(byte[] key, byte[] nonce, byte[] ciphertext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      if (key.length != this.getKeyLength()) {
         throw new InvalidAlgorithmParameterException("Unexpected key length: " + this.getKeyLength());
      } else if (InsecureNonceChaCha20Poly1305Jce.isSupported()) {
         InsecureNonceChaCha20Poly1305Jce aead = InsecureNonceChaCha20Poly1305Jce.create(key);
         return aead.decrypt(nonce, ciphertext, ciphertextOffset, associatedData);
      } else {
         byte[] aeadCiphertext = Arrays.copyOfRange(ciphertext, ciphertextOffset, ciphertext.length);
         InsecureNonceChaCha20Poly1305 aead = new InsecureNonceChaCha20Poly1305(key);
         return aead.decrypt(nonce, aeadCiphertext, associatedData);
      }
   }

   @Override
   public byte[] getAeadId() {
      return HpkeUtil.CHACHA20_POLY1305_AEAD_ID;
   }

   @Override
   public int getKeyLength() {
      return 32;
   }

   @Override
   public int getNonceLength() {
      return 12;
   }
}
