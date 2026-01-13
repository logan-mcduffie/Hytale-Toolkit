package com.google.crypto.tink.subtle;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesCtrJceCipher implements IndCpaCipher {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final ThreadLocal<Cipher> localCipher = new ThreadLocal<Cipher>() {
      protected Cipher initialValue() {
         try {
            return EngineFactory.CIPHER.getInstance("AES/CTR/NoPadding");
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };
   private static final String KEY_ALGORITHM = "AES";
   private static final String CIPHER_ALGORITHM = "AES/CTR/NoPadding";
   private static final int MIN_IV_SIZE_IN_BYTES = 12;
   private final SecretKeySpec keySpec;
   private final int ivSize;
   private final int blockSize;

   public AesCtrJceCipher(final byte[] key, int ivSize) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-CTR in FIPS-mode, as BoringCrypto module is not available.");
      } else {
         Validators.validateAesKeySize(key.length);
         this.keySpec = new SecretKeySpec(key, "AES");
         this.blockSize = localCipher.get().getBlockSize();
         if (ivSize >= 12 && ivSize <= this.blockSize) {
            this.ivSize = ivSize;
         } else {
            throw new GeneralSecurityException("invalid IV size");
         }
      }
   }

   @Override
   public byte[] encrypt(final byte[] plaintext) throws GeneralSecurityException {
      if (plaintext.length > Integer.MAX_VALUE - this.ivSize) {
         throw new GeneralSecurityException("plaintext length can not exceed " + (Integer.MAX_VALUE - this.ivSize));
      } else {
         byte[] ciphertext = new byte[this.ivSize + plaintext.length];
         byte[] iv = Random.randBytes(this.ivSize);
         System.arraycopy(iv, 0, ciphertext, 0, this.ivSize);
         this.doCtr(plaintext, 0, plaintext.length, ciphertext, this.ivSize, iv, true);
         return ciphertext;
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext) throws GeneralSecurityException {
      if (ciphertext.length < this.ivSize) {
         throw new GeneralSecurityException("ciphertext too short");
      } else {
         byte[] iv = new byte[this.ivSize];
         System.arraycopy(ciphertext, 0, iv, 0, this.ivSize);
         byte[] plaintext = new byte[ciphertext.length - this.ivSize];
         this.doCtr(ciphertext, this.ivSize, ciphertext.length - this.ivSize, plaintext, 0, iv, false);
         return plaintext;
      }
   }

   private void doCtr(final byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset, final byte[] iv, boolean encrypt) throws GeneralSecurityException {
      Cipher cipher = localCipher.get();
      byte[] counter = new byte[this.blockSize];
      System.arraycopy(iv, 0, counter, 0, this.ivSize);
      IvParameterSpec paramSpec = new IvParameterSpec(counter);
      if (encrypt) {
         cipher.init(1, this.keySpec, paramSpec);
      } else {
         cipher.init(2, this.keySpec, paramSpec);
      }

      int numBytes = cipher.doFinal(input, inputOffset, inputLen, output, outputOffset);
      if (numBytes != inputLen) {
         throw new GeneralSecurityException("stored output's length does not match input's length");
      }
   }
}
