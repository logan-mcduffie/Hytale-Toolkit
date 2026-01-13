package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

public class Tables64kGCMMultiplier implements GCMMultiplier {
   private byte[] H;
   private long[][][] T;

   @Override
   public void init(byte[] var1) {
      if (this.T == null) {
         this.T = new long[16][256][2];
      } else if (0 != GCMUtil.areEqual(this.H, var1)) {
         return;
      }

      this.H = new byte[16];
      GCMUtil.copy(var1, this.H);

      for (int var2 = 0; var2 < 16; var2++) {
         long[][] var3 = this.T[var2];
         if (var2 == 0) {
            GCMUtil.asLongs(this.H, var3[1]);
            GCMUtil.multiplyP7(var3[1], var3[1]);
         } else {
            GCMUtil.multiplyP8(this.T[var2 - 1][1], var3[1]);
         }

         for (byte var4 = 2; var4 < 256; var4 += 2) {
            GCMUtil.divideP(var3[var4 >> 1], var3[var4]);
            GCMUtil.xor(var3[var4], var3[1], var3[var4 + 1]);
         }
      }
   }

   @Override
   public void multiplyH(byte[] var1) {
      long[] var2 = this.T[0][var1[0] & 255];
      long[] var3 = this.T[1][var1[1] & 255];
      long[] var4 = this.T[2][var1[2] & 255];
      long[] var5 = this.T[3][var1[3] & 255];
      long[] var6 = this.T[4][var1[4] & 255];
      long[] var7 = this.T[5][var1[5] & 255];
      long[] var8 = this.T[6][var1[6] & 255];
      long[] var9 = this.T[7][var1[7] & 255];
      long[] var10 = this.T[8][var1[8] & 255];
      long[] var11 = this.T[9][var1[9] & 255];
      long[] var12 = this.T[10][var1[10] & 255];
      long[] var13 = this.T[11][var1[11] & 255];
      long[] var14 = this.T[12][var1[12] & 255];
      long[] var15 = this.T[13][var1[13] & 255];
      long[] var16 = this.T[14][var1[14] & 255];
      long[] var17 = this.T[15][var1[15] & 255];
      long var18 = var2[0]
         ^ var3[0]
         ^ var4[0]
         ^ var5[0]
         ^ var6[0]
         ^ var7[0]
         ^ var8[0]
         ^ var9[0]
         ^ var10[0]
         ^ var11[0]
         ^ var12[0]
         ^ var13[0]
         ^ var14[0]
         ^ var15[0]
         ^ var16[0]
         ^ var17[0];
      long var20 = var2[1]
         ^ var3[1]
         ^ var4[1]
         ^ var5[1]
         ^ var6[1]
         ^ var7[1]
         ^ var8[1]
         ^ var9[1]
         ^ var10[1]
         ^ var11[1]
         ^ var12[1]
         ^ var13[1]
         ^ var14[1]
         ^ var15[1]
         ^ var16[1]
         ^ var17[1];
      Pack.longToBigEndian(var18, var1, 0);
      Pack.longToBigEndian(var20, var1, 8);
   }
}
