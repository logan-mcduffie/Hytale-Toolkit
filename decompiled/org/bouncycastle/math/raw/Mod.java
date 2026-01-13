package org.bouncycastle.math.raw;

import java.util.Random;
import org.bouncycastle.util.Integers;

public abstract class Mod {
   private static final int M30 = 1073741823;
   private static final long M32L = 4294967295L;

   public static void checkedModOddInverse(int[] var0, int[] var1, int[] var2) {
      if (0 == modOddInverse(var0, var1, var2)) {
         throw new ArithmeticException("Inverse does not exist.");
      }
   }

   public static void checkedModOddInverseVar(int[] var0, int[] var1, int[] var2) {
      if (!modOddInverseVar(var0, var1, var2)) {
         throw new ArithmeticException("Inverse does not exist.");
      }
   }

   public static int inverse32(int var0) {
      int var1 = var0 * (2 - var0 * var0);
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      return var1 * (2 - var0 * var1);
   }

   public static int modOddInverse(int[] var0, int[] var1, int[] var2) {
      int var3 = var0.length;
      int var4 = (var3 << 5) - Integers.numberOfLeadingZeros(var0[var3 - 1]);
      int var5 = (var4 + 29) / 30;
      int[] var6 = new int[4];
      int[] var7 = new int[var5];
      int[] var8 = new int[var5];
      int[] var9 = new int[var5];
      int[] var10 = new int[var5];
      int[] var11 = new int[var5];
      var8[0] = 1;
      encode30(var4, var1, var10);
      encode30(var4, var0, var11);
      System.arraycopy(var11, 0, var9, 0, var5);
      int var12 = 0;
      int var13 = inverse32(var11[0]);
      int var14 = getMaximumHDDivsteps(var4);

      for (byte var15 = 0; var15 < var14; var15 += 30) {
         var12 = hddivsteps30(var12, var9[0], var10[0], var6);
         updateDE30(var5, var7, var8, var6, var13, var11);
         updateFG30(var5, var9, var10, var6);
      }

      int var16 = var9[var5 - 1] >> 31;
      cnegate30(var5, var16, var9);
      cnormalize30(var5, var16, var7, var11);
      decode30(var4, var7, var2);
      return equalTo(var5, var9, 1) & equalTo(var5, var10, 0);
   }

   public static boolean modOddInverseVar(int[] var0, int[] var1, int[] var2) {
      int var3 = var0.length;
      int var4 = (var3 << 5) - Integers.numberOfLeadingZeros(var0[var3 - 1]);
      int var5 = (var4 + 29) / 30;
      int var6 = var4 - Nat.getBitLength(var3, var1);
      int[] var7 = new int[4];
      int[] var8 = new int[var5];
      int[] var9 = new int[var5];
      int[] var10 = new int[var5];
      int[] var11 = new int[var5];
      int[] var12 = new int[var5];
      var9[0] = 1;
      encode30(var4, var1, var11);
      encode30(var4, var0, var12);
      System.arraycopy(var12, 0, var10, 0, var5);
      int var13 = -var6;
      int var14 = var5;
      int var15 = var5;
      int var16 = inverse32(var12[0]);
      int var17 = getMaximumDivsteps(var4);

      for (int var18 = var6; !equalToVar(var15, var11, 0); var15 = trimFG30(var15, var10, var11)) {
         if (var18 >= var17) {
            return false;
         }

         var18 += 30;
         var13 = divsteps30Var(var13, var10[0], var11[0], var7);
         updateDE30(var14, var8, var9, var7, var16, var12);
         updateFG30(var15, var10, var11, var7);
      }

      int var19 = var10[var15 - 1] >> 31;
      int var20 = var8[var14 - 1] >> 31;
      if (var20 < 0) {
         var20 = add30(var14, var8, var12);
      }

      if (var19 < 0) {
         var20 = negate30(var14, var8);
         var19 = negate30(var15, var10);
      }

      if (!equalToVar(var15, var10, 1)) {
         return false;
      } else {
         if (var20 < 0) {
            var20 = add30(var14, var8, var12);
         }

         decode30(var4, var8, var2);
         return true;
      }
   }

   public static int modOddIsCoprime(int[] var0, int[] var1) {
      int var2 = var0.length;
      int var3 = (var2 << 5) - Integers.numberOfLeadingZeros(var0[var2 - 1]);
      int var4 = (var3 + 29) / 30;
      int[] var5 = new int[4];
      int[] var6 = new int[var4];
      int[] var7 = new int[var4];
      int[] var8 = new int[var4];
      encode30(var3, var1, var7);
      encode30(var3, var0, var8);
      System.arraycopy(var8, 0, var6, 0, var4);
      int var9 = 0;
      int var10 = getMaximumHDDivsteps(var3);

      for (byte var11 = 0; var11 < var10; var11 += 30) {
         var9 = hddivsteps30(var9, var6[0], var7[0], var5);
         updateFG30(var4, var6, var7, var5);
      }

      int var12 = var6[var4 - 1] >> 31;
      cnegate30(var4, var12, var6);
      return equalTo(var4, var6, 1) & equalTo(var4, var7, 0);
   }

   public static boolean modOddIsCoprimeVar(int[] var0, int[] var1) {
      int var2 = var0.length;
      int var3 = (var2 << 5) - Integers.numberOfLeadingZeros(var0[var2 - 1]);
      int var4 = (var3 + 29) / 30;
      int var5 = var3 - Nat.getBitLength(var2, var1);
      int[] var6 = new int[4];
      int[] var7 = new int[var4];
      int[] var8 = new int[var4];
      int[] var9 = new int[var4];
      encode30(var3, var1, var8);
      encode30(var3, var0, var9);
      System.arraycopy(var9, 0, var7, 0, var4);
      int var10 = -var5;
      int var11 = var4;
      int var12 = getMaximumDivsteps(var3);

      for (int var13 = var5; !equalToVar(var11, var8, 0); var11 = trimFG30(var11, var7, var8)) {
         if (var13 >= var12) {
            return false;
         }

         var13 += 30;
         var10 = divsteps30Var(var10, var7[0], var8[0], var6);
         updateFG30(var11, var7, var8, var6);
      }

      int var14 = var7[var11 - 1] >> 31;
      if (var14 < 0) {
         var14 = negate30(var11, var7);
      }

      return equalToVar(var11, var7, 1);
   }

   public static int[] random(int[] var0) {
      int var1 = var0.length;
      Random var2 = new Random();
      int[] var3 = Nat.create(var1);
      int var4 = var0[var1 - 1];
      var4 |= var4 >>> 1;
      var4 |= var4 >>> 2;
      var4 |= var4 >>> 4;
      var4 |= var4 >>> 8;
      var4 |= var4 >>> 16;

      do {
         for (int var5 = 0; var5 != var1; var5++) {
            var3[var5] = var2.nextInt();
         }

         var3[var1 - 1] = var3[var1 - 1] & var4;
      } while (Nat.gte(var1, var3, var0));

      return var3;
   }

   private static int add30(int var0, int[] var1, int[] var2) {
      int var3 = 0;
      int var4 = var0 - 1;

      for (int var5 = 0; var5 < var4; var5++) {
         var3 += var1[var5] + var2[var5];
         var1[var5] = var3 & 1073741823;
         var3 >>= 30;
      }

      var3 += var1[var4] + var2[var4];
      var1[var4] = var3;
      return var3 >> 30;
   }

   private static void cnegate30(int var0, int var1, int[] var2) {
      int var3 = 0;
      int var4 = var0 - 1;

      for (int var5 = 0; var5 < var4; var5++) {
         var3 += (var2[var5] ^ var1) - var1;
         var2[var5] = var3 & 1073741823;
         var3 >>= 30;
      }

      var3 += (var2[var4] ^ var1) - var1;
      var2[var4] = var3;
   }

   private static void cnormalize30(int var0, int var1, int[] var2, int[] var3) {
      int var4 = var0 - 1;
      int var5 = 0;
      int var6 = var2[var4] >> 31;

      for (int var7 = 0; var7 < var4; var7++) {
         int var8 = var2[var7] + (var3[var7] & var6);
         var8 = (var8 ^ var1) - var1;
         var5 += var8;
         var2[var7] = var5 & 1073741823;
         var5 >>= 30;
      }

      int var15 = var2[var4] + (var3[var4] & var6);
      var15 = (var15 ^ var1) - var1;
      var5 += var15;
      var2[var4] = var5;
      var5 = 0;
      var6 = var2[var4] >> 31;

      for (int var17 = 0; var17 < var4; var17++) {
         int var20 = var2[var17] + (var3[var17] & var6);
         var5 += var20;
         var2[var17] = var5 & 1073741823;
         var5 >>= 30;
      }

      var15 = var2[var4] + (var3[var4] & var6);
      var5 += var15;
      var2[var4] = var5;
   }

   private static void decode30(int var0, int[] var1, int[] var2) {
      byte var3 = 0;
      long var4 = 0L;
      int var6 = 0;
      int var7 = 0;

      while (var0 > 0) {
         while (var3 < Math.min(32, var0)) {
            var4 |= (long)var1[var6++] << var3;
            var3 += 30;
         }

         var2[var7++] = (int)var4;
         var4 >>>= 32;
         var3 -= 32;
         var0 -= 32;
      }
   }

   private static int divsteps30Var(int var0, int var1, int var2, int[] var3) {
      int var4 = 1;
      int var5 = 0;
      int var6 = 0;
      int var7 = 1;
      int var8 = var1;
      int var9 = var2;
      int var15 = 30;

      while (true) {
         int var17 = Integers.numberOfTrailingZeros(var9 | -1 << var15);
         var9 >>= var17;
         var4 <<= var17;
         var5 <<= var17;
         var0 -= var17;
         var15 -= var17;
         if (var15 <= 0) {
            var3[0] = var4;
            var3[1] = var5;
            var3[2] = var6;
            var3[3] = var7;
            return var0;
         }

         int var11;
         if (var0 <= 0) {
            var0 = 2 - var0;
            int var12 = var8;
            var8 = var9;
            var9 = -var12;
            int var13 = var4;
            var4 = var6;
            var6 = -var13;
            int var14 = var5;
            var5 = var7;
            var7 = -var14;
            int var16 = var0 > var15 ? var15 : var0;
            int var10 = -1 >>> 32 - var16 & 63;
            var11 = var8 * var9 * (var8 * var8 - 2) & var10;
         } else {
            int var21 = var0 > var15 ? var15 : var0;
            int var19 = -1 >>> 32 - var21 & 15;
            var11 = var8 + ((var8 + 1 & 4) << 1);
            var11 = var11 * -var9 & var19;
         }

         var9 += var8 * var11;
         var6 += var4 * var11;
         var7 += var5 * var11;
      }
   }

   private static void encode30(int var0, int[] var1, int[] var2) {
      byte var3 = 0;
      long var4 = 0L;
      int var6 = 0;
      int var7 = 0;

      while (var0 > 0) {
         if (var3 < Math.min(30, var0)) {
            var4 |= (var1[var6++] & 4294967295L) << var3;
            var3 += 32;
         }

         var2[var7++] = (int)var4 & 1073741823;
         var4 >>>= 30;
         var3 -= 30;
         var0 -= 30;
      }
   }

   private static int equalTo(int var0, int[] var1, int var2) {
      int var3 = var1[0] ^ var2;

      for (int var4 = 1; var4 < var0; var4++) {
         var3 |= var1[var4];
      }

      var3 = var3 >>> 1 | var3 & 1;
      return var3 - 1 >> 31;
   }

   private static boolean equalToVar(int var0, int[] var1, int var2) {
      int var3 = var1[0] ^ var2;
      if (var3 != 0) {
         return false;
      } else {
         for (int var4 = 1; var4 < var0; var4++) {
            var3 |= var1[var4];
         }

         return var3 == 0;
      }
   }

   private static int getMaximumDivsteps(int var0) {
      return (int)(188898L * var0 + (var0 < 46 ? 308405 : 181188) >>> 16);
   }

   private static int getMaximumHDDivsteps(int var0) {
      return (int)(150964L * var0 + 99243L >>> 16);
   }

   private static int hddivsteps30(int var0, int var1, int var2, int[] var3) {
      int var4 = 1073741824;
      int var5 = 0;
      int var6 = 0;
      int var7 = 1073741824;
      int var8 = var1;
      int var9 = var2;

      for (int var10 = 0; var10 < 30; var10++) {
         int var11 = var0 >> 31;
         int var12 = -(var9 & 1);
         int var13 = var8 ^ var11;
         int var14 = var4 ^ var11;
         int var15 = var5 ^ var11;
         var9 -= var13 & var12;
         var6 -= var14 & var12;
         var7 -= var15 & var12;
         int var16 = var12 & ~var11;
         var0 = (var0 ^ var16) + 1;
         var8 += var9 & var16;
         var4 += var6 & var16;
         var5 += var7 & var16;
         var9 >>= 1;
         var6 >>= 1;
         var7 >>= 1;
      }

      var3[0] = var4;
      var3[1] = var5;
      var3[2] = var6;
      var3[3] = var7;
      return var0;
   }

   private static int negate30(int var0, int[] var1) {
      int var2 = 0;
      int var3 = var0 - 1;

      for (int var4 = 0; var4 < var3; var4++) {
         var2 -= var1[var4];
         var1[var4] = var2 & 1073741823;
         var2 >>= 30;
      }

      var2 -= var1[var3];
      var1[var3] = var2;
      return var2 >> 30;
   }

   private static int trimFG30(int var0, int[] var1, int[] var2) {
      int var3 = var1[var0 - 1];
      int var4 = var2[var0 - 1];
      int var5 = var0 - 2 >> 31;
      var5 |= var3 ^ var3 >> 31;
      var5 |= var4 ^ var4 >> 31;
      if (var5 == 0) {
         var1[var0 - 2] = var1[var0 - 2] | var3 << 30;
         var2[var0 - 2] = var2[var0 - 2] | var4 << 30;
         var0--;
      }

      return var0;
   }

   private static void updateDE30(int var0, int[] var1, int[] var2, int[] var3, int var4, int[] var5) {
      int var6 = var3[0];
      int var7 = var3[1];
      int var8 = var3[2];
      int var9 = var3[3];
      int var16 = var1[var0 - 1] >> 31;
      int var17 = var2[var0 - 1] >> 31;
      int var13 = (var6 & var16) + (var7 & var17);
      int var14 = (var8 & var16) + (var9 & var17);
      int var15 = var5[0];
      int var10 = var1[0];
      int var11 = var2[0];
      long var18 = (long)var6 * var10 + (long)var7 * var11;
      long var20 = (long)var8 * var10 + (long)var9 * var11;
      var13 -= var4 * (int)var18 + var13 & 1073741823;
      var14 -= var4 * (int)var20 + var14 & 1073741823;
      var18 += (long)var15 * var13;
      var20 += (long)var15 * var14;
      var18 >>= 30;
      var20 >>= 30;

      for (int var12 = 1; var12 < var0; var12++) {
         var15 = var5[var12];
         var10 = var1[var12];
         var11 = var2[var12];
         var18 += (long)var6 * var10 + (long)var7 * var11 + (long)var15 * var13;
         var20 += (long)var8 * var10 + (long)var9 * var11 + (long)var15 * var14;
         var1[var12 - 1] = (int)var18 & 1073741823;
         var18 >>= 30;
         var2[var12 - 1] = (int)var20 & 1073741823;
         var20 >>= 30;
      }

      var1[var0 - 1] = (int)var18;
      var2[var0 - 1] = (int)var20;
   }

   private static void updateFG30(int var0, int[] var1, int[] var2, int[] var3) {
      int var4 = var3[0];
      int var5 = var3[1];
      int var6 = var3[2];
      int var7 = var3[3];
      int var8 = var1[0];
      int var9 = var2[0];
      long var11 = (long)var4 * var8 + (long)var5 * var9;
      long var13 = (long)var6 * var8 + (long)var7 * var9;
      var11 >>= 30;
      var13 >>= 30;

      for (int var10 = 1; var10 < var0; var10++) {
         var8 = var1[var10];
         var9 = var2[var10];
         var11 += (long)var4 * var8 + (long)var5 * var9;
         var13 += (long)var6 * var8 + (long)var7 * var9;
         var1[var10 - 1] = (int)var11 & 1073741823;
         var11 >>= 30;
         var2[var10 - 1] = (int)var13 & 1073741823;
         var13 >>= 30;
      }

      var1[var0 - 1] = (int)var11;
      var2[var0 - 1] = (int)var13;
   }
}
