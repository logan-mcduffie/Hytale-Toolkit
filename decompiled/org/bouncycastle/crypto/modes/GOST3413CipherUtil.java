package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Arrays;

class GOST3413CipherUtil {
   public static byte[] MSB(byte[] var0, int var1) {
      return Arrays.copyOf(var0, var1);
   }

   public static byte[] LSB(byte[] var0, int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(var0, var0.length - var1, var2, 0, var1);
      return var2;
   }

   public static byte[] sum(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = (byte)(var0[var3] ^ var1[var3]);
      }

      return var2;
   }

   public static byte[] copyFromInput(byte[] var0, int var1, int var2) {
      if (var0.length < var1 + var2) {
         var1 = var0.length - var2;
      }

      byte[] var3 = new byte[var1];
      System.arraycopy(var0, var2, var3, 0, var1);
      return var3;
   }
}
