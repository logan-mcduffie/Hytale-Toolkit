package com.google.crypto.tink.subtle;

public final class Random {
   public static byte[] randBytes(int size) {
      return com.google.crypto.tink.internal.Random.randBytes(size);
   }

   public static final int randInt(int max) {
      return com.google.crypto.tink.internal.Random.randInt(max);
   }

   public static final int randInt() {
      return com.google.crypto.tink.internal.Random.randInt();
   }

   private Random() {
   }
}
