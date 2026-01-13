package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.ChaCha20Poly1305Key;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Hex;
import com.google.crypto.tink.subtle.Random;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class ChaCha20Poly1305Jce implements Aead {
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final int NONCE_SIZE_IN_BYTES = 12;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private static final int KEY_SIZE_IN_BYTES = 32;
   private static final String CIPHER_NAME = "ChaCha20-Poly1305";
   private static final String KEY_NAME = "ChaCha20";
   private static final byte[] testKey = Hex.decode("808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9f");
   private static final byte[] testNonce = Hex.decode("070000004041424344454647");
   private static final byte[] testCiphertextOfEmpty = Hex.decode("a0784d7a4716f3feb4f64e7f4b39bf04");
   private final SecretKey keySpec;
   private final byte[] outputPrefix;
   private final Provider provider;

   private static boolean isValid(Cipher cipher) {
      try {
         AlgorithmParameterSpec params = new IvParameterSpec(testNonce);
         cipher.init(2, new SecretKeySpec(testKey, "ChaCha20"), params);
         byte[] output = cipher.doFinal(testCiphertextOfEmpty);
         if (output.length != 0) {
            return false;
         } else {
            cipher.init(2, new SecretKeySpec(testKey, "ChaCha20"), params);
            byte[] output2 = cipher.doFinal(testCiphertextOfEmpty);
            return output2.length == 0;
         }
      } catch (GeneralSecurityException var4) {
         return false;
      }
   }

   private ChaCha20Poly1305Jce(final byte[] key, final byte[] outputPrefix, Provider provider) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
      } else if (key.length != 32) {
         throw new InvalidKeyException("The key length in bytes must be 32.");
      } else {
         this.keySpec = new SecretKeySpec(key, "ChaCha20");
         this.outputPrefix = outputPrefix;
         this.provider = provider;
      }
   }

   @AccessesPartialKey
   public static Aead create(ChaCha20Poly1305Key key) throws GeneralSecurityException {
      Cipher cipher = getValidCipherInstance();
      return new ChaCha20Poly1305Jce(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray(), cipher.getProvider());
   }

   static Cipher getValidCipherInstance() throws GeneralSecurityException {
      Cipher cipher = EngineFactory.CIPHER.getInstance("ChaCha20-Poly1305");
      if (!isValid(cipher)) {
         throw new GeneralSecurityException("JCE does not support algorithm: ChaCha20-Poly1305");
      } else {
         return cipher;
      }
   }

   static Cipher getCipherInstance(Provider provider) throws GeneralSecurityException {
      return Cipher.getInstance("ChaCha20-Poly1305", provider);
   }

   public static boolean isSupported() {
      try {
         Cipher unused = getValidCipherInstance();
         return true;
      } catch (GeneralSecurityException var1) {
         return false;
      }
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext == null) {
         throw new NullPointerException("plaintext is null");
      } else {
         byte[] nonce = Random.randBytes(12);
         AlgorithmParameterSpec params = new IvParameterSpec(nonce);
         Cipher cipher = getCipherInstance(this.provider);
         cipher.init(1, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int outputSize = cipher.getOutputSize(plaintext.length);
         if (outputSize > Integer.MAX_VALUE - this.outputPrefix.length - 12) {
            throw new GeneralSecurityException("plaintext too long");
         } else {
            int len = this.outputPrefix.length + 12 + outputSize;
            byte[] output = Arrays.copyOf(this.outputPrefix, len);
            System.arraycopy(nonce, 0, output, this.outputPrefix.length, 12);
            int written = cipher.doFinal(plaintext, 0, plaintext.length, output, this.outputPrefix.length + 12);
            if (written != outputSize) {
               throw new GeneralSecurityException("not enough data written");
            } else {
               return output;
            }
         }
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext == null) {
         throw new NullPointerException("ciphertext is null");
      } else if (ciphertext.length < this.outputPrefix.length + 12 + 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         byte[] nonce = new byte[12];
         System.arraycopy(ciphertext, this.outputPrefix.length, nonce, 0, 12);
         AlgorithmParameterSpec params = new IvParameterSpec(nonce);
         Cipher cipher = getCipherInstance(this.provider);
         cipher.init(2, this.keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int offset = this.outputPrefix.length + 12;
         int len = ciphertext.length - this.outputPrefix.length - 12;
         return cipher.doFinal(ciphertext, offset, len);
      }
   }
}
