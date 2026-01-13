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
public final class HmacPrfKey extends PrfKey {
   private final HmacPrfParameters parameters;
   private final SecretBytes keyBytes;

   private HmacPrfKey(HmacPrfParameters parameters, SecretBytes keyBytes) {
      this.parameters = parameters;
      this.keyBytes = keyBytes;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static HmacPrfKey.Builder builder() {
      return new HmacPrfKey.Builder();
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

   public HmacPrfParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return null;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof HmacPrfKey)) {
         return false;
      } else {
         HmacPrfKey that = (HmacPrfKey)o;
         return that.parameters.equals(this.parameters) && that.keyBytes.equalsSecretBytes(this.keyBytes);
      }
   }

   public static final class Builder {
      @Nullable
      private HmacPrfParameters parameters = null;
      @Nullable
      private SecretBytes keyBytes = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HmacPrfKey.Builder setParameters(HmacPrfParameters parameters) {
         this.parameters = parameters;
         return this;
      }

      @CanIgnoreReturnValue
      public HmacPrfKey.Builder setKeyBytes(SecretBytes keyBytes) {
         this.keyBytes = keyBytes;
         return this;
      }

      public HmacPrfKey build() throws GeneralSecurityException {
         if (this.parameters != null && this.keyBytes != null) {
            if (this.parameters.getKeySizeBytes() != this.keyBytes.size()) {
               throw new GeneralSecurityException("Key size mismatch");
            } else {
               return new HmacPrfKey(this.parameters, this.keyBytes);
            }
         } else {
            throw new GeneralSecurityException("Cannot build without parameters and/or key material");
         }
      }
   }
}
