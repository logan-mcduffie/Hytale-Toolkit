package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

public class SecP256K1Field {
   static final int[] P = new int[]{-977, -2, -1, -1, -1, -1, -1, -1};
   private static final int[] PExt = new int[]{954529, 1954, 1, 0, 0, 0, 0, 0, -1954, -3, -1, -1, -1, -1, -1, -1};
   private static final int[] PExtInv = new int[]{-954529, -1955, -2, -1, -1, -1, -1, -1, 1953, 2};
   private static final int P7 = -1;
   private static final int PExt15 = -1;
   private static final int PInv33 = 977;

   public static void add(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat256.add(var0, var1, var2);
      if (var3 != 0 || var2[7] == -1 && Nat256.gte(var2, P)) {
         Nat.add33To(8, 977, var2);
      }
   }

   public static void addExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.add(16, var0, var1, var2);
      if ((var3 != 0 || var2[15] == -1 && Nat.gte(16, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(16, var2, PExtInv.length);
      }
   }

   public static void addOne(int[] var0, int[] var1) {
      int var2 = Nat.inc(8, var0, var1);
      if (var2 != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
         Nat.add33To(8, 977, var1);
      }
   }

   public static int[] fromBigInteger(BigInteger var0) {
      int[] var1 = Nat256.fromBigInteger(var0);
      if (var1[7] == -1 && Nat256.gte(var1, P)) {
         Nat256.subFrom(P, var1);
      }

      return var1;
   }

   public static void half(int[] var0, int[] var1) {
      if ((var0[0] & 1) == 0) {
         Nat.shiftDownBit(8, var0, 0, var1);
      } else {
         int var2 = Nat256.add(var0, P, var1);
         Nat.shiftDownBit(8, var1, var2);
      }
   }

   public static void inv(int[] var0, int[] var1) {
      Mod.checkedModOddInverse(P, var0, var1);
   }

   public static int isZero(int[] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < 8; var2++) {
         var1 |= var0[var2];
      }

      var1 = var1 >>> 1 | var1 & 1;
      return var1 - 1 >> 31;
   }

   public static void multiply(int[] var0, int[] var1, int[] var2) {
      int[] var3 = Nat256.createExt();
      Nat256.mul(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiply(int[] var0, int[] var1, int[] var2, int[] var3) {
      Nat256.mul(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat256.mulAddTo(var0, var1, var2);
      if ((var3 != 0 || var2[15] == -1 && Nat.gte(16, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(16, var2, PExtInv.length);
      }
   }

   public static void negate(int[] var0, int[] var1) {
      if (0 != isZero(var0)) {
         Nat256.sub(P, P, var1);
      } else {
         Nat256.sub(P, var0, var1);
      }
   }

   public static void random(SecureRandom var0, int[] var1) {
      byte[] var2 = new byte[32];

      do {
         var0.nextBytes(var2);
         Pack.littleEndianToInt(var2, 0, var1, 0, 8);
      } while (0 == Nat.lessThan(8, var1, P));
   }

   public static void randomMult(SecureRandom var0, int[] var1) {
      do {
         random(var0, var1);
      } while (0 != isZero(var1));
   }

   public static void reduce(int[] var0, int[] var1) {
      long var2 = Nat256.mul33Add(977, var0, 8, var0, 0, var1, 0);
      int var4 = Nat256.mul33DWordAdd(977, var2, var1, 0);
      if (var4 != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
         Nat.add33To(8, 977, var1);
      }
   }

   public static void reduce32(int var0, int[] var1) {
      if (var0 != 0 && Nat256.mul33WordAdd(977, var0, var1, 0) != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
         Nat.add33To(8, 977, var1);
      }
   }

   public static void square(int[] var0, int[] var1) {
      int[] var2 = Nat256.createExt();
      Nat256.square(var0, var2);
      reduce(var2, var1);
   }

   public static void square(int[] var0, int[] var1, int[] var2) {
      Nat256.square(var0, var2);
      reduce(var2, var1);
   }

   public static void squareN(int[] var0, int var1, int[] var2) {
      int[] var3 = Nat256.createExt();
      Nat256.square(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         Nat256.square(var2, var3);
         reduce(var3, var2);
      }
   }

   public static void squareN(int[] var0, int var1, int[] var2, int[] var3) {
      Nat256.square(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         Nat256.square(var2, var3);
         reduce(var3, var2);
      }
   }

   public static void subtract(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat256.sub(var0, var1, var2);
      if (var3 != 0) {
         Nat.sub33From(8, 977, var2);
      }
   }

   public static void subtractExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.sub(16, var0, var1, var2);
      if (var3 != 0 && Nat.subFrom(PExtInv.length, PExtInv, var2) != 0) {
         Nat.decAt(16, var2, PExtInv.length);
      }
   }

   public static void twice(int[] var0, int[] var1) {
      int var2 = Nat.shiftUpBit(8, var0, 0, var1);
      if (var2 != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
         Nat.add33To(8, 977, var1);
      }
   }
}
