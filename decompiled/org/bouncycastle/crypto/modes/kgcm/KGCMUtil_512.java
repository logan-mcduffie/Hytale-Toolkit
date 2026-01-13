package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_512 {
   public static final int SIZE = 8;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
      var2[4] = var0[4] ^ var1[4];
      var2[5] = var0[5] ^ var1[5];
      var2[6] = var0[6] ^ var1[6];
      var2[7] = var0[7] ^ var1[7];
   }

   public static void copy(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
      var1[5] = var0[5];
      var1[6] = var0[6];
      var1[7] = var0[7];
   }

   public static boolean equal(long[] var0, long[] var1) {
      long var2 = 0L;
      var2 |= var0[0] ^ var1[0];
      var2 |= var0[1] ^ var1[1];
      var2 |= var0[2] ^ var1[2];
      var2 |= var0[3] ^ var1[3];
      var2 |= var0[4] ^ var1[4];
      var2 |= var0[5] ^ var1[5];
      var2 |= var0[6] ^ var1[6];
      var2 |= var0[7] ^ var1[7];
      return var2 == 0L;
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var1[0];
      long var5 = var1[1];
      long var7 = var1[2];
      long var9 = var1[3];
      long var11 = var1[4];
      long var13 = var1[5];
      long var15 = var1[6];
      long var17 = var1[7];
      long var19 = 0L;
      long var21 = 0L;
      long var23 = 0L;
      long var25 = 0L;
      long var27 = 0L;
      long var29 = 0L;
      long var31 = 0L;
      long var33 = 0L;
      long var35 = 0L;

      for (byte var37 = 0; var37 < 8; var37 += 2) {
         long var38 = var0[var37];
         long var40 = var0[var37 + 1];

         for (int var42 = 0; var42 < 64; var42++) {
            long var43 = -(var38 & 1L);
            var38 >>>= 1;
            var19 ^= var3 & var43;
            var21 ^= var5 & var43;
            var23 ^= var7 & var43;
            var25 ^= var9 & var43;
            var27 ^= var11 & var43;
            var29 ^= var13 & var43;
            var31 ^= var15 & var43;
            var33 ^= var17 & var43;
            long var45 = -(var40 & 1L);
            var40 >>>= 1;
            var21 ^= var3 & var45;
            var23 ^= var5 & var45;
            var25 ^= var7 & var45;
            var27 ^= var9 & var45;
            var29 ^= var11 & var45;
            var31 ^= var13 & var45;
            var33 ^= var15 & var45;
            var35 ^= var17 & var45;
            long var47 = var17 >> 63;
            var17 = var17 << 1 | var15 >>> 63;
            var15 = var15 << 1 | var13 >>> 63;
            var13 = var13 << 1 | var11 >>> 63;
            var11 = var11 << 1 | var9 >>> 63;
            var9 = var9 << 1 | var7 >>> 63;
            var7 = var7 << 1 | var5 >>> 63;
            var5 = var5 << 1 | var3 >>> 63;
            var3 = var3 << 1 ^ var47 & 293L;
         }

         long var58 = var17;
         var17 = var15;
         var15 = var13;
         var13 = var11;
         var11 = var9;
         var9 = var7;
         var7 = var5;
         var5 = var3 ^ var58 >>> 62 ^ var58 >>> 59 ^ var58 >>> 56;
         var3 = var58 ^ var58 << 2 ^ var58 << 5 ^ var58 << 8;
      }

      var19 ^= var35 ^ var35 << 2 ^ var35 << 5 ^ var35 << 8;
      var21 ^= var35 >>> 62 ^ var35 >>> 59 ^ var35 >>> 56;
      var2[0] = var19;
      var2[1] = var21;
      var2[2] = var23;
      var2[3] = var25;
      var2[4] = var27;
      var2[5] = var29;
      var2[6] = var31;
      var2[7] = var33;
   }

   public static void multiplyX(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var0[4];
      long var12 = var0[5];
      long var14 = var0[6];
      long var16 = var0[7];
      long var18 = var16 >> 63;
      var1[0] = var2 << 1 ^ var18 & 293L;
      var1[1] = var4 << 1 | var2 >>> 63;
      var1[2] = var6 << 1 | var4 >>> 63;
      var1[3] = var8 << 1 | var6 >>> 63;
      var1[4] = var10 << 1 | var8 >>> 63;
      var1[5] = var12 << 1 | var10 >>> 63;
      var1[6] = var14 << 1 | var12 >>> 63;
      var1[7] = var16 << 1 | var14 >>> 63;
   }

   public static void multiplyX8(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var0[4];
      long var12 = var0[5];
      long var14 = var0[6];
      long var16 = var0[7];
      long var18 = var16 >>> 56;
      var1[0] = var2 << 8 ^ var18 ^ var18 << 2 ^ var18 << 5 ^ var18 << 8;
      var1[1] = var4 << 8 | var2 >>> 56;
      var1[2] = var6 << 8 | var4 >>> 56;
      var1[3] = var8 << 8 | var6 >>> 56;
      var1[4] = var10 << 8 | var8 >>> 56;
      var1[5] = var12 << 8 | var10 >>> 56;
      var1[6] = var14 << 8 | var12 >>> 56;
      var1[7] = var16 << 8 | var14 >>> 56;
   }

   public static void one(long[] var0) {
      var0[0] = 1L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
      var0[4] = 0L;
      var0[5] = 0L;
      var0[6] = 0L;
      var0[7] = 0L;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = new long[16];

      for (int var3 = 0; var3 < 8; var3++) {
         Interleave.expand64To128(var0[var3], var2, var3 << 1);
      }

      int var6 = 16;

      while (--var6 >= 8) {
         long var4 = var2[var6];
         var2[var6 - 8] = var2[var6 - 8] ^ var4 ^ var4 << 2 ^ var4 << 5 ^ var4 << 8;
         var2[var6 - 8 + 1] = var2[var6 - 8 + 1] ^ var4 >>> 62 ^ var4 >>> 59 ^ var4 >>> 56;
      }

      copy(var2, var1);
   }

   public static void x(long[] var0) {
      var0[0] = 2L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
      var0[4] = 0L;
      var0[5] = 0L;
      var0[6] = 0L;
      var0[7] = 0L;
   }

   public static void zero(long[] var0) {
      var0[0] = 0L;
      var0[1] = 0L;
      var0[2] = 0L;
      var0[3] = 0L;
      var0[4] = 0L;
      var0[5] = 0L;
      var0[6] = 0L;
      var0[7] = 0L;
   }
}
