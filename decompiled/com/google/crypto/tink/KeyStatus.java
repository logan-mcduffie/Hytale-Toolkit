package com.google.crypto.tink;

import com.google.errorprone.annotations.Immutable;

@Immutable
public final class KeyStatus {
   public static final KeyStatus ENABLED = new KeyStatus("ENABLED");
   public static final KeyStatus DISABLED = new KeyStatus("DISABLED");
   public static final KeyStatus DESTROYED = new KeyStatus("DESTROYED");
   private final String name;

   private KeyStatus(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
