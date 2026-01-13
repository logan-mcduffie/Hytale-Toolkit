package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.util.GF16;

class GF16Utils {
   private static final int GF16_MASK = 585;

   static void encodeMergeInHalf(byte[] var0, int var1, byte[] var2) {
      int var4 = var1 + 1 >>> 1;

      int var3;
      for (var3 = 0; var3 < var1 / 2; var4++) {
         var2[var3] = (byte)(var0[var3] | var0[var4] << 4);
         var3++;
      }

      if ((var1 & 1) == 1) {
         var2[var3] = var0[var3];
      }
   }

   static void decodeMergeInHalf(byte[] var0, byte[] var1, int var2) {
      int var4 = var2 + 1 >>> 1;

      for (int var3 = 0; var3 < var4; var3++) {
         var1[var3] = (byte)(var0[var3] & 15);
         var1[var3 + var4] = (byte)(var0[var3] >>> 4 & 15);
      }
   }

   static void gf16mTranMulMul(byte[] var0, int var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, byte[] var8, int var9) {
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;

      while (var10 < var9) {
         for (int var13 = 0; var13 < var9; var13++) {
            byte var14 = 0;
            int var15 = 0;
            int var16 = var1 + var13;

            for (int var17 = var10; var15 < var9; var17 += var9) {
               var14 ^= GF16.mul(var0[var16], var4[var17]);
               var15++;
               var16 += var9;
            }

            var6[var13] = var14;
         }

         int var18 = 0;

         for (int var21 = 0; var18 < var9; var21 += var9) {
            byte var22 = 0;

            for (int var23 = 0; var23 < var9; var23++) {
               var22 ^= GF16.mul(var2[var21 + var23], var6[var23]);
            }

            var7[var10 + var21] = var22;
            var18++;
         }

         for (int var19 = 0; var19 < var9; var19++) {
            var6[var19] = GF16.innerProduct(var5, var11, var0, var1 + var19, var9);
         }

         for (int var20 = 0; var20 < var9; var20++) {
            var8[var12++] = GF16.innerProduct(var6, 0, var3, var20, var9);
         }

         var10++;
         var11 += var9;
      }
   }

   static void gf16mMulMul(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5) {
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      while (var6 < var5) {
         for (int var9 = 0; var9 < var5; var9++) {
            var3[var9] = GF16.innerProduct(var0, var7, var1, var9, var5);
         }

         for (int var10 = 0; var10 < var5; var10++) {
            var4[var8++] = GF16.innerProduct(var3, 0, var2, var10, var5);
         }

         var6++;
         var7 += var5;
      }
   }

   static void gf16mMul(byte[] var0, byte[] var1, byte[] var2, int var3) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      while (var4 < var3) {
         for (int var7 = 0; var7 < var3; var7++) {
            var2[var6++] = GF16.innerProduct(var0, var5, var1, var7, var3);
         }

         var4++;
         var5 += var3;
      }
   }

   static void gf16mMulMulTo(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5) {
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      while (var6 < var5) {
         for (int var9 = 0; var9 < var5; var9++) {
            var3[var9] = GF16.innerProduct(var0, var7, var1, var9, var5);
         }

         for (int var10 = 0; var10 < var5; var10++) {
            var4[var8++] ^= GF16.innerProduct(var3, 0, var2, var10, var5);
         }

         var6++;
         var7 += var5;
      }
   }

   static void gf16mMulTo(byte[] var0, byte[] var1, byte[] var2, int var3) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      while (var4 < var3) {
         for (int var7 = 0; var7 < var3; var7++) {
            var2[var6++] ^= GF16.innerProduct(var0, var5, var1, var7, var3);
         }

         var4++;
         var5 += var3;
      }
   }

   static void gf16mMulToTo(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5) {
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      while (var6 < var5) {
         for (int var9 = 0; var9 < var5; var9++) {
            var3[var8] ^= GF16.innerProduct(var0, var7, var1, var9, var5);
            var4[var8++] ^= GF16.innerProduct(var1, var7, var2, var9, var5);
         }

         var6++;
         var7 += var5;
      }
   }

   static void gf16mMulTo(byte[] var0, byte[] var1, byte[] var2, int var3, int var4) {
      int var5 = 0;
      int var6 = 0;

      while (var5 < var4) {
         for (int var7 = 0; var7 < var4; var7++) {
            var2[var3++] ^= GF16.innerProduct(var0, var6, var1, var7, var4);
         }

         var5++;
         var6 += var4;
      }
   }

   static void gf16mMulTo(byte[] var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6) {
      int var7 = 0;
      int var8 = 0;

      while (var7 < var6) {
         for (int var9 = 0; var9 < var6; var9++) {
            int var10001 = var5++;
            var4[var10001] = (byte)(var4[var10001] ^ GF16.innerProduct(var0, var8, var1, var9, var6) ^ GF16.innerProduct(var2, var8, var3, var9, var6));
         }

         var7++;
         var8 += var6;
      }
   }

   static void gf16mMulTo(byte[] var0, byte[] var1, int var2, byte[] var3, int var4, int var5) {
      int var6 = 0;
      int var7 = 0;

      while (var6 < var5) {
         for (int var8 = 0; var8 < var5; var8++) {
            var3[var4++] ^= GF16.innerProduct(var0, var7, var1, var2 + var8, var5);
         }

         var6++;
         var7 += var5;
      }
   }

   static int gf16FromNibble(int var0) {
      int var1 = var0 | var0 << 4;
      return var1 & 65 | var1 << 2 & 520;
   }

   static int ctGF16IsNotZero(byte var0) {
      int var1 = var0 & 255;
      return (var1 | var1 >>> 1 | var1 >>> 2 | var1 >>> 3) & 1;
   }

   private static int gf16Reduce(int var0) {
      int var1 = var0 & 1227133513;
      int var2 = var0 >>> 12;
      var1 ^= var2 ^ var2 << 3;
      var2 = var1 >>> 12;
      var1 ^= var2 ^ var2 << 3;
      var2 = var1 >>> 12;
      var1 ^= var2 ^ var2 << 3;
      return var1 & 585;
   }

   static byte gf16ToNibble(int var0) {
      int var1 = gf16Reduce(var0);
      var1 |= var1 >>> 4;
      return (byte)(var1 & 5 | var1 >>> 2 & 10);
   }
}
