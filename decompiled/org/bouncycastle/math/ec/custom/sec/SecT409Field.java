package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;

public class SecT409Field {
   private static final long M25 = 33554431L;
   private static final long M59 = 576460752303423487L;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
      var2[4] = var0[4] ^ var1[4];
      var2[5] = var0[5] ^ var1[5];
      var2[6] = var0[6] ^ var1[6];
   }

   public static void addExt(long[] var0, long[] var1, long[] var2) {
      for (int var3 = 0; var3 < 13; var3++) {
         var2[var3] = var0[var3] ^ var1[var3];
      }
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
      var1[5] = var0[5];
      var1[6] = var0[6];
   }

   private static void addTo(long[] var0, long[] var1) {
      var1[0] ^= var0[0];
      var1[1] ^= var0[1];
      var1[2] ^= var0[2];
      var1[3] ^= var0[3];
      var1[4] ^= var0[4];
      var1[5] ^= var0[5];
      var1[6] ^= var0[6];
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(409, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(13);
      Nat448.copy64(var0, var1);

      for (byte var3 = 1; var3 < 409; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat448.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat448.create64();
         long[] var3 = Nat448.create64();
         long[] var4 = Nat448.create64();
         square(var0, var2);
         squareN(var2, 1, var3);
         multiply(var2, var3, var2);
         squareN(var3, 1, var3);
         multiply(var2, var3, var2);
         squareN(var2, 3, var3);
         multiply(var2, var3, var2);
         squareN(var2, 6, var3);
         multiply(var2, var3, var2);
         squareN(var2, 12, var3);
         multiply(var2, var3, var4);
         squareN(var4, 24, var2);
         squareN(var2, 24, var3);
         multiply(var2, var3, var2);
         squareN(var2, 48, var3);
         multiply(var2, var3, var2);
         squareN(var2, 96, var3);
         multiply(var2, var3, var2);
         squareN(var2, 192, var3);
         multiply(var2, var3, var2);
         multiply(var2, var4, var1);
      }
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat448.createExt64();
      implMultiply(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat448.createExt64();
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
      long var18 = var0[12];
      var12 ^= var18 << 39;
      var14 ^= var18 >>> 25 ^ var18 << 62;
      var16 ^= var18 >>> 2;
      var18 = var0[11];
      var10 ^= var18 << 39;
      var12 ^= var18 >>> 25 ^ var18 << 62;
      var14 ^= var18 >>> 2;
      var18 = var0[10];
      var8 ^= var18 << 39;
      var10 ^= var18 >>> 25 ^ var18 << 62;
      var12 ^= var18 >>> 2;
      var18 = var0[9];
      var6 ^= var18 << 39;
      var8 ^= var18 >>> 25 ^ var18 << 62;
      var10 ^= var18 >>> 2;
      var18 = var0[8];
      var4 ^= var18 << 39;
      var6 ^= var18 >>> 25 ^ var18 << 62;
      var8 ^= var18 >>> 2;
      var2 ^= var16 << 39;
      var4 ^= var16 >>> 25 ^ var16 << 62;
      var6 ^= var16 >>> 2;
      long var20 = var14 >>> 25;
      var1[0] = var2 ^ var20;
      var1[1] = var4 ^ var20 << 23;
      var1[2] = var6;
      var1[3] = var8;
      var1[4] = var10;
      var1[5] = var12;
      var1[6] = var14 & 33554431L;
   }

   public static void reduce39(long[] var0, int var1) {
      long var2 = var0[var1 + 6];
      long var4 = var2 >>> 25;
      var0[var1] ^= var4;
      var0[var1 + 1] = var0[var1 + 1] ^ var4 << 23;
      var0[var1 + 6] = var2 & 33554431L;
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
      var2 = Interleave.unshuffle(var0[4]);
      var4 = Interleave.unshuffle(var0[5]);
      long var14 = var2 & 4294967295L | var4 << 32;
      long var16 = var2 >>> 32 | var4 & -4294967296L;
      var2 = Interleave.unshuffle(var0[6]);
      long var18 = var2 & 4294967295L;
      long var20 = var2 >>> 32;
      var1[0] = var6 ^ var8 << 44;
      var1[1] = var10 ^ var12 << 44 ^ var8 >>> 20;
      var1[2] = var14 ^ var16 << 44 ^ var12 >>> 20;
      var1[3] = var18 ^ var20 << 44 ^ var16 >>> 20 ^ var8 << 13;
      var1[4] = var20 >>> 20 ^ var12 << 13 ^ var8 >>> 51;
      var1[5] = var16 << 13 ^ var12 >>> 51;
      var1[6] = var20 << 13 ^ var16 >>> 51;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(13);
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(13);
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat.create64(13);
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)var0[0] & 1;
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
      long var17 = var0[8];
      long var19 = var0[9];
      long var21 = var0[10];
      long var23 = var0[11];
      long var25 = var0[12];
      long var27 = var0[13];
      var0[0] = var1 ^ var3 << 59;
      var0[1] = var3 >>> 5 ^ var5 << 54;
      var0[2] = var5 >>> 10 ^ var7 << 49;
      var0[3] = var7 >>> 15 ^ var9 << 44;
      var0[4] = var9 >>> 20 ^ var11 << 39;
      var0[5] = var11 >>> 25 ^ var13 << 34;
      var0[6] = var13 >>> 30 ^ var15 << 29;
      var0[7] = var15 >>> 35 ^ var17 << 24;
      var0[8] = var17 >>> 40 ^ var19 << 19;
      var0[9] = var19 >>> 45 ^ var21 << 14;
      var0[10] = var21 >>> 50 ^ var23 << 9;
      var0[11] = var23 >>> 55 ^ var25 << 4 ^ var27 << 63;
      var0[12] = var27 >>> 1;
   }

   protected static void implExpand(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var0[4];
      long var12 = var0[5];
      long var14 = var0[6];
      var1[0] = var2 & 576460752303423487L;
      var1[1] = (var2 >>> 59 ^ var4 << 5) & 576460752303423487L;
      var1[2] = (var4 >>> 54 ^ var6 << 10) & 576460752303423487L;
      var1[3] = (var6 >>> 49 ^ var8 << 15) & 576460752303423487L;
      var1[4] = (var8 >>> 44 ^ var10 << 20) & 576460752303423487L;
      var1[5] = (var10 >>> 39 ^ var12 << 25) & 576460752303423487L;
      var1[6] = var12 >>> 34 ^ var14 << 30;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[7];
      long[] var4 = new long[7];
      implExpand(var0, var3);
      implExpand(var1, var4);
      long[] var5 = new long[8];

      for (int var6 = 0; var6 < 7; var6++) {
         implMulwAcc(var5, var3[var6], var4[var6], var2, var6 << 1);
      }

      long var12 = var2[0];
      long var8 = var2[1];
      var12 ^= var2[2];
      var2[1] = var12 ^ var8;
      var8 ^= var2[3];
      var12 ^= var2[4];
      var2[2] = var12 ^ var8;
      var8 ^= var2[5];
      var12 ^= var2[6];
      var2[3] = var12 ^ var8;
      var8 ^= var2[7];
      var12 ^= var2[8];
      var2[4] = var12 ^ var8;
      var8 ^= var2[9];
      var12 ^= var2[10];
      var2[5] = var12 ^ var8;
      var8 ^= var2[11];
      var12 ^= var2[12];
      var2[6] = var12 ^ var8;
      var8 ^= var2[13];
      long var10 = var12 ^ var8;
      var2[7] = var2[0] ^ var10;
      var2[8] = var2[1] ^ var10;
      var2[9] = var2[2] ^ var10;
      var2[10] = var2[3] ^ var10;
      var2[11] = var2[4] ^ var10;
      var2[12] = var2[5] ^ var10;
      var2[13] = var2[6] ^ var10;
      implMulwAcc(var5, var3[0] ^ var3[1], var4[0] ^ var4[1], var2, 1);
      implMulwAcc(var5, var3[0] ^ var3[2], var4[0] ^ var4[2], var2, 2);
      implMulwAcc(var5, var3[0] ^ var3[3], var4[0] ^ var4[3], var2, 3);
      implMulwAcc(var5, var3[1] ^ var3[2], var4[1] ^ var4[2], var2, 3);
      implMulwAcc(var5, var3[0] ^ var3[4], var4[0] ^ var4[4], var2, 4);
      implMulwAcc(var5, var3[1] ^ var3[3], var4[1] ^ var4[3], var2, 4);
      implMulwAcc(var5, var3[0] ^ var3[5], var4[0] ^ var4[5], var2, 5);
      implMulwAcc(var5, var3[1] ^ var3[4], var4[1] ^ var4[4], var2, 5);
      implMulwAcc(var5, var3[2] ^ var3[3], var4[2] ^ var4[3], var2, 5);
      implMulwAcc(var5, var3[0] ^ var3[6], var4[0] ^ var4[6], var2, 6);
      implMulwAcc(var5, var3[1] ^ var3[5], var4[1] ^ var4[5], var2, 6);
      implMulwAcc(var5, var3[2] ^ var3[4], var4[2] ^ var4[4], var2, 6);
      implMulwAcc(var5, var3[1] ^ var3[6], var4[1] ^ var4[6], var2, 7);
      implMulwAcc(var5, var3[2] ^ var3[5], var4[2] ^ var4[5], var2, 7);
      implMulwAcc(var5, var3[3] ^ var3[4], var4[3] ^ var4[4], var2, 7);
      implMulwAcc(var5, var3[2] ^ var3[6], var4[2] ^ var4[6], var2, 8);
      implMulwAcc(var5, var3[3] ^ var3[5], var4[3] ^ var4[5], var2, 8);
      implMulwAcc(var5, var3[3] ^ var3[6], var4[3] ^ var4[6], var2, 9);
      implMulwAcc(var5, var3[4] ^ var3[5], var4[4] ^ var4[5], var2, 9);
      implMulwAcc(var5, var3[4] ^ var3[6], var4[4] ^ var4[6], var2, 10);
      implMulwAcc(var5, var3[5] ^ var3[6], var4[5] ^ var4[6], var2, 11);
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
      Interleave.expand64To128(var0, 0, 6, var1, 0);
      var1[12] = Interleave.expand32to64((int)var0[6]);
   }
}
