package org.bouncycastle.pqc.crypto.cmce;

class BENES12 extends BENES {
   public BENES12(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   static void layerBenes(long[] var0, long[] var1, int var2) {
      int var6 = 0;
      int var5 = 1 << var2;

      for (int var3 = 0; var3 < 64; var3 += var5 * 2) {
         for (int var4 = var3; var4 < var3 + var5; var4++) {
            long var7 = var0[var4 + 0] ^ var0[var4 + var5];
            var7 &= var1[var6++];
            var0[var4 + 0] = var0[var4 + 0] ^ var7;
            var0[var4 + var5] = var0[var4 + var5] ^ var7;
         }
      }
   }

   private void apply_benes(byte[] var1, byte[] var2, int var3) {
      long[] var8 = new long[64];
      long[] var9 = new long[64];

      for (int var4 = 0; var4 < 64; var4++) {
         var8[var4] = Utils.load8(var1, var4 * 8);
      }

      int var5;
      short var6;
      if (var3 == 0) {
         var6 = 256;
         var5 = this.SYS_T * 2 + 40;
      } else {
         var6 = -256;
         var5 = this.SYS_T * 2 + 40 + (2 * this.GFBITS - 2) * 256;
      }

      transpose_64x64(var8, var8);

      for (int var7 = 0; var7 <= 5; var7++) {
         for (int var10 = 0; var10 < 64; var10++) {
            var9[var10] = Utils.load4(var2, var5 + var10 * 4);
         }

         transpose_64x64(var9, var9);
         layerBenes(var8, var9, var7);
         var5 += var6;
      }

      transpose_64x64(var8, var8);

      for (int var15 = 0; var15 <= 5; var15++) {
         for (int var11 = 0; var11 < 32; var11++) {
            var9[var11] = Utils.load8(var2, var5 + var11 * 8);
         }

         layerBenes(var8, var9, var15);
         var5 += var6;
      }

      for (int var16 = 4; var16 >= 0; var16--) {
         for (int var12 = 0; var12 < 32; var12++) {
            var9[var12] = Utils.load8(var2, var5 + var12 * 8);
         }

         layerBenes(var8, var9, var16);
         var5 += var6;
      }

      transpose_64x64(var8, var8);

      for (int var17 = 5; var17 >= 0; var17--) {
         for (int var13 = 0; var13 < 64; var13++) {
            var9[var13] = Utils.load4(var2, var5 + var13 * 4);
         }

         transpose_64x64(var9, var9);
         layerBenes(var8, var9, var17);
         var5 += var6;
      }

      transpose_64x64(var8, var8);

      for (int var14 = 0; var14 < 64; var14++) {
         Utils.store8(var1, var14 * 8, var8[var14]);
      }
   }

   @Override
   public void support_gen(short[] var1, byte[] var2) {
      byte[][] var4 = new byte[this.GFBITS][(1 << this.GFBITS) / 8];

      for (int var5 = 0; var5 < this.GFBITS; var5++) {
         for (int var6 = 0; var6 < (1 << this.GFBITS) / 8; var6++) {
            var4[var5][var6] = 0;
         }
      }

      for (int var7 = 0; var7 < 1 << this.GFBITS; var7++) {
         short var3 = Utils.bitrev((short)var7, this.GFBITS);

         for (int var10 = 0; var10 < this.GFBITS; var10++) {
            var4[var10][var7 / 8] = (byte)(var4[var10][var7 / 8] | (var3 >> var10 & 1) << var7 % 8);
         }
      }

      for (int var8 = 0; var8 < this.GFBITS; var8++) {
         this.apply_benes(var4[var8], var2, 0);
      }

      for (int var9 = 0; var9 < this.SYS_N; var9++) {
         var1[var9] = 0;

         for (int var11 = this.GFBITS - 1; var11 >= 0; var11--) {
            var1[var9] = (short)(var1[var9] << 1);
            var1[var9] = (short)(var1[var9] | var4[var11][var9 / 8] >> var9 % 8 & 1);
         }
      }
   }
}
