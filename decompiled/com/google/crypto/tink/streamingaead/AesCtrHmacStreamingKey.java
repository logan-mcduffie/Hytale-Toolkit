package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;

public final class AesCtrHmacStreamingKey extends StreamingAeadKey {
   private final AesCtrHmacStreamingParameters parameters;
   private final SecretBytes initialKeymaterial;

   private AesCtrHmacStreamingKey(AesCtrHmacStreamingParameters parameters, SecretBytes initialKeymaterial) {
      this.parameters = parameters;
      this.initialKeymaterial = initialKeymaterial;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesCtrHmacStreamingKey create(AesCtrHmacStreamingParameters parameters, SecretBytes initialKeymaterial) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() != initialKeymaterial.size()) {
         throw new GeneralSecurityException("Key size mismatch");
      } else {
         return new AesCtrHmacStreamingKey(parameters, initialKeymaterial);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getInitialKeyMaterial() {
      return this.initialKeymaterial;
   }

   public AesCtrHmacStreamingParameters getParameters() {
      return this.parameters;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesCtrHmacStreamingKey)) {
         return false;
      } else {
         AesCtrHmacStreamingKey that = (AesCtrHmacStreamingKey)o;
         return that.parameters.equals(this.parameters) && that.initialKeymaterial.equalsSecretBytes(this.initialKeymaterial);
      }
   }
}
