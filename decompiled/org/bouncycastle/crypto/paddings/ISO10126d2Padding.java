package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;

public class ISO10126d2Padding implements BlockCipherPadding {
   SecureRandom random;

   @Override
   public void init(SecureRandom var1) throws IllegalArgumentException {
      this.random = CryptoServicesRegistrar.getSecureRandom(var1);
   }

   @Override
   public String getPaddingName() {
      return "ISO10126-2";
   }

   @Override
   public int addPadding(byte[] var1, int var2) {
      byte var3;
      for (var3 = (byte)(var1.length - var2); var2 < var1.length - 1; var2++) {
         var1[var2] = (byte)this.random.nextInt();
      }

      var1[var2] = var3;
      return var3;
   }

   @Override
   public int padCount(byte[] var1) throws InvalidCipherTextException {
      int var2 = var1[var1.length - 1] & 255;
      int var3 = var1.length - var2;
      int var4 = (var3 | var2 - 1) >> 31;
      if (var4 != 0) {
         throw new InvalidCipherTextException("pad block corrupted");
      } else {
         return var2;
      }
   }
}
