package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class EcdsaPublicKey extends SignaturePublicKey {
   private final EcdsaParameters parameters;
   private final ECPoint publicPoint;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private EcdsaPublicKey(EcdsaParameters parameters, ECPoint publicPoint, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.publicPoint = publicPoint;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static EcdsaPublicKey.Builder builder() {
      return new EcdsaPublicKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public ECPoint getPublicPoint() {
      return this.publicPoint;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public EcdsaParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof EcdsaPublicKey)) {
         return false;
      } else {
         EcdsaPublicKey that = (EcdsaPublicKey)o;
         return that.parameters.equals(this.parameters) && that.publicPoint.equals(this.publicPoint) && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private EcdsaParameters parameters = null;
      @Nullable
      private ECPoint publicPoint = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public EcdsaPublicKey.Builder setParameters(EcdsaParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaPublicKey.Builder setPublicPoint(ECPoint publicPoint) {
         this.publicPoint = publicPoint;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaPublicKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == EcdsaParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == EcdsaParameters.Variant.LEGACY || this.parameters.getVariant() == EcdsaParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
         } else if (this.parameters.getVariant() == EcdsaParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown EcdsaParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public EcdsaPublicKey build() throws GeneralSecurityException {
         if (this.parameters == null) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (this.publicPoint == null) {
            throw new GeneralSecurityException("Cannot build without public point");
         } else {
            EllipticCurvesUtil.checkPointOnCurve(this.publicPoint, this.parameters.getCurveType().toParameterSpec().getCurve());
            if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               Bytes outputPrefix = this.getOutputPrefix();
               return new EcdsaPublicKey(this.parameters, this.publicPoint, outputPrefix, this.idRequirement);
            }
         }
      }
   }
}
