package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.util.Integers;

abstract class ScalarUtil {
   private static final long M = 4294967295L;

   static void addShifted_NP(int var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5) {
      long var6 = 0L;
      long var8 = 0L;
      if (var1 == 0) {
         for (int var10 = 0; var10 <= var0; var10++) {
            int var11 = var4[var10];
            var8 += var2[var10] & 4294967295L;
            var8 += var11 & 4294967295L;
            var6 += var11 & 4294967295L;
            var6 += var3[var10] & 4294967295L;
            var11 = (int)var6;
            var6 >>>= 32;
            var4[var10] = var11;
            var8 += var11 & 4294967295L;
            var2[var10] = (int)var8;
            var8 >>>= 32;
         }
      } else if (var1 < 32) {
         int var42 = 0;
         int var45 = 0;
         int var12 = 0;

         for (int var13 = 0; var13 <= var0; var13++) {
            int var14 = var4[var13];
            int var15 = var14 << var1 | var42 >>> -var1;
            var42 = var14;
            var8 += var2[var13] & 4294967295L;
            var8 += var15 & 4294967295L;
            int var16 = var3[var13];
            int var17 = var16 << var1 | var12 >>> -var1;
            var12 = var16;
            var6 += var14 & 4294967295L;
            var6 += var17 & 4294967295L;
            var14 = (int)var6;
            var6 >>>= 32;
            var4[var13] = var14;
            int var18 = var14 << var1 | var45 >>> -var1;
            var45 = var14;
            var8 += var18 & 4294967295L;
            var2[var13] = (int)var8;
            var8 >>>= 32;
         }
      } else {
         System.arraycopy(var4, 0, var5, 0, var0);
         int var43 = var1 >>> 5;
         int var46 = var1 & 31;
         if (var46 == 0) {
            for (int var47 = var43; var47 <= var0; var47++) {
               var8 += var2[var47] & 4294967295L;
               var8 += var5[var47 - var43] & 4294967295L;
               var6 += var4[var47] & 4294967295L;
               var6 += var3[var47 - var43] & 4294967295L;
               var4[var47] = (int)var6;
               var6 >>>= 32;
               var8 += var4[var47 - var43] & 4294967295L;
               var2[var47] = (int)var8;
               var8 >>>= 32;
            }
         } else {
            int var48 = 0;
            int var49 = 0;
            int var51 = 0;

            for (int var52 = var43; var52 <= var0; var52++) {
               int var53 = var5[var52 - var43];
               int var54 = var53 << var46 | var48 >>> -var46;
               var48 = var53;
               var8 += var2[var52] & 4294967295L;
               var8 += var54 & 4294967295L;
               int var55 = var3[var52 - var43];
               int var19 = var55 << var46 | var51 >>> -var46;
               var51 = var55;
               var6 += var4[var52] & 4294967295L;
               var6 += var19 & 4294967295L;
               var4[var52] = (int)var6;
               var6 >>>= 32;
               int var20 = var4[var52 - var43];
               int var21 = var20 << var46 | var49 >>> -var46;
               var49 = var20;
               var8 += var21 & 4294967295L;
               var2[var52] = (int)var8;
               var8 >>>= 32;
            }
         }
      }
   }

   static void addShifted_UV(int var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5) {
      int var6 = var1 >>> 5;
      int var7 = var1 & 31;
      long var8 = 0L;
      long var10 = 0L;
      if (var7 == 0) {
         for (int var12 = var6; var12 <= var0; var12++) {
            var8 += var2[var12] & 4294967295L;
            var10 += var3[var12] & 4294967295L;
            var8 += var4[var12 - var6] & 4294967295L;
            var10 += var5[var12 - var6] & 4294967295L;
            var2[var12] = (int)var8;
            var8 >>>= 32;
            var3[var12] = (int)var10;
            var10 >>>= 32;
         }
      } else {
         int var27 = 0;
         int var13 = 0;

         for (int var14 = var6; var14 <= var0; var14++) {
            int var15 = var4[var14 - var6];
            int var16 = var5[var14 - var6];
            int var17 = var15 << var7 | var27 >>> -var7;
            int var18 = var16 << var7 | var13 >>> -var7;
            var27 = var15;
            var13 = var16;
            var8 += var2[var14] & 4294967295L;
            var10 += var3[var14] & 4294967295L;
            var8 += var17 & 4294967295L;
            var10 += var18 & 4294967295L;
            var2[var14] = (int)var8;
            var8 >>>= 32;
            var3[var14] = (int)var10;
            var10 >>>= 32;
         }
      }
   }

   static int getBitLength(int var0, int[] var1) {
      int var2 = var0;
      int var3 = var1[var0] >> 31;

      while (var2 > 0 && var1[var2] == var3) {
         var2--;
      }

      return var2 * 32 + 32 - Integers.numberOfLeadingZeros(var1[var2] ^ var3);
   }

   static int getBitLengthPositive(int var0, int[] var1) {
      int var2 = var0;

      while (var2 > 0 && var1[var2] == 0) {
         var2--;
      }

      return var2 * 32 + 32 - Integers.numberOfLeadingZeros(var1[var2]);
   }

   static boolean lessThan(int var0, int[] var1, int[] var2) {
      int var3 = var0;

      do {
         int var4 = var1[var3] + Integer.MIN_VALUE;
         int var5 = var2[var3] + Integer.MIN_VALUE;
         if (var4 < var5) {
            return true;
         }

         if (var4 > var5) {
            return false;
         }
      } while (--var3 >= 0);

      return false;
   }

   static void subShifted_NP(int var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5) {
      long var6 = 0L;
      long var8 = 0L;
      if (var1 == 0) {
         for (int var10 = 0; var10 <= var0; var10++) {
            int var11 = var4[var10];
            var8 += var2[var10] & 4294967295L;
            var8 -= var11 & 4294967295L;
            var6 += var11 & 4294967295L;
            var6 -= var3[var10] & 4294967295L;
            var11 = (int)var6;
            var6 >>= 32;
            var4[var10] = var11;
            var8 -= var11 & 4294967295L;
            var2[var10] = (int)var8;
            var8 >>= 32;
         }
      } else if (var1 < 32) {
         int var42 = 0;
         int var45 = 0;
         int var12 = 0;

         for (int var13 = 0; var13 <= var0; var13++) {
            int var14 = var4[var13];
            int var15 = var14 << var1 | var42 >>> -var1;
            var42 = var14;
            var8 += var2[var13] & 4294967295L;
            var8 -= var15 & 4294967295L;
            int var16 = var3[var13];
            int var17 = var16 << var1 | var12 >>> -var1;
            var12 = var16;
            var6 += var14 & 4294967295L;
            var6 -= var17 & 4294967295L;
            var14 = (int)var6;
            var6 >>= 32;
            var4[var13] = var14;
            int var18 = var14 << var1 | var45 >>> -var1;
            var45 = var14;
            var8 -= var18 & 4294967295L;
            var2[var13] = (int)var8;
            var8 >>= 32;
         }
      } else {
         System.arraycopy(var4, 0, var5, 0, var0);
         int var43 = var1 >>> 5;
         int var46 = var1 & 31;
         if (var46 == 0) {
            for (int var47 = var43; var47 <= var0; var47++) {
               var8 += var2[var47] & 4294967295L;
               var8 -= var5[var47 - var43] & 4294967295L;
               var6 += var4[var47] & 4294967295L;
               var6 -= var3[var47 - var43] & 4294967295L;
               var4[var47] = (int)var6;
               var6 >>= 32;
               var8 -= var4[var47 - var43] & 4294967295L;
               var2[var47] = (int)var8;
               var8 >>= 32;
            }
         } else {
            int var48 = 0;
            int var49 = 0;
            int var51 = 0;

            for (int var52 = var43; var52 <= var0; var52++) {
               int var53 = var5[var52 - var43];
               int var54 = var53 << var46 | var48 >>> -var46;
               var48 = var53;
               var8 += var2[var52] & 4294967295L;
               var8 -= var54 & 4294967295L;
               int var55 = var3[var52 - var43];
               int var19 = var55 << var46 | var51 >>> -var46;
               var51 = var55;
               var6 += var4[var52] & 4294967295L;
               var6 -= var19 & 4294967295L;
               var4[var52] = (int)var6;
               var6 >>= 32;
               int var20 = var4[var52 - var43];
               int var21 = var20 << var46 | var49 >>> -var46;
               var49 = var20;
               var8 -= var21 & 4294967295L;
               var2[var52] = (int)var8;
               var8 >>= 32;
            }
         }
      }
   }

   static void subShifted_UV(int var0, int var1, int[] var2, int[] var3, int[] var4, int[] var5) {
      int var6 = var1 >>> 5;
      int var7 = var1 & 31;
      long var8 = 0L;
      long var10 = 0L;
      if (var7 == 0) {
         for (int var12 = var6; var12 <= var0; var12++) {
            var8 += var2[var12] & 4294967295L;
            var10 += var3[var12] & 4294967295L;
            var8 -= var4[var12 - var6] & 4294967295L;
            var10 -= var5[var12 - var6] & 4294967295L;
            var2[var12] = (int)var8;
            var8 >>= 32;
            var3[var12] = (int)var10;
            var10 >>= 32;
         }
      } else {
         int var27 = 0;
         int var13 = 0;

         for (int var14 = var6; var14 <= var0; var14++) {
            int var15 = var4[var14 - var6];
            int var16 = var5[var14 - var6];
            int var17 = var15 << var7 | var27 >>> -var7;
            int var18 = var16 << var7 | var13 >>> -var7;
            var27 = var15;
            var13 = var16;
            var8 += var2[var14] & 4294967295L;
            var10 += var3[var14] & 4294967295L;
            var8 -= var17 & 4294967295L;
            var10 -= var18 & 4294967295L;
            var2[var14] = (int)var8;
            var8 >>= 32;
            var3[var14] = (int)var10;
            var10 >>= 32;
         }
      }
   }
}
