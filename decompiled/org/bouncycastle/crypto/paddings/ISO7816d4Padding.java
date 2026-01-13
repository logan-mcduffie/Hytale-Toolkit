package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class ISO7816d4Padding implements BlockCipherPadding {
   @Override
   public void init(SecureRandom var1) throws IllegalArgumentException {
   }

   @Override
   public String getPaddingName() {
      return "ISO7816-4";
   }

   @Override
   public int addPadding(byte[] var1, int var2) {
      int var3 = var1.length - var2;
      var1[var2] = -128;
      var2++;

      while (var2 < var1.length) {
         var1[var2] = 0;
         var2++;
      }

      return var3;
   }

   @Override
   public int padCount(byte[] var1) throws InvalidCipherTextException {
      int var2 = -1;
      int var3 = -1;
      int var4 = var1.length;

      while (--var4 >= 0) {
         int var5 = var1[var4] & 255;
         int var6 = (var5 ^ 0) - 1 >> 31;
         int var7 = (var5 ^ 128) - 1 >> 31;
         var2 ^= (var4 ^ var2) & var3 & var7;
         var3 &= var6;
      }

      if (var2 < 0) {
         throw new InvalidCipherTextException("pad block corrupted");
      } else {
         return var1.length - var2;
      }
   }
}
