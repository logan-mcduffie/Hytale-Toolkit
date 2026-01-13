package com.google.crypto.tink.signature;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RsaSsaPssParameters extends SignatureParameters {
   public static final BigInteger F4 = BigInteger.valueOf(65537L);
   private final int modulusSizeBits;
   private final BigInteger publicExponent;
   private final RsaSsaPssParameters.Variant variant;
   private final RsaSsaPssParameters.HashType sigHashType;
   private final RsaSsaPssParameters.HashType mgf1HashType;
   private final int saltLengthBytes;

   private RsaSsaPssParameters(
      int modulusSizeBits,
      BigInteger publicExponent,
      RsaSsaPssParameters.Variant variant,
      RsaSsaPssParameters.HashType sigHashType,
      RsaSsaPssParameters.HashType mgf1HashType,
      int saltLengthBytes
   ) {
      this.modulusSizeBits = modulusSizeBits;
      this.publicExponent = publicExponent;
      this.variant = variant;
      this.sigHashType = sigHashType;
      this.mgf1HashType = mgf1HashType;
      this.saltLengthBytes = saltLengthBytes;
   }

   public static RsaSsaPssParameters.Builder builder() {
      return new RsaSsaPssParameters.Builder();
   }

   public int getModulusSizeBits() {
      return this.modulusSizeBits;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   public RsaSsaPssParameters.Variant getVariant() {
      return this.variant;
   }

   public RsaSsaPssParameters.HashType getSigHashType() {
      return this.sigHashType;
   }

   public RsaSsaPssParameters.HashType getMgf1HashType() {
      return this.mgf1HashType;
   }

   public int getSaltLengthBytes() {
      return this.saltLengthBytes;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof RsaSsaPssParameters)) {
         return false;
      } else {
         RsaSsaPssParameters that = (RsaSsaPssParameters)o;
         return that.getModulusSizeBits() == this.getModulusSizeBits()
            && Objects.equals(that.getPublicExponent(), this.getPublicExponent())
            && Objects.equals(that.getVariant(), this.getVariant())
            && Objects.equals(that.getSigHashType(), this.getSigHashType())
            && Objects.equals(that.getMgf1HashType(), this.getMgf1HashType())
            && that.getSaltLengthBytes() == this.getSaltLengthBytes();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         RsaSsaPssParameters.class, this.modulusSizeBits, this.publicExponent, this.variant, this.sigHashType, this.mgf1HashType, this.saltLengthBytes
      );
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != RsaSsaPssParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "RSA SSA PSS Parameters (variant: "
         + this.variant
         + ", signature hashType: "
         + this.sigHashType
         + ", mgf1 hashType: "
         + this.mgf1HashType
         + ", saltLengthBytes: "
         + this.saltLengthBytes
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
      private BigInteger publicExponent = RsaSsaPssParameters.F4;
      @Nullable
      private RsaSsaPssParameters.HashType sigHashType = null;
      @Nullable
      private RsaSsaPssParameters.HashType mgf1HashType = null;
      @Nullable
      private Integer saltLengthBytes = null;
      private RsaSsaPssParameters.Variant variant = RsaSsaPssParameters.Variant.NO_PREFIX;
      private static final BigInteger TWO = BigInteger.valueOf(2L);
      private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);
      private static final int MIN_RSA_MODULUS_SIZE = 2048;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setModulusSizeBits(int modulusSizeBits) {
         this.modulusSizeBits = modulusSizeBits;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setPublicExponent(BigInteger e) {
         this.publicExponent = e;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setVariant(RsaSsaPssParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setSigHashType(RsaSsaPssParameters.HashType sigHashType) {
         this.sigHashType = sigHashType;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setMgf1HashType(RsaSsaPssParameters.HashType mgf1HashType) {
         this.mgf1HashType = mgf1HashType;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssParameters.Builder setSaltLengthBytes(int saltLengthBytes) throws GeneralSecurityException {
         if (saltLengthBytes < 0) {
            throw new GeneralSecurityException(String.format("Invalid salt length in bytes %d; salt length must be positive", saltLengthBytes));
         } else {
            this.saltLengthBytes = saltLengthBytes;
            return this;
         }
      }

      private void validatePublicExponent(BigInteger publicExponent) throws InvalidAlgorithmParameterException {
         int c = publicExponent.compareTo(RsaSsaPssParameters.F4);
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

      public RsaSsaPssParameters build() throws GeneralSecurityException {
         if (this.modulusSizeBits == null) {
            throw new GeneralSecurityException("key size is not set");
         } else if (this.publicExponent == null) {
            throw new GeneralSecurityException("publicExponent is not set");
         } else if (this.sigHashType == null) {
            throw new GeneralSecurityException("signature hash type is not set");
         } else if (this.mgf1HashType == null) {
            throw new GeneralSecurityException("mgf1 hash type is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("variant is not set");
         } else if (this.saltLengthBytes == null) {
            throw new GeneralSecurityException("salt length is not set");
         } else if (this.modulusSizeBits < 2048) {
            throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least %d bits", this.modulusSizeBits, 2048));
         } else if (this.sigHashType != this.mgf1HashType) {
            throw new GeneralSecurityException("MGF1 hash is different from signature hash");
         } else {
            this.validatePublicExponent(this.publicExponent);
            return new RsaSsaPssParameters(this.modulusSizeBits, this.publicExponent, this.variant, this.sigHashType, this.mgf1HashType, this.saltLengthBytes);
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final RsaSsaPssParameters.HashType SHA256 = new RsaSsaPssParameters.HashType("SHA256");
      public static final RsaSsaPssParameters.HashType SHA384 = new RsaSsaPssParameters.HashType("SHA384");
      public static final RsaSsaPssParameters.HashType SHA512 = new RsaSsaPssParameters.HashType("SHA512");
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
      public static final RsaSsaPssParameters.Variant TINK = new RsaSsaPssParameters.Variant("TINK");
      public static final RsaSsaPssParameters.Variant CRUNCHY = new RsaSsaPssParameters.Variant("CRUNCHY");
      public static final RsaSsaPssParameters.Variant LEGACY = new RsaSsaPssParameters.Variant("LEGACY");
      public static final RsaSsaPssParameters.Variant NO_PREFIX = new RsaSsaPssParameters.Variant("NO_PREFIX");
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
