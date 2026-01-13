package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

public class Tables8kGCMMultiplier implements GCMMultiplier {
   private byte[] H;
   private long[][][] T;

   @Override
   public void init(byte[] var1) {
      if (this.T == null) {
         this.T = new long[2][256][2];
      } else if (0 != GCMUtil.areEqual(this.H, var1)) {
         return;
      }

      this.H = new byte[16];
      GCMUtil.copy(var1, this.H);

      for (int var2 = 0; var2 < 2; var2++) {
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
      long[][] var2 = this.T[0];
      long[][] var3 = this.T[1];
      long[] var4 = var2[var1[14] & 255];
      long[] var5 = var3[var1[15] & 255];
      long var6 = var4[0] ^ var5[0];
      long var8 = var4[1] ^ var5[1];

      for (byte var10 = 12; var10 >= 0; var10 -= 2) {
         var4 = var2[var1[var10] & 255];
         var5 = var3[var1[var10 + 1] & 255];
         long var11 = var8 << 48;
         var8 = var4[1] ^ var5[1] ^ (var8 >>> 16 | var6 << 48);
         var6 = var4[0] ^ var5[0] ^ var6 >>> 16 ^ var11 ^ var11 >>> 1 ^ var11 >>> 2 ^ var11 >>> 7;
      }

      Pack.longToBigEndian(var6, var1, 0);
      Pack.longToBigEndian(var8, var1, 8);
   }
}
