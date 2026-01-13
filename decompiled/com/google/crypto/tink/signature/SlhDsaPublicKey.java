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

public class SlhDsaPublicKey extends SignaturePublicKey {
   private static final int SLH_DSA_SHA2_128S_PUBLIC_KEY_BYTES = 32;
   private final SlhDsaParameters parameters;
   private final Bytes serializedPublicKey;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private SlhDsaPublicKey(SlhDsaParameters parameters, Bytes serializedPublicKey, Bytes outputPrefix, @Nullable Integer idRequirement) {
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
   public static SlhDsaPublicKey.Builder builder() {
      return new SlhDsaPublicKey.Builder();
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

   public SlhDsaParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof SlhDsaPublicKey)) {
         return false;
      } else {
         SlhDsaPublicKey that = (SlhDsaPublicKey)o;
         return that.parameters.equals(this.parameters)
            && that.serializedPublicKey.equals(this.serializedPublicKey)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }

   public static class Builder {
      @Nullable
      private SlhDsaParameters parameters = null;
      @Nullable
      private Bytes serializedPublicKey = null;
      @Nullable
      private Integer idRequirement = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public SlhDsaPublicKey.Builder setParameters(SlhDsaParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public SlhDsaPublicKey.Builder setSerializedPublicKey(Bytes serializedPublicKey) {
         this.serializedPublicKey = serializedPublicKey;
         return this;
      }

      @CanIgnoreReturnValue
      public SlhDsaPublicKey.Builder setIdRequirement(@Nullable Integer idRequirement) {
         this.idRequirement = idRequirement;
         return this;
      }

      private Bytes getOutputPrefix() {
         if (this.parameters.getVariant() == SlhDsaParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
         } else if (this.parameters.getVariant() == SlhDsaParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
         } else {
            throw new IllegalStateException("Unknown SlhDsaParameters.Variant: " + this.parameters.getVariant());
         }
      }

      public SlhDsaPublicKey build() throws GeneralSecurityException {
         if (this.parameters == null) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (this.parameters.getVariant() == SlhDsaParameters.Variant.NO_PREFIX && this.idRequirement != null) {
            throw new GeneralSecurityException("IdRequirement must be null for variant NO_PREFIX");
         } else if (this.parameters.getVariant() == SlhDsaParameters.Variant.TINK && this.idRequirement == null) {
            throw new GeneralSecurityException("Id requirement missing for parameters' variant TINK");
         } else if (this.serializedPublicKey == null) {
            throw new GeneralSecurityException("Cannot build without public key bytes");
         } else if (this.parameters.getHashType() != SlhDsaParameters.HashType.SHA2) {
            throw new GeneralSecurityException("Unknown SLH-DSA hash type option " + this.parameters.getHashType() + "; only SHA2 is currently supported");
         } else if (this.parameters.getPrivateKeySize() != 64) {
            throw new GeneralSecurityException(
               "Unknown SLH-DSA private key size "
                  + this.parameters.getPrivateKeySize()
                  + "; only security level 128 (private key size 64) is currently supported"
            );
         } else if (this.parameters.getSignatureType() != SlhDsaParameters.SignatureType.SMALL_SIGNATURE) {
            throw new GeneralSecurityException(
               "Unknown SLH-DSA signature type " + this.parameters.getSignatureType() + "; only \"S\" (SMALL_SIGNATURE) is currently supported"
            );
         } else if (this.serializedPublicKey.size() != 32) {
            throw new GeneralSecurityException("Incorrect public key size for SLH-DSA-SHA2-128S: should be 32, but was " + this.serializedPublicKey.size());
         } else {
            Bytes outputPrefix = this.getOutputPrefix();
            return new SlhDsaPublicKey(this.parameters, this.serializedPublicKey, outputPrefix, this.idRequirement);
         }
      }
   }
}
