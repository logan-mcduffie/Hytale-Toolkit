package com.google.crypto.tink.hybrid;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;

public final class HpkeParameters extends HybridParameters {
   private final HpkeParameters.KemId kem;
   private final HpkeParameters.KdfId kdf;
   private final HpkeParameters.AeadId aead;
   private final HpkeParameters.Variant variant;

   private HpkeParameters(HpkeParameters.KemId kem, HpkeParameters.KdfId kdf, HpkeParameters.AeadId aead, HpkeParameters.Variant variant) {
      this.kem = kem;
      this.kdf = kdf;
      this.aead = aead;
      this.variant = variant;
   }

   public static HpkeParameters.Builder builder() {
      return new HpkeParameters.Builder();
   }

   public HpkeParameters.KemId getKemId() {
      return this.kem;
   }

   public HpkeParameters.KdfId getKdfId() {
      return this.kdf;
   }

   public HpkeParameters.AeadId getAeadId() {
      return this.aead;
   }

   public HpkeParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != HpkeParameters.Variant.NO_PREFIX;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof HpkeParameters)) {
         return false;
      } else {
         HpkeParameters other = (HpkeParameters)o;
         return this.kem == other.kem && this.kdf == other.kdf && this.aead == other.aead && this.variant == other.variant;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(HpkeParameters.class, this.kem, this.kdf, this.aead, this.variant);
   }

   @Override
   public String toString() {
      return "HPKE Parameters (Variant: " + this.variant + ", KemId: " + this.kem + ", KdfId: " + this.kdf + ", AeadId: " + this.aead + ")";
   }

   @Immutable
   public static final class AeadId extends HpkeParameters.AlgorithmIdentifier {
      public static final HpkeParameters.AeadId AES_128_GCM = new HpkeParameters.AeadId("AES_128_GCM", 1);
      public static final HpkeParameters.AeadId AES_256_GCM = new HpkeParameters.AeadId("AES_256_GCM", 2);
      public static final HpkeParameters.AeadId CHACHA20_POLY1305 = new HpkeParameters.AeadId("CHACHA20_POLY1305", 3);

      private AeadId(String name, int value) {
         super(name, value);
      }
   }

   @Immutable
   private static class AlgorithmIdentifier {
      protected final String name;
      protected final int value;

      private AlgorithmIdentifier(String name, int value) {
         this.name = name;
         this.value = value;
      }

      public int getValue() {
         return this.value;
      }

      @Override
      public String toString() {
         return String.format("%s(0x%04x)", this.name, this.value);
      }
   }

   public static final class Builder {
      private HpkeParameters.KemId kem = null;
      private HpkeParameters.KdfId kdf = null;
      private HpkeParameters.AeadId aead = null;
      private HpkeParameters.Variant variant = HpkeParameters.Variant.NO_PREFIX;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public HpkeParameters.Builder setKemId(HpkeParameters.KemId kem) {
         this.kem = kem;
         return this;
      }

      @CanIgnoreReturnValue
      public HpkeParameters.Builder setKdfId(HpkeParameters.KdfId kdf) {
         this.kdf = kdf;
         return this;
      }

      @CanIgnoreReturnValue
      public HpkeParameters.Builder setAeadId(HpkeParameters.AeadId aead) {
         this.aead = aead;
         return this;
      }

      @CanIgnoreReturnValue
      public HpkeParameters.Builder setVariant(HpkeParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      public HpkeParameters build() throws GeneralSecurityException {
         if (this.kem == null) {
            throw new GeneralSecurityException("HPKE KEM parameter is not set");
         } else if (this.kdf == null) {
            throw new GeneralSecurityException("HPKE KDF parameter is not set");
         } else if (this.aead == null) {
            throw new GeneralSecurityException("HPKE AEAD parameter is not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("HPKE variant is not set");
         } else {
            return new HpkeParameters(this.kem, this.kdf, this.aead, this.variant);
         }
      }
   }

   @Immutable
   public static final class KdfId extends HpkeParameters.AlgorithmIdentifier {
      public static final HpkeParameters.KdfId HKDF_SHA256 = new HpkeParameters.KdfId("HKDF_SHA256", 1);
      public static final HpkeParameters.KdfId HKDF_SHA384 = new HpkeParameters.KdfId("HKDF_SHA384", 2);
      public static final HpkeParameters.KdfId HKDF_SHA512 = new HpkeParameters.KdfId("HKDF_SHA512", 3);

      private KdfId(String name, int value) {
         super(name, value);
      }
   }

   @Immutable
   public static final class KemId extends HpkeParameters.AlgorithmIdentifier {
      public static final HpkeParameters.KemId DHKEM_P256_HKDF_SHA256 = new HpkeParameters.KemId("DHKEM_P256_HKDF_SHA256", 16);
      public static final HpkeParameters.KemId DHKEM_P384_HKDF_SHA384 = new HpkeParameters.KemId("DHKEM_P384_HKDF_SHA384", 17);
      public static final HpkeParameters.KemId DHKEM_P521_HKDF_SHA512 = new HpkeParameters.KemId("DHKEM_P521_HKDF_SHA512", 18);
      public static final HpkeParameters.KemId DHKEM_X25519_HKDF_SHA256 = new HpkeParameters.KemId("DHKEM_X25519_HKDF_SHA256", 32);

      private KemId(String name, int value) {
         super(name, value);
      }
   }

   @Immutable
   public static final class Variant {
      public static final HpkeParameters.Variant TINK = new HpkeParameters.Variant("TINK");
      public static final HpkeParameters.Variant CRUNCHY = new HpkeParameters.Variant("CRUNCHY");
      public static final HpkeParameters.Variant NO_PREFIX = new HpkeParameters.Variant("NO_PREFIX");
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
