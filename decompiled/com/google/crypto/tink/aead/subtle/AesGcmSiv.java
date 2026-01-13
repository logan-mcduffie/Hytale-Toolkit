package com.google.crypto.tink.aead.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.AesGcmSivKey;
import com.google.crypto.tink.aead.AesGcmSivParameters;
import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.util.SecretBytes;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;

@Alpha
public final class AesGcmSiv implements Aead {
   private static final ThreadLocal<Cipher> localAesGcmSivCipher = new ThreadLocal<Cipher>() {
      @Nullable
      protected Cipher initialValue() {
         try {
            Cipher cipher = EngineFactory.CIPHER.getInstance("AES/GCM-SIV/NoPadding");
            return !com.google.crypto.tink.aead.internal.AesGcmSiv.isAesGcmSivCipher(cipher) ? null : cipher;
         } catch (GeneralSecurityException var2) {
            throw new IllegalStateException(var2);
         }
      }
   };
   private final Aead aead;

   private static Cipher cipherSupplier() throws GeneralSecurityException {
      try {
         Cipher cipher = localAesGcmSivCipher.get();
         if (cipher == null) {
            throw new GeneralSecurityException("AES GCM SIV cipher is invalid.");
         } else {
            return cipher;
         }
      } catch (IllegalStateException var1) {
         throw new GeneralSecurityException("AES GCM SIV cipher is not available or is invalid.", var1);
      }
   }

   @AccessesPartialKey
   public static Aead create(AesGcmSivKey key) throws GeneralSecurityException {
      return com.google.crypto.tink.aead.internal.AesGcmSiv.create(key, AesGcmSiv::cipherSupplier);
   }

   @AccessesPartialKey
   private static Aead createFromRawKey(final byte[] key) throws GeneralSecurityException {
      return com.google.crypto.tink.aead.internal.AesGcmSiv.create(
         AesGcmSivKey.builder()
            .setKeyBytes(SecretBytes.copyFrom(key, InsecureSecretKeyAccess.get()))
            .setParameters(AesGcmSivParameters.builder().setKeySizeBytes(key.length).setVariant(AesGcmSivParameters.Variant.NO_PREFIX).build())
            .build(),
         AesGcmSiv::cipherSupplier
      );
   }

   private AesGcmSiv(Aead aead) {
      this.aead = aead;
   }

   public AesGcmSiv(final byte[] key) throws GeneralSecurityException {
      this(createFromRawKey(key));
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      return this.aead.encrypt(plaintext, associatedData);
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      return this.aead.decrypt(ciphertext, associatedData);
   }
}
