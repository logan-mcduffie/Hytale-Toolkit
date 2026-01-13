package org.bouncycastle.math.ec.rfc7748;

import org.bouncycastle.math.raw.Mod;

public abstract class X448Field {
   public static final int SIZE = 16;
   private static final int M28 = 268435455;
   private static final long U32 = 4294967295L;
   private static final int[] P32 = new int[]{-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};

   protected X448Field() {
   }

   public static void add(int[] var0, int[] var1, int[] var2) {
      for (int var3 = 0; var3 < 16; var3++) {
         var2[var3] = var0[var3] + var1[var3];
      }
   }

   public static void addOne(int[] var0) {
      var0[0]++;
   }

   public static void addOne(int[] var0, int var1) {
      var0[var1]++;
   }

   public static int areEqual(int[] var0, int[] var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 16; var3++) {
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
      int var11 = var0[10];
      int var12 = var0[11];
      int var13 = var0[12];
      int var14 = var0[13];
      int var15 = var0[14];
      int var16 = var0[15];
      var2 += var1 >>> 28;
      var1 &= 268435455;
      var6 += var5 >>> 28;
      var5 &= 268435455;
      var10 += var9 >>> 28;
      var9 &= 268435455;
      var14 += var13 >>> 28;
      var13 &= 268435455;
      var3 += var2 >>> 28;
      var2 &= 268435455;
      var7 += var6 >>> 28;
      var6 &= 268435455;
      var11 += var10 >>> 28;
      var10 &= 268435455;
      var15 += var14 >>> 28;
      var14 &= 268435455;
      var4 += var3 >>> 28;
      var3 &= 268435455;
      var8 += var7 >>> 28;
      var7 &= 268435455;
      var12 += var11 >>> 28;
      var11 &= 268435455;
      var16 += var15 >>> 28;
      var15 &= 268435455;
      int var17 = var16 >>> 28;
      var16 &= 268435455;
      var1 += var17;
      var9 += var17;
      var5 += var4 >>> 28;
      var4 &= 268435455;
      var9 += var8 >>> 28;
      var8 &= 268435455;
      var13 += var12 >>> 28;
      var12 &= 268435455;
      var2 += var1 >>> 28;
      var1 &= 268435455;
      var6 += var5 >>> 28;
      var5 &= 268435455;
      var10 += var9 >>> 28;
      var9 &= 268435455;
      var14 += var13 >>> 28;
      var13 &= 268435455;
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
      var0[10] = var11;
      var0[11] = var12;
      var0[12] = var13;
      var0[13] = var14;
      var0[14] = var15;
      var0[15] = var16;
   }

   public static void cmov(int var0, int[] var1, int var2, int[] var3, int var4) {
      for (int var5 = 0; var5 < 16; var5++) {
         int var6 = var3[var4 + var5];
         int var7 = var6 ^ var1[var2 + var5];
         var6 ^= var7 & var0;
         var3[var4 + var5] = var6;
      }
   }

   public static void cnegate(int var0, int[] var1) {
      int[] var2 = create();
      sub(var2, var1, var2);
      cmov(-var0, var2, 0, var1, 0);
   }

   public static void copy(int[] var0, int var1, int[] var2, int var3) {
      for (int var4 = 0; var4 < 16; var4++) {
         var2[var3 + var4] = var0[var1 + var4];
      }
   }

   public static int[] create() {
      return new int[16];
   }

   public static int[] createTable(int var0) {
      return new int[16 * var0];
   }

   public static void cswap(int var0, int[] var1, int[] var2) {
      int var3 = 0 - var0;

      for (int var4 = 0; var4 < 16; var4++) {
         int var5 = var1[var4];
         int var6 = var2[var4];
         int var7 = var3 & (var5 ^ var6);
         var1[var4] = var5 ^ var7;
         var2[var4] = var6 ^ var7;
      }
   }

   public static void decode(int[] var0, int var1, int[] var2) {
      decode224(var0, var1, var2, 0);
      decode224(var0, var1 + 7, var2, 8);
   }

   public static void decode(byte[] var0, int[] var1) {
      decode56(var0, 0, var1, 0);
      decode56(var0, 7, var1, 2);
      decode56(var0, 14, var1, 4);
      decode56(var0, 21, var1, 6);
      decode56(var0, 28, var1, 8);
      decode56(var0, 35, var1, 10);
      decode56(var0, 42, var1, 12);
      decode56(var0, 49, var1, 14);
   }

   public static void decode(byte[] var0, int var1, int[] var2) {
      decode56(var0, var1, var2, 0);
      decode56(var0, var1 + 7, var2, 2);
      decode56(var0, var1 + 14, var2, 4);
      decode56(var0, var1 + 21, var2, 6);
      decode56(var0, var1 + 28, var2, 8);
      decode56(var0, var1 + 35, var2, 10);
      decode56(var0, var1 + 42, var2, 12);
      decode56(var0, var1 + 49, var2, 14);
   }

   public static void decode(byte[] var0, int var1, int[] var2, int var3) {
      decode56(var0, var1, var2, var3);
      decode56(var0, var1 + 7, var2, var3 + 2);
      decode56(var0, var1 + 14, var2, var3 + 4);
      decode56(var0, var1 + 21, var2, var3 + 6);
      decode56(var0, var1 + 28, var2, var3 + 8);
      decode56(var0, var1 + 35, var2, var3 + 10);
      decode56(var0, var1 + 42, var2, var3 + 12);
      decode56(var0, var1 + 49, var2, var3 + 14);
   }

   private static void decode224(int[] var0, int var1, int[] var2, int var3) {
      int var4 = var0[var1 + 0];
      int var5 = var0[var1 + 1];
      int var6 = var0[var1 + 2];
      int var7 = var0[var1 + 3];
      int var8 = var0[var1 + 4];
      int var9 = var0[var1 + 5];
      int var10 = var0[var1 + 6];
      var2[var3 + 0] = var4 & 268435455;
      var2[var3 + 1] = (var4 >>> 28 | var5 << 4) & 268435455;
      var2[var3 + 2] = (var5 >>> 24 | var6 << 8) & 268435455;
      var2[var3 + 3] = (var6 >>> 20 | var7 << 12) & 268435455;
      var2[var3 + 4] = (var7 >>> 16 | var8 << 16) & 268435455;
      var2[var3 + 5] = (var8 >>> 12 | var9 << 20) & 268435455;
      var2[var3 + 6] = (var9 >>> 8 | var10 << 24) & 268435455;
      var2[var3 + 7] = var10 >>> 4;
   }

   private static int decode24(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      return var2 | (var0[++var1] & 0xFF) << 16;
   }

   private static int decode32(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   private static void decode56(byte[] var0, int var1, int[] var2, int var3) {
      int var4 = decode32(var0, var1);
      int var5 = decode24(var0, var1 + 4);
      var2[var3] = var4 & 268435455;
      var2[var3 + 1] = var4 >>> 28 | var5 << 4;
   }

   public static void encode(int[] var0, int[] var1, int var2) {
      encode224(var0, 0, var1, var2);
      encode224(var0, 8, var1, var2 + 7);
   }

   public static void encode(int[] var0, byte[] var1) {
      encode56(var0, 0, var1, 0);
      encode56(var0, 2, var1, 7);
      encode56(var0, 4, var1, 14);
      encode56(var0, 6, var1, 21);
      encode56(var0, 8, var1, 28);
      encode56(var0, 10, var1, 35);
      encode56(var0, 12, var1, 42);
      encode56(var0, 14, var1, 49);
   }

   public static void encode(int[] var0, byte[] var1, int var2) {
      encode56(var0, 0, var1, var2);
      encode56(var0, 2, var1, var2 + 7);
      encode56(var0, 4, var1, var2 + 14);
      encode56(var0, 6, var1, var2 + 21);
      encode56(var0, 8, var1, var2 + 28);
      encode56(var0, 10, var1, var2 + 35);
      encode56(var0, 12, var1, var2 + 42);
      encode56(var0, 14, var1, var2 + 49);
   }

   public static void encode(int[] var0, int var1, byte[] var2, int var3) {
      encode56(var0, var1, var2, var3);
      encode56(var0, var1 + 2, var2, var3 + 7);
      encode56(var0, var1 + 4, var2, var3 + 14);
      encode56(var0, var1 + 6, var2, var3 + 21);
      encode56(var0, var1 + 8, var2, var3 + 28);
      encode56(var0, var1 + 10, var2, var3 + 35);
      encode56(var0, var1 + 12, var2, var3 + 42);
      encode56(var0, var1 + 14, var2, var3 + 49);
   }

   private static void encode224(int[] var0, int var1, int[] var2, int var3) {
      int var4 = var0[var1 + 0];
      int var5 = var0[var1 + 1];
      int var6 = var0[var1 + 2];
      int var7 = var0[var1 + 3];
      int var8 = var0[var1 + 4];
      int var9 = var0[var1 + 5];
      int var10 = var0[var1 + 6];
      int var11 = var0[var1 + 7];
      var2[var3 + 0] = var4 | var5 << 28;
      var2[var3 + 1] = var5 >>> 4 | var6 << 24;
      var2[var3 + 2] = var6 >>> 8 | var7 << 20;
      var2[var3 + 3] = var7 >>> 12 | var8 << 16;
      var2[var3 + 4] = var8 >>> 16 | var9 << 12;
      var2[var3 + 5] = var9 >>> 20 | var10 << 8;
      var2[var3 + 6] = var10 >>> 24 | var11 << 4;
   }

   private static void encode24(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
   }

   private static void encode32(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
      var1[++var2] = (byte)(var0 >>> 24);
   }

   private static void encode56(int[] var0, int var1, byte[] var2, int var3) {
      int var4 = var0[var1];
      int var5 = var0[var1 + 1];
      encode32(var4 | var5 << 28, var2, var3);
      encode24(var5 >>> 4, var2, var3 + 4);
   }

   public static void inv(int[] var0, int[] var1) {
      int[] var2 = create();
      int[] var3 = new int[14];
      copy(var0, 0, var2, 0);
      normalize(var2);
      encode(var2, var3, 0);
      Mod.modOddInverse(P32, var3, var3);
      decode(var3, 0, var1);
   }

   public static void invVar(int[] var0, int[] var1) {
      int[] var2 = create();
      int[] var3 = new int[14];
      copy(var0, 0, var2, 0);
      normalize(var2);
      encode(var2, var3, 0);
      Mod.modOddInverseVar(P32, var3, var3);
      decode(var3, 0, var1);
   }

   public static int isOne(int[] var0) {
      int var1 = var0[0] ^ 1;

      for (int var2 = 1; var2 < 16; var2++) {
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

      for (int var2 = 0; var2 < 16; var2++) {
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
      int var13 = var0[10];
      int var14 = var0[11];
      int var15 = var0[12];
      int var16 = var0[13];
      int var17 = var0[14];
      int var18 = var0[15];
      long var23 = (long)var4 * var1;
      int var19 = (int)var23 & 268435455;
      var23 >>>= 28;
      long var25 = (long)var8 * var1;
      int var20 = (int)var25 & 268435455;
      var25 >>>= 28;
      long var27 = (long)var12 * var1;
      int var21 = (int)var27 & 268435455;
      var27 >>>= 28;
      long var29 = (long)var16 * var1;
      int var22 = (int)var29 & 268435455;
      var29 >>>= 28;
      var23 += (long)var5 * var1;
      var2[2] = (int)var23 & 268435455;
      var23 >>>= 28;
      var25 += (long)var9 * var1;
      var2[6] = (int)var25 & 268435455;
      var25 >>>= 28;
      var27 += (long)var13 * var1;
      var2[10] = (int)var27 & 268435455;
      var27 >>>= 28;
      var29 += (long)var17 * var1;
      var2[14] = (int)var29 & 268435455;
      var29 >>>= 28;
      var23 += (long)var6 * var1;
      var2[3] = (int)var23 & 268435455;
      var23 >>>= 28;
      var25 += (long)var10 * var1;
      var2[7] = (int)var25 & 268435455;
      var25 >>>= 28;
      var27 += (long)var14 * var1;
      var2[11] = (int)var27 & 268435455;
      var27 >>>= 28;
      var29 += (long)var18 * var1;
      var2[15] = (int)var29 & 268435455;
      var29 >>>= 28;
      var25 += var29;
      var23 += (long)var7 * var1;
      var2[4] = (int)var23 & 268435455;
      var23 >>>= 28;
      var25 += (long)var11 * var1;
      var2[8] = (int)var25 & 268435455;
      var25 >>>= 28;
      var27 += (long)var15 * var1;
      var2[12] = (int)var27 & 268435455;
      var27 >>>= 28;
      var29 += (long)var3 * var1;
      var2[0] = (int)var29 & 268435455;
      var29 >>>= 28;
      var2[1] = var19 + (int)var29;
      var2[5] = var20 + (int)var23;
      var2[9] = var21 + (int)var25;
      var2[13] = var22 + (int)var27;
   }

   public static void mul(int[] var0, int[] var1, int[] var2) {
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
      int var13 = var0[10];
      int var14 = var0[11];
      int var15 = var0[12];
      int var16 = var0[13];
      int var17 = var0[14];
      int var18 = var0[15];
      int var19 = var1[0];
      int var20 = var1[1];
      int var21 = var1[2];
      int var22 = var1[3];
      int var23 = var1[4];
      int var24 = var1[5];
      int var25 = var1[6];
      int var26 = var1[7];
      int var27 = var1[8];
      int var28 = var1[9];
      int var29 = var1[10];
      int var30 = var1[11];
      int var31 = var1[12];
      int var32 = var1[13];
      int var33 = var1[14];
      int var34 = var1[15];
      int var35 = var3 + var11;
      int var36 = var4 + var12;
      int var37 = var5 + var13;
      int var38 = var6 + var14;
      int var39 = var7 + var15;
      int var40 = var8 + var16;
      int var41 = var9 + var17;
      int var42 = var10 + var18;
      int var43 = var19 + var27;
      int var44 = var20 + var28;
      int var45 = var21 + var29;
      int var46 = var22 + var30;
      int var47 = var23 + var31;
      int var48 = var24 + var32;
      int var49 = var25 + var33;
      int var50 = var26 + var34;
      long var71 = (long)var3 * var19;
      long var73 = (long)var10 * var20
         + (long)var9 * var21
         + (long)var8 * var22
         + (long)var7 * var23
         + (long)var6 * var24
         + (long)var5 * var25
         + (long)var4 * var26;
      long var75 = (long)var11 * var27;
      long var77 = (long)var18 * var28
         + (long)var17 * var29
         + (long)var16 * var30
         + (long)var15 * var31
         + (long)var14 * var32
         + (long)var13 * var33
         + (long)var12 * var34;
      long var79 = (long)var35 * var43;
      long var81 = (long)var42 * var44
         + (long)var41 * var45
         + (long)var40 * var46
         + (long)var39 * var47
         + (long)var38 * var48
         + (long)var37 * var49
         + (long)var36 * var50;
      long var67 = var71 + var75 + var81 - var73;
      int var51 = (int)var67 & 268435455;
      var67 >>>= 28;
      long var69 = var77 + var79 - var71 + var81;
      int var59 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var83 = (long)var4 * var19 + (long)var3 * var20;
      long var85 = (long)var10 * var21 + (long)var9 * var22 + (long)var8 * var23 + (long)var7 * var24 + (long)var6 * var25 + (long)var5 * var26;
      long var87 = (long)var12 * var27 + (long)var11 * var28;
      long var89 = (long)var18 * var29 + (long)var17 * var30 + (long)var16 * var31 + (long)var15 * var32 + (long)var14 * var33 + (long)var13 * var34;
      long var91 = (long)var36 * var43 + (long)var35 * var44;
      long var93 = (long)var42 * var45 + (long)var41 * var46 + (long)var40 * var47 + (long)var39 * var48 + (long)var38 * var49 + (long)var37 * var50;
      var67 += var83 + var87 + var93 - var85;
      int var52 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var89 + var91 - var83 + var93;
      int var60 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var95 = (long)var5 * var19 + (long)var4 * var20 + (long)var3 * var21;
      long var97 = (long)var10 * var22 + (long)var9 * var23 + (long)var8 * var24 + (long)var7 * var25 + (long)var6 * var26;
      long var99 = (long)var13 * var27 + (long)var12 * var28 + (long)var11 * var29;
      long var101 = (long)var18 * var30 + (long)var17 * var31 + (long)var16 * var32 + (long)var15 * var33 + (long)var14 * var34;
      long var103 = (long)var37 * var43 + (long)var36 * var44 + (long)var35 * var45;
      long var105 = (long)var42 * var46 + (long)var41 * var47 + (long)var40 * var48 + (long)var39 * var49 + (long)var38 * var50;
      var67 += var95 + var99 + var105 - var97;
      int var53 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var101 + var103 - var95 + var105;
      int var61 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var107 = (long)var6 * var19 + (long)var5 * var20 + (long)var4 * var21 + (long)var3 * var22;
      long var109 = (long)var10 * var23 + (long)var9 * var24 + (long)var8 * var25 + (long)var7 * var26;
      long var111 = (long)var14 * var27 + (long)var13 * var28 + (long)var12 * var29 + (long)var11 * var30;
      long var113 = (long)var18 * var31 + (long)var17 * var32 + (long)var16 * var33 + (long)var15 * var34;
      long var115 = (long)var38 * var43 + (long)var37 * var44 + (long)var36 * var45 + (long)var35 * var46;
      long var117 = (long)var42 * var47 + (long)var41 * var48 + (long)var40 * var49 + (long)var39 * var50;
      var67 += var107 + var111 + var117 - var109;
      int var54 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var113 + var115 - var107 + var117;
      int var62 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var119 = (long)var7 * var19 + (long)var6 * var20 + (long)var5 * var21 + (long)var4 * var22 + (long)var3 * var23;
      long var121 = (long)var10 * var24 + (long)var9 * var25 + (long)var8 * var26;
      long var123 = (long)var15 * var27 + (long)var14 * var28 + (long)var13 * var29 + (long)var12 * var30 + (long)var11 * var31;
      long var125 = (long)var18 * var32 + (long)var17 * var33 + (long)var16 * var34;
      long var127 = (long)var39 * var43 + (long)var38 * var44 + (long)var37 * var45 + (long)var36 * var46 + (long)var35 * var47;
      long var129 = (long)var42 * var48 + (long)var41 * var49 + (long)var40 * var50;
      var67 += var119 + var123 + var129 - var121;
      int var55 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var125 + var127 - var119 + var129;
      int var63 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var131 = (long)var8 * var19 + (long)var7 * var20 + (long)var6 * var21 + (long)var5 * var22 + (long)var4 * var23 + (long)var3 * var24;
      long var133 = (long)var10 * var25 + (long)var9 * var26;
      long var135 = (long)var16 * var27 + (long)var15 * var28 + (long)var14 * var29 + (long)var13 * var30 + (long)var12 * var31 + (long)var11 * var32;
      long var137 = (long)var18 * var33 + (long)var17 * var34;
      long var139 = (long)var40 * var43 + (long)var39 * var44 + (long)var38 * var45 + (long)var37 * var46 + (long)var36 * var47 + (long)var35 * var48;
      long var141 = (long)var42 * var49 + (long)var41 * var50;
      var67 += var131 + var135 + var141 - var133;
      int var56 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var137 + var139 - var131 + var141;
      int var64 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var143 = (long)var9 * var19
         + (long)var8 * var20
         + (long)var7 * var21
         + (long)var6 * var22
         + (long)var5 * var23
         + (long)var4 * var24
         + (long)var3 * var25;
      long var145 = (long)var10 * var26;
      long var147 = (long)var17 * var27
         + (long)var16 * var28
         + (long)var15 * var29
         + (long)var14 * var30
         + (long)var13 * var31
         + (long)var12 * var32
         + (long)var11 * var33;
      long var149 = (long)var18 * var34;
      long var151 = (long)var41 * var43
         + (long)var40 * var44
         + (long)var39 * var45
         + (long)var38 * var46
         + (long)var37 * var47
         + (long)var36 * var48
         + (long)var35 * var49;
      long var153 = (long)var42 * var50;
      var67 += var143 + var147 + var153 - var145;
      int var57 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var149 + var151 - var143 + var153;
      int var65 = (int)var69 & 268435455;
      var69 >>>= 28;
      long var155 = (long)var10 * var19
         + (long)var9 * var20
         + (long)var8 * var21
         + (long)var7 * var22
         + (long)var6 * var23
         + (long)var5 * var24
         + (long)var4 * var25
         + (long)var3 * var26;
      long var157 = (long)var18 * var27
         + (long)var17 * var28
         + (long)var16 * var29
         + (long)var15 * var30
         + (long)var14 * var31
         + (long)var13 * var32
         + (long)var12 * var33
         + (long)var11 * var34;
      long var159 = (long)var42 * var43
         + (long)var41 * var44
         + (long)var40 * var45
         + (long)var39 * var46
         + (long)var38 * var47
         + (long)var37 * var48
         + (long)var36 * var49
         + (long)var35 * var50;
      var67 += var155 + var157;
      int var58 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var159 - var155;
      int var66 = (int)var69 & 268435455;
      var69 >>>= 28;
      var67 += var69;
      var67 += var59;
      var59 = (int)var67 & 268435455;
      var67 >>>= 28;
      var69 += var51;
      var51 = (int)var69 & 268435455;
      var69 >>>= 28;
      var60 += (int)var67;
      var52 += (int)var69;
      var2[0] = var51;
      var2[1] = var52;
      var2[2] = var53;
      var2[3] = var54;
      var2[4] = var55;
      var2[5] = var56;
      var2[6] = var57;
      var2[7] = var58;
      var2[8] = var59;
      var2[9] = var60;
      var2[10] = var61;
      var2[11] = var62;
      var2[12] = var63;
      var2[13] = var64;
      var2[14] = var65;
      var2[15] = var66;
   }

   public static void negate(int[] var0, int[] var1) {
      int[] var2 = create();
      sub(var2, var0, var1);
   }

   public static void normalize(int[] var0) {
      reduce(var0, 1);
      reduce(var0, -1);
   }

   public static void one(int[] var0) {
      var0[0] = 1;

      for (int var1 = 1; var1 < 16; var1++) {
         var0[var1] = 0;
      }
   }

   private static void powPm3d4(int[] var0, int[] var1) {
      int[] var2 = create();
      sqr(var0, var2);
      mul(var0, var2, var2);
      int[] var3 = create();
      sqr(var2, var3);
      mul(var0, var3, var3);
      int[] var4 = create();
      sqr(var3, 3, var4);
      mul(var3, var4, var4);
      int[] var5 = create();
      sqr(var4, 3, var5);
      mul(var3, var5, var5);
      int[] var6 = create();
      sqr(var5, 9, var6);
      mul(var5, var6, var6);
      int[] var7 = create();
      sqr(var6, var7);
      mul(var0, var7, var7);
      int[] var8 = create();
      sqr(var7, 18, var8);
      mul(var6, var8, var8);
      int[] var9 = create();
      sqr(var8, 37, var9);
      mul(var8, var9, var9);
      int[] var10 = create();
      sqr(var9, 37, var10);
      mul(var8, var10, var10);
      int[] var11 = create();
      sqr(var10, 111, var11);
      mul(var10, var11, var11);
      int[] var12 = create();
      sqr(var11, var12);
      mul(var0, var12, var12);
      int[] var13 = create();
      sqr(var12, 223, var13);
      mul(var13, var11, var1);
   }

   private static void reduce(int[] var0, int var1) {
      int var2 = var0[15];
      int var3 = var2 & 268435455;
      var2 = (var2 >>> 28) + var1;
      long var4 = var2;

      for (int var6 = 0; var6 < 8; var6++) {
         var4 += var0[var6] & 4294967295L;
         var0[var6] = (int)var4 & 268435455;
         var4 >>= 28;
      }

      var4 += var2;

      for (int var11 = 8; var11 < 15; var11++) {
         var4 += var0[var11] & 4294967295L;
         var0[var11] = (int)var4 & 268435455;
         var4 >>= 28;
      }

      var0[15] = var3 + (int)var4;
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
      int var12 = var0[10];
      int var13 = var0[11];
      int var14 = var0[12];
      int var15 = var0[13];
      int var16 = var0[14];
      int var17 = var0[15];
      int var18 = var2 * 2;
      int var19 = var3 * 2;
      int var20 = var4 * 2;
      int var21 = var5 * 2;
      int var22 = var6 * 2;
      int var23 = var7 * 2;
      int var24 = var8 * 2;
      int var25 = var10 * 2;
      int var26 = var11 * 2;
      int var27 = var12 * 2;
      int var28 = var13 * 2;
      int var29 = var14 * 2;
      int var30 = var15 * 2;
      int var31 = var16 * 2;
      int var32 = var2 + var10;
      int var33 = var3 + var11;
      int var34 = var4 + var12;
      int var35 = var5 + var13;
      int var36 = var6 + var14;
      int var37 = var7 + var15;
      int var38 = var8 + var16;
      int var39 = var9 + var17;
      int var40 = var32 * 2;
      int var41 = var33 * 2;
      int var42 = var34 * 2;
      int var43 = var35 * 2;
      int var44 = var36 * 2;
      int var45 = var37 * 2;
      int var46 = var38 * 2;
      long var67 = (long)var2 * var2;
      long var69 = (long)var9 * var19 + (long)var8 * var20 + (long)var7 * var21 + (long)var6 * var6;
      long var71 = (long)var10 * var10;
      long var73 = (long)var17 * var26 + (long)var16 * var27 + (long)var15 * var28 + (long)var14 * var14;
      long var75 = (long)var32 * var32;
      long var77 = var39 * (var41 & 4294967295L) + var38 * (var42 & 4294967295L) + var37 * (var43 & 4294967295L) + (long)var36 * var36;
      long var63 = var67 + var71 + var77 - var69;
      int var47 = (int)var63 & 268435455;
      var63 >>>= 28;
      long var65 = var73 + var75 - var67 + var77;
      int var55 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var79 = (long)var3 * var18;
      long var81 = (long)var9 * var20 + (long)var8 * var21 + (long)var7 * var22;
      long var83 = (long)var11 * var25;
      long var85 = (long)var17 * var27 + (long)var16 * var28 + (long)var15 * var29;
      long var87 = var33 * (var40 & 4294967295L);
      long var89 = var39 * (var42 & 4294967295L) + var38 * (var43 & 4294967295L) + var37 * (var44 & 4294967295L);
      var63 += var79 + var83 + var89 - var81;
      int var48 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var85 + var87 - var79 + var89;
      int var56 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var91 = (long)var4 * var18 + (long)var3 * var3;
      long var93 = (long)var9 * var21 + (long)var8 * var22 + (long)var7 * var7;
      long var95 = (long)var12 * var25 + (long)var11 * var11;
      long var97 = (long)var17 * var28 + (long)var16 * var29 + (long)var15 * var15;
      long var99 = var34 * (var40 & 4294967295L) + (long)var33 * var33;
      long var101 = var39 * (var43 & 4294967295L) + var38 * (var44 & 4294967295L) + (long)var37 * var37;
      var63 += var91 + var95 + var101 - var93;
      int var49 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var97 + var99 - var91 + var101;
      int var57 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var103 = (long)var5 * var18 + (long)var4 * var19;
      long var105 = (long)var9 * var22 + (long)var8 * var23;
      long var107 = (long)var13 * var25 + (long)var12 * var26;
      long var109 = (long)var17 * var29 + (long)var16 * var30;
      long var111 = var35 * (var40 & 4294967295L) + var34 * (var41 & 4294967295L);
      long var113 = var39 * (var44 & 4294967295L) + var38 * (var45 & 4294967295L);
      var63 += var103 + var107 + var113 - var105;
      int var50 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var109 + var111 - var103 + var113;
      int var58 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var115 = (long)var6 * var18 + (long)var5 * var19 + (long)var4 * var4;
      long var117 = (long)var9 * var23 + (long)var8 * var8;
      long var119 = (long)var14 * var25 + (long)var13 * var26 + (long)var12 * var12;
      long var121 = (long)var17 * var30 + (long)var16 * var16;
      long var123 = var36 * (var40 & 4294967295L) + var35 * (var41 & 4294967295L) + (long)var34 * var34;
      long var125 = var39 * (var45 & 4294967295L) + (long)var38 * var38;
      var63 += var115 + var119 + var125 - var117;
      int var51 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var121 + var123 - var115 + var125;
      int var59 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var127 = (long)var7 * var18 + (long)var6 * var19 + (long)var5 * var20;
      long var129 = (long)var9 * var24;
      long var131 = (long)var15 * var25 + (long)var14 * var26 + (long)var13 * var27;
      long var133 = (long)var17 * var31;
      long var135 = var37 * (var40 & 4294967295L) + var36 * (var41 & 4294967295L) + var35 * (var42 & 4294967295L);
      long var137 = var39 * (var46 & 4294967295L);
      var63 += var127 + var131 + var137 - var129;
      int var52 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var133 + var135 - var127 + var137;
      int var60 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var139 = (long)var8 * var18 + (long)var7 * var19 + (long)var6 * var20 + (long)var5 * var5;
      long var141 = (long)var9 * var9;
      long var143 = (long)var16 * var25 + (long)var15 * var26 + (long)var14 * var27 + (long)var13 * var13;
      long var145 = (long)var17 * var17;
      long var147 = var38 * (var40 & 4294967295L) + var37 * (var41 & 4294967295L) + var36 * (var42 & 4294967295L) + (long)var35 * var35;
      long var149 = (long)var39 * var39;
      var63 += var139 + var143 + var149 - var141;
      int var53 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var145 + var147 - var139 + var149;
      int var61 = (int)var65 & 268435455;
      var65 >>>= 28;
      long var151 = (long)var9 * var18 + (long)var8 * var19 + (long)var7 * var20 + (long)var6 * var21;
      long var153 = (long)var17 * var25 + (long)var16 * var26 + (long)var15 * var27 + (long)var14 * var28;
      long var155 = var39 * (var40 & 4294967295L) + var38 * (var41 & 4294967295L) + var37 * (var42 & 4294967295L) + var36 * (var43 & 4294967295L);
      var63 += var151 + var153;
      int var54 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var155 - var151;
      int var62 = (int)var65 & 268435455;
      var65 >>>= 28;
      var63 += var65;
      var63 += var55;
      var55 = (int)var63 & 268435455;
      var63 >>>= 28;
      var65 += var47;
      var47 = (int)var65 & 268435455;
      var65 >>>= 28;
      var56 += (int)var63;
      var48 += (int)var65;
      var1[0] = var47;
      var1[1] = var48;
      var1[2] = var49;
      var1[3] = var50;
      var1[4] = var51;
      var1[5] = var52;
      var1[6] = var53;
      var1[7] = var54;
      var1[8] = var55;
      var1[9] = var56;
      var1[10] = var57;
      var1[11] = var58;
      var1[12] = var59;
      var1[13] = var60;
      var1[14] = var61;
      var1[15] = var62;
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
      sqr(var0, var3);
      mul(var3, var1, var3);
      sqr(var3, var4);
      mul(var3, var0, var3);
      mul(var4, var0, var4);
      mul(var4, var1, var4);
      int[] var5 = create();
      powPm3d4(var4, var5);
      mul(var5, var3, var5);
      int[] var6 = create();
      sqr(var5, var6);
      mul(var6, var1, var6);
      sub(var0, var6, var6);
      normalize(var6);
      if (isZeroVar(var6)) {
         copy(var5, 0, var2, 0);
         return true;
      } else {
         return false;
      }
   }

   public static void sub(int[] var0, int[] var1, int[] var2) {
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
      int var13 = var0[10];
      int var14 = var0[11];
      int var15 = var0[12];
      int var16 = var0[13];
      int var17 = var0[14];
      int var18 = var0[15];
      int var19 = var1[0];
      int var20 = var1[1];
      int var21 = var1[2];
      int var22 = var1[3];
      int var23 = var1[4];
      int var24 = var1[5];
      int var25 = var1[6];
      int var26 = var1[7];
      int var27 = var1[8];
      int var28 = var1[9];
      int var29 = var1[10];
      int var30 = var1[11];
      int var31 = var1[12];
      int var32 = var1[13];
      int var33 = var1[14];
      int var34 = var1[15];
      int var35 = var3 + 536870910 - var19;
      int var36 = var4 + 536870910 - var20;
      int var37 = var5 + 536870910 - var21;
      int var38 = var6 + 536870910 - var22;
      int var39 = var7 + 536870910 - var23;
      int var40 = var8 + 536870910 - var24;
      int var41 = var9 + 536870910 - var25;
      int var42 = var10 + 536870910 - var26;
      int var43 = var11 + 536870908 - var27;
      int var44 = var12 + 536870910 - var28;
      int var45 = var13 + 536870910 - var29;
      int var46 = var14 + 536870910 - var30;
      int var47 = var15 + 536870910 - var31;
      int var48 = var16 + 536870910 - var32;
      int var49 = var17 + 536870910 - var33;
      int var50 = var18 + 536870910 - var34;
      var37 += var36 >>> 28;
      var36 &= 268435455;
      var41 += var40 >>> 28;
      var40 &= 268435455;
      var45 += var44 >>> 28;
      var44 &= 268435455;
      var49 += var48 >>> 28;
      var48 &= 268435455;
      var38 += var37 >>> 28;
      var37 &= 268435455;
      var42 += var41 >>> 28;
      var41 &= 268435455;
      var46 += var45 >>> 28;
      var45 &= 268435455;
      var50 += var49 >>> 28;
      var49 &= 268435455;
      int var51 = var50 >>> 28;
      var50 &= 268435455;
      var35 += var51;
      var43 += var51;
      var39 += var38 >>> 28;
      var38 &= 268435455;
      var43 += var42 >>> 28;
      var42 &= 268435455;
      var47 += var46 >>> 28;
      var46 &= 268435455;
      var36 += var35 >>> 28;
      var35 &= 268435455;
      var40 += var39 >>> 28;
      var39 &= 268435455;
      var44 += var43 >>> 28;
      var43 &= 268435455;
      var48 += var47 >>> 28;
      var47 &= 268435455;
      var2[0] = var35;
      var2[1] = var36;
      var2[2] = var37;
      var2[3] = var38;
      var2[4] = var39;
      var2[5] = var40;
      var2[6] = var41;
      var2[7] = var42;
      var2[8] = var43;
      var2[9] = var44;
      var2[10] = var45;
      var2[11] = var46;
      var2[12] = var47;
      var2[13] = var48;
      var2[14] = var49;
      var2[15] = var50;
   }

   public static void subOne(int[] var0) {
      int[] var1 = create();
      var1[0] = 1;
      sub(var0, var1, var0);
   }

   public static void zero(int[] var0) {
      for (int var1 = 0; var1 < 16; var1++) {
         var0[var1] = 0;
      }
   }
}
