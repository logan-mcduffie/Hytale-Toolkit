package org.bouncycastle.crypto.modes.gcm;

public class BasicGCMExponentiator implements GCMExponentiator {
   private long[] x;

   @Override
   public void init(byte[] var1) {
      this.x = GCMUtil.asLongs(var1);
   }

   @Override
   public void exponentiateX(long var1, byte[] var3) {
      long[] var4 = GCMUtil.oneAsLongs();
      if (var1 > 0L) {
         long[] var5 = new long[2];
         GCMUtil.copy(this.x, var5);

         do {
            if ((var1 & 1L) != 0L) {
               GCMUtil.multiply(var4, var5);
            }

            GCMUtil.square(var5, var5);
            var1 >>>= 1;
         } while (var1 > 0L);
      }

      GCMUtil.asBytes(var4, var3);
   }
}
