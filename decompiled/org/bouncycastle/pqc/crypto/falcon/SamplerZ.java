package org.bouncycastle.pqc.crypto.falcon;

class SamplerZ {
   static int sample(SamplerCtx var0, double var1, double var3) {
      return sampler(var0, var1, var3);
   }

   static int gaussian0_sampler(FalconRNG var0) {
      int[] var1 = new int[]{
         10745844,
         3068844,
         3741698,
         5559083,
         1580863,
         8248194,
         2260429,
         13669192,
         2736639,
         708981,
         4421575,
         10046180,
         169348,
         7122675,
         4136815,
         30538,
         13063405,
         7650655,
         4132,
         14505003,
         7826148,
         417,
         16768101,
         11363290,
         31,
         8444042,
         8086568,
         1,
         12844466,
         265321,
         0,
         1232676,
         13644283,
         0,
         38047,
         9111839,
         0,
         870,
         6138264,
         0,
         14,
         12545723,
         0,
         0,
         3104126,
         0,
         0,
         28824,
         0,
         0,
         198,
         0,
         0,
         1
      };
      long var6 = var0.prng_get_u64();
      int var5 = var0.prng_get_u8() & 255;
      int var2 = (int)var6 & 16777215;
      int var3 = (int)(var6 >>> 24) & 16777215;
      int var4 = (int)(var6 >>> 48) | var5 << 16;
      int var9 = 0;

      for (byte var8 = 0; var8 < var1.length; var8 += 3) {
         int var10 = var1[var8 + 2];
         int var11 = var1[var8 + 1];
         int var12 = var1[var8];
         int var13 = var2 - var10 >>> 31;
         var13 = var3 - var11 - var13 >>> 31;
         var13 = var4 - var12 - var13 >>> 31;
         var9 += var13;
      }

      return var9;
   }

   private static int BerExp(FalconRNG var0, double var1, double var3) {
      int var5 = (int)(var1 * 1.4426950408889634);
      double var7 = var1 - var5 * 0.6931471805599453;
      int var9 = var5 ^ (var5 ^ 63) & -(63 - var5 >>> 31);
      long var11 = (FPREngine.fpr_expm_p63(var7, var3) << 1) - 1L >>> var9;
      byte var6 = 64;

      int var10;
      do {
         var6 -= 8;
         var10 = (var0.prng_get_u8() & 255) - ((int)(var11 >>> var6) & 0xFF);
      } while (var10 == 0 && var6 > 0);

      return var10 >>> 31;
   }

   private static int sampler(SamplerCtx var0, double var1, double var3) {
      SamplerCtx var5 = var0;
      int var6 = (int)FPREngine.fpr_floor(var1);
      double var7 = var1 - var6;
      double var9 = var3 * var3 * 0.5;
      double var11 = var3 * var0.sigma_min;

      int var14;
      double var19;
      do {
         int var13 = gaussian0_sampler(var5.p);
         int var15 = var5.p.prng_get_u8() & 255 & 1;
         var14 = var15 + ((var15 << 1) - 1) * var13;
         var19 = var14 - var7;
         var19 = var19 * var19 * var9;
         var19 -= var13 * var13 * 0.15086504887537272;
      } while (BerExp(var5.p, var19, var11) == 0);

      return var6 + var14;
   }
}
