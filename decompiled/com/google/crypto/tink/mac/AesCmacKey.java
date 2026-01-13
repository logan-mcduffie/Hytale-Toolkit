package com.google.crypto.tink.mac;

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
public final class AesCmacKey extends MacKey {
   private final AesCmacParameters parameters;
   private final SecretBytes aesKeyBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private AesCmacKey(AesCmacParameters parameters, SecretBytes aesKeyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.aesKeyBytes = aesKeyBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesCmacKey.Builder builder() {
      return new AesCmacKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getAesKey() {
      return this.aesKeyBytes;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public AesCmacParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesCmacKey)) {
         return false;
      } else {
         AesCmacKey that = (AesCmacKey)o;
         return that.parameters.equals(this.parameters)
            && that.aesKeyBytes.equalsSecretBytes(this.aesKeyBytes)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private AesCmacParameters parameters = null;
      @Nullable
      private SecretBytes aesKeyBytes = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesCmacKey.Builder setParameters(AesCmacParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public AesCmacKey.Builder setAesKeyBytes(SecretBytes aesKeyBytes) throws GeneralSecurityException {
         this.aesKeyBytes = aesKeyBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public AesCmacKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == AesCmacParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == AesCmacParameters.Variant.LEGACY || this.parameters.getVariant() == AesCmacParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
         } else if (this.parameters.getVariant() == AesCmacParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown AesCmacParametersParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public AesCmacKey build() throws GeneralSecurityException {
         if (this.parameters != null && this.aesKeyBytes != null) {
            if (this.parameters.getKeySizeBytes() != this.aesKeyBytes.size()) {
               throw new GeneralSecurityException("Key size mismatch");
            } else if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               Bytes outputPrefix = this.getOutputPrefix();
               return new AesCmacKey(this.parameters, this.aesKeyBytes, outputPrefix, this.idRequirement);
            }
         } else {
            throw new GeneralSecurityException("Cannot build without parameters and/or key material");
         }
      }
   }
}
