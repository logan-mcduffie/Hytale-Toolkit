package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.XAesGcmKey;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.prf.AesCmacPrfKey;
import com.google.crypto.tink.prf.AesCmacPrfParameters;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.subtle.PrfAesCmac;
import com.google.crypto.tink.subtle.Random;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class XAesGcm implements Aead {
   private static final int IV_SIZE_IN_BYTES = 12;
   private static final int TAG_SIZE_IN_BYTES = 16;
   private static final int DERIVED_KEY_SIZE_IN_BYTES = 32;
   private static final int MIN_SALT_SIZE_IN_BYTES = 8;
   private static final int MAX_SALT_SIZE_IN_BYTES = 12;
   private final byte[] outputPrefix;
   private final int saltSize;
   private final Prf cmac;

   @AccessesPartialKey
   private static Prf createCmac(byte[] key) throws GeneralSecurityException {
      return PrfAesCmac.create(AesCmacPrfKey.create(AesCmacPrfParameters.create(key.length), SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get())));
   }

   private XAesGcm(final byte[] key, Bytes outputPrefix, int saltSize) throws GeneralSecurityException {
      this.cmac = createCmac(key);
      this.outputPrefix = outputPrefix.toByteArray();
      this.saltSize = saltSize;
   }

   @AccessesPartialKey
   public static Aead create(XAesGcmKey key) throws GeneralSecurityException {
      if (key.getParameters().getSaltSizeBytes() >= 8 && key.getParameters().getSaltSizeBytes() <= 12) {
         return new XAesGcm(key.getKeyBytes().toByteArray(InsecureSecretKeyAccess.get()), key.getOutputPrefix(), key.getParameters().getSaltSizeBytes());
      } else {
         throw new GeneralSecurityException("invalid salt size");
      }
   }

   private byte[] derivePerMessageKey(byte[] salt) throws GeneralSecurityException {
      byte[] derivationBlock1 = new byte[]{0, 1, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      byte[] derivationBlock2 = new byte[]{0, 2, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      if (salt.length <= 12 && salt.length >= 8) {
         System.arraycopy(salt, 0, derivationBlock1, 4, salt.length);
         System.arraycopy(salt, 0, derivationBlock2, 4, salt.length);
         byte[] key = new byte[32];
         System.arraycopy(this.cmac.compute(derivationBlock1, 16), 0, key, 0, 16);
         System.arraycopy(this.cmac.compute(derivationBlock2, 16), 0, key, 16, 16);
         return key;
      } else {
         throw new GeneralSecurityException("invalid salt size");
      }
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      if (plaintext == null) {
         throw new NullPointerException("plaintext is null");
      } else {
         byte[] saltAndIv = Random.randBytes(this.saltSize + 12);
         byte[] salt = Arrays.copyOf(saltAndIv, this.saltSize);
         byte[] iv = Arrays.copyOfRange(saltAndIv, this.saltSize, this.saltSize + 12);
         InsecureNonceAesGcmJce gcm = new InsecureNonceAesGcmJce(this.derivePerMessageKey(salt));
         byte[] ciphertext = gcm.encrypt(iv, plaintext, this.outputPrefix.length + this.saltSize + iv.length, associatedData);
         System.arraycopy(this.outputPrefix, 0, ciphertext, 0, this.outputPrefix.length);
         System.arraycopy(saltAndIv, 0, ciphertext, this.outputPrefix.length, saltAndIv.length);
         return ciphertext;
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      if (ciphertext == null) {
         throw new NullPointerException("ciphertext is null");
      } else if (ciphertext.length < this.outputPrefix.length + this.saltSize + 12 + 16) {
         throw new GeneralSecurityException("ciphertext too short");
      } else if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
         throw new GeneralSecurityException("Decryption failed (OutputPrefix mismatch).");
      } else {
         int prefixAndSaltSize = this.outputPrefix.length + this.saltSize;
         InsecureNonceAesGcmJce gcm = new InsecureNonceAesGcmJce(
            this.derivePerMessageKey(Arrays.copyOfRange(ciphertext, this.outputPrefix.length, prefixAndSaltSize))
         );
         return gcm.decrypt(Arrays.copyOfRange(ciphertext, prefixAndSaltSize, prefixAndSaltSize + 12), ciphertext, prefixAndSaltSize + 12, associatedData);
      }
   }
}
