package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Integers;

class Utils {
   protected static int numBytes(int var0) {
      return var0 == 0 ? 0 : (var0 - 1) / 8 + 1;
   }

   protected static int ceil_log2(int var0) {
      return var0 == 0 ? 0 : 32 - nlz(var0 - 1);
   }

   private static int nlz(int var0) {
      if (var0 == 0) {
         return 32;
      } else {
         byte var1 = 1;
         if (var0 >>> 16 == 0) {
            var1 += 16;
            var0 <<= 16;
         }

         if (var0 >>> 24 == 0) {
            var1 += 8;
            var0 <<= 8;
         }

         if (var0 >>> 28 == 0) {
            var1 += 4;
            var0 <<= 4;
         }

         if (var0 >>> 30 == 0) {
            var1 += 2;
            var0 <<= 2;
         }

         return var1 - (var0 >>> 31);
      }
   }

   protected static int parity(byte[] var0, int var1) {
      byte var2 = var0[0];

      for (int var3 = 1; var3 < var1; var3++) {
         var2 ^= var0[var3];
      }

      return Integers.bitCount(var2 & 0xFF) & 1;
   }

   protected static int parity16(int var0) {
      return Integers.bitCount(var0 & 65535) & 1;
   }

   protected static int parity32(int var0) {
      return Integers.bitCount(var0) & 1;
   }

   protected static void setBitInWordArray(int[] var0, int var1, int var2) {
      setBit(var0, var1, var2);
   }

   protected static int getBitFromWordArray(int[] var0, int var1) {
      return getBit(var0, var1);
   }

   protected static byte getBit(byte[] var0, int var1) {
      int var2 = var1 >>> 3;
      int var3 = var1 & 7 ^ 7;
      return (byte)(var0[var2] >>> var3 & 1);
   }

   protected static byte getCrumbAligned(byte[] var0, int var1) {
      int var2 = var1 >>> 2;
      int var3 = var1 << 1 & 6 ^ 6;
      int var4 = var0[var2] >>> var3;
      return (byte)((var4 & 1) << 1 | (var4 & 2) >> 1);
   }

   protected static int getBit(int var0, int var1) {
      int var2 = var1 ^ 7;
      return var0 >>> var2 & 1;
   }

   protected static int getBit(int[] var0, int var1) {
      int var2 = var1 >>> 5;
      int var3 = var1 & 31 ^ 7;
      return var0[var2] >>> var3 & 1;
   }

   protected static void setBit(byte[] var0, int var1, byte var2) {
      int var3 = var1 >>> 3;
      int var4 = var1 & 7 ^ 7;
      int var5 = var0[var3];
      var5 &= ~(1 << var4);
      var5 |= var2 << var4;
      var0[var3] = (byte)var5;
   }

   protected static int setBit(int var0, int var1, int var2) {
      int var3 = var1 ^ 7;
      var0 &= ~(1 << var3);
      return var0 | var2 << var3;
   }

   protected static void setBit(int[] var0, int var1, int var2) {
      int var3 = var1 >>> 5;
      int var4 = var1 & 31 ^ 7;
      int var5 = var0[var3];
      var5 &= ~(1 << var4);
      var5 |= var2 << var4;
      var0[var3] = var5;
   }

   protected static void zeroTrailingBits(int[] var0, int var1) {
      int var2 = var1 & 31;
      if (var2 != 0) {
         var0[var1 >>> 5] = var0[var1 >>> 5] & getTrailingBitsMask(var1);
      }
   }

   protected static int getTrailingBitsMask(int var0) {
      int var1 = var0 & -8;
      int var2 = ~(-1 << var1);
      int var3 = var0 & 7;
      if (var3 != 0) {
         var2 ^= (65280 >>> var3 & 0xFF) << var1;
      }

      return var2;
   }
}
