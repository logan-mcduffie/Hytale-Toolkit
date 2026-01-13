package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat320;

public class SecT283Field {
   private static final long M27 = 134217727L;
   private static final long M57 = 144115188075855871L;
   private static final long[] ROOT_Z = new long[]{878416384462358536L, 3513665537849438403L, -9076969306111048948L, 585610922974906400L, 34087042L};

   public static void add(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
      var2[4] = var0[4] ^ var1[4];
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
      var2[8] = var0[8] ^ var1[8];
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
      var1[4] = var0[4];
   }

   private static void addTo(long[] var0, long[] var1) {
      var1[0] ^= var0[0];
      var1[1] ^= var0[1];
      var1[2] ^= var0[2];
      var1[3] ^= var0[3];
      var1[4] ^= var0[4];
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(283, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(9);
      Nat320.copy64(var0, var1);

      for (byte var3 = 1; var3 < 283; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat320.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat320.create64();
         long[] var3 = Nat320.create64();
         square(var0, var2);
         multiply(var2, var0, var2);
         squareN(var2, 2, var3);
         multiply(var3, var2, var3);
         squareN(var3, 4, var2);
         multiply(var2, var3, var2);
         squareN(var2, 8, var3);
         multiply(var3, var2, var3);
         square(var3, var3);
         multiply(var3, var0, var3);
         squareN(var3, 17, var2);
         multiply(var2, var3, var2);
         square(var2, var2);
         multiply(var2, var0, var2);
         squareN(var2, 35, var3);
         multiply(var3, var2, var3);
         squareN(var3, 70, var2);
         multiply(var2, var3, var2);
         square(var2, var2);
         multiply(var2, var0, var2);
         squareN(var2, 141, var3);
         multiply(var3, var2, var3);
         square(var3, var1);
      }
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat320.createExt64();
      implMultiply(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat320.createExt64();
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
      long var18 = var0[8];
      var8 ^= var18 << 37 ^ var18 << 42 ^ var18 << 44 ^ var18 << 49;
      var10 ^= var18 >>> 27 ^ var18 >>> 22 ^ var18 >>> 20 ^ var18 >>> 15;
      var6 ^= var16 << 37 ^ var16 << 42 ^ var16 << 44 ^ var16 << 49;
      var8 ^= var16 >>> 27 ^ var16 >>> 22 ^ var16 >>> 20 ^ var16 >>> 15;
      var4 ^= var14 << 37 ^ var14 << 42 ^ var14 << 44 ^ var14 << 49;
      var6 ^= var14 >>> 27 ^ var14 >>> 22 ^ var14 >>> 20 ^ var14 >>> 15;
      var2 ^= var12 << 37 ^ var12 << 42 ^ var12 << 44 ^ var12 << 49;
      var4 ^= var12 >>> 27 ^ var12 >>> 22 ^ var12 >>> 20 ^ var12 >>> 15;
      long var20 = var10 >>> 27;
      var1[0] = var2 ^ var20 ^ var20 << 5 ^ var20 << 7 ^ var20 << 12;
      var1[1] = var4;
      var1[2] = var6;
      var1[3] = var8;
      var1[4] = var10 & 134217727L;
   }

   public static void reduce37(long[] var0, int var1) {
      long var2 = var0[var1 + 4];
      long var4 = var2 >>> 27;
      var0[var1] ^= var4 ^ var4 << 5 ^ var4 << 7 ^ var4 << 12;
      var0[var1 + 4] = var2 & 134217727L;
   }

   public static void sqrt(long[] var0, long[] var1) {
      long[] var2 = Nat320.create64();
      long var3 = Interleave.unshuffle(var0[0]);
      long var5 = Interleave.unshuffle(var0[1]);
      long var7 = var3 & 4294967295L | var5 << 32;
      var2[0] = var3 >>> 32 | var5 & -4294967296L;
      var3 = Interleave.unshuffle(var0[2]);
      var5 = Interleave.unshuffle(var0[3]);
      long var9 = var3 & 4294967295L | var5 << 32;
      var2[1] = var3 >>> 32 | var5 & -4294967296L;
      var3 = Interleave.unshuffle(var0[4]);
      long var11 = var3 & 4294967295L;
      var2[2] = var3 >>> 32;
      multiply(var2, ROOT_Z, var1);
      var1[0] ^= var7;
      var1[1] ^= var9;
      var1[2] ^= var11;
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(9);
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat.create64(9);
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat.create64(9);
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)(var0[0] ^ var0[4] >>> 15) & 1;
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
      var0[0] = var1 ^ var3 << 57;
      var0[1] = var3 >>> 7 ^ var5 << 50;
      var0[2] = var5 >>> 14 ^ var7 << 43;
      var0[3] = var7 >>> 21 ^ var9 << 36;
      var0[4] = var9 >>> 28 ^ var11 << 29;
      var0[5] = var11 >>> 35 ^ var13 << 22;
      var0[6] = var13 >>> 42 ^ var15 << 15;
      var0[7] = var15 >>> 49 ^ var17 << 8;
      var0[8] = var17 >>> 56 ^ var19 << 1;
      var0[9] = var19 >>> 63;
   }

   protected static void implExpand(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var0[2];
      long var8 = var0[3];
      long var10 = var0[4];
      var1[0] = var2 & 144115188075855871L;
      var1[1] = (var2 >>> 57 ^ var4 << 7) & 144115188075855871L;
      var1[2] = (var4 >>> 50 ^ var6 << 14) & 144115188075855871L;
      var1[3] = (var6 >>> 43 ^ var8 << 21) & 144115188075855871L;
      var1[4] = var8 >>> 36 ^ var10 << 28;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[5];
      long[] var4 = new long[5];
      implExpand(var0, var3);
      implExpand(var1, var4);
      long[] var6 = new long[26];
      implMulw(var2, var3[0], var4[0], var6, 0);
      implMulw(var2, var3[1], var4[1], var6, 2);
      implMulw(var2, var3[2], var4[2], var6, 4);
      implMulw(var2, var3[3], var4[3], var6, 6);
      implMulw(var2, var3[4], var4[4], var6, 8);
      long var7 = var3[0] ^ var3[1];
      long var9 = var4[0] ^ var4[1];
      long var11 = var3[0] ^ var3[2];
      long var13 = var4[0] ^ var4[2];
      long var15 = var3[2] ^ var3[4];
      long var17 = var4[2] ^ var4[4];
      long var19 = var3[3] ^ var3[4];
      long var21 = var4[3] ^ var4[4];
      implMulw(var2, var11 ^ var3[3], var13 ^ var4[3], var6, 18);
      implMulw(var2, var15 ^ var3[1], var17 ^ var4[1], var6, 20);
      long var23 = var7 ^ var19;
      long var25 = var9 ^ var21;
      long var27 = var23 ^ var3[2];
      long var29 = var25 ^ var4[2];
      implMulw(var2, var23, var25, var6, 22);
      implMulw(var2, var27, var29, var6, 24);
      implMulw(var2, var7, var9, var6, 10);
      implMulw(var2, var11, var13, var6, 12);
      implMulw(var2, var15, var17, var6, 14);
      implMulw(var2, var19, var21, var6, 16);
      var2[0] = var6[0];
      var2[9] = var6[9];
      long var31 = var6[0] ^ var6[1];
      long var33 = var31 ^ var6[2];
      long var35 = var33 ^ var6[10];
      var2[1] = var35;
      long var37 = var6[3] ^ var6[4];
      long var39 = var6[11] ^ var6[12];
      long var41 = var37 ^ var39;
      long var43 = var33 ^ var41;
      var2[2] = var43;
      long var45 = var31 ^ var37;
      long var47 = var6[5] ^ var6[6];
      long var49 = var45 ^ var47;
      long var51 = var49 ^ var6[8];
      long var53 = var6[13] ^ var6[14];
      long var55 = var51 ^ var53;
      long var57 = var6[18] ^ var6[22];
      long var59 = var57 ^ var6[24];
      long var61 = var55 ^ var59;
      var2[3] = var61;
      long var63 = var6[7] ^ var6[8];
      long var65 = var63 ^ var6[9];
      long var67 = var65 ^ var6[17];
      var2[8] = var67;
      long var69 = var65 ^ var47;
      long var71 = var6[15] ^ var6[16];
      long var73 = var69 ^ var71;
      var2[7] = var73;
      long var75 = var73 ^ var35;
      long var77 = var6[19] ^ var6[20];
      long var79 = var6[25] ^ var6[24];
      long var81 = var6[18] ^ var6[23];
      long var83 = var77 ^ var79;
      long var85 = var83 ^ var81;
      long var87 = var85 ^ var75;
      var2[4] = var87;
      long var89 = var43 ^ var67;
      long var91 = var83 ^ var89;
      long var93 = var6[21] ^ var6[22];
      long var95 = var91 ^ var93;
      var2[5] = var95;
      long var97 = var51 ^ var6[0];
      long var99 = var97 ^ var6[9];
      long var101 = var99 ^ var53;
      long var103 = var101 ^ var6[21];
      long var105 = var103 ^ var6[23];
      long var107 = var105 ^ var6[25];
      var2[6] = var107;
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
      Interleave.expand64To128(var0, 0, 4, var1, 0);
      var1[8] = Interleave.expand32to64((int)var0[4]);
   }
}
