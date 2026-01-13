package org.bouncycastle.crypto.prng.drbg;

import java.util.Hashtable;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.util.Integers;

class Utils {
   static final Hashtable maxSecurityStrengths = new Hashtable();

   static int getMaxSecurityStrength(Digest var0) {
      return (Integer)maxSecurityStrengths.get(var0.getAlgorithmName());
   }

   static int getMaxSecurityStrength(Mac var0) {
      String var1 = var0.getAlgorithmName();
      return (Integer)maxSecurityStrengths.get(var1.substring(0, var1.indexOf("/")));
   }

   static byte[] hash_df(Digest var0, byte[] var1, int var2) {
      byte[] var3 = new byte[(var2 + 7) / 8];
      int var4 = var3.length / var0.getDigestSize();
      int var5 = 1;
      byte[] var6 = new byte[var0.getDigestSize()];

      for (int var7 = 0; var7 <= var4; var7++) {
         var0.update((byte)var5);
         var0.update((byte)(var2 >> 24));
         var0.update((byte)(var2 >> 16));
         var0.update((byte)(var2 >> 8));
         var0.update((byte)var2);
         var0.update(var1, 0, var1.length);
         var0.doFinal(var6, 0);
         int var8 = var3.length - var7 * var6.length > var6.length ? var6.length : var3.length - var7 * var6.length;
         System.arraycopy(var6, 0, var3, var7 * var6.length, var8);
         var5++;
      }

      if (var2 % 8 != 0) {
         int var11 = 8 - var2 % 8;
         int var12 = 0;

         for (int var9 = 0; var9 != var3.length; var9++) {
            int var10 = var3[var9] & 255;
            var3[var9] = (byte)(var10 >>> var11 | var12 << 8 - var11);
            var12 = var10;
         }
      }

      return var3;
   }

   static boolean isTooLarge(byte[] var0, int var1) {
      return var0 != null && var0.length > var1;
   }

   static {
      maxSecurityStrengths.put("SHA-1", Integers.valueOf(128));
      maxSecurityStrengths.put("SHA-224", Integers.valueOf(192));
      maxSecurityStrengths.put("SHA-256", Integers.valueOf(256));
      maxSecurityStrengths.put("SHA-384", Integers.valueOf(256));
      maxSecurityStrengths.put("SHA-512", Integers.valueOf(256));
      maxSecurityStrengths.put("SHA-512/224", Integers.valueOf(192));
      maxSecurityStrengths.put("SHA-512/256", Integers.valueOf(256));
   }
}
