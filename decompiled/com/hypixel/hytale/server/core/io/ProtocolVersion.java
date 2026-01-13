package com.hypixel.hytale.server.core.io;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProtocolVersion {
   private final String hash;

   public ProtocolVersion(String hash) {
      this.hash = hash;
   }

   public String getHash() {
      return this.hash;
   }

   @Override
   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProtocolVersion that = (ProtocolVersion)o;
         return this.hash != null ? this.hash.equals(that.hash) : that.hash == null;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * (this.hash != null ? this.hash.hashCode() : 0);
   }

   @Nonnull
   @Override
   public String toString() {
      return "ProtocolVersion{hash='" + this.hash + "'}";
   }
}
