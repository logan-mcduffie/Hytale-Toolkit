package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;

public abstract class Ed25519 {
   private static final int COORD_INTS = 8;
   private static final int POINT_BYTES = 32;
   private static final int SCALAR_INTS = 8;
   private static final int SCALAR_BYTES = 32;
   public static final int PREHASH_SIZE = 64;
   public static final int PUBLIC_KEY_SIZE = 32;
   public static final int SECRET_KEY_SIZE = 32;
   public static final int SIGNATURE_SIZE = 64;
   private static final byte[] DOM2_PREFIX = new byte[]{
      83, 105, 103, 69, 100, 50, 53, 53, 49, 57, 32, 110, 111, 32, 69, 100, 50, 53, 53, 49, 57, 32, 99, 111, 108, 108, 105, 115, 105, 111, 110, 115
   };
   private static final int[] P = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
   private static final int[] ORDER8_y1 = new int[]{1886001095, 1339575613, 1980447930, 258412557, -95215574, -959694548, 2013120334, 2047061138};
   private static final int[] ORDER8_y2 = new int[]{-1886001114, -1339575614, -1980447931, -258412558, 95215573, 959694547, -2013120335, 100422509};
   private static final int[] B_x = new int[]{52811034, 25909283, 8072341, 50637101, 13785486, 30858332, 20483199, 20966410, 43936626, 4379245};
   private static final int[] B_y = new int[]{40265304, 26843545, 6710886, 53687091, 13421772, 40265318, 26843545, 6710886, 53687091, 13421772};
   private static final int[] B128_x = new int[]{12052516, 1174424, 4087752, 38672185, 20040971, 21899680, 55468344, 20105554, 66708015, 9981791};
   private static final int[] B128_y = new int[]{66430571, 45040722, 4842939, 15895846, 18981244, 46308410, 4697481, 8903007, 53646190, 12474675};
   private static final int[] C_d = new int[]{56195235, 47411844, 25868126, 40503822, 57364, 58321048, 30416477, 31930572, 57760639, 10749657};
   private static final int[] C_d2 = new int[]{45281625, 27714825, 18181821, 13898781, 114729, 49533232, 60832955, 30306712, 48412415, 4722099};
   private static final int[] C_d4 = new int[]{23454386, 55429651, 2809210, 27797563, 229458, 31957600, 54557047, 27058993, 29715967, 9444199};
   private static final int WNAF_WIDTH_128 = 4;
   private static final int WNAF_WIDTH_BASE = 6;
   private static final int PRECOMP_BLOCKS = 8;
   private static final int PRECOMP_TEETH = 4;
   private static final int PRECOMP_SPACING = 8;
   private static final int PRECOMP_RANGE = 256;
   private static final int PRECOMP_POINTS = 8;
   private static final int PRECOMP_MASK = 7;
   private static final Object PRECOMP_LOCK = new Object();
   private static Ed25519.PointPrecomp[] PRECOMP_BASE_WNAF = null;
   private static Ed25519.PointPrecomp[] PRECOMP_BASE128_WNAF = null;
   private static int[] PRECOMP_BASE_COMB = null;

   private static byte[] calculateS(byte[] var0, byte[] var1, byte[] var2) {
      int[] var3 = new int[16];
      Scalar25519.decode(var0, var3);
      int[] var4 = new int[8];
      Scalar25519.decode(var1, var4);
      int[] var5 = new int[8];
      Scalar25519.decode(var2, var5);
      Nat256.mulAddTo(var4, var5, var3);
      byte[] var6 = new byte[64];
      Codec.encode32(var3, 0, var3.length, var6, 0);
      return Scalar25519.reduce512(var6);
   }

   private static boolean checkContextVar(byte[] var0, byte var1) {
      return var0 == null && var1 == 0 || var0 != null && var0.length < 256;
   }

   private static int checkPoint(Ed25519.PointAffine var0) {
      int[] var1 = Ed25519.F.create();
      int[] var2 = Ed25519.F.create();
      int[] var3 = Ed25519.F.create();
      Ed25519.F.sqr(var0.x, var2);
      Ed25519.F.sqr(var0.y, var3);
      Ed25519.F.mul(var2, var3, var1);
      Ed25519.F.sub(var2, var3, var2);
      Ed25519.F.mul(var1, C_d, var1);
      Ed25519.F.addOne(var1);
      Ed25519.F.add(var1, var2, var1);
      Ed25519.F.normalize(var1);
      Ed25519.F.normalize(var3);
      return Ed25519.F.isZero(var1) & ~Ed25519.F.isZero(var3);
   }

   private static int checkPoint(Ed25519.PointAccum var0) {
      int[] var1 = Ed25519.F.create();
      int[] var2 = Ed25519.F.create();
      int[] var3 = Ed25519.F.create();
      int[] var4 = Ed25519.F.create();
      Ed25519.F.sqr(var0.x, var2);
      Ed25519.F.sqr(var0.y, var3);
      Ed25519.F.sqr(var0.z, var4);
      Ed25519.F.mul(var2, var3, var1);
      Ed25519.F.sub(var2, var3, var2);
      Ed25519.F.mul(var2, var4, var2);
      Ed25519.F.sqr(var4, var4);
      Ed25519.F.mul(var1, C_d, var1);
      Ed25519.F.add(var1, var4, var1);
      Ed25519.F.add(var1, var2, var1);
      Ed25519.F.normalize(var1);
      Ed25519.F.normalize(var3);
      Ed25519.F.normalize(var4);
      return Ed25519.F.isZero(var1) & ~Ed25519.F.isZero(var3) & ~Ed25519.F.isZero(var4);
   }

   private static boolean checkPointFullVar(byte[] var0) {
      int var1 = Codec.decode32(var0, 28) & 2147483647;
      int var2 = var1;
      int var3 = var1 ^ P[7];
      int var4 = var1 ^ ORDER8_y1[7];
      int var5 = var1 ^ ORDER8_y2[7];

      for (int var6 = 6; var6 > 0; var6--) {
         int var7 = Codec.decode32(var0, var6 * 4);
         var2 |= var7;
         var3 |= var7 ^ P[var6];
         var4 |= var7 ^ ORDER8_y1[var6];
         var5 |= var7 ^ ORDER8_y2[var6];
      }

      int var10 = Codec.decode32(var0, 0);
      if (var2 == 0 && var10 + Integer.MIN_VALUE <= -2147483647) {
         return false;
      } else if (var3 == 0 && var10 + Integer.MIN_VALUE >= P[0] - 1 + Integer.MIN_VALUE) {
         return false;
      } else {
         var4 |= var10 ^ ORDER8_y1[0];
         var5 |= var10 ^ ORDER8_y2[0];
         return var4 != 0 & var5 != 0;
      }
   }

   private static boolean checkPointOrderVar(Ed25519.PointAffine var0) {
      Ed25519.PointAccum var1 = new Ed25519.PointAccum();
      scalarMultOrderVar(var0, var1);
      return normalizeToNeutralElementVar(var1);
   }

   private static boolean checkPointVar(byte[] var0) {
      if ((Codec.decode32(var0, 28) & 2147483647) < P[7]) {
         return true;
      } else {
         int[] var1 = new int[8];
         Codec.decode32(var0, 0, var1, 0, 8);
         var1[7] &= Integer.MAX_VALUE;
         return !Nat256.gte(var1, P);
      }
   }

   private static byte[] copy(byte[] var0, int var1, int var2) {
      byte[] var3 = new byte[var2];
      System.arraycopy(var0, var1, var3, 0, var2);
      return var3;
   }

   private static Digest createDigest() {
      SHA512Digest var0 = new SHA512Digest();
      if (var0.getDigestSize() != 64) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   public static Digest createPrehash() {
      return createDigest();
   }

   private static boolean decodePointVar(byte[] var0, boolean var1, Ed25519.PointAffine var2) {
      int var3 = (var0[31] & 128) >>> 7;
      Ed25519.F.decode(var0, var2.y);
      int[] var4 = Ed25519.F.create();
      int[] var5 = Ed25519.F.create();
      Ed25519.F.sqr(var2.y, var4);
      Ed25519.F.mul(C_d, var4, var5);
      Ed25519.F.subOne(var4);
      Ed25519.F.addOne(var5);
      if (!Ed25519.F.sqrtRatioVar(var4, var5, var2.x)) {
         return false;
      } else {
         Ed25519.F.normalize(var2.x);
         if (var3 == 1 && Ed25519.F.isZeroVar(var2.x)) {
            return false;
         } else {
            if (var1 ^ var3 != (var2.x[0] & 1)) {
               Ed25519.F.negate(var2.x, var2.x);
               Ed25519.F.normalize(var2.x);
            }

            return true;
         }
      }
   }

   private static void dom2(Digest var0, byte var1, byte[] var2) {
      int var3 = DOM2_PREFIX.length;
      byte[] var4 = new byte[var3 + 2 + var2.length];
      System.arraycopy(DOM2_PREFIX, 0, var4, 0, var3);
      var4[var3] = var1;
      var4[var3 + 1] = (byte)var2.length;
      System.arraycopy(var2, 0, var4, var3 + 2, var2.length);
      var0.update(var4, 0, var4.length);
   }

   private static void encodePoint(Ed25519.PointAffine var0, byte[] var1, int var2) {
      Ed25519.F.encode(var0.y, var1, var2);
      var1[var2 + 32 - 1] = (byte)(var1[var2 + 32 - 1] | (var0.x[0] & 1) << 7);
   }

   public static void encodePublicPoint(Ed25519.PublicPoint var0, byte[] var1, int var2) {
      Ed25519.F.encode(var0.data, 10, var1, var2);
      var1[var2 + 32 - 1] = (byte)(var1[var2 + 32 - 1] | (var0.data[0] & 1) << 7);
   }

   private static int encodeResult(Ed25519.PointAccum var0, byte[] var1, int var2) {
      Ed25519.PointAffine var3 = new Ed25519.PointAffine();
      normalizeToAffine(var0, var3);
      int var4 = checkPoint(var3);
      encodePoint(var3, var1, var2);
      return var4;
   }

   private static Ed25519.PublicPoint exportPoint(Ed25519.PointAffine var0) {
      int[] var1 = new int[20];
      Ed25519.F.copy(var0.x, 0, var1, 0);
      Ed25519.F.copy(var0.y, 0, var1, 10);
      return new Ed25519.PublicPoint(var1);
   }

   public static void generatePrivateKey(SecureRandom var0, byte[] var1) {
      if (var1.length != 32) {
         throw new IllegalArgumentException("k");
      } else {
         var0.nextBytes(var1);
      }
   }

   public static void generatePublicKey(byte[] var0, int var1, byte[] var2, int var3) {
      Digest var4 = createDigest();
      byte[] var5 = new byte[64];
      var4.update(var0, var1, 32);
      var4.doFinal(var5, 0);
      byte[] var6 = new byte[32];
      pruneScalar(var5, 0, var6);
      scalarMultBaseEncoded(var6, var2, var3);
   }

   public static Ed25519.PublicPoint generatePublicKey(byte[] var0, int var1) {
      Digest var2 = createDigest();
      byte[] var3 = new byte[64];
      var2.update(var0, var1, 32);
      var2.doFinal(var3, 0);
      byte[] var4 = new byte[32];
      pruneScalar(var3, 0, var4);
      Ed25519.PointAccum var5 = new Ed25519.PointAccum();
      scalarMultBase(var4, var5);
      Ed25519.PointAffine var6 = new Ed25519.PointAffine();
      normalizeToAffine(var5, var6);
      if (0 == checkPoint(var6)) {
         throw new IllegalStateException();
      } else {
         return exportPoint(var6);
      }
   }

   private static int getWindow4(int[] var0, int var1) {
      int var2 = var1 >>> 3;
      int var3 = (var1 & 7) << 2;
      return var0[var2] >>> var3 & 15;
   }

   private static void groupCombBits(int[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = Interleave.shuffle2(var0[var1]);
      }
   }

   private static void implSign(
      Digest var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, byte var6, byte[] var7, int var8, int var9, byte[] var10, int var11
   ) {
      if (var5 != null) {
         dom2(var0, var6, var5);
      }

      var0.update(var1, 32, 32);
      var0.update(var7, var8, var9);
      var0.doFinal(var1, 0);
      byte[] var12 = Scalar25519.reduce512(var1);
      byte[] var13 = new byte[32];
      scalarMultBaseEncoded(var12, var13, 0);
      if (var5 != null) {
         dom2(var0, var6, var5);
      }

      var0.update(var13, 0, 32);
      var0.update(var3, var4, 32);
      var0.update(var7, var8, var9);
      var0.doFinal(var1, 0);
      byte[] var14 = Scalar25519.reduce512(var1);
      byte[] var15 = calculateS(var12, var14, var2);
      System.arraycopy(var13, 0, var10, var11, 32);
      System.arraycopy(var15, 0, var10, var11 + 32, 32);
   }

   private static void implSign(byte[] var0, int var1, byte[] var2, byte var3, byte[] var4, int var5, int var6, byte[] var7, int var8) {
      if (!checkContextVar(var2, var3)) {
         throw new IllegalArgumentException("ctx");
      } else {
         Digest var9 = createDigest();
         byte[] var10 = new byte[64];
         var9.update(var0, var1, 32);
         var9.doFinal(var10, 0);
         byte[] var11 = new byte[32];
         pruneScalar(var10, 0, var11);
         byte[] var12 = new byte[32];
         scalarMultBaseEncoded(var11, var12, 0);
         implSign(var9, var10, var11, var12, 0, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   private static void implSign(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte var5, byte[] var6, int var7, int var8, byte[] var9, int var10) {
      if (!checkContextVar(var4, var5)) {
         throw new IllegalArgumentException("ctx");
      } else {
         Digest var11 = createDigest();
         byte[] var12 = new byte[64];
         var11.update(var0, var1, 32);
         var11.doFinal(var12, 0);
         byte[] var13 = new byte[32];
         pruneScalar(var12, 0, var13);
         implSign(var11, var12, var13, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   private static boolean implVerify(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte var5, byte[] var6, int var7, int var8) {
      if (!checkContextVar(var4, var5)) {
         throw new IllegalArgumentException("ctx");
      } else {
         byte[] var9 = copy(var0, var1, 32);
         byte[] var10 = copy(var0, var1 + 32, 32);
         byte[] var11 = copy(var2, var3, 32);
         if (!checkPointVar(var9)) {
            return false;
         } else {
            int[] var12 = new int[8];
            if (!Scalar25519.checkVar(var10, var12)) {
               return false;
            } else if (!checkPointFullVar(var11)) {
               return false;
            } else {
               Ed25519.PointAffine var13 = new Ed25519.PointAffine();
               if (!decodePointVar(var9, true, var13)) {
                  return false;
               } else {
                  Ed25519.PointAffine var14 = new Ed25519.PointAffine();
                  if (!decodePointVar(var11, true, var14)) {
                     return false;
                  } else {
                     Digest var15 = createDigest();
                     byte[] var16 = new byte[64];
                     if (var4 != null) {
                        dom2(var15, var5, var4);
                     }

                     var15.update(var9, 0, 32);
                     var15.update(var11, 0, 32);
                     var15.update(var6, var7, var8);
                     var15.doFinal(var16, 0);
                     byte[] var17 = Scalar25519.reduce512(var16);
                     int[] var18 = new int[8];
                     Scalar25519.decode(var17, var18);
                     int[] var19 = new int[4];
                     int[] var20 = new int[4];
                     if (!Scalar25519.reduceBasisVar(var18, var19, var20)) {
                        throw new IllegalStateException();
                     } else {
                        Scalar25519.multiply128Var(var12, var20, var12);
                        Ed25519.PointAccum var21 = new Ed25519.PointAccum();
                        scalarMultStraus128Var(var12, var19, var14, var20, var13, var21);
                        return normalizeToNeutralElementVar(var21);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean implVerify(byte[] var0, int var1, Ed25519.PublicPoint var2, byte[] var3, byte var4, byte[] var5, int var6, int var7) {
      if (!checkContextVar(var3, var4)) {
         throw new IllegalArgumentException("ctx");
      } else {
         byte[] var8 = copy(var0, var1, 32);
         byte[] var9 = copy(var0, var1 + 32, 32);
         if (!checkPointVar(var8)) {
            return false;
         } else {
            int[] var10 = new int[8];
            if (!Scalar25519.checkVar(var9, var10)) {
               return false;
            } else {
               Ed25519.PointAffine var11 = new Ed25519.PointAffine();
               if (!decodePointVar(var8, true, var11)) {
                  return false;
               } else {
                  Ed25519.PointAffine var12 = new Ed25519.PointAffine();
                  Ed25519.F.negate(var2.data, var12.x);
                  Ed25519.F.copy(var2.data, 10, var12.y, 0);
                  byte[] var13 = new byte[32];
                  encodePublicPoint(var2, var13, 0);
                  Digest var14 = createDigest();
                  byte[] var15 = new byte[64];
                  if (var3 != null) {
                     dom2(var14, var4, var3);
                  }

                  var14.update(var8, 0, 32);
                  var14.update(var13, 0, 32);
                  var14.update(var5, var6, var7);
                  var14.doFinal(var15, 0);
                  byte[] var16 = Scalar25519.reduce512(var15);
                  int[] var17 = new int[8];
                  Scalar25519.decode(var16, var17);
                  int[] var18 = new int[4];
                  int[] var19 = new int[4];
                  if (!Scalar25519.reduceBasisVar(var17, var18, var19)) {
                     throw new IllegalStateException();
                  } else {
                     Scalar25519.multiply128Var(var10, var19, var10);
                     Ed25519.PointAccum var20 = new Ed25519.PointAccum();
                     scalarMultStraus128Var(var10, var18, var12, var19, var11, var20);
                     return normalizeToNeutralElementVar(var20);
                  }
               }
            }
         }
      }
   }

   private static void invertDoubleZs(Ed25519.PointExtended[] var0) {
      int var1 = var0.length;
      int[] var2 = Ed25519.F.createTable(var1);
      int[] var3 = Ed25519.F.create();
      Ed25519.F.copy(var0[0].z, 0, var3, 0);
      Ed25519.F.copy(var3, 0, var2, 0);
      int var4 = 0;

      while (++var4 < var1) {
         Ed25519.F.mul(var3, var0[var4].z, var3);
         Ed25519.F.copy(var3, 0, var2, var4 * 10);
      }

      Ed25519.F.add(var3, var3, var3);
      Ed25519.F.invVar(var3, var3);
      var4--;
      int[] var5 = Ed25519.F.create();

      while (var4 > 0) {
         int var6 = var4--;
         Ed25519.F.copy(var2, var4 * 10, var5, 0);
         Ed25519.F.mul(var5, var3, var5);
         Ed25519.F.mul(var3, var0[var6].z, var3);
         Ed25519.F.copy(var5, 0, var0[var6].z, 0);
      }

      Ed25519.F.copy(var3, 0, var0[0].z, 0);
   }

   private static void normalizeToAffine(Ed25519.PointAccum var0, Ed25519.PointAffine var1) {
      Ed25519.F.inv(var0.z, var1.y);
      Ed25519.F.mul(var1.y, var0.x, var1.x);
      Ed25519.F.mul(var1.y, var0.y, var1.y);
      Ed25519.F.normalize(var1.x);
      Ed25519.F.normalize(var1.y);
   }

   private static boolean normalizeToNeutralElementVar(Ed25519.PointAccum var0) {
      Ed25519.F.normalize(var0.x);
      Ed25519.F.normalize(var0.y);
      Ed25519.F.normalize(var0.z);
      return Ed25519.F.isZeroVar(var0.x) && !Ed25519.F.isZeroVar(var0.y) && Ed25519.F.areEqualVar(var0.y, var0.z);
   }

   private static void pointAdd(Ed25519.PointExtended var0, Ed25519.PointExtended var1, Ed25519.PointExtended var2, Ed25519.PointTemp var3) {
      int[] var4 = var2.x;
      int[] var5 = var2.y;
      int[] var6 = var3.r0;
      int[] var7 = var3.r1;
      Ed25519.F.apm(var0.y, var0.x, var5, var4);
      Ed25519.F.apm(var1.y, var1.x, var7, var6);
      Ed25519.F.mul(var4, var6, var4);
      Ed25519.F.mul(var5, var7, var5);
      Ed25519.F.mul(var0.t, var1.t, var6);
      Ed25519.F.mul(var6, C_d2, var6);
      Ed25519.F.add(var0.z, var0.z, var7);
      Ed25519.F.mul(var7, var1.z, var7);
      Ed25519.F.apm(var5, var4, var5, var4);
      Ed25519.F.apm(var7, var6, var7, var6);
      Ed25519.F.mul(var4, var5, var2.t);
      Ed25519.F.mul(var6, var7, var2.z);
      Ed25519.F.mul(var4, var6, var2.x);
      Ed25519.F.mul(var5, var7, var2.y);
   }

   private static void pointAdd(Ed25519.PointPrecomp var0, Ed25519.PointAccum var1, Ed25519.PointTemp var2) {
      int[] var3 = var1.x;
      int[] var4 = var1.y;
      int[] var5 = var2.r0;
      int[] var6 = var1.u;
      int[] var9 = var1.v;
      Ed25519.F.apm(var1.y, var1.x, var4, var3);
      Ed25519.F.mul(var3, var0.ymx_h, var3);
      Ed25519.F.mul(var4, var0.ypx_h, var4);
      Ed25519.F.mul(var1.u, var1.v, var5);
      Ed25519.F.mul(var5, var0.xyd, var5);
      Ed25519.F.apm(var4, var3, var9, var6);
      Ed25519.F.apm(var1.z, var5, var4, var3);
      Ed25519.F.mul(var3, var4, var1.z);
      Ed25519.F.mul(var3, var6, var1.x);
      Ed25519.F.mul(var4, var9, var1.y);
   }

   private static void pointAdd(Ed25519.PointPrecompZ var0, Ed25519.PointAccum var1, Ed25519.PointTemp var2) {
      int[] var3 = var1.x;
      int[] var4 = var1.y;
      int[] var5 = var2.r0;
      int[] var6 = var1.z;
      int[] var7 = var1.u;
      int[] var10 = var1.v;
      Ed25519.F.apm(var1.y, var1.x, var4, var3);
      Ed25519.F.mul(var3, var0.ymx_h, var3);
      Ed25519.F.mul(var4, var0.ypx_h, var4);
      Ed25519.F.mul(var1.u, var1.v, var5);
      Ed25519.F.mul(var5, var0.xyd, var5);
      Ed25519.F.mul(var1.z, var0.z, var6);
      Ed25519.F.apm(var4, var3, var10, var7);
      Ed25519.F.apm(var6, var5, var4, var3);
      Ed25519.F.mul(var3, var4, var1.z);
      Ed25519.F.mul(var3, var7, var1.x);
      Ed25519.F.mul(var4, var10, var1.y);
   }

   private static void pointAddVar(boolean var0, Ed25519.PointPrecomp var1, Ed25519.PointAccum var2, Ed25519.PointTemp var3) {
      int[] var4 = var2.x;
      int[] var5 = var2.y;
      int[] var6 = var3.r0;
      int[] var7 = var2.u;
      int[] var10 = var2.v;
      int[] var11;
      int[] var12;
      if (var0) {
         var11 = var5;
         var12 = var4;
      } else {
         var11 = var4;
         var12 = var5;
      }

      Ed25519.F.apm(var2.y, var2.x, var5, var4);
      Ed25519.F.mul(var11, var1.ymx_h, var11);
      Ed25519.F.mul(var12, var1.ypx_h, var12);
      Ed25519.F.mul(var2.u, var2.v, var6);
      Ed25519.F.mul(var6, var1.xyd, var6);
      Ed25519.F.apm(var5, var4, var10, var7);
      Ed25519.F.apm(var2.z, var6, var12, var11);
      Ed25519.F.mul(var4, var5, var2.z);
      Ed25519.F.mul(var4, var7, var2.x);
      Ed25519.F.mul(var5, var10, var2.y);
   }

   private static void pointAddVar(boolean var0, Ed25519.PointPrecompZ var1, Ed25519.PointAccum var2, Ed25519.PointTemp var3) {
      int[] var4 = var2.x;
      int[] var5 = var2.y;
      int[] var6 = var3.r0;
      int[] var7 = var2.z;
      int[] var8 = var2.u;
      int[] var11 = var2.v;
      int[] var12;
      int[] var13;
      if (var0) {
         var12 = var5;
         var13 = var4;
      } else {
         var12 = var4;
         var13 = var5;
      }

      Ed25519.F.apm(var2.y, var2.x, var5, var4);
      Ed25519.F.mul(var12, var1.ymx_h, var12);
      Ed25519.F.mul(var13, var1.ypx_h, var13);
      Ed25519.F.mul(var2.u, var2.v, var6);
      Ed25519.F.mul(var6, var1.xyd, var6);
      Ed25519.F.mul(var2.z, var1.z, var7);
      Ed25519.F.apm(var5, var4, var11, var8);
      Ed25519.F.apm(var7, var6, var13, var12);
      Ed25519.F.mul(var4, var5, var2.z);
      Ed25519.F.mul(var4, var8, var2.x);
      Ed25519.F.mul(var5, var11, var2.y);
   }

   private static void pointCopy(Ed25519.PointAccum var0, Ed25519.PointExtended var1) {
      Ed25519.F.copy(var0.x, 0, var1.x, 0);
      Ed25519.F.copy(var0.y, 0, var1.y, 0);
      Ed25519.F.copy(var0.z, 0, var1.z, 0);
      Ed25519.F.mul(var0.u, var0.v, var1.t);
   }

   private static void pointCopy(Ed25519.PointAffine var0, Ed25519.PointExtended var1) {
      Ed25519.F.copy(var0.x, 0, var1.x, 0);
      Ed25519.F.copy(var0.y, 0, var1.y, 0);
      Ed25519.F.one(var1.z);
      Ed25519.F.mul(var0.x, var0.y, var1.t);
   }

   private static void pointCopy(Ed25519.PointExtended var0, Ed25519.PointPrecompZ var1) {
      Ed25519.F.apm(var0.y, var0.x, var1.ypx_h, var1.ymx_h);
      Ed25519.F.mul(var0.t, C_d2, var1.xyd);
      Ed25519.F.add(var0.z, var0.z, var1.z);
   }

   private static void pointDouble(Ed25519.PointAccum var0) {
      int[] var1 = var0.x;
      int[] var2 = var0.y;
      int[] var3 = var0.z;
      int[] var4 = var0.u;
      int[] var7 = var0.v;
      Ed25519.F.add(var0.x, var0.y, var4);
      Ed25519.F.sqr(var0.x, var1);
      Ed25519.F.sqr(var0.y, var2);
      Ed25519.F.sqr(var0.z, var3);
      Ed25519.F.add(var3, var3, var3);
      Ed25519.F.apm(var1, var2, var7, var2);
      Ed25519.F.sqr(var4, var4);
      Ed25519.F.sub(var7, var4, var4);
      Ed25519.F.add(var3, var2, var1);
      Ed25519.F.carry(var1);
      Ed25519.F.mul(var1, var2, var0.z);
      Ed25519.F.mul(var1, var4, var0.x);
      Ed25519.F.mul(var2, var7, var0.y);
   }

   private static void pointLookup(int var0, int var1, Ed25519.PointPrecomp var2) {
      int var3 = var0 * 8 * 3 * 10;

      for (int var4 = 0; var4 < 8; var4++) {
         int var5 = (var4 ^ var1) - 1 >> 31;
         Ed25519.F.cmov(var5, PRECOMP_BASE_COMB, var3, var2.ymx_h, 0);
         var3 += 10;
         Ed25519.F.cmov(var5, PRECOMP_BASE_COMB, var3, var2.ypx_h, 0);
         var3 += 10;
         Ed25519.F.cmov(var5, PRECOMP_BASE_COMB, var3, var2.xyd, 0);
         var3 += 10;
      }
   }

   private static void pointLookupZ(int[] var0, int var1, int[] var2, Ed25519.PointPrecompZ var3) {
      int var4 = getWindow4(var0, var1);
      int var5 = var4 >>> 3 ^ 1;
      int var6 = (var4 ^ -var5) & 7;
      int var7 = 0;

      for (int var8 = 0; var7 < 8; var7++) {
         int var9 = (var7 ^ var6) - 1 >> 31;
         Ed25519.F.cmov(var9, var2, var8, var3.ymx_h, 0);
         var8 += 10;
         Ed25519.F.cmov(var9, var2, var8, var3.ypx_h, 0);
         var8 += 10;
         Ed25519.F.cmov(var9, var2, var8, var3.xyd, 0);
         var8 += 10;
         Ed25519.F.cmov(var9, var2, var8, var3.z, 0);
         var8 += 10;
      }

      Ed25519.F.cswap(var5, var3.ymx_h, var3.ypx_h);
      Ed25519.F.cnegate(var5, var3.xyd);
   }

   private static void pointPrecompute(Ed25519.PointAffine var0, Ed25519.PointExtended[] var1, int var2, int var3, Ed25519.PointTemp var4) {
      pointCopy(var0, var1[var2] = new Ed25519.PointExtended());
      Ed25519.PointExtended var5 = new Ed25519.PointExtended();
      pointAdd(var1[var2], var1[var2], var5, var4);

      for (int var6 = 1; var6 < var3; var6++) {
         pointAdd(var1[var2 + var6 - 1], var5, var1[var2 + var6] = new Ed25519.PointExtended(), var4);
      }
   }

   private static int[] pointPrecomputeZ(Ed25519.PointAffine var0, int var1, Ed25519.PointTemp var2) {
      Ed25519.PointExtended var3 = new Ed25519.PointExtended();
      pointCopy(var0, var3);
      Ed25519.PointExtended var4 = new Ed25519.PointExtended();
      pointAdd(var3, var3, var4, var2);
      Ed25519.PointPrecompZ var5 = new Ed25519.PointPrecompZ();
      int[] var6 = Ed25519.F.createTable(var1 * 4);
      int var7 = 0;
      int var8 = 0;

      while (true) {
         pointCopy(var3, var5);
         Ed25519.F.copy(var5.ymx_h, 0, var6, var7);
         var7 += 10;
         Ed25519.F.copy(var5.ypx_h, 0, var6, var7);
         var7 += 10;
         Ed25519.F.copy(var5.xyd, 0, var6, var7);
         var7 += 10;
         Ed25519.F.copy(var5.z, 0, var6, var7);
         var7 += 10;
         if (++var8 == var1) {
            return var6;
         }

         pointAdd(var3, var4, var3, var2);
      }
   }

   private static void pointPrecomputeZ(Ed25519.PointAffine var0, Ed25519.PointPrecompZ[] var1, int var2, Ed25519.PointTemp var3) {
      Ed25519.PointExtended var4 = new Ed25519.PointExtended();
      pointCopy(var0, var4);
      Ed25519.PointExtended var5 = new Ed25519.PointExtended();
      pointAdd(var4, var4, var5, var3);
      int var6 = 0;

      while (true) {
         Ed25519.PointPrecompZ var7 = var1[var6] = new Ed25519.PointPrecompZ();
         pointCopy(var4, var7);
         if (++var6 == var2) {
            return;
         }

         pointAdd(var4, var5, var4, var3);
      }
   }

   private static void pointSetNeutral(Ed25519.PointAccum var0) {
      Ed25519.F.zero(var0.x);
      Ed25519.F.one(var0.y);
      Ed25519.F.one(var0.z);
      Ed25519.F.zero(var0.u);
      Ed25519.F.one(var0.v);
   }

   public static void precompute() {
      synchronized (PRECOMP_LOCK) {
         if (PRECOMP_BASE_COMB == null) {
            byte var1 = 16;
            byte var2 = 64;
            int var3 = var1 * 2 + var2;
            Ed25519.PointExtended[] var4 = new Ed25519.PointExtended[var3];
            Ed25519.PointTemp var5 = new Ed25519.PointTemp();
            Ed25519.PointAffine var6 = new Ed25519.PointAffine();
            Ed25519.F.copy(B_x, 0, var6.x, 0);
            Ed25519.F.copy(B_y, 0, var6.y, 0);
            pointPrecompute(var6, var4, 0, var1, var5);
            Ed25519.PointAffine var7 = new Ed25519.PointAffine();
            Ed25519.F.copy(B128_x, 0, var7.x, 0);
            Ed25519.F.copy(B128_y, 0, var7.y, 0);
            pointPrecompute(var7, var4, var1, var1, var5);
            Ed25519.PointAccum var8 = new Ed25519.PointAccum();
            Ed25519.F.copy(B_x, 0, var8.x, 0);
            Ed25519.F.copy(B_y, 0, var8.y, 0);
            Ed25519.F.one(var8.z);
            Ed25519.F.copy(var8.x, 0, var8.u, 0);
            Ed25519.F.copy(var8.y, 0, var8.v, 0);
            int var9 = var1 * 2;
            Ed25519.PointExtended[] var10 = new Ed25519.PointExtended[4];

            for (int var11 = 0; var11 < 4; var11++) {
               var10[var11] = new Ed25519.PointExtended();
            }

            Ed25519.PointExtended var19 = new Ed25519.PointExtended();

            for (int var12 = 0; var12 < 8; var12++) {
               Ed25519.PointExtended var13 = var4[var9++] = new Ed25519.PointExtended();

               for (int var14 = 0; var14 < 4; var14++) {
                  if (var14 == 0) {
                     pointCopy(var8, var13);
                  } else {
                     pointCopy(var8, var19);
                     pointAdd(var13, var19, var13, var5);
                  }

                  pointDouble(var8);
                  pointCopy(var8, var10[var14]);
                  if (var12 + var14 != 10) {
                     for (int var15 = 1; var15 < 8; var15++) {
                        pointDouble(var8);
                     }
                  }
               }

               Ed25519.F.negate(var13.x, var13.x);
               Ed25519.F.negate(var13.t, var13.t);

               for (int var28 = 0; var28 < 3; var28++) {
                  int var32 = 1 << var28;

                  for (int var16 = 0; var16 < var32; var9++) {
                     var4[var9] = new Ed25519.PointExtended();
                     pointAdd(var4[var9 - var32], var10[var28], var4[var9], var5);
                     var16++;
                  }
               }
            }

            invertDoubleZs(var4);
            PRECOMP_BASE_WNAF = new Ed25519.PointPrecomp[var1];

            for (int var20 = 0; var20 < var1; var20++) {
               Ed25519.PointExtended var23 = var4[var20];
               Ed25519.PointPrecomp var29 = PRECOMP_BASE_WNAF[var20] = new Ed25519.PointPrecomp();
               Ed25519.F.mul(var23.x, var23.z, var23.x);
               Ed25519.F.mul(var23.y, var23.z, var23.y);
               Ed25519.F.apm(var23.y, var23.x, var29.ypx_h, var29.ymx_h);
               Ed25519.F.mul(var23.x, var23.y, var29.xyd);
               Ed25519.F.mul(var29.xyd, C_d4, var29.xyd);
               Ed25519.F.normalize(var29.ymx_h);
               Ed25519.F.normalize(var29.ypx_h);
               Ed25519.F.normalize(var29.xyd);
            }

            PRECOMP_BASE128_WNAF = new Ed25519.PointPrecomp[var1];

            for (int var21 = 0; var21 < var1; var21++) {
               Ed25519.PointExtended var24 = var4[var1 + var21];
               Ed25519.PointPrecomp var30 = PRECOMP_BASE128_WNAF[var21] = new Ed25519.PointPrecomp();
               Ed25519.F.mul(var24.x, var24.z, var24.x);
               Ed25519.F.mul(var24.y, var24.z, var24.y);
               Ed25519.F.apm(var24.y, var24.x, var30.ypx_h, var30.ymx_h);
               Ed25519.F.mul(var24.x, var24.y, var30.xyd);
               Ed25519.F.mul(var30.xyd, C_d4, var30.xyd);
               Ed25519.F.normalize(var30.ymx_h);
               Ed25519.F.normalize(var30.ypx_h);
               Ed25519.F.normalize(var30.xyd);
            }

            PRECOMP_BASE_COMB = Ed25519.F.createTable(var2 * 3);
            Ed25519.PointPrecomp var22 = new Ed25519.PointPrecomp();
            int var25 = 0;

            for (int var31 = var1 * 2; var31 < var3; var31++) {
               Ed25519.PointExtended var33 = var4[var31];
               Ed25519.F.mul(var33.x, var33.z, var33.x);
               Ed25519.F.mul(var33.y, var33.z, var33.y);
               Ed25519.F.apm(var33.y, var33.x, var22.ypx_h, var22.ymx_h);
               Ed25519.F.mul(var33.x, var33.y, var22.xyd);
               Ed25519.F.mul(var22.xyd, C_d4, var22.xyd);
               Ed25519.F.normalize(var22.ymx_h);
               Ed25519.F.normalize(var22.ypx_h);
               Ed25519.F.normalize(var22.xyd);
               Ed25519.F.copy(var22.ymx_h, 0, PRECOMP_BASE_COMB, var25);
               var25 += 10;
               Ed25519.F.copy(var22.ypx_h, 0, PRECOMP_BASE_COMB, var25);
               var25 += 10;
               Ed25519.F.copy(var22.xyd, 0, PRECOMP_BASE_COMB, var25);
               var25 += 10;
            }
         }
      }
   }

   private static void pruneScalar(byte[] var0, int var1, byte[] var2) {
      System.arraycopy(var0, var1, var2, 0, 32);
      var2[0] = (byte)(var2[0] & 248);
      var2[31] = (byte)(var2[31] & 127);
      var2[31] = (byte)(var2[31] | 64);
   }

   private static void scalarMult(byte[] var0, Ed25519.PointAffine var1, Ed25519.PointAccum var2) {
      int[] var3 = new int[8];
      Scalar25519.decode(var0, var3);
      Scalar25519.toSignedDigits(256, var3);
      Ed25519.PointPrecompZ var4 = new Ed25519.PointPrecompZ();
      Ed25519.PointTemp var5 = new Ed25519.PointTemp();
      int[] var6 = pointPrecomputeZ(var1, 8, var5);
      pointSetNeutral(var2);
      int var7 = 63;

      while (true) {
         pointLookupZ(var3, var7, var6, var4);
         pointAdd(var4, var2, var5);
         if (--var7 < 0) {
            return;
         }

         for (int var8 = 0; var8 < 4; var8++) {
            pointDouble(var2);
         }
      }
   }

   private static void scalarMultBase(byte[] var0, Ed25519.PointAccum var1) {
      precompute();
      int[] var2 = new int[8];
      Scalar25519.decode(var0, var2);
      Scalar25519.toSignedDigits(256, var2);
      groupCombBits(var2);
      Ed25519.PointPrecomp var3 = new Ed25519.PointPrecomp();
      Ed25519.PointTemp var4 = new Ed25519.PointTemp();
      pointSetNeutral(var1);
      int var5 = 0;
      byte var6 = 28;

      while (true) {
         for (int var7 = 0; var7 < 8; var7++) {
            int var8 = var2[var7] >>> var6;
            int var9 = var8 >>> 3 & 1;
            int var10 = (var8 ^ -var9) & 7;
            pointLookup(var7, var10, var3);
            Ed25519.F.cnegate(var5 ^ var9, var1.x);
            Ed25519.F.cnegate(var5 ^ var9, var1.u);
            var5 = var9;
            pointAdd(var3, var1, var4);
         }

         var6 -= 4;
         if (var6 < 0) {
            Ed25519.F.cnegate(var5, var1.x);
            Ed25519.F.cnegate(var5, var1.u);
            return;
         }

         pointDouble(var1);
      }
   }

   private static void scalarMultBaseEncoded(byte[] var0, byte[] var1, int var2) {
      Ed25519.PointAccum var3 = new Ed25519.PointAccum();
      scalarMultBase(var0, var3);
      if (0 == encodeResult(var3, var1, var2)) {
         throw new IllegalStateException();
      }
   }

   public static void scalarMultBaseYZ(X25519.Friend var0, byte[] var1, int var2, int[] var3, int[] var4) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by X25519");
      } else {
         byte[] var5 = new byte[32];
         pruneScalar(var1, var2, var5);
         Ed25519.PointAccum var6 = new Ed25519.PointAccum();
         scalarMultBase(var5, var6);
         if (0 == checkPoint(var6)) {
            throw new IllegalStateException();
         } else {
            Ed25519.F.copy(var6.y, 0, var3, 0);
            Ed25519.F.copy(var6.z, 0, var4, 0);
         }
      }
   }

   private static void scalarMultOrderVar(Ed25519.PointAffine var0, Ed25519.PointAccum var1) {
      byte[] var2 = new byte[253];
      Scalar25519.getOrderWnafVar(4, var2);
      byte var3 = 4;
      Ed25519.PointPrecompZ[] var4 = new Ed25519.PointPrecompZ[var3];
      Ed25519.PointTemp var5 = new Ed25519.PointTemp();
      pointPrecomputeZ(var0, var4, var3, var5);
      pointSetNeutral(var1);
      int var6 = 252;

      while (true) {
         byte var7 = var2[var6];
         if (var7 != 0) {
            int var8 = var7 >> 1 ^ var7 >> 31;
            pointAddVar(var7 < 0, var4[var8], var1, var5);
         }

         if (--var6 < 0) {
            return;
         }

         pointDouble(var1);
      }
   }

   private static void scalarMultStraus128Var(int[] var0, int[] var1, Ed25519.PointAffine var2, int[] var3, Ed25519.PointAffine var4, Ed25519.PointAccum var5) {
      precompute();
      byte[] var6 = new byte[256];
      byte[] var7 = new byte[128];
      byte[] var8 = new byte[128];
      Wnaf.getSignedVar(var0, 6, var6);
      Wnaf.getSignedVar(var1, 4, var7);
      Wnaf.getSignedVar(var3, 4, var8);
      byte var9 = 4;
      Ed25519.PointPrecompZ[] var10 = new Ed25519.PointPrecompZ[var9];
      Ed25519.PointPrecompZ[] var11 = new Ed25519.PointPrecompZ[var9];
      Ed25519.PointTemp var12 = new Ed25519.PointTemp();
      pointPrecomputeZ(var2, var10, var9, var12);
      pointPrecomputeZ(var4, var11, var9, var12);
      pointSetNeutral(var5);
      int var13 = 128;

      do {
         var13--;
      } while (var13 >= 0 && (var6[var13] | var6[128 + var13] | var7[var13] | var8[var13]) == 0);

      while (var13 >= 0) {
         byte var14 = var6[var13];
         if (var14 != 0) {
            int var15 = var14 >> 1 ^ var14 >> 31;
            pointAddVar(var14 < 0, PRECOMP_BASE_WNAF[var15], var5, var12);
         }

         byte var19 = var6[128 + var13];
         if (var19 != 0) {
            int var16 = var19 >> 1 ^ var19 >> 31;
            pointAddVar(var19 < 0, PRECOMP_BASE128_WNAF[var16], var5, var12);
         }

         byte var20 = var7[var13];
         if (var20 != 0) {
            int var17 = var20 >> 1 ^ var20 >> 31;
            pointAddVar(var20 < 0, var10[var17], var5, var12);
         }

         byte var21 = var8[var13];
         if (var21 != 0) {
            int var18 = var21 >> 1 ^ var21 >> 31;
            pointAddVar(var21 < 0, var11[var18], var5, var12);
         }

         pointDouble(var5);
         var13--;
      }

      pointDouble(var5);
      pointDouble(var5);
   }

   public static void sign(byte[] var0, int var1, byte[] var2, int var3, int var4, byte[] var5, int var6) {
      Object var7 = null;
      byte var8 = 0;
      implSign(var0, var1, (byte[])var7, var8, var2, var3, var4, var5, var6);
   }

   public static void sign(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5, int var6, byte[] var7, int var8) {
      Object var9 = null;
      byte var10 = 0;
      implSign(var0, var1, var2, var3, (byte[])var9, var10, var4, var5, var6, var7, var8);
   }

   public static void sign(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, int var7) {
      byte var8 = 0;
      implSign(var0, var1, var2, var8, var3, var4, var5, var6, var7);
   }

   public static void sign(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6, int var7, byte[] var8, int var9) {
      byte var10 = 0;
      implSign(var0, var1, var2, var3, var4, var10, var5, var6, var7, var8, var9);
   }

   public static void signPrehash(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, byte[] var5, int var6) {
      byte var7 = 1;
      implSign(var0, var1, var2, var7, var3, var4, 64, var5, var6);
   }

   public static void signPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6, byte[] var7, int var8) {
      byte var9 = 1;
      implSign(var0, var1, var2, var3, var4, var9, var5, var6, 64, var7, var8);
   }

   public static void signPrehash(byte[] var0, int var1, byte[] var2, Digest var3, byte[] var4, int var5) {
      byte[] var6 = new byte[64];
      if (64 != var3.doFinal(var6, 0)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var7 = 1;
         implSign(var0, var1, var2, var7, var6, 0, var6.length, var4, var5);
      }
   }

   public static void signPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, Digest var5, byte[] var6, int var7) {
      byte[] var8 = new byte[64];
      if (64 != var5.doFinal(var8, 0)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var9 = 1;
         implSign(var0, var1, var2, var3, var4, var9, var8, 0, var8.length, var6, var7);
      }
   }

   public static boolean validatePublicKeyFull(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 32);
      if (!checkPointFullVar(var2)) {
         return false;
      } else {
         Ed25519.PointAffine var3 = new Ed25519.PointAffine();
         return !decodePointVar(var2, false, var3) ? false : checkPointOrderVar(var3);
      }
   }

   public static Ed25519.PublicPoint validatePublicKeyFullExport(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 32);
      if (!checkPointFullVar(var2)) {
         return null;
      } else {
         Ed25519.PointAffine var3 = new Ed25519.PointAffine();
         if (!decodePointVar(var2, false, var3)) {
            return null;
         } else {
            return !checkPointOrderVar(var3) ? null : exportPoint(var3);
         }
      }
   }

   public static boolean validatePublicKeyPartial(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 32);
      if (!checkPointFullVar(var2)) {
         return false;
      } else {
         Ed25519.PointAffine var3 = new Ed25519.PointAffine();
         return decodePointVar(var2, false, var3);
      }
   }

   public static Ed25519.PublicPoint validatePublicKeyPartialExport(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 32);
      if (!checkPointFullVar(var2)) {
         return null;
      } else {
         Ed25519.PointAffine var3 = new Ed25519.PointAffine();
         return !decodePointVar(var2, false, var3) ? null : exportPoint(var3);
      }
   }

   public static boolean verify(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5, int var6) {
      Object var7 = null;
      byte var8 = 0;
      return implVerify(var0, var1, var2, var3, (byte[])var7, var8, var4, var5, var6);
   }

   public static boolean verify(byte[] var0, int var1, Ed25519.PublicPoint var2, byte[] var3, int var4, int var5) {
      Object var6 = null;
      byte var7 = 0;
      return implVerify(var0, var1, var2, (byte[])var6, var7, var3, var4, var5);
   }

   public static boolean verify(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6, int var7) {
      byte var8 = 0;
      return implVerify(var0, var1, var2, var3, var4, var8, var5, var6, var7);
   }

   public static boolean verify(byte[] var0, int var1, Ed25519.PublicPoint var2, byte[] var3, byte[] var4, int var5, int var6) {
      byte var7 = 0;
      return implVerify(var0, var1, var2, var3, var7, var4, var5, var6);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6) {
      byte var7 = 1;
      return implVerify(var0, var1, var2, var3, var4, var7, var5, var6, 64);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, Ed25519.PublicPoint var2, byte[] var3, byte[] var4, int var5) {
      byte var6 = 1;
      return implVerify(var0, var1, var2, var3, var6, var4, var5, 64);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, Digest var5) {
      byte[] var6 = new byte[64];
      if (64 != var5.doFinal(var6, 0)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var7 = 1;
         return implVerify(var0, var1, var2, var3, var4, var7, var6, 0, var6.length);
      }
   }

   public static boolean verifyPrehash(byte[] var0, int var1, Ed25519.PublicPoint var2, byte[] var3, Digest var4) {
      byte[] var5 = new byte[64];
      if (64 != var4.doFinal(var5, 0)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var6 = 1;
         return implVerify(var0, var1, var2, var3, var6, var5, 0, var5.length);
      }
   }

   public static final class Algorithm {
      public static final int Ed25519 = 0;
      public static final int Ed25519ctx = 1;
      public static final int Ed25519ph = 2;
   }

   private static class F extends X25519Field {
   }

   private static class PointAccum {
      int[] x = Ed25519.F.create();
      int[] y = Ed25519.F.create();
      int[] z = Ed25519.F.create();
      int[] u = Ed25519.F.create();
      int[] v = Ed25519.F.create();

      private PointAccum() {
      }
   }

   private static class PointAffine {
      int[] x = Ed25519.F.create();
      int[] y = Ed25519.F.create();

      private PointAffine() {
      }
   }

   private static class PointExtended {
      int[] x = Ed25519.F.create();
      int[] y = Ed25519.F.create();
      int[] z = Ed25519.F.create();
      int[] t = Ed25519.F.create();

      private PointExtended() {
      }
   }

   private static class PointPrecomp {
      int[] ymx_h = Ed25519.F.create();
      int[] ypx_h = Ed25519.F.create();
      int[] xyd = Ed25519.F.create();

      private PointPrecomp() {
      }
   }

   private static class PointPrecompZ {
      int[] ymx_h = Ed25519.F.create();
      int[] ypx_h = Ed25519.F.create();
      int[] xyd = Ed25519.F.create();
      int[] z = Ed25519.F.create();

      private PointPrecompZ() {
      }
   }

   private static class PointTemp {
      int[] r0 = Ed25519.F.create();
      int[] r1 = Ed25519.F.create();

      private PointTemp() {
      }
   }

   public static final class PublicPoint {
      final int[] data;

      PublicPoint(int[] var1) {
         this.data = var1;
      }
   }
}
