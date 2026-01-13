package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Arrays;

class GF2PolynomialCalculator {
   private final int VEC_N_SIZE_64;
   private final int PARAM_N;
   private final long RED_MASK;

   GF2PolynomialCalculator(int var1, int var2, long var3) {
      this.VEC_N_SIZE_64 = var1;
      this.PARAM_N = var2;
      this.RED_MASK = var3;
   }

   public void vectMul(long[] var1, long[] var2, long[] var3) {
      long[] var4 = new long[this.VEC_N_SIZE_64 << 1];
      long[] var5 = new long[this.VEC_N_SIZE_64 << 4];
      this.karatsuba(var4, 0, var2, 0, var3, 0, this.VEC_N_SIZE_64, var5, 0);
      this.reduce(var1, var4);
   }

   private void schoolbookMul(long[] var1, int var2, long[] var3, int var4, long[] var5, int var6, int var7) {
      Arrays.fill(var1, var2, var2 + (var7 << 1), 0L);

      for (int var8 = 0; var8 < var7; var2++) {
         long var9 = var3[var8 + var4];

         for (int var11 = 0; var11 < 64; var11++) {
            long var12 = -(var9 >> var11 & 1L);
            if (var11 == 0) {
               int var18 = 0;
               int var19 = var2;

               for (int var20 = var6; var18 < var7; var20++) {
                  var1[var19] ^= var5[var20] & var12;
                  var18++;
                  var19++;
               }
            } else {
               int var14 = 64 - var11;
               int var15 = 0;
               int var16 = var2;

               for (int var17 = var6; var15 < var7; var17++) {
                  var1[var16++] ^= var5[var17] << var11 & var12;
                  var1[var16] ^= var5[var17] >>> var14 & var12;
                  var15++;
               }
            }
         }

         var8++;
      }
   }

   private void karatsuba(long[] var1, int var2, long[] var3, int var4, long[] var5, int var6, int var7, long[] var8, int var9) {
      if (var7 <= 16) {
         this.schoolbookMul(var1, var2, var3, var4, var5, var6, var7);
      } else {
         int var10 = var7 >> 1;
         int var11 = var7 - var10;
         int var12 = var7 << 1;
         int var13 = var10 << 1;
         int var14 = var11 << 1;
         int var15 = var9 + var12;
         int var16 = var15 + var12;
         int var17 = var16 + var12;
         int var18 = var17 + var7;
         int var19 = var9 + (var7 << 3);
         this.karatsuba(var8, var9, var3, var4, var5, var6, var10, var8, var19);
         this.karatsuba(var8, var15, var3, var4 + var10, var5, var6 + var10, var11, var8, var19);

         for (int var20 = 0; var20 < var11; var20++) {
            long var21 = var20 < var10 ? var3[var4 + var20] : 0L;
            long var23 = var20 < var10 ? var5[var6 + var20] : 0L;
            var8[var17 + var20] = var21 ^ var3[var4 + var10 + var20];
            var8[var18 + var20] = var23 ^ var5[var6 + var10 + var20];
         }

         this.karatsuba(var8, var16, var8, var17, var8, var18, var11, var8, var19);
         System.arraycopy(var8, var9, var1, var2, var13);
         System.arraycopy(var8, var15, var1, var2 + var13, var14);

         for (int var25 = 0; var25 < 2 * var11; var25++) {
            long var26 = var25 < var13 ? var8[var9 + var25] : 0L;
            long var27 = var25 < var14 ? var8[var15 + var25] : 0L;
            var1[var2 + var10 + var25] = var1[var2 + var10 + var25] ^ var8[var16 + var25] ^ var26 ^ var27;
         }
      }
   }

   private void reduce(long[] var1, long[] var2) {
      for (int var3 = 0; var3 < this.VEC_N_SIZE_64; var3++) {
         var1[var3] = var2[var3]
            ^ var2[var3 + this.VEC_N_SIZE_64 - 1] >>> (this.PARAM_N & 63)
            ^ var2[var3 + this.VEC_N_SIZE_64] << (int)(64L - (this.PARAM_N & 63L));
      }

      var1[this.VEC_N_SIZE_64 - 1] = var1[this.VEC_N_SIZE_64 - 1] & this.RED_MASK;
   }
}
