package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.aead.AesCtrHmacAeadKey;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.aead.internal.AesGcmJceUtil;
import com.google.crypto.tink.daead.AesSivKey;
import com.google.crypto.tink.daead.AesSivParameters;
import com.google.crypto.tink.hybrid.EciesParameters;
import com.google.crypto.tink.subtle.AesSiv;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EncryptThenAuthenticate;
import com.google.crypto.tink.subtle.Random;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public final class EciesDemHelper {
   private static final byte[] EMPTY_AAD = new byte[0];

   public static EciesDemHelper.Dem getDem(EciesParameters parameters) throws GeneralSecurityException {
      Parameters demParameters = parameters.getDemParameters();
      if (demParameters instanceof AesGcmParameters) {
         return new EciesDemHelper.AesGcmDem((AesGcmParameters)demParameters);
      } else if (demParameters instanceof AesCtrHmacAeadParameters) {
         return new EciesDemHelper.AesCtrHmacDem((AesCtrHmacAeadParameters)demParameters);
      } else if (demParameters instanceof AesSivParameters) {
         return new EciesDemHelper.AesSivDem((AesSivParameters)demParameters);
      } else {
         throw new GeneralSecurityException("Unsupported DEM parameters: " + demParameters);
      }
   }

   private EciesDemHelper() {
   }

   private static final class AesCtrHmacDem implements EciesDemHelper.Dem {
      private final AesCtrHmacAeadParameters parameters;
      private final int keySizeInBytes;

      public AesCtrHmacDem(AesCtrHmacAeadParameters parameters) {
         this.parameters = parameters;
         this.keySizeInBytes = parameters.getAesKeySizeBytes() + parameters.getHmacKeySizeBytes();
      }

      @Override
      public int getSymmetricKeySizeInBytes() {
         return this.keySizeInBytes;
      }

      @AccessesPartialKey
      private Aead getAead(byte[] symmetricKeyValue) throws GeneralSecurityException {
         byte[] aesCtrKeyValue = Arrays.copyOf(symmetricKeyValue, this.parameters.getAesKeySizeBytes());
         byte[] hmacKeyValue = Arrays.copyOfRange(
            symmetricKeyValue, this.parameters.getAesKeySizeBytes(), this.parameters.getAesKeySizeBytes() + this.parameters.getHmacKeySizeBytes()
         );
         return EncryptThenAuthenticate.create(
            AesCtrHmacAeadKey.builder()
               .setParameters(this.parameters)
               .setAesKeyBytes(SecretBytes.copyFrom(aesCtrKeyValue, InsecureSecretKeyAccess.get()))
               .setHmacKeyBytes(SecretBytes.copyFrom(hmacKeyValue, InsecureSecretKeyAccess.get()))
               .build()
         );
      }

      @Override
      public byte[] encrypt(byte[] demKeyValue, byte[] prefix, byte[] header, byte[] plaintext) throws GeneralSecurityException {
         byte[] ciphertext = this.getAead(demKeyValue).encrypt(plaintext, EciesDemHelper.EMPTY_AAD);
         return Bytes.concat(prefix, header, ciphertext);
      }

      @Override
      public byte[] decrypt(byte[] demKeyValue, byte[] ciphertext, int prefixAndHeaderSize) throws GeneralSecurityException {
         if (ciphertext.length < prefixAndHeaderSize) {
            throw new GeneralSecurityException("ciphertext too short");
         } else {
            byte[] demCiphertext = Arrays.copyOfRange(ciphertext, prefixAndHeaderSize, ciphertext.length);
            return this.getAead(demKeyValue).decrypt(demCiphertext, EciesDemHelper.EMPTY_AAD);
         }
      }
   }

   private static final class AesGcmDem implements EciesDemHelper.Dem {
      private static final int AES_GCM_IV_SIZE_IN_BYTES = 12;
      private static final int AES_GCM_TAG_SIZE_IN_BYTES = 16;
      private final int keySizeInBytes;

      public AesGcmDem(AesGcmParameters parameters) throws GeneralSecurityException {
         if (parameters.getIvSizeBytes() != 12) {
            throw new GeneralSecurityException("invalid IV size");
         } else if (parameters.getTagSizeBytes() != 16) {
            throw new GeneralSecurityException("invalid tag size");
         } else if (parameters.getVariant() != AesGcmParameters.Variant.NO_PREFIX) {
            throw new GeneralSecurityException("invalid variant");
         } else {
            this.keySizeInBytes = parameters.getKeySizeBytes();
         }
      }

      @Override
      public int getSymmetricKeySizeInBytes() {
         return this.keySizeInBytes;
      }

      @Override
      public byte[] encrypt(byte[] demKeyValue, byte[] prefix, byte[] header, byte[] plaintext) throws GeneralSecurityException {
         if (demKeyValue.length != this.keySizeInBytes) {
            throw new GeneralSecurityException("invalid key size");
         } else {
            SecretKey keySpec = AesGcmJceUtil.getSecretKey(demKeyValue);
            byte[] nonce = Random.randBytes(12);
            AlgorithmParameterSpec params = AesGcmJceUtil.getParams(nonce);
            Cipher cipher = AesGcmJceUtil.getThreadLocalCipher();
            cipher.init(1, keySpec, params);
            int outputSize = cipher.getOutputSize(plaintext.length);
            int prefixAndHeaderSize = prefix.length + header.length;
            if (outputSize > Integer.MAX_VALUE - prefixAndHeaderSize - 12) {
               throw new GeneralSecurityException("plaintext too long");
            } else {
               int len = prefixAndHeaderSize + 12 + outputSize;
               byte[] output = Arrays.copyOf(prefix, len);
               System.arraycopy(header, 0, output, prefix.length, header.length);
               System.arraycopy(nonce, 0, output, prefixAndHeaderSize, 12);
               int written = cipher.doFinal(plaintext, 0, plaintext.length, output, prefixAndHeaderSize + 12);
               if (written != outputSize) {
                  throw new GeneralSecurityException("not enough data written");
               } else {
                  return output;
               }
            }
         }
      }

      @Override
      public byte[] decrypt(byte[] demKeyValue, byte[] ciphertext, int prefixAndHeaderSize) throws GeneralSecurityException {
         if (ciphertext.length < prefixAndHeaderSize) {
            throw new GeneralSecurityException("ciphertext too short");
         } else if (demKeyValue.length != this.keySizeInBytes) {
            throw new GeneralSecurityException("invalid key size");
         } else {
            SecretKey key = AesGcmJceUtil.getSecretKey(demKeyValue);
            if (ciphertext.length < prefixAndHeaderSize + 12 + 16) {
               throw new GeneralSecurityException("ciphertext too short");
            } else {
               AlgorithmParameterSpec params = AesGcmJceUtil.getParams(ciphertext, prefixAndHeaderSize, 12);
               Cipher cipher = AesGcmJceUtil.getThreadLocalCipher();
               cipher.init(2, key, params);
               int offset = prefixAndHeaderSize + 12;
               int len = ciphertext.length - prefixAndHeaderSize - 12;
               return cipher.doFinal(ciphertext, offset, len);
            }
         }
      }
   }

   private static final class AesSivDem implements EciesDemHelper.Dem {
      private final AesSivParameters parameters;
      private final int keySizeInBytes;

      public AesSivDem(AesSivParameters parameters) {
         this.parameters = parameters;
         this.keySizeInBytes = parameters.getKeySizeBytes();
      }

      @Override
      public int getSymmetricKeySizeInBytes() {
         return this.keySizeInBytes;
      }

      @AccessesPartialKey
      private DeterministicAead getDaead(byte[] symmetricKeyValue) throws GeneralSecurityException {
         return AesSiv.create(
            AesSivKey.builder().setParameters(this.parameters).setKeyBytes(SecretBytes.copyFrom(symmetricKeyValue, InsecureSecretKeyAccess.get())).build()
         );
      }

      @Override
      public byte[] encrypt(byte[] demKeyValue, byte[] prefix, byte[] header, byte[] plaintext) throws GeneralSecurityException {
         byte[] ciphertext = this.getDaead(demKeyValue).encryptDeterministically(plaintext, EciesDemHelper.EMPTY_AAD);
         return Bytes.concat(prefix, header, ciphertext);
      }

      @Override
      public byte[] decrypt(byte[] demKeyValue, byte[] ciphertext, int prefixAndHeaderSize) throws GeneralSecurityException {
         if (ciphertext.length < prefixAndHeaderSize) {
            throw new GeneralSecurityException("ciphertext too short");
         } else {
            byte[] demCiphertext = Arrays.copyOfRange(ciphertext, prefixAndHeaderSize, ciphertext.length);
            return this.getDaead(demKeyValue).decryptDeterministically(demCiphertext, EciesDemHelper.EMPTY_AAD);
         }
      }
   }

   public interface Dem {
      int getSymmetricKeySizeInBytes();

      byte[] encrypt(byte[] demKeyValue, byte[] prefix, byte[] header, byte[] plaintext) throws GeneralSecurityException;

      byte[] decrypt(byte[] demKeyValue, byte[] ciphertext, int prefixAndHeaderSize) throws GeneralSecurityException;
   }
}
