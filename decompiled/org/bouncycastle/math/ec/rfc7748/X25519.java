package org.bouncycastle.math.ec.rfc7748;

import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public abstract class X25519 {
   public static final int POINT_SIZE = 32;
   public static final int SCALAR_SIZE = 32;
   private static final int C_A = 486662;
   private static final int C_A24 = 121666;

   public static boolean calculateAgreement(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      scalarMult(var0, var1, var2, var3, var4, var5);
      return !Arrays.areAllZeroes(var4, var5, 32);
   }

   public static void clampPrivateKey(byte[] var0) {
      if (var0.length != 32) {
         throw new IllegalArgumentException("k");
      } else {
         var0[0] = (byte)(var0[0] & 248);
         var0[31] = (byte)(var0[31] & 127);
         var0[31] = (byte)(var0[31] | 64);
      }
   }

   private static int decode32(byte[] var0, int var1) {
      int var2 = var0[var1] & 255;
      var2 |= (var0[++var1] & 255) << 8;
      var2 |= (var0[++var1] & 255) << 16;
      return var2 | var0[++var1] << 24;
   }

   private static void decodeScalar(byte[] var0, int var1, int[] var2) {
      for (int var3 = 0; var3 < 8; var3++) {
         var2[var3] = decode32(var0, var1 + var3 * 4);
      }

      var2[0] &= -8;
      var2[7] &= Integer.MAX_VALUE;
      var2[7] |= 1073741824;
   }

   public static void generatePrivateKey(SecureRandom var0, byte[] var1) {
      if (var1.length != 32) {
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
      int[] var2 = X25519.F.create();
      int[] var3 = X25519.F.create();
      X25519.F.apm(var0, var1, var2, var3);
      X25519.F.sqr(var2, var2);
      X25519.F.sqr(var3, var3);
      X25519.F.mul(var2, var3, var0);
      X25519.F.sub(var2, var3, var2);
      X25519.F.mul(var2, 121666, var1);
      X25519.F.add(var1, var3, var1);
      X25519.F.mul(var1, var2, var1);
   }

   public static void precompute() {
      Ed25519.precompute();
   }

   public static void scalarMult(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5) {
      int[] var6 = new int[8];
      decodeScalar(var0, var1, var6);
      int[] var7 = X25519.F.create();
      X25519.F.decode(var2, var3, var7);
      int[] var8 = X25519.F.create();
      X25519.F.copy(var7, 0, var8, 0);
      int[] var9 = X25519.F.create();
      var9[0] = 1;
      int[] var10 = X25519.F.create();
      var10[0] = 1;
      int[] var11 = X25519.F.create();
      int[] var12 = X25519.F.create();
      int[] var13 = X25519.F.create();
      int var14 = 254;
      int var15 = 1;

      do {
         X25519.F.apm(var10, var11, var12, var10);
         X25519.F.apm(var8, var9, var11, var8);
         X25519.F.mul(var12, var8, var12);
         X25519.F.mul(var10, var11, var10);
         X25519.F.sqr(var11, var11);
         X25519.F.sqr(var8, var8);
         X25519.F.sub(var11, var8, var13);
         X25519.F.mul(var13, 121666, var9);
         X25519.F.add(var9, var8, var9);
         X25519.F.mul(var9, var13, var9);
         X25519.F.mul(var8, var11, var8);
         X25519.F.apm(var12, var10, var10, var11);
         X25519.F.sqr(var10, var10);
         X25519.F.sqr(var11, var11);
         X25519.F.mul(var11, var7, var11);
         int var16 = --var14 >>> 5;
         int var17 = var14 & 31;
         int var18 = var6[var16] >>> var17 & 1;
         var15 ^= var18;
         X25519.F.cswap(var15, var8, var10);
         X25519.F.cswap(var15, var9, var11);
         var15 = var18;
      } while (var14 >= 3);

      for (int var20 = 0; var20 < 3; var20++) {
         pointDouble(var8, var9);
      }

      X25519.F.inv(var9, var9);
      X25519.F.mul(var8, var9, var8);
      X25519.F.normalize(var8);
      X25519.F.encode(var8, var4, var5);
   }

   public static void scalarMultBase(byte[] var0, int var1, byte[] var2, int var3) {
      int[] var4 = X25519.F.create();
      int[] var5 = X25519.F.create();
      Ed25519.scalarMultBaseYZ(X25519.Friend.INSTANCE, var0, var1, var4, var5);
      X25519.F.apm(var5, var4, var4, var5);
      X25519.F.inv(var5, var5);
      X25519.F.mul(var4, var5, var4);
      X25519.F.normalize(var4);
      X25519.F.encode(var4, var2, var3);
   }

   private static class F extends X25519Field {
   }

   public static class Friend {
      private static final X25519.Friend INSTANCE = new X25519.Friend();

      private Friend() {
      }
   }
}
