package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

public class SecT571Field {
   private static final long M59 = 576460752303423487L;
   private static final long[] ROOT_Z = new long[]{
      3161836309350906777L,
      -7642453882179322845L,
      -3821226941089661423L,
      7312758566309945096L,
      -556661012383879292L,
      8945041530681231562L,
      -4750851271514160027L,
      6847946401097695794L,
      541669439031730457L
   };

   public static void add(long[] var0, long[] var1, long[] var2) {
      for (int var3 = 0; var3 < 9; var3++) {
         var2[var3] = var0[var3] ^ var1[var3];
      }
   }

   private static void add(long[] var0, int var1, long[] var2, int var3, long[] var4, int var5) {
      for (int var6 = 0; var6 < 9; var6++) {
         var4[var5 + var6] = var0[var1 + var6] ^ var2[var3 + var6];
      }
   }

   public static void addBothTo(long[] var0, long[] var1, long[] var2) {
      for (int var3 = 0; var3 < 9; var3++) {
         var2[var3] ^= var0[var3] ^ var1[var3];
      }
   }

   private static void addBothTo(long[] var0, int var1, long[] var2, int var3, long[] var4, int var5) {
      for (int var6 = 0; var6 < 9; var6++) {
         var4[var5 + var6] = var4[var5 + var6] ^ var0[var1 + var6] ^ var2[var3 + var6];
      }
   }

   public static void addExt(long[] var0, long[] var1, long[] var2) {
      for (int var3 = 0; var3 < 18; var3++) {
         var2[var3] = var0[var3] ^ var1[var3];
      }
   }

   public static void addOne(long[] var0, long[] var1) {
      var1[0] = var0[0] ^ 1L;

      for (int var2 = 1; var2 < 9; var2++) {
         var1[var2] = var0[var2];
      }
   }

   private static void addTo(long[] var0, long[] var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         var1[var2] ^= var0[var2];
      }
   }

   public static long[] fromBigInteger(BigInteger var0) {
      return Nat.fromBigInteger64(571, var0);
   }

   public static void halfTrace(long[] var0, long[] var1) {
      long[] var2 = Nat576.createExt64();
      Nat576.copy64(var0, var1);

      for (byte var3 = 1; var3 < 571; var3 += 2) {
         implSquare(var1, var2);
         reduce(var2, var1);
         implSquare(var1, var2);
         reduce(var2, var1);
         addTo(var0, var1);
      }
   }

   public static void invert(long[] var0, long[] var1) {
      if (Nat576.isZero64(var0)) {
         throw new IllegalStateException();
      } else {
         long[] var2 = Nat576.create64();
         long[] var3 = Nat576.create64();
         long[] var4 = Nat576.create64();
         square(var0, var4);
         square(var4, var2);
         square(var2, var3);
         multiply(var2, var3, var2);
         squareN(var2, 2, var3);
         multiply(var2, var3, var2);
         multiply(var2, var4, var2);
         squareN(var2, 5, var3);
         multiply(var2, var3, var2);
         squareN(var3, 5, var3);
         multiply(var2, var3, var2);
         squareN(var2, 15, var3);
         multiply(var2, var3, var4);
         squareN(var4, 30, var2);
         squareN(var2, 30, var3);
         multiply(var2, var3, var2);
         squareN(var2, 60, var3);
         multiply(var2, var3, var2);
         squareN(var3, 60, var3);
         multiply(var2, var3, var2);
         squareN(var2, 180, var3);
         multiply(var2, var3, var2);
         squareN(var3, 180, var3);
         multiply(var2, var3, var2);
         multiply(var2, var4, var1);
      }
   }

   public static void multiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat576.createExt64();
      implMultiply(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat576.createExt64();
      implMultiply(var0, var1, var3);
      addExt(var2, var3, var2);
   }

   public static void multiplyPrecomp(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat576.createExt64();
      implMultiplyPrecomp(var0, var1, var3);
      reduce(var3, var2);
   }

   public static void multiplyPrecompAddToExt(long[] var0, long[] var1, long[] var2) {
      long[] var3 = Nat576.createExt64();
      implMultiplyPrecomp(var0, var1, var3);
      addExt(var2, var3, var2);
   }

   public static long[] precompMultiplicand(long[] var0) {
      short var1 = 144;
      long[] var2 = new long[var1 << 1];
      System.arraycopy(var0, 0, var2, 9, 9);
      byte var3 = 0;

      for (int var4 = 7; var4 > 0; var4--) {
         var3 += 18;
         Nat.shiftUpBit64(9, var2, var3 >>> 1, 0L, var2, var3);
         reduce5(var2, var3);
         add(var2, 9, var2, var3, var2, var3 + 9);
      }

      Nat.shiftUpBits64(var1, var2, 0, 4, 0L, var2, var1);
      return var2;
   }

   public static void reduce(long[] var0, long[] var1) {
      long var2 = var0[9];
      long var4 = var0[17];
      var2 = var2 ^ var4 >>> 59 ^ var4 >>> 57 ^ var4 >>> 54 ^ var4 >>> 49;
      long var6 = var0[8] ^ var4 << 5 ^ var4 << 7 ^ var4 << 10 ^ var4 << 15;

      for (int var8 = 16; var8 >= 10; var8--) {
         var4 = var0[var8];
         var1[var8 - 8] = var6 ^ var4 >>> 59 ^ var4 >>> 57 ^ var4 >>> 54 ^ var4 >>> 49;
         var6 = var0[var8 - 9] ^ var4 << 5 ^ var4 << 7 ^ var4 << 10 ^ var4 << 15;
      }

      var1[1] = var6 ^ var2 >>> 59 ^ var2 >>> 57 ^ var2 >>> 54 ^ var2 >>> 49;
      var6 = var0[0] ^ var2 << 5 ^ var2 << 7 ^ var2 << 10 ^ var2 << 15;
      long var15 = var1[8];
      long var10 = var15 >>> 59;
      var1[0] = var6 ^ var10 ^ var10 << 2 ^ var10 << 5 ^ var10 << 10;
      var1[8] = var15 & 576460752303423487L;
   }

   public static void reduce5(long[] var0, int var1) {
      long var2 = var0[var1 + 8];
      long var4 = var2 >>> 59;
      var0[var1] ^= var4 ^ var4 << 2 ^ var4 << 5 ^ var4 << 10;
      var0[var1 + 8] = var2 & 576460752303423487L;
   }

   public static void sqrt(long[] var0, long[] var1) {
      long[] var2 = Nat576.create64();
      long[] var3 = Nat576.create64();
      int var4 = 0;

      for (int var5 = 0; var5 < 4; var5++) {
         long var6 = Interleave.unshuffle(var0[var4++]);
         long var8 = Interleave.unshuffle(var0[var4++]);
         var2[var5] = var6 & 4294967295L | var8 << 32;
         var3[var5] = var6 >>> 32 | var8 & -4294967296L;
      }

      long var11 = Interleave.unshuffle(var0[var4]);
      var2[4] = var11 & 4294967295L;
      var3[4] = var11 >>> 32;
      multiply(var3, ROOT_Z, var1);
      add(var1, var2, var1);
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = Nat576.createExt64();
      implSquare(var0, var2);
      reduce(var2, var1);
   }

   public static void squareAddToExt(long[] var0, long[] var1) {
      long[] var2 = Nat576.createExt64();
      implSquare(var0, var2);
      addExt(var1, var2, var1);
   }

   public static void squareN(long[] var0, int var1, long[] var2) {
      long[] var3 = Nat576.createExt64();
      implSquare(var0, var3);
      reduce(var3, var2);

      while (--var1 > 0) {
         implSquare(var2, var3);
         reduce(var3, var2);
      }
   }

   public static int trace(long[] var0) {
      return (int)(var0[0] ^ var0[8] >>> 49 ^ var0[8] >>> 57) & 1;
   }

   protected static void implMultiply(long[] var0, long[] var1, long[] var2) {
      long[] var3 = new long[16];

      for (int var4 = 0; var4 < 9; var4++) {
         implMulwAcc(var3, var0[var4], var1[var4], var2, var4 << 1);
      }

      long var10 = var2[0];
      long var6 = var2[1];
      var10 ^= var2[2];
      var2[1] = var10 ^ var6;
      var6 ^= var2[3];
      var10 ^= var2[4];
      var2[2] = var10 ^ var6;
      var6 ^= var2[5];
      var10 ^= var2[6];
      var2[3] = var10 ^ var6;
      var6 ^= var2[7];
      var10 ^= var2[8];
      var2[4] = var10 ^ var6;
      var6 ^= var2[9];
      var10 ^= var2[10];
      var2[5] = var10 ^ var6;
      var6 ^= var2[11];
      var10 ^= var2[12];
      var2[6] = var10 ^ var6;
      var6 ^= var2[13];
      var10 ^= var2[14];
      var2[7] = var10 ^ var6;
      var6 ^= var2[15];
      var10 ^= var2[16];
      var2[8] = var10 ^ var6;
      var6 ^= var2[17];
      long var8 = var10 ^ var6;
      var2[9] = var2[0] ^ var8;
      var2[10] = var2[1] ^ var8;
      var2[11] = var2[2] ^ var8;
      var2[12] = var2[3] ^ var8;
      var2[13] = var2[4] ^ var8;
      var2[14] = var2[5] ^ var8;
      var2[15] = var2[6] ^ var8;
      var2[16] = var2[7] ^ var8;
      var2[17] = var2[8] ^ var8;
      implMulwAcc(var3, var0[0] ^ var0[1], var1[0] ^ var1[1], var2, 1);
      implMulwAcc(var3, var0[0] ^ var0[2], var1[0] ^ var1[2], var2, 2);
      implMulwAcc(var3, var0[0] ^ var0[3], var1[0] ^ var1[3], var2, 3);
      implMulwAcc(var3, var0[1] ^ var0[2], var1[1] ^ var1[2], var2, 3);
      implMulwAcc(var3, var0[0] ^ var0[4], var1[0] ^ var1[4], var2, 4);
      implMulwAcc(var3, var0[1] ^ var0[3], var1[1] ^ var1[3], var2, 4);
      implMulwAcc(var3, var0[0] ^ var0[5], var1[0] ^ var1[5], var2, 5);
      implMulwAcc(var3, var0[1] ^ var0[4], var1[1] ^ var1[4], var2, 5);
      implMulwAcc(var3, var0[2] ^ var0[3], var1[2] ^ var1[3], var2, 5);
      implMulwAcc(var3, var0[0] ^ var0[6], var1[0] ^ var1[6], var2, 6);
      implMulwAcc(var3, var0[1] ^ var0[5], var1[1] ^ var1[5], var2, 6);
      implMulwAcc(var3, var0[2] ^ var0[4], var1[2] ^ var1[4], var2, 6);
      implMulwAcc(var3, var0[0] ^ var0[7], var1[0] ^ var1[7], var2, 7);
      implMulwAcc(var3, var0[1] ^ var0[6], var1[1] ^ var1[6], var2, 7);
      implMulwAcc(var3, var0[2] ^ var0[5], var1[2] ^ var1[5], var2, 7);
      implMulwAcc(var3, var0[3] ^ var0[4], var1[3] ^ var1[4], var2, 7);
      implMulwAcc(var3, var0[0] ^ var0[8], var1[0] ^ var1[8], var2, 8);
      implMulwAcc(var3, var0[1] ^ var0[7], var1[1] ^ var1[7], var2, 8);
      implMulwAcc(var3, var0[2] ^ var0[6], var1[2] ^ var1[6], var2, 8);
      implMulwAcc(var3, var0[3] ^ var0[5], var1[3] ^ var1[5], var2, 8);
      implMulwAcc(var3, var0[1] ^ var0[8], var1[1] ^ var1[8], var2, 9);
      implMulwAcc(var3, var0[2] ^ var0[7], var1[2] ^ var1[7], var2, 9);
      implMulwAcc(var3, var0[3] ^ var0[6], var1[3] ^ var1[6], var2, 9);
      implMulwAcc(var3, var0[4] ^ var0[5], var1[4] ^ var1[5], var2, 9);
      implMulwAcc(var3, var0[2] ^ var0[8], var1[2] ^ var1[8], var2, 10);
      implMulwAcc(var3, var0[3] ^ var0[7], var1[3] ^ var1[7], var2, 10);
      implMulwAcc(var3, var0[4] ^ var0[6], var1[4] ^ var1[6], var2, 10);
      implMulwAcc(var3, var0[3] ^ var0[8], var1[3] ^ var1[8], var2, 11);
      implMulwAcc(var3, var0[4] ^ var0[7], var1[4] ^ var1[7], var2, 11);
      implMulwAcc(var3, var0[5] ^ var0[6], var1[5] ^ var1[6], var2, 11);
      implMulwAcc(var3, var0[4] ^ var0[8], var1[4] ^ var1[8], var2, 12);
      implMulwAcc(var3, var0[5] ^ var0[7], var1[5] ^ var1[7], var2, 12);
      implMulwAcc(var3, var0[5] ^ var0[8], var1[5] ^ var1[8], var2, 13);
      implMulwAcc(var3, var0[6] ^ var0[7], var1[6] ^ var1[7], var2, 13);
      implMulwAcc(var3, var0[6] ^ var0[8], var1[6] ^ var1[8], var2, 14);
      implMulwAcc(var3, var0[7] ^ var0[8], var1[7] ^ var1[8], var2, 15);
   }

   protected static void implMultiplyPrecomp(long[] var0, long[] var1, long[] var2) {
      byte var3 = 15;

      for (byte var4 = 56; var4 >= 0; var4 -= 8) {
         for (byte var5 = 1; var5 < 9; var5 += 2) {
            int var6 = (int)(var0[var5] >>> var4);
            int var7 = var6 & var3;
            int var8 = var6 >>> 4 & var3;
            addBothTo(var1, 9 * var7, var1, 9 * (var8 + 16), var2, var5 - 1);
         }

         Nat.shiftUpBits64(16, var2, 0, 8, 0L);
      }

      for (byte var9 = 56; var9 >= 0; var9 -= 8) {
         for (byte var10 = 0; var10 < 9; var10 += 2) {
            int var11 = (int)(var0[var10] >>> var9);
            int var12 = var11 & var3;
            int var13 = var11 >>> 4 & var3;
            addBothTo(var1, 9 * var12, var1, 9 * (var13 + 16), var2, var10);
         }

         if (var9 > 0) {
            Nat.shiftUpBits64(18, var2, 0, 8, 0L);
         }
      }
   }

   protected static void implMulwAcc(long[] var0, long var1, long var3, long[] var5, int var6) {
      var0[1] = var3;

      for (byte var7 = 2; var7 < 16; var7 += 2) {
         var0[var7] = var0[var7 >>> 1] << 1;
         var0[var7 + 1] = var0[var7] ^ var3;
      }

      int var16 = (int)var1;
      long var10 = 0L;
      long var12 = var0[var16 & 15] ^ var0[var16 >>> 4 & 15] << 4;
      byte var14 = 56;

      do {
         var16 = (int)(var1 >>> var14);
         long var8 = var0[var16 & 15] ^ var0[var16 >>> 4 & 15] << 4;
         var12 ^= var8 << var14;
         var10 ^= var8 >>> -var14;
         var14 -= 8;
      } while (var14 > 0);

      for (int var15 = 0; var15 < 7; var15++) {
         var1 = (var1 & -72340172838076674L) >>> 1;
         var10 ^= var1 & var3 << var15 >> 63;
      }

      var5[var6] ^= var12;
      var5[var6 + 1] = var5[var6 + 1] ^ var10;
   }

   protected static void implSquare(long[] var0, long[] var1) {
      Interleave.expand64To128(var0, 0, 9, var1, 0);
   }
}
