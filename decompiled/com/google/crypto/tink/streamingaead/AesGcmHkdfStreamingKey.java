package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;

public final class AesGcmHkdfStreamingKey extends StreamingAeadKey {
   private final AesGcmHkdfStreamingParameters parameters;
   private final SecretBytes initialKeymaterial;

   private AesGcmHkdfStreamingKey(AesGcmHkdfStreamingParameters parameters, SecretBytes initialKeymaterial) {
      this.parameters = parameters;
      this.initialKeymaterial = initialKeymaterial;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static AesGcmHkdfStreamingKey create(AesGcmHkdfStreamingParameters parameters, SecretBytes initialKeymaterial) throws GeneralSecurityException {
      if (parameters.getKeySizeBytes() != initialKeymaterial.size()) {
         throw new GeneralSecurityException("Key size mismatch");
      } else {
         return new AesGcmHkdfStreamingKey(parameters, initialKeymaterial);
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

   public AesGcmHkdfStreamingParameters getParameters() {
      return this.parameters;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof AesGcmHkdfStreamingKey)) {
         return false;
      } else {
         AesGcmHkdfStreamingKey that = (AesGcmHkdfStreamingKey)o;
         return that.parameters.equals(this.parameters) && that.initialKeymaterial.equalsSecretBytes(this.initialKeymaterial);
      }
   }
}
