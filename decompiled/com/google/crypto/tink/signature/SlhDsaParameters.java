package com.google.crypto.tink.signature;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public class SlhDsaParameters extends SignatureParameters {
   public static final int SLH_DSA_128_PRIVATE_KEY_SIZE_BYTES = 64;
   private final SlhDsaParameters.HashType hashType;
   private final SlhDsaParameters.SignatureType signatureType;
   private final SlhDsaParameters.Variant variant;
   private final int privateKeySize;

   public static SlhDsaParameters createSlhDsaWithSha2And128S(SlhDsaParameters.Variant variant) {
      return new SlhDsaParameters(SlhDsaParameters.HashType.SHA2, 64, SlhDsaParameters.SignatureType.SMALL_SIGNATURE, variant);
   }

   private SlhDsaParameters(
      SlhDsaParameters.HashType hashType, int privateKeySizeBytes, SlhDsaParameters.SignatureType signatureType, SlhDsaParameters.Variant variant
   ) {
      this.hashType = hashType;
      this.privateKeySize = privateKeySizeBytes;
      this.signatureType = signatureType;
      this.variant = variant;
   }

   public SlhDsaParameters.HashType getHashType() {
      return this.hashType;
   }

   public SlhDsaParameters.SignatureType getSignatureType() {
      return this.signatureType;
   }

   public SlhDsaParameters.Variant getVariant() {
      return this.variant;
   }

   public int getPrivateKeySize() {
      return this.privateKeySize;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof SlhDsaParameters)) {
         return false;
      } else {
         SlhDsaParameters other = (SlhDsaParameters)o;
         return other.getHashType() == this.getHashType()
            && other.getSignatureType() == this.getSignatureType()
            && other.getVariant() == this.getVariant()
            && other.getPrivateKeySize() == this.getPrivateKeySize();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(SlhDsaParameters.class, this.hashType, this.privateKeySize, this.signatureType, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != SlhDsaParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "SLH-DSA-" + this.hashType.toString() + "-" + this.privateKeySize * 2 + this.signatureType + " instance, variant: " + this.variant;
   }

   @Immutable
   public static final class HashType {
      public static final SlhDsaParameters.HashType SHA2 = new SlhDsaParameters.HashType("SHA2");
      public static final SlhDsaParameters.HashType SHAKE = new SlhDsaParameters.HashType("SHAKE");
      private final String name;

      private HashType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class SignatureType {
      public static final SlhDsaParameters.SignatureType FAST_SIGNING = new SlhDsaParameters.SignatureType("F");
      public static final SlhDsaParameters.SignatureType SMALL_SIGNATURE = new SlhDsaParameters.SignatureType("S");
      private final String name;

      private SignatureType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final SlhDsaParameters.Variant TINK = new SlhDsaParameters.Variant("TINK");
      public static final SlhDsaParameters.Variant NO_PREFIX = new SlhDsaParameters.Variant("NO_PREFIX");
      private final String name;

      private Variant(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
