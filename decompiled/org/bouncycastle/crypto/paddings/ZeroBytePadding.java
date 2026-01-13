package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class ZeroBytePadding implements BlockCipherPadding {
   @Override
   public void init(SecureRandom var1) throws IllegalArgumentException {
   }

   @Override
   public String getPaddingName() {
      return "ZeroByte";
   }

   @Override
   public int addPadding(byte[] var1, int var2) {
      int var3;
      for (var3 = var1.length - var2; var2 < var1.length; var2++) {
         var1[var2] = 0;
      }

      return var3;
   }

   @Override
   public int padCount(byte[] var1) throws InvalidCipherTextException {
      int var2 = 0;
      int var3 = -1;

      for (int var4 = var1.length; --var4 >= 0; var2 -= var3) {
         int var5 = var1[var4] & 255;
         int var6 = (var5 ^ 0) - 1 >> 31;
         var3 &= var6;
      }

      return var2;
   }
}
