package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecT131Field {
   private static final long M03 = 7L;
   private static final long M44 = 17592186044415L;
   private static final long[] ROOT_Z = new long[]{2791191049453778211L, 2791191049453778402L, 6L};

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
      return Nat.fromBigInteger64(131, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(5);
      Nat192.copy64(var0, var1);

      for (byte var3 = 1; var3 < 131; var3 += 2) {
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
         multiply(var2, var0, var2);
         squareN(var2, 2, var3);
         multiply(var3, var2, var3);
         squareN(var3, 4, var2);
         multiply(var2, var3, var2);
         squareN(var2, 8, var3);
         multiply(var3, var2, var3);
         squareN(var3, 16, var2);
         multiply(var2, var3, var2);
         squareN(var2, 32, var3);
         multiply(var3, var2, var3);
         square(var3, var3);
         multiply(var3, var0, var3);
         squareN(var3, 65, var2);
         multiply(var2, var3, var2);
         square(var2, var1);
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
      var4 ^= var10 << 61 ^ var10 << 63;
      var6 ^= var10 >>> 3 ^ var10 >>> 1 ^ var10 ^ var10 << 5;
      var8 ^= var10 >>> 59;
      var2 ^= var8 << 61 ^ var8 << 63;
      var4 ^= var8 >>> 3 ^ var8 >>> 1 ^ var8 ^ var8 << 5;
      var6 ^= var8 >>> 59;
      long var12 = var6 >>> 3;
      var1[0] = var2 ^ var12 ^ var12 << 2 ^ var12 << 3 ^ var12 << 8;
      var1[1] = var4 ^ var12 >>> 56;
      var1[2] = var6 & 7L;
   }

   public static void reduce61(long[] var0, int var1) {
      long var2 = var0[var1 + 2];
      long var4 = var2 >>> 3;
      var0[var1] ^= var4 ^ var4 << 2 ^ var4 << 3 ^ var4 << 8;
      var0[var1 + 1] = var0[var1 + 1] ^ var4 >>> 56;
      var0[var1 + 2] = var2 & 7L;
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
      long[] var2 = Nat.create64(5);
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(5);
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat.create64(5);
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)(var0[0] ^ var0[1] >>> 59 ^ var0[2] >>> 1) & 1;
   }

   protected static void implCompactExt(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var0[2];
      long var7 = var0[3];
      long var9 = var0[4];
      long var11 = var0[5];
      var0[0] = var1 ^ var3 << 44;
      var0[1] = var3 >>> 20 ^ var5 << 24;
      var0[2] = var5 >>> 40 ^ var7 << 4 ^ var9 << 48;
      var0[3] = var7 >>> 60 ^ var11 << 28 ^ var9 >>> 16;
      var0[4] = var11 >>> 36;
      var0[5] = 0L;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long var3 = var0[0];
      long var5 = var0[1];
      long var7 = var0[2];
      var7 = (var5 >>> 24 ^ var7 << 40) & 17592186044415L;
      var5 = (var3 >>> 44 ^ var5 << 20) & 17592186044415L;
      var3 &= 17592186044415L;
      long var9 = var1[0];
      long var11 = var1[1];
      long var13 = var1[2];
      var13 = (var11 >>> 24 ^ var13 << 40) & 17592186044415L;
      var11 = (var9 >>> 44 ^ var11 << 20) & 17592186044415L;
      var9 &= 17592186044415L;
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
      var43 ^= var41 >>> 44;
      var41 &= 17592186044415L;
      var45 ^= var43 >>> 44;
      var43 &= 17592186044415L;
      var41 = var41 >>> 1 ^ (var43 & 1L) << 43;
      var43 = var43 >>> 1 ^ (var45 & 1L) << 43;
      var45 >>>= 1;
      var41 ^= var41 << 1;
      var41 ^= var41 << 2;
      var41 ^= var41 << 4;
      var41 ^= var41 << 8;
      var41 ^= var41 << 16;
      var41 ^= var41 << 32;
      var41 &= 17592186044415L;
      var43 ^= var41 >>> 43;
      var43 ^= var43 << 1;
      var43 ^= var43 << 2;
      var43 ^= var43 << 4;
      var43 ^= var43 << 8;
      var43 ^= var43 << 16;
      var43 ^= var43 << 32;
      var43 &= 17592186044415L;
      var45 ^= var43 >>> 43;
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
      long var12 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3 ^ var0[var7 >>> 6 & 7] << 6 ^ var0[var7 >>> 9 & 7] << 9 ^ var0[var7 >>> 12 & 7] << 12;
      byte var14 = 30;

      do {
         var7 = (int)(var1 >>> var14);
         long var8 = var0[var7 & 7] ^ var0[var7 >>> 3 & 7] << 3 ^ var0[var7 >>> 6 & 7] << 6 ^ var0[var7 >>> 9 & 7] << 9 ^ var0[var7 >>> 12 & 7] << 12;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 15;
      } while (var14 > 0);

      var5[var6] = var12 & 17592186044415L;
      var5[var6 + 1] = var12 >>> 44 ^ var10 << 20;
   }

   protected static void implSquare(long[] var0, long[] var1) {
      Interleave.expand64To128(var0, 0, 2, var1, 0);
      var1[4] = Interleave.expand8to16((int)var0[2]) & 4294967295L;
   }
}
