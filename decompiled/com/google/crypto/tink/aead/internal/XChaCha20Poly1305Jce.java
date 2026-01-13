package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.XChaCha20Poly1305Key;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.Random;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Immutable
public final class XChaCha20Poly1305Jce implements Aead {
   private static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final int NONCE_SIZE_IN_BYTES = 24;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private static final int KEY_SIZE_IN_BYTES = 32;
   private static final String KEY_NAME = "ChaCha20";
   private final byte[] key;
   private final byte[] outputPrefix;
   private final Provider provider;

   private XChaCha20Poly1305Jce(final byte[] key, final byte[] outputPrefix, Provider provider) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ChaCha20Poly1305 in FIPS-mode.");
      } else if (key.length != 32) {
         throw new InvalidKeyException("The key length in bytes must be 32.");
      } else {
         this.key = key;
         this.outputPrefix = outputPrefix;
         this.provider = provider;
      }
   }

   @AccessesPartialKey
   public static Aead create(XChaCha20Poly1305Key key) throws GeneralSecurityException {
      Cipher cipher = ChaCha20Poly1305Jce.getValidCipherInstance();
      return new XChaCha20Poly1305Jce(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix().toByteArray(), cipher.getProvider());
   }

   public static boolean isSupported() {
      return ChaCha20Poly1305Jce.isSupported();
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext == null) {
         throw new NullPointerException("plaintext is null");
      } else {
         byte[] nonce = Random.randBytes(24);
         byte[] subkey = ChaCha20Util.hChaCha20(this.key, nonce);
         SecretKeySpec keySpec = new SecretKeySpec(subkey, "ChaCha20");
         AlgorithmParameterSpec params = new IvParameterSpec(getChaCha20Nonce(nonce));
         Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
         cipher.init(1, keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int outputSize = cipher.getOutputSize(plaintext.length);
         if (outputSize > Integer.MAX_VALUE - this.outputPrefix.length - 24) {
            throw new GeneralSecurityException("plaintext too long");
         } else {
            int len = this.outputPrefix.length + 24 + outputSize;
            byte[] output = Arrays.copyOf(this.outputPrefix, len);
            System.arraycopy(nonce, 0, output, this.outputPrefix.length, 24);
            int written = cipher.doFinal(plaintext, 0, plaintext.length, output, this.outputPrefix.length + 24);
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
      } else if (ciphertext.length < this.outputPrefix.length + 24 + 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         byte[] nonce = new byte[24];
         System.arraycopy(ciphertext, this.outputPrefix.length, nonce, 0, 24);
         byte[] subkey = ChaCha20Util.hChaCha20(this.key, nonce);
         SecretKeySpec keySpec = new SecretKeySpec(subkey, "ChaCha20");
         AlgorithmParameterSpec params = new IvParameterSpec(getChaCha20Nonce(nonce));
         Cipher cipher = ChaCha20Poly1305Jce.getCipherInstance(this.provider);
         cipher.init(2, keySpec, params);
         if (associatedData != null && associatedData.length != 0) {
            cipher.updateAAD(associatedData);
         }

         int offset = this.outputPrefix.length + 24;
         int len = ciphertext.length - this.outputPrefix.length - 24;
         return cipher.doFinal(ciphertext, offset, len);
      }
   }

   static byte[] getChaCha20Nonce(byte[] nonce) {
      byte[] chacha20Nonce = new byte[12];
      System.arraycopy(nonce, 16, chacha20Nonce, 4, 8);
      return chacha20Nonce;
   }
}
