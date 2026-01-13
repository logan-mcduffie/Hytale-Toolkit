package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Arrays;

public class XofUtils {
   public static byte[] leftEncode(long var0) {
      byte var2 = 1;
      long var3 = var0;

      while ((var3 >>= 8) != 0L) {
         var2++;
      }

      byte[] var5 = new byte[var2 + 1];
      var5[0] = var2;

      for (int var6 = 1; var6 <= var2; var6++) {
         var5[var6] = (byte)(var0 >> 8 * (var2 - var6));
      }

      return var5;
   }

   public static byte[] rightEncode(long var0) {
      byte var2 = 1;
      long var3 = var0;

      while ((var3 >>= 8) != 0L) {
         var2++;
      }

      byte[] var5 = new byte[var2 + 1];
      var5[var2] = var2;

      for (int var6 = 0; var6 < var2; var6++) {
         var5[var6] = (byte)(var0 >> 8 * (var2 - var6 - 1));
      }

      return var5;
   }

   static byte[] encode(byte var0) {
      return Arrays.concatenate(leftEncode(8L), new byte[]{var0});
   }

   static byte[] encode(byte[] var0, int var1, int var2) {
      return var0.length == var2
         ? Arrays.concatenate(leftEncode(var2 * 8), var0)
         : Arrays.concatenate(leftEncode(var2 * 8), Arrays.copyOfRange(var0, var1, var1 + var2));
   }
}
