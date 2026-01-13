package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.util.Pack;

class GF2Field {
   static final byte[][] gfMulTable = new byte[256][256];
   static final byte[] gfInvTable = new byte[256];
   public static final int MASK = 255;

   private static short gf4Mul2(short var0) {
      int var1 = var0 << 1;
      var1 ^= (var0 >>> 1) * 7;
      return (short)(var1 & 0xFF);
   }

   private static short gf4Mul3(short var0) {
      int var1 = var0 - 2 >>> 1;
      int var2 = var1 & var0 * 3 | ~var1 & var0 - 1;
      return (short)(var2 & 0xFF);
   }

   private static short gf4Mul(short var0, short var1) {
      int var2 = var0 * (var1 & 1);
      var2 ^= gf4Mul2(var0) * (var1 >>> 1);
      return (short)(var2 & 0xFF);
   }

   private static short gf4Squ(short var0) {
      int var1 = var0 ^ var0 >>> 1;
      return (short)(var1 & 0xFF);
   }

   private static short gf16Mul(short var0, short var1) {
      short var2 = (short)(var0 & 3 & 0xFF);
      short var3 = (short)(var0 >>> 2 & 0xFF);
      short var4 = (short)(var1 & 3 & 0xFF);
      short var5 = (short)(var1 >>> 2 & 0xFF);
      short var6 = gf4Mul(var2, var4);
      short var7 = gf4Mul(var3, var5);
      short var8 = (short)(gf4Mul((short)(var2 ^ var3), (short)(var4 ^ var5)) ^ var6);
      short var9 = gf4Mul2(var7);
      return (short)((var8 << 2 ^ var6 ^ var9) & 0xFF);
   }

   private static short gf16Squ(short var0) {
      short var1 = (short)(var0 & 3 & 0xFF);
      short var2 = (short)(var0 >>> 2 & 0xFF);
      var2 = gf4Squ(var2);
      short var3 = gf4Mul2(var2);
      return (short)((var2 << 2 ^ var3 ^ gf4Squ(var1)) & 0xFF);
   }

   private static short gf16Mul8(short var0) {
      short var1 = (short)(var0 & 3 & 0xFF);
      short var2 = (short)(var0 >>> 2 & 0xFF);
      int var3 = gf4Mul2((short)(var1 ^ var2)) << 2;
      var3 |= gf4Mul3(var2);
      return (short)(var3 & 0xFF);
   }

   private static short gf256Mul(short var0, short var1) {
      short var2 = (short)(var0 & 15 & 0xFF);
      short var3 = (short)(var0 >>> 4 & 0xFF);
      short var4 = (short)(var1 & 15 & 0xFF);
      short var5 = (short)(var1 >>> 4 & 0xFF);
      short var6 = gf16Mul(var2, var4);
      short var7 = gf16Mul(var3, var5);
      short var8 = (short)(gf16Mul((short)(var2 ^ var3), (short)(var4 ^ var5)) ^ var6);
      short var9 = gf16Mul8(var7);
      return (short)((var8 << 4 ^ var6 ^ var9) & 0xFF);
   }

   private static short gf256Squ(short var0) {
      short var1 = (short)(var0 & 15 & 0xFF);
      short var2 = (short)(var0 >>> 4 & 0xFF);
      var2 = gf16Squ(var2);
      short var3 = gf16Mul8(var2);
      return (short)((var2 << 4 ^ var3 ^ gf16Squ(var1)) & 0xFF);
   }

   private static short gf256Inv(short var0) {
      short var1 = gf256Squ(var0);
      short var2 = gf256Squ(var1);
      short var3 = gf256Squ(var2);
      short var4 = gf256Mul(var2, var1);
      short var5 = gf256Mul(var4, var3);
      short var6 = gf256Squ(var5);
      var6 = gf256Squ(var6);
      var6 = gf256Squ(var6);
      short var7 = gf256Mul(var6, var5);
      short var8 = gf256Squ(var7);
      return gf256Mul(var1, var8);
   }

   public static short addElem(short var0, short var1) {
      return (short)(var0 ^ var1);
   }

   public static long addElem_64(long var0, long var2) {
      return var0 ^ var2;
   }

   public static short invElem(short var0) {
      return (short)(gfInvTable[var0] & 255);
   }

   public static long invElem_64(long var0) {
      return gf256Inv_64(var0);
   }

   public static short multElem(short var0, short var1) {
      return (short)(gfMulTable[var0][var1] & 255);
   }

   public static long multElem_64(long var0, long var2) {
      return gf256Mul_64(var0, var2);
   }

   private static long gf4Mul2_64(long var0) {
      long var2 = var0 & 6148914691236517205L;
      long var4 = var0 & -6148914691236517206L;
      return var4 ^ var2 << 1 ^ var4 >>> 1;
   }

   private static long gf4Mul_64(long var0, long var2) {
      long var4 = (var0 << 1 & var2 ^ var2 << 1 & var0) & -6148914691236517206L;
      long var6 = var0 & var2;
      return var6 ^ var4 ^ (var6 & -6148914691236517206L) >>> 1;
   }

   private static long gf4Squ_64(long var0) {
      long var2 = var0 & -6148914691236517206L;
      return var0 ^ var2 >>> 1;
   }

   private static long gf16Mul_64(long var0, long var2) {
      long var4 = gf4Mul_64(var0, var2);
      long var6 = var4 & 3689348814741910323L;
      long var8 = var4 & -3689348814741910324L;
      long var10 = (var0 << 2 ^ var0) & -3689348814741910324L ^ var8 >>> 2;
      long var12 = (var2 << 2 ^ var2) & -3689348814741910324L ^ 2459565876494606882L;
      long var14 = gf4Mul_64(var10, var12);
      return var14 ^ var6 << 2 ^ var6;
   }

   private static long gf16Squ_64(long var0) {
      long var2 = gf4Squ_64(var0);
      long var4 = gf4Mul2_64(var2 & -3689348814741910324L);
      return var2 ^ var4 >>> 2;
   }

   private static long gf16Mul8_64(long var0) {
      long var2 = var0 & 3689348814741910323L;
      long var4 = var0 & -3689348814741910324L;
      long var6 = var2 << 2 ^ var4 ^ var4 >>> 2;
      long var8 = gf4Mul2_64(var6);
      return var8 ^ var4 >>> 2;
   }

   private static long gf256Mul_64(long var0, long var2) {
      long var4 = gf16Mul_64(var0, var2);
      long var6 = var4 & 1085102592571150095L;
      long var8 = var4 & -1085102592571150096L;
      long var10 = (var0 << 4 ^ var0) & -1085102592571150096L ^ var8 >>> 4;
      long var12 = (var2 << 4 ^ var2) & -1085102592571150096L ^ 578721382704613384L;
      long var14 = gf16Mul_64(var10, var12);
      return var14 ^ var6 << 4 ^ var6;
   }

   private static long gf256Squ_64(long var0) {
      long var2 = gf16Squ_64(var0);
      long var4 = var2 & -1085102592571150096L;
      long var6 = gf16Mul8_64(var4);
      return var2 ^ var6 >>> 4;
   }

   private static long gf256Inv_64(long var0) {
      long var2 = gf256Squ_64(var0);
      long var4 = gf256Squ_64(var2);
      long var6 = gf256Squ_64(var4);
      long var8 = gf256Mul_64(var4, var2);
      long var10 = gf256Mul_64(var8, var6);
      long var12 = gf256Squ_64(var10);
      var12 = gf256Squ_64(var12);
      var12 = gf256Squ_64(var12);
      long var14 = gf256Mul_64(var12, var10);
      long var16 = gf256Squ_64(var14);
      return gf256Mul_64(var2, var16);
   }

   static {
      long var0 = 72340172838076673L;

      for (int var2 = 1; var2 <= 255; var2++) {
         long var3 = 506097522914230528L;

         for (byte var5 = 0; var5 < 256; var5 += 8) {
            long var6 = gf256Mul_64(var0, var3);
            Pack.longToLittleEndian(var6, gfMulTable[var2], var5);
            var3 += 578721382704613384L;
         }

         var0 += 72340172838076673L;
      }

      var0 = 506097522914230528L;

      for (byte var9 = 0; var9 < 256; var9 += 8) {
         long var10 = gf256Inv_64(var0);
         Pack.longToLittleEndian(var10, gfInvTable, var9);
         var0 += 578721382704613384L;
      }
   }
}
