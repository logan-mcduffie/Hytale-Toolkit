package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class InsecureNonceChaCha20Poly1305Jce {
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final int NONCE_SIZE_IN_BYTES = 12;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private static final int KEY_SIZE_IN_BYTES = 32;
   private static final String KEY_NAME = "ChaCha20";
   private final SecretKey keySpec;
   private final Provider provider;

   private InsecureNonceChaCha20Poly1305Jce(final byte[] key, Provider provider) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
      } else if (key.length != 32) {
         throw new InvalidKeyException("The key length in bytes must be 32.");
      } else {
         this.keySpec = new SecretKeySpec(key, "ChaCha20");
         this.provider = provider;
      }
   }

   @AccessesPartialKey
   public static InsecureNonceChaCha20Poly1305Jce create(final byte[] key) throws GeneralSecurityException {
      Cipher cipher = ChaCha20Poly1305Jce.getValidCipherInstance();
      return new InsecureNonceChaCha20Poly1305Jce(key, cipher.getProvider());
   }

   public static boolean isSupported() {
      return ChaCha20Poly1305Jce.isSupported();
   }

   public byte[] encrypt(final byte[] nonce, final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      return this.encrypt(nonce, plaintext, 0, associatedData);
   }

   public byte[] encrypt(final byte[] nonce, final byte[] plaintext, int ciphertextOffset, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext == null) {
         throw new NullPointerException("plaintext is null");
      } else if (nonce.length != 12) {
         throw new GeneralSecurityException("nonce length must be 12 bytes.");
      } else {
         AlgorithmParameterSpec params = new IvParameterSpec(nonce);
         Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
         cipher.init(1, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int ciphertextSize = cipher.getOutputSize(plaintext.length);
         if (ciphertextSize > Integer.MAX_VALUE - ciphertextOffset) {
            throw new GeneralSecurityException("plaintext too long");
         } else {
            int outputSize = ciphertextOffset + ciphertextSize;
            byte[] output = new byte[outputSize];
            int written = cipher.doFinal(plaintext, 0, plaintext.length, output, ciphertextOffset);
            if (written != ciphertextSize) {
               throw new GeneralSecurityException("not enough data written");
            } else {
               return output;
            }
         }
      }
   }

   public byte[] decrypt(final byte[] nonce, final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      return this.decrypt(nonce, ciphertext, 0, associatedData);
   }

   public byte[] decrypt(final byte[] nonce, final byte[] ciphertextWithPrefix, int ciphertextOffset, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertextWithPrefix == null) {
         throw new NullPointerException("ciphertext is null");
      } else if (nonce.length != 12) {
         throw new GeneralSecurityException("nonce length must be 12 bytes.");
      } else if (ciphertextWithPrefix.length < ciphertextOffset + 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         AlgorithmParameterSpec params = new IvParameterSpec(nonce);
         Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
         cipher.init(2, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         return cipher.doFinal(ciphertextWithPrefix, ciphertextOffset, ciphertextWithPrefix.length - ciphertextOffset);
      }
   }
}
