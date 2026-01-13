package org.bouncycastle.math.ec.rfc7748;

import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;

public abstract class X448 {
   public static final int POINT_SIZE = 56;
   public static final int SCALAR_SIZE = 56;
   private static final int C_A = 156326;
   private static final int C_A24 = 39082;

   public static boolean calculateAgreement(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      scalarMult(var0, var1, var2, var3, var4, var5);
      return !Arrays.areAllZeroes(var4, var5, 56);
   }

   public static void clampPrivateKey(byte[] var0) {
      if (var0.length != 56) {
         throw new IllegalArgumentException("k");
      } else {
         var0[0] = (byte)(var0[0] & 252);
         var0[55] = (byte)(var0[55] | 128);
      }
   }

   private static int decode32(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   private static void decodeScalar(byte[] var0, int var1, int[] var2) {
      for (int var3 = 0; var3 < 14; var3++) {
         var2[var3] = decode32(var0, var1 + var3 * 4);
      }

      var2[0] &= -4;
      var2[13] |= Integer.MIN_VALUE;
   }

   public static void generatePrivateKey(SecureRandom var0, byte[] var1) {
      if (var1.length != 56) {
         throw new IllegalArgumentException("k");
      } else {
         var0.nextBytes(var1);
         clampPrivateKey(var1);
      }
   }

   public static void generatePublicKey(byte[] var0, int var1, byte[] var2, int var3) {
      scalarMultBase(var0, var1, var2, var3);
   }

   private static void pointDouble(int[] var0, int[] var1) {
      int[] var2 = X448.F.create();
      int[] var3 = X448.F.create();
      X448.F.add(var0, var1, var2);
      X448.F.sub(var0, var1, var3);
      X448.F.sqr(var2, var2);
      X448.F.sqr(var3, var3);
      X448.F.mul(var2, var3, var0);
      X448.F.sub(var2, var3, var2);
      X448.F.mul(var2, 39082, var1);
      X448.F.add(var1, var3, var1);
      X448.F.mul(var1, var2, var1);
   }

   public static void precompute() {
      Ed448.precompute();
   }

   public static void scalarMult(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      int[] var6 = new int[14];
      decodeScalar(var0, var1, var6);
      int[] var7 = X448.F.create();
      X448.F.decode(var2, var3, var7);
      int[] var8 = X448.F.create();
      X448.F.copy(var7, 0, var8, 0);
      int[] var9 = X448.F.create();
      var9[0] = 1;
      int[] var10 = X448.F.create();
      var10[0] = 1;
      int[] var11 = X448.F.create();
      int[] var12 = X448.F.create();
      int[] var13 = X448.F.create();
      int var14 = 447;
      int var15 = 1;

      do {
         X448.F.add(var10, var11, var12);
         X448.F.sub(var10, var11, var10);
         X448.F.add(var8, var9, var11);
         X448.F.sub(var8, var9, var8);
         X448.F.mul(var12, var8, var12);
         X448.F.mul(var10, var11, var10);
         X448.F.sqr(var11, var11);
         X448.F.sqr(var8, var8);
         X448.F.sub(var11, var8, var13);
         X448.F.mul(var13, 39082, var9);
         X448.F.add(var9, var8, var9);
         X448.F.mul(var9, var13, var9);
         X448.F.mul(var8, var11, var8);
         X448.F.sub(var12, var10, var11);
         X448.F.add(var12, var10, var10);
         X448.F.sqr(var10, var10);
         X448.F.sqr(var11, var11);
         X448.F.mul(var11, var7, var11);
         int var16 = --var14 >>> 5;
         int var17 = var14 & 31;
         int var18 = var6[var16] >>> var17 & 1;
         var15 ^= var18;
         X448.F.cswap(var15, var8, var10);
         X448.F.cswap(var15, var9, var11);
         var15 = var18;
      } while (var14 >= 2);

      for (int var20 = 0; var20 < 2; var20++) {
         pointDouble(var8, var9);
      }

      X448.F.inv(var9, var9);
      X448.F.mul(var8, var9, var8);
      X448.F.normalize(var8);
      X448.F.encode(var8, var4, var5);
   }

   public static void scalarMultBase(byte[] var0, int var1, byte[] var2, int var3) {
      int[] var4 = X448.F.create();
      int[] var5 = X448.F.create();
      Ed448.scalarMultBaseXY(X448.Friend.INSTANCE, var0, var1, var4, var5);
      X448.F.inv(var4, var4);
      X448.F.mul(var4, var5, var4);
      X448.F.sqr(var4, var4);
      X448.F.normalize(var4);
      X448.F.encode(var4, var2, var3);
   }

   private static class F extends X448Field {
   }

   public static class Friend {
      private static final X448.Friend INSTANCE = new X448.Friend();

      private Friend() {
      }
   }
}
