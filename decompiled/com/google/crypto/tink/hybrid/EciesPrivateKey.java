package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.X25519;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;
import javax.annotation.Nullable;

@Immutable
public final class EciesPrivateKey extends HybridPrivateKey {
   private final EciesPublicKey publicKey;
   @Nullable
   private final SecretBigInteger nistPrivateKeyValue;
   @Nullable
   private final SecretBytes x25519PrivateKeyBytes;

   private EciesPrivateKey(EciesPublicKey publicKey, @Nullable SecretBigInteger nistPrivateKeyValue, @Nullable SecretBytes x25519PrivateKeyBytes) {
      this.publicKey = publicKey;
      this.nistPrivateKeyValue = nistPrivateKeyValue;
      this.x25519PrivateKeyBytes = x25519PrivateKeyBytes;
   }

   private static ECParameterSpec toParameterSpecNistCurve(EciesParameters.CurveType curveType) {
      if (curveType == EciesParameters.CurveType.NIST_P256) {
         return EllipticCurves.getNistP256Params();
      } else if (curveType == EciesParameters.CurveType.NIST_P384) {
         return EllipticCurves.getNistP384Params();
      } else if (curveType == EciesParameters.CurveType.NIST_P521) {
         return EllipticCurves.getNistP521Params();
      } else {
         throw new IllegalArgumentException("Unable to determine NIST curve type for " + curveType);
      }
   }

   private static void validateNistPrivateKeyValue(BigInteger privateValue, ECPoint publicPoint, EciesParameters.CurveType curveType) throws GeneralSecurityException {
      BigInteger order = toParameterSpecNistCurve(curveType).getOrder();
      if (privateValue.signum() > 0 && privateValue.compareTo(order) < 0) {
         ECPoint p = EllipticCurvesUtil.multiplyByGenerator(privateValue, toParameterSpecNistCurve(curveType));
         if (!p.equals(publicPoint)) {
            throw new GeneralSecurityException("Invalid private value");
         }
      } else {
         throw new GeneralSecurityException("Invalid private value");
      }
   }

   private static void validateX25519PrivateKeyBytes(byte[] privateKeyBytes, byte[] publicKeyBytes) throws GeneralSecurityException {
      if (privateKeyBytes.length != 32) {
         throw new GeneralSecurityException("Private key bytes length for X25519 curve must be 32");
      } else {
         byte[] expectedPublicKeyBytes = X25519.publicFromPrivate(privateKeyBytes);
         if (!Arrays.equals(expectedPublicKeyBytes, publicKeyBytes)) {
            throw new GeneralSecurityException("Invalid private key for public key.");
         }
      }
   }

   @AccessesPartialKey
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static EciesPrivateKey createForCurveX25519(EciesPublicKey publicKey, SecretBytes x25519PrivateKeyBytes) throws GeneralSecurityException {
      if (publicKey == null) {
         throw new GeneralSecurityException("ECIES private key cannot be constructed without an ECIES public key");
      } else if (publicKey.getX25519CurvePointBytes() == null) {
         throw new GeneralSecurityException("ECIES private key for X25519 curve cannot be constructed with NIST-curve public key");
      } else if (x25519PrivateKeyBytes == null) {
         throw new GeneralSecurityException("ECIES private key cannot be constructed without secret");
      } else {
         validateX25519PrivateKeyBytes(x25519PrivateKeyBytes.toByteArray(InsecureSecretKeyAccess.get()), publicKey.getX25519CurvePointBytes().toByteArray());
         return new EciesPrivateKey(publicKey, null, x25519PrivateKeyBytes);
      }
   }

   @AccessesPartialKey
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static EciesPrivateKey createForNistCurve(EciesPublicKey publicKey, SecretBigInteger nistPrivateKeyValue) throws GeneralSecurityException {
      if (publicKey == null) {
         throw new GeneralSecurityException("ECIES private key cannot be constructed without an ECIES public key");
      } else if (publicKey.getNistCurvePoint() == null) {
         throw new GeneralSecurityException("ECIES private key for NIST curve cannot be constructed with X25519-curve public key");
      } else if (nistPrivateKeyValue == null) {
         throw new GeneralSecurityException("ECIES private key cannot be constructed without secret");
      } else {
         validateNistPrivateKeyValue(
            nistPrivateKeyValue.getBigInteger(InsecureSecretKeyAccess.get()), publicKey.getNistCurvePoint(), publicKey.getParameters().getCurveType()
         );
         return new EciesPrivateKey(publicKey, nistPrivateKeyValue, null);
      }
   }

   @Nullable
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getX25519PrivateKeyBytes() {
      return this.x25519PrivateKeyBytes;
   }

   @Nullable
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBigInteger getNistPrivateKeyValue() {
      return this.nistPrivateKeyValue;
   }

   public EciesParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public EciesPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof EciesPrivateKey)) {
         return false;
      } else {
         EciesPrivateKey other = (EciesPrivateKey)o;
         if (!this.publicKey.equalsKey(other.publicKey)) {
            return false;
         } else {
            return this.x25519PrivateKeyBytes == null && other.x25519PrivateKeyBytes == null
               ? this.nistPrivateKeyValue.equalsSecretBigInteger(other.nistPrivateKeyValue)
               : this.x25519PrivateKeyBytes.equalsSecretBytes(other.x25519PrivateKeyBytes);
         }
      }
   }
}
