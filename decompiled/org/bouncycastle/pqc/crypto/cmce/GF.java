package org.bouncycastle.pqc.crypto.cmce;

abstract class GF {
   final short gf_iszero(short var1) {
      return (short)(var1 - 1 >> 31);
   }

   protected abstract void gf_mul_poly(int var1, int[] var2, short[] var3, short[] var4, short[] var5, int[] var6);

   protected abstract void gf_sqr_poly(int var1, int[] var2, short[] var3, short[] var4, int[] var5);

   protected abstract short gf_frac(short var1, short var2);

   protected abstract short gf_inv(short var1);

   protected abstract short gf_mul(short var1, short var2);

   protected abstract int gf_mul_ext(short var1, short var2);

   protected abstract short gf_reduce(int var1);

   protected abstract short gf_sq(short var1);

   protected abstract int gf_sq_ext(short var1);
}
