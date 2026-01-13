package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.util.Pack;

public class Tables4kGCMMultiplier implements GCMMultiplier {
   private byte[] H;
   private long[][] T;

   @Override
   public void init(byte[] var1) {
      if (this.T == null) {
         this.T = new long[256][2];
      } else if (0 != GCMUtil.areEqual(this.H, var1)) {
         return;
      }

      this.H = new byte[16];
      GCMUtil.copy(var1, this.H);
      GCMUtil.asLongs(this.H, this.T[1]);
      GCMUtil.multiplyP7(this.T[1], this.T[1]);

      for (byte var2 = 2; var2 < 256; var2 += 2) {
         GCMUtil.divideP(this.T[var2 >> 1], this.T[var2]);
         GCMUtil.xor(this.T[var2], this.T[1], this.T[var2 + 1]);
      }
   }

   @Override
   public void multiplyH(byte[] var1) {
      long[] var2 = this.T[var1[15] & 255];
      long var3 = var2[0];
      long var5 = var2[1];

      for (int var7 = 14; var7 >= 0; var7--) {
         var2 = this.T[var1[var7] & 255];
         long var8 = var5 << 56;
         var5 = var2[1] ^ (var5 >>> 8 | var3 << 56);
         var3 = var2[0] ^ var3 >>> 8 ^ var8 ^ var8 >>> 1 ^ var8 >>> 2 ^ var8 >>> 7;
      }

      Pack.longToBigEndian(var3, var1, 0);
      Pack.longToBigEndian(var5, var1, 8);
   }
}
