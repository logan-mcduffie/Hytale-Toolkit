package com.google.crypto.tink.jwt;

import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.spec.ECParameterSpec;
import java.util.Objects;
import java.util.Optional;

public final class JwtEcdsaParameters extends JwtSignatureParameters {
   private final JwtEcdsaParameters.KidStrategy kidStrategy;
   private final JwtEcdsaParameters.Algorithm algorithm;

   public static JwtEcdsaParameters.Builder builder() {
      return new JwtEcdsaParameters.Builder();
   }

   private JwtEcdsaParameters(JwtEcdsaParameters.KidStrategy kidStrategy, JwtEcdsaParameters.Algorithm algorithm) {
      this.kidStrategy = kidStrategy;
      this.algorithm = algorithm;
   }

   public JwtEcdsaParameters.KidStrategy getKidStrategy() {
      return this.kidStrategy;
   }

   public JwtEcdsaParameters.Algorithm getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.kidStrategy.equals(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID);
   }

   @Override
   public boolean allowKidAbsent() {
      return this.kidStrategy.equals(JwtEcdsaParameters.KidStrategy.CUSTOM) || this.kidStrategy.equals(JwtEcdsaParameters.KidStrategy.IGNORED);
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof JwtEcdsaParameters)) {
         return false;
      } else {
         JwtEcdsaParameters that = (JwtEcdsaParameters)o;
         return that.kidStrategy.equals(this.kidStrategy) && that.algorithm.equals(this.algorithm);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(JwtEcdsaParameters.class, this.kidStrategy, this.algorithm);
   }

   @Override
   public String toString() {
      return "JWT ECDSA Parameters (kidStrategy: " + this.kidStrategy + ", Algorithm " + this.algorithm + ")";
   }

   @Immutable
   public static final class Algorithm {
      public static final JwtEcdsaParameters.Algorithm ES256 = new JwtEcdsaParameters.Algorithm("ES256", EllipticCurvesUtil.NIST_P256_PARAMS);
      public static final JwtEcdsaParameters.Algorithm ES384 = new JwtEcdsaParameters.Algorithm("ES384", EllipticCurvesUtil.NIST_P384_PARAMS);
      public static final JwtEcdsaParameters.Algorithm ES512 = new JwtEcdsaParameters.Algorithm("ES512", EllipticCurvesUtil.NIST_P521_PARAMS);
      private final String name;
      private final ECParameterSpec ecParameterSpec;

      private Algorithm(String name, ECParameterSpec ecParameterSpec) {
         this.name = name;
         this.ecParameterSpec = ecParameterSpec;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public String getStandardName() {
         return this.name;
      }

      public ECParameterSpec getEcParameterSpec() {
         return this.ecParameterSpec;
      }
   }

   public static final class Builder {
      Optional<JwtEcdsaParameters.KidStrategy> kidStrategy = Optional.empty();
      Optional<JwtEcdsaParameters.Algorithm> algorithm = Optional.empty();

      @CanIgnoreReturnValue
      public JwtEcdsaParameters.Builder setKidStrategy(JwtEcdsaParameters.KidStrategy kidStrategy) {
         this.kidStrategy = Optional.of(kidStrategy);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtEcdsaParameters.Builder setAlgorithm(JwtEcdsaParameters.Algorithm algorithm) {
         this.algorithm = Optional.of(algorithm);
         return this;
      }

      public JwtEcdsaParameters build() throws GeneralSecurityException {
         if (!this.algorithm.isPresent()) {
            throw new GeneralSecurityException("Algorithm must be set");
         } else if (!this.kidStrategy.isPresent()) {
            throw new GeneralSecurityException("KidStrategy must be set");
         } else {
            return new JwtEcdsaParameters(this.kidStrategy.get(), this.algorithm.get());
         }
      }

      private Builder() {
      }
   }

   @Immutable
   public static final class KidStrategy {
      public static final JwtEcdsaParameters.KidStrategy BASE64_ENCODED_KEY_ID = new JwtEcdsaParameters.KidStrategy("BASE64_ENCODED_KEY_ID");
      public static final JwtEcdsaParameters.KidStrategy IGNORED = new JwtEcdsaParameters.KidStrategy("IGNORED");
      public static final JwtEcdsaParameters.KidStrategy CUSTOM = new JwtEcdsaParameters.KidStrategy("CUSTOM");
      private final String name;

      private KidStrategy(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
