package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public final class InsecureNonceAesGcmJce {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   public static final int IV_SIZE_IN_BYTES = 12;
   public static final int TAG_SIZE_IN_BYTES = 16;
   private final SecretKey keySpec;

   public InsecureNonceAesGcmJce(final byte[] key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-GCM in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         this.keySpec = AesGcmJceUtil.getSecretKey(key);
      }
   }

   public byte[] encrypt(final byte[] iv, final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      return this.encrypt(iv, plaintext, 0, associatedData);
   }

   public byte[] encrypt(final byte[] iv, final byte[] plaintext, int ciphertextOffset, final byte[] associatedData) throws GeneralSecurityException {
      if (iv.length != 12) {
         throw new GeneralSecurityException("iv is wrong size");
      } else {
         AlgorithmParameterSpec params = AesGcmJceUtil.getParams(iv);
         Cipher localCipher = AesGcmJceUtil.getThreadLocalCipher();
         localCipher.init(1, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            localCipher.updateAAD(associatedData);
         }

         int ciphertextSize = localCipher.getOutputSize(plaintext.length);
         if (ciphertextSize > Integer.MAX_VALUE - ciphertextOffset) {
            throw new GeneralSecurityException("plaintext too long");
         } else {
            int outputSize = ciphertextOffset + ciphertextSize;
            byte[] output = new byte[outputSize];
            int written = localCipher.doFinal(plaintext, 0, plaintext.length, output, ciphertextOffset);
            if (written != ciphertextSize) {
               throw new GeneralSecurityException("not enough data written");
            } else {
               return output;
            }
         }
      }
   }

   public byte[] decrypt(final byte[] iv, final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      return this.decrypt(iv, ciphertext, 0, associatedData);
   }

   public byte[] decrypt(final byte[] iv, final byte[] ciphertextWithPrefix, int ciphertextOffset, final byte[] associatedData) throws GeneralSecurityException {
      if (iv.length != 12) {
         throw new GeneralSecurityException("iv is wrong size");
      } else if (ciphertextWithPrefix.length < 16 + ciphertextOffset) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         AlgorithmParameterSpec params = AesGcmJceUtil.getParams(iv);
         Cipher localCipher = AesGcmJceUtil.getThreadLocalCipher();
         localCipher.init(2, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            localCipher.updateAAD(associatedData);
         }

         return localCipher.doFinal(ciphertextWithPrefix, ciphertextOffset, ciphertextWithPrefix.length - ciphertextOffset);
      }
   }
}
