package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.subtle.Base64;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Optional;
import javax.annotation.Nullable;

public final class JwtRsaSsaPssPublicKey extends JwtSignaturePublicKey {
   private final JwtRsaSsaPssParameters parameters;
   private final RsaSsaPssPublicKey rsaSsaPssPublicKey;
   private final Optional<Integer> idRequirement;
   private final Optional<String> kid;

   private JwtRsaSsaPssPublicKey(
      JwtRsaSsaPssParameters parameters, RsaSsaPssPublicKey rsaSsaPssPublicKey, Optional<Integer> idRequirement, Optional<String> kid
   ) {
      this.parameters = parameters;
      this.rsaSsaPssPublicKey = rsaSsaPssPublicKey;
      this.idRequirement = idRequirement;
      this.kid = kid;
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   public static JwtRsaSsaPssPublicKey.Builder builder() {
      return new JwtRsaSsaPssPublicKey.Builder();
   }

   @RestrictedApi(
      explanation = "Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey",
      link = "https://developers.google.com/tink/design/access_control#accessing_partial_keys",
      allowedOnPath = ".*Test\\.java",
      allowlistAnnotations = AccessesPartialKey.class
   )
   @AccessesPartialKey
   public BigInteger getModulus() {
      return this.rsaSsaPssPublicKey.getModulus();
   }

   @Override
   public Optional<String> getKid() {
      return this.kid;
   }

   public JwtRsaSsaPssParameters getParameters() {
      return this.parameters;
   }

   @Nullable
   @Override
   public Integer getIdRequirementOrNull() {
      return this.idRequirement.orElse(null);
   }

   @Override
   public boolean equalsKey(Key o) {
      if (!(o instanceof JwtRsaSsaPssPublicKey)) {
         return false;
      } else {
         JwtRsaSsaPssPublicKey that = (JwtRsaSsaPssPublicKey)o;
         return that.parameters.equals(this.parameters)
            && that.rsaSsaPssPublicKey.equalsKey(this.rsaSsaPssPublicKey)
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
   RsaSsaPssPublicKey getRsaSsaPssPublicKey() {
      return this.rsaSsaPssPublicKey;
   }

   public static class Builder {
      private Optional<JwtRsaSsaPssParameters> parameters = Optional.empty();
      private Optional<BigInteger> modulus = Optional.empty();
      private Optional<Integer> idRequirement = Optional.empty();
      private Optional<String> customKid = Optional.empty();

      private Builder() {
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPublicKey.Builder setParameters(JwtRsaSsaPssParameters parameters) {
         this.parameters = Optional.of(parameters);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPublicKey.Builder setModulus(BigInteger modulus) {
         this.modulus = Optional.of(modulus);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPublicKey.Builder setIdRequirement(Integer idRequirement) {
         this.idRequirement = Optional.of(idRequirement);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssPublicKey.Builder setCustomKid(String customKid) {
         this.customKid = Optional.of(customKid);
         return this;
      }

      private Optional<String> computeKid() throws GeneralSecurityException {
         if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
            if (this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid must not be set for KidStrategy BASE64_ENCODED_KEY_ID");
            } else {
               byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(this.idRequirement.get()).array();
               return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
            }
         } else if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPssParameters.KidStrategy.CUSTOM)) {
            if (!this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid needs to be set for KidStrategy CUSTOM");
            } else {
               return this.customKid;
            }
         } else if (this.parameters.get().getKidStrategy().equals(JwtRsaSsaPssParameters.KidStrategy.IGNORED)) {
            if (this.customKid.isPresent()) {
               throw new GeneralSecurityException("customKid must not be set for KidStrategy IGNORED");
            } else {
               return Optional.empty();
            }
         } else {
            throw new IllegalStateException("Unknown kid strategy");
         }
      }

      private static RsaSsaPssParameters.HashType getHashType(JwtRsaSsaPssParameters.Algorithm algorithm) throws GeneralSecurityException {
         if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS256)) {
            return RsaSsaPssParameters.HashType.SHA256;
         } else if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS384)) {
            return RsaSsaPssParameters.HashType.SHA384;
         } else if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS512)) {
            return RsaSsaPssParameters.HashType.SHA512;
         } else {
            throw new GeneralSecurityException("unknown algorithm " + algorithm);
         }
      }

      private static int getSaltLengthBytes(JwtRsaSsaPssParameters.Algorithm algorithm) throws GeneralSecurityException {
         if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS256)) {
            return 32;
         } else if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS384)) {
            return 48;
         } else if (algorithm.equals(JwtRsaSsaPssParameters.Algorithm.PS512)) {
            return 64;
         } else {
            throw new GeneralSecurityException("unknown algorithm " + algorithm);
         }
      }

      @AccessesPartialKey
      public JwtRsaSsaPssPublicKey build() throws GeneralSecurityException {
         if (!this.parameters.isPresent()) {
            throw new GeneralSecurityException("Cannot build without parameters");
         } else if (!this.modulus.isPresent()) {
            throw new GeneralSecurityException("Cannot build without modulus");
         } else {
            RsaSsaPssParameters.HashType hashType = getHashType(this.parameters.get().getAlgorithm());
            RsaSsaPssParameters rsaSsaPssParameters = RsaSsaPssParameters.builder()
               .setModulusSizeBits(this.parameters.get().getModulusSizeBits())
               .setPublicExponent(this.parameters.get().getPublicExponent())
               .setSigHashType(hashType)
               .setMgf1HashType(hashType)
               .setSaltLengthBytes(getSaltLengthBytes(this.parameters.get().getAlgorithm()))
               .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
               .build();
            RsaSsaPssPublicKey rsaSsaPssPublicKey = RsaSsaPssPublicKey.builder().setParameters(rsaSsaPssParameters).setModulus(this.modulus.get()).build();
            if (this.parameters.get().hasIdRequirement() && !this.idRequirement.isPresent()) {
               throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            } else if (!this.parameters.get().hasIdRequirement() && this.idRequirement.isPresent()) {
               throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            } else {
               return new JwtRsaSsaPssPublicKey(this.parameters.get(), rsaSsaPssPublicKey, this.idRequirement, this.computeKid());
            }
         }
      }
   }
}
