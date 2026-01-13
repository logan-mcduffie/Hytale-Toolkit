package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat576 {
   public static void copy64(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
      var1[5] = var0[5];
      var1[6] = var0[6];
      var1[7] = var0[7];
      var1[8] = var0[8];
   }

   public static void copy64(long[] var0, int var1, long[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
      var2[var3 + 2] = var0[var1 + 2];
      var2[var3 + 3] = var0[var1 + 3];
      var2[var3 + 4] = var0[var1 + 4];
      var2[var3 + 5] = var0[var1 + 5];
      var2[var3 + 6] = var0[var1 + 6];
      var2[var3 + 7] = var0[var1 + 7];
      var2[var3 + 8] = var0[var1 + 8];
   }

   public static long[] create64() {
      return new long[9];
   }

   public static long[] createExt64() {
      return new long[18];
   }

   public static boolean eq64(long[] var0, long[] var1) {
      for (int var2 = 8; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static long[] fromBigInteger64(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 576) {
         long[] var1 = create64();

         for (int var2 = 0; var2 < 9; var2++) {
            var1[var2] = var0.longValue();
            var0 = var0.shiftRight(64);
         }

         return var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean isOne64(long[] var0) {
      if (var0[0] != 1L) {
         return false;
      } else {
         for (int var1 = 1; var1 < 9; var1++) {
            if (var0[var1] != 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isZero64(long[] var0) {
      for (int var1 = 0; var1 < 9; var1++) {
         if (var0[var1] != 0L) {
            return false;
         }
      }

      return true;
   }

   public static BigInteger toBigInteger64(long[] var0) {
      byte[] var1 = new byte[72];

      for (int var2 = 0; var2 < 9; var2++) {
         long var3 = var0[var2];
         if (var3 != 0L) {
            Pack.longToBigEndian(var3, var1, 8 - var2 << 3);
         }
      }

      return new BigInteger(1, var1);
   }
}
