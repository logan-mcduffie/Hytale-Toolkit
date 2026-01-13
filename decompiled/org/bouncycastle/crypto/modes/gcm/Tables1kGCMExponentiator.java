package org.bouncycastle.crypto.modes.gcm;

import java.util.ArrayList;
import java.util.List;

public class Tables1kGCMExponentiator implements GCMExponentiator {
   private List lookupPowX2;

   @Override
   public void init(byte[] var1) {
      long[] var2 = GCMUtil.asLongs(var1);
      if (this.lookupPowX2 == null || 0L == GCMUtil.areEqual(var2, (long[])this.lookupPowX2.get(0))) {
         this.lookupPowX2 = new ArrayList(8);
         this.lookupPowX2.add(var2);
      }
   }

   @Override
   public void exponentiateX(long var1, byte[] var3) {
      long[] var4 = GCMUtil.oneAsLongs();
      int var5 = 0;

      while (var1 > 0L) {
         if ((var1 & 1L) != 0L) {
            GCMUtil.multiply(var4, this.getPowX2(var5));
         }

         var5++;
         var1 >>>= 1;
      }

      GCMUtil.asBytes(var4, var3);
   }

   private long[] getPowX2(int var1) {
      int var2 = this.lookupPowX2.size() - 1;
      if (var2 < var1) {
         long[] var3 = (long[])this.lookupPowX2.get(var2);

         do {
            long[] var4 = new long[2];
            GCMUtil.square(var3, var4);
            this.lookupPowX2.add(var4);
            var3 = var4;
         } while (++var2 < var1);
      }

      return (long[])this.lookupPowX2.get(var1);
   }
}
