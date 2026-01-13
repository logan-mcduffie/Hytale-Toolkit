package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class FalconRNG {
   byte[] bd = new byte[512];
   int ptr = 0;
   byte[] sd = new byte[256];

   void prng_init(SHAKEDigest var1) {
      var1.doOutput(this.sd, 0, 56);
      this.prng_refill();
   }

   void prng_refill() {
      int[] var1 = new int[]{1634760805, 857760878, 2036477234, 1797285236};
      long var2 = Pack.littleEndianToLong(this.sd, 48);
      int[] var5 = new int[16];

      for (int var4 = 0; var4 < 8; var4++) {
         System.arraycopy(var1, 0, var5, 0, var1.length);
         Pack.littleEndianToInt(this.sd, 0, var5, 4, 12);
         var5[14] ^= (int)var2;
         var5[15] ^= (int)(var2 >>> 32);

         for (int var7 = 0; var7 < 10; var7++) {
            this.QROUND(0, 4, 8, 12, var5);
            this.QROUND(1, 5, 9, 13, var5);
            this.QROUND(2, 6, 10, 14, var5);
            this.QROUND(3, 7, 11, 15, var5);
            this.QROUND(0, 5, 10, 15, var5);
            this.QROUND(1, 6, 11, 12, var5);
            this.QROUND(2, 7, 8, 13, var5);
            this.QROUND(3, 4, 9, 14, var5);
         }

         for (int var6 = 0; var6 < 4; var6++) {
            var5[var6] += var1[var6];
         }

         for (int var8 = 4; var8 < 14; var8++) {
            var5[var8] += Pack.littleEndianToInt(this.sd, 4 * var8 - 16);
         }

         var5[14] += Pack.littleEndianToInt(this.sd, 40) ^ (int)var2;
         var5[15] += Pack.littleEndianToInt(this.sd, 44) ^ (int)(var2 >>> 32);
         var2++;

         for (int var9 = 0; var9 < 16; var9++) {
            Pack.intToLittleEndian(var5[var9], this.bd, (var4 << 2) + (var9 << 5));
         }
      }

      Pack.longToLittleEndian(var2, this.sd, 48);
      this.ptr = 0;
   }

   private void QROUND(int var1, int var2, int var3, int var4, int[] var5) {
      var5[var1] += var5[var2];
      var5[var4] ^= var5[var1];
      var5[var4] = var5[var4] << 16 | var5[var4] >>> 16;
      var5[var3] += var5[var4];
      var5[var2] ^= var5[var3];
      var5[var2] = var5[var2] << 12 | var5[var2] >>> 20;
      var5[var1] += var5[var2];
      var5[var4] ^= var5[var1];
      var5[var4] = var5[var4] << 8 | var5[var4] >>> 24;
      var5[var3] += var5[var4];
      var5[var2] ^= var5[var3];
      var5[var2] = var5[var2] << 7 | var5[var2] >>> 25;
   }

   long prng_get_u64() {
      int var1 = this.ptr;
      if (var1 >= this.bd.length - 9) {
         this.prng_refill();
         var1 = 0;
      }

      this.ptr = var1 + 8;
      return Pack.littleEndianToLong(this.bd, var1);
   }

   byte prng_get_u8() {
      byte var1 = this.bd[this.ptr++];
      if (this.ptr == this.bd.length) {
         this.prng_refill();
      }

      return var1;
   }
}
