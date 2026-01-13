package com.google.crypto.tink.prf;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class HkdfPrfKey extends PrfKey {
   private final HkdfPrfParameters parameters;
   private final SecretBytes keyBytes;

   private HkdfPrfKey(HkdfPrfParameters parameters, SecretBytes keyBytes) {
      this.parameters = parameters;
      this.keyBytes = keyBytes;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static HkdfPrfKey.Builder builder() {
      return new HkdfPrfKey.Builder();
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

   public HkdfPrfParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return null;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof HkdfPrfKey)) {
         return false;
      } else {
         HkdfPrfKey that = (HkdfPrfKey)o;
         return that.parameters.equals(this.parameters) && that.keyBytes.equalsSecretBytes(this.keyBytes);
      }
   }

   public static final class Builder {
      @Nullable
      private HkdfPrfParameters parameters = null;
      @Nullable
      private SecretBytes keyBytes = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HkdfPrfKey.Builder setParameters(HkdfPrfParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public HkdfPrfKey.Builder setKeyBytes(SecretBytes keyBytes) {
         this.keyBytes = keyBytes;
         return this;
      }

      public HkdfPrfKey build() throws GeneralSecurityException {
         if (this.parameters != null && this.keyBytes != null) {
            if (this.parameters.getKeySizeBytes() != this.keyBytes.size()) {
               throw new GeneralSecurityException("Key size mismatch");
            } else {
               return new HkdfPrfKey(this.parameters, this.keyBytes);
            }
         } else {
            throw new GeneralSecurityException("Cannot build without parameters and/or key material");
         }
      }
   }
}
