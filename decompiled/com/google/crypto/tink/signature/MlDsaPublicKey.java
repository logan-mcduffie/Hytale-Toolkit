package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public class MlDsaPublicKey extends SignaturePublicKey {
   private static final int MLDSA65_PUBLIC_KEY_BYTES = 1952;
   private final MlDsaParameters parameters;
   private final Bytes serializedPublicKey;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private MlDsaPublicKey(MlDsaParameters parameters, Bytes serializedPublicKey, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.serializedPublicKey = serializedPublicKey;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static MlDsaPublicKey.Builder builder() {
      return new MlDsaPublicKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public Bytes getSerializedPublicKey() {
      return this.serializedPublicKey;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public MlDsaParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof MlDsaPublicKey)) {
         return false;
      } else {
         MlDsaPublicKey that = (MlDsaPublicKey)o;
         return that.parameters.equals(this.parameters)
            && that.serializedPublicKey.equals(this.serializedPublicKey)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private MlDsaParameters parameters = null;
      @Nullable
      private Bytes serializedPublicKey = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public MlDsaPublicKey.Builder setParameters(MlDsaParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public MlDsaPublicKey.Builder setSerializedPublicKey(Bytes serializedPublicKey) {
         this.serializedPublicKey = serializedPublicKey;
         return this;
      }

      @CanIgnoreReturnValue
      public MlDsaPublicKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == MlDsaParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == MlDsaParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown MlDsaParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public MlDsaPublicKey build() throws GeneralSecurityException {
         if (this.parameters == null) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (this.parameters.getVariant() == MlDsaParameters.Variant.NO_PREFIX && this.idRequirement != null) {
            throw new GeneralSecurityException("Id requirement present for parameters' variant NO_PREFIX");
         } else if (this.parameters.getVariant() == MlDsaParameters.Variant.TINK && this.idRequirement == null) {
            throw new GeneralSecurityException("Id requirement missing for parameters' variant TINK");
         } else if (this.serializedPublicKey == null) {
            throw new GeneralSecurityException("Cannot build without public key bytes");
         } else if (this.parameters.getMlDsaInstance() != MlDsaParameters.MlDsaInstance.ML_DSA_65) {
            throw new GeneralSecurityException("Unknown ML-DSA instance; only ML-DSA-65 is currently supported");
         } else if (this.serializedPublicKey.size() != 1952) {
            throw new GeneralSecurityException("Incorrect public key size for ML-DSA-65");
         } else {
            Bytes outputPrefix = this.getOutputPrefix();
            return new MlDsaPublicKey(this.parameters, this.serializedPublicKey, outputPrefix, this.idRequirement);
         }
      }
   }
}
