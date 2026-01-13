package org.bouncycastle.math.raw;

import java.math.BigInteger;
import org.bouncycastle.util.Pack;

public abstract class Nat448 {
   public static void copy64(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
      var1[5] = var0[5];
      var1[6] = var0[6];
   }

   public static void copy64(long[] var0, int var1, long[] var2, int var3) {
      var2[var3 + 0] = var0[var1 + 0];
      var2[var3 + 1] = var0[var1 + 1];
      var2[var3 + 2] = var0[var1 + 2];
      var2[var3 + 3] = var0[var1 + 3];
      var2[var3 + 4] = var0[var1 + 4];
      var2[var3 + 5] = var0[var1 + 5];
      var2[var3 + 6] = var0[var1 + 6];
   }

   public static long[] create64() {
      return new long[7];
   }

   public static long[] createExt64() {
      return new long[14];
   }

   public static boolean eq64(long[] var0, long[] var1) {
      for (int var2 = 6; var2 >= 0; var2--) {
         if (var0[var2] != var1[var2]) {
            return false;
         }
      }

      return true;
   }

   public static long[] fromBigInteger64(BigInteger var0) {
      if (var0.signum() >= 0 && var0.bitLength() <= 448) {
         long[] var1 = create64();

         for (int var2 = 0; var2 < 7; var2++) {
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
         for (int var1 = 1; var1 < 7; var1++) {
            if (var0[var1] != 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isZero64(long[] var0) {
      for (int var1 = 0; var1 < 7; var1++) {
         if (var0[var1] != 0L) {
            return false;
         }
      }

      return true;
   }

   public static void mul(int[] var0, int[] var1, int[] var2) {
      Nat224.mul(var0, var1, var2);
      Nat224.mul(var0, 7, var1, 7, var2, 14);
      int var3 = Nat224.addToEachOther(var2, 7, var2, 14);
      int var4 = var3 + Nat224.addTo(var2, 0, var2, 7, 0);
      var3 += Nat224.addTo(var2, 21, var2, 14, var4);
      int[] var5 = Nat224.create();
      int[] var6 = Nat224.create();
      boolean var7 = Nat224.diff(var0, 7, var0, 0, var5, 0) != Nat224.diff(var1, 7, var1, 0, var6, 0);
      int[] var8 = Nat224.createExt();
      Nat224.mul(var5, var6, var8);
      var3 += var7 ? Nat.addTo(14, var8, 0, var2, 7) : Nat.subFrom(14, var8, 0, var2, 7);
      Nat.addWordAt(28, var3, var2, 21);
   }

   public static void square(int[] var0, int[] var1) {
      Nat224.square(var0, var1);
      Nat224.square(var0, 7, var1, 14);
      int var2 = Nat224.addToEachOther(var1, 7, var1, 14);
      int var3 = var2 + Nat224.addTo(var1, 0, var1, 7, 0);
      var2 += Nat224.addTo(var1, 21, var1, 14, var3);
      int[] var4 = Nat224.create();
      Nat224.diff(var0, 7, var0, 0, var4, 0);
      int[] var5 = Nat224.createExt();
      Nat224.square(var4, var5);
      var2 += Nat.subFrom(14, var5, 0, var1, 7);
      Nat.addWordAt(28, var2, var1, 21);
   }

   public static BigInteger toBigInteger64(long[] var0) {
      byte[] var1 = new byte[56];

      for (int var2 = 0; var2 < 7; var2++) {
         long var3 = var0[var2];
         if (var3 != 0L) {
            Pack.longToBigEndian(var3, var1, 6 - var2 << 3);
         }
      }

      return new BigInteger(1, var1);
   }
}
