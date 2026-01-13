package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.util.GF16;

class MapGroup1 {
   public final byte[][][][] p11;
   public final byte[][][][] p12;
   public final byte[][][][] p21;
   public final byte[][][] aAlpha;
   public final byte[][][] bAlpha;
   public final byte[][][] qAlpha1;
   public final byte[][][] qAlpha2;

   public MapGroup1(SnovaParameters var1) {
      int var2 = var1.getM();
      int var3 = var1.getV();
      int var4 = var1.getO();
      int var5 = var1.getAlpha();
      int var6 = var1.getLsq();
      this.p11 = new byte[var2][var3][var3][var6];
      this.p12 = new byte[var2][var3][var4][var6];
      this.p21 = new byte[var2][var4][var3][var6];
      this.aAlpha = new byte[var2][var5][var6];
      this.bAlpha = new byte[var2][var5][var6];
      this.qAlpha1 = new byte[var2][var5][var6];
      this.qAlpha2 = new byte[var2][var5][var6];
   }

   void decode(byte[] var1, int var2, boolean var3) {
      int var4 = decodeP(var1, 0, this.p11, var2);
      var4 += decodeP(var1, var4, this.p12, var2 - var4);
      var4 += decodeP(var1, var4, this.p21, var2 - var4);
      if (var3) {
         var4 += decodeAlpha(var1, var4, this.aAlpha, var2 - var4);
         var4 += decodeAlpha(var1, var4, this.bAlpha, var2 - var4);
         var4 += decodeAlpha(var1, var4, this.qAlpha1, var2 - var4);
         decodeAlpha(var1, var4, this.qAlpha2, var2 - var4);
      }
   }

   static int decodeP(byte[] var0, int var1, byte[][][][] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var2.length; var5++) {
         var4 += decodeAlpha(var0, var1 + var4, var2[var5], var3);
      }

      return var4;
   }

   private static int decodeAlpha(byte[] var0, int var1, byte[][][] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var2.length; var5++) {
         var4 += decodeArray(var0, var1 + var4, var2[var5], var3 - var4);
      }

      return var4;
   }

   static int decodeArray(byte[] var0, int var1, byte[][] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var2.length; var5++) {
         int var6 = Math.min(var2[var5].length, var3 << 1);
         GF16.decode(var0, var1 + var4, var2[var5], 0, var6);
         var6 = var6 + 1 >> 1;
         var4 += var6;
         var3 -= var6;
      }

      return var4;
   }

   void fill(byte[] var1, boolean var2) {
      int var3 = fillP(var1, 0, this.p11, var1.length);
      var3 += fillP(var1, var3, this.p12, var1.length - var3);
      var3 += fillP(var1, var3, this.p21, var1.length - var3);
      if (var2) {
         var3 += fillAlpha(var1, var3, this.aAlpha, var1.length - var3);
         var3 += fillAlpha(var1, var3, this.bAlpha, var1.length - var3);
         var3 += fillAlpha(var1, var3, this.qAlpha1, var1.length - var3);
         fillAlpha(var1, var3, this.qAlpha2, var1.length - var3);
      }
   }

   static int fillP(byte[] var0, int var1, byte[][][][] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var2.length; var5++) {
         var4 += fillAlpha(var0, var1 + var4, var2[var5], var3 - var4);
      }

      return var4;
   }

   static int fillAlpha(byte[] var0, int var1, byte[][][] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var2.length; var5++) {
         for (int var6 = 0; var6 < var2[var5].length; var6++) {
            int var7 = Math.min(var2[var5][var6].length, var3 - var4);
            System.arraycopy(var0, var1 + var4, var2[var5][var6], 0, var7);
            var4 += var7;
         }
      }

      return var4;
   }
}
