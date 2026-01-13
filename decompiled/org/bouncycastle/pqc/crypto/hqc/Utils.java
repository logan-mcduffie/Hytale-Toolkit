package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.util.Pack;

class Utils {
   static void fromLongArrayToByteArray(byte[] var0, long[] var1) {
      int var2 = var0.length / 8;

      for (int var3 = 0; var3 != var2; var3++) {
         Pack.longToLittleEndian(var1[var3], var0, var3 * 8);
      }

      if (var0.length % 8 != 0) {
         int var5 = var2 * 8;
         int var4 = 0;

         while (var5 < var0.length) {
            var0[var5++] = (byte)(var1[var2] >>> var4++ * 8);
         }
      }
   }

   static void fromLongArrayToByteArray(byte[] var0, int var1, int var2, long[] var3) {
      int var4 = var2 >> 3;

      for (int var5 = 0; var5 != var4; var5++) {
         Pack.longToLittleEndian(var3[var5], var0, var1);
         var1 += 8;
      }

      if ((var2 & 7) != 0) {
         int var6 = 0;

         while (var1 < var0.length) {
            var0[var1++] = (byte)(var3[var4] >>> var6++ * 8);
         }
      }
   }

   static long bitMask(long var0, long var2) {
      return (1L << (int)(var0 % var2)) - 1L;
   }

   static void fromByteArrayToLongArray(long[] var0, byte[] var1, int var2, int var3) {
      byte[] var4 = var1;
      if (var3 % 8 != 0) {
         var4 = new byte[(var3 + 7) / 8 * 8];
         System.arraycopy(var1, var2, var4, 0, var3);
         var2 = 0;
      }

      int var5 = Math.min(var0.length, var3 + 7 >>> 3);

      for (int var6 = 0; var6 < var5; var6++) {
         var0[var6] = Pack.littleEndianToLong(var4, var2);
         var2 += 8;
      }
   }

   static void fromByte32ArrayToLongArray(long[] var0, int[] var1) {
      for (byte var2 = 0; var2 != var1.length; var2 += 2) {
         var0[var2 / 2] = var1[var2] & 4294967295L;
         var0[var2 / 2] = var0[var2 / 2] | (long)var1[var2 + 1] << 32;
      }
   }

   static void fromLongArrayToByte32Array(int[] var0, long[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         var0[2 * var2] = (int)var1[var2];
         var0[2 * var2 + 1] = (int)(var1[var2] >> 32);
      }
   }

   static void copyBytes(int[] var0, int var1, int[] var2, int var3, int var4) {
      System.arraycopy(var0, var1, var2, var3, var4 / 2);
   }

   static int getByteSizeFromBitSize(int var0) {
      return (var0 + 7) / 8;
   }

   static int getByte64SizeFromBitSize(int var0) {
      return (var0 + 63) / 64;
   }

   static int toUnsigned8bits(int var0) {
      return var0 & 0xFF;
   }

   static int toUnsigned16Bits(int var0) {
      return var0 & 65535;
   }
}
