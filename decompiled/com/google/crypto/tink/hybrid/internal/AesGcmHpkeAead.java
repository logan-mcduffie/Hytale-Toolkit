package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.aead.internal.InsecureNonceAesGcmJce;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

@Immutable
final class AesGcmHpkeAead implements HpkeAead {
   private final int keyLength;

   AesGcmHpkeAead(int keyLength) throws InvalidAlgorithmParameterException {
      if (keyLength != 16 && keyLength != 32) {
         throw new InvalidAlgorithmParameterException("Unsupported key length: " + keyLength);
      } else {
         this.keyLength = keyLength;
      }
   }

   @Override
   public byte[] seal(byte[] key, byte[] nonce, byte[] plaintext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      if (key.length != this.keyLength) {
         throw new InvalidAlgorithmParameterException("Unexpected key length: " + key.length);
      } else {
         InsecureNonceAesGcmJce aead = new InsecureNonceAesGcmJce(key);
         return aead.encrypt(nonce, plaintext, ciphertextOffset, associatedData);
      }
   }

   @Override
   public byte[] open(byte[] key, byte[] nonce, byte[] ciphertext, int ciphertextOffset, byte[] associatedData) throws GeneralSecurityException {
      if (key.length != this.keyLength) {
         throw new InvalidAlgorithmParameterException("Unexpected key length: " + key.length);
      } else {
         InsecureNonceAesGcmJce aead = new InsecureNonceAesGcmJce(key);
         return aead.decrypt(nonce, ciphertext, ciphertextOffset, associatedData);
      }
   }

   @Override
   public byte[] getAeadId() throws GeneralSecurityException {
      switch (this.keyLength) {
         case 16:
            return HpkeUtil.AES_128_GCM_AEAD_ID;
         case 32:
            return HpkeUtil.AES_256_GCM_AEAD_ID;
         default:
            throw new GeneralSecurityException("Could not determine HPKE AEAD ID");
      }
   }

   @Override
   public int getKeyLength() {
      return this.keyLength;
   }

   @Override
   public int getNonceLength() {
      return 12;
   }
}
