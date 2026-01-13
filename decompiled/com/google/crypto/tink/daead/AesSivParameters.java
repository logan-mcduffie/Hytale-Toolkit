package com.google.crypto.tink.daead;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class AesSivParameters extends DeterministicAeadParameters {
   private final int keySizeBytes;
   private final AesSivParameters.Variant variant;

   private AesSivParameters(int keySizeBytes, AesSivParameters.Variant variant) {
      this.keySizeBytes = keySizeBytes;
      this.variant = variant;
   }

   public static AesSivParameters.Builder builder() {
      return new AesSivParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public AesSivParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof AesSivParameters)) {
         return false;
      } else {
         AesSivParameters that = (AesSivParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(AesSivParameters.class, this.keySizeBytes, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != AesSivParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "AesSiv Parameters (variant: " + this.variant + ", " + this.keySizeBytes + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      private AesSivParameters.Variant variant = AesSivParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesSivParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         if (keySizeBytes != 32 && keySizeBytes != 48 && keySizeBytes != 64) {
            throw new InvalidAlgorithmParameterException(
               String.format("Invalid key size %d; only 32-byte, 48-byte and 64-byte AES-SIV keys are supported", keySizeBytes)
            );
         } else {
            this.keySizeBytes = keySizeBytes;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public AesSivParameters.Builder setVariant(AesSivParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      public AesSivParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("Key size is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("Variant is not set");
         } else {
            return new AesSivParameters(this.keySizeBytes, this.variant);
         }
      }
   }

   @Immutable
   public static final class Variant {
      public static final AesSivParameters.Variant TINK = new AesSivParameters.Variant("TINK");
      public static final AesSivParameters.Variant CRUNCHY = new AesSivParameters.Variant("CRUNCHY");
      public static final AesSivParameters.Variant NO_PREFIX = new AesSivParameters.Variant("NO_PREFIX");
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
