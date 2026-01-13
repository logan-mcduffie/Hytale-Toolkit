package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.math.raw.Interleave;

final class GF12 extends GF {
   @Override
   protected void gf_mul_poly(int var1, int[] var2, short[] var3, short[] var4, short[] var5, int[] var6) {
      var6[0] = this.gf_mul_ext(var4[0], var5[0]);

      for (int var7 = 1; var7 < var1; var7++) {
         var6[var7 + var7 - 1] = 0;
         short var8 = var4[var7];
         short var9 = var5[var7];

         for (int var10 = 0; var10 < var7; var10++) {
            var6[var7 + var10] = var6[var7 + var10] ^ this.gf_mul_ext_par(var8, var5[var10], var4[var10], var9);
         }

         var6[var7 + var7] = this.gf_mul_ext(var8, var9);
      }

      for (int var11 = (var1 - 1) * 2; var11 >= var1; var11--) {
         int var13 = var6[var11];

         for (int var14 = 0; var14 < var2.length - 1; var14++) {
            var6[var11 - var1 + var2[var14]] = var6[var11 - var1 + var2[var14]] ^ var13;
         }

         var6[var11 - var1] = var6[var11 - var1] ^ var13 << 1;
      }

      for (int var12 = 0; var12 < var1; var12++) {
         var3[var12] = this.gf_reduce(var6[var12]);
      }
   }

   @Override
   protected void gf_sqr_poly(int var1, int[] var2, short[] var3, short[] var4, int[] var5) {
      var5[0] = this.gf_sq_ext(var4[0]);

      for (int var6 = 1; var6 < var1; var6++) {
         var5[var6 + var6 - 1] = 0;
         var5[var6 + var6] = this.gf_sq_ext(var4[var6]);
      }

      for (int var9 = (var1 - 1) * 2; var9 >= var1; var9--) {
         int var7 = var5[var9];

         for (int var8 = 0; var8 < var2.length - 1; var8++) {
            var5[var9 - var1 + var2[var8]] = var5[var9 - var1 + var2[var8]] ^ var7;
         }

         var5[var9 - var1] = var5[var9 - var1] ^ var7 << 1;
      }

      for (int var10 = 0; var10 < var1; var10++) {
         var3[var10] = this.gf_reduce(var5[var10]);
      }
   }

   @Override
   protected short gf_frac(short var1, short var2) {
      return this.gf_mul(this.gf_inv(var1), var2);
   }

   @Override
   protected short gf_inv(short var1) {
      short var4 = this.gf_sq(var1);
      short var2 = this.gf_mul(var4, var1);
      var4 = this.gf_sq(var2);
      var4 = this.gf_sq(var4);
      short var3 = this.gf_mul(var4, var2);
      var4 = this.gf_sq(var3);
      var4 = this.gf_sq(var4);
      var4 = this.gf_sq(var4);
      var4 = this.gf_sq(var4);
      var4 = this.gf_mul(var4, var3);
      var4 = this.gf_sq(var4);
      var4 = this.gf_sq(var4);
      var4 = this.gf_mul(var4, var2);
      var4 = this.gf_sq(var4);
      var4 = this.gf_mul(var4, var1);
      return this.gf_sq(var4);
   }

   @Override
   protected short gf_mul(short var1, short var2) {
      short var3 = var1;
      short var4 = var2;
      int var5 = var1 * (var2 & 1);

      for (int var6 = 1; var6 < 12; var6++) {
         var5 ^= var3 * (var4 & 1 << var6);
      }

      return this.gf_reduce(var5);
   }

   @Override
   protected int gf_mul_ext(short var1, short var2) {
      short var3 = var1;
      short var4 = var2;
      int var5 = var1 * (var2 & 1);

      for (int var6 = 1; var6 < 12; var6++) {
         var5 ^= var3 * (var4 & 1 << var6);
      }

      return var5;
   }

   private int gf_mul_ext_par(short var1, short var2, short var3, short var4) {
      short var5 = var1;
      short var6 = var2;
      short var7 = var3;
      short var8 = var4;
      int var9 = var1 * (var2 & 1);
      int var10 = var3 * (var4 & 1);

      for (int var11 = 1; var11 < 12; var11++) {
         var9 ^= var5 * (var6 & 1 << var11);
         var10 ^= var7 * (var8 & 1 << var11);
      }

      return var9 ^ var10;
   }

   @Override
   protected short gf_reduce(int var1) {
      int var2 = var1 & 4095;
      int var3 = var1 >>> 12;
      int var4 = (var1 & 2093056) >>> 9;
      int var5 = (var1 & 14680064) >>> 18;
      int var6 = var1 >>> 21;
      return (short)(var2 ^ var3 ^ var4 ^ var5 ^ var6);
   }

   @Override
   protected short gf_sq(short var1) {
      int var2 = Interleave.expand16to32(var1);
      return this.gf_reduce(var2);
   }

   @Override
   protected int gf_sq_ext(short var1) {
      return Interleave.expand16to32(var1);
   }
}
