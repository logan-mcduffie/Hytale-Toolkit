package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat128 {
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
      return (int)var4;
   }

   public static void copy(int[] var0, int[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
   }

   public static void copy(int[] var0, int var1, int[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
      var2[var3 + 2] = var0[var1 + 2];
      var2[var3 + 3] = var0[var1 + 3];
   }

   public static void copy64(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
   }

   public static void copy64(long[] var0, int var1, long[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
   }

   public static int[] create() {
      return new int[4];
   }

   public static long[] create64() {
      return new long[2];
   }

   public static int[] createExt() {
      return new int[8];
   }

   public static long[] createExt64() {
      return new long[4];
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
      for (int var2 = 3; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static boolean eq64(long[] var0, long[] var1) {
      for (int var2 = 1; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static int[] fromBigInteger(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 128) {
         int[] var1 = create();

         for (int var2 = 0; var2 < 4; var2++) {
            var1[var2] = var0.intValue();
            var0 = var0.shiftRight(32);
         }

         return var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long[] fromBigInteger64(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 128) {
         long[] var1 = create64();

         for (int var2 = 0; var2 < 2; var2++) {
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
         if (var2 >= 0 && var2 < 4) {
            int var3 = var1 & 31;
            return var0[var2] >>> var3 & 1;
         } else {
            return 0;
         }
      }
   }

   public static boolean gte(int[] var0, int[] var1) {
      for (int var2 = 3; var2 >= 0; var2--) {
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
      for (int var4 = 3; var4 >= 0; var4--) {
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
         for (int var1 = 1; var1 < 4; var1++) {
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
         for (int var1 = 1; var1 < 2; var1++) {
            if (var0[var1] != 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isZero(int[] var0) {
      for (int var1 = 0; var1 < 4; var1++) {
         if (var0[var1] != 0) {
            return false;
         }
      }

      return true;
   }

   public static boolean isZero64(long[] var0) {
      for (int var1 = 0; var1 < 2; var1++) {
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
      long var11 = 0L;
      long var13 = var0[0] & 4294967295L;
      var11 += var13 * var3;
      var2[0] = (int)var11;
      var11 >>>= 32;
      var11 += var13 * var5;
      var2[1] = (int)var11;
      var11 >>>= 32;
      var11 += var13 * var7;
      var2[2] = (int)var11;
      var11 >>>= 32;
      var11 += var13 * var9;
      var2[3] = (int)var11;
      var11 >>>= 32;
      var2[4] = (int)var11;

      for (int var24 = 1; var24 < 4; var24++) {
         long var12 = 0L;
         long var14 = var0[var24] & 4294967295L;
         var12 += var14 * var3 + (var2[var24 + 0] & 4294967295L);
         var2[var24 + 0] = (int)var12;
         var12 >>>= 32;
         var12 += var14 * var5 + (var2[var24 + 1] & 4294967295L);
         var2[var24 + 1] = (int)var12;
         var12 >>>= 32;
         var12 += var14 * var7 + (var2[var24 + 2] & 4294967295L);
         var2[var24 + 2] = (int)var12;
         var12 >>>= 32;
         var12 += var14 * var9 + (var2[var24 + 3] & 4294967295L);
         var2[var24 + 3] = (int)var12;
         var12 >>>= 32;
         var2[var24 + 4] = (int)var12;
      }
   }

   public static void mul(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      long var6 = var2[var3 + 0] & 4294967295L;
      long var8 = var2[var3 + 1] & 4294967295L;
      long var10 = var2[var3 + 2] & 4294967295L;
      long var12 = var2[var3 + 3] & 4294967295L;
      long var14 = 0L;
      long var16 = var0[var1 + 0] & 4294967295L;
      var14 += var16 * var6;
      var4[var5 + 0] = (int)var14;
      var14 >>>= 32;
      var14 += var16 * var8;
      var4[var5 + 1] = (int)var14;
      var14 >>>= 32;
      var14 += var16 * var10;
      var4[var5 + 2] = (int)var14;
      var14 >>>= 32;
      var14 += var16 * var12;
      var4[var5 + 3] = (int)var14;
      var14 >>>= 32;
      var4[var5 + 4] = (int)var14;

      for (int var27 = 1; var27 < 4; var27++) {
         var5++;
         long var15 = 0L;
         long var17 = var0[var1 + var27] & 4294967295L;
         var15 += var17 * var6 + (var4[var5 + 0] & 4294967295L);
         var4[var5 + 0] = (int)var15;
         var15 >>>= 32;
         var15 += var17 * var8 + (var4[var5 + 1] & 4294967295L);
         var4[var5 + 1] = (int)var15;
         var15 >>>= 32;
         var15 += var17 * var10 + (var4[var5 + 2] & 4294967295L);
         var4[var5 + 2] = (int)var15;
         var15 >>>= 32;
         var15 += var17 * var12 + (var4[var5 + 3] & 4294967295L);
         var4[var5 + 3] = (int)var15;
         var15 >>>= 32;
         var4[var5 + 4] = (int)var15;
      }
   }

   public static int mulAddTo(int[] var0, int[] var1, int[] var2) {
      long var3 = var1[0] & 4294967295L;
      long var5 = var1[1] & 4294967295L;
      long var7 = var1[2] & 4294967295L;
      long var9 = var1[3] & 4294967295L;
      long var11 = 0L;

      for (int var13 = 0; var13 < 4; var13++) {
         long var14 = 0L;
         long var16 = var0[var13] & 4294967295L;
         var14 += var16 * var3 + (var2[var13 + 0] & 4294967295L);
         var2[var13 + 0] = (int)var14;
         var14 >>>= 32;
         var14 += var16 * var5 + (var2[var13 + 1] & 4294967295L);
         var2[var13 + 1] = (int)var14;
         var14 >>>= 32;
         var14 += var16 * var7 + (var2[var13 + 2] & 4294967295L);
         var2[var13 + 2] = (int)var14;
         var14 >>>= 32;
         var14 += var16 * var9 + (var2[var13 + 3] & 4294967295L);
         var2[var13 + 3] = (int)var14;
         var14 >>>= 32;
         var11 += var14 + (var2[var13 + 4] & 4294967295L);
         var2[var13 + 4] = (int)var11;
         var11 >>>= 32;
      }

      return (int)var11;
   }

   public static int mulAddTo(int[] var0, int var1, int[] var2, int var3, int[] var4, int var5) {
      long var6 = var2[var3 + 0] & 4294967295L;
      long var8 = var2[var3 + 1] & 4294967295L;
      long var10 = var2[var3 + 2] & 4294967295L;
      long var12 = var2[var3 + 3] & 4294967295L;
      long var14 = 0L;

      for (int var16 = 0; var16 < 4; var16++) {
         long var17 = 0L;
         long var19 = var0[var1 + var16] & 4294967295L;
         var17 += var19 * var6 + (var4[var5 + 0] & 4294967295L);
         var4[var5 + 0] = (int)var17;
         var17 >>>= 32;
         var17 += var19 * var8 + (var4[var5 + 1] & 4294967295L);
         var4[var5 + 1] = (int)var17;
         var17 >>>= 32;
         var17 += var19 * var10 + (var4[var5 + 2] & 4294967295L);
         var4[var5 + 2] = (int)var17;
         var17 >>>= 32;
         var17 += var19 * var12 + (var4[var5 + 3] & 4294967295L);
         var4[var5 + 3] = (int)var17;
         var17 >>>= 32;
         var14 += var17 + (var4[var5 + 4] & 4294967295L);
         var4[var5 + 4] = (int)var14;
         var14 >>>= 32;
         var5++;
      }

      return (int)var14;
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
      return var7 + var17;
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
      return (int)var5;
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
      return var4 == 0L ? 0 : Nat.incAt(4, var2, var3, 3);
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
      return var5 == 0L ? 0 : Nat.incAt(4, var3, var4, 3);
   }

   public static int mulWordsAdd(int var0, int var1, int[] var2, int var3) {
      long var4 = 0L;
      long var6 = var0 & 4294967295L;
      long var8 = var1 & 4294967295L;
      var4 += var8 * var6 + (var2[var3 + 0] & 4294967295L);
      var2[var3 + 0] = (int)var4;
      var4 >>>= 32;
      var4 += var2[var3 + 1] & 4294967295L;
      var2[var3 + 1] = (int)var4;
      var4 >>>= 32;
      return var4 == 0L ? 0 : Nat.incAt(4, var2, var3, 2);
   }

   public static int mulWord(int var0, int[] var1, int[] var2, int var3) {
      long var4 = 0L;
      long var6 = var0 & 4294967295L;
      int var8 = 0;

      do {
         var4 += var6 * (var1[var8] & 4294967295L);
         var2[var3 + var8] = (int)var4;
         var4 >>>= 32;
      } while (++var8 < 4);

      return (int)var4;
   }

   public static void square(int[] var0, int[] var1) {
      long var2 = var0[0] & 4294967295L;
      int var6 = 0;
      int var8 = 3;
      int var9 = 8;

      do {
         long var10 = var0[var8--] & 4294967295L;
         long var12 = var10 * var10;
         var1[--var9] = var6 << 31 | (int)(var12 >>> 33);
         var1[--var9] = (int)(var12 >>> 1);
         var6 = (int)var12;
      } while (var8 > 0);

      long var40 = var2 * var2;
      long var4 = var6 << 31 & 4294967295L | var40 >>> 33;
      var1[0] = (int)var40;
      var6 = (int)(var40 >>> 32) & 1;
      long var38 = var0[1] & 4294967295L;
      var40 = var1[2] & 4294967295L;
      var4 += var38 * var2;
      int var7 = (int)var4;
      var1[1] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var40 += var4 >>> 32;
      long var44 = var0[2] & 4294967295L;
      long var14 = var1[3] & 4294967295L;
      long var16 = var1[4] & 4294967295L;
      var40 += var44 * var2;
      var7 = (int)var40;
      var1[2] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var14 += (var40 >>> 32) + var44 * var38;
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
      var16 += (var14 >>> 32) + var18 * var38;
      var20 += (var16 >>> 32) + var18 * var44;
      var22 += var20 >>> 32;
      var20 &= 4294967295L;
      var7 = (int)var16;
      var1[4] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var20;
      var1[5] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = (int)var22;
      var1[6] = var7 << 1 | var6;
      var6 = var7 >>> 31;
      var7 = var1[7] + (int)(var22 >>> 32);
      var1[7] = var7 << 1 | var6;
   }

   public static void square(int[] var0, int var1, int[] var2, int var3) {
      long var4 = var0[var1 + 0] & 4294967295L;
      int var8 = 0;
      int var10 = 3;
      int var11 = 8;

      do {
         long var12 = var0[var1 + var10--] & 4294967295L;
         long var14 = var12 * var12;
         var2[var3 + --var11] = var8 << 31 | (int)(var14 >>> 33);
         var2[var3 + --var11] = (int)(var14 >>> 1);
         var8 = (int)var14;
      } while (var10 > 0);

      long var42 = var4 * var4;
      long var6 = var8 << 31 & 4294967295L | var42 >>> 33;
      var2[var3 + 0] = (int)var42;
      var8 = (int)(var42 >>> 32) & 1;
      long var40 = var0[var1 + 1] & 4294967295L;
      var42 = var2[var3 + 2] & 4294967295L;
      var6 += var40 * var4;
      int var9 = (int)var6;
      var2[var3 + 1] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var42 += var6 >>> 32;
      long var46 = var0[var1 + 2] & 4294967295L;
      long var16 = var2[var3 + 3] & 4294967295L;
      long var18 = var2[var3 + 4] & 4294967295L;
      var42 += var46 * var4;
      var9 = (int)var42;
      var2[var3 + 2] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var16 += (var42 >>> 32) + var46 * var40;
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
      var18 += (var16 >>> 32) + var20 * var40;
      var22 += (var18 >>> 32) + var20 * var46;
      var24 += var22 >>> 32;
      var9 = (int)var18;
      var2[var3 + 4] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var22;
      var2[var3 + 5] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = (int)var24;
      var2[var3 + 6] = var9 << 1 | var8;
      var8 = var9 >>> 31;
      var9 = var2[var3 + 7] + (int)(var24 >>> 32);
      var2[var3 + 7] = var9 << 1 | var8;
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
      return (int)var4;
   }

   public static BigInteger toBigInteger(int[] var0) {
      byte[] var1 = new byte[16];

      for (int var2 = 0; var2 < 4; var2++) {
         int var3 = var0[var2];
         if (var3 != 0) {
            Pack.intToBigEndian(var3, var1, 3 - var2 << 2);
         }
      }

      return new BigInteger(1, var1);
   }

   public static BigInteger toBigInteger64(long[] var0) {
      byte[] var1 = new byte[16];

      for (int var2 = 0; var2 < 2; var2++) {
         long var3 = var0[var2];
         if (var3 != 0L) {
            Pack.longToBigEndian(var3, var1, 1 - var2 << 3);
         }
      }

      return new BigInteger(1, var1);
   }

   public static void zero(int[] var0) {
      var0[0] = 0;
      var0[1] = 0;
      var0[2] = 0;
      var0[3] = 0;
   }
}
