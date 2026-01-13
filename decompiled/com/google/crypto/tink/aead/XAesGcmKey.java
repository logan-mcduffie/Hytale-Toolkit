package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class XAesGcmKey extends AeadKey {
   private final XAesGcmParameters parameters;
   private final SecretBytes keyBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private XAesGcmKey(XAesGcmParameters parameters, SecretBytes keyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
      this.parameters = parameters;
      this.keyBytes = keyBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   private static Bytes getOutputPrefix(XAesGcmParameters parameters, @Nullable Integer idRequirement) {
      if (parameters.getVariant() == XAesGcmParameters.Variant.NO_PREFIX) {
         return OutputPrefixUtil.EMPTY_PREFIX;
      } else if (parameters.getVariant() == XAesGcmParameters.Variant.TINK) {
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
   public static XAesGcmKey create(XAesGcmParameters parameters, SecretBytes secretBytes, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (parameters.getVariant() != XAesGcmParameters.Variant.NO_PREFIX && idRequirement == null) {
         throw new GeneralSecurityException("For given Variant " + parameters.getVariant() + " the value of idRequirement must be non-null");
      } else if (parameters.getVariant() == XAesGcmParameters.Variant.NO_PREFIX && idRequirement != null) {
         throw new GeneralSecurityException("For given Variant NO_PREFIX the value of idRequirement must be null");
      } else if (secretBytes.size() != 32) {
         throw new GeneralSecurityException("XAesGcmKey key must be constructed with key of length 32 bytes, not " + secretBytes.size());
      } else {
         return new XAesGcmKey(parameters, secretBytes, getOutputPrefix(parameters, idRequirement), idRequirement);
      }
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

   public XAesGcmParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof XAesGcmKey)) {
         return false;
      } else {
         XAesGcmKey that = (XAesGcmKey)o;
         return that.parameters.equals(this.parameters)
            && that.keyBytes.equalsSecretBytes(this.keyBytes)
            && Objects.equals(that.idRequirement, this.idRequirement);
      }
   }
}
