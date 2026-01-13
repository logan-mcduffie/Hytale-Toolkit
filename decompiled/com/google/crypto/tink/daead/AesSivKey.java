package com.google.crypto.tink.daead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class AesSivKey extends DeterministicAeadKey {
   private final AesSivParameters parameters;
   private final SecretBytes keyBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private AesSivKey(AesSivParameters parameters, SecretBytes keyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.keyBytes = keyBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesSivKey.Builder builder() {
      return new AesSivKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getKeyBytes() {
      return this.keyBytes;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public AesSivParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesSivKey)) {
         return false;
      } else {
         AesSivKey that = (AesSivKey)o;
         return that.parameters.equals(this.parameters)
            && that.keyBytes.equalsSecretBytes(this.keyBytes)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private AesSivParameters parameters = null;
      @Nullable
      private SecretBytes keyBytes = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesSivKey.Builder setParameters(AesSivParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public AesSivKey.Builder setKeyBytes(SecretBytes keyBytes) {
         this.keyBytes = keyBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public AesSivKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      public AesSivKey build() throws GeneralSecurityException {
         if (this.parameters != null && this.keyBytes != null) {
            if (this.parameters.getKeySizeBytes() != this.keyBytes.size()) {
               throw new GeneralSecurityException("Key size mismatch");
            } else if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               Bytes outputPrefix = this.getOutputPrefix();
               return new AesSivKey(this.parameters, this.keyBytes, outputPrefix, this.idRequirement);
            }
         } else {
            throw new IllegalArgumentException("Cannot build without parameters and/or key material");
         }
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == AesSivParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == AesSivParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
         } else if (this.parameters.getVariant() == AesSivParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown AesSivParameters.Variant: " + this.parameters.getVariant());
         }
      }
   }
}
