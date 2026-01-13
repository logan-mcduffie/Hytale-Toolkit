package org.bouncycastle.pqc.crypto.cmce;

class BENES13 extends BENES {
   public BENES13(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   static void layer_in(long[] var0, long[] var1, int var2) {
      int var6 = 0;
      int var5 = 1 << var2;

      for (int var3 = 0; var3 < 64; var3 += var5 * 2) {
         for (int var4 = var3; var4 < var3 + var5; var4++) {
            long var7 = var0[var4 + 0] ^ var0[var4 + var5];
            var7 &= var1[var6++];
            var0[var4 + 0] = var0[var4 + 0] ^ var7;
            var0[var4 + var5] = var0[var4 + var5] ^ var7;
            var7 = var0[64 + var4 + 0] ^ var0[64 + var4 + var5];
            var7 &= var1[var6++];
            var0[64 + var4 + 0] = var0[64 + var4 + 0] ^ var7;
            var0[64 + var4 + var5] = var0[64 + var4 + var5] ^ var7;
         }
      }
   }

   static void layer_ex(long[] var0, long[] var1, int var2) {
      int var6 = 0;
      int var5 = 1 << var2;

      for (int var3 = 0; var3 < 128; var3 += var5 * 2) {
         for (int var4 = var3; var4 < var3 + var5; var4++) {
            long var7 = var0[var4 + 0] ^ var0[var4 + var5];
            var7 &= var1[var6++];
            var0[var4 + 0] = var0[var4 + 0] ^ var7;
            var0[var4 + var5] = var0[var4 + var5] ^ var7;
         }
      }
   }

   void apply_benes(byte[] var1, byte[] var2, int var3) {
      byte var7 = 0;
      int var8 = 0;
      long[] var9 = new long[128];
      long[] var10 = new long[128];
      long[] var11 = new long[64];
      long[] var12 = new long[64];
      short var6;
      if (var3 == 0) {
         var8 = this.SYS_T * 2 + 40;
         var6 = 0;
      } else {
         var8 = this.SYS_T * 2 + 40 + 12288;
         var6 = -1024;
      }

      for (int var4 = 0; var4 < 64; var4++) {
         var9[var4 + 0] = Utils.load8(var1, var7 + var4 * 16 + 0);
         var9[var4 + 64] = Utils.load8(var1, var7 + var4 * 16 + 8);
      }

      transpose_64x64(var10, var9, 0);
      transpose_64x64(var10, var9, 64);

      for (int var5 = 0; var5 <= 6; var5++) {
         for (int var13 = 0; var13 < 64; var13++) {
            var11[var13] = Utils.load8(var2, var8);
            var8 += 8;
         }

         var8 += var6;
         transpose_64x64(var12, var11);
         layer_ex(var10, var12, var5);
      }

      transpose_64x64(var9, var10, 0);
      transpose_64x64(var9, var10, 64);

      for (int var18 = 0; var18 <= 5; var18++) {
         for (int var14 = 0; var14 < 64; var14++) {
            var11[var14] = Utils.load8(var2, var8);
            var8 += 8;
         }

         var8 += var6;
         layer_in(var9, var11, var18);
      }

      for (int var19 = 4; var19 >= 0; var19--) {
         for (int var15 = 0; var15 < 64; var15++) {
            var11[var15] = Utils.load8(var2, var8);
            var8 += 8;
         }

         var8 += var6;
         layer_in(var9, var11, var19);
      }

      transpose_64x64(var10, var9, 0);
      transpose_64x64(var10, var9, 64);

      for (int var20 = 6; var20 >= 0; var20--) {
         for (int var16 = 0; var16 < 64; var16++) {
            var11[var16] = Utils.load8(var2, var8);
            var8 += 8;
         }

         var8 += var6;
         transpose_64x64(var12, var11);
         layer_ex(var10, var12, var20);
      }

      transpose_64x64(var9, var10, 0);
      transpose_64x64(var9, var10, 64);

      for (int var17 = 0; var17 < 64; var17++) {
         Utils.store8(var1, var7 + var17 * 16 + 0, var9[0 + var17]);
         Utils.store8(var1, var7 + var17 * 16 + 8, var9[64 + var17]);
      }
   }

   @Override
   public void support_gen(short[] var1, byte[] var2) {
      byte[][] var6 = new byte[this.GFBITS][(1 << this.GFBITS) / 8];

      for (int var4 = 0; var4 < this.GFBITS; var4++) {
         for (int var5 = 0; var5 < (1 << this.GFBITS) / 8; var5++) {
            var6[var4][var5] = 0;
         }
      }

      for (int var7 = 0; var7 < 1 << this.GFBITS; var7++) {
         short var3 = Utils.bitrev((short)var7, this.GFBITS);

         for (int var9 = 0; var9 < this.GFBITS; var9++) {
            var6[var9][var7 / 8] = (byte)(var6[var9][var7 / 8] | (var3 >> var9 & 1) << var7 % 8);
         }
      }

      for (int var10 = 0; var10 < this.GFBITS; var10++) {
         this.apply_benes(var6[var10], var2, 0);
      }

      for (int var8 = 0; var8 < this.SYS_N; var8++) {
         var1[var8] = 0;

         for (int var11 = this.GFBITS - 1; var11 >= 0; var11--) {
            var1[var8] = (short)(var1[var8] << 1);
            var1[var8] = (short)(var1[var8] | var6[var11][var8 / 8] >> var8 % 8 & 1);
         }
      }
   }
}
