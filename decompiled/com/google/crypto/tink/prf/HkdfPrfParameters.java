package com.google.crypto.tink.prf;

import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class HkdfPrfParameters extends PrfParameters {
   private static final int MIN_KEY_SIZE = 16;
   private final int keySizeBytes;
   private final HkdfPrfParameters.HashType hashType;
   @Nullable
   private final Bytes salt;

   private HkdfPrfParameters(int keySizeBytes, HkdfPrfParameters.HashType hashType, Bytes salt) {
      this.keySizeBytes = keySizeBytes;
      this.hashType = hashType;
      this.salt = salt;
   }

   public static HkdfPrfParameters.Builder builder() {
      return new HkdfPrfParameters.Builder();
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public HkdfPrfParameters.HashType getHashType() {
      return this.hashType;
   }

   @Nullable
   public Bytes getSalt() {
      return this.salt;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof HkdfPrfParameters)) {
         return false;
      } else {
         HkdfPrfParameters that = (HkdfPrfParameters)o;
         return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getHashType() == this.getHashType() && Objects.equals(that.getSalt(), this.getSalt());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(HkdfPrfParameters.class, this.keySizeBytes, this.hashType, this.salt);
   }

   @Override
   public boolean hasIdRequirement() {
      return false;
   }

   @Override
   public String toString() {
      return "HKDF PRF Parameters (hashType: " + this.hashType + ", salt: " + this.salt + ", and " + this.keySizeBytes + "-byte key)";
   }

   public static final class Builder {
      @Nullable
      private Integer keySizeBytes = null;
      @Nullable
      private HkdfPrfParameters.HashType hashType = null;
      @Nullable
      private Bytes salt = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HkdfPrfParameters.Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
         if (keySizeBytes < 16) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 128-bit or larger are supported", keySizeBytes * 8));
         } else {
            this.keySizeBytes = keySizeBytes;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public HkdfPrfParameters.Builder setHashType(HkdfPrfParameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      @CanIgnoreReturnValue
      public HkdfPrfParameters.Builder setSalt(Bytes salt) {
         if (salt.size() == 0) {
            this.salt = null;
            return this;
         } else {
            this.salt = salt;
            return this;
         }
      }

      public HkdfPrfParameters build() throws GeneralSecurityException {
         if (this.keySizeBytes == null) {
            throw new GeneralSecurityException("key size is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("hash type is not set");
         } else {
            return new HkdfPrfParameters(this.keySizeBytes, this.hashType, this.salt);
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final HkdfPrfParameters.HashType SHA1 = new HkdfPrfParameters.HashType("SHA1");
      public static final HkdfPrfParameters.HashType SHA224 = new HkdfPrfParameters.HashType("SHA224");
      public static final HkdfPrfParameters.HashType SHA256 = new HkdfPrfParameters.HashType("SHA256");
      public static final HkdfPrfParameters.HashType SHA384 = new HkdfPrfParameters.HashType("SHA384");
      public static final HkdfPrfParameters.HashType SHA512 = new HkdfPrfParameters.HashType("SHA512");
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
