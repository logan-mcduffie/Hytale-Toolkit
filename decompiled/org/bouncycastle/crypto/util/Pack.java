package org.bouncycastle.crypto.util;

/** @deprecated */
public abstract class Pack {
   public static int bigEndianToInt(byte[] var0, int var1) {
      int var2 = var0[var1] << 24;
      var2 |= (var0[++var1] & 255) << 16;
      var2 |= (var0[++var1] & 255) << 8;
      return var2 | var0[++var1] & 0xFF;
   }

   public static void bigEndianToInt(byte[] var0, int var1, int[] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = bigEndianToInt(var0, var1);
         var1 += 4;
      }
   }

   public static byte[] intToBigEndian(int var0) {
      byte[] var1 = new byte[4];
      intToBigEndian(var0, var1, 0);
      return var1;
   }

   public static void intToBigEndian(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)(var0 >>> 24);
      var1[++var2] = (byte)(var0 >>> 16);
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)var0;
   }

   public static byte[] intToBigEndian(int[] var0) {
      byte[] var1 = new byte[4 * var0.length];
      intToBigEndian(var0, var1, 0);
      return var1;
   }

   public static void intToBigEndian(int[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         intToBigEndian(var0[var3], var1, var2);
         var2 += 4;
      }
   }

   public static long bigEndianToLong(byte[] var0, int var1) {
      int var2 = bigEndianToInt(var0, var1);
      int var3 = bigEndianToInt(var0, var1 + 4);
      return (var2 & 4294967295L) << 32 | var3 & 4294967295L;
   }

   public static void bigEndianToLong(byte[] var0, int var1, long[] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = bigEndianToLong(var0, var1);
         var1 += 8;
      }
   }

   public static byte[] longToBigEndian(long var0) {
      byte[] var2 = new byte[8];
      longToBigEndian(var0, var2, 0);
      return var2;
   }

   public static void longToBigEndian(long var0, byte[] var2, int var3) {
      intToBigEndian((int)(var0 >>> 32), var2, var3);
      intToBigEndian((int)(var0 & 4294967295L), var2, var3 + 4);
   }

   public static byte[] longToBigEndian(long[] var0) {
      byte[] var1 = new byte[8 * var0.length];
      longToBigEndian(var0, var1, 0);
      return var1;
   }

   public static void longToBigEndian(long[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         longToBigEndian(var0[var3], var1, var2);
         var2 += 8;
      }
   }

   public static int littleEndianToInt(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   public static void littleEndianToInt(byte[] var0, int var1, int[] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = littleEndianToInt(var0, var1);
         var1 += 4;
      }
   }

   public static void littleEndianToInt(byte[] var0, int var1, int[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = littleEndianToInt(var0, var1);
         var1 += 4;
      }
   }

   public static byte[] intToLittleEndian(int var0) {
      byte[] var1 = new byte[4];
      intToLittleEndian(var0, var1, 0);
      return var1;
   }

   public static void intToLittleEndian(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)(var0 >>> 16);
      var1[++var2] = (byte)(var0 >>> 24);
   }

   public static byte[] intToLittleEndian(int[] var0) {
      byte[] var1 = new byte[4 * var0.length];
      intToLittleEndian(var0, var1, 0);
      return var1;
   }

   public static void intToLittleEndian(int[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         intToLittleEndian(var0[var3], var1, var2);
         var2 += 4;
      }
   }

   public static long littleEndianToLong(byte[] var0, int var1) {
      int var2 = littleEndianToInt(var0, var1);
      int var3 = littleEndianToInt(var0, var1 + 4);
      return (var3 & 4294967295L) << 32 | var2 & 4294967295L;
   }

   public static void littleEndianToLong(byte[] var0, int var1, long[] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = littleEndianToLong(var0, var1);
         var1 += 8;
      }
   }

   public static byte[] longToLittleEndian(long var0) {
      byte[] var2 = new byte[8];
      longToLittleEndian(var0, var2, 0);
      return var2;
   }

   public static void longToLittleEndian(long var0, byte[] var2, int var3) {
      intToLittleEndian((int)(var0 & 4294967295L), var2, var3);
      intToLittleEndian((int)(var0 >>> 32), var2, var3 + 4);
   }

   public static byte[] longToLittleEndian(long[] var0) {
      byte[] var1 = new byte[8 * var0.length];
      longToLittleEndian(var0, var1, 0);
      return var1;
   }

   public static void longToLittleEndian(long[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         longToLittleEndian(var0[var3], var1, var2);
         var2 += 8;
      }
   }
}
