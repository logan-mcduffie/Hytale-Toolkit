package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class FalconSign {
   void smallints_to_fpr(double[] var1, int var2, byte[] var3, int var4) {
      int var5 = 1 << var4;

      for (int var6 = 0; var6 < var5; var6++) {
         var1[var2 + var6] = var3[var6];
      }
   }

   void ffSampling_fft_dyntree(
      SamplerCtx var1,
      double[] var2,
      int var3,
      double[] var4,
      int var5,
      double[] var6,
      int var7,
      double[] var8,
      int var9,
      double[] var10,
      int var11,
      int var12,
      int var13,
      double[] var14,
      int var15
   ) {
      if (var13 == 0) {
         double var20 = var6[var7];
         var20 = Math.sqrt(var20) * FPREngine.fpr_inv_sigma[var12];
         var2[var3] = SamplerZ.sample(var1, var2[var3], var20);
         var4[var5] = SamplerZ.sample(var1, var4[var5], var20);
      } else {
         int var16 = 1 << var13;
         int var17 = var16 >> 1;
         FalconFFT.poly_LDL_fft(var6, var7, var8, var9, var10, var11, var13);
         FalconFFT.poly_split_fft(var14, var15, var14, var15 + var17, var6, var7, var13);
         System.arraycopy(var14, var15, var6, var7, var16);
         FalconFFT.poly_split_fft(var14, var15, var14, var15 + var17, var10, var11, var13);
         System.arraycopy(var14, var15, var10, var11, var16);
         System.arraycopy(var8, var9, var14, var15, var16);
         System.arraycopy(var6, var7, var8, var9, var17);
         System.arraycopy(var10, var11, var8, var9 + var17, var17);
         int var19 = var15 + var16;
         FalconFFT.poly_split_fft(var14, var19, var14, var19 + var17, var4, var5, var13);
         this.ffSampling_fft_dyntree(
            var1, var14, var19, var14, var19 + var17, var10, var11, var10, var11 + var17, var8, var9 + var17, var12, var13 - 1, var14, var19 + var16
         );
         FalconFFT.poly_merge_fft(var14, var15 + (var16 << 1), var14, var19, var14, var19 + var17, var13);
         System.arraycopy(var4, var5, var14, var19, var16);
         FalconFFT.poly_sub(var14, var19, var14, var15 + (var16 << 1), var13);
         System.arraycopy(var14, var15 + (var16 << 1), var4, var5, var16);
         FalconFFT.poly_mul_fft(var14, var15, var14, var19, var13);
         FalconFFT.poly_add(var2, var3, var14, var15, var13);
         FalconFFT.poly_split_fft(var14, var15, var14, var15 + var17, var2, var3, var13);
         this.ffSampling_fft_dyntree(
            var1, var14, var15, var14, var15 + var17, var6, var7, var6, var7 + var17, var8, var9, var12, var13 - 1, var14, var15 + var16
         );
         FalconFFT.poly_merge_fft(var2, var3, var14, var15, var14, var15 + var17, var13);
      }
   }

   int do_sign_dyn(SamplerCtx var1, short[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, short[] var7, int var8, double[] var9, int var10) {
      int var11 = 1 << var8;
      int var18 = var10 + var11;
      int var19 = var18 + var11;
      int var20 = var19 + var11;
      this.smallints_to_fpr(var9, var18, var3, var8);
      this.smallints_to_fpr(var9, var10, var4, var8);
      this.smallints_to_fpr(var9, var20, var5, var8);
      this.smallints_to_fpr(var9, var19, var6, var8);
      FalconFFT.FFT(var9, var18, var8);
      FalconFFT.FFT(var9, var10, var8);
      FalconFFT.FFT(var9, var20, var8);
      FalconFFT.FFT(var9, var19, var8);
      FalconFFT.poly_neg(var9, var18, var8);
      FalconFFT.poly_neg(var9, var20, var8);
      int var13 = var20 + var11;
      int var14 = var13 + var11;
      System.arraycopy(var9, var18, var9, var13, var11);
      FalconFFT.poly_mulselfadj_fft(var9, var13, var8);
      System.arraycopy(var9, var10, var9, var14, var11);
      FalconFFT.poly_muladj_fft(var9, var14, var9, var19, var8);
      FalconFFT.poly_mulselfadj_fft(var9, var10, var8);
      FalconFFT.poly_add(var9, var10, var9, var13, var8);
      System.arraycopy(var9, var18, var9, var13, var11);
      FalconFFT.poly_muladj_fft(var9, var18, var9, var20, var8);
      FalconFFT.poly_add(var9, var18, var9, var14, var8);
      FalconFFT.poly_mulselfadj_fft(var9, var19, var8);
      System.arraycopy(var9, var20, var9, var14, var11);
      FalconFFT.poly_mulselfadj_fft(var9, var14, var8);
      FalconFFT.poly_add(var9, var19, var9, var14, var8);
      var13 += var11;
      var14 = var13 + var11;

      for (int var12 = 0; var12 < var11; var12++) {
         var9[var13 + var12] = var7[var12];
      }

      FalconFFT.FFT(var9, var13, var8);
      double var24 = 8.137358613394092E-5;
      System.arraycopy(var9, var13, var9, var14, var11);
      FalconFFT.poly_mul_fft(var9, var14, var9, var13, var8);
      FalconFFT.poly_mulconst(var9, var14, -var24, var8);
      FalconFFT.poly_mul_fft(var9, var13, var9, var20, var8);
      FalconFFT.poly_mulconst(var9, var13, var24, var8);
      System.arraycopy(var9, var13, var9, var20, 2 * var11);
      var13 = var19 + var11;
      var14 = var13 + var11;
      this.ffSampling_fft_dyntree(var1, var9, var13, var9, var14, var9, var10, var9, var18, var9, var19, var8, var8, var9, var14 + var11);
      var18 = var10 + var11;
      var19 = var18 + var11;
      var20 = var19 + var11;
      System.arraycopy(var9, var13, var9, var20 + var11, var11 * 2);
      var13 = var20 + var11;
      var14 = var13 + var11;
      this.smallints_to_fpr(var9, var18, var3, var8);
      this.smallints_to_fpr(var9, var10, var4, var8);
      this.smallints_to_fpr(var9, var20, var5, var8);
      this.smallints_to_fpr(var9, var19, var6, var8);
      FalconFFT.FFT(var9, var18, var8);
      FalconFFT.FFT(var9, var10, var8);
      FalconFFT.FFT(var9, var20, var8);
      FalconFFT.FFT(var9, var19, var8);
      FalconFFT.poly_neg(var9, var18, var8);
      FalconFFT.poly_neg(var9, var20, var8);
      int var15 = var14 + var11;
      int var16 = var15 + var11;
      System.arraycopy(var9, var13, var9, var15, var11);
      System.arraycopy(var9, var14, var9, var16, var11);
      FalconFFT.poly_mul_fft(var9, var15, var9, var10, var8);
      FalconFFT.poly_mul_fft(var9, var16, var9, var19, var8);
      FalconFFT.poly_add(var9, var15, var9, var16, var8);
      System.arraycopy(var9, var13, var9, var16, var11);
      FalconFFT.poly_mul_fft(var9, var16, var9, var18, var8);
      System.arraycopy(var9, var15, var9, var13, var11);
      FalconFFT.poly_mul_fft(var9, var14, var9, var20, var8);
      FalconFFT.poly_add(var9, var14, var9, var16, var8);
      FalconFFT.iFFT(var9, var13, var8);
      FalconFFT.iFFT(var9, var14, var8);
      int var26 = 0;
      int var27 = 0;

      for (int var30 = 0; var30 < var11; var30++) {
         int var29 = (var7[var30] & '\uffff') - (int)FPREngine.fpr_rint(var9[var13 + var30]);
         var26 += var29 * var29;
         var27 |= var26;
      }

      var26 |= -(var27 >>> 31);
      short[] var28 = new short[var11];

      for (int var31 = 0; var31 < var11; var31++) {
         var28[var31] = (short)(-FPREngine.fpr_rint(var9[var14 + var31]));
      }

      if (FalconCommon.is_short_half(var26, var28, var8) != 0) {
         System.arraycopy(var28, 0, var2, 0, var11);
         return 1;
      } else {
         return 0;
      }
   }

   void sign_dyn(short[] var1, SHAKEDigest var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, short[] var7, int var8, double[] var9) {
      byte var10 = 0;

      SamplerCtx var11;
      do {
         var11 = new SamplerCtx();
         var11.sigma_min = FPREngine.fpr_sigma_min[var8];
         var11.p.prng_init(var2);
      } while (this.do_sign_dyn(var11, var1, var3, var4, var5, var6, var7, var8, var9, var10) == 0);
   }
}
