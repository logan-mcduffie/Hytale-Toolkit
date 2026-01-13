package com.google.crypto.tink.aead;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;

public final class LegacyKmsAeadParameters extends AeadParameters {
   private final String keyUri;
   private final LegacyKmsAeadParameters.Variant variant;

   private LegacyKmsAeadParameters(String keyUri, LegacyKmsAeadParameters.Variant variant) {
      this.keyUri = keyUri;
      this.variant = variant;
   }

   public static LegacyKmsAeadParameters create(String keyUri) throws GeneralSecurityException {
      return new LegacyKmsAeadParameters(keyUri, LegacyKmsAeadParameters.Variant.NO_PREFIX);
   }

   public static LegacyKmsAeadParameters create(String keyUri, LegacyKmsAeadParameters.Variant variant) {
      return new LegacyKmsAeadParameters(keyUri, variant);
   }

   public String keyUri() {
      return this.keyUri;
   }

   public LegacyKmsAeadParameters.Variant variant() {
      return this.variant;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != LegacyKmsAeadParameters.Variant.NO_PREFIX;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof LegacyKmsAeadParameters)) {
         return false;
      } else {
         LegacyKmsAeadParameters that = (LegacyKmsAeadParameters)o;
         return that.keyUri.equals(this.keyUri) && that.variant.equals(this.variant);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(LegacyKmsAeadParameters.class, this.keyUri, this.variant);
   }

   @Override
   public String toString() {
      return "LegacyKmsAead Parameters (keyUri: " + this.keyUri + ", variant: " + this.variant + ")";
   }

   @Immutable
   public static final class Variant {
      public static final LegacyKmsAeadParameters.Variant TINK = new LegacyKmsAeadParameters.Variant("TINK");
      public static final LegacyKmsAeadParameters.Variant NO_PREFIX = new LegacyKmsAeadParameters.Variant("NO_PREFIX");
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
