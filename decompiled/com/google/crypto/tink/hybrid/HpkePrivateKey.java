package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.X25519;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.util.Arrays;

@Immutable
public final class HpkePrivateKey extends HybridPrivateKey {
   private final HpkePublicKey publicKey;
   private final SecretBytes privateKeyBytes;

   private HpkePrivateKey(HpkePublicKey publicKey, SecretBytes privateKeyBytes) {
      this.publicKey = publicKey;
      this.privateKeyBytes = privateKeyBytes;
   }

   private static void validatePrivateKeyByteLength(HpkeParameters.KemId kemId, SecretBytes privateKeyBytes) throws GeneralSecurityException {
      int keyLengthInBytes = privateKeyBytes.size();
      String parameterizedErrorMessage = "Encoded private key byte length for " + kemId + " must be %d, not " + keyLengthInBytes;
      if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
         if (keyLengthInBytes != 32) {
            throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 32));
         }
      } else if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
         if (keyLengthInBytes != 48) {
            throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 48));
         }
      } else if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
         if (keyLengthInBytes != 66) {
            throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 66));
         }
      } else if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
         if (keyLengthInBytes != 32) {
            throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 32));
         }
      } else {
         throw new GeneralSecurityException("Unable to validate private key length for " + kemId);
      }
   }

   private static boolean isNistKem(HpkeParameters.KemId kemId) {
      return kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256
         || kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384
         || kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512;
   }

   private static ECParameterSpec getNistCurveParams(HpkeParameters.KemId kemId) {
      if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
         return EllipticCurves.getNistP256Params();
      } else if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
         return EllipticCurves.getNistP384Params();
      } else if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
         return EllipticCurves.getNistP521Params();
      } else {
         throw new IllegalArgumentException("Unable to determine NIST curve params for " + kemId);
      }
   }

   private static void validateKeyPair(HpkeParameters.KemId kemId, byte[] publicKeyBytes, byte[] privateKeyBytes) throws GeneralSecurityException {
      if (isNistKem(kemId)) {
         ECParameterSpec spec = getNistCurveParams(kemId);
         BigInteger order = spec.getOrder();
         BigInteger privateKey = BigIntegerEncoding.fromUnsignedBigEndianBytes(privateKeyBytes);
         if (privateKey.signum() > 0 && privateKey.compareTo(order) < 0) {
            ECPoint expectedPoint = EllipticCurvesUtil.multiplyByGenerator(privateKey, spec);
            ECPoint publicPoint = EllipticCurves.pointDecode(spec.getCurve(), EllipticCurves.PointFormatType.UNCOMPRESSED, publicKeyBytes);
            if (!expectedPoint.equals(publicPoint)) {
               throw new GeneralSecurityException("Invalid private key for public key.");
            }
         } else {
            throw new GeneralSecurityException("Invalid private key.");
         }
      } else if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
         byte[] expectedPublicKeyBytes = X25519.publicFromPrivate(privateKeyBytes);
         if (!Arrays.equals(expectedPublicKeyBytes, publicKeyBytes)) {
            throw new GeneralSecurityException("Invalid private key for public key.");
         }
      } else {
         throw new IllegalArgumentException("Unable to validate key pair for " + kemId);
      }
   }

   @AccessesPartialKey
   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static HpkePrivateKey create(HpkePublicKey publicKey, SecretBytes privateKeyBytes) throws GeneralSecurityException {
      if (publicKey == null) {
         throw new GeneralSecurityException("HPKE private key cannot be constructed without an HPKE public key");
      } else if (privateKeyBytes == null) {
         throw new GeneralSecurityException("HPKE private key cannot be constructed without secret");
      } else {
         validatePrivateKeyByteLength(publicKey.getParameters().getKemId(), privateKeyBytes);
         validateKeyPair(
            publicKey.getParameters().getKemId(), publicKey.getPublicKeyBytes().toByteArray(), privateKeyBytes.toByteArray(InsecureSecretKeyAccess.get())
         );
         return new HpkePrivateKey(publicKey, privateKeyBytes);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBytes getPrivateKeyBytes() {
      return this.privateKeyBytes;
   }

   public HpkeParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public HpkePublicKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof HpkePrivateKey)) {
         return false;
      } else {
         HpkePrivateKey other = (HpkePrivateKey)o;
         return this.publicKey.equalsKey(other.publicKey) && this.privateKeyBytes.equalsSecretBytes(other.privateKeyBytes);
      }
   }
}
