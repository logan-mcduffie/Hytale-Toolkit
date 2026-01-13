package org.bouncycastle.math.ec.rfc8032;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

abstract class Scalar25519 {
   static final int SIZE = 8;
   private static final int SCALAR_BYTES = 32;
   private static final long M08L = 255L;
   private static final long M28L = 268435455L;
   private static final long M32L = 4294967295L;
   private static final int TARGET_LENGTH = 254;
   private static final int[] L = new int[]{1559614445, 1477600026, -1560830762, 350157278, 0, 0, 0, 268435456};
   private static final int[] LSq = new int[]{
      -1424848535,
      -487721339,
      580428573,
      1745064566,
      -770181698,
      1036971123,
      461123738,
      -1582065343,
      1268693629,
      -889041821,
      -731974758,
      43769659,
      0,
      0,
      0,
      16777216
   };
   private static final int L0 = -50998291;
   private static final int L1 = 19280294;
   private static final int L2 = 127719000;
   private static final int L3 = -6428113;
   private static final int L4 = 5343;

   static boolean checkVar(byte[] var0, int[] var1) {
      decode(var0, var1);
      return !Nat256.gte(var1, L);
   }

   static void decode(byte[] var0, int[] var1) {
      Codec.decode32(var0, 0, var1, 0, 8);
   }

   static void getOrderWnafVar(int var0, byte[] var1) {
      Wnaf.getSignedVar(L, var0, var1);
   }

   static void multiply128Var(int[] var0, int[] var1, int[] var2) {
      int[] var3 = new int[12];
      Nat256.mul128(var0, var1, var3);
      if (var1[3] < 0) {
         Nat256.addTo(L, 0, var3, 4, 0);
         Nat256.subFrom(var0, 0, var3, 4, 0);
      }

      byte[] var4 = new byte[48];
      Codec.encode32(var3, 0, 12, var4, 0);
      byte[] var5 = reduce384(var4);
      decode(var5, var2);
   }

   static byte[] reduce384(byte[] var0) {
      long var1 = Codec.decode32(var0, 0) & 4294967295L;
      long var3 = Codec.decode24(var0, 4) << 4 & 4294967295L;
      long var5 = Codec.decode32(var0, 7) & 4294967295L;
      long var7 = Codec.decode24(var0, 11) << 4 & 4294967295L;
      long var9 = Codec.decode32(var0, 14) & 4294967295L;
      long var11 = Codec.decode24(var0, 18) << 4 & 4294967295L;
      long var13 = Codec.decode32(var0, 21) & 4294967295L;
      long var15 = Codec.decode24(var0, 25) << 4 & 4294967295L;
      long var17 = Codec.decode32(var0, 28) & 4294967295L;
      long var19 = Codec.decode24(var0, 32) << 4 & 4294967295L;
      long var21 = Codec.decode32(var0, 35) & 4294967295L;
      long var23 = Codec.decode24(var0, 39) << 4 & 4294967295L;
      long var25 = Codec.decode32(var0, 42) & 4294967295L;
      long var27 = Codec.decode16(var0, 46) << 4 & 4294967295L;
      var27 += var25 >> 28;
      var25 &= 268435455L;
      var9 -= var27 * -50998291L;
      var11 -= var27 * 19280294L;
      var13 -= var27 * 127719000L;
      var15 -= var27 * -6428113L;
      var17 -= var27 * 5343L;
      var25 += var23 >> 28;
      var23 &= 268435455L;
      var7 -= var25 * -50998291L;
      var9 -= var25 * 19280294L;
      var11 -= var25 * 127719000L;
      var13 -= var25 * -6428113L;
      var15 -= var25 * 5343L;
      var23 += var21 >> 28;
      var21 &= 268435455L;
      var5 -= var23 * -50998291L;
      var7 -= var23 * 19280294L;
      var9 -= var23 * 127719000L;
      var11 -= var23 * -6428113L;
      var13 -= var23 * 5343L;
      var21 += var19 >> 28;
      var19 &= 268435455L;
      var3 -= var21 * -50998291L;
      var5 -= var21 * 19280294L;
      var7 -= var21 * 127719000L;
      var9 -= var21 * -6428113L;
      var11 -= var21 * 5343L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 += var17 >> 28;
      var17 &= 268435455L;
      long var29 = var17 >>> 27;
      var19 += var29;
      var1 -= var19 * -50998291L;
      var3 -= var19 * 19280294L;
      var5 -= var19 * 127719000L;
      var7 -= var19 * -6428113L;
      var9 -= var19 * 5343L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 = var17 >> 28;
      var17 &= 268435455L;
      var19 -= var29;
      var1 += var19 & -50998291L;
      var3 += var19 & 19280294L;
      var5 += var19 & 127719000L;
      var7 += var19 & -6428113L;
      var9 += var19 & 5343L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      byte[] var31 = new byte[64];
      Codec.encode56(var1 | var3 << 28, var31, 0);
      Codec.encode56(var5 | var7 << 28, var31, 7);
      Codec.encode56(var9 | var11 << 28, var31, 14);
      Codec.encode56(var13 | var15 << 28, var31, 21);
      Codec.encode32((int)var17, var31, 28);
      return var31;
   }

   static byte[] reduce512(byte[] var0) {
      long var1 = Codec.decode32(var0, 0) & 4294967295L;
      long var3 = Codec.decode24(var0, 4) << 4 & 4294967295L;
      long var5 = Codec.decode32(var0, 7) & 4294967295L;
      long var7 = Codec.decode24(var0, 11) << 4 & 4294967295L;
      long var9 = Codec.decode32(var0, 14) & 4294967295L;
      long var11 = Codec.decode24(var0, 18) << 4 & 4294967295L;
      long var13 = Codec.decode32(var0, 21) & 4294967295L;
      long var15 = Codec.decode24(var0, 25) << 4 & 4294967295L;
      long var17 = Codec.decode32(var0, 28) & 4294967295L;
      long var19 = Codec.decode24(var0, 32) << 4 & 4294967295L;
      long var21 = Codec.decode32(var0, 35) & 4294967295L;
      long var23 = Codec.decode24(var0, 39) << 4 & 4294967295L;
      long var25 = Codec.decode32(var0, 42) & 4294967295L;
      long var27 = Codec.decode24(var0, 46) << 4 & 4294967295L;
      long var29 = Codec.decode32(var0, 49) & 4294967295L;
      long var31 = Codec.decode24(var0, 53) << 4 & 4294967295L;
      long var33 = Codec.decode32(var0, 56) & 4294967295L;
      long var35 = Codec.decode24(var0, 60) << 4 & 4294967295L;
      long var37 = var0[63] & 255L;
      var19 -= var37 * -50998291L;
      var21 -= var37 * 19280294L;
      var23 -= var37 * 127719000L;
      var25 -= var37 * -6428113L;
      var27 -= var37 * 5343L;
      var35 += var33 >> 28;
      var33 &= 268435455L;
      var17 -= var35 * -50998291L;
      var19 -= var35 * 19280294L;
      var21 -= var35 * 127719000L;
      var23 -= var35 * -6428113L;
      var25 -= var35 * 5343L;
      var15 -= var33 * -50998291L;
      var17 -= var33 * 19280294L;
      var19 -= var33 * 127719000L;
      var21 -= var33 * -6428113L;
      var23 -= var33 * 5343L;
      var31 += var29 >> 28;
      var29 &= 268435455L;
      var13 -= var31 * -50998291L;
      var15 -= var31 * 19280294L;
      var17 -= var31 * 127719000L;
      var19 -= var31 * -6428113L;
      var21 -= var31 * 5343L;
      var11 -= var29 * -50998291L;
      var13 -= var29 * 19280294L;
      var15 -= var29 * 127719000L;
      var17 -= var29 * -6428113L;
      var19 -= var29 * 5343L;
      var27 += var25 >> 28;
      var25 &= 268435455L;
      var9 -= var27 * -50998291L;
      var11 -= var27 * 19280294L;
      var13 -= var27 * 127719000L;
      var15 -= var27 * -6428113L;
      var17 -= var27 * 5343L;
      var25 += var23 >> 28;
      var23 &= 268435455L;
      var7 -= var25 * -50998291L;
      var9 -= var25 * 19280294L;
      var11 -= var25 * 127719000L;
      var13 -= var25 * -6428113L;
      var15 -= var25 * 5343L;
      var23 += var21 >> 28;
      var21 &= 268435455L;
      var5 -= var23 * -50998291L;
      var7 -= var23 * 19280294L;
      var9 -= var23 * 127719000L;
      var11 -= var23 * -6428113L;
      var13 -= var23 * 5343L;
      var21 += var19 >> 28;
      var19 &= 268435455L;
      var3 -= var21 * -50998291L;
      var5 -= var21 * 19280294L;
      var7 -= var21 * 127719000L;
      var9 -= var21 * -6428113L;
      var11 -= var21 * 5343L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 += var17 >> 28;
      var17 &= 268435455L;
      long var39 = var17 >>> 27;
      var19 += var39;
      var1 -= var19 * -50998291L;
      var3 -= var19 * 19280294L;
      var5 -= var19 * 127719000L;
      var7 -= var19 * -6428113L;
      var9 -= var19 * 5343L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      var19 = var17 >> 28;
      var17 &= 268435455L;
      var19 -= var39;
      var1 += var19 & -50998291L;
      var3 += var19 & 19280294L;
      var5 += var19 & 127719000L;
      var7 += var19 & -6428113L;
      var9 += var19 & 5343L;
      var3 += var1 >> 28;
      var1 &= 268435455L;
      var5 += var3 >> 28;
      var3 &= 268435455L;
      var7 += var5 >> 28;
      var5 &= 268435455L;
      var9 += var7 >> 28;
      var7 &= 268435455L;
      var11 += var9 >> 28;
      var9 &= 268435455L;
      var13 += var11 >> 28;
      var11 &= 268435455L;
      var15 += var13 >> 28;
      var13 &= 268435455L;
      var17 += var15 >> 28;
      var15 &= 268435455L;
      byte[] var41 = new byte[32];
      Codec.encode56(var1 | var3 << 28, var41, 0);
      Codec.encode56(var5 | var7 << 28, var41, 7);
      Codec.encode56(var9 | var11 << 28, var41, 14);
      Codec.encode56(var13 | var15 << 28, var41, 21);
      Codec.encode32((int)var17, var41, 28);
      return var41;
   }

   static boolean reduceBasisVar(int[] var0, int[] var1, int[] var2) {
      int[] var3 = new int[16];
      System.arraycopy(LSq, 0, var3, 0, 16);
      int[] var4 = new int[16];
      Nat256.square(var0, var4);
      var4[0]++;
      int[] var5 = new int[16];
      Nat256.mul(L, var0, var5);
      int[] var6 = new int[16];
      int[] var7 = new int[4];
      System.arraycopy(L, 0, var7, 0, 4);
      int[] var8 = new int[4];
      int[] var9 = new int[4];
      System.arraycopy(var0, 0, var9, 0, 4);
      int[] var10 = new int[4];
      var10[0] = 1;
      int var11 = 1016;
      int var12 = 15;
      int var13 = ScalarUtil.getBitLengthPositive(var12, var4);

      while (var13 > 254) {
         if (--var11 < 0) {
            return false;
         }

         int var14 = ScalarUtil.getBitLength(var12, var5);
         int var15 = var14 - var13;
         var15 &= ~(var15 >> 31);
         if (var5[var12] < 0) {
            ScalarUtil.addShifted_NP(var12, var15, var3, var4, var5, var6);
            ScalarUtil.addShifted_UV(3, var15, var7, var8, var9, var10);
         } else {
            ScalarUtil.subShifted_NP(var12, var15, var3, var4, var5, var6);
            ScalarUtil.subShifted_UV(3, var15, var7, var8, var9, var10);
         }

         if (ScalarUtil.lessThan(var12, var3, var4)) {
            int[] var16 = var7;
            var7 = var9;
            var9 = var16;
            int[] var17 = var8;
            var8 = var10;
            var10 = var17;
            int[] var18 = var3;
            var3 = var4;
            var4 = var18;
            var12 = var13 >>> 5;
            var13 = ScalarUtil.getBitLengthPositive(var12, var18);
         }
      }

      System.arraycopy(var9, 0, var1, 0, 4);
      System.arraycopy(var10, 0, var2, 0, 4);
      return true;
   }

   static void toSignedDigits(int var0, int[] var1) {
      Nat.caddTo(8, ~var1[0] & 1, L, var1);
      Nat.shiftDownBit(8, var1, 1);
   }
}
