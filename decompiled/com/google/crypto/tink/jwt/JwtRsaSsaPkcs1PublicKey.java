package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.RsaSsaPkcs1Parameters;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.subtle.Base64;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Optional;
import javax.annotation.Nullable;

public final class JwtRsaSsaPkcs1PublicKey extends JwtSignaturePublicKey {
   private final JwtRsaSsaPkcs1Parameters parameters;
   private final RsaSsaPkcs1PublicKey rsaSsaPkcs1PublicKey;
   private final Optional<Integer> idRequirement;
   private final Optional<String> kid;

   private static RsaSsaPkcs1Parameters.HashType getHashType(JwtRsaSsaPkcs1Parameters.Algorithm algorithm) throws GeneralSecurityException {
      if (algorithm.equals(JwtRsaSsaPkcs1Parameters.Algorithm.RS256)) {
         return RsaSsaPkcs1Parameters.HashType.SHA256;
      } else if (algorithm.equals(JwtRsaSsaPkcs1Parameters.Algorithm.RS384)) {
         return RsaSsaPkcs1Parameters.HashType.SHA384;
      } else if (algorithm.equals(JwtRsaSsaPkcs1Parameters.Algorithm.RS512)) {
         return RsaSsaPkcs1Parameters.HashType.SHA512;
      } else {
         throw new GeneralSecurityException("unknown algorithm " + algorithm);
      }
   }

   @AccessesPartialKey
   private static RsaSsaPkcs1PublicKey toRsaSsaPkcs1PublicKey(JwtRsaSsaPkcs1Parameters parameters, BigInteger modulus) throws GeneralSecurityException {
      RsaSsaPkcs1Parameters rsaSsaPkcs1Parameters = RsaSsaPkcs1Parameters.builder()
         .setModulusSizeBits(parameters.getModulusSizeBits())
         .setPublicExponent(parameters.getPublicExponent())
         .setHashType(getHashType(parameters.getAlgorithm()))
         .setVariant(RsaSsaPkcs1Parameters.Variant.NO_PREFIX)
         .build();
      return RsaSsaPkcs1PublicKey.builder().setParameters(rsaSsaPkcs1Parameters).setModulus(modulus).build();
   }

   private JwtRsaSsaPkcs1PublicKey(
      JwtRsaSsaPkcs1Parameters parameters, RsaSsaPkcs1PublicKey rsaSsaPkcs1PublicKey, Optional<Integer> idRequirement, Optional<String> kid
   ) {
      this.parameters = parameters;
      this.rsaSsaPkcs1PublicKey = rsaSsaPkcs1PublicKey;
      this.idRequirement = idRequirement;
      this.kid = kid;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static JwtRsaSsaPkcs1PublicKey.Builder builder() {
      return new JwtRsaSsaPkcs1PublicKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public BigInteger getModulus() {
      return this.rsaSsaPkcs1PublicKey.getModulus();
   }

   @Override
   public Optional<String> getKid() {
      return this.kid;
   }

   public JwtRsaSsaPkcs1Parameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement.orElse(null);
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof JwtRsaSsaPkcs1PublicKey)) {
         return false;
      } else {
         JwtRsaSsaPkcs1PublicKey that = (JwtRsaSsaPkcs1PublicKey)o;
         return that.parameters.equals(this.parameters)
            && that.rsaSsaPkcs1PublicKey.equalsKey(this.rsaSsaPkcs1PublicKey)
            && that.kid.equals(this.kid)
            && that.idRequirement.equals(this.idRequirement);
      }
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   RsaSsaPkcs1PublicKey getRsaSsaPkcs1PublicKey() {
      return this.rsaSsaPkcs1PublicKey;
   }

   public static class Builder {
      private Optional<JwtRsaSsaPkcs1Parameters> parameters = Optional.empty();
      private Optional<BigInteger> modulus = Optional.empty();
      private Optional<Integer> idRequirement = Optional.empty();
      private Optional<String> customKid = Optional.empty();

      private Builder() {
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PublicKey.Builder setParameters(JwtRsaSsaPkcs1Parameters parameters) {
         this.parameters = Optional.of(parameters);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PublicKey.Builder setModulus(BigInteger modulus) {
         this.modulus = Optional.of(modulus);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PublicKey.Builder setIdRequirement(Integer idRequirement) {
         this.idRequirement = Optional.of(idRequirement);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPkcs1PublicKey.Builder setCustomKid(String customKid) {
         this.customKid = Optional.of(customKid);
         return this;
      }

      private Optional<String> computeKid() throws GeneralSecurityException {
         if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
            if (this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid must not be set for KidStrategy BASE64_ENCODED_KEY_ID");
            } else {
               byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(this.idRequirement.get()).array();
               return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
            }
         } else if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.CUSTOM)) {
            if (!this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid needs to be set for KidStrategy CUSTOM");
            } else {
               return this.customKid;
            }
         } else if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED)) {
            if (this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid must not be set for KidStrategy IGNORED");
            } else {
               return Optional.empty();
            }
         } else {
            throw new IllegalStateException("Unknown kid strategy");
         }
      }

      public JwtRsaSsaPkcs1PublicKey build() throws GeneralSecurityException {
         if (!this.parameters.isPresent()) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (!this.modulus.isPresent()) {
            throw new GeneralSecurityException("Cannot build without modulus");
         } else {
            RsaSsaPkcs1PublicKey rsaSsaPkcs1PublicKey = JwtRsaSsaPkcs1PublicKey.toRsaSsaPkcs1PublicKey(this.parameters.get(), this.modulus.get());
            if (this.parameters.get().hasIdRequirement() && !this.idRequirement.isPresent()) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.get().hasIdRequirement() && this.idRequirement.isPresent()) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               return new JwtRsaSsaPkcs1PublicKey(this.parameters.get(), rsaSsaPkcs1PublicKey, this.idRequirement, this.computeKid());
            }
         }
      }
   }
}
