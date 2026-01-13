package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecT163Field {
   private static final long M35 = 34359738367L;
   private static final long M55 = 36028797018963967L;
   private static final long[] ROOT_Z = new long[]{-5270498306774157648L, 5270498306774195053L, 19634136210L};

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
   }

   public static void addExt(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
      var2[4] = var0[4] ^ var1[4];
      var2[5] = var0[5] ^ var1[5];
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;
      var1[1] = var0[1];
      var1[2] = var0[2];
   }

   private static void addTo(long[] var0, long[] var1) {
      var1[0] ^= var0[0];
      var1[1] ^= var0[1];
      var1[2] ^= var0[2];
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(163, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat192.createExt64();
      Nat192.copy64(var0, var1);

      for (byte var3 = 1; var3 < 163; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat192.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat192.create64();
         long[] var3 = Nat192.create64();
         square(var0, var2);
         squareN(var2, 1, var3);
         multiply(var2, var3, var2);
         squareN(var3, 1, var3);
         multiply(var2, var3, var2);
         squareN(var2, 3, var3);
         multiply(var2, var3, var2);
         squareN(var3, 3, var3);
         multiply(var2, var3, var2);
         squareN(var2, 9, var3);
         multiply(var2, var3, var2);
         squareN(var3, 9, var3);
         multiply(var2, var3, var2);
         squareN(var2, 27, var3);
         multiply(var2, var3, var2);
         squareN(var3, 27, var3);
         multiply(var2, var3, var2);
         squareN(var2, 81, var3);
         multiply(var2, var3, var1);
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
      long var10 = var0[4];
      long var12 = var0[5];
      var6 ^= var12 << 29 ^ var12 << 32 ^ var12 << 35 ^ var12 << 36;
      var8 ^= var12 >>> 35 ^ var12 >>> 32 ^ var12 >>> 29 ^ var12 >>> 28;
      var4 ^= var10 << 29 ^ var10 << 32 ^ var10 << 35 ^ var10 << 36;
      var6 ^= var10 >>> 35 ^ var10 >>> 32 ^ var10 >>> 29 ^ var10 >>> 28;
      var2 ^= var8 << 29 ^ var8 << 32 ^ var8 << 35 ^ var8 << 36;
      var4 ^= var8 >>> 35 ^ var8 >>> 32 ^ var8 >>> 29 ^ var8 >>> 28;
      long var14 = var6 >>> 35;
      var1[0] = var2 ^ var14 ^ var14 << 3 ^ var14 << 6 ^ var14 << 7;
      var1[1] = var4;
      var1[2] = var6 & 34359738367L;
   }

   public static void reduce29(long[] var0, int var1) {
      long var2 = var0[var1 + 2];
      long var4 = var2 >>> 35;
      var0[var1] ^= var4 ^ var4 << 3 ^ var4 << 6 ^ var4 << 7;
      var0[var1 + 2] = var2 & 34359738367L;
   }

   public static void sqrt(long[] var0, long[] var1) {
      long[] var2 = Nat192.create64();
      long var3 = Interleave.unshuffle(var0[0]);
      long var5 = Interleave.unshuffle(var0[1]);
      long var7 = var3 & 4294967295L | var5 << 32;
      var2[0] = var3 >>> 32 | var5 & -4294967296L;
      var3 = Interleave.unshuffle(var0[2]);
      long var9 = var3 & 4294967295L;
      var2[1] = var3 >>> 32;
      multiply(var2, ROOT_Z, var1);
      var1[0] ^= var7;
      var1[1] ^= var9;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat192.createExt64();
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat192.createExt64();
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat192.createExt64();
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)(var0[0] ^ var0[2] >>> 29) & 1;
   }

   protected static void implCompactExt(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var0[2];
      long var7 = var0[3];
      long var9 = var0[4];
      long var11 = var0[5];
      var0[0] = var1 ^ var3 << 55;
      var0[1] = var3 >>> 9 ^ var5 << 46;
      var0[2] = var5 >>> 18 ^ var7 << 37;
      var0[3] = var7 >>> 27 ^ var9 << 28;
      var0[4] = var9 >>> 36 ^ var11 << 19;
      var0[5] = var11 >>> 45;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var0[0];
      long var5 = var0[1];
      long var7 = var0[2];
      var7 = var5 >>> 46 ^ var7 << 18;
      var5 = (var3 >>> 55 ^ var5 << 9) & 36028797018963967L;
      var3 &= 36028797018963967L;
      long var9 = var1[0];
      long var11 = var1[1];
      long var13 = var1[2];
      var13 = var11 >>> 46 ^ var13 << 18;
      var11 = (var9 >>> 55 ^ var11 << 9) & 36028797018963967L;
      var9 &= 36028797018963967L;
      long[] var16 = new long[10];
      implMulw(var2, var3, var9, var16, 0);
      implMulw(var2, var7, var13, var16, 2);
      long var17 = var3 ^ var5 ^ var7;
      long var19 = var9 ^ var11 ^ var13;
      implMulw(var2, var17, var19, var16, 4);
      long var21 = var5 << 1 ^ var7 << 2;
      long var23 = var11 << 1 ^ var13 << 2;
      implMulw(var2, var3 ^ var21, var9 ^ var23, var16, 6);
      implMulw(var2, var17 ^ var21, var19 ^ var23, var16, 8);
      long var25 = var16[6] ^ var16[8];
      long var27 = var16[7] ^ var16[9];
      long var29 = var25 << 1 ^ var16[6];
      long var31 = var25 ^ var27 << 1 ^ var16[7];
      long var35 = var16[0];
      long var37 = var16[1] ^ var16[0] ^ var16[4];
      long var39 = var16[1] ^ var16[5];
      long var41 = var35 ^ var29 ^ var16[2] << 4 ^ var16[2] << 1;
      long var43 = var37 ^ var31 ^ var16[3] << 4 ^ var16[3] << 1;
      long var45 = var39 ^ var27;
      var43 ^= var41 >>> 55;
      var41 &= 36028797018963967L;
      var45 ^= var43 >>> 55;
      var43 &= 36028797018963967L;
      var41 = var41 >>> 1 ^ (var43 & 1L) << 54;
      var43 = var43 >>> 1 ^ (var45 & 1L) << 54;
      var45 >>>= 1;
      var41 ^= var41 << 1;
      var41 ^= var41 << 2;
      var41 ^= var41 << 4;
      var41 ^= var41 << 8;
      var41 ^= var41 << 16;
      var41 ^= var41 << 32;
      var41 &= 36028797018963967L;
      var43 ^= var41 >>> 54;
      var43 ^= var43 << 1;
      var43 ^= var43 << 2;
      var43 ^= var43 << 4;
      var43 ^= var43 << 8;
      var43 ^= var43 << 16;
      var43 ^= var43 << 32;
      var43 &= 36028797018963967L;
      var45 ^= var43 >>> 54;
      var45 ^= var45 << 1;
      var45 ^= var45 << 2;
      var45 ^= var45 << 4;
      var45 ^= var45 << 8;
      var45 ^= var45 << 16;
      var45 ^= var45 << 32;
      var2[0] = var35;
      var2[1] = var37 ^ var41 ^ var16[2];
      var2[2] = var39 ^ var43 ^ var41 ^ var16[3];
      var2[3] = var45 ^ var43;
      var2[4] = var45 ^ var16[2];
      var2[5] = var16[3];
      implCompactExt(var2);
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
      long var12 = var0[var7 & 3];
      byte var14 = 47;

      do {
         var7 = (int)(var1 >>> var14);
         long var8 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3 ^ var0[var7 >>> 6 & 7] << 6;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 9;
      } while (var14 > 0);

      var5[var6] = var12 & 36028797018963967L;
      var5[var6 + 1] = var12 >>> 55 ^ var10 << 9;
   }

   protected static void implSquare(long[] var0, long[] var1) {
      Interleave.expand64To128(var0, 0, 3, var1, 0);
   }
}
