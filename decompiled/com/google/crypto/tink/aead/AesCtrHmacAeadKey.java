package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class AesCtrHmacAeadKey extends AeadKey {
   private final AesCtrHmacAeadParameters parameters;
   private final SecretBytes aesKeyBytes;
   private final SecretBytes hmacKeyBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private AesCtrHmacAeadKey(
      AesCtrHmacAeadParameters parameters, SecretBytes aesKeyBytes, SecretBytes hmacKeyBytes, Bytes outputPrefix, @Nullable Integer idRequirement
   ) {
      this.parameters = parameters;
      this.aesKeyBytes = aesKeyBytes;
      this.hmacKeyBytes = hmacKeyBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesCtrHmacAeadKey.Builder builder() {
      return new AesCtrHmacAeadKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getAesKeyBytes() {
      return this.aesKeyBytes;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getHmacKeyBytes() {
      return this.hmacKeyBytes;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public AesCtrHmacAeadParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesCtrHmacAeadKey)) {
         return false;
      } else {
         AesCtrHmacAeadKey that = (AesCtrHmacAeadKey)o;
         return that.parameters.equals(this.parameters)
            && that.aesKeyBytes.equalsSecretBytes(this.aesKeyBytes)
            && that.hmacKeyBytes.equalsSecretBytes(this.hmacKeyBytes)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private AesCtrHmacAeadParameters parameters = null;
      @Nullable
      private SecretBytes aesKeyBytes = null;
      @Nullable
      private SecretBytes hmacKeyBytes = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public AesCtrHmacAeadKey.Builder setParameters(AesCtrHmacAeadParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public AesCtrHmacAeadKey.Builder setAesKeyBytes(SecretBytes aesKeyBytes) {
         this.aesKeyBytes = aesKeyBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public AesCtrHmacAeadKey.Builder setHmacKeyBytes(SecretBytes hmacKeyBytes) {
         this.hmacKeyBytes = hmacKeyBytes;
         return this;
      }

      @CanIgnoreReturnValue
      public AesCtrHmacAeadKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == AesCtrHmacAeadParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == AesCtrHmacAeadParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
         } else if (this.parameters.getVariant() == AesCtrHmacAeadParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown AesCtrHmacAeadParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public AesCtrHmacAeadKey build() throws GeneralSecurityException {
         if (this.parameters == null) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (this.aesKeyBytes != null && this.hmacKeyBytes != null) {
            if (this.parameters.getAesKeySizeBytes() != this.aesKeyBytes.size()) {
               throw new GeneralSecurityException("AES key size mismatch");
            } else if (this.parameters.getHmacKeySizeBytes() != this.hmacKeyBytes.size()) {
               throw new GeneralSecurityException("HMAC key size mismatch");
            } else if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               Bytes outputPrefix = this.getOutputPrefix();
               return new AesCtrHmacAeadKey(this.parameters, this.aesKeyBytes, this.hmacKeyBytes, outputPrefix, this.idRequirement);
            }
         } else {
            throw new GeneralSecurityException("Cannot build without key material");
         }
      }
   }
}
