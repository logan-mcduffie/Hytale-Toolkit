package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.math.raw.Interleave;

final class GF13 extends GF {
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

         for (int var14 = 0; var14 < var2.length; var14++) {
            var6[var11 - var1 + var2[var14]] = var6[var11 - var1 + var2[var14]] ^ var13;
         }
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

         for (int var8 = 0; var8 < var2.length; var8++) {
            var5[var9 - var1 + var2[var8]] = var5[var9 - var1 + var2[var8]] ^ var7;
         }
      }

      for (int var10 = 0; var10 < var1; var10++) {
         var3[var10] = this.gf_reduce(var5[var10]);
      }
   }

   @Override
   protected short gf_frac(short var1, short var2) {
      short var3 = this.gf_sqmul(var1, var1);
      short var4 = this.gf_sq2mul(var3, var3);
      short var5 = this.gf_sq2(var4);
      var5 = this.gf_sq2mul(var5, var4);
      var5 = this.gf_sq2(var5);
      var5 = this.gf_sq2mul(var5, var4);
      return this.gf_sqmul(var5, var2);
   }

   @Override
   protected short gf_inv(short var1) {
      return this.gf_frac(var1, (short)1);
   }

   @Override
   protected short gf_mul(short var1, short var2) {
      short var3 = var1;
      short var4 = var2;
      int var5 = var1 * (var2 & 1);

      for (int var6 = 1; var6 < 13; var6++) {
         var5 ^= var3 * (var4 & 1 << var6);
      }

      return this.gf_reduce(var5);
   }

   @Override
   protected int gf_mul_ext(short var1, short var2) {
      short var3 = var1;
      short var4 = var2;
      int var5 = var1 * (var2 & 1);

      for (int var6 = 1; var6 < 13; var6++) {
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

      for (int var11 = 1; var11 < 13; var11++) {
         var9 ^= var5 * (var6 & 1 << var11);
         var10 ^= var7 * (var8 & 1 << var11);
      }

      return var9 ^ var10;
   }

   @Override
   protected short gf_reduce(int var1) {
      int var2 = var1 & 8191;
      int var3 = var1 >>> 13;
      int var4 = var3 << 4 ^ var3 << 3 ^ var3 << 1;
      int var5 = var4 >>> 13;
      int var6 = var4 & 8191;
      int var7 = var5 << 4 ^ var5 << 3 ^ var5 << 1;
      return (short)(var2 ^ var3 ^ var5 ^ var6 ^ var7);
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

   private short gf_sq2(short var1) {
      int var2 = Interleave.expand16to32(var1);
      var1 = this.gf_reduce(var2);
      int var3 = Interleave.expand16to32(var1);
      return this.gf_reduce(var3);
   }

   private short gf_sqmul(short var1, short var2) {
      long var3 = var1;
      long var5 = var2;
      long var7 = (var5 << 6) * (var3 & 64L);
      var3 ^= var3 << 7;
      var7 ^= (var5 << 0) * (var3 & 16385L);
      var7 ^= (var5 << 1) * (var3 & 32770L);
      var7 ^= (var5 << 2) * (var3 & 65540L);
      var7 ^= (var5 << 3) * (var3 & 131080L);
      var7 ^= (var5 << 4) * (var3 & 262160L);
      var7 ^= (var5 << 5) * (var3 & 524320L);
      long var9 = var7 & 137371844608L;
      var7 ^= var9 >>> 18 ^ var9 >>> 20 ^ var9 >>> 24 ^ var9 >>> 26;
      return this.gf_reduce((int)var7 & 67108863);
   }

   private short gf_sq2mul(short var1, short var2) {
      long var3 = var1;
      long var5 = var2;
      long var7 = (var5 << 18) * (var3 & 64L);
      var3 ^= var3 << 21;
      var7 ^= (var5 << 0) * (var3 & 268435457L);
      var7 ^= (var5 << 3) * (var3 & 536870914L);
      var7 ^= (var5 << 6) * (var3 & 1073741828L);
      var7 ^= (var5 << 9) * (var3 & 2147483656L);
      var7 ^= (var5 << 12) * (var3 & 4294967312L);
      var7 ^= (var5 << 15) * (var3 & 8589934624L);
      long var9 = var7 & 2305834213120671744L;
      var7 ^= var9 >>> 18 ^ var9 >>> 20 ^ var9 >>> 24 ^ var9 >>> 26;
      var9 = var7 & 8796025913344L;
      var7 ^= var9 >>> 18 ^ var9 >>> 20 ^ var9 >>> 24 ^ var9 >>> 26;
      return this.gf_reduce((int)var7 & 67108863);
   }
}
