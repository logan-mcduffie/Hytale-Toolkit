package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RsaSsaPssPublicKey extends SignaturePublicKey {
   private final RsaSsaPssParameters parameters;
   private final BigInteger modulus;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private RsaSsaPssPublicKey(RsaSsaPssParameters parameters, BigInteger modulus, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.modulus = modulus;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static RsaSsaPssPublicKey.Builder builder() {
      return new RsaSsaPssPublicKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public BigInteger getModulus() {
      return this.modulus;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public RsaSsaPssParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof RsaSsaPssPublicKey)) {
         return false;
      } else {
         RsaSsaPssPublicKey that = (RsaSsaPssPublicKey)o;
         return that.parameters.equals(this.parameters) && that.modulus.equals(this.modulus) && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private RsaSsaPssParameters parameters = null;
      @Nullable
      private BigInteger modulus = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPublicKey.Builder setParameters(RsaSsaPssParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPublicKey.Builder setModulus(BigInteger modulus) {
         this.modulus = modulus;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPublicKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.LEGACY || this.parameters.getVariant() == RsaSsaPssParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
         } else if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown RsaSsaPssParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public RsaSsaPssPublicKey build() throws GeneralSecurityException {
         if (this.parameters == null) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (this.modulus == null) {
            throw new GeneralSecurityException("Cannot build without modulus");
         } else {
            int modulusSize = this.modulus.bitLength();
            int paramModulusSize = this.parameters.getModulusSizeBits();
            if (modulusSize != paramModulusSize) {
               throw new GeneralSecurityException("Got modulus size " + modulusSize + ", but parameters requires modulus size " + paramModulusSize);
            } else if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               Bytes outputPrefix = this.getOutputPrefix();
               return new RsaSsaPssPublicKey(this.parameters, this.modulus, outputPrefix, this.idRequirement);
            }
         }
      }
   }
}
