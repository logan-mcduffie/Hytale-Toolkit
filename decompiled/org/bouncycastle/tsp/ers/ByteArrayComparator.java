package org.bouncycastle.tsp.ers;

import java.util.Comparator;

class ByteArrayComparator implements Comparator {
   @Override
   public int compare(Object var1, Object var2) {
      byte[] var3 = (byte[])var1;
      byte[] var4 = (byte[])var2;

      for (int var5 = 0; var5 < var3.length && var5 < var4.length; var5++) {
         int var6 = var3[var5] & 255;
         int var7 = var4[var5] & 255;
         if (var6 != var7) {
            return var6 - var7;
         }
      }

      return var3.length - var4.length;
   }
}
