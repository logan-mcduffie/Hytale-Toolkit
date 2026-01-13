package org.bouncycastle.util;

public class Bytes {
   public static final int BYTES = 1;
   public static final int SIZE = 8;

   public static void xor(int var0, byte[] var1, byte[] var2, byte[] var3) {
      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] = (byte)(var1[var4] ^ var2[var4]);
      }
   }

   public static void xor(int var0, byte[] var1, int var2, byte[] var3, byte[] var4, int var5) {
      for (int var6 = 0; var6 < var0; var6++) {
         var4[var5++] = (byte)(var1[var2++] ^ var3[var6]);
      }
   }

   public static void xor(int var0, byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      for (int var7 = 0; var7 < var0; var7++) {
         var5[var6 + var7] = (byte)(var1[var2 + var7] ^ var3[var4 + var7]);
      }
   }

   public static void xor(int var0, byte[] var1, byte[] var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var0; var5++) {
         var3[var4++] = (byte)(var1[var5] ^ var2[var5]);
      }
   }

   public static void xor(int var0, byte[] var1, byte[] var2, int var3, byte[] var4, int var5) {
      for (int var6 = 0; var6 < var0; var6++) {
         var4[var5++] = (byte)(var1[var6] ^ var2[var3++]);
      }
   }

   public static void xorTo(int var0, byte[] var1, byte[] var2) {
      for (int var3 = 0; var3 < var0; var3++) {
         var2[var3] ^= var1[var3];
      }
   }

   public static void xorTo(int var0, byte[] var1, int var2, byte[] var3) {
      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] ^= var1[var2++];
      }
   }

   public static void xorTo(int var0, byte[] var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < var0; var5++) {
         var3[var4 + var5] = (byte)(var3[var4 + var5] ^ var1[var2 + var5]);
      }
   }
}
