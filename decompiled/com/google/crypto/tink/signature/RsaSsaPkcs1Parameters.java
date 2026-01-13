package com.google.crypto.tink.signature;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RsaSsaPkcs1Parameters extends SignatureParameters {
   public static final BigInteger F4 = BigInteger.valueOf(65537L);
   private final int modulusSizeBits;
   private final BigInteger publicExponent;
   private final RsaSsaPkcs1Parameters.Variant variant;
   private final RsaSsaPkcs1Parameters.HashType hashType;

   private RsaSsaPkcs1Parameters(int modulusSizeBits, BigInteger publicExponent, RsaSsaPkcs1Parameters.Variant variant, RsaSsaPkcs1Parameters.HashType hashType) {
      this.modulusSizeBits = modulusSizeBits;
      this.publicExponent = publicExponent;
      this.variant = variant;
      this.hashType = hashType;
   }

   public static RsaSsaPkcs1Parameters.Builder builder() {
      return new RsaSsaPkcs1Parameters.Builder();
   }

   public int getModulusSizeBits() {
      return this.modulusSizeBits;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   public RsaSsaPkcs1Parameters.Variant getVariant() {
      return this.variant;
   }

   public RsaSsaPkcs1Parameters.HashType getHashType() {
      return this.hashType;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof RsaSsaPkcs1Parameters)) {
         return false;
      } else {
         RsaSsaPkcs1Parameters that = (RsaSsaPkcs1Parameters)o;
         return that.getModulusSizeBits() == this.getModulusSizeBits()
            && Objects.equals(that.getPublicExponent(), this.getPublicExponent())
            && that.getVariant() == this.getVariant()
            && that.getHashType() == this.getHashType();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(RsaSsaPkcs1Parameters.class, this.modulusSizeBits, this.publicExponent, this.variant, this.hashType);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != RsaSsaPkcs1Parameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "RSA SSA PKCS1 Parameters (variant: "
         + this.variant
         + ", hashType: "
         + this.hashType
         + ", publicExponent: "
         + this.publicExponent
         + ", and "
         + this.modulusSizeBits
         + "-bit modulus)";
   }

   public static final class Builder {
      @Nullable
      private Integer modulusSizeBits = null;
      @Nullable
      private BigInteger publicExponent = RsaSsaPkcs1Parameters.F4;
      @Nullable
      private RsaSsaPkcs1Parameters.HashType hashType = null;
      private RsaSsaPkcs1Parameters.Variant variant = RsaSsaPkcs1Parameters.Variant.NO_PREFIX;
      private static final BigInteger TWO = BigInteger.valueOf(2L);
      private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);

      private Builder() {
      }

      @CanIgnoreReturnValue
      public RsaSsaPkcs1Parameters.Builder setModulusSizeBits(int modulusSizeBits) {
         this.modulusSizeBits = modulusSizeBits;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPkcs1Parameters.Builder setPublicExponent(BigInteger e) {
         this.publicExponent = e;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPkcs1Parameters.Builder setVariant(RsaSsaPkcs1Parameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPkcs1Parameters.Builder setHashType(RsaSsaPkcs1Parameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      private void validatePublicExponent(BigInteger publicExponent) throws InvalidAlgorithmParameterException {
         int c = publicExponent.compareTo(RsaSsaPkcs1Parameters.F4);
         if (c != 0) {
            if (c < 0) {
               throw new InvalidAlgorithmParameterException("Public exponent must be at least 65537.");
            } else if (publicExponent.mod(TWO).equals(BigInteger.ZERO)) {
               throw new InvalidAlgorithmParameterException("Invalid public exponent");
            } else if (publicExponent.compareTo(PUBLIC_EXPONENT_UPPER_BOUND) > 0) {
               throw new InvalidAlgorithmParameterException("Public exponent cannot be larger than 2^256.");
            }
         }
      }

      public RsaSsaPkcs1Parameters build() throws GeneralSecurityException {
         if (this.modulusSizeBits == null) {
            throw new GeneralSecurityException("key size is not set");
         } else if (this.publicExponent == null) {
            throw new GeneralSecurityException("publicExponent is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("hash type is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("variant is not set");
         } else if (this.modulusSizeBits < 2048) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least 2048 bits", this.modulusSizeBits));
         } else {
            this.validatePublicExponent(this.publicExponent);
            return new RsaSsaPkcs1Parameters(this.modulusSizeBits, this.publicExponent, this.variant, this.hashType);
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final RsaSsaPkcs1Parameters.HashType SHA256 = new RsaSsaPkcs1Parameters.HashType("SHA256");
      public static final RsaSsaPkcs1Parameters.HashType SHA384 = new RsaSsaPkcs1Parameters.HashType("SHA384");
      public static final RsaSsaPkcs1Parameters.HashType SHA512 = new RsaSsaPkcs1Parameters.HashType("SHA512");
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
      public static final RsaSsaPkcs1Parameters.Variant TINK = new RsaSsaPkcs1Parameters.Variant("TINK");
      public static final RsaSsaPkcs1Parameters.Variant CRUNCHY = new RsaSsaPkcs1Parameters.Variant("CRUNCHY");
      public static final RsaSsaPkcs1Parameters.Variant LEGACY = new RsaSsaPkcs1Parameters.Variant("LEGACY");
      public static final RsaSsaPkcs1Parameters.Variant NO_PREFIX = new RsaSsaPkcs1Parameters.Variant("NO_PREFIX");
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
