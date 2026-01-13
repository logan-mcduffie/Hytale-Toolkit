package org.bouncycastle.crypto.prng;

public class EntropyUtil {
   public static byte[] generateSeed(EntropySource var0, int var1) {
      byte[] var2 = new byte[var1];
      if (var1 * 8 <= var0.entropySize()) {
         byte[] var3 = var0.getEntropy();
         System.arraycopy(var3, 0, var2, 0, var2.length);
      } else {
         int var6 = var0.entropySize() / 8;

         for (int var4 = 0; var4 < var2.length; var4 += var6) {
            byte[] var5 = var0.getEntropy();
            if (var5.length <= var2.length - var4) {
               System.arraycopy(var5, 0, var2, var4, var5.length);
            } else {
               System.arraycopy(var5, 0, var2, var4, var2.length - var4);
            }
         }
      }

      return var2;
   }
}
