package com.google.crypto.tink.prf;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class HmacPrfParameters extends PrfParameters {
   private static final int MIN_KEY_SIZE = 16;
   private final int keySizeBytes;
   private final HmacPrfParameters.HashType hashType;

   private HmacPrfParameters(int keySizeBytes, HmacPrfParameters.HashType hashType) {
      this.keySizeBytes = keySizeBytes;
      this.hashType = hashType;
   }

   public static HmacPrfParameters.Builder builder() {
      return new HmacPrfParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public HmacPrfParameters.HashType getHashType() {
      return this.hashType;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof HmacPrfParameters)) {
         return false;
      } else {
         HmacPrfParameters that = (HmacPrfParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getHashType() == this.getHashType();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(HmacPrfParameters.class, this.keySizeBytes, this.hashType);
   }

   @Override
   public boolean hasIdRequirement() {
      return false;
   }

   @Override
   public String toString() {
      return "HMAC PRF Parameters (hashType: " + this.hashType + " and " + this.keySizeBytes + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      @Nullable
      private HmacPrfParameters.HashType hashType = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HmacPrfParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         if (keySizeBytes < 16) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 128-bit or larger are supported", keySizeBytes * 8));
         } else {
            this.keySizeBytes = keySizeBytes;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public HmacPrfParameters.Builder setHashType(HmacPrfParameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      public HmacPrfParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("key size is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("hash type is not set");
         } else {
            return new HmacPrfParameters(this.keySizeBytes, this.hashType);
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final HmacPrfParameters.HashType SHA1 = new HmacPrfParameters.HashType("SHA1");
      public static final HmacPrfParameters.HashType SHA224 = new HmacPrfParameters.HashType("SHA224");
      public static final HmacPrfParameters.HashType SHA256 = new HmacPrfParameters.HashType("SHA256");
      public static final HmacPrfParameters.HashType SHA384 = new HmacPrfParameters.HashType("SHA384");
      public static final HmacPrfParameters.HashType SHA512 = new HmacPrfParameters.HashType("SHA512");
      private final String name;

      private HashType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
