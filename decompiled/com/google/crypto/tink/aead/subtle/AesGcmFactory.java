package com.google.crypto.tink.aead.subtle;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.subtle.AesGcmJce;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

@Immutable
public final class AesGcmFactory implements AeadFactory {
   private final int keySizeInBytes;

   public AesGcmFactory(int keySizeInBytes) throws GeneralSecurityException {
      this.keySizeInBytes = validateAesKeySize(keySizeInBytes);
   }

   @Override
   public int getKeySizeInBytes() {
      return this.keySizeInBytes;
   }

   @Override
   public Aead createAead(final byte[] symmetricKey) throws GeneralSecurityException {
      if (symmetricKey.length != this.getKeySizeInBytes()) {
         throw new GeneralSecurityException(
            String.format("Symmetric key has incorrect length; expected %s, but got %s", this.getKeySizeInBytes(), symmetricKey.length)
         );
      } else {
         return new AesGcmJce(symmetricKey);
      }
   }

   @CanIgnoreReturnValue
   private static int validateAesKeySize(int sizeInBytes) throws InvalidAlgorithmParameterException {
      if (sizeInBytes != 16 && sizeInBytes != 32) {
         throw new InvalidAlgorithmParameterException(String.format("Invalid AES key size, expected 16 or 32, but got %d", sizeInBytes));
      } else {
         return sizeInBytes;
      }
   }
}
