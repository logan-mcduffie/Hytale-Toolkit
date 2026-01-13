package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.util.GF16;

class GF16Utils {
   static final long NIBBLE_MASK_MSB = 8608480567731124087L;
   static final long MASK_MSB = -8608480567731124088L;
   static final long MASK_LSB = 1229782938247303441L;
   static final long NIBBLE_MASK_LSB = -1229782938247303442L;

   static void mVecMulAdd(int var0, long[] var1, int var2, int var3, long[] var4, int var5) {
      long var14 = var3 & 4294967295L;
      long var16 = var14 & 1L;
      long var18 = var14 >>> 1 & 1L;
      long var20 = var14 >>> 2 & 1L;
      long var22 = var14 >>> 3 & 1L;

      for (int var24 = 0; var24 < var0; var24++) {
         long var6 = var1[var2++];
         long var8 = var6 & -var16;
         long var10 = var6 & -8608480567731124088L;
         var6 &= 8608480567731124087L;
         long var12 = var10 >>> 3;
         var6 = var6 << 1 ^ var12 + (var12 << 1);
         var8 ^= var6 & -var18;
         var10 = var6 & -8608480567731124088L;
         var6 &= 8608480567731124087L;
         var12 = var10 >>> 3;
         var6 = var6 << 1 ^ var12 + (var12 << 1);
         var8 ^= var6 & -var20;
         var10 = var6 & -8608480567731124088L;
         var6 &= 8608480567731124087L;
         var12 = var10 >>> 3;
         var6 = var6 << 1 ^ var12 + (var12 << 1);
         var4[var5++] ^= var8 ^ var6 & -var22;
      }
   }

   static void mulAddMUpperTriangularMatXMat(int var0, long[] var1, byte[] var2, long[] var3, int var4, int var5, int var6) {
      int var7 = 0;
      int var8 = var6 * var0;
      int var9 = 0;
      int var10 = 0;

      for (int var11 = 0; var9 < var5; var11 += var8) {
         int var12 = var9;

         for (int var13 = var10; var12 < var5; var13 += var6) {
            int var14 = 0;

            for (int var15 = 0; var14 < var6; var15 += var0) {
               mVecMulAdd(var0, var1, var7, var2[var13 + var14], var3, var4 + var11 + var15);
               var14++;
            }

            var7 += var0;
            var12++;
         }

         var9++;
         var10 += var6;
      }
   }

   static void mulAddMatTransXMMat(int var0, byte[] var1, long[] var2, int var3, long[] var4, int var5, int var6) {
      int var7 = var6 * var0;
      int var8 = 0;

      for (int var9 = 0; var8 < var6; var9 += var7) {
         int var10 = 0;
         int var11 = 0;

         for (int var12 = 0; var10 < var5; var12 += var7) {
            byte var13 = var1[var11 + var8];
            int var14 = 0;

            for (int var15 = 0; var14 < var6; var15 += var0) {
               mVecMulAdd(var0, var2, var3 + var12 + var15, var13, var4, var9 + var15);
               var14++;
            }

            var10++;
            var11 += var6;
         }

         var8++;
      }
   }

   static void mulAddMatXMMat(int var0, byte[] var1, long[] var2, long[] var3, int var4, int var5) {
      int var6 = var0 * var4;
      int var7 = 0;
      int var8 = 0;

      for (int var9 = 0; var7 < var4; var9 += var6) {
         int var10 = 0;

         for (int var11 = 0; var10 < var5; var11 += var6) {
            byte var12 = var1[var8 + var10];
            int var13 = 0;

            for (int var14 = 0; var13 < var4; var14 += var0) {
               mVecMulAdd(var0, var2, var11 + var14, var12, var3, var9 + var14);
               var13++;
            }

            var10++;
         }

         var7++;
         var8 += var5;
      }
   }

   static void mulAddMatXMMat(int var0, byte[] var1, long[] var2, int var3, long[] var4, int var5, int var6, int var7) {
      int var8 = var0 * var7;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;

      while (var9 < var5) {
         int var12 = 0;

         for (int var13 = 0; var12 < var6; var13 += var8) {
            byte var14 = var1[var11 + var12];
            int var15 = 0;

            for (int var16 = 0; var15 < var7; var16 += var0) {
               mVecMulAdd(var0, var2, var13 + var16 + var3, var14, var4, var10 + var16);
               var15++;
            }

            var12++;
         }

         var9++;
         var10 += var8;
         var11 += var6;
      }
   }

   static void mulAddMUpperTriangularMatXMatTrans(int var0, long[] var1, byte[] var2, long[] var3, int var4, int var5) {
      int var6 = 0;
      int var7 = var0 * var5;
      int var8 = 0;

      for (int var9 = 0; var8 < var4; var9 += var7) {
         for (int var10 = var8; var10 < var4; var10++) {
            int var11 = 0;
            int var12 = 0;

            for (int var13 = 0; var11 < var5; var13 += var0) {
               mVecMulAdd(var0, var1, var6, var2[var12 + var10], var3, var9 + var13);
               var11++;
               var12 += var4;
            }

            var6 += var0;
         }

         var8++;
      }
   }

   static long mulFx8(byte var0, long var1) {
      int var3 = var0 & 255;
      long var4 = -(var3 & 1) & var1 ^ -(var3 >> 1 & 1) & var1 << 1 ^ -(var3 >> 2 & 1) & var1 << 2 ^ -(var3 >> 3 & 1) & var1 << 3;
      long var6 = var4 & -1085102592571150096L;
      return (var4 ^ var6 >>> 4 ^ var6 >>> 3) & 1085102592571150095L;
   }

   static void matMul(byte[] var0, byte[] var1, int var2, byte[] var3, int var4, int var5) {
      int var6 = 0;
      int var7 = 0;

      for (int var8 = 0; var6 < var5; var6++) {
         byte var9 = 0;

         for (int var10 = 0; var10 < var4; var10++) {
            var9 ^= GF16.mul(var0[var7++], var1[var2 + var10]);
         }

         var3[var8++] = var9;
      }
   }
}
