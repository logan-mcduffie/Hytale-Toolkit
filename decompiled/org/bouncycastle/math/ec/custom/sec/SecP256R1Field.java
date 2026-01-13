package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Pack;

public class SecP256R1Field {
   private static final long M = 4294967295L;
   static final int[] P = new int[]{-1, -1, -1, 0, 0, 0, 1, -1};
   private static final int[] PExt = new int[]{1, 0, 0, -2, -1, -1, -2, 1, -2, 1, -2, 1, 1, -2, 2, -2};
   private static final int P7 = -1;
   private static final int PExt15s1 = Integer.MAX_VALUE;

   public static void add(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat256.add(var0, var1, var2);
      if (var3 != 0 || var2[7] == -1 && Nat256.gte(var2, P)) {
         addPInvTo(var2);
      }
   }

   public static void addExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.add(16, var0, var1, var2);
      if (var3 != 0 || var2[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, var2, PExt)) {
         Nat.subFrom(16, PExt, var2);
      }
   }

   public static void addOne(int[] var0, int[] var1) {
      int var2 = Nat.inc(8, var0, var1);
      if (var2 != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
         addPInvTo(var1);
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
      if (var3 != 0 || var2[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, var2, PExt)) {
         Nat.subFrom(16, PExt, var2);
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
      long var2 = var0[8] & 4294967295L;
      long var4 = var0[9] & 4294967295L;
      long var6 = var0[10] & 4294967295L;
      long var8 = var0[11] & 4294967295L;
      long var10 = var0[12] & 4294967295L;
      long var12 = var0[13] & 4294967295L;
      long var14 = var0[14] & 4294967295L;
      long var16 = var0[15] & 4294967295L;
      var2 -= 6L;
      long var18 = var2 + var4;
      long var20 = var4 + var6;
      long var22 = var6 + var8 - var16;
      long var24 = var8 + var10;
      long var26 = var10 + var12;
      long var28 = var12 + var14;
      long var30 = var14 + var16;
      long var32 = var28 - var18;
      long var34 = 0L;
      var34 += (var0[0] & 4294967295L) - var24 - var32;
      var1[0] = (int)var34;
      var34 >>= 32;
      var34 += (var0[1] & 4294967295L) + var20 - var26 - var30;
      var1[1] = (int)var34;
      var34 >>= 32;
      var34 += (var0[2] & 4294967295L) + var22 - var28;
      var1[2] = (int)var34;
      var34 >>= 32;
      var34 += (var0[3] & 4294967295L) + (var24 << 1) + var32 - var30;
      var1[3] = (int)var34;
      var34 >>= 32;
      var34 += (var0[4] & 4294967295L) + (var26 << 1) + var14 - var20;
      var1[4] = (int)var34;
      var34 >>= 32;
      var34 += (var0[5] & 4294967295L) + (var28 << 1) - var22;
      var1[5] = (int)var34;
      var34 >>= 32;
      var34 += (var0[6] & 4294967295L) + (var30 << 1) + var32;
      var1[6] = (int)var34;
      var34 >>= 32;
      var34 += (var0[7] & 4294967295L) + (var16 << 1) + var2 - var22 - var26;
      var1[7] = (int)var34;
      var34 >>= 32;
      var34 += 6L;
      reduce32((int)var34, var1);
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
            var2 += var1[2] & 4294967295L;
            var1[2] = (int)var2;
            var2 >>= 32;
         }

         var2 += (var1[3] & 4294967295L) - var4;
         var1[3] = (int)var2;
         var2 >>= 32;
         if (var2 != 0L) {
            var2 += var1[4] & 4294967295L;
            var1[4] = (int)var2;
            var2 >>= 32;
            var2 += var1[5] & 4294967295L;
            var1[5] = (int)var2;
            var2 >>= 32;
         }

         var2 += (var1[6] & 4294967295L) - var4;
         var1[6] = (int)var2;
         var2 >>= 32;
         var2 += (var1[7] & 4294967295L) + var4;
         var1[7] = (int)var2;
         var2 >>= 32;
      }

      if (var2 != 0L || var1[7] == -1 && Nat256.gte(var1, P)) {
         addPInvTo(var1);
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
         subPInvFrom(var2);
      }
   }

   public static void subtractExt(int[] var0, int[] var1, int[] var2) {
      int var3 = Nat.sub(16, var0, var1, var2);
      if (var3 != 0) {
         Nat.addTo(16, PExt, var2);
      }
   }

   public static void twice(int[] var0, int[] var1) {
      int var2 = Nat.shiftUpBit(8, var0, 0, var1);
      if (var2 != 0 || var1[7] == -1 && Nat256.gte(var1, P)) {
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
         var1 += var0[2] & 4294967295L;
         var0[2] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[3] & 4294967295L) - 1L;
      var0[3] = (int)var1;
      var1 >>= 32;
      if (var1 != 0L) {
         var1 += var0[4] & 4294967295L;
         var0[4] = (int)var1;
         var1 >>= 32;
         var1 += var0[5] & 4294967295L;
         var0[5] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[6] & 4294967295L) - 1L;
      var0[6] = (int)var1;
      var1 >>= 32;
      var1 += (var0[7] & 4294967295L) + 1L;
      var0[7] = (int)var1;
   }

   private static void subPInvFrom(int[] var0) {
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
         var1 += var0[4] & 4294967295L;
         var0[4] = (int)var1;
         var1 >>= 32;
         var1 += var0[5] & 4294967295L;
         var0[5] = (int)var1;
         var1 >>= 32;
      }

      var1 += (var0[6] & 4294967295L) + 1L;
      var0[6] = (int)var1;
      var1 >>= 32;
      var1 += (var0[7] & 4294967295L) - 1L;
      var0[7] = (int)var1;
   }
}
