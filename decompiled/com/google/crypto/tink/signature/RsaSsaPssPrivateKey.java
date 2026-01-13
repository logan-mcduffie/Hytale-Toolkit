package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public final class RsaSsaPssPrivateKey extends SignaturePrivateKey {
   private final RsaSsaPssPublicKey publicKey;
   private final SecretBigInteger d;
   private final SecretBigInteger p;
   private final SecretBigInteger q;
   private final SecretBigInteger dP;
   private final SecretBigInteger dQ;
   private final SecretBigInteger qInv;

   private RsaSsaPssPrivateKey(
      RsaSsaPssPublicKey publicKey, SecretBigInteger p, SecretBigInteger q, SecretBigInteger d, SecretBigInteger dP, SecretBigInteger dQ, SecretBigInteger qInv
   ) {
      this.publicKey = publicKey;
      this.p = p;
      this.q = q;
      this.d = d;
      this.dP = dP;
      this.dQ = dQ;
      this.qInv = qInv;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static RsaSsaPssPrivateKey.Builder builder() {
      return new RsaSsaPssPrivateKey.Builder();
   }

   public RsaSsaPssParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public RsaSsaPssPublicKey getPublicKey() {
      return this.publicKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBigInteger getPrimeP() {
      return this.p;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public SecretBigInteger getPrimeQ() {
      return this.q;
   }

   public SecretBigInteger getPrivateExponent() {
      return this.d;
   }

   public SecretBigInteger getPrimeExponentP() {
      return this.dP;
   }

   public SecretBigInteger getPrimeExponentQ() {
      return this.dQ;
   }

   public SecretBigInteger getCrtCoefficient() {
      return this.qInv;
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof RsaSsaPssPrivateKey)) {
         return false;
      } else {
         RsaSsaPssPrivateKey that = (RsaSsaPssPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey)
            && this.p.equalsSecretBigInteger(that.p)
            && this.q.equalsSecretBigInteger(that.q)
            && this.d.equalsSecretBigInteger(that.d)
            && this.dP.equalsSecretBigInteger(that.dP)
            && this.dQ.equalsSecretBigInteger(that.dQ)
            && this.qInv.equalsSecretBigInteger(that.qInv);
      }
   }

   public static class Builder {
      @Nullable
      private RsaSsaPssPublicKey publicKey = null;
      @Nullable
      private SecretBigInteger d = null;
      @Nullable
      private SecretBigInteger p = null;
      @Nullable
      private SecretBigInteger q = null;
      @Nullable
      private SecretBigInteger dP = null;
      @Nullable
      private SecretBigInteger dQ = null;
      @Nullable
      private SecretBigInteger qInv = null;
      private static final int PRIME_CERTAINTY = 10;

      private Builder() {
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPrivateKey.Builder setPublicKey(RsaSsaPssPublicKey publicKey) {
         this.publicKey = publicKey;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPrivateKey.Builder setPrimes(SecretBigInteger p, SecretBigInteger q) {
         this.p = p;
         this.q = q;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPrivateKey.Builder setPrivateExponent(SecretBigInteger d) {
         this.d = d;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPrivateKey.Builder setPrimeExponents(SecretBigInteger dP, SecretBigInteger dQ) {
         this.dP = dP;
         this.dQ = dQ;
         return this;
      }

      @CanIgnoreReturnValue
      public RsaSsaPssPrivateKey.Builder setCrtCoefficient(SecretBigInteger qInv) {
         this.qInv = qInv;
         return this;
      }

      @AccessesPartialKey
      public RsaSsaPssPrivateKey build() throws GeneralSecurityException {
         if (this.publicKey == null) {
            throw new GeneralSecurityException("Cannot build without a RSA SSA PKCS1 public key");
         } else if (this.p == null || this.q == null) {
            throw new GeneralSecurityException("Cannot build without prime factors");
         } else if (this.d == null) {
            throw new GeneralSecurityException("Cannot build without private exponent");
         } else if (this.dP == null || this.dQ == null) {
            throw new GeneralSecurityException("Cannot build without prime exponents");
         } else if (this.qInv == null) {
            throw new GeneralSecurityException("Cannot build without CRT coefficient");
         } else {
            BigInteger e = this.publicKey.getParameters().getPublicExponent();
            BigInteger n = this.publicKey.getModulus();
            BigInteger ip = this.p.getBigInteger(InsecureSecretKeyAccess.get());
            BigInteger iq = this.q.getBigInteger(InsecureSecretKeyAccess.get());
            BigInteger id = this.d.getBigInteger(InsecureSecretKeyAccess.get());
            BigInteger idP = this.dP.getBigInteger(InsecureSecretKeyAccess.get());
            BigInteger idQ = this.dQ.getBigInteger(InsecureSecretKeyAccess.get());
            BigInteger iqInv = this.qInv.getBigInteger(InsecureSecretKeyAccess.get());
            if (!ip.isProbablePrime(10)) {
               throw new GeneralSecurityException("p is not a prime");
            } else if (!iq.isProbablePrime(10)) {
               throw new GeneralSecurityException("q is not a prime");
            } else if (!ip.multiply(iq).equals(n)) {
               throw new GeneralSecurityException("Prime p times prime q is not equal to the public key's modulus");
            } else {
               BigInteger pMinusOne = ip.subtract(BigInteger.ONE);
               BigInteger qMinusOne = iq.subtract(BigInteger.ONE);
               BigInteger lambda = pMinusOne.divide(pMinusOne.gcd(qMinusOne)).multiply(qMinusOne);
               if (!e.multiply(id).mod(lambda).equals(BigInteger.ONE)) {
                  throw new GeneralSecurityException("D is invalid.");
               } else if (!e.multiply(idP).mod(pMinusOne).equals(BigInteger.ONE)) {
                  throw new GeneralSecurityException("dP is invalid.");
               } else if (!e.multiply(idQ).mod(qMinusOne).equals(BigInteger.ONE)) {
                  throw new GeneralSecurityException("dQ is invalid.");
               } else if (!iq.multiply(iqInv).mod(ip).equals(BigInteger.ONE)) {
                  throw new GeneralSecurityException("qInv is invalid.");
               } else {
                  return new RsaSsaPssPrivateKey(this.publicKey, this.p, this.q, this.d, this.dP, this.dQ, this.qInv);
               }
            }
         }
      }
   }
}
