package org.bouncycastle.pqc.crypto.hqc;

class FastFourierTransform {
   static void fastFourierTransform(int[] var0, int[] var1, int var2, int var3) {
      byte var4 = 8;
      short var5 = 128;
      int var6 = 1 << var3;
      int[] var7 = new int[var6];
      int[] var8 = new int[var6];
      int[] var9 = new int[var4 - 1];
      int[] var10 = new int[var5];
      int[] var11 = new int[var5];
      int[] var12 = new int[var4 - 1];
      int[] var13 = new int[var5];
      computeFFTBetas(var12, var4);
      computeSubsetSum(var13, var12, var4 - 1);
      computeRadix(var7, var8, var1, var3, var3);

      for (int var14 = 0; var14 < var4 - 1; var14++) {
         var9[var14] = GFCalculator.mult(var12[var14], var12[var14]) ^ var12[var14];
      }

      computeFFTRec(var10, var7, (var2 + 1) / 2, var4 - 1, var3 - 1, var9, var3, var4);
      computeFFTRec(var11, var8, var2 / 2, var4 - 1, var3 - 1, var9, var3, var4);
      int var16 = 1 << var4 - 1;
      System.arraycopy(var11, 0, var0, var16, var16);
      var0[0] = var10[0];
      var0[var16] ^= var10[0];

      for (int var15 = 1; var15 < var16; var15++) {
         var0[var15] = var10[var15] ^ GFCalculator.mult(var13[var15], var11[var15]);
         var0[var16 + var15] = var0[var16 + var15] ^ var0[var15];
      }
   }

   static void computeFFTBetas(int[] var0, int var1) {
      for (int var2 = 0; var2 < var1 - 1; var2++) {
         var0[var2] = 1 << var1 - 1 - var2;
      }
   }

   static void computeSubsetSum(int[] var0, int[] var1, int var2) {
      var0[0] = 0;

      for (int var3 = 0; var3 < var2; var3++) {
         for (int var4 = 0; var4 < 1 << var3; var4++) {
            var0[(1 << var3) + var4] = var1[var3] ^ var0[var4];
         }
      }
   }

   static void computeRadix(int[] var0, int[] var1, int[] var2, int var3, int var4) {
      switch (var3) {
         case 1:
            var0[0] = var2[0];
            var1[0] = var2[1];
            return;
         case 2:
            var0[0] = var2[0];
            var0[1] = var2[2] ^ var2[3];
            var1[0] = var2[1] ^ var0[1];
            var1[1] = var2[3];
            return;
         case 3:
            var0[0] = var2[0];
            var0[2] = var2[4] ^ var2[6];
            var0[3] = var2[6] ^ var2[7];
            var1[1] = var2[3] ^ var2[5] ^ var2[7];
            var1[2] = var2[5] ^ var2[6];
            var1[3] = var2[7];
            var0[1] = var2[2] ^ var0[2] ^ var1[1];
            var1[0] = var2[1] ^ var0[1];
            return;
         case 4:
            var0[4] = var2[8] ^ var2[12];
            var0[6] = var2[12] ^ var2[14];
            var0[7] = var2[14] ^ var2[15];
            var1[5] = var2[11] ^ var2[13];
            var1[6] = var2[13] ^ var2[14];
            var1[7] = var2[15];
            var0[5] = var2[10] ^ var2[12] ^ var1[5];
            var1[4] = var2[9] ^ var2[13] ^ var0[5];
            var0[0] = var2[0];
            var1[3] = var2[7] ^ var2[11] ^ var2[15];
            var0[3] = var2[6] ^ var2[10] ^ var2[14] ^ var1[3];
            var0[2] = var2[4] ^ var0[4] ^ var0[3] ^ var1[3];
            var1[1] = var2[3] ^ var2[5] ^ var2[9] ^ var2[13] ^ var1[3];
            var1[2] = var2[3] ^ var1[1] ^ var0[3];
            var0[1] = var2[2] ^ var0[2] ^ var1[1];
            var1[0] = var2[1] ^ var0[1];
            return;
         default:
            computeRadixBig(var0, var1, var2, var3, var4);
      }
   }

   static void computeRadixBig(int[] var0, int[] var1, int[] var2, int var3, int var4) {
      int var5 = 1;
      var5 <<= var3 - 2;
      int var6 = 1 << var4 - 2;
      int[] var7 = new int[2 * var6 + 1];
      int[] var8 = new int[2 * var6 + 1];
      int[] var9 = new int[var6];
      int[] var10 = new int[var6];
      int[] var11 = new int[var6];
      int[] var12 = new int[var6];
      Utils.copyBytes(var2, 3 * var5, var7, 0, 2 * var5);
      Utils.copyBytes(var2, 3 * var5, var7, var5, 2 * var5);
      Utils.copyBytes(var2, 0, var8, 0, 4 * var5);

      for (int var13 = 0; var13 < var5; var13++) {
         var7[var13] ^= var2[2 * var5 + var13];
         var8[var5 + var13] = var8[var5 + var13] ^ var7[var13];
      }

      computeRadix(var9, var10, var7, var3 - 1, var4);
      computeRadix(var11, var12, var8, var3 - 1, var4);
      Utils.copyBytes(var11, 0, var0, 0, 2 * var5);
      Utils.copyBytes(var9, 0, var0, var5, 2 * var5);
      Utils.copyBytes(var12, 0, var1, 0, 2 * var5);
      Utils.copyBytes(var10, 0, var1, var5, 2 * var5);
   }

   static void computeFFTRec(int[] var0, int[] var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      int var8 = 1 << var6 - 2;
      int var9 = 1 << var7 - 2;
      int[] var10 = new int[var8];
      int[] var11 = new int[var8];
      int[] var12 = new int[var7 - 2];
      int[] var13 = new int[var7 - 2];
      int[] var15 = new int[var9];
      int[] var16 = new int[var9];
      int[] var17 = new int[var9];
      int[] var18 = new int[var7 - var6 + 1];
      if (var4 == 1) {
         for (int var28 = 0; var28 < var3; var28++) {
            var18[var28] = GFCalculator.mult(var5[var28], var1[1]);
         }

         var0[0] = var1[0];
         byte var24 = 1;

         for (int var29 = 0; var29 < var3; var29++) {
            for (int var30 = 0; var30 < var24; var30++) {
               var0[var24 + var30] = var0[var30] ^ var18[var29];
            }

            var24 <<= 1;
         }
      } else {
         if (var5[var3 - 1] != 1) {
            int var20 = 1;
            int var19 = 1;
            var19 <<= var4;

            for (int var21 = 1; var21 < var19; var21++) {
               var20 = GFCalculator.mult(var20, var5[var3 - 1]);
               var1[var21] = GFCalculator.mult(var20, var1[var21]);
            }
         }

         computeRadix(var10, var11, var1, var4, var6);

         for (int var25 = 0; var25 < var3 - 1; var25++) {
            var12[var25] = GFCalculator.mult(var5[var25], GFCalculator.inverse(var5[var3 - 1]));
            var13[var25] = GFCalculator.mult(var12[var25], var12[var25]) ^ var12[var25];
         }

         computeSubsetSum(var15, var12, var3 - 1);
         computeFFTRec(var16, var10, (var2 + 1) / 2, var3 - 1, var4 - 1, var13, var6, var7);
         int var14 = 1;
         var14 <<= var3 - 1 & 15;
         if (var2 <= 3) {
            var0[0] = var16[0];
            var0[var14] = var16[0] ^ var11[0];

            for (int var26 = 1; var26 < var14; var26++) {
               var0[var26] = var16[var26] ^ GFCalculator.mult(var15[var26], var11[0]);
               var0[var14 + var26] = var0[var26] ^ var11[0];
            }
         } else {
            computeFFTRec(var17, var11, var2 / 2, var3 - 1, var4 - 1, var13, var6, var7);
            System.arraycopy(var17, 0, var0, var14, var14);
            var0[0] = var16[0];
            var0[var14] ^= var16[0];

            for (int var27 = 1; var27 < var14; var27++) {
               var0[var27] = var16[var27] ^ GFCalculator.mult(var15[var27], var17[var27]);
               var0[var14 + var27] = var0[var14 + var27] ^ var0[var27];
            }
         }
      }
   }

   static void fastFourierTransformGetError(byte[] var0, int[] var1, int var2, int[] var3) {
      byte var4 = 8;
      short var5 = 255;
      int[] var6 = new int[var4 - 1];
      int[] var7 = new int[var2];
      computeFFTBetas(var6, var4);
      computeSubsetSum(var7, var6, var4 - 1);
      var0[0] = (byte)(var0[0] ^ 1 ^ Utils.toUnsigned16Bits(-var1[0] >> 15));
      var0[0] = (byte)(var0[0] ^ 1 ^ Utils.toUnsigned16Bits(-var1[var2] >> 15));

      for (int var8 = 1; var8 < var2; var8++) {
         int var9 = var5 - var3[var7[var8]];
         var0[var9] = (byte)(var0[var9] ^ 1 ^ Math.abs(-var1[var8] >> 15));
         var9 = var5 - var3[var7[var8] ^ 1];
         var0[var9] = (byte)(var0[var9] ^ 1 ^ Math.abs(-var1[var2 + var8] >> 15));
      }
   }
}
