package org.bouncycastle.math.raw;

public abstract class Mont256 {
   private static final long M = 4294967295L;

   public static int inverse32(int var0) {
      int var1 = var0 * (2 - var0 * var0);
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      return var1 * (2 - var0 * var1);
   }

   public static void multAdd(int[] var0, int[] var1, int[] var2, int[] var3, int var4) {
      int var5 = 0;
      long var6 = var1[0] & 4294967295L;

      for (int var8 = 0; var8 < 8; var8++) {
         long var9 = var2[0] & 4294967295L;
         long var11 = var0[var8] & 4294967295L;
         long var13 = var11 * var6;
         long var15 = (var13 & 4294967295L) + var9;
         long var17 = (int)var15 * var4 & 4294967295L;
         long var19 = var17 * (var3[0] & 4294967295L);
         var15 += var19 & 4294967295L;
         var15 = (var15 >>> 32) + (var13 >>> 32) + (var19 >>> 32);

         for (int var21 = 1; var21 < 8; var21++) {
            var13 = var11 * (var1[var21] & 4294967295L);
            var19 = var17 * (var3[var21] & 4294967295L);
            var15 += (var13 & 4294967295L) + (var19 & 4294967295L) + (var2[var21] & 4294967295L);
            var2[var21 - 1] = (int)var15;
            var15 = (var15 >>> 32) + (var13 >>> 32) + (var19 >>> 32);
         }

         var15 += var5 & 4294967295L;
         var2[7] = (int)var15;
         var5 = (int)(var15 >>> 32);
      }

      if (var5 != 0 || Nat256.gte(var2, var3)) {
         Nat256.sub(var2, var3, var2);
      }
   }

   public static void multAddXF(int[] var0, int[] var1, int[] var2, int[] var3) {
      int var4 = 0;
      long var5 = var1[0] & 4294967295L;

      for (int var7 = 0; var7 < 8; var7++) {
         long var8 = var0[var7] & 4294967295L;
         long var10 = var8 * var5 + (var2[0] & 4294967295L);
         long var12 = var10 & 4294967295L;
         var10 = (var10 >>> 32) + var12;

         for (int var14 = 1; var14 < 8; var14++) {
            long var15 = var8 * (var1[var14] & 4294967295L);
            long var17 = var12 * (var3[var14] & 4294967295L);
            var10 += (var15 & 4294967295L) + (var17 & 4294967295L) + (var2[var14] & 4294967295L);
            var2[var14 - 1] = (int)var10;
            var10 = (var10 >>> 32) + (var15 >>> 32) + (var17 >>> 32);
         }

         var10 += var4 & 4294967295L;
         var2[7] = (int)var10;
         var4 = (int)(var10 >>> 32);
      }

      if (var4 != 0 || Nat256.gte(var2, var3)) {
         Nat256.sub(var2, var3, var2);
      }
   }

   public static void reduce(int[] var0, int[] var1, int var2) {
      for (int var3 = 0; var3 < 8; var3++) {
         int var4 = var0[0];
         long var5 = var4 * var2 & 4294967295L;
         long var7 = var5 * (var1[0] & 4294967295L) + (var4 & 4294967295L);
         var7 >>>= 32;

         for (int var9 = 1; var9 < 8; var9++) {
            var7 += var5 * (var1[var9] & 4294967295L) + (var0[var9] & 4294967295L);
            var0[var9 - 1] = (int)var7;
            var7 >>>= 32;
         }

         var0[7] = (int)var7;
      }

      if (Nat256.gte(var0, var1)) {
         Nat256.sub(var0, var1, var0);
      }
   }

   public static void reduceXF(int[] var0, int[] var1) {
      for (int var2 = 0; var2 < 8; var2++) {
         int var3 = var0[0];
         long var4 = var3 & 4294967295L;
         long var6 = var4;

         for (int var8 = 1; var8 < 8; var8++) {
            var6 += var4 * (var1[var8] & 4294967295L) + (var0[var8] & 4294967295L);
            var0[var8 - 1] = (int)var6;
            var6 >>>= 32;
         }

         var0[7] = (int)var6;
      }

      if (Nat256.gte(var0, var1)) {
         Nat256.sub(var0, var1, var0);
      }
   }
}
