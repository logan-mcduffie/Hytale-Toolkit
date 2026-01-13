package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class TBCPadding implements BlockCipherPadding {
   @Override
   public void init(SecureRandom var1) throws IllegalArgumentException {
   }

   @Override
   public String getPaddingName() {
      return "TBC";
   }

   @Override
   public int addPadding(byte[] var1, int var2) {
      int var3 = var1.length - var2;
      byte var4;
      if (var2 > 0) {
         var4 = (byte)((var1[var2 - 1] & 1) == 0 ? 255 : 0);
      } else {
         var4 = (byte)((var1[var1.length - 1] & 1) == 0 ? 255 : 0);
      }

      while (var2 < var1.length) {
         var1[var2] = var4;
         var2++;
      }

      return var3;
   }

   @Override
   public int padCount(byte[] var1) throws InvalidCipherTextException {
      int var2 = var1.length;
      int var3 = var1[--var2] & 255;
      int var4 = 1;

      for (int var5 = -1; --var2 >= 0; var4 -= var5) {
         int var6 = var1[var2] & 255;
         int var7 = (var6 ^ var3) - 1 >> 31;
         var5 &= var7;
      }

      return var4;
   }
}
