package com.google.crypto.tink.mac;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class HmacParameters extends MacParameters {
   private final int keySizeBytes;
   private final int tagSizeBytes;
   private final HmacParameters.Variant variant;
   private final HmacParameters.HashType hashType;

   private HmacParameters(int keySizeBytes, int tagSizeBytes, HmacParameters.Variant variant, HmacParameters.HashType hashType) {
      this.keySizeBytes = keySizeBytes;
      this.tagSizeBytes = tagSizeBytes;
      this.variant = variant;
      this.hashType = hashType;
   }

   public static HmacParameters.Builder builder() {
      return new HmacParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public int getCryptographicTagSizeBytes() {
      return this.tagSizeBytes;
   }

   public int getTotalTagSizeBytes() {
      if (this.variant == HmacParameters.Variant.NO_PREFIX) {
         return this.getCryptographicTagSizeBytes();
      } else if (this.variant == HmacParameters.Variant.TINK) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else if (this.variant == HmacParameters.Variant.CRUNCHY) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else if (this.variant == HmacParameters.Variant.LEGACY) {
         return this.getCryptographicTagSizeBytes() + 5;
      } else {
         throw new IllegalStateException("Unknown variant");
      }
   }

   public HmacParameters.Variant getVariant() {
      return this.variant;
   }

   public HmacParameters.HashType getHashType() {
      return this.hashType;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof HmacParameters)) {
         return false;
      } else {
         HmacParameters that = (HmacParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes()
            && that.getTotalTagSizeBytes() == this.getTotalTagSizeBytes()
            && that.getVariant() == this.getVariant()
            && that.getHashType() == this.getHashType();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(HmacParameters.class, this.keySizeBytes, this.tagSizeBytes, this.variant, this.hashType);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != HmacParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "HMAC Parameters (variant: "
         + this.variant
         + ", hashType: "
         + this.hashType
         + ", "
         + this.tagSizeBytes
         + "-byte tags, and "
         + this.keySizeBytes
         + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      @Nullable
      private Integer tagSizeBytes = null;
      private HmacParameters.HashType hashType = null;
      private HmacParameters.Variant variant = HmacParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HmacParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         this.keySizeBytes = keySizeBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public HmacParameters.Builder setTagSizeBytes(int tagSizeBytes) throws GeneralSecurityException {
         this.tagSizeBytes = tagSizeBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public HmacParameters.Builder setVariant(HmacParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      @CanIgnoreReturnValue
      public HmacParameters.Builder setHashType(HmacParameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      private static void validateTagSizeBytes(int tagSizeBytes, HmacParameters.HashType hashType) throws GeneralSecurityException {
         if (tagSizeBytes < 10) {
            throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; must be at least 10 bytes", tagSizeBytes));
         } else if (hashType == HmacParameters.HashType.SHA1) {
            if (tagSizeBytes > 20) {
               throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 20 bytes for SHA1", tagSizeBytes));
            }
         } else if (hashType == HmacParameters.HashType.SHA224) {
            if (tagSizeBytes > 28) {
               throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 28 bytes for SHA224", tagSizeBytes));
            }
         } else if (hashType == HmacParameters.HashType.SHA256) {
            if (tagSizeBytes > 32) {
               throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 32 bytes for SHA256", tagSizeBytes));
            }
         } else if (hashType == HmacParameters.HashType.SHA384) {
            if (tagSizeBytes > 48) {
               throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 48 bytes for SHA384", tagSizeBytes));
            }
         } else if (hashType == HmacParameters.HashType.SHA512) {
            if (tagSizeBytes > 64) {
               throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 64 bytes for SHA512", tagSizeBytes));
            }
         } else {
            throw new GeneralSecurityException("unknown hash type; must be SHA256, SHA384 or SHA512");
         }
      }

      public HmacParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("key size is not set");
         } else if (this.tagSizeBytes == null) {
            throw new GeneralSecurityException("tag size is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("hash type is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("variant is not set");
         } else if (this.keySizeBytes < 16) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least 16 bytes", this.keySizeBytes));
         } else {
            validateTagSizeBytes(this.tagSizeBytes, this.hashType);
            return new HmacParameters(this.keySizeBytes, this.tagSizeBytes, this.variant, this.hashType);
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final HmacParameters.HashType SHA1 = new HmacParameters.HashType("SHA1");
      public static final HmacParameters.HashType SHA224 = new HmacParameters.HashType("SHA224");
      public static final HmacParameters.HashType SHA256 = new HmacParameters.HashType("SHA256");
      public static final HmacParameters.HashType SHA384 = new HmacParameters.HashType("SHA384");
      public static final HmacParameters.HashType SHA512 = new HmacParameters.HashType("SHA512");
      private final String name;

      private HashType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final HmacParameters.Variant TINK = new HmacParameters.Variant("TINK");
      public static final HmacParameters.Variant CRUNCHY = new HmacParameters.Variant("CRUNCHY");
      public static final HmacParameters.Variant LEGACY = new HmacParameters.Variant("LEGACY");
      public static final HmacParameters.Variant NO_PREFIX = new HmacParameters.Variant("NO_PREFIX");
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
