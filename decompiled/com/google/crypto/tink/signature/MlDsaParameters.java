package com.google.crypto.tink.signature;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class MlDsaParameters extends SignatureParameters {
   private final MlDsaParameters.MlDsaInstance mlDsaInstance;
   private final MlDsaParameters.Variant variant;

   public static MlDsaParameters create(MlDsaParameters.MlDsaInstance mlDsaInstance, MlDsaParameters.Variant variant) {
      return new MlDsaParameters(mlDsaInstance, variant);
   }

   private MlDsaParameters(MlDsaParameters.MlDsaInstance mlDsaInstance, MlDsaParameters.Variant variant) {
      this.mlDsaInstance = mlDsaInstance;
      this.variant = variant;
   }

   public MlDsaParameters.MlDsaInstance getMlDsaInstance() {
      return this.mlDsaInstance;
   }

   public MlDsaParameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof MlDsaParameters)) {
         return false;
      } else {
         MlDsaParameters other = (MlDsaParameters)o;
         return other.getMlDsaInstance() == this.getMlDsaInstance() && other.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(MlDsaParameters.class, this.mlDsaInstance, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != MlDsaParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "ML-DSA Parameters (ML-DSA instance: " + this.mlDsaInstance + ", variant: " + this.variant + ")";
   }

   @Immutable
   public static final class MlDsaInstance {
      public static final MlDsaParameters.MlDsaInstance ML_DSA_65 = new MlDsaParameters.MlDsaInstance("ML_DSA_65");
      public static final MlDsaParameters.MlDsaInstance ML_DSA_87 = new MlDsaParameters.MlDsaInstance("ML_DSA_87");
      private final String name;

      private MlDsaInstance(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   @Immutable
   public static final class Variant {
      public static final MlDsaParameters.Variant TINK = new MlDsaParameters.Variant("TINK");
      public static final MlDsaParameters.Variant NO_PREFIX = new MlDsaParameters.Variant("NO_PREFIX");
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
