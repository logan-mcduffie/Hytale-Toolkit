package com.google.crypto.tink.jwt;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import java.util.Optional;

public final class JwtRsaSsaPssParameters extends JwtSignatureParameters {
   public static final BigInteger F4 = BigInteger.valueOf(65537L);
   private final int modulusSizeBits;
   private final BigInteger publicExponent;
   private final JwtRsaSsaPssParameters.KidStrategy kidStrategy;
   private final JwtRsaSsaPssParameters.Algorithm algorithm;

   private JwtRsaSsaPssParameters(
      int modulusSizeBits, BigInteger publicExponent, JwtRsaSsaPssParameters.KidStrategy kidStrategy, JwtRsaSsaPssParameters.Algorithm algorithm
   ) {
      this.modulusSizeBits = modulusSizeBits;
      this.publicExponent = publicExponent;
      this.kidStrategy = kidStrategy;
      this.algorithm = algorithm;
   }

   public static JwtRsaSsaPssParameters.Builder builder() {
      return new JwtRsaSsaPssParameters.Builder();
   }

   public int getModulusSizeBits() {
      return this.modulusSizeBits;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   public JwtRsaSsaPssParameters.KidStrategy getKidStrategy() {
      return this.kidStrategy;
   }

   public JwtRsaSsaPssParameters.Algorithm getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public boolean allowKidAbsent() {
      return this.kidStrategy.equals(JwtRsaSsaPssParameters.KidStrategy.CUSTOM) || this.kidStrategy.equals(JwtRsaSsaPssParameters.KidStrategy.IGNORED);
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof JwtRsaSsaPssParameters)) {
         return false;
      } else {
         JwtRsaSsaPssParameters that = (JwtRsaSsaPssParameters)o;
         return that.getModulusSizeBits() == this.getModulusSizeBits()
            && Objects.equals(that.getPublicExponent(), this.getPublicExponent())
            && that.kidStrategy.equals(this.kidStrategy)
            && that.algorithm.equals(this.algorithm);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(JwtRsaSsaPssParameters.class, this.modulusSizeBits, this.publicExponent, this.kidStrategy, this.algorithm);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.kidStrategy.equals(JwtRsaSsaPssParameters.KidStrategy.BASE64_ENCODED_KEY_ID);
   }

   @Override
   public String toString() {
      return "JWT RSA SSA PSS Parameters (kidStrategy: "
         + this.kidStrategy
         + ", algorithm "
         + this.algorithm
         + ", publicExponent: "
         + this.publicExponent
         + ", and "
         + this.modulusSizeBits
         + "-bit modulus)";
   }

   @Immutable
   public static final class Algorithm {
      public static final JwtRsaSsaPssParameters.Algorithm PS256 = new JwtRsaSsaPssParameters.Algorithm("PS256");
      public static final JwtRsaSsaPssParameters.Algorithm PS384 = new JwtRsaSsaPssParameters.Algorithm("PS384");
      public static final JwtRsaSsaPssParameters.Algorithm PS512 = new JwtRsaSsaPssParameters.Algorithm("PS512");
      private final String name;

      private Algorithm(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public String getStandardName() {
         return this.name;
      }
   }

   public static final class Builder {
      Optional<Integer> modulusSizeBits = Optional.empty();
      Optional<BigInteger> publicExponent = Optional.of(JwtRsaSsaPssParameters.F4);
      Optional<JwtRsaSsaPssParameters.KidStrategy> kidStrategy = Optional.empty();
      Optional<JwtRsaSsaPssParameters.Algorithm> algorithm = Optional.empty();
      private static final BigInteger TWO = BigInteger.valueOf(2L);
      private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);

      private Builder() {
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssParameters.Builder setModulusSizeBits(int modulusSizeBits) {
         this.modulusSizeBits = Optional.of(modulusSizeBits);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssParameters.Builder setPublicExponent(BigInteger e) {
         this.publicExponent = Optional.of(e);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssParameters.Builder setKidStrategy(JwtRsaSsaPssParameters.KidStrategy kidStrategy) {
         this.kidStrategy = Optional.of(kidStrategy);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtRsaSsaPssParameters.Builder setAlgorithm(JwtRsaSsaPssParameters.Algorithm algorithm) {
         this.algorithm = Optional.of(algorithm);
         return this;
      }

      private void validatePublicExponent(BigInteger publicExponent) throws InvalidAlgorithmParameterException {
         int c = publicExponent.compareTo(JwtRsaSsaPssParameters.F4);
         if (c != 0) {
            if (c < 0) {
               throw new InvalidAlgorithmParameterException("Public exponent must be at least 65537.");
            } else if (publicExponent.mod(TWO).equals(BigInteger.ZERO)) {
               throw new InvalidAlgorithmParameterException("Invalid public exponent");
            } else if (publicExponent.compareTo(PUBLIC_EXPONENT_UPPER_BOUND) > 0) {
               throw new InvalidAlgorithmParameterException("Public exponent cannot be larger than 2^256.");
            }
         }
      }

      public JwtRsaSsaPssParameters build() throws GeneralSecurityException {
         if (!this.modulusSizeBits.isPresent()) {
            throw new GeneralSecurityException("key size is not set");
         } else if (!this.publicExponent.isPresent()) {
            throw new GeneralSecurityException("publicExponent is not set");
         } else if (!this.algorithm.isPresent()) {
            throw new GeneralSecurityException("Algorithm must be set");
         } else if (!this.kidStrategy.isPresent()) {
            throw new GeneralSecurityException("KidStrategy must be set");
         } else if (this.modulusSizeBits.get() < 2048) {
            throw new InvalidAlgorithmParameterException(
               String.format("Invalid modulus size in bits %d; must be at least 2048 bits", this.modulusSizeBits.get())
            );
         } else {
            this.validatePublicExponent(this.publicExponent.get());
            return new JwtRsaSsaPssParameters(this.modulusSizeBits.get(), this.publicExponent.get(), this.kidStrategy.get(), this.algorithm.get());
         }
      }
   }

   @Immutable
   public static final class KidStrategy {
      public static final JwtRsaSsaPssParameters.KidStrategy BASE64_ENCODED_KEY_ID = new JwtRsaSsaPssParameters.KidStrategy("BASE64_ENCODED_KEY_ID");
      public static final JwtRsaSsaPssParameters.KidStrategy IGNORED = new JwtRsaSsaPssParameters.KidStrategy("IGNORED");
      public static final JwtRsaSsaPssParameters.KidStrategy CUSTOM = new JwtRsaSsaPssParameters.KidStrategy("CUSTOM");
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
