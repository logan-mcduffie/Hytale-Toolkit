package com.google.crypto.tink.aead;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class AesGcmSivParameters extends AeadParameters {
   private final int keySizeBytes;
   private final AesGcmSivParameters.Variant variant;

   private AesGcmSivParameters(int keySizeBytes, AesGcmSivParameters.Variant variant) {
      this.keySizeBytes = keySizeBytes;
      this.variant = variant;
   }

   public static AesGcmSivParameters.Builder builder() {
      return new AesGcmSivParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public AesGcmSivParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof AesGcmSivParameters)) {
         return false;
      } else {
         AesGcmSivParameters that = (AesGcmSivParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(AesGcmSivParameters.class, this.keySizeBytes, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != AesGcmSivParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "AesGcmSiv Parameters (variant: " + this.variant + ", " + this.keySizeBytes + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      private AesGcmSivParameters.Variant variant = AesGcmSivParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesGcmSivParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         if (keySizeBytes != 16 && keySizeBytes != 32) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 16-byte and 32-byte AES keys are supported", keySizeBytes));
         } else {
            this.keySizeBytes = keySizeBytes;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public AesGcmSivParameters.Builder setVariant(AesGcmSivParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      public AesGcmSivParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("Key size is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("Variant is not set");
         } else {
            return new AesGcmSivParameters(this.keySizeBytes, this.variant);
         }
      }
   }

   @Immutable
   public static final class Variant {
      public static final AesGcmSivParameters.Variant TINK = new AesGcmSivParameters.Variant("TINK");
      public static final AesGcmSivParameters.Variant CRUNCHY = new AesGcmSivParameters.Variant("CRUNCHY");
      public static final AesGcmSivParameters.Variant NO_PREFIX = new AesGcmSivParameters.Variant("NO_PREFIX");
      private final String name;

      private Variant(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
