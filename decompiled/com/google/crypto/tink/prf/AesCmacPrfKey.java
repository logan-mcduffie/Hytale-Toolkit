package com.google.crypto.tink.prf;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class AesCmacPrfKey extends PrfKey {
   private final AesCmacPrfParameters parameters;
   private final SecretBytes keyBytes;

   private AesCmacPrfKey(AesCmacPrfParameters parameters, SecretBytes keyBytes) {
      this.parameters = parameters;
      this.keyBytes = keyBytes;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesCmacPrfKey create(AesCmacPrfParameters parameters, SecretBytes keyBytes) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() != keyBytes.size()) {
         throw new GeneralSecurityException("Key size mismatch");
      } else {
         return new AesCmacPrfKey(parameters, keyBytes);
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

   public AesCmacPrfParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return null;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesCmacPrfKey)) {
         return false;
      } else {
         AesCmacPrfKey that = (AesCmacPrfKey)o;
         return that.parameters.equals(this.parameters) && that.keyBytes.equalsSecretBytes(this.keyBytes);
      }
   }
}
