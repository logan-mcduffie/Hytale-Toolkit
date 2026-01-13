package org.bouncycastle.util;

public abstract class Pack {
   public static short bigEndianToShort(byte[] var0, int var1) {
      int var2 = (var0[var1] & 255) << 8;
      var2 |= var0[++var1] & 255;
      return (short)var2;
   }

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

   public static void bigEndianToInt(byte[] var0, int var1, int[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = bigEndianToInt(var0, var1);
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

   public static void intToBigEndian(int[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         intToBigEndian(var0[var1 + var5], var3, var4);
         var4 += 4;
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

   public static void bigEndianToLong(byte[] var0, int var1, long[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = bigEndianToLong(var0, var1);
         var1 += 8;
      }
   }

   public static long bigEndianToLong(byte[] var0, int var1, int var2) {
      long var3 = 0L;

      for (int var5 = 0; var5 < var2; var5++) {
         var3 |= (var0[var5 + var1] & 255L) << (7 - var5 << 3);
      }

      return var3;
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

   public static void longToBigEndian(long[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         longToBigEndian(var0[var1 + var5], var3, var4);
         var4 += 8;
      }
   }

   @Deprecated
   public static void longToBigEndian(long var0, byte[] var2, int var3, int var4) {
      for (int var5 = var4 - 1; var5 >= 0; var5--) {
         var2[var5 + var3] = (byte)(var0 & 255L);
         var0 >>>= 8;
      }
   }

   public static short littleEndianToShort(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      return (short)var2;
   }

   public static void littleEndianToShort(byte[] var0, int var1, short[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = littleEndianToShort(var0, var1);
         var1 += 2;
      }
   }

   public static int littleEndianToInt(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   public static int littleEndianToInt_High(byte[] var0, int var1, int var2) {
      return littleEndianToInt_Low(var0, var1, var2) << (4 - var2 << 3);
   }

   public static int littleEndianToInt_Low(byte[] var0, int var1, int var2) {
      int var3 = var0[var1] & 255;
      byte var4 = 0;

      for (int var5 = 1; var5 < var2; var5++) {
         var4 += 8;
         var3 |= (var0[var1 + var5] & 255) << var4;
      }

      return var3;
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

   public static int[] littleEndianToInt(byte[] var0, int var1, int var2) {
      int[] var3 = new int[var2];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = littleEndianToInt(var0, var1);
         var1 += 4;
      }

      return var3;
   }

   public static byte[] shortToLittleEndian(short var0) {
      byte[] var1 = new byte[2];
      shortToLittleEndian(var0, var1, 0);
      return var1;
   }

   public static void shortToLittleEndian(short var0, byte[] var1, int var2) {
      var1[var2] = (byte)var0;
      var1[++var2] = (byte)(var0 >>> 8);
   }

   public static void shortToLittleEndian(short[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         shortToLittleEndian(var0[var1 + var5], var3, var4);
         var4 += 2;
      }
   }

   public static byte[] shortToBigEndian(short var0) {
      byte[] var1 = new byte[2];
      shortToBigEndian(var0, var1, 0);
      return var1;
   }

   public static void shortToBigEndian(short var0, byte[] var1, int var2) {
      var1[var2] = (byte)(var0 >>> 8);
      var1[++var2] = (byte)var0;
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

   public static void intToLittleEndian(int[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         intToLittleEndian(var0[var1 + var5], var3, var4);
         var4 += 4;
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

   public static long littleEndianToLong(byte[] var0, int var1, int var2) {
      long var3 = 0L;

      for (int var5 = 0; var5 < var2; var5++) {
         var3 |= (var0[var1 + var5] & 255L) << (var5 << 3);
      }

      return var3;
   }

   public static void littleEndianToLong(byte[] var0, int var1, long[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = littleEndianToLong(var0, var1);
         var1 += 8;
      }
   }

   public static void longToLittleEndian_High(long var0, byte[] var2, int var3, int var4) {
      byte var5 = 56;
      var2[var3] = (byte)(var0 >>> var5);

      for (int var6 = 1; var6 < var4; var6++) {
         var5 -= 8;
         var2[var3 + var6] = (byte)(var0 >>> var5);
      }
   }

   public static void longToLittleEndian(long var0, byte[] var2, int var3, int var4) {
      for (int var5 = 0; var5 < var4; var5++) {
         var2[var3 + var5] = (byte)(var0 >>> (var5 << 3));
      }
   }

   public static long littleEndianToLong_High(byte[] var0, int var1, int var2) {
      return littleEndianToLong_Low(var0, var1, var2) << (8 - var2 << 3);
   }

   public static long littleEndianToLong_Low(byte[] var0, int var1, int var2) {
      long var3 = var0[var1] & 255;

      for (int var5 = 1; var5 < var2; var5++) {
         var3 <<= 8;
         var3 |= var0[var1 + var5] & 255;
      }

      return var3;
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

   public static void longToLittleEndian(long[] var0, int var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var2; var5++) {
         longToLittleEndian(var0[var1 + var5], var3, var4);
         var4 += 8;
      }
   }
}
