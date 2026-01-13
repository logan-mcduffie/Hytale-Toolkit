package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat128;

public class SecT113Field {
   private static final long M49 = 562949953421311L;
   private static final long M57 = 144115188075855871L;

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
   }

   public static void addExt(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;
      var1[1] = var0[1];
   }

   private static void addTo(long[] var0, long[] var1) {
      var1[0] ^= var0[0];
      var1[1] ^= var0[1];
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(113, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat128.createExt64();
      Nat128.copy64(var0, var1);

      for (byte var3 = 1; var3 < 113; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat128.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat128.create64();
         long[] var3 = Nat128.create64();
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
         squareN(var3, 28, var2);
         multiply(var2, var3, var2);
         squareN(var2, 56, var3);
         multiply(var3, var2, var3);
         square(var3, var1);
      }
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[8];
      implMultiply(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[8];
      implMultiply(var0, var1, var3);
      addExt(var2, var3, var2);
   }

   public static void reduce(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      var4 ^= var8 << 15 ^ var8 << 24;
      var6 ^= var8 >>> 49 ^ var8 >>> 40;
      var2 ^= var6 << 15 ^ var6 << 24;
      var4 ^= var6 >>> 49 ^ var6 >>> 40;
      long var10 = var4 >>> 49;
      var1[0] = var2 ^ var10 ^ var10 << 9;
      var1[1] = var4 & 562949953421311L;
   }

   public static void reduce15(long[] var0, int var1) {
      long var2 = var0[var1 + 1];
      long var4 = var2 >>> 49;
      var0[var1] ^= var4 ^ var4 << 9;
      var0[var1 + 1] = var2 & 562949953421311L;
   }

   public static void sqrt(long[] var0, long[] var1) {
      long var2 = Interleave.unshuffle(var0[0]);
      long var4 = Interleave.unshuffle(var0[1]);
      long var6 = var2 & 4294967295L | var4 << 32;
      long var8 = var2 >>> 32 | var4 & -4294967296L;
      var1[0] = var6 ^ var8 << 57 ^ var8 << 5;
      var1[1] = var8 >>> 7 ^ var8 >>> 59;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat128.createExt64();
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat128.createExt64();
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat128.createExt64();
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

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var0[0];
      long var5 = var0[1];
      var5 = (var3 >>> 57 ^ var5 << 7) & 144115188075855871L;
      var3 &= 144115188075855871L;
      long var7 = var1[0];
      long var9 = var1[1];
      var9 = (var7 >>> 57 ^ var9 << 7) & 144115188075855871L;
      var7 &= 144115188075855871L;
      long[] var12 = new long[6];
      implMulw(var2, var3, var7, var12, 0);
      implMulw(var2, var5, var9, var12, 2);
      implMulw(var2, var3 ^ var5, var7 ^ var9, var12, 4);
      long var13 = var12[1] ^ var12[2];
      long var15 = var12[0];
      long var17 = var12[3];
      long var19 = var12[4] ^ var15 ^ var13;
      long var21 = var12[5] ^ var17 ^ var13;
      var2[0] = var15 ^ var19 << 57;
      var2[1] = var19 >>> 7 ^ var21 << 50;
      var2[2] = var21 >>> 14 ^ var17 << 43;
      var2[3] = var17 >>> 21;
   }

   protected static void implMulw(long[] var0, long var1, long var3, long[] var5, int var6) {
      var0[1] = var3;
      var0[2] = var0[1] << 1;
      var0[3] = var0[2] ^ var3;
      var0[4] = var0[2] << 1;
      var0[5] = var0[4] ^ var3;
      var0[6] = var0[3] << 1;
      var0[7] = var0[6] ^ var3;
      int var7 = (int)var1;
      long var10 = 0L;
      long var12 = var0[var7 & 7];
      byte var14 = 48;

      do {
         var7 = (int)(var1 >>> var14);
         long var8 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3 ^ var0[var7 >>> 6 & 7] << 6;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 9;
      } while (var14 > 0);

      var10 ^= (var1 & 72198606942111744L & var3 << 7 >> 63) >>> 8;
      var5[var6] = var12 & 144115188075855871L;
      var5[var6 + 1] = var12 >>> 57 ^ var10 << 7;
   }

   protected static void implSquare(long[] var0, long[] var1) {
      Interleave.expand64To128(var0, 0, 2, var1, 0);
   }
}
