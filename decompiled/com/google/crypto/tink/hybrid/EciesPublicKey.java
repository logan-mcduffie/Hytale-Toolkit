package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class EciesPublicKey extends HybridPublicKey {
   private final EciesParameters parameters;
   @Nullable
   private final ECPoint nistPublicPoint;
   @Nullable
   private final Bytes x25519PublicPointBytes;
   private final Bytes outputPrefix;
   @Nullable
   private final Integer idRequirement;

   private EciesPublicKey(
      EciesParameters parameters,
      @Nullable ECPoint nistPublicPoint,
      @Nullable Bytes x25519PublicPointBytes,
      Bytes outputPrefix,
      @Nullable Integer idRequirement
   ) {
      this.parameters = parameters;
      this.nistPublicPoint = nistPublicPoint;
      this.x25519PublicPointBytes = x25519PublicPointBytes;
      this.outputPrefix = outputPrefix;
      this.idRequirement = idRequirement;
   }

   private static void validateIdRequirement(EciesParameters.Variant variant, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (!variant.equals(EciesParameters.Variant.NO_PREFIX) && idRequirement == null) {
         throw new GeneralSecurityException("'idRequirement' must be non-null for " + variant + " variant.");
      } else if (variant.equals(EciesParameters.Variant.NO_PREFIX) && idRequirement != null) {
         throw new GeneralSecurityException("'idRequirement' must be null for NO_PREFIX variant.");
      }
   }

   private static EllipticCurve getParameterSpecNistCurve(EciesParameters.CurveType curveType) {
      if (curveType == EciesParameters.CurveType.NIST_P256) {
         return EllipticCurves.getNistP256Params().getCurve();
      } else if (curveType == EciesParameters.CurveType.NIST_P384) {
         return EllipticCurves.getNistP384Params().getCurve();
      } else if (curveType == EciesParameters.CurveType.NIST_P521) {
         return EllipticCurves.getNistP521Params().getCurve();
      } else {
         throw new IllegalArgumentException("Unable to determine NIST curve type for " + curveType);
      }
   }

   private static Bytes createOutputPrefix(EciesParameters.Variant variant, @Nullable Integer idRequirement) {
      if (variant == EciesParameters.Variant.NO_PREFIX) {
         return OutputPrefixUtil.EMPTY_PREFIX;
      } else if (idRequirement == null) {
         throw new IllegalStateException("idRequirement must be non-null for EciesParameters.Variant: " + variant);
      } else if (variant == EciesParameters.Variant.CRUNCHY) {
         return OutputPrefixUtil.getLegacyOutputPrefix(idRequirement);
      } else if (variant == EciesParameters.Variant.TINK) {
         return OutputPrefixUtil.getTinkOutputPrefix(idRequirement);
      } else {
         throw new IllegalStateException("Unknown EciesParameters.Variant: " + variant);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static EciesPublicKey createForCurveX25519(EciesParameters parameters, Bytes publicPointBytes, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (!parameters.getCurveType().equals(EciesParameters.CurveType.X25519)) {
         throw new GeneralSecurityException("createForCurveX25519 may only be called with parameters for curve X25519");
      } else {
         validateIdRequirement(parameters.getVariant(), idRequirement);
         if (publicPointBytes.size() != 32) {
            throw new GeneralSecurityException("Encoded public point byte length for X25519 curve must be 32");
         } else {
            Bytes prefix = createOutputPrefix(parameters.getVariant(), idRequirement);
            return new EciesPublicKey(parameters, null, publicPointBytes, prefix, idRequirement);
         }
      }
   }

   public static EciesPublicKey createForNistCurve(EciesParameters parameters, ECPoint publicPoint, @Nullable Integer idRequirement) throws GeneralSecurityException {
      if (parameters.getCurveType().equals(EciesParameters.CurveType.X25519)) {
         throw new GeneralSecurityException("createForNistCurve may only be called with parameters for NIST curve");
      } else {
         validateIdRequirement(parameters.getVariant(), idRequirement);
         EllipticCurvesUtil.checkPointOnCurve(publicPoint, getParameterSpecNistCurve(parameters.getCurveType()));
         Bytes prefix = createOutputPrefix(parameters.getVariant(), idRequirement);
         return new EciesPublicKey(parameters, publicPoint, null, prefix, idRequirement);
      }
   }

   @Nullable
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public ECPoint getNistCurvePoint() {
      return this.nistPublicPoint;
   }

   @Nullable
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public Bytes getX25519CurvePointBytes() {
      return this.x25519PublicPointBytes;
   }

   @Override
   public Bytes getOutputPrefix() {
      return this.outputPrefix;
   }

   public EciesParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof EciesPublicKey)) {
         return false;
      } else {
         EciesPublicKey other = (EciesPublicKey)o;
         return this.parameters.equals(other.parameters)
            && Objects.equals(this.x25519PublicPointBytes, other.x25519PublicPointBytes)
            && Objects.equals(this.nistPublicPoint, other.nistPublicPoint)
            && Objects.equals(this.idRequirement, other.idRequirement);
      }
   }
}
