package com.google.crypto.tink.mac;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class AesCmacParameters extends MacParameters {
   private final int keySizeBytes;
   private final int tagSizeBytes;
   private final AesCmacParameters.Variant variant;

   private AesCmacParameters(int keySizeBytes, int tagSizeBytes, AesCmacParameters.Variant variant) {
      this.keySizeBytes = keySizeBytes;
      this.tagSizeBytes = tagSizeBytes;
      this.variant = variant;
   }

   public static AesCmacParameters.Builder builder() {
      return new AesCmacParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public int getCryptographicTagSizeBytes() {
      return this.tagSizeBytes;
   }

   public int getTotalTagSizeBytes() {
      if (this.variant == AesCmacParameters.Variant.NO_PREFIX) {
         return this.getCryptographicTagSizeBytes();
      } else if (this.variant == AesCmacParameters.Variant.TINK) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else if (this.variant == AesCmacParameters.Variant.CRUNCHY) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else if (this.variant == AesCmacParameters.Variant.LEGACY) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else {
         throw new IllegalStateException("Unknown variant");
      }
   }

   public AesCmacParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof AesCmacParameters)) {
         return false;
      } else {
         AesCmacParameters that = (AesCmacParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes()
            && that.getTotalTagSizeBytes() == this.getTotalTagSizeBytes()
            && that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(AesCmacParameters.class, this.keySizeBytes, this.tagSizeBytes, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != AesCmacParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "AES-CMAC Parameters (variant: " + this.variant + ", " + this.tagSizeBytes + "-byte tags, and " + this.keySizeBytes + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      @Nullable
      private Integer tagSizeBytes = null;
      private AesCmacParameters.Variant variant = AesCmacParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesCmacParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         if (keySizeBytes != 16 && keySizeBytes != 32) {
            throw new InvalidAlgorithmParameterException(
               String.format("Invalid key size %d; only 128-bit and 256-bit AES keys are supported", keySizeBytes * 8)
            );
         } else {
            this.keySizeBytes = keySizeBytes;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public AesCmacParameters.Builder setTagSizeBytes(int tagSizeBytes) throws GeneralSecurityException {
         if (tagSizeBytes >= 10 && 16 >= tagSizeBytes) {
            this.tagSizeBytes = tagSizeBytes;
            return this;
         } else {
            throw new GeneralSecurityException("Invalid tag size for AesCmacParameters: " + tagSizeBytes);
         }
      }

      @CanIgnoreReturnValue
      public AesCmacParameters.Builder setVariant(AesCmacParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      public AesCmacParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("key size not set");
         } else if (this.tagSizeBytes == null) {
            throw new GeneralSecurityException("tag size not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("variant not set");
         } else {
            return new AesCmacParameters(this.keySizeBytes, this.tagSizeBytes, this.variant);
         }
      }
   }

   @Immutable
   public static final class Variant {
      public static final AesCmacParameters.Variant TINK = new AesCmacParameters.Variant("TINK");
      public static final AesCmacParameters.Variant CRUNCHY = new AesCmacParameters.Variant("CRUNCHY");
      public static final AesCmacParameters.Variant LEGACY = new AesCmacParameters.Variant("LEGACY");
      public static final AesCmacParameters.Variant NO_PREFIX = new AesCmacParameters.Variant("NO_PREFIX");
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
