package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.RsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Optional;

public final class JwtRsaSsaPkcs1PrivateKey extends JwtSignaturePrivateKey {
   private final JwtRsaSsaPkcs1PublicKey publicKey;
   private final RsaSsaPkcs1PrivateKey rsaSsaPkcs1PrivateKey;

   private JwtRsaSsaPkcs1PrivateKey(JwtRsaSsaPkcs1PublicKey publicKey, RsaSsaPkcs1PrivateKey rsaSsaPkcs1PrivateKey) {
      this.publicKey = publicKey;
      this.rsaSsaPkcs1PrivateKey = rsaSsaPkcs1PrivateKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static JwtRsaSsaPkcs1PrivateKey.Builder builder() {
      return new JwtRsaSsaPkcs1PrivateKey.Builder();
   }

   public JwtRsaSsaPkcs1Parameters getParameters() {
      return this.publicKey.getParameters();
   }

   public JwtRsaSsaPkcs1PublicKey getPublicKey() {
      return this.publicKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public SecretBigInteger getPrimeP() {
      return this.rsaSsaPkcs1PrivateKey.getPrimeP();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public SecretBigInteger getPrimeQ() {
      return this.rsaSsaPkcs1PrivateKey.getPrimeQ();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrivateExponent() {
      return this.rsaSsaPkcs1PrivateKey.getPrivateExponent();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrimeExponentP() {
      return this.rsaSsaPkcs1PrivateKey.getPrimeExponentP();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrimeExponentQ() {
      return this.rsaSsaPkcs1PrivateKey.getPrimeExponentQ();
   }

   @AccessesPartialKey
   public SecretBigInteger getCrtCoefficient() {
      return this.rsaSsaPkcs1PrivateKey.getCrtCoefficient();
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof JwtRsaSsaPkcs1PrivateKey)) {
         return false;
      } else {
         JwtRsaSsaPkcs1PrivateKey that = (JwtRsaSsaPkcs1PrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && that.rsaSsaPkcs1PrivateKey.equalsKey(this.rsaSsaPkcs1PrivateKey);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   RsaSsaPkcs1PrivateKey getRsaSsaPkcs1PrivateKey() {
      return this.rsaSsaPkcs1PrivateKey;
   }

   public static class Builder {
      private Optional<JwtRsaSsaPkcs1PublicKey> publicKey = Optional.empty();
      private Optional<SecretBigInteger> d = Optional.empty();
      private Optional<SecretBigInteger> p = Optional.empty();
      private Optional<SecretBigInteger> q = Optional.empty();
      private Optional<SecretBigInteger> dP = Optional.empty();
      private Optional<SecretBigInteger> dQ = Optional.empty();
      private Optional<SecretBigInteger> qInv = Optional.empty();

      private Builder() {
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PrivateKey.Builder setPublicKey(JwtRsaSsaPkcs1PublicKey publicKey) {
         this.publicKey = Optional.of(publicKey);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PrivateKey.Builder setPrimes(SecretBigInteger p, SecretBigInteger q) {
         this.p = Optional.of(p);
         this.q = Optional.of(q);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PrivateKey.Builder setPrivateExponent(SecretBigInteger d) {
         this.d = Optional.of(d);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PrivateKey.Builder setPrimeExponents(SecretBigInteger dP, SecretBigInteger dQ) {
         this.dP = Optional.of(dP);
         this.dQ = Optional.of(dQ);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PrivateKey.Builder setCrtCoefficient(SecretBigInteger qInv) {
         this.qInv = Optional.of(qInv);
         return this;
      }

      @AccessesPartialKey
      public JwtRsaSsaPkcs1PrivateKey build() throws GeneralSecurityException {
         if (!this.publicKey.isPresent()) {
            throw new GeneralSecurityException("Cannot build without a RSA SSA PKCS1 public key");
         } else if (!this.p.isPresent() || !this.q.isPresent()) {
            throw new GeneralSecurityException("Cannot build without prime factors");
         } else if (!this.d.isPresent()) {
            throw new GeneralSecurityException("Cannot build without private exponent");
         } else if (!this.dP.isPresent() || !this.dQ.isPresent()) {
            throw new GeneralSecurityException("Cannot build without prime exponents");
         } else if (!this.qInv.isPresent()) {
            throw new GeneralSecurityException("Cannot build without CRT coefficient");
         } else {
            RsaSsaPkcs1PrivateKey rsaSsaPkcs1PrivateKey = RsaSsaPkcs1PrivateKey.builder()
               .setPublicKey(this.publicKey.get().getRsaSsaPkcs1PublicKey())
               .setPrimes(this.p.get(), this.q.get())
               .setPrivateExponent(this.d.get())
               .setPrimeExponents(this.dP.get(), this.dQ.get())
               .setCrtCoefficient(this.qInv.get())
               .build();
            return new JwtRsaSsaPkcs1PrivateKey(this.publicKey.get(), rsaSsaPkcs1PrivateKey);
         }
      }
   }
}
