package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;

@Immutable
public final class EcdsaPrivateKey extends SignaturePrivateKey {
   private final EcdsaPublicKey publicKey;
   private final SecretBigInteger privateValue;

   private EcdsaPrivateKey(EcdsaPublicKey publicKey, SecretBigInteger privateValue) {
      this.publicKey = publicKey;
      this.privateValue = privateValue;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static EcdsaPrivateKey.Builder builder() {
      return new EcdsaPrivateKey.Builder();
   }

   public EcdsaParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public EcdsaPublicKey getPublicKey() {
      return this.publicKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBigInteger getPrivateValue() {
      return this.privateValue;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof EcdsaPrivateKey)) {
         return false;
      } else {
         EcdsaPrivateKey that = (EcdsaPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && this.privateValue.equalsSecretBigInteger(that.privateValue);
      }
   }

   public static class Builder {
      private EcdsaPublicKey publicKey = null;
      private SecretBigInteger privateValue = null;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public EcdsaPrivateKey.Builder setPublicKey(EcdsaPublicKey publicKey) {
         this.publicKey = publicKey;
         return this;
      }

      @CanIgnoreReturnValue
      public EcdsaPrivateKey.Builder setPrivateValue(SecretBigInteger privateValue) {
         this.privateValue = privateValue;
         return this;
      }

      private static void validatePrivateValue(BigInteger privateValue, ECPoint publicPoint, EcdsaParameters.CurveType curveType) throws GeneralSecurityException {
         BigInteger order = curveType.toParameterSpec().getOrder();
         if (privateValue.signum() > 0 && privateValue.compareTo(order) < 0) {
            ECPoint p = EllipticCurvesUtil.multiplyByGenerator(privateValue, curveType.toParameterSpec());
            if (!p.equals(publicPoint)) {
               throw new GeneralSecurityException("Invalid private value");
            }
         } else {
            throw new GeneralSecurityException("Invalid private value");
         }
      }

      @AccessesPartialKey
      public EcdsaPrivateKey build() throws GeneralSecurityException {
         if (this.publicKey == null) {
            throw new GeneralSecurityException("Cannot build without a ecdsa public key");
         } else if (this.privateValue == null) {
            throw new GeneralSecurityException("Cannot build without a private value");
         } else {
            validatePrivateValue(
               this.privateValue.getBigInteger(InsecureSecretKeyAccess.get()), this.publicKey.getPublicPoint(), this.publicKey.getParameters().getCurveType()
            );
            return new EcdsaPrivateKey(this.publicKey, this.privateValue);
         }
      }
   }
}
