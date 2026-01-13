package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.math.ec.rfc7748.X448Field;
import org.bouncycastle.math.raw.Nat;

public abstract class Ed448 {
   private static final int COORD_INTS = 14;
   private static final int POINT_BYTES = 57;
   private static final int SCALAR_INTS = 14;
   private static final int SCALAR_BYTES = 57;
   public static final int PREHASH_SIZE = 64;
   public static final int PUBLIC_KEY_SIZE = 57;
   public static final int SECRET_KEY_SIZE = 57;
   public static final int SIGNATURE_SIZE = 114;
   private static final byte[] DOM4_PREFIX = new byte[]{83, 105, 103, 69, 100, 52, 52, 56};
   private static final int[] P = new int[]{-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};
   private static final int[] B_x = new int[]{
      118276190,
      40534716,
      9670182,
      135141552,
      85017403,
      259173222,
      68333082,
      171784774,
      174973732,
      15824510,
      73756743,
      57518561,
      94773951,
      248652241,
      107736333,
      82941708
   };
   private static final int[] B_y = new int[]{
      36764180,
      8885695,
      130592152,
      20104429,
      163904957,
      30304195,
      121295871,
      5901357,
      125344798,
      171541512,
      175338348,
      209069246,
      3626697,
      38307682,
      24032956,
      110359655
   };
   private static final int[] B225_x = new int[]{
      110141154,
      30892124,
      160820362,
      264558960,
      217232225,
      47722141,
      19029845,
      8326902,
      183409749,
      170134547,
      90340180,
      222600478,
      61097333,
      7431335,
      198491505,
      102372861
   };
   private static final int[] B225_y = new int[]{
      221945828,
      50763449,
      132637478,
      109250759,
      216053960,
      61612587,
      50649998,
      138339097,
      98949899,
      248139835,
      186410297,
      126520782,
      47339196,
      78164062,
      198835543,
      169622712
   };
   private static final int C_d = 39081;
   private static final int WNAF_WIDTH_225 = 5;
   private static final int WNAF_WIDTH_BASE = 7;
   private static final int PRECOMP_BLOCKS = 5;
   private static final int PRECOMP_TEETH = 5;
   private static final int PRECOMP_SPACING = 18;
   private static final int PRECOMP_RANGE = 450;
   private static final int PRECOMP_POINTS = 16;
   private static final int PRECOMP_MASK = 15;
   private static final Object PRECOMP_LOCK = new Object();
   private static Ed448.PointAffine[] PRECOMP_BASE_WNAF = null;
   private static Ed448.PointAffine[] PRECOMP_BASE225_WNAF = null;
   private static int[] PRECOMP_BASE_COMB = null;

   private static byte[] calculateS(byte[] var0, byte[] var1, byte[] var2) {
      int[] var3 = new int[28];
      Scalar448.decode(var0, var3);
      int[] var4 = new int[14];
      Scalar448.decode(var1, var4);
      int[] var5 = new int[14];
      Scalar448.decode(var2, var5);
      Nat.mulAddTo(14, var4, var5, var3);
      byte[] var6 = new byte[114];
      Codec.encode32(var3, 0, var3.length, var6, 0);
      return Scalar448.reduce912(var6);
   }

   private static boolean checkContextVar(byte[] var0) {
      return var0 != null && var0.length < 256;
   }

   private static int checkPoint(Ed448.PointAffine var0) {
      int[] var1 = Ed448.F.create();
      int[] var2 = Ed448.F.create();
      int[] var3 = Ed448.F.create();
      Ed448.F.sqr(var0.x, var2);
      Ed448.F.sqr(var0.y, var3);
      Ed448.F.mul(var2, var3, var1);
      Ed448.F.add(var2, var3, var2);
      Ed448.F.mul(var1, 39081, var1);
      Ed448.F.subOne(var1);
      Ed448.F.add(var1, var2, var1);
      Ed448.F.normalize(var1);
      Ed448.F.normalize(var3);
      return Ed448.F.isZero(var1) & ~Ed448.F.isZero(var3);
   }

   private static int checkPoint(Ed448.PointProjective var0) {
      int[] var1 = Ed448.F.create();
      int[] var2 = Ed448.F.create();
      int[] var3 = Ed448.F.create();
      int[] var4 = Ed448.F.create();
      Ed448.F.sqr(var0.x, var2);
      Ed448.F.sqr(var0.y, var3);
      Ed448.F.sqr(var0.z, var4);
      Ed448.F.mul(var2, var3, var1);
      Ed448.F.add(var2, var3, var2);
      Ed448.F.mul(var2, var4, var2);
      Ed448.F.sqr(var4, var4);
      Ed448.F.mul(var1, 39081, var1);
      Ed448.F.sub(var1, var4, var1);
      Ed448.F.add(var1, var2, var1);
      Ed448.F.normalize(var1);
      Ed448.F.normalize(var3);
      Ed448.F.normalize(var4);
      return Ed448.F.isZero(var1) & ~Ed448.F.isZero(var3) & ~Ed448.F.isZero(var4);
   }

   private static boolean checkPointFullVar(byte[] var0) {
      if ((var0[56] & 127) != 0) {
         return false;
      } else {
         int var1 = Codec.decode32(var0, 52);
         int var2 = var1;
         int var3 = var1 ^ P[13];

         for (int var4 = 12; var4 > 0; var4--) {
            int var5 = Codec.decode32(var0, var4 * 4);
            if (var3 == 0 && var5 + Integer.MIN_VALUE > P[var4] + Integer.MIN_VALUE) {
               return false;
            }

            var2 |= var5;
            var3 |= var5 ^ P[var4];
         }

         int var6 = Codec.decode32(var0, 0);
         return var2 == 0 && var6 + Integer.MIN_VALUE <= -2147483647 ? false : var3 != 0 || var6 + Integer.MIN_VALUE < P[0] - 1 + Integer.MIN_VALUE;
      }
   }

   private static boolean checkPointOrderVar(Ed448.PointAffine var0) {
      Ed448.PointProjective var1 = new Ed448.PointProjective();
      scalarMultOrderVar(var0, var1);
      return normalizeToNeutralElementVar(var1);
   }

   private static boolean checkPointVar(byte[] var0) {
      if ((var0[56] & 127) != 0) {
         return false;
      } else if (Codec.decode32(var0, 52) != P[13]) {
         return true;
      } else {
         int[] var1 = new int[14];
         Codec.decode32(var0, 0, var1, 0, 14);
         return !Nat.gte(14, var1, P);
      }
   }

   private static byte[] copy(byte[] var0, int var1, int var2) {
      byte[] var3 = new byte[var2];
      System.arraycopy(var0, var1, var3, 0, var2);
      return var3;
   }

   public static Xof createPrehash() {
      return createXof();
   }

   private static Xof createXof() {
      return new SHAKEDigest(256);
   }

   private static boolean decodePointVar(byte[] var0, boolean var1, Ed448.PointAffine var2) {
      int var3 = (var0[56] & 128) >>> 7;
      Ed448.F.decode(var0, var2.y);
      int[] var4 = Ed448.F.create();
      int[] var5 = Ed448.F.create();
      Ed448.F.sqr(var2.y, var4);
      Ed448.F.mul(var4, 39081, var5);
      Ed448.F.negate(var4, var4);
      Ed448.F.addOne(var4);
      Ed448.F.addOne(var5);
      if (!Ed448.F.sqrtRatioVar(var4, var5, var2.x)) {
         return false;
      } else {
         Ed448.F.normalize(var2.x);
         if (var3 == 1 && Ed448.F.isZeroVar(var2.x)) {
            return false;
         } else {
            if (var1 ^ var3 != (var2.x[0] & 1)) {
               Ed448.F.negate(var2.x, var2.x);
               Ed448.F.normalize(var2.x);
            }

            return true;
         }
      }
   }

   private static void dom4(Xof var0, byte var1, byte[] var2) {
      int var3 = DOM4_PREFIX.length;
      byte[] var4 = new byte[var3 + 2 + var2.length];
      System.arraycopy(DOM4_PREFIX, 0, var4, 0, var3);
      var4[var3] = var1;
      var4[var3 + 1] = (byte)var2.length;
      System.arraycopy(var2, 0, var4, var3 + 2, var2.length);
      var0.update(var4, 0, var4.length);
   }

   private static void encodePoint(Ed448.PointAffine var0, byte[] var1, int var2) {
      Ed448.F.encode(var0.y, var1, var2);
      var1[var2 + 57 - 1] = (byte)((var0.x[0] & 1) << 7);
   }

   public static void encodePublicPoint(Ed448.PublicPoint var0, byte[] var1, int var2) {
      Ed448.F.encode(var0.data, 16, var1, var2);
      var1[var2 + 57 - 1] = (byte)((var0.data[0] & 1) << 7);
   }

   private static int encodeResult(Ed448.PointProjective var0, byte[] var1, int var2) {
      Ed448.PointAffine var3 = new Ed448.PointAffine();
      normalizeToAffine(var0, var3);
      int var4 = checkPoint(var3);
      encodePoint(var3, var1, var2);
      return var4;
   }

   private static Ed448.PublicPoint exportPoint(Ed448.PointAffine var0) {
      int[] var1 = new int[32];
      Ed448.F.copy(var0.x, 0, var1, 0);
      Ed448.F.copy(var0.y, 0, var1, 16);
      return new Ed448.PublicPoint(var1);
   }

   public static void generatePrivateKey(SecureRandom var0, byte[] var1) {
      if (var1.length != 57) {
         throw new IllegalArgumentException("k");
      } else {
         var0.nextBytes(var1);
      }
   }

   public static void generatePublicKey(byte[] var0, int var1, byte[] var2, int var3) {
      Xof var4 = createXof();
      byte[] var5 = new byte[114];
      var4.update(var0, var1, 57);
      var4.doFinal(var5, 0, var5.length);
      byte[] var6 = new byte[57];
      pruneScalar(var5, 0, var6);
      scalarMultBaseEncoded(var6, var2, var3);
   }

   public static Ed448.PublicPoint generatePublicKey(byte[] var0, int var1) {
      Xof var2 = createXof();
      byte[] var3 = new byte[114];
      var2.update(var0, var1, 57);
      var2.doFinal(var3, 0, var3.length);
      byte[] var4 = new byte[57];
      pruneScalar(var3, 0, var4);
      Ed448.PointProjective var5 = new Ed448.PointProjective();
      scalarMultBase(var4, var5);
      Ed448.PointAffine var6 = new Ed448.PointAffine();
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

   private static void implSign(
      Xof var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, byte var6, byte[] var7, int var8, int var9, byte[] var10, int var11
   ) {
      dom4(var0, var6, var5);
      var0.update(var1, 57, 57);
      var0.update(var7, var8, var9);
      var0.doFinal(var1, 0, var1.length);
      byte[] var12 = Scalar448.reduce912(var1);
      byte[] var13 = new byte[57];
      scalarMultBaseEncoded(var12, var13, 0);
      dom4(var0, var6, var5);
      var0.update(var13, 0, 57);
      var0.update(var3, var4, 57);
      var0.update(var7, var8, var9);
      var0.doFinal(var1, 0, var1.length);
      byte[] var14 = Scalar448.reduce912(var1);
      byte[] var15 = calculateS(var12, var14, var2);
      System.arraycopy(var13, 0, var10, var11, 57);
      System.arraycopy(var15, 0, var10, var11 + 57, 57);
   }

   private static void implSign(byte[] var0, int var1, byte[] var2, byte var3, byte[] var4, int var5, int var6, byte[] var7, int var8) {
      if (!checkContextVar(var2)) {
         throw new IllegalArgumentException("ctx");
      } else {
         Xof var9 = createXof();
         byte[] var10 = new byte[114];
         var9.update(var0, var1, 57);
         var9.doFinal(var10, 0, var10.length);
         byte[] var11 = new byte[57];
         pruneScalar(var10, 0, var11);
         byte[] var12 = new byte[57];
         scalarMultBaseEncoded(var11, var12, 0);
         implSign(var9, var10, var11, var12, 0, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   private static void implSign(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte var5, byte[] var6, int var7, int var8, byte[] var9, int var10) {
      if (!checkContextVar(var4)) {
         throw new IllegalArgumentException("ctx");
      } else {
         Xof var11 = createXof();
         byte[] var12 = new byte[114];
         var11.update(var0, var1, 57);
         var11.doFinal(var12, 0, var12.length);
         byte[] var13 = new byte[57];
         pruneScalar(var12, 0, var13);
         implSign(var11, var12, var13, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   private static boolean implVerify(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte var5, byte[] var6, int var7, int var8) {
      if (!checkContextVar(var4)) {
         throw new IllegalArgumentException("ctx");
      } else {
         byte[] var9 = copy(var0, var1, 57);
         byte[] var10 = copy(var0, var1 + 57, 57);
         byte[] var11 = copy(var2, var3, 57);
         if (!checkPointVar(var9)) {
            return false;
         } else {
            int[] var12 = new int[14];
            if (!Scalar448.checkVar(var10, var12)) {
               return false;
            } else if (!checkPointFullVar(var11)) {
               return false;
            } else {
               Ed448.PointAffine var13 = new Ed448.PointAffine();
               if (!decodePointVar(var9, true, var13)) {
                  return false;
               } else {
                  Ed448.PointAffine var14 = new Ed448.PointAffine();
                  if (!decodePointVar(var11, true, var14)) {
                     return false;
                  } else {
                     Xof var15 = createXof();
                     byte[] var16 = new byte[114];
                     dom4(var15, var5, var4);
                     var15.update(var9, 0, 57);
                     var15.update(var11, 0, 57);
                     var15.update(var6, var7, var8);
                     var15.doFinal(var16, 0, var16.length);
                     byte[] var17 = Scalar448.reduce912(var16);
                     int[] var18 = new int[14];
                     Scalar448.decode(var17, var18);
                     int[] var19 = new int[8];
                     int[] var20 = new int[8];
                     if (!Scalar448.reduceBasisVar(var18, var19, var20)) {
                        throw new IllegalStateException();
                     } else {
                        Scalar448.multiply225Var(var12, var20, var12);
                        Ed448.PointProjective var21 = new Ed448.PointProjective();
                        scalarMultStraus225Var(var12, var19, var14, var20, var13, var21);
                        return normalizeToNeutralElementVar(var21);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean implVerify(byte[] var0, int var1, Ed448.PublicPoint var2, byte[] var3, byte var4, byte[] var5, int var6, int var7) {
      if (!checkContextVar(var3)) {
         throw new IllegalArgumentException("ctx");
      } else {
         byte[] var8 = copy(var0, var1, 57);
         byte[] var9 = copy(var0, var1 + 57, 57);
         if (!checkPointVar(var8)) {
            return false;
         } else {
            int[] var10 = new int[14];
            if (!Scalar448.checkVar(var9, var10)) {
               return false;
            } else {
               Ed448.PointAffine var11 = new Ed448.PointAffine();
               if (!decodePointVar(var8, true, var11)) {
                  return false;
               } else {
                  Ed448.PointAffine var12 = new Ed448.PointAffine();
                  Ed448.F.negate(var2.data, var12.x);
                  Ed448.F.copy(var2.data, 16, var12.y, 0);
                  byte[] var13 = new byte[57];
                  encodePublicPoint(var2, var13, 0);
                  Xof var14 = createXof();
                  byte[] var15 = new byte[114];
                  dom4(var14, var4, var3);
                  var14.update(var8, 0, 57);
                  var14.update(var13, 0, 57);
                  var14.update(var5, var6, var7);
                  var14.doFinal(var15, 0, var15.length);
                  byte[] var16 = Scalar448.reduce912(var15);
                  int[] var17 = new int[14];
                  Scalar448.decode(var16, var17);
                  int[] var18 = new int[8];
                  int[] var19 = new int[8];
                  if (!Scalar448.reduceBasisVar(var17, var18, var19)) {
                     throw new IllegalStateException();
                  } else {
                     Scalar448.multiply225Var(var10, var19, var10);
                     Ed448.PointProjective var20 = new Ed448.PointProjective();
                     scalarMultStraus225Var(var10, var18, var12, var19, var11, var20);
                     return normalizeToNeutralElementVar(var20);
                  }
               }
            }
         }
      }
   }

   private static void invertZs(Ed448.PointProjective[] var0) {
      int var1 = var0.length;
      int[] var2 = Ed448.F.createTable(var1);
      int[] var3 = Ed448.F.create();
      Ed448.F.copy(var0[0].z, 0, var3, 0);
      Ed448.F.copy(var3, 0, var2, 0);
      int var4 = 0;

      while (++var4 < var1) {
         Ed448.F.mul(var3, var0[var4].z, var3);
         Ed448.F.copy(var3, 0, var2, var4 * 16);
      }

      Ed448.F.invVar(var3, var3);
      var4--;
      int[] var5 = Ed448.F.create();

      while (var4 > 0) {
         int var6 = var4--;
         Ed448.F.copy(var2, var4 * 16, var5, 0);
         Ed448.F.mul(var5, var3, var5);
         Ed448.F.mul(var3, var0[var6].z, var3);
         Ed448.F.copy(var5, 0, var0[var6].z, 0);
      }

      Ed448.F.copy(var3, 0, var0[0].z, 0);
   }

   private static void normalizeToAffine(Ed448.PointProjective var0, Ed448.PointAffine var1) {
      Ed448.F.inv(var0.z, var1.y);
      Ed448.F.mul(var1.y, var0.x, var1.x);
      Ed448.F.mul(var1.y, var0.y, var1.y);
      Ed448.F.normalize(var1.x);
      Ed448.F.normalize(var1.y);
   }

   private static boolean normalizeToNeutralElementVar(Ed448.PointProjective var0) {
      Ed448.F.normalize(var0.x);
      Ed448.F.normalize(var0.y);
      Ed448.F.normalize(var0.z);
      return Ed448.F.isZeroVar(var0.x) && !Ed448.F.isZeroVar(var0.y) && Ed448.F.areEqualVar(var0.y, var0.z);
   }

   private static void pointAdd(Ed448.PointAffine var0, Ed448.PointProjective var1, Ed448.PointTemp var2) {
      int[] var3 = var2.r1;
      int[] var4 = var2.r2;
      int[] var5 = var2.r3;
      int[] var6 = var2.r4;
      int[] var7 = var2.r5;
      int[] var8 = var2.r6;
      int[] var9 = var2.r7;
      Ed448.F.sqr(var1.z, var3);
      Ed448.F.mul(var0.x, var1.x, var4);
      Ed448.F.mul(var0.y, var1.y, var5);
      Ed448.F.mul(var4, var5, var6);
      Ed448.F.mul(var6, 39081, var6);
      Ed448.F.add(var3, var6, var7);
      Ed448.F.sub(var3, var6, var8);
      Ed448.F.add(var0.y, var0.x, var9);
      Ed448.F.add(var1.y, var1.x, var6);
      Ed448.F.mul(var9, var6, var9);
      Ed448.F.add(var5, var4, var3);
      Ed448.F.sub(var5, var4, var6);
      Ed448.F.carry(var3);
      Ed448.F.sub(var9, var3, var9);
      Ed448.F.mul(var9, var1.z, var9);
      Ed448.F.mul(var6, var1.z, var6);
      Ed448.F.mul(var7, var9, var1.x);
      Ed448.F.mul(var6, var8, var1.y);
      Ed448.F.mul(var7, var8, var1.z);
   }

   private static void pointAdd(Ed448.PointProjective var0, Ed448.PointProjective var1, Ed448.PointTemp var2) {
      int[] var3 = var2.r0;
      int[] var4 = var2.r1;
      int[] var5 = var2.r2;
      int[] var6 = var2.r3;
      int[] var7 = var2.r4;
      int[] var8 = var2.r5;
      int[] var9 = var2.r6;
      int[] var10 = var2.r7;
      Ed448.F.mul(var0.z, var1.z, var3);
      Ed448.F.sqr(var3, var4);
      Ed448.F.mul(var0.x, var1.x, var5);
      Ed448.F.mul(var0.y, var1.y, var6);
      Ed448.F.mul(var5, var6, var7);
      Ed448.F.mul(var7, 39081, var7);
      Ed448.F.add(var4, var7, var8);
      Ed448.F.sub(var4, var7, var9);
      Ed448.F.add(var0.y, var0.x, var10);
      Ed448.F.add(var1.y, var1.x, var7);
      Ed448.F.mul(var10, var7, var10);
      Ed448.F.add(var6, var5, var4);
      Ed448.F.sub(var6, var5, var7);
      Ed448.F.carry(var4);
      Ed448.F.sub(var10, var4, var10);
      Ed448.F.mul(var10, var3, var10);
      Ed448.F.mul(var7, var3, var7);
      Ed448.F.mul(var8, var10, var1.x);
      Ed448.F.mul(var7, var9, var1.y);
      Ed448.F.mul(var8, var9, var1.z);
   }

   private static void pointAddVar(boolean var0, Ed448.PointAffine var1, Ed448.PointProjective var2, Ed448.PointTemp var3) {
      int[] var4 = var3.r1;
      int[] var5 = var3.r2;
      int[] var6 = var3.r3;
      int[] var7 = var3.r4;
      int[] var8 = var3.r5;
      int[] var9 = var3.r6;
      int[] var10 = var3.r7;
      int[] var11;
      int[] var12;
      int[] var13;
      int[] var14;
      if (var0) {
         var11 = var7;
         var12 = var4;
         var13 = var9;
         var14 = var8;
         Ed448.F.sub(var1.y, var1.x, var10);
      } else {
         var11 = var4;
         var12 = var7;
         var13 = var8;
         var14 = var9;
         Ed448.F.add(var1.y, var1.x, var10);
      }

      Ed448.F.sqr(var2.z, var4);
      Ed448.F.mul(var1.x, var2.x, var5);
      Ed448.F.mul(var1.y, var2.y, var6);
      Ed448.F.mul(var5, var6, var7);
      Ed448.F.mul(var7, 39081, var7);
      Ed448.F.add(var4, var7, var13);
      Ed448.F.sub(var4, var7, var14);
      Ed448.F.add(var2.y, var2.x, var7);
      Ed448.F.mul(var10, var7, var10);
      Ed448.F.add(var6, var5, var11);
      Ed448.F.sub(var6, var5, var12);
      Ed448.F.carry(var11);
      Ed448.F.sub(var10, var4, var10);
      Ed448.F.mul(var10, var2.z, var10);
      Ed448.F.mul(var7, var2.z, var7);
      Ed448.F.mul(var8, var10, var2.x);
      Ed448.F.mul(var7, var9, var2.y);
      Ed448.F.mul(var8, var9, var2.z);
   }

   private static void pointAddVar(boolean var0, Ed448.PointProjective var1, Ed448.PointProjective var2, Ed448.PointTemp var3) {
      int[] var4 = var3.r0;
      int[] var5 = var3.r1;
      int[] var6 = var3.r2;
      int[] var7 = var3.r3;
      int[] var8 = var3.r4;
      int[] var9 = var3.r5;
      int[] var10 = var3.r6;
      int[] var11 = var3.r7;
      int[] var12;
      int[] var13;
      int[] var14;
      int[] var15;
      if (var0) {
         var12 = var8;
         var13 = var5;
         var14 = var10;
         var15 = var9;
         Ed448.F.sub(var1.y, var1.x, var11);
      } else {
         var12 = var5;
         var13 = var8;
         var14 = var9;
         var15 = var10;
         Ed448.F.add(var1.y, var1.x, var11);
      }

      Ed448.F.mul(var1.z, var2.z, var4);
      Ed448.F.sqr(var4, var5);
      Ed448.F.mul(var1.x, var2.x, var6);
      Ed448.F.mul(var1.y, var2.y, var7);
      Ed448.F.mul(var6, var7, var8);
      Ed448.F.mul(var8, 39081, var8);
      Ed448.F.add(var5, var8, var14);
      Ed448.F.sub(var5, var8, var15);
      Ed448.F.add(var2.y, var2.x, var8);
      Ed448.F.mul(var11, var8, var11);
      Ed448.F.add(var7, var6, var12);
      Ed448.F.sub(var7, var6, var13);
      Ed448.F.carry(var12);
      Ed448.F.sub(var11, var5, var11);
      Ed448.F.mul(var11, var4, var11);
      Ed448.F.mul(var8, var4, var8);
      Ed448.F.mul(var9, var11, var2.x);
      Ed448.F.mul(var8, var10, var2.y);
      Ed448.F.mul(var9, var10, var2.z);
   }

   private static void pointCopy(Ed448.PointAffine var0, Ed448.PointProjective var1) {
      Ed448.F.copy(var0.x, 0, var1.x, 0);
      Ed448.F.copy(var0.y, 0, var1.y, 0);
      Ed448.F.one(var1.z);
   }

   private static void pointCopy(Ed448.PointProjective var0, Ed448.PointProjective var1) {
      Ed448.F.copy(var0.x, 0, var1.x, 0);
      Ed448.F.copy(var0.y, 0, var1.y, 0);
      Ed448.F.copy(var0.z, 0, var1.z, 0);
   }

   private static void pointDouble(Ed448.PointProjective var0, Ed448.PointTemp var1) {
      int[] var2 = var1.r1;
      int[] var3 = var1.r2;
      int[] var4 = var1.r3;
      int[] var5 = var1.r4;
      int[] var6 = var1.r7;
      int[] var7 = var1.r0;
      Ed448.F.add(var0.x, var0.y, var2);
      Ed448.F.sqr(var2, var2);
      Ed448.F.sqr(var0.x, var3);
      Ed448.F.sqr(var0.y, var4);
      Ed448.F.add(var3, var4, var5);
      Ed448.F.carry(var5);
      Ed448.F.sqr(var0.z, var6);
      Ed448.F.add(var6, var6, var6);
      Ed448.F.carry(var6);
      Ed448.F.sub(var5, var6, var7);
      Ed448.F.sub(var2, var5, var2);
      Ed448.F.sub(var3, var4, var3);
      Ed448.F.mul(var2, var7, var0.x);
      Ed448.F.mul(var5, var3, var0.y);
      Ed448.F.mul(var5, var7, var0.z);
   }

   private static void pointLookup(int var0, int var1, Ed448.PointAffine var2) {
      int var3 = var0 * 16 * 2 * 16;

      for (int var4 = 0; var4 < 16; var4++) {
         int var5 = (var4 ^ var1) - 1 >> 31;
         Ed448.F.cmov(var5, PRECOMP_BASE_COMB, var3, var2.x, 0);
         var3 += 16;
         Ed448.F.cmov(var5, PRECOMP_BASE_COMB, var3, var2.y, 0);
         var3 += 16;
      }
   }

   private static void pointLookup(int[] var0, int var1, int[] var2, Ed448.PointProjective var3) {
      int var4 = getWindow4(var0, var1);
      int var5 = var4 >>> 3 ^ 1;
      int var6 = (var4 ^ -var5) & 7;
      int var7 = 0;

      for (int var8 = 0; var7 < 8; var7++) {
         int var9 = (var7 ^ var6) - 1 >> 31;
         Ed448.F.cmov(var9, var2, var8, var3.x, 0);
         var8 += 16;
         Ed448.F.cmov(var9, var2, var8, var3.y, 0);
         var8 += 16;
         Ed448.F.cmov(var9, var2, var8, var3.z, 0);
         var8 += 16;
      }

      Ed448.F.cnegate(var5, var3.x);
   }

   private static void pointLookup15(int[] var0, Ed448.PointProjective var1) {
      int var2 = 336;
      Ed448.F.copy(var0, var2, var1.x, 0);
      var2 += 16;
      Ed448.F.copy(var0, var2, var1.y, 0);
      var2 += 16;
      Ed448.F.copy(var0, var2, var1.z, 0);
   }

   private static int[] pointPrecompute(Ed448.PointProjective var0, int var1, Ed448.PointTemp var2) {
      Ed448.PointProjective var3 = new Ed448.PointProjective();
      pointCopy(var0, var3);
      Ed448.PointProjective var4 = new Ed448.PointProjective();
      pointCopy(var0, var4);
      pointDouble(var4, var2);
      int[] var5 = Ed448.F.createTable(var1 * 3);
      int var6 = 0;
      int var7 = 0;

      while (true) {
         Ed448.F.copy(var3.x, 0, var5, var6);
         var6 += 16;
         Ed448.F.copy(var3.y, 0, var5, var6);
         var6 += 16;
         Ed448.F.copy(var3.z, 0, var5, var6);
         var6 += 16;
         if (++var7 == var1) {
            return var5;
         }

         pointAdd(var4, var3, var2);
      }
   }

   private static void pointPrecompute(Ed448.PointAffine var0, Ed448.PointProjective[] var1, int var2, int var3, Ed448.PointTemp var4) {
      Ed448.PointProjective var5 = new Ed448.PointProjective();
      pointCopy(var0, var5);
      pointDouble(var5, var4);
      var1[var2] = new Ed448.PointProjective();
      pointCopy(var0, var1[var2]);

      for (int var6 = 1; var6 < var3; var6++) {
         var1[var2 + var6] = new Ed448.PointProjective();
         pointCopy(var1[var2 + var6 - 1], var1[var2 + var6]);
         pointAdd(var5, var1[var2 + var6], var4);
      }
   }

   private static void pointSetNeutral(Ed448.PointProjective var0) {
      Ed448.F.zero(var0.x);
      Ed448.F.one(var0.y);
      Ed448.F.one(var0.z);
   }

   public static void precompute() {
      synchronized (PRECOMP_LOCK) {
         if (PRECOMP_BASE_COMB == null) {
            byte var1 = 32;
            byte var2 = 80;
            int var3 = var1 * 2 + var2;
            Ed448.PointProjective[] var4 = new Ed448.PointProjective[var3];
            Ed448.PointTemp var5 = new Ed448.PointTemp();
            Ed448.PointAffine var6 = new Ed448.PointAffine();
            Ed448.F.copy(B_x, 0, var6.x, 0);
            Ed448.F.copy(B_y, 0, var6.y, 0);
            pointPrecompute(var6, var4, 0, var1, var5);
            Ed448.PointAffine var7 = new Ed448.PointAffine();
            Ed448.F.copy(B225_x, 0, var7.x, 0);
            Ed448.F.copy(B225_y, 0, var7.y, 0);
            pointPrecompute(var7, var4, var1, var1, var5);
            Ed448.PointProjective var8 = new Ed448.PointProjective();
            pointCopy(var6, var8);
            int var9 = var1 * 2;
            Ed448.PointProjective[] var10 = new Ed448.PointProjective[5];

            for (int var11 = 0; var11 < 5; var11++) {
               var10[var11] = new Ed448.PointProjective();
            }

            for (int var18 = 0; var18 < 5; var18++) {
               Ed448.PointProjective var12 = var4[var9++] = new Ed448.PointProjective();

               for (int var13 = 0; var13 < 5; var13++) {
                  if (var13 == 0) {
                     pointCopy(var8, var12);
                  } else {
                     pointAdd(var8, var12, var5);
                  }

                  pointDouble(var8, var5);
                  pointCopy(var8, var10[var13]);
                  if (var18 + var13 != 8) {
                     for (int var14 = 1; var14 < 18; var14++) {
                        pointDouble(var8, var5);
                     }
                  }
               }

               Ed448.F.negate(var12.x, var12.x);

               for (int var26 = 0; var26 < 4; var26++) {
                  int var30 = 1 << var26;

                  for (int var15 = 0; var15 < var30; var9++) {
                     var4[var9] = new Ed448.PointProjective();
                     pointCopy(var4[var9 - var30], var4[var9]);
                     pointAdd(var10[var26], var4[var9], var5);
                     var15++;
                  }
               }
            }

            invertZs(var4);
            PRECOMP_BASE_WNAF = new Ed448.PointAffine[var1];

            for (int var19 = 0; var19 < var1; var19++) {
               Ed448.PointProjective var23 = var4[var19];
               Ed448.PointAffine var27 = PRECOMP_BASE_WNAF[var19] = new Ed448.PointAffine();
               Ed448.F.mul(var23.x, var23.z, var27.x);
               Ed448.F.normalize(var27.x);
               Ed448.F.mul(var23.y, var23.z, var27.y);
               Ed448.F.normalize(var27.y);
            }

            PRECOMP_BASE225_WNAF = new Ed448.PointAffine[var1];

            for (int var20 = 0; var20 < var1; var20++) {
               Ed448.PointProjective var24 = var4[var1 + var20];
               Ed448.PointAffine var28 = PRECOMP_BASE225_WNAF[var20] = new Ed448.PointAffine();
               Ed448.F.mul(var24.x, var24.z, var28.x);
               Ed448.F.normalize(var28.x);
               Ed448.F.mul(var24.y, var24.z, var28.y);
               Ed448.F.normalize(var28.y);
            }

            PRECOMP_BASE_COMB = Ed448.F.createTable(var2 * 2);
            int var21 = 0;

            for (int var25 = var1 * 2; var25 < var3; var25++) {
               Ed448.PointProjective var29 = var4[var25];
               Ed448.F.mul(var29.x, var29.z, var29.x);
               Ed448.F.normalize(var29.x);
               Ed448.F.mul(var29.y, var29.z, var29.y);
               Ed448.F.normalize(var29.y);
               Ed448.F.copy(var29.x, 0, PRECOMP_BASE_COMB, var21);
               var21 += 16;
               Ed448.F.copy(var29.y, 0, PRECOMP_BASE_COMB, var21);
               var21 += 16;
            }
         }
      }
   }

   private static void pruneScalar(byte[] var0, int var1, byte[] var2) {
      System.arraycopy(var0, var1, var2, 0, 56);
      var2[0] = (byte)(var2[0] & 252);
      var2[55] = (byte)(var2[55] | 128);
      var2[56] = 0;
   }

   private static void scalarMult(byte[] var0, Ed448.PointProjective var1, Ed448.PointProjective var2) {
      int[] var3 = new int[15];
      Scalar448.decode(var0, var3);
      Scalar448.toSignedDigits(449, var3, var3);
      Ed448.PointProjective var4 = new Ed448.PointProjective();
      Ed448.PointTemp var5 = new Ed448.PointTemp();
      int[] var6 = pointPrecompute(var1, 8, var5);
      pointLookup15(var6, var2);
      pointAdd(var1, var2, var5);
      int var7 = 111;

      while (true) {
         pointLookup(var3, var7, var6, var4);
         pointAdd(var4, var2, var5);
         if (--var7 < 0) {
            return;
         }

         for (int var8 = 0; var8 < 4; var8++) {
            pointDouble(var2, var5);
         }
      }
   }

   private static void scalarMultBase(byte[] var0, Ed448.PointProjective var1) {
      precompute();
      int[] var2 = new int[15];
      Scalar448.decode(var0, var2);
      Scalar448.toSignedDigits(450, var2, var2);
      Ed448.PointAffine var3 = new Ed448.PointAffine();
      Ed448.PointTemp var4 = new Ed448.PointTemp();
      pointSetNeutral(var1);
      int var5 = 17;

      while (true) {
         int var6 = var5;

         for (int var7 = 0; var7 < 5; var7++) {
            int var8 = 0;

            for (int var9 = 0; var9 < 5; var9++) {
               int var10 = var2[var6 >>> 5] >>> (var6 & 31);
               var8 &= ~(1 << var9);
               var8 ^= var10 << var9;
               var6 += 18;
            }

            int var12 = var8 >>> 4 & 1;
            int var13 = (var8 ^ -var12) & 15;
            pointLookup(var7, var13, var3);
            Ed448.F.cnegate(var12, var3.x);
            pointAdd(var3, var1, var4);
         }

         if (--var5 < 0) {
            return;
         }

         pointDouble(var1, var4);
      }
   }

   private static void scalarMultBaseEncoded(byte[] var0, byte[] var1, int var2) {
      Ed448.PointProjective var3 = new Ed448.PointProjective();
      scalarMultBase(var0, var3);
      if (0 == encodeResult(var3, var1, var2)) {
         throw new IllegalStateException();
      }
   }

   public static void scalarMultBaseXY(X448.Friend var0, byte[] var1, int var2, int[] var3, int[] var4) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by X448");
      } else {
         byte[] var5 = new byte[57];
         pruneScalar(var1, var2, var5);
         Ed448.PointProjective var6 = new Ed448.PointProjective();
         scalarMultBase(var5, var6);
         if (0 == checkPoint(var6)) {
            throw new IllegalStateException();
         } else {
            Ed448.F.copy(var6.x, 0, var3, 0);
            Ed448.F.copy(var6.y, 0, var4, 0);
         }
      }
   }

   private static void scalarMultOrderVar(Ed448.PointAffine var0, Ed448.PointProjective var1) {
      byte[] var2 = new byte[447];
      Scalar448.getOrderWnafVar(5, var2);
      byte var3 = 8;
      Ed448.PointProjective[] var4 = new Ed448.PointProjective[var3];
      Ed448.PointTemp var5 = new Ed448.PointTemp();
      pointPrecompute(var0, var4, 0, var3, var5);
      pointSetNeutral(var1);
      int var6 = 446;

      while (true) {
         byte var7 = var2[var6];
         if (var7 != 0) {
            int var8 = var7 >> 1 ^ var7 >> 31;
            pointAddVar(var7 < 0, var4[var8], var1, var5);
         }

         if (--var6 < 0) {
            return;
         }

         pointDouble(var1, var5);
      }
   }

   private static void scalarMultStraus225Var(int[] var0, int[] var1, Ed448.PointAffine var2, int[] var3, Ed448.PointAffine var4, Ed448.PointProjective var5) {
      precompute();
      byte[] var6 = new byte[450];
      byte[] var7 = new byte[225];
      byte[] var8 = new byte[225];
      Wnaf.getSignedVar(var0, 7, var6);
      Wnaf.getSignedVar(var1, 5, var7);
      Wnaf.getSignedVar(var3, 5, var8);
      byte var9 = 8;
      Ed448.PointProjective[] var10 = new Ed448.PointProjective[var9];
      Ed448.PointProjective[] var11 = new Ed448.PointProjective[var9];
      Ed448.PointTemp var12 = new Ed448.PointTemp();
      pointPrecompute(var2, var10, 0, var9, var12);
      pointPrecompute(var4, var11, 0, var9, var12);
      pointSetNeutral(var5);
      int var13 = 225;

      do {
         var13--;
      } while (var13 >= 0 && (var6[var13] | var6[225 + var13] | var7[var13] | var8[var13]) == 0);

      while (var13 >= 0) {
         byte var14 = var6[var13];
         if (var14 != 0) {
            int var15 = var14 >> 1 ^ var14 >> 31;
            pointAddVar(var14 < 0, PRECOMP_BASE_WNAF[var15], var5, var12);
         }

         byte var19 = var6[225 + var13];
         if (var19 != 0) {
            int var16 = var19 >> 1 ^ var19 >> 31;
            pointAddVar(var19 < 0, PRECOMP_BASE225_WNAF[var16], var5, var12);
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

         pointDouble(var5, var12);
         var13--;
      }

      pointDouble(var5, var12);
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

   public static void signPrehash(byte[] var0, int var1, byte[] var2, Xof var3, byte[] var4, int var5) {
      byte[] var6 = new byte[64];
      if (64 != var3.doFinal(var6, 0, 64)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var7 = 1;
         implSign(var0, var1, var2, var7, var6, 0, var6.length, var4, var5);
      }
   }

   public static void signPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, Xof var5, byte[] var6, int var7) {
      byte[] var8 = new byte[64];
      if (64 != var5.doFinal(var8, 0, 64)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var9 = 1;
         implSign(var0, var1, var2, var3, var4, var9, var8, 0, var8.length, var6, var7);
      }
   }

   public static boolean validatePublicKeyFull(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 57);
      if (!checkPointFullVar(var2)) {
         return false;
      } else {
         Ed448.PointAffine var3 = new Ed448.PointAffine();
         return !decodePointVar(var2, false, var3) ? false : checkPointOrderVar(var3);
      }
   }

   public static Ed448.PublicPoint validatePublicKeyFullExport(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 57);
      if (!checkPointFullVar(var2)) {
         return null;
      } else {
         Ed448.PointAffine var3 = new Ed448.PointAffine();
         if (!decodePointVar(var2, false, var3)) {
            return null;
         } else {
            return !checkPointOrderVar(var3) ? null : exportPoint(var3);
         }
      }
   }

   public static boolean validatePublicKeyPartial(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 57);
      if (!checkPointFullVar(var2)) {
         return false;
      } else {
         Ed448.PointAffine var3 = new Ed448.PointAffine();
         return decodePointVar(var2, false, var3);
      }
   }

   public static Ed448.PublicPoint validatePublicKeyPartialExport(byte[] var0, int var1) {
      byte[] var2 = copy(var0, var1, 57);
      if (!checkPointFullVar(var2)) {
         return null;
      } else {
         Ed448.PointAffine var3 = new Ed448.PointAffine();
         return !decodePointVar(var2, false, var3) ? null : exportPoint(var3);
      }
   }

   public static boolean verify(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6, int var7) {
      byte var8 = 0;
      return implVerify(var0, var1, var2, var3, var4, var8, var5, var6, var7);
   }

   public static boolean verify(byte[] var0, int var1, Ed448.PublicPoint var2, byte[] var3, byte[] var4, int var5, int var6) {
      byte var7 = 0;
      return implVerify(var0, var1, var2, var3, var7, var4, var5, var6);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, byte[] var5, int var6) {
      byte var7 = 1;
      return implVerify(var0, var1, var2, var3, var4, var7, var5, var6, 64);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, Ed448.PublicPoint var2, byte[] var3, byte[] var4, int var5) {
      byte var6 = 1;
      return implVerify(var0, var1, var2, var3, var6, var4, var5, 64);
   }

   public static boolean verifyPrehash(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, Xof var5) {
      byte[] var6 = new byte[64];
      if (64 != var5.doFinal(var6, 0, 64)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var7 = 1;
         return implVerify(var0, var1, var2, var3, var4, var7, var6, 0, var6.length);
      }
   }

   public static boolean verifyPrehash(byte[] var0, int var1, Ed448.PublicPoint var2, byte[] var3, Xof var4) {
      byte[] var5 = new byte[64];
      if (64 != var4.doFinal(var5, 0, 64)) {
         throw new IllegalArgumentException("ph");
      } else {
         byte var6 = 1;
         return implVerify(var0, var1, var2, var3, var6, var5, 0, var5.length);
      }
   }

   public static final class Algorithm {
      public static final int Ed448 = 0;
      public static final int Ed448ph = 1;
   }

   private static class F extends X448Field {
   }

   private static class PointAffine {
      int[] x = Ed448.F.create();
      int[] y = Ed448.F.create();

      private PointAffine() {
      }
   }

   private static class PointProjective {
      int[] x = Ed448.F.create();
      int[] y = Ed448.F.create();
      int[] z = Ed448.F.create();

      private PointProjective() {
      }
   }

   private static class PointTemp {
      int[] r0 = Ed448.F.create();
      int[] r1 = Ed448.F.create();
      int[] r2 = Ed448.F.create();
      int[] r3 = Ed448.F.create();
      int[] r4 = Ed448.F.create();
      int[] r5 = Ed448.F.create();
      int[] r6 = Ed448.F.create();
      int[] r7 = Ed448.F.create();

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
