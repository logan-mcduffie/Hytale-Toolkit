package org.bouncycastle.pqc.crypto.falcon;

class FalconFFT {
   static void FFT(double[] var0, int var1, int var2) {
      int var5 = 1 << var2;
      int var6 = var5 >> 1;
      int var4 = var6;
      int var3 = 1;

      for (byte var7 = 2; var3 < var2; var7 <<= 1) {
         int var8 = var4 >> 1;
         int var9 = var7 >> 1;
         int var10 = 0;

         for (int var11 = 0; var10 < var9; var11 += var4) {
            int var12 = var11 + var8 + var1;
            int var13 = var7 + var10 << 1;
            double var17 = FPREngine.fpr_gm_tab[var13];
            double var19 = FPREngine.fpr_gm_tab[var13 + 1];
            var13 = var1 + var11;
            int var14 = var13 + var6;
            int var15 = var13 + var8;

            for (int var16 = var15 + var6; var13 < var12; var16++) {
               double var21 = var0[var13];
               double var23 = var0[var14];
               double var29 = var0[var15];
               double var31 = var0[var16];
               double var25 = var29 * var17 - var31 * var19;
               double var27 = var29 * var19 + var31 * var17;
               var0[var13] = var21 + var25;
               var0[var14] = var23 + var27;
               var0[var15] = var21 - var25;
               var0[var16] = var23 - var27;
               var13++;
               var14++;
               var15++;
            }

            var10++;
         }

         var4 = var8;
         var3++;
      }
   }

   static void iFFT(double[] var0, int var1, int var2) {
      int var4 = 1 << var2;
      int var6 = 1;
      int var7 = var4;
      int var5 = var4 >> 1;

      for (int var3 = var2; var3 > 1; var3--) {
         int var9 = var7 >> 1;
         int var8 = var6 << 1;
         int var10 = 0;

         for (int var11 = 0; var11 < var5; var11 += var8) {
            int var12 = var11 + var6 + var1;
            int var13 = var9 + var10 << 1;
            double var17 = FPREngine.fpr_gm_tab[var13];
            double var19 = -FPREngine.fpr_gm_tab[var13 + 1];
            var13 = var1 + var11;
            int var14 = var13 + var5;
            int var15 = var13 + var6;

            for (int var16 = var15 + var5; var13 < var12; var16++) {
               double var21 = var0[var13];
               double var23 = var0[var14];
               double var25 = var0[var15];
               double var27 = var0[var16];
               var0[var13] = var21 + var25;
               var0[var14] = var23 + var27;
               var21 -= var25;
               var23 -= var27;
               var0[var15] = var21 * var17 - var23 * var19;
               var0[var16] = var21 * var19 + var23 * var17;
               var13++;
               var14++;
               var15++;
            }

            var10++;
         }

         var6 = var8;
         var7 = var9;
      }

      if (var2 > 0) {
         double var29 = FPREngine.fpr_p2_tab[var2];

         for (int var31 = 0; var31 < var4; var31++) {
            var0[var1 + var31] = var0[var1 + var31] * var29;
         }
      }
   }

   static void poly_add(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;

      for (int var6 = 0; var6 < var5; var6++) {
         var0[var1 + var6] = var0[var1 + var6] + var2[var3 + var6];
      }
   }

   static void poly_sub(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;

      for (int var6 = 0; var6 < var5; var6++) {
         var0[var1 + var6] = var0[var1 + var6] - var2[var3 + var6];
      }
   }

   static void poly_neg(double[] var0, int var1, int var2) {
      int var3 = 1 << var2;

      for (int var4 = 0; var4 < var3; var4++) {
         var0[var1 + var4] = -var0[var1 + var4];
      }
   }

   static void poly_adj_fft(double[] var0, int var1, int var2) {
      int var3 = 1 << var2;

      for (int var4 = var3 >> 1; var4 < var3; var4++) {
         var0[var1 + var4] = -var0[var1 + var4];
      }
   }

   static void poly_mul_fft(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;
      int var6 = var5 >> 1;
      int var7 = 0;
      int var16 = var1;
      int var17 = var1 + var6;

      for (int var18 = var3; var7 < var6; var17++) {
         double var8 = var0[var16];
         double var10 = var0[var17];
         double var12 = var2[var18];
         double var14 = var2[var18 + var6];
         var0[var16] = var8 * var12 - var10 * var14;
         var0[var17] = var8 * var14 + var10 * var12;
         var7++;
         var16++;
         var18++;
      }
   }

   static void poly_muladj_fft(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;
      int var6 = var5 >> 1;
      int var7 = 0;

      for (int var8 = var1; var7 < var6; var8++) {
         double var9 = var0[var8];
         double var11 = var0[var8 + var6];
         double var13 = var2[var3 + var7];
         double var15 = var2[var3 + var7 + var6];
         var0[var8] = var9 * var13 + var11 * var15;
         var0[var8 + var6] = var11 * var13 - var9 * var15;
         var7++;
      }
   }

   static void poly_mulselfadj_fft(double[] var0, int var1, int var2) {
      int var3 = 1 << var2;
      int var4 = var3 >> 1;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = var0[var1 + var5];
         double var8 = var0[var1 + var5 + var4];
         var0[var1 + var5] = var6 * var6 + var8 * var8;
         var0[var1 + var5 + var4] = 0.0;
      }
   }

   static void poly_mulconst(double[] var0, int var1, double var2, int var4) {
      int var5 = 1 << var4;

      for (int var6 = 0; var6 < var5; var6++) {
         var0[var1 + var6] = var0[var1 + var6] * var2;
      }
   }

   static void poly_invnorm2_fft(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5, int var6) {
      int var7 = 1 << var6;
      int var8 = var7 >> 1;

      for (int var9 = 0; var9 < var8; var9++) {
         double var10 = var2[var3 + var9];
         double var12 = var2[var3 + var9 + var8];
         double var14 = var4[var5 + var9];
         double var16 = var4[var5 + var9 + var8];
         var0[var1 + var9] = 1.0 / (var10 * var10 + var12 * var12 + var14 * var14 + var16 * var16);
      }
   }

   static void poly_add_muladj_fft(double[] var0, double[] var1, double[] var2, double[] var3, double[] var4, int var5) {
      int var6 = 1 << var5;
      int var7 = var6 >> 1;

      for (int var8 = 0; var8 < var7; var8++) {
         int var33 = var8 + var7;
         double var9 = var1[var8];
         double var11 = var1[var33];
         double var13 = var2[var8];
         double var15 = var2[var33];
         double var17 = var3[var8];
         double var19 = var3[var33];
         double var21 = var4[var8];
         double var23 = var4[var33];
         double var25 = var9 * var17 + var11 * var19;
         double var27 = var11 * var17 - var9 * var19;
         double var29 = var13 * var21 + var15 * var23;
         double var31 = var15 * var21 - var13 * var23;
         var0[var8] = var25 + var29;
         var0[var33] = var27 + var31;
      }
   }

   static void poly_mul_autoadj_fft(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;
      int var6 = var5 >> 1;

      for (int var7 = 0; var7 < var6; var7++) {
         var0[var1 + var7] = var0[var1 + var7] * var2[var3 + var7];
         var0[var1 + var7 + var6] = var0[var1 + var7 + var6] * var2[var3 + var7];
      }
   }

   static void poly_div_autoadj_fft(double[] var0, int var1, double[] var2, int var3, int var4) {
      int var5 = 1 << var4;
      int var6 = var5 >> 1;

      for (int var7 = 0; var7 < var6; var7++) {
         double var8 = 1.0 / var2[var3 + var7];
         var0[var1 + var7] = var0[var1 + var7] * var8;
         var0[var1 + var7 + var6] = var0[var1 + var7 + var6] * var8;
      }
   }

   static void poly_LDL_fft(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5, int var6) {
      int var7 = 1 << var6;
      int var8 = var7 >> 1;
      int var9 = 0;
      int var10 = var8;
      int var11 = var3;

      for (int var12 = var3 + var8; var9 < var8; var12++) {
         double var13 = var0[var1 + var9];
         double var15 = var0[var1 + var10];
         double var17 = var2[var11];
         double var19 = var2[var12];
         double var23 = 1.0 / (var13 * var13 + var15 * var15);
         double var21 = var13 * var23;
         var23 *= -var15;
         var13 = var17 * var21 - var19 * var23;
         var15 = var17 * var23 + var19 * var21;
         double var27 = var13 * var17 + var15 * var19;
         var19 = var13 * -var19 + var15 * var17;
         var4[var5 + var9] = var4[var5 + var9] - var27;
         var4[var5 + var10] = var4[var5 + var10] - var19;
         var2[var11] = var13;
         var2[var12] = -var15;
         var9++;
         var10++;
         var11++;
      }
   }

   static void poly_split_fft(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5, int var6) {
      int var7 = 1 << var6;
      int var8 = var7 >> 1;
      int var9 = var8 >> 1;
      var0[var1] = var4[var5];
      var2[var3] = var4[var5 + var8];

      for (int var10 = 0; var10 < var9; var10++) {
         int var23 = var5 + (var10 << 1);
         double var11 = var4[var23];
         double var13 = var4[var23++ + var8];
         double var15 = var4[var23];
         double var17 = var4[var23 + var8];
         var0[var1 + var10] = (var11 + var15) * 0.5;
         var0[var1 + var10 + var9] = (var13 + var17) * 0.5;
         double var19 = var11 - var15;
         double var21 = var13 - var17;
         var23 = var10 + var8 << 1;
         var15 = FPREngine.fpr_gm_tab[var23];
         var17 = -FPREngine.fpr_gm_tab[var23 + 1];
         var23 = var3 + var10;
         var2[var23] = (var19 * var15 - var21 * var17) * 0.5;
         var2[var23 + var9] = (var19 * var17 + var21 * var15) * 0.5;
      }
   }

   static void poly_merge_fft(double[] var0, int var1, double[] var2, int var3, double[] var4, int var5, int var6) {
      int var7 = 1 << var6;
      int var8 = var7 >> 1;
      int var9 = var8 >> 1;
      var0[var1] = var2[var3];
      var0[var1 + var8] = var4[var5];

      for (int var10 = 0; var10 < var9; var10++) {
         int var11 = var5 + var10;
         double var12 = var4[var11];
         double var14 = var4[var11 + var9];
         var11 = var10 + var8 << 1;
         double var20 = FPREngine.fpr_gm_tab[var11];
         double var22 = FPREngine.fpr_gm_tab[var11 + 1];
         double var16 = var12 * var20 - var14 * var22;
         double var18 = var12 * var22 + var14 * var20;
         var11 = var3 + var10;
         var12 = var2[var11];
         var14 = var2[var11 + var9];
         var11 = var1 + (var10 << 1);
         var0[var11] = var12 + var16;
         var0[var11++ + var8] = var14 + var18;
         var0[var11] = var12 - var16;
         var0[var11 + var8] = var14 - var18;
      }
   }
}
