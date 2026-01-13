package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.AesGcmSivKey;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.Hex;
import com.google.crypto.tink.subtle.Random;
import com.google.crypto.tink.subtle.Validators;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesGcmSiv implements Aead {
   private static final byte[] testPlaintext = Hex.decode("7a806c");
   private static final byte[] testAad = Hex.decode("46bb91c3c5");
   private static final byte[] testKey = Hex.decode("36864200e0eaf5284d884a0e77d31646");
   private static final byte[] testNounce = Hex.decode("bae8e37fc83441b16034566b");
   private static final byte[] testResult = Hex.decode("af60eb711bd85bc1e4d3e0a462e074eea428a8");
   private static final int IV_SIZE_IN_BYTES = 12;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private final AesGcmSiv.ThrowingSupplier<Cipher> cipherSupplier;
   private final SecretKey keySpec;
   private final byte[] outputPrefix;

   public static boolean isAesGcmSivCipher(Cipher cipher) {
      try {
         AlgorithmParameterSpec params = getParams(testNounce);
         cipher.init(2, new SecretKeySpec(testKey, "AES"), params);
         cipher.updateAAD(testAad);
         byte[] output = cipher.doFinal(testResult, 0, testResult.length);
         return Bytes.equal(output, testPlaintext);
      } catch (GeneralSecurityException var3) {
         return false;
      }
   }

   @AccessesPartialKey
   public static Aead create(AesGcmSivKey key, AesGcmSiv.ThrowingSupplier<Cipher> cipherSupplier) throws GeneralSecurityException {
      if (!isAesGcmSivCipher(cipherSupplier.get())) {
         throw new IllegalStateException("Cipher does not implement AES GCM SIV.");
      } else {
         return new AesGcmSiv(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray(), cipherSupplier);
      }
   }

   private AesGcmSiv(byte[] key, byte[] outputPrefix, AesGcmSiv.ThrowingSupplier<Cipher> cipherSupplier) throws GeneralSecurityException {
      this.outputPrefix = outputPrefix;
      Validators.validateAesKeySize(key.length);
      this.keySpec = new SecretKeySpec(key, "AES");
      this.cipherSupplier = cipherSupplier;
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      Cipher cipher = this.cipherSupplier.get();
      if (plaintext.length > 2147483619 - this.outputPrefix.length) {
         throw new GeneralSecurityException("plaintext too long");
      } else {
         int ciphertextLen = this.outputPrefix.length + 12 + plaintext.length + 16;
         byte[] ciphertext = Arrays.copyOf(this.outputPrefix, ciphertextLen);
         byte[] iv = Random.randBytes(12);
         System.arraycopy(iv, 0, ciphertext, this.outputPrefix.length, 12);
         AlgorithmParameterSpec params = getParams(iv);
         cipher.init(1, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int written = cipher.doFinal(plaintext, 0, plaintext.length, ciphertext, this.outputPrefix.length + 12);
         if (written != plaintext.length + 16) {
            int actualTagSize = written - plaintext.length;
            throw new GeneralSecurityException(String.format("encryption failed; AES-GCM-SIV tag must be %s bytes, but got only %s bytes", 16, actualTagSize));
         } else {
            return ciphertext;
         }
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext.length < this.outputPrefix.length + 12 + 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         Cipher cipher = this.cipherSupplier.get();
         AlgorithmParameterSpec params = getParams(ciphertext, this.outputPrefix.length, 12);
         cipher.init(2, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int offset = this.outputPrefix.length + 12;
         int len = ciphertext.length - this.outputPrefix.length - 12;
         return cipher.doFinal(ciphertext, offset, len);
      }
   }

   private static AlgorithmParameterSpec getParams(final byte[] iv) {
      return getParams(iv, 0, iv.length);
   }

   private static AlgorithmParameterSpec getParams(final byte[] buf, int offset, int len) {
      return new GCMParameterSpec(128, buf, offset, len);
   }

   public interface ThrowingSupplier<T> {
      T get() throws GeneralSecurityException;
   }
}
