package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Pack;

class BIKEUtils {
   static int getHammingWeight(byte[] var0) {
      byte var1 = 0;

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1 += var0[var2];
      }

      return var1;
   }

   static void fromBitArrayToByteArray(byte[] var0, byte[] var1, int var2, int var3) {
      byte var4 = 0;
      int var5 = 0;

      for (long var6 = var3; var4 < var6; var5++) {
         if (var4 + 8 >= var3) {
            int var10 = var1[var2 + var4];

            for (int var11 = var3 - var4 - 1; var11 >= 1; var11--) {
               var10 |= var1[var2 + var4 + var11] << var11;
            }

            var0[var5] = (byte)var10;
         } else {
            int var8 = var1[var2 + var4];

            for (int var9 = 7; var9 >= 1; var9--) {
               var8 |= var1[var2 + var4 + var9] << var9;
            }

            var0[var5] = (byte)var8;
         }

         var4 += 8;
      }
   }

   static void generateRandomByteArray(byte[] var0, int var1, int var2, Xof var3) {
      byte[] var4 = new byte[4];

      for (int var6 = var2 - 1; var6 >= 0; var6--) {
         var3.doOutput(var4, 0, 4);
         long var7 = Pack.littleEndianToInt(var4, 0) & 4294967295L;
         var7 = var7 * (var1 - var6) >> 32;
         int var5 = (int)var7;
         var5 += var6;
         if (CHECK_BIT(var0, var5) != 0) {
            var5 = var6;
         }

         SET_BIT(var0, var5);
      }
   }

   protected static int CHECK_BIT(byte[] var0, int var1) {
      int var2 = var1 / 8;
      int var3 = var1 % 8;
      return var0[var2] >>> var3 & 1;
   }

   protected static void SET_BIT(byte[] var0, int var1) {
      int var2 = var1 / 8;
      int var3 = var1 % 8;
      var0[var2] = (byte)(var0[var2] | 1L << (int)var3);
   }
}
