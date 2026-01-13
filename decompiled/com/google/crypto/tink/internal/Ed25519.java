package com.google.crypto.tink.internal;

import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

public final class Ed25519 {
   public static final int SECRET_KEY_LEN = 32;
   public static final int PUBLIC_KEY_LEN = 32;
   public static final int SIGNATURE_LEN = 64;
   private static final Ed25519.CachedXYT CACHED_NEUTRAL = new Ed25519.CachedXYT(
      new long[]{1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
      new long[]{1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
      new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}
   );
   private static final Ed25519.PartialXYZT NEUTRAL = new Ed25519.PartialXYZT(
      new Ed25519.XYZ(
         new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
         new long[]{1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L},
         new long[]{1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}
      ),
      new long[]{1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}
   );
   static final byte[] GROUP_ORDER = new byte[]{
      -19, -45, -11, 92, 26, 99, 18, 88, -42, -100, -9, -94, -34, -7, -34, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16
   };

   private static void add(Ed25519.PartialXYZT partialXYZT, Ed25519.XYZT extended, Ed25519.CachedXYT cached) {
      long[] t = new long[10];
      Field25519.sum(partialXYZT.xyz.x, extended.xyz.y, extended.xyz.x);
      Field25519.sub(partialXYZT.xyz.y, extended.xyz.y, extended.xyz.x);
      Field25519.mult(partialXYZT.xyz.y, partialXYZT.xyz.y, cached.yMinusX);
      Field25519.mult(partialXYZT.xyz.z, partialXYZT.xyz.x, cached.yPlusX);
      Field25519.mult(partialXYZT.t, extended.t, cached.t2d);
      cached.multByZ(partialXYZT.xyz.x, extended.xyz.z);
      Field25519.sum(t, partialXYZT.xyz.x, partialXYZT.xyz.x);
      Field25519.sub(partialXYZT.xyz.x, partialXYZT.xyz.z, partialXYZT.xyz.y);
      Field25519.sum(partialXYZT.xyz.y, partialXYZT.xyz.z, partialXYZT.xyz.y);
      Field25519.sum(partialXYZT.xyz.z, t, partialXYZT.t);
      Field25519.sub(partialXYZT.t, t, partialXYZT.t);
   }

   private static void sub(Ed25519.PartialXYZT partialXYZT, Ed25519.XYZT extended, Ed25519.CachedXYT cached) {
      long[] t = new long[10];
      Field25519.sum(partialXYZT.xyz.x, extended.xyz.y, extended.xyz.x);
      Field25519.sub(partialXYZT.xyz.y, extended.xyz.y, extended.xyz.x);
      Field25519.mult(partialXYZT.xyz.y, partialXYZT.xyz.y, cached.yPlusX);
      Field25519.mult(partialXYZT.xyz.z, partialXYZT.xyz.x, cached.yMinusX);
      Field25519.mult(partialXYZT.t, extended.t, cached.t2d);
      cached.multByZ(partialXYZT.xyz.x, extended.xyz.z);
      Field25519.sum(t, partialXYZT.xyz.x, partialXYZT.xyz.x);
      Field25519.sub(partialXYZT.xyz.x, partialXYZT.xyz.z, partialXYZT.xyz.y);
      Field25519.sum(partialXYZT.xyz.y, partialXYZT.xyz.z, partialXYZT.xyz.y);
      Field25519.sub(partialXYZT.xyz.z, t, partialXYZT.t);
      Field25519.sum(partialXYZT.t, t, partialXYZT.t);
   }

   private static void doubleXYZ(Ed25519.PartialXYZT partialXYZT, Ed25519.XYZ p) {
      long[] t0 = new long[10];
      Field25519.square(partialXYZT.xyz.x, p.x);
      Field25519.square(partialXYZT.xyz.z, p.y);
      Field25519.square(partialXYZT.t, p.z);
      Field25519.sum(partialXYZT.t, partialXYZT.t, partialXYZT.t);
      Field25519.sum(partialXYZT.xyz.y, p.x, p.y);
      Field25519.square(t0, partialXYZT.xyz.y);
      Field25519.sum(partialXYZT.xyz.y, partialXYZT.xyz.z, partialXYZT.xyz.x);
      Field25519.sub(partialXYZT.xyz.z, partialXYZT.xyz.z, partialXYZT.xyz.x);
      Field25519.sub(partialXYZT.xyz.x, t0, partialXYZT.xyz.y);
      Field25519.sub(partialXYZT.t, partialXYZT.t, partialXYZT.xyz.z);
   }

   private static void doubleXYZT(Ed25519.PartialXYZT partialXYZT, Ed25519.XYZT p) {
      doubleXYZ(partialXYZT, p.xyz);
   }

   private static int eq(int a, int b) {
      int r = ~(a ^ b) & 0xFF;
      r &= r << 4;
      r &= r << 2;
      r &= r << 1;
      return r >> 7 & 1;
   }

   private static void select(Ed25519.CachedXYT t, int pos, byte b) {
      int bnegative = (b & 255) >> 7;
      int babs = b - ((-bnegative & b) << 1);
      t.copyConditional(Ed25519Constants.B_TABLE[pos][0], eq(babs, 1));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][1], eq(babs, 2));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][2], eq(babs, 3));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][3], eq(babs, 4));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][4], eq(babs, 5));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][5], eq(babs, 6));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][6], eq(babs, 7));
      t.copyConditional(Ed25519Constants.B_TABLE[pos][7], eq(babs, 8));
      long[] yPlusX = Arrays.copyOf(t.yMinusX, 10);
      long[] yMinusX = Arrays.copyOf(t.yPlusX, 10);
      long[] t2d = Arrays.copyOf(t.t2d, 10);
      neg(t2d, t2d);
      Ed25519.CachedXYT minust = new Ed25519.CachedXYT(yPlusX, yMinusX, t2d);
      t.copyConditional(minust, bnegative);
   }

   private static Ed25519.XYZ scalarMultWithBase(byte[] a) {
      byte[] e = new byte[64];

      for (int i = 0; i < 32; i++) {
         e[2 * i + 0] = (byte)((a[i] & 255) >> 0 & 15);
         e[2 * i + 1] = (byte)((a[i] & 255) >> 4 & 15);
      }

      int carry = 0;

      for (int i = 0; i < e.length - 1; i++) {
         e[i] = (byte)(e[i] + carry);
         int var9 = e[i] + 8;
         carry = var9 >> 4;
         e[i] = (byte)(e[i] - (carry << 4));
      }

      e[e.length - 1] = (byte)(e[e.length - 1] + carry);
      Ed25519.PartialXYZT ret = new Ed25519.PartialXYZT(NEUTRAL);
      Ed25519.XYZT xyzt = new Ed25519.XYZT();

      for (int i = 1; i < e.length; i += 2) {
         Ed25519.CachedXYT t = new Ed25519.CachedXYT(CACHED_NEUTRAL);
         select(t, i / 2, e[i]);
         add(ret, Ed25519.XYZT.fromPartialXYZT(xyzt, ret), t);
      }

      Ed25519.XYZ xyz = new Ed25519.XYZ();
      doubleXYZ(ret, Ed25519.XYZ.fromPartialXYZT(xyz, ret));
      doubleXYZ(ret, Ed25519.XYZ.fromPartialXYZT(xyz, ret));
      doubleXYZ(ret, Ed25519.XYZ.fromPartialXYZT(xyz, ret));
      doubleXYZ(ret, Ed25519.XYZ.fromPartialXYZT(xyz, ret));

      for (int i = 0; i < e.length; i += 2) {
         Ed25519.CachedXYT t = new Ed25519.CachedXYT(CACHED_NEUTRAL);
         select(t, i / 2, e[i]);
         add(ret, Ed25519.XYZT.fromPartialXYZT(xyzt, ret), t);
      }

      Ed25519.XYZ result = new Ed25519.XYZ(ret);
      if (!result.isOnCurve()) {
         throw new IllegalStateException("arithmetic error in scalar multiplication");
      } else {
         return result;
      }
   }

   public static byte[] scalarMultWithBaseToBytes(byte[] a) {
      return scalarMultWithBase(a).toBytes();
   }

   private static byte[] slide(byte[] a) {
      byte[] r = new byte[256];

      for (int i = 0; i < 256; i++) {
         r[i] = (byte)(1 & (a[i >> 3] & 255) >> (i & 7));
      }

      for (int i = 0; i < 256; i++) {
         if (r[i] != 0) {
            for (int b = 1; b <= 6 && i + b < 256; b++) {
               if (r[i + b] != 0) {
                  if (r[i] + (r[i + b] << b) <= 15) {
                     r[i] = (byte)(r[i] + (r[i + b] << b));
                     r[i + b] = 0;
                  } else {
                     if (r[i] - (r[i + b] << b) < -15) {
                        break;
                     }

                     r[i] = (byte)(r[i] - (r[i + b] << b));

                     for (int k = i + b; k < 256; k++) {
                        if (r[k] == 0) {
                           r[k] = 1;
                           break;
                        }

                        r[k] = 0;
                     }
                  }
               }
            }
         }
      }

      return r;
   }

   private static Ed25519.XYZ doubleScalarMultVarTime(byte[] a, Ed25519.XYZT pointA, byte[] b) {
      Ed25519.CachedXYZT[] pointAArray = new Ed25519.CachedXYZT[8];
      pointAArray[0] = new Ed25519.CachedXYZT(pointA);
      Ed25519.PartialXYZT t = new Ed25519.PartialXYZT();
      doubleXYZT(t, pointA);
      Ed25519.XYZT doubleA = new Ed25519.XYZT(t);

      for (int i = 1; i < pointAArray.length; i++) {
         add(t, doubleA, pointAArray[i - 1]);
         pointAArray[i] = new Ed25519.CachedXYZT(new Ed25519.XYZT(t));
      }

      byte[] aSlide = slide(a);
      byte[] bSlide = slide(b);
      t = new Ed25519.PartialXYZT(NEUTRAL);
      Ed25519.XYZT u = new Ed25519.XYZT();
      int i = 255;

      while (i >= 0 && aSlide[i] == 0 && bSlide[i] == 0) {
         i--;
      }

      for (; i >= 0; i--) {
         doubleXYZ(t, new Ed25519.XYZ(t));
         if (aSlide[i] > 0) {
            add(t, Ed25519.XYZT.fromPartialXYZT(u, t), pointAArray[aSlide[i] / 2]);
         } else if (aSlide[i] < 0) {
            sub(t, Ed25519.XYZT.fromPartialXYZT(u, t), pointAArray[-aSlide[i] / 2]);
         }

         if (bSlide[i] > 0) {
            add(t, Ed25519.XYZT.fromPartialXYZT(u, t), Ed25519Constants.B2[bSlide[i] / 2]);
         } else if (bSlide[i] < 0) {
            sub(t, Ed25519.XYZT.fromPartialXYZT(u, t), Ed25519Constants.B2[-bSlide[i] / 2]);
         }
      }

      return new Ed25519.XYZ(t);
   }

   private static boolean isNonZeroVarTime(long[] in) {
      long[] inCopy = new long[in.length + 1];
      System.arraycopy(in, 0, inCopy, 0, in.length);
      Field25519.reduceCoefficients(inCopy);
      byte[] bytes = Field25519.contract(inCopy);

      for (byte b : bytes) {
         if (b != 0) {
            return true;
         }
      }

      return false;
   }

   private static int getLsb(long[] in) {
      return Field25519.contract(in)[0] & 1;
   }

   private static void neg(long[] out, long[] in) {
      for (int i = 0; i < in.length; i++) {
         out[i] = -in[i];
      }
   }

   private static void pow2252m3(long[] out, long[] in) {
      long[] t0 = new long[10];
      long[] t1 = new long[10];
      long[] t2 = new long[10];
      Field25519.square(t0, in);
      Field25519.square(t1, t0);

      for (int i = 1; i < 2; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t1, in, t1);
      Field25519.mult(t0, t0, t1);
      Field25519.square(t0, t0);
      Field25519.mult(t0, t1, t0);
      Field25519.square(t1, t0);

      for (int i = 1; i < 5; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t0, t1, t0);
      Field25519.square(t1, t0);

      for (int i = 1; i < 10; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t1, t1, t0);
      Field25519.square(t2, t1);

      for (int i = 1; i < 20; i++) {
         Field25519.square(t2, t2);
      }

      Field25519.mult(t1, t2, t1);
      Field25519.square(t1, t1);

      for (int i = 1; i < 10; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t0, t1, t0);
      Field25519.square(t1, t0);

      for (int i = 1; i < 50; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t1, t1, t0);
      Field25519.square(t2, t1);

      for (int i = 1; i < 100; i++) {
         Field25519.square(t2, t2);
      }

      Field25519.mult(t1, t2, t1);
      Field25519.square(t1, t1);

      for (int i = 1; i < 50; i++) {
         Field25519.square(t1, t1);
      }

      Field25519.mult(t0, t1, t0);
      Field25519.square(t0, t0);

      for (int i = 1; i < 2; i++) {
         Field25519.square(t0, t0);
      }

      Field25519.mult(out, t0, in);
   }

   private static long load3(byte[] in, int idx) {
      long result = in[idx] & 255L;
      result |= (long)(in[idx + 1] & 255) << 8;
      return result | (long)(in[idx + 2] & 255) << 16;
   }

   private static long load4(byte[] in, int idx) {
      long result = load3(in, idx);
      return result | (long)(in[idx + 3] & 255) << 24;
   }

   private static void reduce(byte[] s) {
      long s0 = 2097151L & load3(s, 0);
      long s1 = 2097151L & load4(s, 2) >> 5;
      long s2 = 2097151L & load3(s, 5) >> 2;
      long s3 = 2097151L & load4(s, 7) >> 7;
      long s4 = 2097151L & load4(s, 10) >> 4;
      long s5 = 2097151L & load3(s, 13) >> 1;
      long s6 = 2097151L & load4(s, 15) >> 6;
      long s7 = 2097151L & load3(s, 18) >> 3;
      long s8 = 2097151L & load3(s, 21);
      long s9 = 2097151L & load4(s, 23) >> 5;
      long s10 = 2097151L & load3(s, 26) >> 2;
      long s11 = 2097151L & load4(s, 28) >> 7;
      long s12 = 2097151L & load4(s, 31) >> 4;
      long s13 = 2097151L & load3(s, 34) >> 1;
      long s14 = 2097151L & load4(s, 36) >> 6;
      long s15 = 2097151L & load3(s, 39) >> 3;
      long s16 = 2097151L & load3(s, 42);
      long s17 = 2097151L & load4(s, 44) >> 5;
      long s18 = 2097151L & load3(s, 47) >> 2;
      long s19 = 2097151L & load4(s, 49) >> 7;
      long s20 = 2097151L & load4(s, 52) >> 4;
      long s21 = 2097151L & load3(s, 55) >> 1;
      long s22 = 2097151L & load4(s, 57) >> 6;
      long s23 = load4(s, 60) >> 3;
      s11 += s23 * 666643L;
      s12 += s23 * 470296L;
      s13 += s23 * 654183L;
      s14 -= s23 * 997805L;
      s15 += s23 * 136657L;
      s16 -= s23 * 683901L;
      s10 += s22 * 666643L;
      s11 += s22 * 470296L;
      s12 += s22 * 654183L;
      s13 -= s22 * 997805L;
      s14 += s22 * 136657L;
      s15 -= s22 * 683901L;
      s9 += s21 * 666643L;
      s10 += s21 * 470296L;
      s11 += s21 * 654183L;
      s12 -= s21 * 997805L;
      s13 += s21 * 136657L;
      s14 -= s21 * 683901L;
      s8 += s20 * 666643L;
      s9 += s20 * 470296L;
      s10 += s20 * 654183L;
      s11 -= s20 * 997805L;
      s12 += s20 * 136657L;
      s13 -= s20 * 683901L;
      s7 += s19 * 666643L;
      s8 += s19 * 470296L;
      s9 += s19 * 654183L;
      s10 -= s19 * 997805L;
      s11 += s19 * 136657L;
      s12 -= s19 * 683901L;
      s6 += s18 * 666643L;
      s7 += s18 * 470296L;
      s8 += s18 * 654183L;
      s9 -= s18 * 997805L;
      s10 += s18 * 136657L;
      s11 -= s18 * 683901L;
      long carry6 = s6 + 1048576L >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      long carry8 = s8 + 1048576L >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      long carry10 = s10 + 1048576L >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      long carry12 = s12 + 1048576L >> 21;
      s13 += carry12;
      s12 -= carry12 << 21;
      long carry14 = s14 + 1048576L >> 21;
      s15 += carry14;
      s14 -= carry14 << 21;
      long carry16 = s16 + 1048576L >> 21;
      s17 += carry16;
      s16 -= carry16 << 21;
      long carry7 = s7 + 1048576L >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      long carry9 = s9 + 1048576L >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      long carry11 = s11 + 1048576L >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      long carry13 = s13 + 1048576L >> 21;
      s14 += carry13;
      s13 -= carry13 << 21;
      long carry15 = s15 + 1048576L >> 21;
      s16 += carry15;
      s15 -= carry15 << 21;
      s5 += s17 * 666643L;
      s6 += s17 * 470296L;
      s7 += s17 * 654183L;
      s8 -= s17 * 997805L;
      s9 += s17 * 136657L;
      s10 -= s17 * 683901L;
      s4 += s16 * 666643L;
      s5 += s16 * 470296L;
      s6 += s16 * 654183L;
      s7 -= s16 * 997805L;
      s8 += s16 * 136657L;
      s9 -= s16 * 683901L;
      s3 += s15 * 666643L;
      s4 += s15 * 470296L;
      s5 += s15 * 654183L;
      s6 -= s15 * 997805L;
      s7 += s15 * 136657L;
      s8 -= s15 * 683901L;
      s2 += s14 * 666643L;
      s3 += s14 * 470296L;
      s4 += s14 * 654183L;
      s5 -= s14 * 997805L;
      s6 += s14 * 136657L;
      s7 -= s14 * 683901L;
      s1 += s13 * 666643L;
      s2 += s13 * 470296L;
      s3 += s13 * 654183L;
      s4 -= s13 * 997805L;
      s5 += s13 * 136657L;
      s6 -= s13 * 683901L;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      s12 = 0L;
      long carry0 = s0 + 1048576L >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      long carry2 = s2 + 1048576L >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      long carry4 = s4 + 1048576L >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry6 = s6 + 1048576L >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry8 = s8 + 1048576L >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry10 = s10 + 1048576L >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      long carry1 = s1 + 1048576L >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      long carry3 = s3 + 1048576L >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      long carry5 = s5 + 1048576L >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry7 = s7 + 1048576L >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry9 = s9 + 1048576L >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry11 = s11 + 1048576L >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      s12 = 0L;
      carry0 = s0 >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      carry1 = s1 >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      carry2 = s2 >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      carry3 = s3 >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      carry4 = s4 >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry5 = s5 >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry6 = s6 >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry7 = s7 >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry8 = s8 >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry9 = s9 >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry10 = s10 >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      carry11 = s11 >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      carry0 = s0 >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      carry1 = s1 >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      carry2 = s2 >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      carry3 = s3 >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      carry4 = s4 >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry5 = s5 >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry6 = s6 >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry7 = s7 >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry8 = s8 >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry9 = s9 >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry10 = s10 >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      s[0] = (byte)s0;
      s[1] = (byte)(s0 >> 8);
      s[2] = (byte)(s0 >> 16 | s1 << 5);
      s[3] = (byte)(s1 >> 3);
      s[4] = (byte)(s1 >> 11);
      s[5] = (byte)(s1 >> 19 | s2 << 2);
      s[6] = (byte)(s2 >> 6);
      s[7] = (byte)(s2 >> 14 | s3 << 7);
      s[8] = (byte)(s3 >> 1);
      s[9] = (byte)(s3 >> 9);
      s[10] = (byte)(s3 >> 17 | s4 << 4);
      s[11] = (byte)(s4 >> 4);
      s[12] = (byte)(s4 >> 12);
      s[13] = (byte)(s4 >> 20 | s5 << 1);
      s[14] = (byte)(s5 >> 7);
      s[15] = (byte)(s5 >> 15 | s6 << 6);
      s[16] = (byte)(s6 >> 2);
      s[17] = (byte)(s6 >> 10);
      s[18] = (byte)(s6 >> 18 | s7 << 3);
      s[19] = (byte)(s7 >> 5);
      s[20] = (byte)(s7 >> 13);
      s[21] = (byte)s8;
      s[22] = (byte)(s8 >> 8);
      s[23] = (byte)(s8 >> 16 | s9 << 5);
      s[24] = (byte)(s9 >> 3);
      s[25] = (byte)(s9 >> 11);
      s[26] = (byte)(s9 >> 19 | s10 << 2);
      s[27] = (byte)(s10 >> 6);
      s[28] = (byte)(s10 >> 14 | s11 << 7);
      s[29] = (byte)(s11 >> 1);
      s[30] = (byte)(s11 >> 9);
      s[31] = (byte)(s11 >> 17);
   }

   private static void mulAdd(byte[] s, byte[] a, byte[] b, byte[] c) {
      long a0 = 2097151L & load3(a, 0);
      long a1 = 2097151L & load4(a, 2) >> 5;
      long a2 = 2097151L & load3(a, 5) >> 2;
      long a3 = 2097151L & load4(a, 7) >> 7;
      long a4 = 2097151L & load4(a, 10) >> 4;
      long a5 = 2097151L & load3(a, 13) >> 1;
      long a6 = 2097151L & load4(a, 15) >> 6;
      long a7 = 2097151L & load3(a, 18) >> 3;
      long a8 = 2097151L & load3(a, 21);
      long a9 = 2097151L & load4(a, 23) >> 5;
      long a10 = 2097151L & load3(a, 26) >> 2;
      long a11 = load4(a, 28) >> 7;
      long b0 = 2097151L & load3(b, 0);
      long b1 = 2097151L & load4(b, 2) >> 5;
      long b2 = 2097151L & load3(b, 5) >> 2;
      long b3 = 2097151L & load4(b, 7) >> 7;
      long b4 = 2097151L & load4(b, 10) >> 4;
      long b5 = 2097151L & load3(b, 13) >> 1;
      long b6 = 2097151L & load4(b, 15) >> 6;
      long b7 = 2097151L & load3(b, 18) >> 3;
      long b8 = 2097151L & load3(b, 21);
      long b9 = 2097151L & load4(b, 23) >> 5;
      long b10 = 2097151L & load3(b, 26) >> 2;
      long b11 = load4(b, 28) >> 7;
      long c0 = 2097151L & load3(c, 0);
      long c1 = 2097151L & load4(c, 2) >> 5;
      long c2 = 2097151L & load3(c, 5) >> 2;
      long c3 = 2097151L & load4(c, 7) >> 7;
      long c4 = 2097151L & load4(c, 10) >> 4;
      long c5 = 2097151L & load3(c, 13) >> 1;
      long c6 = 2097151L & load4(c, 15) >> 6;
      long c7 = 2097151L & load3(c, 18) >> 3;
      long c8 = 2097151L & load3(c, 21);
      long c9 = 2097151L & load4(c, 23) >> 5;
      long c10 = 2097151L & load3(c, 26) >> 2;
      long c11 = load4(c, 28) >> 7;
      long s0 = c0 + a0 * b0;
      long s1 = c1 + a0 * b1 + a1 * b0;
      long s2 = c2 + a0 * b2 + a1 * b1 + a2 * b0;
      long s3 = c3 + a0 * b3 + a1 * b2 + a2 * b1 + a3 * b0;
      long s4 = c4 + a0 * b4 + a1 * b3 + a2 * b2 + a3 * b1 + a4 * b0;
      long s5 = c5 + a0 * b5 + a1 * b4 + a2 * b3 + a3 * b2 + a4 * b1 + a5 * b0;
      long s6 = c6 + a0 * b6 + a1 * b5 + a2 * b4 + a3 * b3 + a4 * b2 + a5 * b1 + a6 * b0;
      long s7 = c7 + a0 * b7 + a1 * b6 + a2 * b5 + a3 * b4 + a4 * b3 + a5 * b2 + a6 * b1 + a7 * b0;
      long s8 = c8 + a0 * b8 + a1 * b7 + a2 * b6 + a3 * b5 + a4 * b4 + a5 * b3 + a6 * b2 + a7 * b1 + a8 * b0;
      long s9 = c9 + a0 * b9 + a1 * b8 + a2 * b7 + a3 * b6 + a4 * b5 + a5 * b4 + a6 * b3 + a7 * b2 + a8 * b1 + a9 * b0;
      long s10 = c10 + a0 * b10 + a1 * b9 + a2 * b8 + a3 * b7 + a4 * b6 + a5 * b5 + a6 * b4 + a7 * b3 + a8 * b2 + a9 * b1 + a10 * b0;
      long s11 = c11 + a0 * b11 + a1 * b10 + a2 * b9 + a3 * b8 + a4 * b7 + a5 * b6 + a6 * b5 + a7 * b4 + a8 * b3 + a9 * b2 + a10 * b1 + a11 * b0;
      long s12 = a1 * b11 + a2 * b10 + a3 * b9 + a4 * b8 + a5 * b7 + a6 * b6 + a7 * b5 + a8 * b4 + a9 * b3 + a10 * b2 + a11 * b1;
      long s13 = a2 * b11 + a3 * b10 + a4 * b9 + a5 * b8 + a6 * b7 + a7 * b6 + a8 * b5 + a9 * b4 + a10 * b3 + a11 * b2;
      long s14 = a3 * b11 + a4 * b10 + a5 * b9 + a6 * b8 + a7 * b7 + a8 * b6 + a9 * b5 + a10 * b4 + a11 * b3;
      long s15 = a4 * b11 + a5 * b10 + a6 * b9 + a7 * b8 + a8 * b7 + a9 * b6 + a10 * b5 + a11 * b4;
      long s16 = a5 * b11 + a6 * b10 + a7 * b9 + a8 * b8 + a9 * b7 + a10 * b6 + a11 * b5;
      long s17 = a6 * b11 + a7 * b10 + a8 * b9 + a9 * b8 + a10 * b7 + a11 * b6;
      long s18 = a7 * b11 + a8 * b10 + a9 * b9 + a10 * b8 + a11 * b7;
      long s19 = a8 * b11 + a9 * b10 + a10 * b9 + a11 * b8;
      long s20 = a9 * b11 + a10 * b10 + a11 * b9;
      long s21 = a10 * b11 + a11 * b10;
      long s22 = a11 * b11;
      long s23 = 0L;
      long carry0 = s0 + 1048576L >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      long carry2 = s2 + 1048576L >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      long carry4 = s4 + 1048576L >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      long carry6 = s6 + 1048576L >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      long carry8 = s8 + 1048576L >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      long carry10 = s10 + 1048576L >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      long carry12 = s12 + 1048576L >> 21;
      s13 += carry12;
      s12 -= carry12 << 21;
      long carry14 = s14 + 1048576L >> 21;
      s15 += carry14;
      s14 -= carry14 << 21;
      long carry16 = s16 + 1048576L >> 21;
      s17 += carry16;
      s16 -= carry16 << 21;
      long carry18 = s18 + 1048576L >> 21;
      s19 += carry18;
      s18 -= carry18 << 21;
      long carry20 = s20 + 1048576L >> 21;
      s21 += carry20;
      s20 -= carry20 << 21;
      long carry22 = s22 + 1048576L >> 21;
      s23 += carry22;
      s22 -= carry22 << 21;
      long carry1 = s1 + 1048576L >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      long carry3 = s3 + 1048576L >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      long carry5 = s5 + 1048576L >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      long carry7 = s7 + 1048576L >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      long carry9 = s9 + 1048576L >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      long carry11 = s11 + 1048576L >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      long carry13 = s13 + 1048576L >> 21;
      s14 += carry13;
      s13 -= carry13 << 21;
      long carry15 = s15 + 1048576L >> 21;
      s16 += carry15;
      s15 -= carry15 << 21;
      long carry17 = s17 + 1048576L >> 21;
      s18 += carry17;
      s17 -= carry17 << 21;
      long carry19 = s19 + 1048576L >> 21;
      s20 += carry19;
      s19 -= carry19 << 21;
      long carry21 = s21 + 1048576L >> 21;
      s22 += carry21;
      s21 -= carry21 << 21;
      s11 += s23 * 666643L;
      s12 += s23 * 470296L;
      s13 += s23 * 654183L;
      s14 -= s23 * 997805L;
      s15 += s23 * 136657L;
      s16 -= s23 * 683901L;
      s10 += s22 * 666643L;
      s11 += s22 * 470296L;
      s12 += s22 * 654183L;
      s13 -= s22 * 997805L;
      s14 += s22 * 136657L;
      s15 -= s22 * 683901L;
      s9 += s21 * 666643L;
      s10 += s21 * 470296L;
      s11 += s21 * 654183L;
      s12 -= s21 * 997805L;
      s13 += s21 * 136657L;
      s14 -= s21 * 683901L;
      s8 += s20 * 666643L;
      s9 += s20 * 470296L;
      s10 += s20 * 654183L;
      s11 -= s20 * 997805L;
      s12 += s20 * 136657L;
      s13 -= s20 * 683901L;
      s7 += s19 * 666643L;
      s8 += s19 * 470296L;
      s9 += s19 * 654183L;
      s10 -= s19 * 997805L;
      s11 += s19 * 136657L;
      s12 -= s19 * 683901L;
      s6 += s18 * 666643L;
      s7 += s18 * 470296L;
      s8 += s18 * 654183L;
      s9 -= s18 * 997805L;
      s10 += s18 * 136657L;
      s11 -= s18 * 683901L;
      carry6 = s6 + 1048576L >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry8 = s8 + 1048576L >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry10 = s10 + 1048576L >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      carry12 = s12 + 1048576L >> 21;
      s13 += carry12;
      s12 -= carry12 << 21;
      carry14 = s14 + 1048576L >> 21;
      s15 += carry14;
      s14 -= carry14 << 21;
      carry16 = s16 + 1048576L >> 21;
      s17 += carry16;
      s16 -= carry16 << 21;
      carry7 = s7 + 1048576L >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry9 = s9 + 1048576L >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry11 = s11 + 1048576L >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      carry13 = s13 + 1048576L >> 21;
      s14 += carry13;
      s13 -= carry13 << 21;
      carry15 = s15 + 1048576L >> 21;
      s16 += carry15;
      s15 -= carry15 << 21;
      s5 += s17 * 666643L;
      s6 += s17 * 470296L;
      s7 += s17 * 654183L;
      s8 -= s17 * 997805L;
      s9 += s17 * 136657L;
      s10 -= s17 * 683901L;
      s4 += s16 * 666643L;
      s5 += s16 * 470296L;
      s6 += s16 * 654183L;
      s7 -= s16 * 997805L;
      s8 += s16 * 136657L;
      s9 -= s16 * 683901L;
      s3 += s15 * 666643L;
      s4 += s15 * 470296L;
      s5 += s15 * 654183L;
      s6 -= s15 * 997805L;
      s7 += s15 * 136657L;
      s8 -= s15 * 683901L;
      s2 += s14 * 666643L;
      s3 += s14 * 470296L;
      s4 += s14 * 654183L;
      s5 -= s14 * 997805L;
      s6 += s14 * 136657L;
      s7 -= s14 * 683901L;
      s1 += s13 * 666643L;
      s2 += s13 * 470296L;
      s3 += s13 * 654183L;
      s4 -= s13 * 997805L;
      s5 += s13 * 136657L;
      s6 -= s13 * 683901L;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      s12 = 0L;
      carry0 = s0 + 1048576L >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      carry2 = s2 + 1048576L >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      carry4 = s4 + 1048576L >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry6 = s6 + 1048576L >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry8 = s8 + 1048576L >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry10 = s10 + 1048576L >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      carry1 = s1 + 1048576L >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      carry3 = s3 + 1048576L >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      carry5 = s5 + 1048576L >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry7 = s7 + 1048576L >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry9 = s9 + 1048576L >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry11 = s11 + 1048576L >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      s12 = 0L;
      carry0 = s0 >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      carry1 = s1 >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      carry2 = s2 >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      carry3 = s3 >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      carry4 = s4 >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry5 = s5 >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry6 = s6 >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry7 = s7 >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry8 = s8 >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry9 = s9 >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry10 = s10 >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      carry11 = s11 >> 21;
      s12 += carry11;
      s11 -= carry11 << 21;
      s0 += s12 * 666643L;
      s1 += s12 * 470296L;
      s2 += s12 * 654183L;
      s3 -= s12 * 997805L;
      s4 += s12 * 136657L;
      s5 -= s12 * 683901L;
      carry0 = s0 >> 21;
      s1 += carry0;
      s0 -= carry0 << 21;
      carry1 = s1 >> 21;
      s2 += carry1;
      s1 -= carry1 << 21;
      carry2 = s2 >> 21;
      s3 += carry2;
      s2 -= carry2 << 21;
      carry3 = s3 >> 21;
      s4 += carry3;
      s3 -= carry3 << 21;
      carry4 = s4 >> 21;
      s5 += carry4;
      s4 -= carry4 << 21;
      carry5 = s5 >> 21;
      s6 += carry5;
      s5 -= carry5 << 21;
      carry6 = s6 >> 21;
      s7 += carry6;
      s6 -= carry6 << 21;
      carry7 = s7 >> 21;
      s8 += carry7;
      s7 -= carry7 << 21;
      carry8 = s8 >> 21;
      s9 += carry8;
      s8 -= carry8 << 21;
      carry9 = s9 >> 21;
      s10 += carry9;
      s9 -= carry9 << 21;
      carry10 = s10 >> 21;
      s11 += carry10;
      s10 -= carry10 << 21;
      s[0] = (byte)s0;
      s[1] = (byte)(s0 >> 8);
      s[2] = (byte)(s0 >> 16 | s1 << 5);
      s[3] = (byte)(s1 >> 3);
      s[4] = (byte)(s1 >> 11);
      s[5] = (byte)(s1 >> 19 | s2 << 2);
      s[6] = (byte)(s2 >> 6);
      s[7] = (byte)(s2 >> 14 | s3 << 7);
      s[8] = (byte)(s3 >> 1);
      s[9] = (byte)(s3 >> 9);
      s[10] = (byte)(s3 >> 17 | s4 << 4);
      s[11] = (byte)(s4 >> 4);
      s[12] = (byte)(s4 >> 12);
      s[13] = (byte)(s4 >> 20 | s5 << 1);
      s[14] = (byte)(s5 >> 7);
      s[15] = (byte)(s5 >> 15 | s6 << 6);
      s[16] = (byte)(s6 >> 2);
      s[17] = (byte)(s6 >> 10);
      s[18] = (byte)(s6 >> 18 | s7 << 3);
      s[19] = (byte)(s7 >> 5);
      s[20] = (byte)(s7 >> 13);
      s[21] = (byte)s8;
      s[22] = (byte)(s8 >> 8);
      s[23] = (byte)(s8 >> 16 | s9 << 5);
      s[24] = (byte)(s9 >> 3);
      s[25] = (byte)(s9 >> 11);
      s[26] = (byte)(s9 >> 19 | s10 << 2);
      s[27] = (byte)(s10 >> 6);
      s[28] = (byte)(s10 >> 14 | s11 << 7);
      s[29] = (byte)(s11 >> 1);
      s[30] = (byte)(s11 >> 9);
      s[31] = (byte)(s11 >> 17);
   }

   public static byte[] getHashedScalar(final byte[] privateKey) throws GeneralSecurityException {
      MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance("SHA-512");
      digest.update(privateKey, 0, 32);
      byte[] h = digest.digest();
      h[0] = (byte)(h[0] & 248);
      h[31] = (byte)(h[31] & 127);
      h[31] = (byte)(h[31] | 64);
      return h;
   }

   public static byte[] sign(final byte[] message, final byte[] publicKey, final byte[] hashedPrivateKey) throws GeneralSecurityException {
      byte[] messageCopy = Arrays.copyOfRange(message, 0, message.length);
      MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance("SHA-512");
      digest.update(hashedPrivateKey, 32, 32);
      digest.update(messageCopy);
      byte[] r = digest.digest();
      reduce(r);
      byte[] rB = Arrays.copyOfRange(scalarMultWithBase(r).toBytes(), 0, 32);
      digest.reset();
      digest.update(rB);
      digest.update(publicKey);
      digest.update(messageCopy);
      byte[] hram = digest.digest();
      reduce(hram);
      byte[] s = new byte[32];
      mulAdd(s, hram, hashedPrivateKey, r);
      return Bytes.concat(rB, s);
   }

   private static boolean isSmallerThanGroupOrder(byte[] s) {
      for (int j = 31; j >= 0; j--) {
         int a = s[j] & 255;
         int b = GROUP_ORDER[j] & 255;
         if (a != b) {
            return a < b;
         }
      }

      return false;
   }

   public static boolean verify(final byte[] message, final byte[] signature, final byte[] publicKey) throws GeneralSecurityException {
      if (signature.length != 64) {
         return false;
      } else {
         byte[] s = Arrays.copyOfRange(signature, 32, 64);
         if (!isSmallerThanGroupOrder(s)) {
            return false;
         } else {
            MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance("SHA-512");
            digest.update(signature, 0, 32);
            digest.update(publicKey);
            digest.update(message);
            byte[] h = digest.digest();
            reduce(h);
            Ed25519.XYZT negPublicKey = Ed25519.XYZT.fromBytesNegateVarTime(publicKey);
            Ed25519.XYZ xyz = doubleScalarMultVarTime(h, negPublicKey, s);
            byte[] expectedR = xyz.toBytes();

            for (int i = 0; i < 32; i++) {
               if (expectedR[i] != signature[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static void init() {
      if (Ed25519Constants.D == null) {
         throw new IllegalStateException("Could not initialize Ed25519.");
      }
   }

   private Ed25519() {
   }

   static class CachedXYT {
      final long[] yPlusX;
      final long[] yMinusX;
      final long[] t2d;

      CachedXYT() {
         this(new long[10], new long[10], new long[10]);
      }

      CachedXYT(long[] yPlusX, long[] yMinusX, long[] t2d) {
         this.yPlusX = yPlusX;
         this.yMinusX = yMinusX;
         this.t2d = t2d;
      }

      CachedXYT(Ed25519.CachedXYT other) {
         this.yPlusX = Arrays.copyOf(other.yPlusX, 10);
         this.yMinusX = Arrays.copyOf(other.yMinusX, 10);
         this.t2d = Arrays.copyOf(other.t2d, 10);
      }

      void multByZ(long[] output, long[] in) {
         System.arraycopy(in, 0, output, 0, 10);
      }

      void copyConditional(Ed25519.CachedXYT other, int icopy) {
         Curve25519.copyConditional(this.yPlusX, other.yPlusX, icopy);
         Curve25519.copyConditional(this.yMinusX, other.yMinusX, icopy);
         Curve25519.copyConditional(this.t2d, other.t2d, icopy);
      }
   }

   private static class CachedXYZT extends Ed25519.CachedXYT {
      private final long[] z;

      CachedXYZT() {
         this(new long[10], new long[10], new long[10], new long[10]);
      }

      CachedXYZT(Ed25519.XYZT xyzt) {
         this();
         Field25519.sum(this.yPlusX, xyzt.xyz.y, xyzt.xyz.x);
         Field25519.sub(this.yMinusX, xyzt.xyz.y, xyzt.xyz.x);
         System.arraycopy(xyzt.xyz.z, 0, this.z, 0, 10);
         Field25519.mult(this.t2d, xyzt.t, Ed25519Constants.D2);
      }

      CachedXYZT(long[] yPlusX, long[] yMinusX, long[] z, long[] t2d) {
         super(yPlusX, yMinusX, t2d);
         this.z = z;
      }

      @Override
      public void multByZ(long[] output, long[] in) {
         Field25519.mult(output, in, this.z);
      }
   }

   private static class PartialXYZT {
      final Ed25519.XYZ xyz;
      final long[] t;

      PartialXYZT() {
         this(new Ed25519.XYZ(), new long[10]);
      }

      PartialXYZT(Ed25519.XYZ xyz, long[] t) {
         this.xyz = xyz;
         this.t = t;
      }

      PartialXYZT(Ed25519.PartialXYZT other) {
         this.xyz = new Ed25519.XYZ(other.xyz);
         this.t = Arrays.copyOf(other.t, 10);
      }
   }

   private static class XYZ {
      final long[] x;
      final long[] y;
      final long[] z;

      XYZ() {
         this(new long[10], new long[10], new long[10]);
      }

      XYZ(long[] x, long[] y, long[] z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      XYZ(Ed25519.XYZ xyz) {
         this.x = Arrays.copyOf(xyz.x, 10);
         this.y = Arrays.copyOf(xyz.y, 10);
         this.z = Arrays.copyOf(xyz.z, 10);
      }

      XYZ(Ed25519.PartialXYZT partialXYZT) {
         this();
         fromPartialXYZT(this, partialXYZT);
      }

      @CanIgnoreReturnValue
      static Ed25519.XYZ fromPartialXYZT(Ed25519.XYZ out, Ed25519.PartialXYZT in) {
         Field25519.mult(out.x, in.xyz.x, in.t);
         Field25519.mult(out.y, in.xyz.y, in.xyz.z);
         Field25519.mult(out.z, in.xyz.z, in.t);
         return out;
      }

      byte[] toBytes() {
         long[] recip = new long[10];
         long[] x = new long[10];
         long[] y = new long[10];
         Field25519.inverse(recip, this.z);
         Field25519.mult(x, this.x, recip);
         Field25519.mult(y, this.y, recip);
         byte[] s = Field25519.contract(y);
         s[31] = (byte)(s[31] ^ Ed25519.getLsb(x) << 7);
         return s;
      }

      boolean isOnCurve() {
         long[] x2 = new long[10];
         Field25519.square(x2, this.x);
         long[] y2 = new long[10];
         Field25519.square(y2, this.y);
         long[] z2 = new long[10];
         Field25519.square(z2, this.z);
         long[] z4 = new long[10];
         Field25519.square(z4, z2);
         long[] lhs = new long[10];
         Field25519.sub(lhs, y2, x2);
         Field25519.mult(lhs, lhs, z2);
         long[] rhs = new long[10];
         Field25519.mult(rhs, x2, y2);
         Field25519.mult(rhs, rhs, Ed25519Constants.D);
         Field25519.sum(rhs, z4);
         Field25519.reduce(rhs, rhs);
         return Bytes.equal(Field25519.contract(lhs), Field25519.contract(rhs));
      }
   }

   private static class XYZT {
      final Ed25519.XYZ xyz;
      final long[] t;

      XYZT() {
         this(new Ed25519.XYZ(), new long[10]);
      }

      XYZT(Ed25519.XYZ xyz, long[] t) {
         this.xyz = xyz;
         this.t = t;
      }

      XYZT(Ed25519.PartialXYZT partialXYZT) {
         this();
         fromPartialXYZT(this, partialXYZT);
      }

      @CanIgnoreReturnValue
      private static Ed25519.XYZT fromPartialXYZT(Ed25519.XYZT out, Ed25519.PartialXYZT in) {
         Field25519.mult(out.xyz.x, in.xyz.x, in.t);
         Field25519.mult(out.xyz.y, in.xyz.y, in.xyz.z);
         Field25519.mult(out.xyz.z, in.xyz.z, in.t);
         Field25519.mult(out.t, in.xyz.x, in.xyz.y);
         return out;
      }

      private static Ed25519.XYZT fromBytesNegateVarTime(byte[] s) throws GeneralSecurityException {
         long[] x = new long[10];
         long[] y = Field25519.expand(s);
         long[] z = new long[10];
         z[0] = 1L;
         long[] t = new long[10];
         long[] u = new long[10];
         long[] v = new long[10];
         long[] vxx = new long[10];
         long[] check = new long[10];
         Field25519.square(u, y);
         Field25519.mult(v, u, Ed25519Constants.D);
         Field25519.sub(u, u, z);
         Field25519.sum(v, v, z);
         long[] v3 = new long[10];
         Field25519.square(v3, v);
         Field25519.mult(v3, v3, v);
         Field25519.square(x, v3);
         Field25519.mult(x, x, v);
         Field25519.mult(x, x, u);
         Ed25519.pow2252m3(x, x);
         Field25519.mult(x, x, v3);
         Field25519.mult(x, x, u);
         Field25519.square(vxx, x);
         Field25519.mult(vxx, vxx, v);
         Field25519.sub(check, vxx, u);
         if (Ed25519.isNonZeroVarTime(check)) {
            Field25519.sum(check, vxx, u);
            if (Ed25519.isNonZeroVarTime(check)) {
               throw new GeneralSecurityException("Cannot convert given bytes to extended projective coordinates. No square root exists for modulo 2^255-19");
            }

            Field25519.mult(x, x, Ed25519Constants.SQRTM1);
         }

         if (!Ed25519.isNonZeroVarTime(x) && (s[31] & 255) >> 7 != 0) {
            throw new GeneralSecurityException(
               "Cannot convert given bytes to extended projective coordinates. Computed x is zero and encoded x's least significant bit is not zero"
            );
         } else {
            if (Ed25519.getLsb(x) == (s[31] & 255) >> 7) {
               Ed25519.neg(x, x);
            }

            Field25519.mult(t, x, y);
            return new Ed25519.XYZT(new Ed25519.XYZ(x, y, z), t);
         }
      }
   }
}
