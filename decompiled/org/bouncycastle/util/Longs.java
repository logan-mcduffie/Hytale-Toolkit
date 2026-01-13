package org.bouncycastle.util;

public class Longs {
   public static final int BYTES = 8;
   public static final int SIZE = 64;

   public static long highestOneBit(long var0) {
      return Long.highestOneBit(var0);
   }

   public static long lowestOneBit(long var0) {
      return Long.lowestOneBit(var0);
   }

   public static int numberOfLeadingZeros(long var0) {
      return Long.numberOfLeadingZeros(var0);
   }

   public static int numberOfTrailingZeros(long var0) {
      return Long.numberOfTrailingZeros(var0);
   }

   public static long reverse(long var0) {
      return Long.reverse(var0);
   }

   public static long reverseBytes(long var0) {
      return Long.reverseBytes(var0);
   }

   public static long rotateLeft(long var0, int var2) {
      return Long.rotateLeft(var0, var2);
   }

   public static long rotateRight(long var0, int var2) {
      return Long.rotateRight(var0, var2);
   }

   public static Long valueOf(long var0) {
      return var0;
   }

   public static void xorTo(int var0, long[] var1, int var2, long[] var3, int var4) {
      for (int var5 = 0; var5 < var0; var5++) {
         var3[var4 + var5] = var3[var4 + var5] ^ var1[var2 + var5];
      }
   }
}
