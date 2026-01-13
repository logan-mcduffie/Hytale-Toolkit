package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.RsaSsaPssPrivateKey;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Optional;

public final class JwtRsaSsaPssPrivateKey extends JwtSignaturePrivateKey {
   private final JwtRsaSsaPssPublicKey publicKey;
   private final RsaSsaPssPrivateKey rsaSsaPssPrivateKey;

   private JwtRsaSsaPssPrivateKey(JwtRsaSsaPssPublicKey publicKey, RsaSsaPssPrivateKey rsaSsaPssPrivateKey) {
      this.publicKey = publicKey;
      this.rsaSsaPssPrivateKey = rsaSsaPssPrivateKey;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static JwtRsaSsaPssPrivateKey.Builder builder() {
      return new JwtRsaSsaPssPrivateKey.Builder();
   }

   public JwtRsaSsaPssParameters getParameters() {
      return this.publicKey.getParameters();
   }

   public JwtRsaSsaPssPublicKey getPublicKey() {
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
      return this.rsaSsaPssPrivateKey.getPrimeP();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public SecretBigInteger getPrimeQ() {
      return this.rsaSsaPssPrivateKey.getPrimeQ();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrivateExponent() {
      return this.rsaSsaPssPrivateKey.getPrivateExponent();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrimeExponentP() {
      return this.rsaSsaPssPrivateKey.getPrimeExponentP();
   }

   @AccessesPartialKey
   public SecretBigInteger getPrimeExponentQ() {
      return this.rsaSsaPssPrivateKey.getPrimeExponentQ();
   }

   @AccessesPartialKey
   public SecretBigInteger getCrtCoefficient() {
      return this.rsaSsaPssPrivateKey.getCrtCoefficient();
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof JwtRsaSsaPssPrivateKey)) {
         return false;
      } else {
         JwtRsaSsaPssPrivateKey that = (JwtRsaSsaPssPrivateKey)o;
         return that.publicKey.equalsKey(this.publicKey) && that.rsaSsaPssPrivateKey.equalsKey(this.rsaSsaPssPrivateKey);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   RsaSsaPssPrivateKey getRsaSsaPssPrivateKey() {
      return this.rsaSsaPssPrivateKey;
   }

   public static class Builder {
      private Optional<JwtRsaSsaPssPublicKey> publicKey = Optional.empty();
      private Optional<SecretBigInteger> d = Optional.empty();
      private Optional<SecretBigInteger> p = Optional.empty();
      private Optional<SecretBigInteger> q = Optional.empty();
      private Optional<SecretBigInteger> dP = Optional.empty();
      private Optional<SecretBigInteger> dQ = Optional.empty();
      private Optional<SecretBigInteger> qInv = Optional.empty();

      private Builder() {
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPrivateKey.Builder setPublicKey(JwtRsaSsaPssPublicKey publicKey) {
         this.publicKey = Optional.of(publicKey);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPrivateKey.Builder setPrimes(SecretBigInteger p, SecretBigInteger q) {
         this.p = Optional.of(p);
         this.q = Optional.of(q);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPrivateKey.Builder setPrivateExponent(SecretBigInteger d) {
         this.d = Optional.of(d);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPrivateKey.Builder setPrimeExponents(SecretBigInteger dP, SecretBigInteger dQ) {
         this.dP = Optional.of(dP);
         this.dQ = Optional.of(dQ);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPrivateKey.Builder setCrtCoefficient(SecretBigInteger qInv) {
         this.qInv = Optional.of(qInv);
         return this;
      }

      @AccessesPartialKey
      public JwtRsaSsaPssPrivateKey build() throws GeneralSecurityException {
         if (!this.publicKey.isPresent()) {
            throw new GeneralSecurityException("Cannot build without a RSA SSA PSS public key");
         } else if (!this.p.isPresent() || !this.q.isPresent()) {
            throw new GeneralSecurityException("Cannot build without prime factors");
         } else if (!this.d.isPresent()) {
            throw new GeneralSecurityException("Cannot build without private exponent");
         } else if (!this.dP.isPresent() || !this.dQ.isPresent()) {
            throw new GeneralSecurityException("Cannot build without prime exponents");
         } else if (!this.qInv.isPresent()) {
            throw new GeneralSecurityException("Cannot build without CRT coefficient");
         } else {
            RsaSsaPssPrivateKey rsaSsaPssPrivateKey = RsaSsaPssPrivateKey.builder()
               .setPublicKey(this.publicKey.get().getRsaSsaPssPublicKey())
               .setPrimes(this.p.get(), this.q.get())
               .setPrivateExponent(this.d.get())
               .setPrimeExponents(this.dP.get(), this.dQ.get())
               .setCrtCoefficient(this.qInv.get())
               .build();
            return new JwtRsaSsaPssPrivateKey(this.publicKey.get(), rsaSsaPssPrivateKey);
         }
      }
   }
}
