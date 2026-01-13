package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class WotsPlus {
   private final SPHINCSPlusEngine engine;
   private final int w;

   WotsPlus(SPHINCSPlusEngine var1) {
      this.engine = var1;
      this.w = this.engine.WOTS_W;
   }

   byte[] pkGen(byte[] var1, byte[] var2, ADRS var3) {
      ADRS var4 = new ADRS(var3);
      byte[][] var5 = new byte[this.engine.WOTS_LEN][];

      for (int var6 = 0; var6 < this.engine.WOTS_LEN; var6++) {
         ADRS var7 = new ADRS(var3);
         var7.setTypeAndClear(5);
         var7.setKeyPairAddress(var3.getKeyPairAddress());
         var7.setChainAddress(var6);
         var7.setHashAddress(0);
         byte[] var8 = this.engine.PRF(var2, var1, var7);
         var7.setTypeAndClear(0);
         var7.setKeyPairAddress(var3.getKeyPairAddress());
         var7.setChainAddress(var6);
         var7.setHashAddress(0);
         var5[var6] = this.chain(var8, 0, this.w - 1, var2, var7);
      }

      var4.setTypeAndClear(1);
      var4.setKeyPairAddress(var3.getKeyPairAddress());
      return this.engine.T_l(var2, var4, Arrays.concatenate(var5));
   }

   byte[] chain(byte[] var1, int var2, int var3, byte[] var4, ADRS var5) {
      if (var3 == 0) {
         return Arrays.clone(var1);
      } else if (var2 + var3 > this.w - 1) {
         return null;
      } else {
         byte[] var6 = var1;

         for (int var7 = 0; var7 < var3; var7++) {
            var5.setHashAddress(var2 + var7);
            var6 = this.engine.F(var4, var5, var6);
         }

         return var6;
      }
   }

   public byte[] sign(byte[] var1, byte[] var2, byte[] var3, ADRS var4) {
      ADRS var5 = new ADRS(var4);
      int[] var6 = new int[this.engine.WOTS_LEN];
      this.base_w(var1, 0, this.w, var6, 0, this.engine.WOTS_LEN1);
      int var7 = 0;

      for (int var8 = 0; var8 < this.engine.WOTS_LEN1; var8++) {
         var7 += this.w - 1 - var6[var8];
      }

      if (this.engine.WOTS_LOGW % 8 != 0) {
         var7 <<= 8 - this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW % 8;
      }

      int var13 = (this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW + 7) / 8;
      byte[] var9 = Pack.intToBigEndian(var7);
      this.base_w(var9, 4 - var13, this.w, var6, this.engine.WOTS_LEN1, this.engine.WOTS_LEN2);
      byte[][] var10 = new byte[this.engine.WOTS_LEN][];

      for (int var11 = 0; var11 < this.engine.WOTS_LEN; var11++) {
         var5.setTypeAndClear(5);
         var5.setKeyPairAddress(var4.getKeyPairAddress());
         var5.setChainAddress(var11);
         var5.setHashAddress(0);
         byte[] var12 = this.engine.PRF(var3, var2, var5);
         var5.setTypeAndClear(0);
         var5.setKeyPairAddress(var4.getKeyPairAddress());
         var5.setChainAddress(var11);
         var5.setHashAddress(0);
         var10[var11] = this.chain(var12, 0, var6[var11], var3, var5);
      }

      return Arrays.concatenate(var10);
   }

   void base_w(byte[] var1, int var2, int var3, int[] var4, int var5, int var6) {
      byte var7 = 0;
      int var8 = 0;

      for (int var9 = 0; var9 < var6; var9++) {
         if (var8 == 0) {
            var7 = var1[var2++];
            var8 += 8;
         }

         var8 -= this.engine.WOTS_LOGW;
         var4[var5++] = var7 >>> var8 & var3 - 1;
      }
   }

   public byte[] pkFromSig(byte[] var1, byte[] var2, byte[] var3, ADRS var4) {
      ADRS var5 = new ADRS(var4);
      int[] var6 = new int[this.engine.WOTS_LEN];
      this.base_w(var2, 0, this.w, var6, 0, this.engine.WOTS_LEN1);
      int var7 = 0;

      for (int var8 = 0; var8 < this.engine.WOTS_LEN1; var8++) {
         var7 += this.w - 1 - var6[var8];
      }

      var7 <<= 8 - this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW % 8;
      int var14 = (this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW + 7) / 8;
      byte[] var9 = Pack.intToBigEndian(var7);
      this.base_w(var9, 4 - var14, this.w, var6, this.engine.WOTS_LEN1, this.engine.WOTS_LEN2);
      byte[] var10 = new byte[this.engine.N];
      byte[][] var11 = new byte[this.engine.WOTS_LEN][];

      for (int var12 = 0; var12 < this.engine.WOTS_LEN; var12++) {
         var4.setChainAddress(var12);
         System.arraycopy(var1, var12 * this.engine.N, var10, 0, this.engine.N);
         var11[var12] = this.chain(var10, var6[var12], this.w - 1 - var6[var12], var3, var4);
      }

      var5.setTypeAndClear(1);
      var5.setKeyPairAddress(var4.getKeyPairAddress());
      return this.engine.T_l(var3, var5, Arrays.concatenate(var11));
   }
}
