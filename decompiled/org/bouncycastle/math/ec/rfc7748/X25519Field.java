package org.bouncycastle.math.ec.rfc7748;

import org.bouncycastle.math.raw.Mod;

public abstract class X25519Field {
   public static final int SIZE = 10;
   private static final int M24 = 16777215;
   private static final int M25 = 33554431;
   private static final int M26 = 67108863;
   private static final int[] P32 = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
   private static final int[] ROOT_NEG_ONE = new int[]{-32595792, -7943725, 4688975, 3500415, 6194736, 33281959, -12573105, -1002827, 163343, 5703241};

   protected X25519Field() {
   }

   public static void add(int[] var0, int[] var1, int[] var2) {
      for (int var3 = 0; var3 < 10; var3++) {
         var2[var3] = var0[var3] + var1[var3];
      }
   }

   public static void addOne(int[] var0) {
      var0[0]++;
   }

   public static void addOne(int[] var0, int var1) {
      var0[var1]++;
   }

   public static void apm(int[] var0, int[] var1, int[] var2, int[] var3) {
      for (int var4 = 0; var4 < 10; var4++) {
         int var5 = var0[var4];
         int var6 = var1[var4];
         var2[var4] = var5 + var6;
         var3[var4] = var5 - var6;
      }
   }

   public static int areEqual(int[] var0, int[] var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 10; var3++) {
         var2 |= var0[var3] ^ var1[var3];
      }

      var2 = var2 >>> 1 | var2 & 1;
      return var2 - 1 >> 31;
   }

   public static boolean areEqualVar(int[] var0, int[] var1) {
      return 0 != areEqual(var0, var1);
   }

   public static void carry(int[] var0) {
      int var1 = var0[0];
      int var2 = var0[1];
      int var3 = var0[2];
      int var4 = var0[3];
      int var5 = var0[4];
      int var6 = var0[5];
      int var7 = var0[6];
      int var8 = var0[7];
      int var9 = var0[8];
      int var10 = var0[9];
      var3 += var2 >> 26;
      var2 &= 67108863;
      var5 += var4 >> 26;
      var4 &= 67108863;
      var8 += var7 >> 26;
      var7 &= 67108863;
      var10 += var9 >> 26;
      var9 &= 67108863;
      var4 += var3 >> 25;
      var3 &= 33554431;
      var6 += var5 >> 25;
      var5 &= 33554431;
      var9 += var8 >> 25;
      var8 &= 33554431;
      var1 += (var10 >> 25) * 38;
      var10 &= 33554431;
      var2 += var1 >> 26;
      var1 &= 67108863;
      var7 += var6 >> 26;
      var6 &= 67108863;
      var3 += var2 >> 26;
      var2 &= 67108863;
      var5 += var4 >> 26;
      var4 &= 67108863;
      var8 += var7 >> 26;
      var7 &= 67108863;
      var10 += var9 >> 26;
      var9 &= 67108863;
      var0[0] = var1;
      var0[1] = var2;
      var0[2] = var3;
      var0[3] = var4;
      var0[4] = var5;
      var0[5] = var6;
      var0[6] = var7;
      var0[7] = var8;
      var0[8] = var9;
      var0[9] = var10;
   }

   public static void cmov(int var0, int[] var1, int var2, int[] var3, int var4) {
      for (int var5 = 0; var5 < 10; var5++) {
         int var6 = var3[var4 + var5];
         int var7 = var6 ^ var1[var2 + var5];
         var6 ^= var7 & var0;
         var3[var4 + var5] = var6;
      }
   }

   public static void cnegate(int var0, int[] var1) {
      int var2 = 0 - var0;

      for (int var3 = 0; var3 < 10; var3++) {
         var1[var3] = (var1[var3] ^ var2) - var2;
      }
   }

   public static void copy(int[] var0, int var1, int[] var2, int var3) {
      for (int var4 = 0; var4 < 10; var4++) {
         var2[var3 + var4] = var0[var1 + var4];
      }
   }

   public static int[] create() {
      return new int[10];
   }

   public static int[] createTable(int var0) {
      return new int[10 * var0];
   }

   public static void cswap(int var0, int[] var1, int[] var2) {
      int var3 = 0 - var0;

      for (int var4 = 0; var4 < 10; var4++) {
         int var5 = var1[var4];
         int var6 = var2[var4];
         int var7 = var3 & (var5 ^ var6);
         var1[var4] = var5 ^ var7;
         var2[var4] = var6 ^ var7;
      }
   }

   public static void decode(int[] var0, int var1, int[] var2) {
      decode128(var0, var1, var2, 0);
      decode128(var0, var1 + 4, var2, 5);
      var2[9] &= 16777215;
   }

   public static void decode(byte[] var0, int[] var1) {
      decode128(var0, 0, var1, 0);
      decode128(var0, 16, var1, 5);
      var1[9] &= 16777215;
   }

   public static void decode(byte[] var0, int var1, int[] var2) {
      decode128(var0, var1, var2, 0);
      decode128(var0, var1 + 16, var2, 5);
      var2[9] &= 16777215;
   }

   public static void decode(byte[] var0, int var1, int[] var2, int var3) {
      decode128(var0, var1, var2, var3);
      decode128(var0, var1 + 16, var2, var3 + 5);
      var2[var3 + 9] = var2[var3 + 9] & 16777215;
   }

   private static void decode128(int[] var0, int var1, int[] var2, int var3) {
      int var4 = var0[var1 + 0];
      int var5 = var0[var1 + 1];
      int var6 = var0[var1 + 2];
      int var7 = var0[var1 + 3];
      var2[var3 + 0] = var4 & 67108863;
      var2[var3 + 1] = (var5 << 6 | var4 >>> 26) & 67108863;
      var2[var3 + 2] = (var6 << 12 | var5 >>> 20) & 33554431;
      var2[var3 + 3] = (var7 << 19 | var6 >>> 13) & 67108863;
      var2[var3 + 4] = var7 >>> 7;
   }

   private static void decode128(byte[] var0, int var1, int[] var2, int var3) {
      int var4 = decode32(var0, var1 + 0);
      int var5 = decode32(var0, var1 + 4);
      int var6 = decode32(var0, var1 + 8);
      int var7 = decode32(var0, var1 + 12);
      var2[var3 + 0] = var4 & 67108863;
      var2[var3 + 1] = (var5 << 6 | var4 >>> 26) & 67108863;
      var2[var3 + 2] = (var6 << 12 | var5 >>> 20) & 33554431;
      var2[var3 + 3] = (var7 << 19 | var6 >>> 13) & 67108863;
      var2[var3 + 4] = var7 >>> 7;
   }

   private static int decode32(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   public static void encode(int[] var0, int[] var1, int var2) {
      encode128(var0, 0, var1, var2);
      encode128(var0, 5, var1, var2 + 4);
   }

   public static void encode(int[] var0, byte[] var1) {
      encode128(var0, 0, var1, 0);
      encode128(var0, 5, var1, 16);
   }

   public static void encode(int[] var0, byte[] var1, int var2) {
      encode128(var0, 0, var1, var2);
      encode128(var0, 5, var1, var2 + 16);
   }

   public static void encode(int[] var0, int var1, byte[] var2, int var3) {
      encode128(var0, var1, var2, var3);
      encode128(var0, var1 + 5, var2, var3 + 16);
   }

   private static void encode128(int[] var0, int var1, int[] var2, int var3) {
      int var4 = var0[var1 + 0];
      int var5 = var0[var1 + 1];
      int var6 = var0[var1 + 2];
      int var7 = var0[var1 + 3];
      int var8 = var0[var1 + 4];
      var2[var3 + 0] = var4 | var5 << 26;
      var2[var3 + 1] = var5 >>> 6 | var6 << 20;
      var2[var3 + 2] = var6 >>> 12 | var7 << 13;
      var2[var3 + 3] = var7 >>> 19 | var8 << 7;
   }

   private static void encode128(int[] var0, int var1, byte[] var2, int var3) {
      int var4 = var0[var1 + 0];
      int var5 = var0[var1 + 1];
      int var6 = var0[var1 + 2];
      int var7 = var0[var1 + 3];
      int var8 = var0[var1 + 4];
      int var9 = var4 | var5 << 26;
      encode32(var9, var2, var3 + 0);
      int var10 = var5 >>> 6 | var6 << 20;
      encode32(var10, var2, var3 + 4);
      int var11 = var6 >>> 12 | var7 << 13;
      encode32(var11, var2, var3 + 8);
      int var12 = var7 >>> 19 | var8 << 7;
      encode32(var12, var2, var3 + 12);
   }

   private static void encode32(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
      var1[++var2] = (byte)(var0 >>> 24);
   }

   public static void inv(int[] var0, int[] var1) {
      int[] var2 = create();
      int[] var3 = new int[8];
      copy(var0, 0, var2, 0);
      normalize(var2);
      encode(var2, var3, 0);
      Mod.modOddInverse(P32, var3, var3);
      decode(var3, 0, var1);
   }

   public static void invVar(int[] var0, int[] var1) {
      int[] var2 = create();
      int[] var3 = new int[8];
      copy(var0, 0, var2, 0);
      normalize(var2);
      encode(var2, var3, 0);
      Mod.modOddInverseVar(P32, var3, var3);
      decode(var3, 0, var1);
   }

   public static int isOne(int[] var0) {
      int var1 = var0[0] ^ 1;

      for (int var2 = 1; var2 < 10; var2++) {
         var1 |= var0[var2];
      }

      var1 = var1 >>> 1 | var1 & 1;
      return var1 - 1 >> 31;
   }

   public static boolean isOneVar(int[] var0) {
      return 0 != isOne(var0);
   }

   public static int isZero(int[] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < 10; var2++) {
         var1 |= var0[var2];
      }

      var1 = var1 >>> 1 | var1 & 1;
      return var1 - 1 >> 31;
   }

   public static boolean isZeroVar(int[] var0) {
      return 0 != isZero(var0);
   }

   public static void mul(int[] var0, int var1, int[] var2) {
      int var3 = var0[0];
      int var4 = var0[1];
      int var5 = var0[2];
      int var6 = var0[3];
      int var7 = var0[4];
      int var8 = var0[5];
      int var9 = var0[6];
      int var10 = var0[7];
      int var11 = var0[8];
      int var12 = var0[9];
      long var13 = (long)var5 * var1;
      var5 = (int)var13 & 33554431;
      var13 >>= 25;
      long var15 = (long)var7 * var1;
      var7 = (int)var15 & 33554431;
      var15 >>= 25;
      long var17 = (long)var10 * var1;
      var10 = (int)var17 & 33554431;
      var17 >>= 25;
      long var19 = (long)var12 * var1;
      var12 = (int)var19 & 33554431;
      var19 >>= 25;
      var19 *= 38L;
      var19 += (long)var3 * var1;
      var2[0] = (int)var19 & 67108863;
      var19 >>= 26;
      var15 += (long)var8 * var1;
      var2[5] = (int)var15 & 67108863;
      var15 >>= 26;
      var19 += (long)var4 * var1;
      var2[1] = (int)var19 & 67108863;
      var19 >>= 26;
      var13 += (long)var6 * var1;
      var2[3] = (int)var13 & 67108863;
      var13 >>= 26;
      var15 += (long)var9 * var1;
      var2[6] = (int)var15 & 67108863;
      var15 >>= 26;
      var17 += (long)var11 * var1;
      var2[8] = (int)var17 & 67108863;
      var17 >>= 26;
      var2[2] = var5 + (int)var19;
      var2[4] = var7 + (int)var13;
      var2[7] = var10 + (int)var15;
      var2[9] = var12 + (int)var17;
   }

   public static void mul(int[] var0, int[] var1, int[] var2) {
      int var3 = var0[0];
      int var4 = var1[0];
      int var5 = var0[1];
      int var6 = var1[1];
      int var7 = var0[2];
      int var8 = var1[2];
      int var9 = var0[3];
      int var10 = var1[3];
      int var11 = var0[4];
      int var12 = var1[4];
      int var13 = var0[5];
      int var14 = var1[5];
      int var15 = var0[6];
      int var16 = var1[6];
      int var17 = var0[7];
      int var18 = var1[7];
      int var19 = var0[8];
      int var20 = var1[8];
      int var21 = var0[9];
      int var22 = var1[9];
      long var23 = (long)var3 * var4;
      long var25 = (long)var3 * var6 + (long)var5 * var4;
      long var27 = (long)var3 * var8 + (long)var5 * var6 + (long)var7 * var4;
      long var29 = (long)var5 * var8 + (long)var7 * var6;
      var29 <<= 1;
      var29 += (long)var3 * var10 + (long)var9 * var4;
      long var31 = (long)var7 * var8;
      var31 <<= 1;
      var31 += (long)var3 * var12 + (long)var5 * var10 + (long)var9 * var6 + (long)var11 * var4;
      long var33 = (long)var5 * var12 + (long)var7 * var10 + (long)var9 * var8 + (long)var11 * var6;
      var33 <<= 1;
      long var35 = (long)var7 * var12 + (long)var11 * var8;
      var35 <<= 1;
      var35 += (long)var9 * var10;
      long var37 = (long)var9 * var12 + (long)var11 * var10;
      long var39 = (long)var11 * var12;
      var39 <<= 1;
      long var41 = (long)var13 * var14;
      long var43 = (long)var13 * var16 + (long)var15 * var14;
      long var45 = (long)var13 * var18 + (long)var15 * var16 + (long)var17 * var14;
      long var47 = (long)var15 * var18 + (long)var17 * var16;
      var47 <<= 1;
      var47 += (long)var13 * var20 + (long)var19 * var14;
      long var49 = (long)var17 * var18;
      var49 <<= 1;
      var49 += (long)var13 * var22 + (long)var15 * var20 + (long)var19 * var16 + (long)var21 * var14;
      long var51 = (long)var15 * var22 + (long)var17 * var20 + (long)var19 * var18 + (long)var21 * var16;
      long var53 = (long)var17 * var22 + (long)var21 * var18;
      var53 <<= 1;
      var53 += (long)var19 * var20;
      long var55 = (long)var19 * var22 + (long)var21 * var20;
      long var57 = (long)var21 * var22;
      var23 -= var51 * 76L;
      var25 -= var53 * 38L;
      var27 -= var55 * 38L;
      var29 -= var57 * 76L;
      var33 -= var41;
      var35 -= var43;
      var37 -= var45;
      var39 -= var47;
      var3 += var13;
      var4 += var14;
      var5 += var15;
      var6 += var16;
      var7 += var17;
      var8 += var18;
      var9 += var19;
      var10 += var20;
      var11 += var21;
      var12 += var22;
      long var59 = (long)var3 * var4;
      long var61 = (long)var3 * var6 + (long)var5 * var4;
      long var63 = (long)var3 * var8 + (long)var5 * var6 + (long)var7 * var4;
      long var65 = (long)var5 * var8 + (long)var7 * var6;
      var65 <<= 1;
      var65 += (long)var3 * var10 + (long)var9 * var4;
      long var67 = (long)var7 * var8;
      var67 <<= 1;
      var67 += (long)var3 * var12 + (long)var5 * var10 + (long)var9 * var6 + (long)var11 * var4;
      long var69 = (long)var5 * var12 + (long)var7 * var10 + (long)var9 * var8 + (long)var11 * var6;
      var69 <<= 1;
      long var71 = (long)var7 * var12 + (long)var11 * var8;
      var71 <<= 1;
      var71 += (long)var9 * var10;
      long var73 = (long)var9 * var12 + (long)var11 * var10;
      long var75 = (long)var11 * var12;
      var75 <<= 1;
      long var79 = var39 + (var65 - var29);
      int var77 = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var67 - var31 - var49;
      int var78 = (int)var79 & 33554431;
      var79 >>= 25;
      var79 = var23 + (var79 + var69 - var33) * 38L;
      var2[0] = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var25 + (var71 - var35) * 38L;
      var2[1] = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var27 + (var73 - var37) * 38L;
      var2[2] = (int)var79 & 33554431;
      var79 >>= 25;
      var79 += var29 + (var75 - var39) * 38L;
      var2[3] = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var31 + var49 * 38L;
      var2[4] = (int)var79 & 33554431;
      var79 >>= 25;
      var79 += var33 + (var59 - var23);
      var2[5] = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var35 + (var61 - var25);
      var2[6] = (int)var79 & 67108863;
      var79 >>= 26;
      var79 += var37 + (var63 - var27);
      var2[7] = (int)var79 & 33554431;
      var79 >>= 25;
      var79 += var77;
      var2[8] = (int)var79 & 67108863;
      var79 >>= 26;
      var2[9] = var78 + (int)var79;
   }

   public static void negate(int[] var0, int[] var1) {
      for (int var2 = 0; var2 < 10; var2++) {
         var1[var2] = -var0[var2];
      }
   }

   public static void normalize(int[] var0) {
      int var1 = var0[9] >>> 23 & 1;
      reduce(var0, var1);
      reduce(var0, -var1);
   }

   public static void one(int[] var0) {
      var0[0] = 1;

      for (int var1 = 1; var1 < 10; var1++) {
         var0[var1] = 0;
      }
   }

   private static void powPm5d8(int[] var0, int[] var1, int[] var2) {
      sqr(var0, var1);
      mul(var0, var1, var1);
      int[] var4 = create();
      sqr(var1, var4);
      mul(var0, var4, var4);
      sqr(var4, 2, var4);
      mul(var1, var4, var4);
      int[] var6 = create();
      sqr(var4, 5, var6);
      mul(var4, var6, var6);
      int[] var7 = create();
      sqr(var6, 5, var7);
      mul(var4, var7, var7);
      sqr(var7, 10, var4);
      mul(var6, var4, var4);
      sqr(var4, 25, var6);
      mul(var4, var6, var6);
      sqr(var6, 25, var7);
      mul(var4, var7, var7);
      sqr(var7, 50, var4);
      mul(var6, var4, var4);
      sqr(var4, 125, var6);
      mul(var4, var6, var6);
      sqr(var6, 2, var4);
      mul(var4, var0, var2);
   }

   private static void reduce(int[] var0, int var1) {
      int var2 = var0[9];
      int var3 = var2 & 16777215;
      var2 = (var2 >> 24) + var1;
      long var4 = var2 * 19;
      var4 += var0[0];
      var0[0] = (int)var4 & 67108863;
      var4 >>= 26;
      var4 += var0[1];
      var0[1] = (int)var4 & 67108863;
      var4 >>= 26;
      var4 += var0[2];
      var0[2] = (int)var4 & 33554431;
      var4 >>= 25;
      var4 += var0[3];
      var0[3] = (int)var4 & 67108863;
      var4 >>= 26;
      var4 += var0[4];
      var0[4] = (int)var4 & 33554431;
      var4 >>= 25;
      var4 += var0[5];
      var0[5] = (int)var4 & 67108863;
      var4 >>= 26;
      var4 += var0[6];
      var0[6] = (int)var4 & 67108863;
      var4 >>= 26;
      var4 += var0[7];
      var0[7] = (int)var4 & 33554431;
      var4 >>= 25;
      var4 += var0[8];
      var0[8] = (int)var4 & 67108863;
      var4 >>= 26;
      var0[9] = var3 + (int)var4;
   }

   public static void sqr(int[] var0, int[] var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var0[4];
      int var7 = var0[5];
      int var8 = var0[6];
      int var9 = var0[7];
      int var10 = var0[8];
      int var11 = var0[9];
      int var12 = var3 * 2;
      int var13 = var4 * 2;
      int var14 = var5 * 2;
      int var15 = var6 * 2;
      long var16 = (long)var2 * var2;
      long var18 = (long)var2 * var12;
      long var20 = (long)var2 * var13 + (long)var3 * var3;
      long var22 = (long)var12 * var13 + (long)var2 * var14;
      long var24 = (long)var4 * var13 + (long)var2 * var15 + (long)var3 * var14;
      long var26 = (long)var12 * var15 + (long)var13 * var14;
      long var28 = (long)var13 * var15 + (long)var5 * var5;
      long var30 = (long)var5 * var15;
      long var32 = (long)var6 * var15;
      int var34 = var8 * 2;
      int var35 = var9 * 2;
      int var36 = var10 * 2;
      int var37 = var11 * 2;
      long var38 = (long)var7 * var7;
      long var40 = (long)var7 * var34;
      long var42 = (long)var7 * var35 + (long)var8 * var8;
      long var44 = (long)var34 * var35 + (long)var7 * var36;
      long var46 = (long)var9 * var35 + (long)var7 * var37 + (long)var8 * var36;
      long var48 = (long)var34 * var37 + (long)var35 * var36;
      long var50 = (long)var35 * var37 + (long)var10 * var10;
      long var52 = (long)var10 * var37;
      long var54 = (long)var11 * var37;
      var16 -= var48 * 38L;
      var18 -= var50 * 38L;
      var20 -= var52 * 38L;
      var22 -= var54 * 38L;
      var26 -= var38;
      var28 -= var40;
      var30 -= var42;
      var32 -= var44;
      var2 += var7;
      var3 += var8;
      var4 += var9;
      var5 += var10;
      var6 += var11;
      var12 = var3 * 2;
      var13 = var4 * 2;
      var14 = var5 * 2;
      var15 = var6 * 2;
      long var56 = (long)var2 * var2;
      long var58 = (long)var2 * var12;
      long var60 = (long)var2 * var13 + (long)var3 * var3;
      long var62 = (long)var12 * var13 + (long)var2 * var14;
      long var64 = (long)var4 * var13 + (long)var2 * var15 + (long)var3 * var14;
      long var66 = (long)var12 * var15 + (long)var13 * var14;
      long var68 = (long)var13 * var15 + (long)var5 * var5;
      long var70 = (long)var5 * var15;
      long var72 = (long)var6 * var15;
      long var76 = var32 + (var62 - var22);
      int var74 = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var64 - var24 - var46;
      int var75 = (int)var76 & 33554431;
      var76 >>= 25;
      var76 = var16 + (var76 + var66 - var26) * 38L;
      var1[0] = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var18 + (var68 - var28) * 38L;
      var1[1] = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var20 + (var70 - var30) * 38L;
      var1[2] = (int)var76 & 33554431;
      var76 >>= 25;
      var76 += var22 + (var72 - var32) * 38L;
      var1[3] = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var24 + var46 * 38L;
      var1[4] = (int)var76 & 33554431;
      var76 >>= 25;
      var76 += var26 + (var56 - var16);
      var1[5] = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var28 + (var58 - var18);
      var1[6] = (int)var76 & 67108863;
      var76 >>= 26;
      var76 += var30 + (var60 - var20);
      var1[7] = (int)var76 & 33554431;
      var76 >>= 25;
      var76 += var74;
      var1[8] = (int)var76 & 67108863;
      var76 >>= 26;
      var1[9] = var75 + (int)var76;
   }

   public static void sqr(int[] var0, int var1, int[] var2) {
      sqr(var0, var2);

      while (--var1 > 0) {
         sqr(var2, var2);
      }
   }

   public static boolean sqrtRatioVar(int[] var0, int[] var1, int[] var2) {
      int[] var3 = create();
      int[] var4 = create();
      mul(var0, var1, var3);
      sqr(var1, var4);
      mul(var3, var4, var3);
      sqr(var4, var4);
      mul(var4, var3, var4);
      int[] var5 = create();
      int[] var6 = create();
      powPm5d8(var4, var5, var6);
      mul(var6, var3, var6);
      int[] var7 = create();
      sqr(var6, var7);
      mul(var7, var1, var7);
      sub(var7, var0, var5);
      normalize(var5);
      if (isZeroVar(var5)) {
         copy(var6, 0, var2, 0);
         return true;
      } else {
         add(var7, var0, var5);
         normalize(var5);
         if (isZeroVar(var5)) {
            mul(var6, ROOT_NEG_ONE, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   public static void sub(int[] var0, int[] var1, int[] var2) {
      for (int var3 = 0; var3 < 10; var3++) {
         var2[var3] = var0[var3] - var1[var3];
      }
   }

   public static void subOne(int[] var0) {
      var0[0]--;
   }

   public static void zero(int[] var0) {
      for (int var1 = 0; var1 < 10; var1++) {
         var0[var1] = 0;
      }
   }
}
