package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class FalconCommon {
   static final int[] l2bound = new int[]{0, 101498, 208714, 428865, 892039, 1852696, 3842630, 7959734, 16468416, 34034726, 70265242};

   static void hash_to_point_vartime(SHAKEDigest var0, short[] var1, int var2) {
      int var4 = 0;
      int var3 = 1 << var2;
      byte[] var5 = new byte[2];

      while (var3 > 0) {
         var0.doOutput(var5, 0, 2);
         int var6 = (var5[0] & 255) << 8 | var5[1] & 255;
         if (var6 < 61445) {
            var6 %= 12289;
            var1[var4++] = (short)var6;
            var3--;
         }
      }
   }

   static int is_short(short[] var0, int var1, short[] var2, int var3) {
      int var4 = 1 << var3;
      int var6 = 0;
      int var7 = 0;

      for (int var5 = 0; var5 < var4; var5++) {
         short var8 = var0[var1 + var5];
         int var9 = var6 + var8 * var8;
         var7 |= var9;
         var8 = var2[var5];
         var6 = var9 + var8 * var8;
         var7 |= var6;
      }

      var6 |= -(var7 >>> 31);
      return (var6 & 4294967295L) <= l2bound[var3] ? 1 : 0;
   }

   static int is_short_half(int var0, short[] var1, int var2) {
      int var3 = 1 << var2;
      int var5 = -(var0 >>> 31);

      for (int var4 = 0; var4 < var3; var4++) {
         short var6 = var1[var4];
         var0 += var6 * var6;
         var5 |= var0;
      }

      var0 |= -(var5 >>> 31);
      return (var0 & 4294967295L) <= l2bound[var2] ? 1 : 0;
   }
}
