package com.google.crypto.tink.jwt;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;

public class JwtHmacParameters extends JwtMacParameters {
   private final int keySizeBytes;
   private final JwtHmacParameters.KidStrategy kidStrategy;
   private final JwtHmacParameters.Algorithm algorithm;

   public static JwtHmacParameters.Builder builder() {
      return new JwtHmacParameters.Builder();
   }

   private JwtHmacParameters(int keySizeBytes, JwtHmacParameters.KidStrategy kidStrategy, JwtHmacParameters.Algorithm algorithm) {
      this.keySizeBytes = keySizeBytes;
      this.kidStrategy = kidStrategy;
      this.algorithm = algorithm;
   }

   public int getKeySizeBytes() {
      return this.keySizeBytes;
   }

   public JwtHmacParameters.KidStrategy getKidStrategy() {
      return this.kidStrategy;
   }

   public JwtHmacParameters.Algorithm getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public boolean hasIdRequirement() {
      return this.kidStrategy.equals(JwtHmacParameters.KidStrategy.BASE64_ENCODED_KEY_ID);
   }

   @Override
   public boolean allowKidAbsent() {
      return this.kidStrategy.equals(JwtHmacParameters.KidStrategy.CUSTOM) || this.kidStrategy.equals(JwtHmacParameters.KidStrategy.IGNORED);
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof JwtHmacParameters)) {
         return false;
      } else {
         JwtHmacParameters that = (JwtHmacParameters)o;
         return that.keySizeBytes == this.keySizeBytes && that.kidStrategy.equals(this.kidStrategy) && that.algorithm.equals(this.algorithm);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(JwtHmacParameters.class, this.keySizeBytes, this.kidStrategy, this.algorithm);
   }

   @Override
   public String toString() {
      return "JWT HMAC Parameters (kidStrategy: " + this.kidStrategy + ", Algorithm " + this.algorithm + ", and " + this.keySizeBytes + "-byte key)";
   }

   @Immutable
   public static final class Algorithm {
      public static final JwtHmacParameters.Algorithm HS256 = new JwtHmacParameters.Algorithm("HS256");
      public static final JwtHmacParameters.Algorithm HS384 = new JwtHmacParameters.Algorithm("HS384");
      public static final JwtHmacParameters.Algorithm HS512 = new JwtHmacParameters.Algorithm("HS512");
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
      Optional<Integer> keySizeBytes = Optional.empty();
      Optional<JwtHmacParameters.KidStrategy> kidStrategy = Optional.empty();
      Optional<JwtHmacParameters.Algorithm> algorithm = Optional.empty();

      @CanIgnoreReturnValue
      public JwtHmacParameters.Builder setKeySizeBytes(int keySizeBytes) {
         this.keySizeBytes = Optional.of(keySizeBytes);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtHmacParameters.Builder setKidStrategy(JwtHmacParameters.KidStrategy kidStrategy) {
         this.kidStrategy = Optional.of(kidStrategy);
         return this;
      }

      @CanIgnoreReturnValue
      public JwtHmacParameters.Builder setAlgorithm(JwtHmacParameters.Algorithm algorithm) {
         this.algorithm = Optional.of(algorithm);
         return this;
      }

      public JwtHmacParameters build() throws GeneralSecurityException {
         if (!this.keySizeBytes.isPresent()) {
            throw new GeneralSecurityException("Key Size must be set");
         } else if (!this.algorithm.isPresent()) {
            throw new GeneralSecurityException("Algorithm must be set");
         } else if (!this.kidStrategy.isPresent()) {
            throw new GeneralSecurityException("KidStrategy must be set");
         } else if (this.keySizeBytes.get() < 16) {
            throw new GeneralSecurityException("Key size must be at least 16 bytes");
         } else {
            return new JwtHmacParameters(this.keySizeBytes.get(), this.kidStrategy.get(), this.algorithm.get());
         }
      }

      private Builder() {
      }
   }

   @Immutable
   public static final class KidStrategy {
      public static final JwtHmacParameters.KidStrategy BASE64_ENCODED_KEY_ID = new JwtHmacParameters.KidStrategy("BASE64_ENCODED_KEY_ID");
      public static final JwtHmacParameters.KidStrategy IGNORED = new JwtHmacParameters.KidStrategy("IGNORED");
      public static final JwtHmacParameters.KidStrategy CUSTOM = new JwtHmacParameters.KidStrategy("CUSTOM");
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
