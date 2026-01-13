package org.bouncycastle.pqc.crypto.crystals.dilithium;

class Rounding {
   public static int[] power2Round(int var0) {
      int[] var1 = new int[]{var0 + 4096 - 1 >> 13, 0};
      var1[1] = var0 - (var1[0] << 13);
      return var1;
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

   public static int makeHint(int var0, int var1, DilithiumEngine var2) {
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
