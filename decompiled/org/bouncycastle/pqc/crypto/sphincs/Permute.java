package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Pack;

class Permute {
   private static final int CHACHA_ROUNDS = 12;

   protected static int rotl(int var0, int var1) {
      return var0 << var1 | var0 >>> -var1;
   }

   public static void permute(int var0, int[] var1) {
      if (var1.length != 16) {
         throw new IllegalArgumentException();
      } else if (var0 % 2 != 0) {
         throw new IllegalArgumentException("Number of rounds must be even");
      } else {
         int var2 = var1[0];
         int var3 = var1[1];
         int var4 = var1[2];
         int var5 = var1[3];
         int var6 = var1[4];
         int var7 = var1[5];
         int var8 = var1[6];
         int var9 = var1[7];
         int var10 = var1[8];
         int var11 = var1[9];
         int var12 = var1[10];
         int var13 = var1[11];
         int var14 = var1[12];
         int var15 = var1[13];
         int var16 = var1[14];
         int var17 = var1[15];

         for (int var18 = var0; var18 > 0; var18 -= 2) {
            int var19 = var2 + var6;
            int var55 = rotl(var14 ^ var19, 16);
            int var43 = var10 + var55;
            var6 = rotl(var6 ^ var43, 12);
            int var20 = var19 + var6;
            int var56 = rotl(var55 ^ var20, 8);
            int var44 = var43 + var56;
            var6 = rotl(var6 ^ var44, 7);
            int var22 = var3 + var7;
            int var58 = rotl(var15 ^ var22, 16);
            int var46 = var11 + var58;
            var7 = rotl(var7 ^ var46, 12);
            int var23 = var22 + var7;
            int var59 = rotl(var58 ^ var23, 8);
            int var47 = var46 + var59;
            var7 = rotl(var7 ^ var47, 7);
            int var25 = var4 + var8;
            int var61 = rotl(var16 ^ var25, 16);
            int var49 = var12 + var61;
            var8 = rotl(var8 ^ var49, 12);
            int var26 = var25 + var8;
            int var62 = rotl(var61 ^ var26, 8);
            int var50 = var49 + var62;
            var8 = rotl(var8 ^ var50, 7);
            int var28 = var5 + var9;
            int var64 = rotl(var17 ^ var28, 16);
            int var52 = var13 + var64;
            var9 = rotl(var9 ^ var52, 12);
            int var29 = var28 + var9;
            int var65 = rotl(var64 ^ var29, 8);
            int var53 = var52 + var65;
            var9 = rotl(var9 ^ var53, 7);
            int var21 = var20 + var7;
            int var66 = rotl(var65 ^ var21, 16);
            int var51 = var50 + var66;
            var7 = rotl(var7 ^ var51, 12);
            var2 = var21 + var7;
            var17 = rotl(var66 ^ var2, 8);
            var12 = var51 + var17;
            var7 = rotl(var7 ^ var12, 7);
            int var24 = var23 + var8;
            int var57 = rotl(var56 ^ var24, 16);
            int var54 = var53 + var57;
            var8 = rotl(var8 ^ var54, 12);
            var3 = var24 + var8;
            var14 = rotl(var57 ^ var3, 8);
            var13 = var54 + var14;
            var8 = rotl(var8 ^ var13, 7);
            int var27 = var26 + var9;
            int var60 = rotl(var59 ^ var27, 16);
            int var45 = var44 + var60;
            var9 = rotl(var9 ^ var45, 12);
            var4 = var27 + var9;
            var15 = rotl(var60 ^ var4, 8);
            var10 = var45 + var15;
            var9 = rotl(var9 ^ var10, 7);
            int var30 = var29 + var6;
            int var63 = rotl(var62 ^ var30, 16);
            int var48 = var47 + var63;
            var6 = rotl(var6 ^ var48, 12);
            var5 = var30 + var6;
            var16 = rotl(var63 ^ var5, 8);
            var11 = var48 + var16;
            var6 = rotl(var6 ^ var11, 7);
         }

         var1[0] = var2;
         var1[1] = var3;
         var1[2] = var4;
         var1[3] = var5;
         var1[4] = var6;
         var1[5] = var7;
         var1[6] = var8;
         var1[7] = var9;
         var1[8] = var10;
         var1[9] = var11;
         var1[10] = var12;
         var1[11] = var13;
         var1[12] = var14;
         var1[13] = var15;
         var1[14] = var16;
         var1[15] = var17;
      }
   }

   void chacha_permute(byte[] var1, byte[] var2) {
      int[] var4 = new int[16];

      for (int var3 = 0; var3 < 16; var3++) {
         var4[var3] = Pack.littleEndianToInt(var2, 4 * var3);
      }

      permute(12, var4);

      for (int var5 = 0; var5 < 16; var5++) {
         Pack.intToLittleEndian(var4[var5], var1, 4 * var5);
      }
   }
}
