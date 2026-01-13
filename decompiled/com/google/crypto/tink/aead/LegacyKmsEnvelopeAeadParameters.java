package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class LegacyKmsEnvelopeAeadParameters extends AeadParameters {
   private final LegacyKmsEnvelopeAeadParameters.Variant variant;
   private final String kekUri;
   private final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy dekParsingStrategy;
   private final AeadParameters dekParametersForNewKeys;

   private LegacyKmsEnvelopeAeadParameters(
      LegacyKmsEnvelopeAeadParameters.Variant variant,
      String kekUri,
      LegacyKmsEnvelopeAeadParameters.DekParsingStrategy dekParsingStrategy,
      AeadParameters dekParametersForNewKeys
   ) {
      this.variant = variant;
      this.kekUri = kekUri;
      this.dekParsingStrategy = dekParsingStrategy;
      this.dekParametersForNewKeys = dekParametersForNewKeys;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static LegacyKmsEnvelopeAeadParameters.Builder builder() {
      return new LegacyKmsEnvelopeAeadParameters.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public String getKekUri() {
      return this.kekUri;
   }

   public LegacyKmsEnvelopeAeadParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != LegacyKmsEnvelopeAeadParameters.Variant.NO_PREFIX;
   }

   public LegacyKmsEnvelopeAeadParameters.DekParsingStrategy getDekParsingStrategy() {
      return this.dekParsingStrategy;
   }

   public AeadParameters getDekParametersForNewKeys() {
      return this.dekParametersForNewKeys;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof LegacyKmsEnvelopeAeadParameters)) {
         return false;
      } else {
         LegacyKmsEnvelopeAeadParameters that = (LegacyKmsEnvelopeAeadParameters)o;
         return that.dekParsingStrategy.equals(this.dekParsingStrategy)
            && that.dekParametersForNewKeys.equals(this.dekParametersForNewKeys)
            && that.kekUri.equals(this.kekUri)
            && that.variant.equals(this.variant);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(LegacyKmsEnvelopeAeadParameters.class, this.kekUri, this.dekParsingStrategy, this.dekParametersForNewKeys, this.variant);
   }

   @Override
   public String toString() {
      return "LegacyKmsEnvelopeAead Parameters (kekUri: "
         + this.kekUri
         + ", dekParsingStrategy: "
         + this.dekParsingStrategy
         + ", dekParametersForNewKeys: "
         + this.dekParametersForNewKeys
         + ", variant: "
         + this.variant
         + ")";
   }

   public static class Builder {
      @Nullable
      private LegacyKmsEnvelopeAeadParameters.Variant variant;
      @Nullable
      private String kekUri;
      @Nullable
      private LegacyKmsEnvelopeAeadParameters.DekParsingStrategy dekParsingStrategy;
      @Nullable
      private AeadParameters dekParametersForNewKeys;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public LegacyKmsEnvelopeAeadParameters.Builder setVariant(LegacyKmsEnvelopeAeadParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      @CanIgnoreReturnValue
      public LegacyKmsEnvelopeAeadParameters.Builder setKekUri(String kekUri) {
         this.kekUri = kekUri;
         return this;
      }

      @CanIgnoreReturnValue
      public LegacyKmsEnvelopeAeadParameters.Builder setDekParsingStrategy(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy dekParsingStrategy) {
         this.dekParsingStrategy = dekParsingStrategy;
         return this;
      }

      @CanIgnoreReturnValue
      public LegacyKmsEnvelopeAeadParameters.Builder setDekParametersForNewKeys(AeadParameters aeadParameters) {
         this.dekParametersForNewKeys = aeadParameters;
         return this;
      }

      private static boolean parsingStrategyAllowed(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy parsingStrategy, AeadParameters aeadParameters) {
         if (parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_GCM) && aeadParameters instanceof AesGcmParameters) {
            return true;
         } else if (parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_CHACHA20POLY1305)
            && aeadParameters instanceof ChaCha20Poly1305Parameters) {
            return true;
         } else if (parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_XCHACHA20POLY1305)
            && aeadParameters instanceof XChaCha20Poly1305Parameters) {
            return true;
         } else if (parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_CTR_HMAC)
            && aeadParameters instanceof AesCtrHmacAeadParameters) {
            return true;
         } else {
            return parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_EAX) && aeadParameters instanceof AesEaxParameters
               ? true
               : parsingStrategy.equals(LegacyKmsEnvelopeAeadParameters.DekParsingStrategy.ASSUME_AES_GCM_SIV) && aeadParameters instanceof AesGcmSivParameters;
         }
      }

      public LegacyKmsEnvelopeAeadParameters build() throws GeneralSecurityException {
         if (this.variant == null) {
            this.variant = LegacyKmsEnvelopeAeadParameters.Variant.NO_PREFIX;
         }

         if (this.kekUri == null) {
            throw new GeneralSecurityException("kekUri must be set");
         } else if (this.dekParsingStrategy == null) {
            throw new GeneralSecurityException("dekParsingStrategy must be set");
         } else if (this.dekParametersForNewKeys == null) {
            throw new GeneralSecurityException("dekParametersForNewKeys must be set");
         } else if (this.dekParametersForNewKeys.hasIdRequirement()) {
            throw new GeneralSecurityException("dekParametersForNewKeys must not have ID Requirements");
         } else if (!parsingStrategyAllowed(this.dekParsingStrategy, this.dekParametersForNewKeys)) {
            throw new GeneralSecurityException(
               "Cannot use parsing strategy "
                  + this.dekParsingStrategy.toString()
                  + " when new keys are picked according to "
                  + this.dekParametersForNewKeys
                  + "."
            );
         } else {
            return new LegacyKmsEnvelopeAeadParameters(this.variant, this.kekUri, this.dekParsingStrategy, this.dekParametersForNewKeys);
         }
      }
   }

   @Immutable
   public static final class DekParsingStrategy {
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_AES_GCM = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_AES_GCM"
      );
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_XCHACHA20POLY1305 = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_XCHACHA20POLY1305"
      );
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_CHACHA20POLY1305 = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_CHACHA20POLY1305"
      );
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_AES_CTR_HMAC = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_AES_CTR_HMAC"
      );
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_AES_EAX = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_AES_EAX"
      );
      public static final LegacyKmsEnvelopeAeadParameters.DekParsingStrategy ASSUME_AES_GCM_SIV = new LegacyKmsEnvelopeAeadParameters.DekParsingStrategy(
         "ASSUME_AES_GCM_SIV"
      );
      private final String name;

      private DekParsingStrategy(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final LegacyKmsEnvelopeAeadParameters.Variant TINK = new LegacyKmsEnvelopeAeadParameters.Variant("TINK");
      public static final LegacyKmsEnvelopeAeadParameters.Variant NO_PREFIX = new LegacyKmsEnvelopeAeadParameters.Variant("NO_PREFIX");
      private final String name;

      private Variant(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
