package com.google.crypto.tink.aead;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;

public final class XAesGcmParameters extends AeadParameters {
   private final XAesGcmParameters.Variant variant;
   private final int saltSizeBytes;

   public static XAesGcmParameters create(XAesGcmParameters.Variant variant, int saltSizeBytes) throws GeneralSecurityException {
      if (saltSizeBytes >= 8 && saltSizeBytes <= 12) {
         return new XAesGcmParameters(variant, saltSizeBytes);
      } else {
         throw new GeneralSecurityException("Salt size must be between 8 and 12 bytes");
      }
   }

   private XAesGcmParameters(XAesGcmParameters.Variant variant, int saltSizeBytes) {
      this.variant = variant;
      this.saltSizeBytes = saltSizeBytes;
   }

   public XAesGcmParameters.Variant getVariant() {
      return this.variant;
   }

   public int getSaltSizeBytes() {
      return this.saltSizeBytes;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof XAesGcmParameters)) {
         return false;
      } else {
         XAesGcmParameters that = (XAesGcmParameters)o;
         return that.getVariant() == this.getVariant() && that.getSaltSizeBytes() == this.getSaltSizeBytes();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(XAesGcmParameters.class, this.variant, this.saltSizeBytes);
   }

   @Override
   public boolean hasIdRequirement() {
      return this.variant != XAesGcmParameters.Variant.NO_PREFIX;
   }

   @Override
   public String toString() {
      return "X-AES-GCM Parameters (variant: " + this.variant + "salt_size_bytes: " + this.saltSizeBytes + ")";
   }

   @Immutable
   public static final class Variant {
      public static final XAesGcmParameters.Variant TINK = new XAesGcmParameters.Variant("TINK");
      public static final XAesGcmParameters.Variant NO_PREFIX = new XAesGcmParameters.Variant("NO_PREFIX");
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
