package com.google.crypto.tink.signature;

import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.spec.ECParameterSpec;
import java.util.Objects;

public final class EcdsaParameters extends SignatureParameters {
   private final EcdsaParameters.SignatureEncoding signatureEncoding;
   private final EcdsaParameters.CurveType curveType;
   private final EcdsaParameters.HashType hashType;
   private final EcdsaParameters.Variant variant;

   private EcdsaParameters(
      EcdsaParameters.SignatureEncoding signatureEncoding,
      EcdsaParameters.CurveType curveType,
      EcdsaParameters.HashType hashType,
      EcdsaParameters.Variant variant
   ) {
      this.signatureEncoding = signatureEncoding;
      this.curveType = curveType;
      this.hashType = hashType;
      this.variant = variant;
   }

   public static EcdsaParameters.Builder builder() {
      return new EcdsaParameters.Builder();
   }

   public EcdsaParameters.SignatureEncoding getSignatureEncoding() {
      return this.signatureEncoding;
   }

   public EcdsaParameters.CurveType getCurveType() {
      return this.curveType;
   }

   public EcdsaParameters.HashType getHashType() {
      return this.hashType;
   }

   public EcdsaParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof EcdsaParameters)) {
         return false;
      } else {
         EcdsaParameters that = (EcdsaParameters)o;
         return that.getSignatureEncoding() == this.getSignatureEncoding()
            && that.getCurveType() == this.getCurveType()
            && that.getHashType() == this.getHashType()
            && that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(EcdsaParameters.class, this.signatureEncoding, this.curveType, this.hashType, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != EcdsaParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "ECDSA Parameters (variant: "
         + this.variant
         + ", hashType: "
         + this.hashType
         + ", encoding: "
         + this.signatureEncoding
         + ", curve: "
         + this.curveType
         + ")";
   }

   public static final class Builder {
      private EcdsaParameters.SignatureEncoding signatureEncoding = null;
      private EcdsaParameters.CurveType curveType = null;
      private EcdsaParameters.HashType hashType = null;
      private EcdsaParameters.Variant variant = EcdsaParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public EcdsaParameters.Builder setSignatureEncoding(EcdsaParameters.SignatureEncoding signatureEncoding) {
         this.signatureEncoding = signatureEncoding;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaParameters.Builder setCurveType(EcdsaParameters.CurveType curveType) {
         this.curveType = curveType;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaParameters.Builder setHashType(EcdsaParameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaParameters.Builder setVariant(EcdsaParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      public EcdsaParameters build() throws GeneralSecurityException {
         if (this.signatureEncoding == null) {
            throw new GeneralSecurityException("signature encoding is not set");
         } else if (this.curveType == null) {
            throw new GeneralSecurityException("EC curve type is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("hash type is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("variant is not set");
         } else if (this.curveType == EcdsaParameters.CurveType.NIST_P256 && this.hashType != EcdsaParameters.HashType.SHA256) {
            throw new GeneralSecurityException("NIST_P256 requires SHA256");
         } else if (this.curveType == EcdsaParameters.CurveType.NIST_P384
            && this.hashType != EcdsaParameters.HashType.SHA384
            && this.hashType != EcdsaParameters.HashType.SHA512) {
            throw new GeneralSecurityException("NIST_P384 requires SHA384 or SHA512");
         } else if (this.curveType == EcdsaParameters.CurveType.NIST_P521 && this.hashType != EcdsaParameters.HashType.SHA512) {
            throw new GeneralSecurityException("NIST_P521 requires SHA512");
         } else {
            return new EcdsaParameters(this.signatureEncoding, this.curveType, this.hashType, this.variant);
         }
      }
   }

   @Immutable
   public static final class CurveType {
      public static final EcdsaParameters.CurveType NIST_P256 = new EcdsaParameters.CurveType("NIST_P256", EllipticCurvesUtil.NIST_P256_PARAMS);
      public static final EcdsaParameters.CurveType NIST_P384 = new EcdsaParameters.CurveType("NIST_P384", EllipticCurvesUtil.NIST_P384_PARAMS);
      public static final EcdsaParameters.CurveType NIST_P521 = new EcdsaParameters.CurveType("NIST_P521", EllipticCurvesUtil.NIST_P521_PARAMS);
      private final String name;
      private final ECParameterSpec spec;

      private CurveType(String name, ECParameterSpec spec) {
         this.name = name;
         this.spec = spec;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public ECParameterSpec toParameterSpec() {
         return this.spec;
      }

      public static EcdsaParameters.CurveType fromParameterSpec(ECParameterSpec spec) throws GeneralSecurityException {
         if (EllipticCurvesUtil.isSameEcParameterSpec(spec, NIST_P256.toParameterSpec())) {
            return NIST_P256;
         } else if (EllipticCurvesUtil.isSameEcParameterSpec(spec, NIST_P384.toParameterSpec())) {
            return NIST_P384;
         } else if (EllipticCurvesUtil.isSameEcParameterSpec(spec, NIST_P521.toParameterSpec())) {
            return NIST_P521;
         } else {
            throw new GeneralSecurityException("unknown ECParameterSpec");
         }
      }
   }

   @Immutable
   public static final class HashType {
      public static final EcdsaParameters.HashType SHA256 = new EcdsaParameters.HashType("SHA256");
      public static final EcdsaParameters.HashType SHA384 = new EcdsaParameters.HashType("SHA384");
      public static final EcdsaParameters.HashType SHA512 = new EcdsaParameters.HashType("SHA512");
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
   public static final class SignatureEncoding {
      public static final EcdsaParameters.SignatureEncoding IEEE_P1363 = new EcdsaParameters.SignatureEncoding("IEEE_P1363");
      public static final EcdsaParameters.SignatureEncoding DER = new EcdsaParameters.SignatureEncoding("DER");
      private final String name;

      private SignatureEncoding(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final EcdsaParameters.Variant TINK = new EcdsaParameters.Variant("TINK");
      public static final EcdsaParameters.Variant CRUNCHY = new EcdsaParameters.Variant("CRUNCHY");
      public static final EcdsaParameters.Variant LEGACY = new EcdsaParameters.Variant("LEGACY");
      public static final EcdsaParameters.Variant NO_PREFIX = new EcdsaParameters.Variant("NO_PREFIX");
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
