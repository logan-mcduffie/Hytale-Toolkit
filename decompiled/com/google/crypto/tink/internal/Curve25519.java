package com.google.crypto.tink.internal;

import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.Hex;
import java.security.InvalidKeyException;
import java.util.Arrays;

@Alpha
public final class Curve25519 {
   static final byte[][] BANNED_PUBLIC_KEYS = new byte[][]{
      {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {-32, -21, 122, 124, 59, 65, -72, -82, 22, 86, -29, -6, -15, -97, -60, 106, -38, 9, -115, -21, -100, 50, -79, -3, -122, 98, 5, 22, 95, 73, -72, 0},
      {95, -100, -107, -68, -93, 80, -116, 36, -79, -48, -79, 85, -100, -125, -17, 91, 4, 68, 92, -60, 88, 28, -114, -122, -40, 34, 78, -35, -48, -97, 17, 87},
      {-20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 127},
      {-19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 127},
      {-18, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 127}
   };

   private static void monty(long[] x2, long[] z2, long[] x3, long[] z3, long[] x, long[] z, long[] xprime, long[] zprime, long[] qmqp) {
      long[] origx = Arrays.copyOf(x, 10);
      long[] zzz = new long[19];
      long[] xx = new long[19];
      long[] zz = new long[19];
      long[] xxprime = new long[19];
      long[] zzprime = new long[19];
      long[] zzzprime = new long[19];
      long[] xxxprime = new long[19];
      Field25519.sum(x, z);
      Field25519.sub(z, origx);
      long[] origxprime = Arrays.copyOf(xprime, 10);
      Field25519.sum(xprime, zprime);
      Field25519.sub(zprime, origxprime);
      Field25519.product(xxprime, xprime, z);
      Field25519.product(zzprime, x, zprime);
      Field25519.reduceSizeByModularReduction(xxprime);
      Field25519.reduceCoefficients(xxprime);
      Field25519.reduceSizeByModularReduction(zzprime);
      Field25519.reduceCoefficients(zzprime);
      System.arraycopy(xxprime, 0, origxprime, 0, 10);
      Field25519.sum(xxprime, zzprime);
      Field25519.sub(zzprime, origxprime);
      Field25519.square(xxxprime, xxprime);
      Field25519.square(zzzprime, zzprime);
      Field25519.product(zzprime, zzzprime, qmqp);
      Field25519.reduceSizeByModularReduction(zzprime);
      Field25519.reduceCoefficients(zzprime);
      System.arraycopy(xxxprime, 0, x3, 0, 10);
      System.arraycopy(zzprime, 0, z3, 0, 10);
      Field25519.square(xx, x);
      Field25519.square(zz, z);
      Field25519.product(x2, xx, zz);
      Field25519.reduceSizeByModularReduction(x2);
      Field25519.reduceCoefficients(x2);
      Field25519.sub(zz, xx);
      Arrays.fill(zzz, 10, zzz.length - 1, 0L);
      Field25519.scalarProduct(zzz, zz, 121665L);
      Field25519.reduceCoefficients(zzz);
      Field25519.sum(zzz, xx);
      Field25519.product(z2, zz, zzz);
      Field25519.reduceSizeByModularReduction(z2);
      Field25519.reduceCoefficients(z2);
   }

   static void swapConditional(long[] a, long[] b, int iswap) {
      int swap = -iswap;

      for (int i = 0; i < 10; i++) {
         int x = swap & ((int)a[i] ^ (int)b[i]);
         a[i] = (int)a[i] ^ x;
         b[i] = (int)b[i] ^ x;
      }
   }

   static void copyConditional(long[] a, long[] b, int icopy) {
      int copy = -icopy;

      for (int i = 0; i < 10; i++) {
         int x = copy & ((int)a[i] ^ (int)b[i]);
         a[i] = (int)a[i] ^ x;
      }
   }

   public static void curveMult(long[] resultx, byte[] n, byte[] qBytes) throws InvalidKeyException {
      byte[] qBytesWithoutMsb = validatePubKeyAndClearMsb(qBytes);
      long[] q = Field25519.expand(qBytesWithoutMsb);
      long[] nqpqx = new long[19];
      long[] nqpqz = new long[19];
      nqpqz[0] = 1L;
      long[] nqx = new long[19];
      nqx[0] = 1L;
      long[] nqz = new long[19];
      long[] nqpqx2 = new long[19];
      long[] nqpqz2 = new long[19];
      nqpqz2[0] = 1L;
      long[] nqx2 = new long[19];
      long[] nqz2 = new long[19];
      nqz2[0] = 1L;
      long[] t = new long[19];
      System.arraycopy(q, 0, nqpqx, 0, 10);

      for (int i = 0; i < 32; i++) {
         int b = n[32 - i - 1] & 255;

         for (int j = 0; j < 8; j++) {
            int bit = b >> 7 - j & 1;
            swapConditional(nqx, nqpqx, bit);
            swapConditional(nqz, nqpqz, bit);
            monty(nqx2, nqz2, nqpqx2, nqpqz2, nqx, nqz, nqpqx, nqpqz, q);
            swapConditional(nqx2, nqpqx2, bit);
            swapConditional(nqz2, nqpqz2, bit);
            t = nqx;
            nqx = nqx2;
            nqx2 = t;
            t = nqz;
            nqz = nqz2;
            nqz2 = t;
            t = nqpqx;
            nqpqx = nqpqx2;
            nqpqx2 = t;
            t = nqpqz;
            nqpqz = nqpqz2;
            nqpqz2 = t;
         }
      }

      long[] zmone = new long[10];
      Field25519.inverse(zmone, nqz);
      Field25519.mult(resultx, nqx, zmone);
      if (!isCollinear(q, resultx, nqpqx, nqpqz)) {
         throw new IllegalStateException("Arithmetic error in curve multiplication with the public key: " + Hex.encode(qBytes));
      }
   }

   private static byte[] validatePubKeyAndClearMsb(byte[] pubKey) throws InvalidKeyException {
      if (pubKey.length != 32) {
         throw new InvalidKeyException("Public key length is not 32-byte");
      } else {
         byte[] pubKeyWithoutMsb = Arrays.copyOf(pubKey, pubKey.length);
         pubKeyWithoutMsb[31] = (byte)(pubKeyWithoutMsb[31] & 127);

         for (int i = 0; i < BANNED_PUBLIC_KEYS.length; i++) {
            if (Bytes.equal(BANNED_PUBLIC_KEYS[i], pubKeyWithoutMsb)) {
               throw new InvalidKeyException("Banned public key: " + Hex.encode(BANNED_PUBLIC_KEYS[i]));
            }
         }

         return pubKeyWithoutMsb;
      }
   }

   private static boolean isCollinear(long[] x1, long[] x2, long[] x3, long[] z3) {
      long[] x1multx2 = new long[10];
      long[] x1addx2 = new long[10];
      long[] lhs = new long[11];
      long[] t = new long[11];
      long[] t2 = new long[11];
      Field25519.mult(x1multx2, x1, x2);
      Field25519.sum(x1addx2, x1, x2);
      long[] a = new long[10];
      a[0] = 486662L;
      Field25519.sum(t, x1addx2, a);
      Field25519.mult(t, t, z3);
      Field25519.sum(t, x3);
      Field25519.mult(t, t, x1multx2);
      Field25519.mult(t, t, x3);
      Field25519.scalarProduct(lhs, t, 4L);
      Field25519.reduceCoefficients(lhs);
      Field25519.mult(t, x1multx2, z3);
      Field25519.sub(t, t, z3);
      Field25519.mult(t2, x1addx2, x3);
      Field25519.sum(t, t, t2);
      Field25519.square(t, t);
      return Bytes.equal(Field25519.contract(lhs), Field25519.contract(t));
   }

   private Curve25519() {
   }
}
