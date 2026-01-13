package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public abstract class GCMUtil {
   public static final int SIZE_BYTES = 16;
   public static final int SIZE_INTS = 4;
   public static final int SIZE_LONGS = 2;
   private static final int E1 = -520093696;
   private static final long E1L = -2233785415175766016L;

   public static byte[] oneAsBytes() {
      byte[] var0 = new byte[16];
      var0[0] = -128;
      return var0;
   }

   public static int[] oneAsInts() {
      int[] var0 = new int[4];
      var0[0] = Integer.MIN_VALUE;
      return var0;
   }

   public static long[] oneAsLongs() {
      return new long[]{Long.MIN_VALUE, 0L};
   }

   public static byte areEqual(byte[] var0, byte[] var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 16; var3++) {
         var2 |= var0[var3] ^ var1[var3];
      }

      var2 = var2 >>> 1 | var2 & 1;
      return (byte)(var2 - 1 >> 31);
   }

   public static int areEqual(int[] var0, int[] var1) {
      int var2 = 0;
      var2 |= var0[0] ^ var1[0];
      var2 |= var0[1] ^ var1[1];
      var2 |= var0[2] ^ var1[2];
      var2 |= var0[3] ^ var1[3];
      var2 = var2 >>> 1 | var2 & 1;
      return var2 - 1 >> 31;
   }

   public static long areEqual(long[] var0, long[] var1) {
      long var2 = 0L;
      var2 |= var0[0] ^ var1[0];
      var2 |= var0[1] ^ var1[1];
      var2 = var2 >>> 1 | var2 & 1L;
      return var2 - 1L >> 63;
   }

   public static byte[] asBytes(int[] var0) {
      byte[] var1 = new byte[16];
      Pack.intToBigEndian(var0, 0, 4, var1, 0);
      return var1;
   }

   public static void asBytes(int[] var0, byte[] var1) {
      Pack.intToBigEndian(var0, 0, 4, var1, 0);
   }

   public static byte[] asBytes(long[] var0) {
      byte[] var1 = new byte[16];
      Pack.longToBigEndian(var0, 0, 2, var1, 0);
      return var1;
   }

   public static void asBytes(long[] var0, byte[] var1) {
      Pack.longToBigEndian(var0, 0, 2, var1, 0);
   }

   public static int[] asInts(byte[] var0) {
      int[] var1 = new int[4];
      Pack.bigEndianToInt(var0, 0, var1, 0, 4);
      return var1;
   }

   public static void asInts(byte[] var0, int[] var1) {
      Pack.bigEndianToInt(var0, 0, var1, 0, 4);
   }

   public static long[] asLongs(byte[] var0) {
      long[] var1 = new long[2];
      Pack.bigEndianToLong(var0, 0, var1, 0, 2);
      return var1;
   }

   public static void asLongs(byte[] var0, long[] var1) {
      Pack.bigEndianToLong(var0, 0, var1, 0, 2);
   }

   public static void copy(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < 16; var2++) {
         var1[var2] = var0[var2];
      }
   }

   public static void copy(int[] var0, int[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
      var1[2] = var0[2];
      var1[3] = var0[3];
   }

   public static void copy(long[] var0, long[] var1) {
      var1[0] = var0[0];
      var1[1] = var0[1];
   }

   public static void divideP(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var2 >> 63;
      var2 ^= var6 & -2233785415175766016L;
      var1[0] = var2 << 1 | var4 >>> 63;
      var1[1] = var4 << 1 | -var6;
   }

   public static void multiply(byte[] var0, byte[] var1) {
      long[] var2 = asLongs(var0);
      long[] var3 = asLongs(var1);
      multiply(var2, var3);
      asBytes(var2, var0);
   }

   static void multiply(byte[] var0, long[] var1) {
      long var2 = Pack.bigEndianToLong(var0, 0);
      long var4 = Pack.bigEndianToLong(var0, 8);
      long var6 = var1[0];
      long var8 = var1[1];
      long var10 = Longs.reverse(var2);
      long var12 = Longs.reverse(var4);
      long var14 = Longs.reverse(var6);
      long var16 = Longs.reverse(var8);
      long var18 = Longs.reverse(implMul64(var10, var14));
      long var20 = implMul64(var2, var6) << 1;
      long var22 = Longs.reverse(implMul64(var12, var16));
      long var24 = implMul64(var4, var8) << 1;
      long var26 = Longs.reverse(implMul64(var10 ^ var12, var14 ^ var16));
      long var28 = implMul64(var2 ^ var4, var6 ^ var8) << 1;
      long var32 = var20 ^ var18 ^ var22 ^ var26;
      long var34 = var22 ^ var20 ^ var24 ^ var28;
      var32 ^= var24 ^ var24 >>> 1 ^ var24 >>> 2 ^ var24 >>> 7;
      var34 ^= var24 << 62 ^ var24 << 57;
      long var30 = var18 ^ var34 ^ var34 >>> 1 ^ var34 >>> 2 ^ var34 >>> 7;
      var32 ^= var34 << 63 ^ var34 << 62 ^ var34 << 57;
      Pack.longToBigEndian(var30, var0, 0);
      Pack.longToBigEndian(var32, var0, 8);
   }

   public static void multiply(int[] var0, int[] var1) {
      int var2 = var1[0];
      int var3 = var1[1];
      int var4 = var1[2];
      int var5 = var1[3];
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;

      for (int var10 = 0; var10 < 4; var10++) {
         int var11 = var0[var10];

         for (int var12 = 0; var12 < 32; var12++) {
            int var13 = var11 >> 31;
            var11 <<= 1;
            var6 ^= var2 & var13;
            var7 ^= var3 & var13;
            var8 ^= var4 & var13;
            var9 ^= var5 & var13;
            int var14 = var5 << 31 >> 8;
            var5 = var5 >>> 1 | var4 << 31;
            var4 = var4 >>> 1 | var3 << 31;
            var3 = var3 >>> 1 | var2 << 31;
            var2 = var2 >>> 1 ^ var14 & -520093696;
         }
      }

      var0[0] = var6;
      var0[1] = var7;
      var0[2] = var8;
      var0[3] = var9;
   }

   public static void multiply(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var1[0];
      long var8 = var1[1];
      long var10 = Longs.reverse(var2);
      long var12 = Longs.reverse(var4);
      long var14 = Longs.reverse(var6);
      long var16 = Longs.reverse(var8);
      long var18 = Longs.reverse(implMul64(var10, var14));
      long var20 = implMul64(var2, var6) << 1;
      long var22 = Longs.reverse(implMul64(var12, var16));
      long var24 = implMul64(var4, var8) << 1;
      long var26 = Longs.reverse(implMul64(var10 ^ var12, var14 ^ var16));
      long var28 = implMul64(var2 ^ var4, var6 ^ var8) << 1;
      long var32 = var20 ^ var18 ^ var22 ^ var26;
      long var34 = var22 ^ var20 ^ var24 ^ var28;
      var32 ^= var24 ^ var24 >>> 1 ^ var24 >>> 2 ^ var24 >>> 7;
      var34 ^= var24 << 62 ^ var24 << 57;
      long var30 = var18 ^ var34 ^ var34 >>> 1 ^ var34 >>> 2 ^ var34 >>> 7;
      var32 ^= var34 << 63 ^ var34 << 62 ^ var34 << 57;
      var0[0] = var30;
      var0[1] = var32;
   }

   public static void multiplyP(int[] var0) {
      int var1 = var0[0];
      int var2 = var0[1];
      int var3 = var0[2];
      int var4 = var0[3];
      int var5 = var4 << 31 >> 31;
      var0[0] = var1 >>> 1 ^ var5 & -520093696;
      var0[1] = var2 >>> 1 | var1 << 31;
      var0[2] = var3 >>> 1 | var2 << 31;
      var0[3] = var4 >>> 1 | var3 << 31;
   }

   public static void multiplyP(int[] var0, int[] var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var5 << 31 >> 31;
      var1[0] = var2 >>> 1 ^ var6 & -520093696;
      var1[1] = var3 >>> 1 | var2 << 31;
      var1[2] = var4 >>> 1 | var3 << 31;
      var1[3] = var5 >>> 1 | var4 << 31;
   }

   public static void multiplyP(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var3 << 63 >> 63;
      var0[0] = var1 >>> 1 ^ var5 & -2233785415175766016L;
      var0[1] = var3 >>> 1 | var1 << 63;
   }

   public static void multiplyP(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 << 63 >> 63;
      var1[0] = var2 >>> 1 ^ var6 & -2233785415175766016L;
      var1[1] = var4 >>> 1 | var2 << 63;
   }

   public static void multiplyP3(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 << 61;
      var1[0] = var2 >>> 3 ^ var6 ^ var6 >>> 1 ^ var6 >>> 2 ^ var6 >>> 7;
      var1[1] = var4 >>> 3 | var2 << 61;
   }

   public static void multiplyP4(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 << 60;
      var1[0] = var2 >>> 4 ^ var6 ^ var6 >>> 1 ^ var6 >>> 2 ^ var6 >>> 7;
      var1[1] = var4 >>> 4 | var2 << 60;
   }

   public static void multiplyP7(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 << 57;
      var1[0] = var2 >>> 7 ^ var6 ^ var6 >>> 1 ^ var6 >>> 2 ^ var6 >>> 7;
      var1[1] = var4 >>> 7 | var2 << 57;
   }

   public static void multiplyP8(int[] var0) {
      int var1 = var0[0];
      int var2 = var0[1];
      int var3 = var0[2];
      int var4 = var0[3];
      int var5 = var4 << 24;
      var0[0] = var1 >>> 8 ^ var5 ^ var5 >>> 1 ^ var5 >>> 2 ^ var5 >>> 7;
      var0[1] = var2 >>> 8 | var1 << 24;
      var0[2] = var3 >>> 8 | var2 << 24;
      var0[3] = var4 >>> 8 | var3 << 24;
   }

   public static void multiplyP8(int[] var0, int[] var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var5 << 24;
      var1[0] = var2 >>> 8 ^ var6 ^ var6 >>> 1 ^ var6 >>> 2 ^ var6 >>> 7;
      var1[1] = var3 >>> 8 | var2 << 24;
      var1[2] = var4 >>> 8 | var3 << 24;
      var1[3] = var5 >>> 8 | var4 << 24;
   }

   public static void multiplyP8(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var3 << 56;
      var0[0] = var1 >>> 8 ^ var5 ^ var5 >>> 1 ^ var5 >>> 2 ^ var5 >>> 7;
      var0[1] = var3 >>> 8 | var1 << 56;
   }

   public static void multiplyP8(long[] var0, long[] var1) {
      long var2 = var0[0];
      long var4 = var0[1];
      long var6 = var4 << 56;
      var1[0] = var2 >>> 8 ^ var6 ^ var6 >>> 1 ^ var6 >>> 2 ^ var6 >>> 7;
      var1[1] = var4 >>> 8 | var2 << 56;
   }

   public static void multiplyP16(long[] var0) {
      long var1 = var0[0];
      long var3 = var0[1];
      long var5 = var3 << 48;
      var0[0] = var1 >>> 16 ^ var5 ^ var5 >>> 1 ^ var5 >>> 2 ^ var5 >>> 7;
      var0[1] = var3 >>> 16 | var1 << 48;
   }

   public static long[] pAsLongs() {
      return new long[]{4611686018427387904L, 0L};
   }

   public static void square(long[] var0, long[] var1) {
      long[] var2 = new long[4];
      Interleave.expand64To128Rev(var0[0], var2, 0);
      Interleave.expand64To128Rev(var0[1], var2, 2);
      long var3 = var2[0];
      long var5 = var2[1];
      long var7 = var2[2];
      long var9 = var2[3];
      var5 ^= var9 ^ var9 >>> 1 ^ var9 >>> 2 ^ var9 >>> 7;
      var7 ^= var9 << 63 ^ var9 << 62 ^ var9 << 57;
      var3 ^= var7 ^ var7 >>> 1 ^ var7 >>> 2 ^ var7 >>> 7;
      var5 ^= var7 << 63 ^ var7 << 62 ^ var7 << 57;
      var1[0] = var3;
      var1[1] = var5;
   }

   public static void xor(byte[] var0, byte[] var1) {
      int var2 = 0;

      do {
         var0[var2] ^= var1[var2];
         var0[++var2] = (byte)(var0[var2] ^ var1[var2]);
         var0[++var2] = (byte)(var0[var2] ^ var1[var2]);
         var0[++var2] = (byte)(var0[var2] ^ var1[var2]);
      } while (++var2 < 16);
   }

   public static void xor(byte[] var0, byte[] var1, int var2) {
      int var3 = 0;

      do {
         var0[var3] ^= var1[var2 + var3];
         var0[++var3] = (byte)(var0[var3] ^ var1[var2 + var3]);
         var0[++var3] = (byte)(var0[var3] ^ var1[var2 + var3]);
         var0[++var3] = (byte)(var0[var3] ^ var1[var2 + var3]);
      } while (++var3 < 16);
   }

   public static void xor(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      int var6 = 0;

      do {
         var4[var5 + var6] = (byte)(var0[var1 + var6] ^ var2[var3 + var6]);
         var4[var5 + ++var6] = (byte)(var0[var1 + var6] ^ var2[var3 + var6]);
         var4[var5 + ++var6] = (byte)(var0[var1 + var6] ^ var2[var3 + var6]);
         var4[var5 + ++var6] = (byte)(var0[var1 + var6] ^ var2[var3 + var6]);
      } while (++var6 < 16);
   }

   public static void xor(byte[] var0, byte[] var1, int var2, int var3) {
      while (--var3 >= 0) {
         var0[var3] ^= var1[var2 + var3];
      }
   }

   public static void xor(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      while (--var4 >= 0) {
         var0[var1 + var4] = (byte)(var0[var1 + var4] ^ var2[var3 + var4]);
      }
   }

   public static void xor(byte[] var0, byte[] var1, byte[] var2) {
      int var3 = 0;

      do {
         var2[var3] = (byte)(var0[var3] ^ var1[var3]);
         var2[++var3] = (byte)(var0[var3] ^ var1[var3]);
         var2[++var3] = (byte)(var0[var3] ^ var1[var3]);
         var2[++var3] = (byte)(var0[var3] ^ var1[var3]);
      } while (++var3 < 16);
   }

   public static void xor(int[] var0, int[] var1) {
      var0[0] ^= var1[0];
      var0[1] ^= var1[1];
      var0[2] ^= var1[2];
      var0[3] ^= var1[3];
   }

   public static void xor(int[] var0, int[] var1, int[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
      var2[2] = var0[2] ^ var1[2];
      var2[3] = var0[3] ^ var1[3];
   }

   public static void xor(long[] var0, long[] var1) {
      var0[0] ^= var1[0];
      var0[1] ^= var1[1];
   }

   public static void xor(long[] var0, long[] var1, long[] var2) {
      var2[0] = var0[0] ^ var1[0];
      var2[1] = var0[1] ^ var1[1];
   }

   private static long implMul64(long var0, long var2) {
      long var4 = var0 & 1229782938247303441L;
      long var6 = var0 & 2459565876494606882L;
      long var8 = var0 & 4919131752989213764L;
      long var10 = var0 & -8608480567731124088L;
      long var12 = var2 & 1229782938247303441L;
      long var14 = var2 & 2459565876494606882L;
      long var16 = var2 & 4919131752989213764L;
      long var18 = var2 & -8608480567731124088L;
      long var20 = var4 * var12 ^ var6 * var18 ^ var8 * var16 ^ var10 * var14;
      long var22 = var4 * var14 ^ var6 * var12 ^ var8 * var18 ^ var10 * var16;
      long var24 = var4 * var16 ^ var6 * var14 ^ var8 * var12 ^ var10 * var18;
      long var26 = var4 * var18 ^ var6 * var16 ^ var8 * var14 ^ var10 * var12;
      var20 &= 1229782938247303441L;
      var22 &= 2459565876494606882L;
      var24 &= 4919131752989213764L;
      var26 &= -8608480567731124088L;
      return var20 | var22 | var24 | var26;
   }
}
