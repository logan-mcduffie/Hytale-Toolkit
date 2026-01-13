package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.aead.XChaCha20Poly1305Parameters;
import com.google.crypto.tink.daead.AesSivParameters;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public final class EciesParameters extends HybridParameters {
   private static final Set<Parameters> acceptedDemParameters = TinkBugException.exceptionIsBug(() -> listAcceptedDemParameters());
   private final EciesParameters.CurveType curveType;
   private final EciesParameters.HashType hashType;
   @Nullable
   private final EciesParameters.PointFormat nistCurvePointFormat;
   private final EciesParameters.Variant variant;
   private final Parameters demParameters;
   @Nullable
   private final Bytes salt;

   private static Set<Parameters> listAcceptedDemParameters() throws GeneralSecurityException {
      HashSet<Parameters> acceptedDemParameters = new HashSet<>();
      acceptedDemParameters.add(
         AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(16).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
      );
      acceptedDemParameters.add(
         AesGcmParameters.builder().setIvSizeBytes(12).setKeySizeBytes(32).setTagSizeBytes(16).setVariant(AesGcmParameters.Variant.NO_PREFIX).build()
      );
      acceptedDemParameters.add(
         AesCtrHmacAeadParameters.builder()
            .setAesKeySizeBytes(16)
            .setHmacKeySizeBytes(32)
            .setTagSizeBytes(16)
            .setIvSizeBytes(16)
            .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
            .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
            .build()
      );
      acceptedDemParameters.add(
         AesCtrHmacAeadParameters.builder()
            .setAesKeySizeBytes(32)
            .setHmacKeySizeBytes(32)
            .setTagSizeBytes(32)
            .setIvSizeBytes(16)
            .setHashType(AesCtrHmacAeadParameters.HashType.SHA256)
            .setVariant(AesCtrHmacAeadParameters.Variant.NO_PREFIX)
            .build()
      );
      acceptedDemParameters.add(XChaCha20Poly1305Parameters.create());
      acceptedDemParameters.add(AesSivParameters.builder().setKeySizeBytes(64).setVariant(AesSivParameters.Variant.NO_PREFIX).build());
      return Collections.unmodifiableSet(acceptedDemParameters);
   }

   private EciesParameters(
      EciesParameters.CurveType curveType,
      EciesParameters.HashType hashType,
      @Nullable EciesParameters.PointFormat pointFormat,
      Parameters demParameters,
      EciesParameters.Variant variant,
      Bytes salt
   ) {
      this.curveType = curveType;
      this.hashType = hashType;
      this.nistCurvePointFormat = pointFormat;
      this.demParameters = demParameters;
      this.variant = variant;
      this.salt = salt;
   }

   public static EciesParameters.Builder builder() {
      return new EciesParameters.Builder();
   }

   public EciesParameters.CurveType getCurveType() {
      return this.curveType;
   }

   public EciesParameters.HashType getHashType() {
      return this.hashType;
   }

   @Nullable
   public EciesParameters.PointFormat getNistCurvePointFormat() {
      return this.nistCurvePointFormat;
   }

   public Parameters getDemParameters() {
      return this.demParameters;
   }

   public EciesParameters.Variant getVariant() {
      return this.variant;
   }

   @Nullable
   public Bytes getSalt() {
      return this.salt;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != EciesParameters.Variant.NO_PREFIX;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof EciesParameters)) {
         return false;
      } else {
         EciesParameters that = (EciesParameters)o;
         return Objects.equals(that.getCurveType(), this.getCurveType())
            && Objects.equals(that.getHashType(), this.getHashType())
            && Objects.equals(that.getNistCurvePointFormat(), this.getNistCurvePointFormat())
            && Objects.equals(that.getDemParameters(), this.getDemParameters())
            && Objects.equals(that.getVariant(), this.getVariant())
            && Objects.equals(that.getSalt(), this.getSalt());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(EciesParameters.class, this.curveType, this.hashType, this.nistCurvePointFormat, this.demParameters, this.variant, this.salt);
   }

   @Override
   public String toString() {
      return String.format(
         "EciesParameters(curveType=%s, hashType=%s, pointFormat=%s, demParameters=%s, variant=%s, salt=%s)",
         this.curveType,
         this.hashType,
         this.nistCurvePointFormat,
         this.demParameters,
         this.variant,
         this.salt
      );
   }

   public static final class Builder {
      private EciesParameters.CurveType curveType = null;
      private EciesParameters.HashType hashType = null;
      private EciesParameters.PointFormat nistCurvePointFormat = null;
      private Parameters demParameters = null;
      private EciesParameters.Variant variant = EciesParameters.Variant.NO_PREFIX;
      @Nullable
      private Bytes salt = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setCurveType(EciesParameters.CurveType curveType) {
         this.curveType = curveType;
         return this;
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setHashType(EciesParameters.HashType hashType) {
         this.hashType = hashType;
         return this;
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setNistCurvePointFormat(EciesParameters.PointFormat pointFormat) {
         this.nistCurvePointFormat = pointFormat;
         return this;
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setDemParameters(Parameters demParameters) throws GeneralSecurityException {
         if (!EciesParameters.acceptedDemParameters.contains(demParameters)) {
            throw new GeneralSecurityException(
               "Invalid DEM parameters "
                  + demParameters
                  + "; only AES128_GCM_RAW, AES256_GCM_RAW, AES128_CTR_HMAC_SHA256_RAW, AES256_CTR_HMAC_SHA256_RAW XCHACHA20_POLY1305_RAW and AES256_SIV_RAW are currently supported."
            );
         } else {
            this.demParameters = demParameters;
            return this;
         }
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setVariant(EciesParameters.Variant variant) {
         this.variant = variant;
         return this;
      }

      @CanIgnoreReturnValue
      public EciesParameters.Builder setSalt(Bytes salt) {
         if (salt.size() == 0) {
            this.salt = null;
            return this;
         } else {
            this.salt = salt;
            return this;
         }
      }

      public EciesParameters build() throws GeneralSecurityException {
         if (this.curveType == null) {
            throw new GeneralSecurityException("Elliptic curve type is not set");
         } else if (this.hashType == null) {
            throw new GeneralSecurityException("Hash type is not set");
         } else if (this.demParameters == null) {
            throw new GeneralSecurityException("DEM parameters are not set");
         } else if (this.variant == null) {
            throw new GeneralSecurityException("Variant is not set");
         } else if (this.curveType != EciesParameters.CurveType.X25519 && this.nistCurvePointFormat == null) {
            throw new GeneralSecurityException("Point format is not set");
         } else if (this.curveType == EciesParameters.CurveType.X25519 && this.nistCurvePointFormat != null) {
            throw new GeneralSecurityException("For Curve25519 point format must not be set");
         } else {
            return new EciesParameters(this.curveType, this.hashType, this.nistCurvePointFormat, this.demParameters, this.variant, this.salt);
         }
      }
   }

   @Immutable
   public static final class CurveType {
      public static final EciesParameters.CurveType NIST_P256 = new EciesParameters.CurveType("NIST_P256");
      public static final EciesParameters.CurveType NIST_P384 = new EciesParameters.CurveType("NIST_P384");
      public static final EciesParameters.CurveType NIST_P521 = new EciesParameters.CurveType("NIST_P521");
      public static final EciesParameters.CurveType X25519 = new EciesParameters.CurveType("X25519");
      private final String name;

      private CurveType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class HashType {
      public static final EciesParameters.HashType SHA1 = new EciesParameters.HashType("SHA1");
      public static final EciesParameters.HashType SHA224 = new EciesParameters.HashType("SHA224");
      public static final EciesParameters.HashType SHA256 = new EciesParameters.HashType("SHA256");
      public static final EciesParameters.HashType SHA384 = new EciesParameters.HashType("SHA384");
      public static final EciesParameters.HashType SHA512 = new EciesParameters.HashType("SHA512");
      private final String name;

      private HashType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class PointFormat {
      public static final EciesParameters.PointFormat COMPRESSED = new EciesParameters.PointFormat("COMPRESSED");
      public static final EciesParameters.PointFormat UNCOMPRESSED = new EciesParameters.PointFormat("UNCOMPRESSED");
      public static final EciesParameters.PointFormat LEGACY_UNCOMPRESSED = new EciesParameters.PointFormat("LEGACY_UNCOMPRESSED");
      private final String name;

      private PointFormat(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final EciesParameters.Variant TINK = new EciesParameters.Variant("TINK");
      public static final EciesParameters.Variant CRUNCHY = new EciesParameters.Variant("CRUNCHY");
      public static final EciesParameters.Variant NO_PREFIX = new EciesParameters.Variant("NO_PREFIX");
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
