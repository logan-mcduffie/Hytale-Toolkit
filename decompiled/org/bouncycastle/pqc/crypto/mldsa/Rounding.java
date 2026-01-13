package org.bouncycastle.pqc.crypto.mldsa;

class Rounding {
   static void power2RoundAll(int[] var0, int[] var1) {
      byte var2 = 13;
      short var3 = 256;
      int var4 = (1 << var2 - 1) - 1;
      int var5 = -1 << var2;

      for (int var6 = 0; var6 < var3; var6++) {
         int var7 = var0[var6];
         int var8 = var7 + var4;
         int var9 = var7 - (var8 & var5);
         var0[var6] = var8 >> var2;
         var1[var6] = var9;
      }
   }

   public static int[] decompose(int var0, int var1) {
      int var2 = var0 + 127 >> 7;
      if (var1 == 261888) {
         var2 = var2 * 1025 + 2097152 >> 22;
         var2 &= 15;
      } else {
         if (var1 != 95232) {
            throw new RuntimeException("Wrong Gamma2!");
         }

         var2 = var2 * 11275 + 8388608 >> 24;
         var2 ^= 43 - var2 >> 31 & var2;
      }

      int var3 = var0 - var2 * 2 * var1;
      var3 -= 4190208 - var3 >> 31 & 8380417;
      return new int[]{var3, var2};
   }

   public static int makeHint(int var0, int var1, MLDSAEngine var2) {
      int var3 = var2.getDilithiumGamma2();
      int var4 = 8380417;
      return var0 > var3 && var0 <= var4 - var3 && (var0 != var4 - var3 || var1 != 0) ? 1 : 0;
   }

   public static int useHint(int var0, int var1, int var2) {
      int[] var5 = decompose(var0, var2);
      int var3 = var5[0];
      int var4 = var5[1];
      if (var1 == 0) {
         return var4;
      } else if (var2 == 261888) {
         return var3 > 0 ? var4 + 1 & 15 : var4 - 1 & 15;
      } else if (var2 == 95232) {
         if (var3 > 0) {
            return var4 == 43 ? 0 : var4 + 1;
         } else {
            return var4 == 0 ? 43 : var4 - 1;
         }
      } else {
         throw new RuntimeException("Wrong Gamma2!");
      }
   }
}
