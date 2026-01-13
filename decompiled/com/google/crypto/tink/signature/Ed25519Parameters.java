package com.google.crypto.tink.signature;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class Ed25519Parameters extends SignatureParameters {
   private final Ed25519Parameters.Variant variant;

   public static Ed25519Parameters create() {
      return new Ed25519Parameters(Ed25519Parameters.Variant.NO_PREFIX);
   }

   public static Ed25519Parameters create(Ed25519Parameters.Variant variant) {
      return new Ed25519Parameters(variant);
   }

   private Ed25519Parameters(Ed25519Parameters.Variant variant) {
      this.variant = variant;
   }

   public Ed25519Parameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Ed25519Parameters)) {
         return false;
      } else {
         Ed25519Parameters that = (Ed25519Parameters)o;
         return that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(Ed25519Parameters.class, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != Ed25519Parameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "Ed25519 Parameters (variant: " + this.variant + ")";
   }

   @Immutable
   public static final class Variant {
      public static final Ed25519Parameters.Variant TINK = new Ed25519Parameters.Variant("TINK");
      public static final Ed25519Parameters.Variant CRUNCHY = new Ed25519Parameters.Variant("CRUNCHY");
      public static final Ed25519Parameters.Variant LEGACY = new Ed25519Parameters.Variant("LEGACY");
      public static final Ed25519Parameters.Variant NO_PREFIX = new Ed25519Parameters.Variant("NO_PREFIX");
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
