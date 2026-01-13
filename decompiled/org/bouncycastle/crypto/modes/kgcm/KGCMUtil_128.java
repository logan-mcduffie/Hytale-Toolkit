package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_128 {
   public static final int SIZE = 2;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
   }

   public static void copy(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
   }

   public static boolean equal(long[] var0, long[] var1) {
      long var2 = 0L;
      var2 |= var0[0] ^ var1[0];
      var2 |= var0[1] ^ var1[1];
      return var2 == 0L;
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var0[0];
      long var5 = var0[1];
      long var7 = var1[0];
      long var9 = var1[1];
      long var11 = 0L;
      long var13 = 0L;
      long var15 = 0L;

      for (int var17 = 0; var17 < 64; var17++) {
         long var18 = -(var3 & 1L);
         var3 >>>= 1;
         var11 ^= var7 & var18;
         var13 ^= var9 & var18;
         long var20 = -(var5 & 1L);
         var5 >>>= 1;
         var13 ^= var7 & var20;
         var15 ^= var9 & var20;
         long var22 = var9 >> 63;
         var9 = var9 << 1 | var7 >>> 63;
         var7 = var7 << 1 ^ var22 & 135L;
      }

      var11 ^= var15 ^ var15 << 1 ^ var15 << 2 ^ var15 << 7;
      var13 ^= var15 >>> 63 ^ var15 >>> 62 ^ var15 >>> 57;
      var2[0] = var11;
      var2[1] = var13;
   }

   public static void multiplyX(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 >> 63;
      var1[0] = var2 << 1 ^ var6 & 135L;
      var1[1] = var4 << 1 | var2 >>> 63;
   }

   public static void multiplyX8(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 >>> 56;
      var1[0] = var2 << 8 ^ var6 ^ var6 << 1 ^ var6 << 2 ^ var6 << 7;
      var1[1] = var4 << 8 | var2 >>> 56;
   }

   public static void one(long[] var0) {
      var0[0] = 1L;
      var0[1] = 0L;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = new long[4];
      Interleave.expand64To128(var0[0], var2, 0);
      Interleave.expand64To128(var0[1], var2, 2);
      long var3 = var2[0];
      long var5 = var2[1];
      long var7 = var2[2];
      long var9 = var2[3];
      var5 ^= var9 ^ var9 << 1 ^ var9 << 2 ^ var9 << 7;
      var7 ^= var9 >>> 63 ^ var9 >>> 62 ^ var9 >>> 57;
      var3 ^= var7 ^ var7 << 1 ^ var7 << 2 ^ var7 << 7;
      var5 ^= var7 >>> 63 ^ var7 >>> 62 ^ var7 >>> 57;
      var1[0] = var3;
      var1[1] = var5;
   }

   public static void x(long[] var0) {
      var0[0] = 2L;
      var0[1] = 0L;
   }

   public static void zero(long[] var0) {
      var0[0] = 0L;
      var0[1] = 0L;
   }
}
