package org.bouncycastle.math.ec.rfc8032;

abstract class Wnaf {
   static void getSignedVar(int[] var0, int var1, byte[] var2) {
      int[] var3 = new int[var0.length * 2];
      int var4 = var0[var0.length - 1] >> 31;
      int var5 = var0.length;
      int var6 = var3.length;

      while (--var5 >= 0) {
         int var7 = var0[var5];
         var3[--var6] = var7 >>> 16 | var4 << 16;
         var6--;
         var4 = var7;
         var3[var6] = var7;
      }

      var4 = 32 - var1;
      var5 = 0;
      var6 = 0;

      for (int var16 = 0; var16 < var3.length; var5 -= 16) {
         int var8 = var3[var16];

         while (var5 < 16) {
            int var9 = var8 >>> var5;
            int var10 = var9 & 1;
            if (var10 == var6) {
               var5++;
            } else {
               int var11 = (var9 | 1) << var4;
               var6 = var11 >>> 31;
               var2[(var16 << 4) + var5] = (byte)(var11 >> var4);
               var5 += var1;
            }
         }

         var16++;
      }
   }
}
