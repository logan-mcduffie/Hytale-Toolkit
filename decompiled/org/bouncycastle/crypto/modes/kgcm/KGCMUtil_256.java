package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_256 {
   public static final int SIZE = 4;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
   }

   public static void copy(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
   }

   public static boolean equal(long[] var0, long[] var1) {
      long var2 = 0L;
      var2 |= var0[0] ^ var1[0];
      var2 |= var0[1] ^ var1[1];
      var2 |= var0[2] ^ var1[2];
      var2 |= var0[3] ^ var1[3];
      return var2 == 0L;
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var0[0];
      long var5 = var0[1];
      long var7 = var0[2];
      long var9 = var0[3];
      long var11 = var1[0];
      long var13 = var1[1];
      long var15 = var1[2];
      long var17 = var1[3];
      long var19 = 0L;
      long var21 = 0L;
      long var23 = 0L;
      long var25 = 0L;
      long var27 = 0L;

      for (int var29 = 0; var29 < 64; var29++) {
         long var30 = -(var3 & 1L);
         var3 >>>= 1;
         var19 ^= var11 & var30;
         var21 ^= var13 & var30;
         var23 ^= var15 & var30;
         var25 ^= var17 & var30;
         long var32 = -(var5 & 1L);
         var5 >>>= 1;
         var21 ^= var11 & var32;
         var23 ^= var13 & var32;
         var25 ^= var15 & var32;
         var27 ^= var17 & var32;
         long var34 = var17 >> 63;
         var17 = var17 << 1 | var15 >>> 63;
         var15 = var15 << 1 | var13 >>> 63;
         var13 = var13 << 1 | var11 >>> 63;
         var11 = var11 << 1 ^ var34 & 1061L;
      }

      long var41 = var15;
      var15 = var13;
      var13 = var11 ^ var17 >>> 62 ^ var17 >>> 59 ^ var17 >>> 54;
      var11 = var17 ^ var17 << 2 ^ var17 << 5 ^ var17 << 10;

      for (int var31 = 0; var31 < 64; var31++) {
         long var51 = -(var7 & 1L);
         var7 >>>= 1;
         var19 ^= var11 & var51;
         var21 ^= var13 & var51;
         var23 ^= var15 & var51;
         var25 ^= var41 & var51;
         long var52 = -(var9 & 1L);
         var9 >>>= 1;
         var21 ^= var11 & var52;
         var23 ^= var13 & var52;
         var25 ^= var15 & var52;
         var27 ^= var41 & var52;
         long var36 = var41 >> 63;
         var41 = var41 << 1 | var15 >>> 63;
         var15 = var15 << 1 | var13 >>> 63;
         var13 = var13 << 1 | var11 >>> 63;
         var11 = var11 << 1 ^ var36 & 1061L;
      }

      var19 ^= var27 ^ var27 << 2 ^ var27 << 5 ^ var27 << 10;
      var21 ^= var27 >>> 62 ^ var27 >>> 59 ^ var27 >>> 54;
      var2[0] = var19;
      var2[1] = var21;
      var2[2] = var23;
      var2[3] = var25;
   }

   public static void multiplyX(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var8 >> 63;
      var1[0] = var2 << 1 ^ var10 & 1061L;
      var1[1] = var4 << 1 | var2 >>> 63;
      var1[2] = var6 << 1 | var4 >>> 63;
      var1[3] = var8 << 1 | var6 >>> 63;
   }

   public static void multiplyX8(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var8 >>> 56;
      var1[0] = var2 << 8 ^ var10 ^ var10 << 2 ^ var10 << 5 ^ var10 << 10;
      var1[1] = var4 << 8 | var2 >>> 56;
      var1[2] = var6 << 8 | var4 >>> 56;
      var1[3] = var8 << 8 | var6 >>> 56;
   }

   public static void one(long[] var0) {
      var0[0] = 1L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = new long[8];

      for (int var3 = 0; var3 < 4; var3++) {
         Interleave.expand64To128(var0[var3], var2, var3 << 1);
      }

      int var6 = 8;

      while (--var6 >= 4) {
         long var4 = var2[var6];
         var2[var6 - 4] = var2[var6 - 4] ^ var4 ^ var4 << 2 ^ var4 << 5 ^ var4 << 10;
         var2[var6 - 4 + 1] = var2[var6 - 4 + 1] ^ var4 >>> 62 ^ var4 >>> 59 ^ var4 >>> 54;
      }

      copy(var2, var1);
   }

   public static void x(long[] var0) {
      var0[0] = 2L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
   }

   public static void zero(long[] var0) {
      var0[0] = 0L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
   }
}
