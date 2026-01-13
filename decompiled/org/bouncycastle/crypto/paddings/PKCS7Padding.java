package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class PKCS7Padding implements BlockCipherPadding {
   @Override
   public void init(SecureRandom var1) throws IllegalArgumentException {
   }

   @Override
   public String getPaddingName() {
      return "PKCS7";
   }

   @Override
   public int addPadding(byte[] var1, int var2) {
      byte var3;
      for (var3 = (byte)(var1.length - var2); var2 < var1.length; var2++) {
         var1[var2] = var3;
      }

      return var3;
   }

   @Override
   public int padCount(byte[] var1) throws InvalidCipherTextException {
      byte var2 = var1[var1.length - 1];
      int var3 = var2 & 255;
      int var4 = var1.length - var3;
      int var5 = (var4 | var3 - 1) >> 31;

      for (int var6 = 0; var6 < var1.length; var6++) {
         var5 |= (var1[var6] ^ var2) & ~(var6 - var4 >> 31);
      }

      if (var5 != 0) {
         throw new InvalidCipherTextException("pad block corrupted");
      } else {
         return var3;
      }
   }
}
