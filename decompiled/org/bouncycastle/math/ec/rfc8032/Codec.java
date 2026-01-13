package org.bouncycastle.math.ec.rfc8032;

abstract class Codec {
   static int decode16(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      return var2 | (var0[++var1] & 0xFF) << 8;
   }

   static int decode24(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      return var2 | (var0[++var1] & 0xFF) << 16;
   }

   static int decode32(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   static void decode32(byte[] var0, int var1, int[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = decode32(var0, var1 + var5 * 4);
      }
   }

   static void encode24(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
   }

   static void encode32(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
      var1[++var2] = (byte)(var0 >>> 24);
   }

   static void encode32(int[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         encode32(var0[var1 + var5], var3, var4 + var5 * 4);
      }
   }

   static void encode56(long var0, byte[] var2, int var3) {
      encode32((int)var0, var2, var3);
      encode24((int)(var0 >>> 32), var2, var3 + 4);
   }
}
