package com.google.crypto.tink.aead;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class XChaCha20Poly1305Parameters extends AeadParameters {
   private final XChaCha20Poly1305Parameters.Variant variant;

   public static XChaCha20Poly1305Parameters create() {
      return new XChaCha20Poly1305Parameters(XChaCha20Poly1305Parameters.Variant.NO_PREFIX);
   }

   public static XChaCha20Poly1305Parameters create(XChaCha20Poly1305Parameters.Variant variant) {
      return new XChaCha20Poly1305Parameters(variant);
   }

   private XChaCha20Poly1305Parameters(XChaCha20Poly1305Parameters.Variant variant) {
      this.variant = variant;
   }

   public XChaCha20Poly1305Parameters.Variant getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof XChaCha20Poly1305Parameters)) {
         return false;
      } else {
         XChaCha20Poly1305Parameters that = (XChaCha20Poly1305Parameters)o;
         return that.getVariant() == this.getVariant();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(XChaCha20Poly1305Parameters.class, this.variant);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != XChaCha20Poly1305Parameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "XChaCha20Poly1305 Parameters (variant: " + this.variant + ")";
   }

   @Immutable
   public static final class Variant {
      public static final XChaCha20Poly1305Parameters.Variant TINK = new XChaCha20Poly1305Parameters.Variant("TINK");
      public static final XChaCha20Poly1305Parameters.Variant CRUNCHY = new XChaCha20Poly1305Parameters.Variant("CRUNCHY");
      public static final XChaCha20Poly1305Parameters.Variant NO_PREFIX = new XChaCha20Poly1305Parameters.Variant("NO_PREFIX");
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
