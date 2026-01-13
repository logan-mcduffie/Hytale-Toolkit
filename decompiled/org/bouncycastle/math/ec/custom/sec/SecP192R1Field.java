package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Pack;

public class SecP192R1Field {
   private static final long M = 4294967295L;
   static final int[] P = new int[]{-1, -1, -2, -1, -1, -1};
   private static final int[] PExt = new int[]{1, 0, 2, 0, 1, 0, -2, -1, -3, -1, -1, -1};
   private static final int[] PExtInv = new int[]{-1, -1, -3, -1, -2, -1, 1, 0, 2};
   private static final int P5 = -1;
   private static final int PExt11 = -1;

   public static void add(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat192.add(var0, var1, var2);
      if (var3 != 0 || var2[5] == -1 && Nat192.gte(var2, P)) {
         addPInvTo(var2);
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
         addPInvTo(var1);
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
      long var2 = var0[6] & 4294967295L;
      long var4 = var0[7] & 4294967295L;
      long var6 = var0[8] & 4294967295L;
      long var8 = var0[9] & 4294967295L;
      long var10 = var0[10] & 4294967295L;
      long var12 = var0[11] & 4294967295L;
      long var14 = var2 + var10;
      long var16 = var4 + var12;
      long var18 = 0L;
      var18 += (var0[0] & 4294967295L) + var14;
      int var20 = (int)var18;
      var18 >>= 32;
      var18 += (var0[1] & 4294967295L) + var16;
      var1[1] = (int)var18;
      var18 >>= 32;
      var14 += var6;
      var16 += var8;
      var18 += (var0[2] & 4294967295L) + var14;
      long var21 = var18 & 4294967295L;
      var18 >>= 32;
      var18 += (var0[3] & 4294967295L) + var16;
      var1[3] = (int)var18;
      var18 >>= 32;
      var14 -= var2;
      var16 -= var4;
      var18 += (var0[4] & 4294967295L) + var14;
      var1[4] = (int)var18;
      var18 >>= 32;
      var18 += (var0[5] & 4294967295L) + var16;
      var1[5] = (int)var18;
      var18 >>= 32;
      var21 += var18;
      var18 += var20 & 4294967295L;
      var1[0] = (int)var18;
      var18 >>= 32;
      if (var18 != 0L) {
         var18 += var1[1] & 4294967295L;
         var1[1] = (int)var18;
         var21 += var18 >> 32;
      }

      var1[2] = (int)var21;
      var18 = var21 >> 32;
      if (var18 != 0L && Nat.incAt(6, var1, 3) != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         addPInvTo(var1);
      }
   }

   public static void reduce32(int var0, int[] var1) {
      long var2 = 0L;
      if (var0 != 0) {
         long var4 = var0 & 4294967295L;
         var2 += (var1[0] & 4294967295L) + var4;
         var1[0] = (int)var2;
         var2 >>= 32;
         if (var2 != 0L) {
            var2 += var1[1] & 4294967295L;
            var1[1] = (int)var2;
            var2 >>= 32;
         }

         var2 += (var1[2] & 4294967295L) + var4;
         var1[2] = (int)var2;
         var2 >>= 32;
      }

      if (var2 != 0L && Nat.incAt(6, var1, 3) != 0 || var1[5] == -1 && Nat192.gte(var1, P)) {
         addPInvTo(var1);
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
         subPInvFrom(var2);
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
         addPInvTo(var1);
      }
   }

   private static void addPInvTo(int[] var0) {
      long var1 = (var0[0] & 4294967295L) + 1L;
      var0[0] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         var1 += var0[1] & 4294967295L;
         var0[1] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[2] & 4294967295L) + 1L;
      var0[2] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         Nat.incAt(6, var0, 3);
      }
   }

   private static void subPInvFrom(int[] var0) {
      long var1 = (var0[0] & 4294967295L) - 1L;
      var0[0] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         var1 += var0[1] & 4294967295L;
         var0[1] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[2] & 4294967295L) - 1L;
      var0[2] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         Nat.decAt(6, var0, 3);
      }
   }
}
