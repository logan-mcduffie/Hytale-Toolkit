package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Pack;

public class SecP192K1Field {
   static final int[] P = new int[]{-4553, -2, -1, -1, -1, -1};
   private static final int[] PExt = new int[]{20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, -1, -1};
   private static final int[] PExtInv = new int[]{-20729809, -9107, -2, -1, -1, -1, 9105, 2};
   private static final int P5 = -1;
   private static final int PExt11 = -1;
   private static final int PInv33 = 4553;

   public static void add(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat192.add(var0, var1, var2);
      if (var3 != 0 || var2[5] == -1 && Nat192.gte(var2, P)) {
         Nat.add33To(6, 4553, var2);
      }
   }

   public static void addExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.add(12, var0, var1, var2);
      if ((var3 != 0 || var2[11] == -1 && Nat.gte(12, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(12, var2, PExtInv.length);
      }
   }

   public static void addOne(int[] var0, int[] var1) {
      int var2 = Nat.inc(6, var0, var1);
      if (var2 != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         Nat.add33To(6, 4553, var1);
      }
   }

   public static int[] fromBigInteger(BigInteger var0) {
      int[] var1 = Nat192.fromBigInteger(var0);
      if (var1[5] == -1 && Nat192.gte(var1, P)) {
         Nat192.subFrom(P, var1);
      }

      return var1;
   }

   public static void half(int[] var0, int[] var1) {
      if ((var0[0] & 1) == 0) {
         Nat.shiftDownBit(6, var0, 0, var1);
      } else {
         int var2 = Nat192.add(var0, P, var1);
         Nat.shiftDownBit(6, var1, var2);
      }
   }

   public static void inv(int[] var0, int[] var1) {
      Mod.checkedModOddInverse(P, var0, var1);
   }

   public static int isZero(int[] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < 6; var2++) {
         var1 |= var0[var2];
      }

      var1 = var1 >>> 1 | var1 & 1;
      return var1 - 1 >> 31;
   }

   public static void multiply(int[] var0, int[] var1, int[] var2) {
      int[] var3 = Nat192.createExt();
      Nat192.mul(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat192.mulAddTo(var0, var1, var2);
      if ((var3 != 0 || var2[11] == -1 && Nat.gte(12, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(12, var2, PExtInv.length);
      }
   }

   public static void negate(int[] var0, int[] var1) {
      if (0 != isZero(var0)) {
         Nat192.sub(P, P, var1);
      } else {
         Nat192.sub(P, var0, var1);
      }
   }

   public static void random(SecureRandom var0, int[] var1) {
      byte[] var2 = new byte[24];

      do {
         var0.nextBytes(var2);
         Pack.littleEndianToInt(var2, 0, var1, 0, 6);
      } while (0 == Nat.lessThan(6, var1, P));
   }

   public static void randomMult(SecureRandom var0, int[] var1) {
      do {
         random(var0, var1);
      } while (0 != isZero(var1));
   }

   public static void reduce(int[] var0, int[] var1) {
      long var2 = Nat192.mul33Add(4553, var0, 6, var0, 0, var1, 0);
      int var4 = Nat192.mul33DWordAdd(4553, var2, var1, 0);
      if (var4 != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         Nat.add33To(6, 4553, var1);
      }
   }

   public static void reduce32(int var0, int[] var1) {
      if (var0 != 0 && Nat192.mul33WordAdd(4553, var0, var1, 0) != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         Nat.add33To(6, 4553, var1);
      }
   }

   public static void square(int[] var0, int[] var1) {
      int[] var2 = Nat192.createExt();
      Nat192.square(var0, var2);
      reduce(var2, var1);
   }

   public static void squareN(int[] var0, int var1, int[] var2) {
      int[] var3 = Nat192.createExt();
      Nat192.square(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         Nat192.square(var2, var3);
         reduce(var3, var2);
      }
   }

   public static void subtract(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat192.sub(var0, var1, var2);
      if (var3 != 0) {
         Nat.sub33From(6, 4553, var2);
      }
   }

   public static void subtractExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.sub(12, var0, var1, var2);
      if (var3 != 0 && Nat.subFrom(PExtInv.length, PExtInv, var2) != 0) {
         Nat.decAt(12, var2, PExtInv.length);
      }
   }

   public static void twice(int[] var0, int[] var1) {
      int var2 = Nat.shiftUpBit(6, var0, 0, var1);
      if (var2 != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         Nat.add33To(6, 4553, var1);
      }
   }
}
