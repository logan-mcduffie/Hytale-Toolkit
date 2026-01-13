package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Pack;

public class SecP224R1Field {
   private static final long M = 4294967295L;
   static final int[] P = new int[]{1, 0, 0, -1, -1, -1, -1};
   private static final int[] PExt = new int[]{1, 0, 0, -2, -1, -1, 0, 2, 0, 0, -2, -1, -1, -1};
   private static final int[] PExtInv = new int[]{-1, -1, -1, 1, 0, 0, -1, -3, -1, -1, 1};
   private static final int P6 = -1;
   private static final int PExt13 = -1;

   public static void add(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat224.add(var0, var1, var2);
      if (var3 != 0 || var2[6] == -1 && Nat224.gte(var2, P)) {
         addPInvTo(var2);
      }
   }

   public static void addExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.add(14, var0, var1, var2);
      if ((var3 != 0 || var2[13] == -1 && Nat.gte(14, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(14, var2, PExtInv.length);
      }
   }

   public static void addOne(int[] var0, int[] var1) {
      int var2 = Nat.inc(7, var0, var1);
      if (var2 != 0 || var1[6] == -1 && Nat224.gte(var1, P)) {
         addPInvTo(var1);
      }
   }

   public static int[] fromBigInteger(BigInteger var0) {
      int[] var1 = Nat224.fromBigInteger(var0);
      if (var1[6] == -1 && Nat224.gte(var1, P)) {
         Nat224.subFrom(P, var1);
      }

      return var1;
   }

   public static void half(int[] var0, int[] var1) {
      if ((var0[0] & 1) == 0) {
         Nat.shiftDownBit(7, var0, 0, var1);
      } else {
         int var2 = Nat224.add(var0, P, var1);
         Nat.shiftDownBit(7, var1, var2);
      }
   }

   public static void inv(int[] var0, int[] var1) {
      Mod.checkedModOddInverse(P, var0, var1);
   }

   public static int isZero(int[] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < 7; var2++) {
         var1 |= var0[var2];
      }

      var1 = var1 >>> 1 | var1 & 1;
      return var1 - 1 >> 31;
   }

   public static void multiply(int[] var0, int[] var1, int[] var2) {
      int[] var3 = Nat224.createExt();
      Nat224.mul(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat224.mulAddTo(var0, var1, var2);
      if ((var3 != 0 || var2[13] == -1 && Nat.gte(14, var2, PExt)) && Nat.addTo(PExtInv.length, PExtInv, var2) != 0) {
         Nat.incAt(14, var2, PExtInv.length);
      }
   }

   public static void negate(int[] var0, int[] var1) {
      if (0 != isZero(var0)) {
         Nat224.sub(P, P, var1);
      } else {
         Nat224.sub(P, var0, var1);
      }
   }

   public static void random(SecureRandom var0, int[] var1) {
      byte[] var2 = new byte[28];

      do {
         var0.nextBytes(var2);
         Pack.littleEndianToInt(var2, 0, var1, 0, 7);
      } while (0 == Nat.lessThan(7, var1, P));
   }

   public static void randomMult(SecureRandom var0, int[] var1) {
      do {
         random(var0, var1);
      } while (0 != isZero(var1));
   }

   public static void reduce(int[] var0, int[] var1) {
      long var2 = var0[10] & 4294967295L;
      long var4 = var0[11] & 4294967295L;
      long var6 = var0[12] & 4294967295L;
      long var8 = var0[13] & 4294967295L;
      long var10 = (var0[7] & 4294967295L) + var4 - 1L;
      long var12 = (var0[8] & 4294967295L) + var6;
      long var14 = (var0[9] & 4294967295L) + var8;
      long var16 = 0L;
      var16 += (var0[0] & 4294967295L) - var10;
      long var18 = var16 & 4294967295L;
      var16 >>= 32;
      var16 += (var0[1] & 4294967295L) - var12;
      var1[1] = (int)var16;
      var16 >>= 32;
      var16 += (var0[2] & 4294967295L) - var14;
      var1[2] = (int)var16;
      var16 >>= 32;
      var16 += (var0[3] & 4294967295L) + var10 - var2;
      long var20 = var16 & 4294967295L;
      var16 >>= 32;
      var16 += (var0[4] & 4294967295L) + var12 - var4;
      var1[4] = (int)var16;
      var16 >>= 32;
      var16 += (var0[5] & 4294967295L) + var14 - var6;
      var1[5] = (int)var16;
      var16 >>= 32;
      var16 += (var0[6] & 4294967295L) + var2 - var8;
      var1[6] = (int)var16;
      var16 >>= 32;
      var20 += ++var16;
      var18 -= var16;
      var1[0] = (int)var18;
      var16 = var18 >> 32;
      if (var16 != 0L) {
         var16 += var1[1] & 4294967295L;
         var1[1] = (int)var16;
         var16 >>= 32;
         var16 += var1[2] & 4294967295L;
         var1[2] = (int)var16;
         var20 += var16 >> 32;
      }

      var1[3] = (int)var20;
      var16 = var20 >> 32;
      if (var16 != 0L && Nat.incAt(7, var1, 4) != 0 || var1[6] == -1 && Nat224.gte(var1, P)) {
         addPInvTo(var1);
      }
   }

   public static void reduce32(int var0, int[] var1) {
      long var2 = 0L;
      if (var0 != 0) {
         long var4 = var0 & 4294967295L;
         var2 += (var1[0] & 4294967295L) - var4;
         var1[0] = (int)var2;
         var2 >>= 32;
         if (var2 != 0L) {
            var2 += var1[1] & 4294967295L;
            var1[1] = (int)var2;
            var2 >>= 32;
            var2 += var1[2] & 4294967295L;
            var1[2] = (int)var2;
            var2 >>= 32;
         }

         var2 += (var1[3] & 4294967295L) + var4;
         var1[3] = (int)var2;
         var2 >>= 32;
      }

      if (var2 != 0L && Nat.incAt(7, var1, 4) != 0 || var1[6] == -1 && Nat224.gte(var1, P)) {
         addPInvTo(var1);
      }
   }

   public static void square(int[] var0, int[] var1) {
      int[] var2 = Nat224.createExt();
      Nat224.square(var0, var2);
      reduce(var2, var1);
   }

   public static void squareN(int[] var0, int var1, int[] var2) {
      int[] var3 = Nat224.createExt();
      Nat224.square(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         Nat224.square(var2, var3);
         reduce(var3, var2);
      }
   }

   public static void subtract(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat224.sub(var0, var1, var2);
      if (var3 != 0) {
         subPInvFrom(var2);
      }
   }

   public static void subtractExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.sub(14, var0, var1, var2);
      if (var3 != 0 && Nat.subFrom(PExtInv.length, PExtInv, var2) != 0) {
         Nat.decAt(14, var2, PExtInv.length);
      }
   }

   public static void twice(int[] var0, int[] var1) {
      int var2 = Nat.shiftUpBit(7, var0, 0, var1);
      if (var2 != 0 || var1[6] == -1 && Nat224.gte(var1, P)) {
         addPInvTo(var1);
      }
   }

   private static void addPInvTo(int[] var0) {
      long var1 = (var0[0] & 4294967295L) - 1L;
      var0[0] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         var1 += var0[1] & 4294967295L;
         var0[1] = (int)var1;
         var1 >>= 32;
         var1 += var0[2] & 4294967295L;
         var0[2] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[3] & 4294967295L) + 1L;
      var0[3] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         Nat.incAt(7, var0, 4);
      }
   }

   private static void subPInvFrom(int[] var0) {
      long var1 = (var0[0] & 4294967295L) + 1L;
      var0[0] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         var1 += var0[1] & 4294967295L;
         var0[1] = (int)var1;
         var1 >>= 32;
         var1 += var0[2] & 4294967295L;
         var0[2] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[3] & 4294967295L) - 1L;
      var0[3] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         Nat.decAt(7, var0, 4);
      }
   }
}
