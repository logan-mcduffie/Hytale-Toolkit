package org.bouncycastle.util;

public class GF16 {
   private static final byte[] F_STAR = new byte[]{1, 2, 4, 8, 3, 6, 12, 11, 5, 10, 7, 14, 15, 13, 9};
   private static final byte[] MT4B = new byte[256];
   private static final byte[] INV4B = new byte[16];

   static byte mt(int var0, int var1) {
      return MT4B[var0 << 4 ^ var1];
   }

   public static byte mul(byte var0, byte var1) {
      return MT4B[var0 << 4 | var1];
   }

   public static int mul(int var0, int var1) {
      return MT4B[var0 << 4 | var1];
   }

   public static byte inv(byte var0) {
      return INV4B[var0 & 15];
   }

   public static void decode(byte[] var0, byte[] var1, int var2) {
      int var4 = 0;
      int var5 = var2 >> 1;

      int var3;
      for (var3 = 0; var3 < var5; var3++) {
         var1[var4++] = (byte)(var0[var3] & 15);
         var1[var4++] = (byte)(var0[var3] >>> 4 & 15);
      }

      if ((var2 & 1) == 1) {
         var1[var4] = (byte)(var0[var3] & 15);
      }
   }

   public static void decode(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = var4 >> 1;

      for (int var6 = 0; var6 < var5; var6++) {
         var2[var3++] = (byte)(var0[var1] & 15);
         var2[var3++] = (byte)(var0[var1++] >>> 4 & 15);
      }

      if ((var4 & 1) == 1) {
         var2[var3] = (byte)(var0[var1] & 15);
      }
   }

   public static void encode(byte[] var0, byte[] var1, int var2) {
      int var4 = 0;
      int var5 = var2 >> 1;

      int var3;
      for (var3 = 0; var3 < var5; var3++) {
         int var6 = var0[var4++] & 15;
         int var7 = (var0[var4++] & 15) << 4;
         var1[var3] = (byte)(var6 | var7);
      }

      if ((var2 & 1) == 1) {
         var1[var3] = (byte)(var0[var4] & 15);
      }
   }

   public static void encode(byte[] var0, byte[] var1, int var2, int var3) {
      int var5 = 0;
      int var6 = var3 >> 1;

      for (int var4 = 0; var4 < var6; var4++) {
         int var7 = var0[var5++] & 15;
         int var8 = (var0[var5++] & 15) << 4;
         var1[var2++] = (byte)(var7 | var8);
      }

      if ((var3 & 1) == 1) {
         var1[var2] = (byte)(var0[var5] & 15);
      }
   }

   public static byte innerProduct(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      byte var5 = 0;
      int var6 = 0;

      while (var6 < var4) {
         var5 ^= mul(var0[var1++], var2[var3]);
         var6++;
         var3 += var4;
      }

      return var5;
   }

   static {
      for (int var0 = 0; var0 < 15; var0++) {
         for (int var1 = 0; var1 < 15; var1++) {
            MT4B[F_STAR[var0] << 4 ^ F_STAR[var1]] = F_STAR[(var0 + var1) % 15];
         }
      }

      byte var5 = F_STAR[1];
      byte var6 = F_STAR[14];
      byte var2 = 1;
      byte var3 = 1;
      INV4B[1] = 1;

      for (int var4 = 0; var4 < 14; var4++) {
         var2 = mt(var2, var5);
         var3 = mt(var3, var6);
         INV4B[var2] = (byte)var3;
      }
   }
}
