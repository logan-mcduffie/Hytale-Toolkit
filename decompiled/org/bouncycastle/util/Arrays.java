package org.bouncycastle.util;

import java.math.BigInteger;
import java.util.NoSuchElementException;

public final class Arrays {
   private Arrays() {
   }

   public static boolean areAllZeroes(byte[] var0, int var1, int var2) {
      byte var3 = 0;

      for (int var4 = 0; var4 < var2; var4++) {
         var3 |= var0[var1 + var4];
      }

      return var3 == 0;
   }

   public static boolean areEqual(boolean[] var0, boolean[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(byte[] var0, byte[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5) {
      int var6 = var2 - var1;
      int var7 = var5 - var4;
      if (var6 != var7) {
         return false;
      } else {
         for (int var8 = 0; var8 < var6; var8++) {
            if (var0[var1 + var8] != var3[var4 + var8]) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean areEqual(char[] var0, char[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(int[] var0, int[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(long[] var0, long[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(Object[] var0, Object[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean areEqual(short[] var0, short[] var1) {
      return java.util.Arrays.equals(var0, var1);
   }

   public static boolean constantTimeAreEqual(byte[] var0, byte[] var1) {
      if (var0 == null || var1 == null) {
         return false;
      } else if (var0 == var1) {
         return true;
      } else {
         int var2 = var0.length < var1.length ? var0.length : var1.length;
         int var3 = var0.length ^ var1.length;

         for (int var4 = 0; var4 != var2; var4++) {
            var3 |= var0[var4] ^ var1[var4];
         }

         for (int var5 = var2; var5 < var1.length; var5++) {
            var3 |= var1[var5] ^ ~var1[var5];
         }

         return var3 == 0;
      }
   }

   public static boolean constantTimeAreEqual(int var0, byte[] var1, int var2, byte[] var3, int var4) {
      if (null == var1) {
         throw new NullPointerException("'a' cannot be null");
      } else if (null == var3) {
         throw new NullPointerException("'b' cannot be null");
      } else if (var0 < 0) {
         throw new IllegalArgumentException("'len' cannot be negative");
      } else if (var2 > var1.length - var0) {
         throw new IndexOutOfBoundsException("'aOff' value invalid for specified length");
      } else if (var4 > var3.length - var0) {
         throw new IndexOutOfBoundsException("'bOff' value invalid for specified length");
      } else {
         int var5 = 0;

         for (int var6 = 0; var6 < var0; var6++) {
            var5 |= var1[var2 + var6] ^ var3[var4 + var6];
         }

         return 0 == var5;
      }
   }

   public static boolean constantTimeAreEqual(int var0, long[] var1, int var2, long[] var3, int var4) {
      if (null == var1) {
         throw new NullPointerException("'a' cannot be null");
      } else if (null == var3) {
         throw new NullPointerException("'b' cannot be null");
      } else if (var0 < 0) {
         throw new IllegalArgumentException("'len' cannot be negative");
      } else if (var2 > var1.length - var0) {
         throw new IndexOutOfBoundsException("'aOff' value invalid for specified length");
      } else if (var4 > var3.length - var0) {
         throw new IndexOutOfBoundsException("'bOff' value invalid for specified length");
      } else {
         long var5 = 0L;

         for (int var7 = 0; var7 < var0; var7++) {
            var5 |= var1[var2 + var7] ^ var3[var4 + var7];
         }

         return 0L == var5;
      }
   }

   public static boolean constantTimeAreEqual(char[] var0, char[] var1) {
      if (var0 == null || var1 == null) {
         return false;
      } else if (var0 == var1) {
         return true;
      } else {
         int var2 = Math.min(var0.length, var1.length);
         int var3 = var0.length ^ var1.length;

         for (int var4 = 0; var4 != var2; var4++) {
            var3 |= var0[var4] ^ var1[var4];
         }

         for (int var5 = var2; var5 < var1.length; var5++) {
            var3 |= (byte)var1[var5] ^ (byte)(~var1[var5]);
         }

         return var3 == 0;
      }
   }

   public static boolean constantTimeAreEqual(long[] var0, long[] var1) {
      if (var0 == null || var1 == null) {
         return false;
      } else if (var0 == var1) {
         return true;
      } else {
         int var2 = var0.length < var1.length ? var0.length : var1.length;
         long var3 = var0.length ^ var1.length;

         for (int var5 = 0; var5 != var2; var5++) {
            var3 |= var0[var5] ^ var1[var5];
         }

         for (int var6 = var2; var6 < var1.length; var6++) {
            var3 |= var1[var6] ^ ~var1[var6];
         }

         return var3 == 0L;
      }
   }

   public static int compareUnsigned(byte[] var0, byte[] var1) {
      if (var0 == var1) {
         return 0;
      } else if (var0 == null) {
         return -1;
      } else if (var1 == null) {
         return 1;
      } else {
         int var2 = Math.min(var0.length, var1.length);

         for (int var3 = 0; var3 < var2; var3++) {
            int var4 = var0[var3] & 255;
            int var5 = var1[var3] & 255;
            if (var4 < var5) {
               return -1;
            }

            if (var4 > var5) {
               return 1;
            }
         }

         if (var0.length < var1.length) {
            return -1;
         } else {
            return var0.length > var1.length ? 1 : 0;
         }
      }
   }

   public static boolean contains(boolean[] var0, boolean var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(byte[] var0, byte var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(char[] var0, char var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(int[] var0, int var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(long[] var0, long var1) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         if (var0[var3] == var1) {
            return true;
         }
      }

      return false;
   }

   public static boolean contains(short[] var0, short var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         if (var0[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public static void fill(boolean[] var0, boolean var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(boolean[] var0, int var1, int var2, boolean var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(byte[] var0, byte var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(byte[] var0, int var1, int var2, byte var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(char[] var0, char var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(char[] var0, int var1, int var2, char var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(int[] var0, int var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(int[] var0, int var1, int var2, int var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(long[] var0, long var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(long[] var0, int var1, int var2, long var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(Object[] var0, Object var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(Object[] var0, int var1, int var2, Object var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static void fill(short[] var0, short var1) {
      java.util.Arrays.fill(var0, var1);
   }

   public static void fill(short[] var0, int var1, int var2, short var3) {
      java.util.Arrays.fill(var0, var1, var2, var3);
   }

   public static int hashCode(byte[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            var2 *= 257;
            var2 ^= var0[var1];
         }

         return var2;
      }
   }

   public static int hashCode(byte[] var0, int var1, int var2) {
      if (var0 == null) {
         return 0;
      } else {
         int var3 = var2;
         int var4 = var2 + 1;

         while (--var3 >= 0) {
            var4 *= 257;
            var4 ^= var0[var1 + var3];
         }

         return var4;
      }
   }

   public static int hashCode(char[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            var2 *= 257;
            var2 ^= var0[var1];
         }

         return var2;
      }
   }

   public static int hashCode(int[][] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1 = var1 * 257 + hashCode(var0[var2]);
      }

      return var1;
   }

   public static int hashCode(int[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            var2 *= 257;
            var2 ^= var0[var1];
         }

         return var2;
      }
   }

   public static int hashCode(int[] var0, int var1, int var2) {
      if (var0 == null) {
         return 0;
      } else {
         int var3 = var2;
         int var4 = var2 + 1;

         while (--var3 >= 0) {
            var4 *= 257;
            var4 ^= var0[var1 + var3];
         }

         return var4;
      }
   }

   public static int hashCode(long[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            long var3 = var0[var1];
            var2 *= 257;
            var2 ^= (int)var3;
            var2 *= 257;
            var2 ^= (int)(var3 >>> 32);
         }

         return var2;
      }
   }

   public static int hashCode(long[] var0, int var1, int var2) {
      if (var0 == null) {
         return 0;
      } else {
         int var3 = var2;
         int var4 = var2 + 1;

         while (--var3 >= 0) {
            long var5 = var0[var1 + var3];
            var4 *= 257;
            var4 ^= (int)var5;
            var4 *= 257;
            var4 ^= (int)(var5 >>> 32);
         }

         return var4;
      }
   }

   public static int hashCode(short[][][] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1 = var1 * 257 + hashCode(var0[var2]);
      }

      return var1;
   }

   public static int hashCode(short[][] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1 = var1 * 257 + hashCode(var0[var2]);
      }

      return var1;
   }

   public static int hashCode(short[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            var2 *= 257;
            var2 ^= var0[var1] & 255;
         }

         return var2;
      }
   }

   public static int hashCode(Object[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length;
         int var2 = var1 + 1;

         while (--var1 >= 0) {
            var2 *= 257;
            var2 ^= Objects.hashCode(var0[var1]);
         }

         return var2;
      }
   }

   public static boolean[] clone(boolean[] var0) {
      return null == var0 ? null : (boolean[])var0.clone();
   }

   public static byte[] clone(byte[] var0) {
      return null == var0 ? null : (byte[])var0.clone();
   }

   public static char[] clone(char[] var0) {
      return null == var0 ? null : (char[])var0.clone();
   }

   public static int[] clone(int[] var0) {
      return null == var0 ? null : (int[])var0.clone();
   }

   public static long[] clone(long[] var0) {
      return null == var0 ? null : (long[])var0.clone();
   }

   public static short[] clone(short[] var0) {
      return null == var0 ? null : (short[])var0.clone();
   }

   public static BigInteger[] clone(BigInteger[] var0) {
      return null == var0 ? null : (BigInteger[])var0.clone();
   }

   public static byte[] clone(byte[] var0, byte[] var1) {
      if (var0 == null) {
         return null;
      } else if (var1 != null && var1.length == var0.length) {
         System.arraycopy(var0, 0, var1, 0, var1.length);
         return var1;
      } else {
         return clone(var0);
      }
   }

   public static long[] clone(long[] var0, long[] var1) {
      if (var0 == null) {
         return null;
      } else if (var1 != null && var1.length == var0.length) {
         System.arraycopy(var0, 0, var1, 0, var1.length);
         return var1;
      } else {
         return clone(var0);
      }
   }

   public static byte[][] clone(byte[][] var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[][] var1 = new byte[var0.length][];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = clone(var0[var2]);
         }

         return var1;
      }
   }

   public static byte[][][] clone(byte[][][] var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[][][] var1 = new byte[var0.length][][];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = clone(var0[var2]);
         }

         return var1;
      }
   }

   public static boolean[] copyOf(boolean[] var0, int var1) {
      boolean[] var2 = new boolean[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static byte[] copyOf(byte[] var0, int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static char[] copyOf(char[] var0, int var1) {
      char[] var2 = new char[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static int[] copyOf(int[] var0, int var1) {
      int[] var2 = new int[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static long[] copyOf(long[] var0, int var1) {
      long[] var2 = new long[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static short[] copyOf(short[] var0, int var1) {
      short[] var2 = new short[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static BigInteger[] copyOf(BigInteger[] var0, int var1) {
      BigInteger[] var2 = new BigInteger[var1];
      System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
      return var2;
   }

   public static boolean[] copyOfRange(boolean[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      boolean[] var4 = new boolean[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static byte[] copyOfRange(byte[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      byte[] var4 = new byte[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static char[] copyOfRange(char[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      char[] var4 = new char[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static int[] copyOfRange(int[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      int[] var4 = new int[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static long[] copyOfRange(long[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      long[] var4 = new long[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static short[] copyOfRange(short[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      short[] var4 = new short[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   public static BigInteger[] copyOfRange(BigInteger[] var0, int var1, int var2) {
      int var3 = getLength(var1, var2);
      BigInteger[] var4 = new BigInteger[var3];
      System.arraycopy(var0, var1, var4, 0, Math.min(var0.length - var1, var3));
      return var4;
   }

   private static int getLength(int var0, int var1) {
      int var2 = var1 - var0;
      if (var2 < 0) {
         throw new IllegalArgumentException(var0 + " > " + var1);
      } else {
         return var2;
      }
   }

   public static byte[] append(byte[] var0, byte var1) {
      if (var0 == null) {
         return new byte[]{var1};
      } else {
         int var2 = var0.length;
         byte[] var3 = new byte[var2 + 1];
         System.arraycopy(var0, 0, var3, 0, var2);
         var3[var2] = var1;
         return var3;
      }
   }

   public static short[] append(short[] var0, short var1) {
      if (var0 == null) {
         return new short[]{var1};
      } else {
         int var2 = var0.length;
         short[] var3 = new short[var2 + 1];
         System.arraycopy(var0, 0, var3, 0, var2);
         var3[var2] = var1;
         return var3;
      }
   }

   public static int[] append(int[] var0, int var1) {
      if (var0 == null) {
         return new int[]{var1};
      } else {
         int var2 = var0.length;
         int[] var3 = new int[var2 + 1];
         System.arraycopy(var0, 0, var3, 0, var2);
         var3[var2] = var1;
         return var3;
      }
   }

   public static String[] append(String[] var0, String var1) {
      if (var0 == null) {
         return new String[]{var1};
      } else {
         int var2 = var0.length;
         String[] var3 = new String[var2 + 1];
         System.arraycopy(var0, 0, var3, 0, var2);
         var3[var2] = var1;
         return var3;
      }
   }

   public static byte[] concatenate(byte[] var0, byte[] var1) {
      if (null == var0) {
         return clone(var1);
      } else if (null == var1) {
         return clone(var0);
      } else {
         byte[] var2 = new byte[var0.length + var1.length];
         System.arraycopy(var0, 0, var2, 0, var0.length);
         System.arraycopy(var1, 0, var2, var0.length, var1.length);
         return var2;
      }
   }

   public static short[] concatenate(short[] var0, short[] var1) {
      if (null == var0) {
         return clone(var1);
      } else if (null == var1) {
         return clone(var0);
      } else {
         short[] var2 = new short[var0.length + var1.length];
         System.arraycopy(var0, 0, var2, 0, var0.length);
         System.arraycopy(var1, 0, var2, var0.length, var1.length);
         return var2;
      }
   }

   public static byte[] concatenate(byte[] var0, byte[] var1, byte[] var2) {
      if (null == var0) {
         return concatenate(var1, var2);
      } else if (null == var1) {
         return concatenate(var0, var2);
      } else if (null == var2) {
         return concatenate(var0, var1);
      } else {
         byte[] var3 = new byte[var0.length + var1.length + var2.length];
         int var4 = 0;
         System.arraycopy(var0, 0, var3, var4, var0.length);
         var4 += var0.length;
         System.arraycopy(var1, 0, var3, var4, var1.length);
         var4 += var1.length;
         System.arraycopy(var2, 0, var3, var4, var2.length);
         return var3;
      }
   }

   public static byte[] concatenate(byte[] var0, byte[] var1, byte[] var2, byte[] var3) {
      if (null == var0) {
         return concatenate(var1, var2, var3);
      } else if (null == var1) {
         return concatenate(var0, var2, var3);
      } else if (null == var2) {
         return concatenate(var0, var1, var3);
      } else if (null == var3) {
         return concatenate(var0, var1, var2);
      } else {
         byte[] var4 = new byte[var0.length + var1.length + var2.length + var3.length];
         int var5 = 0;
         System.arraycopy(var0, 0, var4, var5, var0.length);
         var5 += var0.length;
         System.arraycopy(var1, 0, var4, var5, var1.length);
         var5 += var1.length;
         System.arraycopy(var2, 0, var4, var5, var2.length);
         var5 += var2.length;
         System.arraycopy(var3, 0, var4, var5, var3.length);
         return var4;
      }
   }

   public static byte[] concatenate(byte[][] var0) {
      int var1 = 0;

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1 += var0[var2].length;
      }

      byte[] var5 = new byte[var1];
      int var3 = 0;

      for (int var4 = 0; var4 != var0.length; var4++) {
         System.arraycopy(var0[var4], 0, var5, var3, var0[var4].length);
         var3 += var0[var4].length;
      }

      return var5;
   }

   public static int[] concatenate(int[] var0, int[] var1) {
      if (null == var0) {
         return clone(var1);
      } else if (null == var1) {
         return clone(var0);
      } else {
         int[] var2 = new int[var0.length + var1.length];
         System.arraycopy(var0, 0, var2, 0, var0.length);
         System.arraycopy(var1, 0, var2, var0.length, var1.length);
         return var2;
      }
   }

   public static byte[] prepend(byte[] var0, byte var1) {
      if (var0 == null) {
         return new byte[]{var1};
      } else {
         int var2 = var0.length;
         byte[] var3 = new byte[var2 + 1];
         System.arraycopy(var0, 0, var3, 1, var2);
         var3[0] = var1;
         return var3;
      }
   }

   public static short[] prepend(short[] var0, short var1) {
      if (var0 == null) {
         return new short[]{var1};
      } else {
         int var2 = var0.length;
         short[] var3 = new short[var2 + 1];
         System.arraycopy(var0, 0, var3, 1, var2);
         var3[0] = var1;
         return var3;
      }
   }

   public static int[] prepend(int[] var0, int var1) {
      if (var0 == null) {
         return new int[]{var1};
      } else {
         int var2 = var0.length;
         int[] var3 = new int[var2 + 1];
         System.arraycopy(var0, 0, var3, 1, var2);
         var3[0] = var1;
         return var3;
      }
   }

   public static byte[] reverse(byte[] var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.length;
         byte[] var3 = new byte[var2];

         while (--var2 >= 0) {
            var3[var2] = var0[var1++];
         }

         return var3;
      }
   }

   public static int[] reverse(int[] var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.length;
         int[] var3 = new int[var2];

         while (--var2 >= 0) {
            var3[var2] = var0[var1++];
         }

         return var3;
      }
   }

   public static void reverse(byte[] var0, byte[] var1) {
      int var2 = var0.length - 1;

      for (int var3 = 0; var3 <= var2; var3++) {
         var1[var3] = var0[var2 - var3];
      }
   }

   public static byte[] reverseInPlace(byte[] var0) {
      if (null == var0) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.length - 1;

         while (var1 < var2) {
            byte var3 = var0[var1];
            byte var4 = var0[var2];
            var0[var1++] = var4;
            var0[var2--] = var3;
         }

         return var0;
      }
   }

   public static void reverseInPlace(byte[] var0, int var1, int var2) {
      int var3 = var1;
      int var4 = var1 + var2 - 1;

      while (var3 < var4) {
         byte var5 = var0[var3];
         byte var6 = var0[var4];
         var0[var3++] = var6;
         var0[var4--] = var5;
      }
   }

   public static short[] reverseInPlace(short[] var0) {
      if (null == var0) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.length - 1;

         while (var1 < var2) {
            short var3 = var0[var1];
            short var4 = var0[var2];
            var0[var1++] = var4;
            var0[var2--] = var3;
         }

         return var0;
      }
   }

   public static int[] reverseInPlace(int[] var0) {
      if (null == var0) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.length - 1;

         while (var1 < var2) {
            int var3 = var0[var1];
            int var4 = var0[var2];
            var0[var1++] = var4;
            var0[var2--] = var3;
         }

         return var0;
      }
   }

   public static void clear(byte[] var0) {
      if (null != var0) {
         java.util.Arrays.fill(var0, (byte)0);
      }
   }

   public static void clear(int[] var0) {
      if (null != var0) {
         java.util.Arrays.fill(var0, 0);
      }
   }

   public static void clear(long[] var0) {
      if (null != var0) {
         java.util.Arrays.fill(var0, 0L);
      }
   }

   public static void clear(char[] var0) {
      if (null != var0) {
         java.util.Arrays.fill(var0, '\u0000');
      }
   }

   public static boolean isNullOrContainsNull(Object[] var0) {
      if (null == var0) {
         return true;
      } else {
         int var1 = var0.length;

         for (int var2 = 0; var2 < var1; var2++) {
            if (null == var0[var2]) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isNullOrEmpty(byte[] var0) {
      return null == var0 || var0.length < 1;
   }

   public static boolean isNullOrEmpty(int[] var0) {
      return null == var0 || var0.length < 1;
   }

   public static boolean isNullOrEmpty(Object[] var0) {
      return null == var0 || var0.length < 1;
   }

   public static boolean segmentsOverlap(int var0, int var1, int var2, int var3) {
      return var1 > 0 && var3 > 0 && var0 - var2 < var3 && var2 - var0 < var1;
   }

   public static void validateRange(byte[] var0, int var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException("'buf' cannot be null");
      } else if ((var1 | var0.length - var1 | var2 - var1 | var0.length - var2) < 0) {
         throw new IndexOutOfBoundsException("buf.length: " + var0.length + ", from: " + var1 + ", to: " + var2);
      }
   }

   public static void validateSegment(byte[] var0, int var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException("'buf' cannot be null");
      } else {
         int var3 = var0.length - var1;
         int var4 = var3 - var2;
         if ((var1 | var2 | var3 | var4) < 0) {
            throw new IndexOutOfBoundsException("buf.length: " + var0.length + ", off: " + var1 + ", len: " + var2);
         }
      }
   }

   public static class Iterator<T> implements java.util.Iterator<T> {
      private final T[] dataArray;
      private int position = 0;

      public Iterator(T[] var1) {
         this.dataArray = (T[])var1;
      }

      @Override
      public boolean hasNext() {
         return this.position < this.dataArray.length;
      }

      @Override
      public T next() {
         if (this.position == this.dataArray.length) {
            throw new NoSuchElementException("Out of elements: " + this.position);
         } else {
            return this.dataArray[this.position++];
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("Cannot remove element from an Array.");
      }
   }
}
