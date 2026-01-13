package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecT233Field {
   private static final long M41 = 2199023255551L;
   private static final long M59 = 576460752303423487L;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
   }

   public static void addExt(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
      var2[4] = var0[4] ^ var1[4];
      var2[5] = var0[5] ^ var1[5];
      var2[6] = var0[6] ^ var1[6];
      var2[7] = var0[7] ^ var1[7];
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
   }

   private static void addTo(long[] var0, long[] var1) {
      var1[0] ^= var0[0];
      var1[1] ^= var0[1];
      var1[2] ^= var0[2];
      var1[3] ^= var0[3];
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(233, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat256.createExt64();
      Nat256.copy64(var0, var1);

      for (byte var3 = 1; var3 < 233; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat256.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat256.create64();
         long[] var3 = Nat256.create64();
         square(var0, var2);
         multiply(var2, var0, var2);
         square(var2, var2);
         multiply(var2, var0, var2);
         squareN(var2, 3, var3);
         multiply(var3, var2, var3);
         square(var3, var3);
         multiply(var3, var0, var3);
         squareN(var3, 7, var2);
         multiply(var2, var3, var2);
         squareN(var2, 14, var3);
         multiply(var3, var2, var3);
         square(var3, var3);
         multiply(var3, var0, var3);
         squareN(var3, 29, var2);
         multiply(var2, var3, var2);
         squareN(var2, 58, var3);
         multiply(var3, var2, var3);
         squareN(var3, 116, var2);
         multiply(var2, var3, var2);
         square(var2, var1);
      }
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat256.createExt64();
      implMultiply(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat256.createExt64();
      implMultiply(var0, var1, var3);
      addExt(var2, var3, var2);
   }

   public static void reduce(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var0[4];
      long var12 = var0[5];
      long var14 = var0[6];
      long var16 = var0[7];
      var8 ^= var16 << 23;
      var10 ^= var16 >>> 41 ^ var16 << 33;
      var12 ^= var16 >>> 31;
      var6 ^= var14 << 23;
      var8 ^= var14 >>> 41 ^ var14 << 33;
      var10 ^= var14 >>> 31;
      var4 ^= var12 << 23;
      var6 ^= var12 >>> 41 ^ var12 << 33;
      var8 ^= var12 >>> 31;
      var2 ^= var10 << 23;
      var4 ^= var10 >>> 41 ^ var10 << 33;
      var6 ^= var10 >>> 31;
      long var18 = var8 >>> 41;
      var1[0] = var2 ^ var18;
      var1[1] = var4 ^ var18 << 10;
      var1[2] = var6;
      var1[3] = var8 & 2199023255551L;
   }

   public static void reduce23(long[] var0, int var1) {
      long var2 = var0[var1 + 3];
      long var4 = var2 >>> 41;
      var0[var1] ^= var4;
      var0[var1 + 1] = var0[var1 + 1] ^ var4 << 10;
      var0[var1 + 3] = var2 & 2199023255551L;
   }

   public static void sqrt(long[] var0, long[] var1) {
      long var2 = Interleave.unshuffle(var0[0]);
      long var4 = Interleave.unshuffle(var0[1]);
      long var6 = var2 & 4294967295L | var4 << 32;
      long var8 = var2 >>> 32 | var4 & -4294967296L;
      var2 = Interleave.unshuffle(var0[2]);
      var4 = Interleave.unshuffle(var0[3]);
      long var10 = var2 & 4294967295L | var4 << 32;
      long var12 = var2 >>> 32 | var4 & -4294967296L;
      long var14 = var12 >>> 27;
      var12 ^= var8 >>> 27 | var12 << 37;
      var8 ^= var8 << 37;
      long[] var16 = Nat256.createExt64();
      int[] var17 = new int[]{32, 117, 191};

      for (int var18 = 0; var18 < var17.length; var18++) {
         int var19 = var17[var18] >>> 6;
         int var20 = var17[var18] & 63;
         var16[var19] ^= var8 << var20;
         var16[var19 + 1] = var16[var19 + 1] ^ (var12 << var20 | var8 >>> -var20);
         var16[var19 + 2] = var16[var19 + 2] ^ (var14 << var20 | var12 >>> -var20);
         var16[var19 + 3] = var16[var19 + 3] ^ var14 >>> -var20;
      }

      reduce(var16, var1);
      var1[0] ^= var6;
      var1[1] ^= var10;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat256.createExt64();
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat256.createExt64();
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat256.createExt64();
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)(var0[0] ^ var0[2] >>> 31) & 1;
   }

   protected static void implCompactExt(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var0[2];
      long var7 = var0[3];
      long var9 = var0[4];
      long var11 = var0[5];
      long var13 = var0[6];
      long var15 = var0[7];
      var0[0] = var1 ^ var3 << 59;
      var0[1] = var3 >>> 5 ^ var5 << 54;
      var0[2] = var5 >>> 10 ^ var7 << 49;
      var0[3] = var7 >>> 15 ^ var9 << 44;
      var0[4] = var9 >>> 20 ^ var11 << 39;
      var0[5] = var11 >>> 25 ^ var13 << 34;
      var0[6] = var13 >>> 30 ^ var15 << 29;
      var0[7] = var15 >>> 35;
   }

   protected static void implExpand(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      var1[0] = var2 & 576460752303423487L;
      var1[1] = (var2 >>> 59 ^ var4 << 5) & 576460752303423487L;
      var1[2] = (var4 >>> 54 ^ var6 << 10) & 576460752303423487L;
      var1[3] = var6 >>> 49 ^ var8 << 15;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[4];
      long[] var4 = new long[4];
      implExpand(var0, var3);
      implExpand(var1, var4);
      long[] var5 = new long[8];
      implMulwAcc(var5, var3[0], var4[0], var2, 0);
      implMulwAcc(var5, var3[1], var4[1], var2, 1);
      implMulwAcc(var5, var3[2], var4[2], var2, 2);
      implMulwAcc(var5, var3[3], var4[3], var2, 3);

      for (int var6 = 5; var6 > 0; var6--) {
         var2[var6] ^= var2[var6 - 1];
      }

      implMulwAcc(var5, var3[0] ^ var3[1], var4[0] ^ var4[1], var2, 1);
      implMulwAcc(var5, var3[2] ^ var3[3], var4[2] ^ var4[3], var2, 3);

      for (int var21 = 7; var21 > 1; var21--) {
         var2[var21] ^= var2[var21 - 2];
      }

      long var22 = var3[0] ^ var3[2];
      long var8 = var3[1] ^ var3[3];
      long var10 = var4[0] ^ var4[2];
      long var12 = var4[1] ^ var4[3];
      implMulwAcc(var5, var22 ^ var8, var10 ^ var12, var2, 3);
      long[] var14 = new long[3];
      implMulwAcc(var5, var22, var10, var14, 0);
      implMulwAcc(var5, var8, var12, var14, 1);
      long var15 = var14[0];
      long var17 = var14[1];
      long var19 = var14[2];
      var2[2] ^= var15;
      var2[3] ^= var15 ^ var17;
      var2[4] ^= var19 ^ var17;
      var2[5] ^= var19;
      implCompactExt(var2);
   }

   protected static void implMulwAcc(long[] var0, long var1, long var3, long[] var5, int var6) {
      var0[1] = var3;
      var0[2] = var0[1] << 1;
      var0[3] = var0[2] ^ var3;
      var0[4] = var0[2] << 1;
      var0[5] = var0[4] ^ var3;
      var0[6] = var0[3] << 1;
      var0[7] = var0[6] ^ var3;
      int var7 = (int)var1;
      long var10 = 0L;
      long var12 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3;
      byte var14 = 54;

      do {
         var7 = (int)(var1 >>> var14);
         long var8 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 6;
      } while (var14 > 0);

      var5[var6] ^= var12 & 576460752303423487L;
      var5[var6 + 1] = var5[var6 + 1] ^ var12 >>> 59 ^ var10 << 5;
   }

   protected static void implSquare(long[] var0, long[] var1) {
      Interleave.expand64To128(var0, 0, 4, var1, 0);
   }
}
