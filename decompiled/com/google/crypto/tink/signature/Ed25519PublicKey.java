package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class Ed25519PublicKey extends SignaturePublicKey {
   private final Ed25519Parameters parameters;
   private final Bytes publicKeyBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private Ed25519PublicKey(Ed25519Parameters parameters, Bytes publicKeyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.publicKeyBytes = publicKeyBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   private static Bytes createOutputPrefix(Ed25519Parameters parameters, @Nullable Integer idRequirement) {
      if (parameters.getVariant() == Ed25519Parameters.Variant.NO_PREFIX) {
         return OutputPrefixUtil.EMPTY_PREFIX;
      } else if (parameters.getVariant() == Ed25519Parameters.Variant.CRUNCHY || parameters.getVariant() == Ed25519Parameters.Variant.LEGACY) {
         return OutputPrefixUtil.getLegacyOutputPrefix(idRequirement);
      } else if (parameters.getVariant() == Ed25519Parameters.Variant.TINK) {
         return OutputPrefixUtil.getTinkOutputPrefix(idRequirement);
      } else {
         throw new IllegalStateException("Unknown Variant: " + parameters.getVariant());
      }
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public static Ed25519PublicKey create(Bytes publicKeyBytes) throws GeneralSecurityException {
      return create(Ed25519Parameters.Variant.NO_PREFIX, publicKeyBytes, null);
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static Ed25519PublicKey create(Ed25519Parameters.Variant variant, Bytes publicKeyBytes, @Nullable Integer idRequirement) throws GeneralSecurityException {
      Ed25519Parameters parameters = Ed25519Parameters.create(variant);
      if (!variant.equals(Ed25519Parameters.Variant.NO_PREFIX) && idRequirement == null) {
         throw new GeneralSecurityException("For given Variant " + variant + " the value of idRequirement must be non-null");
      } else if (variant.equals(Ed25519Parameters.Variant.NO_PREFIX) && idRequirement != null) {
         throw new GeneralSecurityException("For given Variant NO_PREFIX the value of idRequirement must be null");
      } else if (publicKeyBytes.size() != 32) {
         throw new GeneralSecurityException("Ed25519 key must be constructed with key of length 32 bytes, not " + publicKeyBytes.size());
      } else {
         return new Ed25519PublicKey(parameters, publicKeyBytes, createOutputPrefix(parameters, idRequirement), idRequirement);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public Bytes getPublicKeyBytes() {
      return this.publicKeyBytes;
   }

   public Ed25519Parameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof Ed25519PublicKey)) {
         return false;
      } else {
         Ed25519PublicKey that = (Ed25519PublicKey)o;
         return that.parameters.equals(this.parameters)
            && that.publicKeyBytes.equals(this.publicKeyBytes)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }
}
