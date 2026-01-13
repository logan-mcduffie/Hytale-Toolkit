package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat192 {
   private static final long M = 4294967295L;

   public static int add(int[] var0, int[] var1, int[] var2) {
      long var3 = 0L;
      var3 += (var0[0] & 4294967295L) + (var1[0] & 4294967295L);
      var2[0] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[1] & 4294967295L) + (var1[1] & 4294967295L);
      var2[1] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[2] & 4294967295L) + (var1[2] & 4294967295L);
      var2[2] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[3] & 4294967295L) + (var1[3] & 4294967295L);
      var2[3] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[4] & 4294967295L) + (var1[4] & 4294967295L);
      var2[4] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[5] & 4294967295L) + (var1[5] & 4294967295L);
      var2[5] = (int)var3;
      var3 >>>= 32;
      return (int)var3;
   }

   public static int addBothTo(int[] var0, int[] var1, int[] var2) {
      long var3 = 0L;
      var3 += (var0[0] & 4294967295L) + (var1[0] & 4294967295L) + (var2[0] & 4294967295L);
      var2[0] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[1] & 4294967295L) + (var1[1] & 4294967295L) + (var2[1] & 4294967295L);
      var2[1] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[2] & 4294967295L) + (var1[2] & 4294967295L) + (var2[2] & 4294967295L);
      var2[2] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[3] & 4294967295L) + (var1[3] & 4294967295L) + (var2[3] & 4294967295L);
      var2[3] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[4] & 4294967295L) + (var1[4] & 4294967295L) + (var2[4] & 4294967295L);
      var2[4] = (int)var3;
      var3 >>>= 32;
      var3 += (var0[5] & 4294967295L) + (var1[5] & 4294967295L) + (var2[5] & 4294967295L);
      var2[5] = (int)var3;
      var3 >>>= 32;
      return (int)var3;
   }

   public static int addTo(int[] var0, int[] var1) {
      long var2 = 0L;
      var2 += (var0[0] & 4294967295L) + (var1[0] & 4294967295L);
      var1[0] = (int)var2;
      var2 >>>= 32;
      var2 += (var0[1] & 4294967295L) + (var1[1] & 4294967295L);
      var1[1] = (int)var2;
      var2 >>>= 32;
      var2 += (var0[2] & 4294967295L) + (var1[2] & 4294967295L);
      var1[2] = (int)var2;
      var2 >>>= 32;
      var2 += (var0[3] & 4294967295L) + (var1[3] & 4294967295L);
      var1[3] = (int)var2;
      var2 >>>= 32;
      var2 += (var0[4] & 4294967295L) + (var1[4] & 4294967295L);
      var1[4] = (int)var2;
      var2 >>>= 32;
      var2 += (var0[5] & 4294967295L) + (var1[5] & 4294967295L);
      var1[5] = (int)var2;
      var2 >>>= 32;
      return (int)var2;
   }

   public static int addTo(int[] var0, int var1, int[] var2, int var3, int var4) {
      long var5 = var4 & 4294967295L;
      var5 += (var0[var1 + 0] & 4294967295L) + (var2[var3 + 0] & 4294967295L);
      var2[var3 + 0] = (int)var5;
      var5 >>>= 32;
      var5 += (var0[var1 + 1] & 4294967295L) + (var2[var3 + 1] & 4294967295L);
      var2[var3 + 1] = (int)var5;
      var5 >>>= 32;
      var5 += (var0[var1 + 2] & 4294967295L) + (var2[var3 + 2] & 4294967295L);
      var2[var3 + 2] = (int)var5;
      var5 >>>= 32;
      var5 += (var0[var1 + 3] & 4294967295L) + (var2[var3 + 3] & 4294967295L);
      var2[var3 + 3] = (int)var5;
      var5 >>>= 32;
      var5 += (var0[var1 + 4] & 4294967295L) + (var2[var3 + 4] & 4294967295L);
      var2[var3 + 4] = (int)var5;
      var5 >>>= 32;
      var5 += (var0[var1 + 5] & 4294967295L) + (var2[var3 + 5] & 4294967295L);
      var2[var3 + 5] = (int)var5;
      var5 >>>= 32;
      return (int)var5;
   }

   public static int addToEachOther(int[] var0, int var1, int[] var2, int var3) {
      long var4 = 0L;
      var4 += (var0[var1 + 0] & 4294967295L) + (var2[var3 + 0] & 4294967295L);
      var0[var1 + 0] = (int)var4;
      var2[var3 + 0] = (int)var4;
      var4 >>>= 32;
      var4 += (var0[var1 + 1] & 4294967295L) + (var2[var3 + 1] & 4294967295L);
      var0[var1 + 1] = (int)var4;
      var2[var3 + 1] = (int)var4;
      var4 >>>= 32;
      var4 += (var0[var1 + 2] & 4294967295L) + (var2[var3 + 2] & 4294967295L);
      var0[var1 + 2] = (int)var4;
      var2[var3 + 2] = (int)var4;
      var4 >>>= 32;
      var4 += (var0[var1 + 3] & 4294967295L) + (var2[var3 + 3] & 4294967295L);
      var0[var1 + 3] = (int)var4;
      var2[var3 + 3] = (int)var4;
      var4 >>>= 32;
      var4 += (var0[var1 + 4] & 4294967295L) + (var2[var3 + 4] & 4294967295L);
      var0[var1 + 4] = (int)var4;
      var2[var3 + 4] = (int)var4;
      var4 >>>= 32;
      var4 += (var0[var1 + 5] & 4294967295L) + (var2[var3 + 5] & 4294967295L);
      var0[var1 + 5] = (int)var4;
      var2[var3 + 5] = (int)var4;
      var4 >>>= 32;
      return (int)var4;
   }

   public static void copy(int[] var0, int[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
      var1[5] = var0[5];
   }

   public static void copy(int[] var0, int var1, int[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
      var2[var3 + 2] = var0[var1 + 2];
      var2[var3 + 3] = var0[var1 + 3];
      var2[var3 + 4] = var0[var1 + 4];
      var2[var3 + 5] = var0[var1 + 5];
   }

   public static void copy64(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
   }

   public static void copy64(long[] var0, int var1, long[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
      var2[var3 + 2] = var0[var1 + 2];
   }

   public static int[] create() {
      return new int[6];
   }

   public static long[] create64() {
      return new long[3];
   }

   public static int[] createExt() {
      return new int[12];
   }

   public static long[] createExt64() {
      return new long[6];
   }

   public static boolean diff(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      boolean var6 = gte(var0, var1, var2, var3);
      if (var6) {
         sub(var0, var1, var2, var3, var4, var5);
      } else {
         sub(var2, var3, var0, var1, var4, var5);
      }

      return var6;
   }

   public static boolean eq(int[] var0, int[] var1) {
      for (int var2 = 5; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static boolean eq64(long[] var0, long[] var1) {
      for (int var2 = 2; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static int[] fromBigInteger(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 192) {
         int[] var1 = create();

         for (int var2 = 0; var2 < 6; var2++) {
            var1[var2] = var0.intValue();
            var0 = var0.shiftRight(32);
         }

         return var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long[] fromBigInteger64(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 192) {
         long[] var1 = create64();

         for (int var2 = 0; var2 < 3; var2++) {
            var1[var2] = var0.longValue();
            var0 = var0.shiftRight(64);
         }

         return var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int getBit(int[] var0, int var1) {
      if (var1 == 0) {
         return var0[0] & 1;
      } else {
         int var2 = var1 >> 5;
         if (var2 >= 0 && var2 < 6) {
            int var3 = var1 & 31;
            return var0[var2] >>> var3 & 1;
         } else {
            return 0;
         }
      }
   }

   public static boolean gte(int[] var0, int[] var1) {
      for (int var2 = 5; var2 >= 0; var2--) {
         int var3 = var0[var2] ^ Integer.MIN_VALUE;
         int var4 = var1[var2] ^ Integer.MIN_VALUE;
         if (var3 < var4) {
            return false;
         }

         if (var3 > var4) {
            return true;
         }
      }

      return true;
   }

   public static boolean gte(int[] var0, int var1, int[] var2, int var3) {
      for (int var4 = 5; var4 >= 0; var4--) {
         int var5 = var0[var1 + var4] ^ Integer.MIN_VALUE;
         int var6 = var2[var3 + var4] ^ Integer.MIN_VALUE;
         if (var5 < var6) {
            return false;
         }

         if (var5 > var6) {
            return true;
         }
      }

      return true;
   }

   public static boolean isOne(int[] var0) {
      if (var0[0] != 1) {
         return false;
      } else {
         for (int var1 = 1; var1 < 6; var1++) {
            if (var0[var1] != 0) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isOne64(long[] var0) {
      if (var0[0] != 1L) {
         return false;
      } else {
         for (int var1 = 1; var1 < 3; var1++) {
            if (var0[var1] != 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isZero(int[] var0) {
      for (int var1 = 0; var1 < 6; var1++) {
         if (var0[var1] != 0) {
            return false;
         }
      }

      return true;
   }

   public static boolean isZero64(long[] var0) {
      for (int var1 = 0; var1 < 3; var1++) {
         if (var0[var1] != 0L) {
            return false;
         }
      }

      return true;
   }

   public static void mul(int[] var0, int[] var1, int[] var2) {
      long var3 = var1[0] & 4294967295L;
      long var5 = var1[1] & 4294967295L;
      long var7 = var1[2] & 4294967295L;
      long var9 = var1[3] & 4294967295L;
      long var11 = var1[4] & 4294967295L;
      long var13 = var1[5] & 4294967295L;
      long var15 = 0L;
      long var17 = var0[0] & 4294967295L;
      var15 += var17 * var3;
      var2[0] = (int)var15;
      var15 >>>= 32;
      var15 += var17 * var5;
      var2[1] = (int)var15;
      var15 >>>= 32;
      var15 += var17 * var7;
      var2[2] = (int)var15;
      var15 >>>= 32;
      var15 += var17 * var9;
      var2[3] = (int)var15;
      var15 >>>= 32;
      var15 += var17 * var11;
      var2[4] = (int)var15;
      var15 >>>= 32;
      var15 += var17 * var13;
      var2[5] = (int)var15;
      var15 >>>= 32;
      var2[6] = (int)var15;

      for (int var32 = 1; var32 < 6; var32++) {
         long var16 = 0L;
         long var18 = var0[var32] & 4294967295L;
         var16 += var18 * var3 + (var2[var32 + 0] & 4294967295L);
         var2[var32 + 0] = (int)var16;
         var16 >>>= 32;
         var16 += var18 * var5 + (var2[var32 + 1] & 4294967295L);
         var2[var32 + 1] = (int)var16;
         var16 >>>= 32;
         var16 += var18 * var7 + (var2[var32 + 2] & 4294967295L);
         var2[var32 + 2] = (int)var16;
         var16 >>>= 32;
         var16 += var18 * var9 + (var2[var32 + 3] & 4294967295L);
         var2[var32 + 3] = (int)var16;
         var16 >>>= 32;
         var16 += var18 * var11 + (var2[var32 + 4] & 4294967295L);
         var2[var32 + 4] = (int)var16;
         var16 >>>= 32;
         var16 += var18 * var13 + (var2[var32 + 5] & 4294967295L);
         var2[var32 + 5] = (int)var16;
         var16 >>>= 32;
         var2[var32 + 6] = (int)var16;
      }
   }

   public static void mul(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      long var6 = var2[var3 + 0] & 4294967295L;
      long var8 = var2[var3 + 1] & 4294967295L;
      long var10 = var2[var3 + 2] & 4294967295L;
      long var12 = var2[var3 + 3] & 4294967295L;
      long var14 = var2[var3 + 4] & 4294967295L;
      long var16 = var2[var3 + 5] & 4294967295L;
      long var18 = 0L;
      long var20 = var0[var1 + 0] & 4294967295L;
      var18 += var20 * var6;
      var4[var5 + 0] = (int)var18;
      var18 >>>= 32;
      var18 += var20 * var8;
      var4[var5 + 1] = (int)var18;
      var18 >>>= 32;
      var18 += var20 * var10;
      var4[var5 + 2] = (int)var18;
      var18 >>>= 32;
      var18 += var20 * var12;
      var4[var5 + 3] = (int)var18;
      var18 >>>= 32;
      var18 += var20 * var14;
      var4[var5 + 4] = (int)var18;
      var18 >>>= 32;
      var18 += var20 * var16;
      var4[var5 + 5] = (int)var18;
      var18 >>>= 32;
      var4[var5 + 6] = (int)var18;

      for (int var35 = 1; var35 < 6; var35++) {
         var5++;
         long var19 = 0L;
         long var21 = var0[var1 + var35] & 4294967295L;
         var19 += var21 * var6 + (var4[var5 + 0] & 4294967295L);
         var4[var5 + 0] = (int)var19;
         var19 >>>= 32;
         var19 += var21 * var8 + (var4[var5 + 1] & 4294967295L);
         var4[var5 + 1] = (int)var19;
         var19 >>>= 32;
         var19 += var21 * var10 + (var4[var5 + 2] & 4294967295L);
         var4[var5 + 2] = (int)var19;
         var19 >>>= 32;
         var19 += var21 * var12 + (var4[var5 + 3] & 4294967295L);
         var4[var5 + 3] = (int)var19;
         var19 >>>= 32;
         var19 += var21 * var14 + (var4[var5 + 4] & 4294967295L);
         var4[var5 + 4] = (int)var19;
         var19 >>>= 32;
         var19 += var21 * var16 + (var4[var5 + 5] & 4294967295L);
         var4[var5 + 5] = (int)var19;
         var19 >>>= 32;
         var4[var5 + 6] = (int)var19;
      }
   }

   public static int mulAddTo(int[] var0, int[] var1, int[] var2) {
      long var3 = var1[0] & 4294967295L;
      long var5 = var1[1] & 4294967295L;
      long var7 = var1[2] & 4294967295L;
      long var9 = var1[3] & 4294967295L;
      long var11 = var1[4] & 4294967295L;
      long var13 = var1[5] & 4294967295L;
      long var15 = 0L;

      for (int var17 = 0; var17 < 6; var17++) {
         long var18 = 0L;
         long var20 = var0[var17] & 4294967295L;
         var18 += var20 * var3 + (var2[var17 + 0] & 4294967295L);
         var2[var17 + 0] = (int)var18;
         var18 >>>= 32;
         var18 += var20 * var5 + (var2[var17 + 1] & 4294967295L);
         var2[var17 + 1] = (int)var18;
         var18 >>>= 32;
         var18 += var20 * var7 + (var2[var17 + 2] & 4294967295L);
         var2[var17 + 2] = (int)var18;
         var18 >>>= 32;
         var18 += var20 * var9 + (var2[var17 + 3] & 4294967295L);
         var2[var17 + 3] = (int)var18;
         var18 >>>= 32;
         var18 += var20 * var11 + (var2[var17 + 4] & 4294967295L);
         var2[var17 + 4] = (int)var18;
         var18 >>>= 32;
         var18 += var20 * var13 + (var2[var17 + 5] & 4294967295L);
         var2[var17 + 5] = (int)var18;
         var18 >>>= 32;
         var15 += var18 + (var2[var17 + 6] & 4294967295L);
         var2[var17 + 6] = (int)var15;
         var15 >>>= 32;
      }

      return (int)var15;
   }

   public static int mulAddTo(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      long var6 = var2[var3 + 0] & 4294967295L;
      long var8 = var2[var3 + 1] & 4294967295L;
      long var10 = var2[var3 + 2] & 4294967295L;
      long var12 = var2[var3 + 3] & 4294967295L;
      long var14 = var2[var3 + 4] & 4294967295L;
      long var16 = var2[var3 + 5] & 4294967295L;
      long var18 = 0L;

      for (int var20 = 0; var20 < 6; var20++) {
         long var21 = 0L;
         long var23 = var0[var1 + var20] & 4294967295L;
         var21 += var23 * var6 + (var4[var5 + 0] & 4294967295L);
         var4[var5 + 0] = (int)var21;
         var21 >>>= 32;
         var21 += var23 * var8 + (var4[var5 + 1] & 4294967295L);
         var4[var5 + 1] = (int)var21;
         var21 >>>= 32;
         var21 += var23 * var10 + (var4[var5 + 2] & 4294967295L);
         var4[var5 + 2] = (int)var21;
         var21 >>>= 32;
         var21 += var23 * var12 + (var4[var5 + 3] & 4294967295L);
         var4[var5 + 3] = (int)var21;
         var21 >>>= 32;
         var21 += var23 * var14 + (var4[var5 + 4] & 4294967295L);
         var4[var5 + 4] = (int)var21;
         var21 >>>= 32;
         var21 += var23 * var16 + (var4[var5 + 5] & 4294967295L);
         var4[var5 + 5] = (int)var21;
         var21 >>>= 32;
         var18 += var21 + (var4[var5 + 6] & 4294967295L);
         var4[var5 + 6] = (int)var18;
         var18 >>>= 32;
         var5++;
      }

      return (int)var18;
   }

   public static long mul33Add(int var0, int[] var1, int var2, int[] var3, int var4, int[] var5, int var6) {
      long var7 = 0L;
      long var9 = var0 & 4294967295L;
      long var11 = var1[var2 + 0] & 4294967295L;
      var7 += var9 * var11 + (var3[var4 + 0] & 4294967295L);
      var5[var6 + 0] = (int)var7;
      var7 >>>= 32;
      long var13 = var1[var2 + 1] & 4294967295L;
      var7 += var9 * var13 + var11 + (var3[var4 + 1] & 4294967295L);
      var5[var6 + 1] = (int)var7;
      var7 >>>= 32;
      long var15 = var1[var2 + 2] & 4294967295L;
      var7 += var9 * var15 + var13 + (var3[var4 + 2] & 4294967295L);
      var5[var6 + 2] = (int)var7;
      var7 >>>= 32;
      long var17 = var1[var2 + 3] & 4294967295L;
      var7 += var9 * var17 + var15 + (var3[var4 + 3] & 4294967295L);
      var5[var6 + 3] = (int)var7;
      var7 >>>= 32;
      long var19 = var1[var2 + 4] & 4294967295L;
      var7 += var9 * var19 + var17 + (var3[var4 + 4] & 4294967295L);
      var5[var6 + 4] = (int)var7;
      var7 >>>= 32;
      long var21 = var1[var2 + 5] & 4294967295L;
      var7 += var9 * var21 + var19 + (var3[var4 + 5] & 4294967295L);
      var5[var6 + 5] = (int)var7;
      var7 >>>= 32;
      return var7 + var21;
   }

   public static int mulWordAddExt(int var0, int[] var1, int var2, int[] var3, int var4) {
      long var5 = 0L;
      long var7 = var0 & 4294967295L;
      var5 += var7 * (var1[var2 + 0] & 4294967295L) + (var3[var4 + 0] & 4294967295L);
      var3[var4 + 0] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1[var2 + 1] & 4294967295L) + (var3[var4 + 1] & 4294967295L);
      var3[var4 + 1] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1[var2 + 2] & 4294967295L) + (var3[var4 + 2] & 4294967295L);
      var3[var4 + 2] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1[var2 + 3] & 4294967295L) + (var3[var4 + 3] & 4294967295L);
      var3[var4 + 3] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1[var2 + 4] & 4294967295L) + (var3[var4 + 4] & 4294967295L);
      var3[var4 + 4] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1[var2 + 5] & 4294967295L) + (var3[var4 + 5] & 4294967295L);
      var3[var4 + 5] = (int)var5;
      var5 >>>= 32;
      return (int)var5;
   }

   public static int mul33DWordAdd(int var0, long var1, int[] var3, int var4) {
      long var5 = 0L;
      long var7 = var0 & 4294967295L;
      long var9 = var1 & 4294967295L;
      var5 += var7 * var9 + (var3[var4 + 0] & 4294967295L);
      var3[var4 + 0] = (int)var5;
      var5 >>>= 32;
      long var11 = var1 >>> 32;
      var5 += var7 * var11 + var9 + (var3[var4 + 1] & 4294967295L);
      var3[var4 + 1] = (int)var5;
      var5 >>>= 32;
      var5 += var11 + (var3[var4 + 2] & 4294967295L);
      var3[var4 + 2] = (int)var5;
      var5 >>>= 32;
      var5 += var3[var4 + 3] & 4294967295L;
      var3[var4 + 3] = (int)var5;
      var5 >>>= 32;
      return var5 == 0L ? 0 : Nat.incAt(6, var3, var4, 4);
   }

   public static int mul33WordAdd(int var0, int var1, int[] var2, int var3) {
      long var4 = 0L;
      long var6 = var0 & 4294967295L;
      long var8 = var1 & 4294967295L;
      var4 += var8 * var6 + (var2[var3 + 0] & 4294967295L);
      var2[var3 + 0] = (int)var4;
      var4 >>>= 32;
      var4 += var8 + (var2[var3 + 1] & 4294967295L);
      var2[var3 + 1] = (int)var4;
      var4 >>>= 32;
      var4 += var2[var3 + 2] & 4294967295L;
      var2[var3 + 2] = (int)var4;
      var4 >>>= 32;
      return var4 == 0L ? 0 : Nat.incAt(6, var2, var3, 3);
   }

   public static int mulWordDwordAdd(int var0, long var1, int[] var3, int var4) {
      long var5 = 0L;
      long var7 = var0 & 4294967295L;
      var5 += var7 * (var1 & 4294967295L) + (var3[var4 + 0] & 4294967295L);
      var3[var4 + 0] = (int)var5;
      var5 >>>= 32;
      var5 += var7 * (var1 >>> 32) + (var3[var4 + 1] & 4294967295L);
      var3[var4 + 1] = (int)var5;
      var5 >>>= 32;
      var5 += var3[var4 + 2] & 4294967295L;
      var3[var4 + 2] = (int)var5;
      var5 >>>= 32;
      return var5 == 0L ? 0 : Nat.incAt(6, var3, var4, 3);
   }

   public static int mulWord(int var0, int[] var1, int[] var2, int var3) {
      long var4 = 0L;
      long var6 = var0 & 4294967295L;
      int var8 = 0;

      do {
         var4 += var6 * (var1[var8] & 4294967295L);
         var2[var3 + var8] = (int)var4;
         var4 >>>= 32;
      } while (++var8 < 6);

      return (int)var4;
   }

   public static void square(int[] var0, int[] var1) {
      long var2 = var0[0] & 4294967295L;
      int var6 = 0;
      int var8 = 5;
      int var9 = 12;

      do {
         long var10 = var0[var8--] & 4294967295L;
         long var12 = var10 * var10;
         var1[--var9] = var6 << 31 | (int)(var12 >>> 33);
         var1[--var9] = (int)(var12 >>> 1);
         var6 = (int)var12;
      } while (var8 > 0);

      long var60 = var2 * var2;
      long var4 = var6 << 31 & 4294967295L | var60 >>> 33;
      var1[0] = (int)var60;
      var6 = (int)(var60 >>> 32) & 1;
      long var58 = var0[1] & 4294967295L;
      var60 = var1[2] & 4294967295L;
      var4 += var58 * var2;
      int var7 = (int)var4;
      var1[1] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var60 += var4 >>> 32;
      long var64 = var0[2] & 4294967295L;
      long var14 = var1[3] & 4294967295L;
      long var16 = var1[4] & 4294967295L;
      var60 += var64 * var2;
      var7 = (int)var60;
      var1[2] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var14 += (var60 >>> 32) + var64 * var58;
      var16 += var14 >>> 32;
      var14 &= 4294967295L;
      long var18 = var0[3] & 4294967295L;
      long var20 = (var1[5] & 4294967295L) + (var16 >>> 32);
      var16 &= 4294967295L;
      long var22 = (var1[6] & 4294967295L) + (var20 >>> 32);
      var20 &= 4294967295L;
      var14 += var18 * var2;
      var7 = (int)var14;
      var1[3] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var16 += (var14 >>> 32) + var18 * var58;
      var20 += (var16 >>> 32) + var18 * var64;
      var16 &= 4294967295L;
      var22 += var20 >>> 32;
      var20 &= 4294967295L;
      long var24 = var0[4] & 4294967295L;
      long var26 = (var1[7] & 4294967295L) + (var22 >>> 32);
      var22 &= 4294967295L;
      long var28 = (var1[8] & 4294967295L) + (var26 >>> 32);
      var26 &= 4294967295L;
      var16 += var24 * var2;
      var7 = (int)var16;
      var1[4] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var20 += (var16 >>> 32) + var24 * var58;
      var22 += (var20 >>> 32) + var24 * var64;
      var20 &= 4294967295L;
      var26 += (var22 >>> 32) + var24 * var18;
      var22 &= 4294967295L;
      var28 += var26 >>> 32;
      var26 &= 4294967295L;
      long var30 = var0[5] & 4294967295L;
      long var32 = (var1[9] & 4294967295L) + (var28 >>> 32);
      var28 &= 4294967295L;
      long var34 = (var1[10] & 4294967295L) + (var32 >>> 32);
      var32 &= 4294967295L;
      var20 += var30 * var2;
      var7 = (int)var20;
      var1[5] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var22 += (var20 >>> 32) + var30 * var58;
      var26 += (var22 >>> 32) + var30 * var64;
      var28 += (var26 >>> 32) + var30 * var18;
      var32 += (var28 >>> 32) + var30 * var24;
      var34 += var32 >>> 32;
      var7 = (int)var22;
      var1[6] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var26;
      var1[7] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var28;
      var1[8] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var32;
      var1[9] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var34;
      var1[10] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = var1[11] + (int)(var34 >>> 32);
      var1[11] = var7 << 1 | var6;
   }

   public static void square(int[] var0, int var1, int[] var2, int var3) {
      long var4 = var0[var1 + 0] & 4294967295L;
      int var8 = 0;
      int var10 = 5;
      int var11 = 12;

      do {
         long var12 = var0[var1 + var10--] & 4294967295L;
         long var14 = var12 * var12;
         var2[var3 + --var11] = var8 << 31 | (int)(var14 >>> 33);
         var2[var3 + --var11] = (int)(var14 >>> 1);
         var8 = (int)var14;
      } while (var10 > 0);

      long var62 = var4 * var4;
      long var6 = var8 << 31 & 4294967295L | var62 >>> 33;
      var2[var3 + 0] = (int)var62;
      var8 = (int)(var62 >>> 32) & 1;
      long var60 = var0[var1 + 1] & 4294967295L;
      var62 = var2[var3 + 2] & 4294967295L;
      var6 += var60 * var4;
      int var9 = (int)var6;
      var2[var3 + 1] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var62 += var6 >>> 32;
      long var66 = var0[var1 + 2] & 4294967295L;
      long var16 = var2[var3 + 3] & 4294967295L;
      long var18 = var2[var3 + 4] & 4294967295L;
      var62 += var66 * var4;
      var9 = (int)var62;
      var2[var3 + 2] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var16 += (var62 >>> 32) + var66 * var60;
      var18 += var16 >>> 32;
      var16 &= 4294967295L;
      long var20 = var0[var1 + 3] & 4294967295L;
      long var22 = (var2[var3 + 5] & 4294967295L) + (var18 >>> 32);
      var18 &= 4294967295L;
      long var24 = (var2[var3 + 6] & 4294967295L) + (var22 >>> 32);
      var22 &= 4294967295L;
      var16 += var20 * var4;
      var9 = (int)var16;
      var2[var3 + 3] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var18 += (var16 >>> 32) + var20 * var60;
      var22 += (var18 >>> 32) + var20 * var66;
      var18 &= 4294967295L;
      var24 += var22 >>> 32;
      var22 &= 4294967295L;
      long var26 = var0[var1 + 4] & 4294967295L;
      long var28 = (var2[var3 + 7] & 4294967295L) + (var24 >>> 32);
      var24 &= 4294967295L;
      long var30 = (var2[var3 + 8] & 4294967295L) + (var28 >>> 32);
      var28 &= 4294967295L;
      var18 += var26 * var4;
      var9 = (int)var18;
      var2[var3 + 4] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var22 += (var18 >>> 32) + var26 * var60;
      var24 += (var22 >>> 32) + var26 * var66;
      var22 &= 4294967295L;
      var28 += (var24 >>> 32) + var26 * var20;
      var24 &= 4294967295L;
      var30 += var28 >>> 32;
      var28 &= 4294967295L;
      long var32 = var0[var1 + 5] & 4294967295L;
      long var34 = (var2[var3 + 9] & 4294967295L) + (var30 >>> 32);
      var30 &= 4294967295L;
      long var36 = (var2[var3 + 10] & 4294967295L) + (var34 >>> 32);
      var34 &= 4294967295L;
      var22 += var32 * var4;
      var9 = (int)var22;
      var2[var3 + 5] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var24 += (var22 >>> 32) + var32 * var60;
      var28 += (var24 >>> 32) + var32 * var66;
      var30 += (var28 >>> 32) + var32 * var20;
      var34 += (var30 >>> 32) + var32 * var26;
      var36 += var34 >>> 32;
      var9 = (int)var24;
      var2[var3 + 6] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var28;
      var2[var3 + 7] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var30;
      var2[var3 + 8] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var34;
      var2[var3 + 9] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var36;
      var2[var3 + 10] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = var2[var3 + 11] + (int)(var36 >>> 32);
      var2[var3 + 11] = var9 << 1 | var8;
   }

   public static int sub(int[] var0, int[] var1, int[] var2) {
      long var3 = 0L;
      var3 += (var0[0] & 4294967295L) - (var1[0] & 4294967295L);
      var2[0] = (int)var3;
      var3 >>= 32;
      var3 += (var0[1] & 4294967295L) - (var1[1] & 4294967295L);
      var2[1] = (int)var3;
      var3 >>= 32;
      var3 += (var0[2] & 4294967295L) - (var1[2] & 4294967295L);
      var2[2] = (int)var3;
      var3 >>= 32;
      var3 += (var0[3] & 4294967295L) - (var1[3] & 4294967295L);
      var2[3] = (int)var3;
      var3 >>= 32;
      var3 += (var0[4] & 4294967295L) - (var1[4] & 4294967295L);
      var2[4] = (int)var3;
      var3 >>= 32;
      var3 += (var0[5] & 4294967295L) - (var1[5] & 4294967295L);
      var2[5] = (int)var3;
      var3 >>= 32;
      return (int)var3;
   }

   public static int sub(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      long var6 = 0L;
      var6 += (var0[var1 + 0] & 4294967295L) - (var2[var3 + 0] & 4294967295L);
      var4[var5 + 0] = (int)var6;
      var6 >>= 32;
      var6 += (var0[var1 + 1] & 4294967295L) - (var2[var3 + 1] & 4294967295L);
      var4[var5 + 1] = (int)var6;
      var6 >>= 32;
      var6 += (var0[var1 + 2] & 4294967295L) - (var2[var3 + 2] & 4294967295L);
      var4[var5 + 2] = (int)var6;
      var6 >>= 32;
      var6 += (var0[var1 + 3] & 4294967295L) - (var2[var3 + 3] & 4294967295L);
      var4[var5 + 3] = (int)var6;
      var6 >>= 32;
      var6 += (var0[var1 + 4] & 4294967295L) - (var2[var3 + 4] & 4294967295L);
      var4[var5 + 4] = (int)var6;
      var6 >>= 32;
      var6 += (var0[var1 + 5] & 4294967295L) - (var2[var3 + 5] & 4294967295L);
      var4[var5 + 5] = (int)var6;
      var6 >>= 32;
      return (int)var6;
   }

   public static int subBothFrom(int[] var0, int[] var1, int[] var2) {
      long var3 = 0L;
      var3 += (var2[0] & 4294967295L) - (var0[0] & 4294967295L) - (var1[0] & 4294967295L);
      var2[0] = (int)var3;
      var3 >>= 32;
      var3 += (var2[1] & 4294967295L) - (var0[1] & 4294967295L) - (var1[1] & 4294967295L);
      var2[1] = (int)var3;
      var3 >>= 32;
      var3 += (var2[2] & 4294967295L) - (var0[2] & 4294967295L) - (var1[2] & 4294967295L);
      var2[2] = (int)var3;
      var3 >>= 32;
      var3 += (var2[3] & 4294967295L) - (var0[3] & 4294967295L) - (var1[3] & 4294967295L);
      var2[3] = (int)var3;
      var3 >>= 32;
      var3 += (var2[4] & 4294967295L) - (var0[4] & 4294967295L) - (var1[4] & 4294967295L);
      var2[4] = (int)var3;
      var3 >>= 32;
      var3 += (var2[5] & 4294967295L) - (var0[5] & 4294967295L) - (var1[5] & 4294967295L);
      var2[5] = (int)var3;
      var3 >>= 32;
      return (int)var3;
   }

   public static int subFrom(int[] var0, int[] var1) {
      long var2 = 0L;
      var2 += (var1[0] & 4294967295L) - (var0[0] & 4294967295L);
      var1[0] = (int)var2;
      var2 >>= 32;
      var2 += (var1[1] & 4294967295L) - (var0[1] & 4294967295L);
      var1[1] = (int)var2;
      var2 >>= 32;
      var2 += (var1[2] & 4294967295L) - (var0[2] & 4294967295L);
      var1[2] = (int)var2;
      var2 >>= 32;
      var2 += (var1[3] & 4294967295L) - (var0[3] & 4294967295L);
      var1[3] = (int)var2;
      var2 >>= 32;
      var2 += (var1[4] & 4294967295L) - (var0[4] & 4294967295L);
      var1[4] = (int)var2;
      var2 >>= 32;
      var2 += (var1[5] & 4294967295L) - (var0[5] & 4294967295L);
      var1[5] = (int)var2;
      var2 >>= 32;
      return (int)var2;
   }

   public static int subFrom(int[] var0, int var1, int[] var2, int var3) {
      long var4 = 0L;
      var4 += (var2[var3 + 0] & 4294967295L) - (var0[var1 + 0] & 4294967295L);
      var2[var3 + 0] = (int)var4;
      var4 >>= 32;
      var4 += (var2[var3 + 1] & 4294967295L) - (var0[var1 + 1] & 4294967295L);
      var2[var3 + 1] = (int)var4;
      var4 >>= 32;
      var4 += (var2[var3 + 2] & 4294967295L) - (var0[var1 + 2] & 4294967295L);
      var2[var3 + 2] = (int)var4;
      var4 >>= 32;
      var4 += (var2[var3 + 3] & 4294967295L) - (var0[var1 + 3] & 4294967295L);
      var2[var3 + 3] = (int)var4;
      var4 >>= 32;
      var4 += (var2[var3 + 4] & 4294967295L) - (var0[var1 + 4] & 4294967295L);
      var2[var3 + 4] = (int)var4;
      var4 >>= 32;
      var4 += (var2[var3 + 5] & 4294967295L) - (var0[var1 + 5] & 4294967295L);
      var2[var3 + 5] = (int)var4;
      var4 >>= 32;
      return (int)var4;
   }

   public static BigInteger toBigInteger(int[] var0) {
      byte[] var1 = new byte[24];

      for (int var2 = 0; var2 < 6; var2++) {
         int var3 = var0[var2];
         if (var3 != 0) {
            Pack.intToBigEndian(var3, var1, 5 - var2 << 2);
         }
      }

      return new BigInteger(1, var1);
   }

   public static BigInteger toBigInteger64(long[] var0) {
      byte[] var1 = new byte[24];

      for (int var2 = 0; var2 < 3; var2++) {
         long var3 = var0[var2];
         if (var3 != 0L) {
            Pack.longToBigEndian(var3, var1, 2 - var2 << 3);
         }
      }

      return new BigInteger(1, var1);
   }

   public static void zero(int[] var0) {
      var0[0] = 0;
      var0[1] = 0;
      var0[2] = 0;
      var0[3] = 0;
      var0[4] = 0;
      var0[5] = 0;
   }
}
