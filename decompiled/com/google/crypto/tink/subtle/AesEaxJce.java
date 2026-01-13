package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.AesEaxKey;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesEaxJce implements Aead {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
   private static final ThreadLocal<Cipher> localCtrCipher = new ThreadLocal<Cipher>() {
      protected Cipher initialValue() {
         try {
            return EngineFactory.CIPHER.getInstance("AES/CTR/NOPADDING");
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };
   static final int BLOCK_SIZE_IN_BYTES = 16;
   static final int TAG_SIZE_IN_BYTES = 16;
   private final byte[] outputPrefix;
   private final Prf cmac;
   private final SecretKeySpec keySpec;
   private final int ivSizeInBytes;

   @AccessesPartialKey
   public static Aead create(AesEaxKey key) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-EAX in FIPS-mode.");
      } else if (key.getParameters().getTagSizeBytes() != 16) {
         throw new GeneralSecurityException("AesEaxJce only supports 16 byte tag size, not " + key.getParameters().getTagSizeBytes());
      } else {
         return new AesEaxJce(
            key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getParameters().getIvSizeBytes(), key.getOutputPrefix().toByteArray()
         );
      }
   }

   @AccessesPartialKey
   private static Prf createCmac(byte[] key) throws GeneralSecurityException {
      return PrfAesCmac.create(AesCmacPrfKey.create(AesCmacPrfParameters.create(key.length), SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get())));
   }

   private AesEaxJce(final byte[] key, int ivSizeInBytes, byte[] outputPrefix) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use AES-EAX in FIPS-mode.");
      } else if (ivSizeInBytes != 12 && ivSizeInBytes != 16) {
         throw new IllegalArgumentException("IV size should be either 12 or 16 bytes");
      } else {
         this.ivSizeInBytes = ivSizeInBytes;
         Validators.validateAesKeySize(key.length);
         this.keySpec = new SecretKeySpec(key, "AES");
         this.cmac = createCmac(key);
         this.outputPrefix = outputPrefix;
      }
   }

   public AesEaxJce(final byte[] key, int ivSizeInBytes) throws GeneralSecurityException {
      this(key, ivSizeInBytes, new byte[0]);
   }

   private byte[] omac(int tag, final byte[] data, int offset, int length) throws GeneralSecurityException {
      byte[] input = new byte[length + 16];
      input[15] = (byte)tag;
      System.arraycopy(data, offset, input, 16, length);
      return this.cmac.compute(input, 16);
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext.length > Integer.MAX_VALUE - this.outputPrefix.length - this.ivSizeInBytes - 16) {
         throw new GeneralSecurityException("plaintext too long");
      } else {
         byte[] ciphertext = Arrays.copyOf(this.outputPrefix, this.outputPrefix.length + this.ivSizeInBytes + plaintext.length + 16);
         byte[] iv = Random.randBytes(this.ivSizeInBytes);
         System.arraycopy(iv, 0, ciphertext, this.outputPrefix.length, this.ivSizeInBytes);
         byte[] n = this.omac(0, iv, 0, iv.length);
         byte[] aad = associatedData;
         if (associatedData == null) {
            aad = new byte[0];
         }

         byte[] h = this.omac(1, aad, 0, aad.length);
         Cipher ctr = localCtrCipher.get();
         ctr.init(1, this.keySpec, new IvParameterSpec(n));
         ctr.doFinal(plaintext, 0, plaintext.length, ciphertext, this.outputPrefix.length + this.ivSizeInBytes);
         byte[] t = this.omac(2, ciphertext, this.outputPrefix.length + this.ivSizeInBytes, plaintext.length);
         int offset = this.outputPrefix.length + plaintext.length + this.ivSizeInBytes;

         for (int i = 0; i < 16; i++) {
            ciphertext[offset + i] = (byte)(h[i] ^ n[i] ^ t[i]);
         }

         return ciphertext;
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      int plaintextLength = ciphertext.length - this.outputPrefix.length - this.ivSizeInBytes - 16;
      if (plaintextLength < 0) {
         throw new GeneralSecurityException("ciphertext too short");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         byte[] n = this.omac(0, ciphertext, this.outputPrefix.length, this.ivSizeInBytes);
         byte[] aad = associatedData;
         if (associatedData == null) {
            aad = new byte[0];
         }

         byte[] h = this.omac(1, aad, 0, aad.length);
         byte[] t = this.omac(2, ciphertext, this.outputPrefix.length + this.ivSizeInBytes, plaintextLength);
         byte res = 0;
         int offset = ciphertext.length - 16;

         for (int i = 0; i < 16; i++) {
            res = (byte)(res | ciphertext[offset + i] ^ h[i] ^ n[i] ^ t[i]);
         }

         if (res != 0) {
            throw new AEADBadTagException("tag mismatch");
         } else {
            Cipher ctr = localCtrCipher.get();
            ctr.init(1, this.keySpec, new IvParameterSpec(n));
            return ctr.doFinal(ciphertext, this.outputPrefix.length + this.ivSizeInBytes, plaintextLength);
         }
      }
   }
}
