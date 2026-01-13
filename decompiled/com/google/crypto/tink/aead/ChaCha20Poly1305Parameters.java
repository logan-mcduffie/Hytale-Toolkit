package com.google.crypto.tink.aead;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class ChaCha20Poly1305Parameters extends AeadParameters {
   private final ChaCha20Poly1305Parameters.Variant variant;

   public static ChaCha20Poly1305Parameters create() {
      return new ChaCha20Poly1305Parameters(ChaCha20Poly1305Parameters.Variant.NO_PREFIX);
   }

   public static ChaCha20Poly1305Parameters create(ChaCha20Poly1305Parameters.Variant variant) {
      return new ChaCha20Poly1305Parameters(variant);
   }

   private ChaCha20Poly1305Parameters(ChaCha20Poly1305Parameters.Variant variant) {
      this.variant = variant;
   }

   public ChaCha20Poly1305Parameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof ChaCha20Poly1305Parameters)) {
         return false;
      } else {
         ChaCha20Poly1305Parameters that = (ChaCha20Poly1305Parameters)o;
         return that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(ChaCha20Poly1305Parameters.class, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != ChaCha20Poly1305Parameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "ChaCha20Poly1305 Parameters (variant: " + this.variant + ")";
   }

   @Immutable
   public static final class Variant {
      public static final ChaCha20Poly1305Parameters.Variant TINK = new ChaCha20Poly1305Parameters.Variant("TINK");
      public static final ChaCha20Poly1305Parameters.Variant CRUNCHY = new ChaCha20Poly1305Parameters.Variant("CRUNCHY");
      public static final ChaCha20Poly1305Parameters.Variant NO_PREFIX = new ChaCha20Poly1305Parameters.Variant("NO_PREFIX");
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
