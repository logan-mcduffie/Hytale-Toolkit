package com.google.crypto.tink.internal;

import java.math.BigInteger;

final class Ed25519Constants {
   static final long[] D = Field25519.expand(toLittleEndian(Ed25519Constants.D_BI));
   static final long[] D2 = Field25519.expand(toLittleEndian(Ed25519Constants.D2_BI));
   static final long[] SQRTM1 = Field25519.expand(toLittleEndian(Ed25519Constants.SQRTM1_BI));
   static final Ed25519.CachedXYT[][] B_TABLE = new Ed25519.CachedXYT[32][8];
   static final Ed25519.CachedXYT[] B2;
   private static final BigInteger P_BI = BigInteger.valueOf(2L).pow(255).subtract(BigInteger.valueOf(19L));
   private static final BigInteger D_BI = BigInteger.valueOf(-121665L).multiply(BigInteger.valueOf(121666L).modInverse(P_BI)).mod(P_BI);
   private static final BigInteger D2_BI = BigInteger.valueOf(2L).multiply(D_BI).mod(P_BI);
   private static final BigInteger SQRTM1_BI = BigInteger.valueOf(2L).modPow(P_BI.subtract(BigInteger.ONE).divide(BigInteger.valueOf(4L)), P_BI);

   private static BigInteger recoverX(BigInteger y) {
      BigInteger xx = y.pow(2).subtract(BigInteger.ONE).multiply(D_BI.multiply(y.pow(2)).add(BigInteger.ONE).modInverse(P_BI));
      BigInteger x = xx.modPow(P_BI.add(BigInteger.valueOf(3L)).divide(BigInteger.valueOf(8L)), P_BI);
      if (!x.pow(2).subtract(xx).mod(P_BI).equals(BigInteger.ZERO)) {
         x = x.multiply(SQRTM1_BI).mod(P_BI);
      }

      if (x.testBit(0)) {
         x = P_BI.subtract(x);
      }

      return x;
   }

   private static Ed25519Constants.Point edwards(Ed25519Constants.Point a, Ed25519Constants.Point b) {
      Ed25519Constants.Point o = new Ed25519Constants.Point();
      BigInteger xxyy = D_BI.multiply(a.x.multiply(b.x).multiply(a.y).multiply(b.y)).mod(P_BI);
      o.x = a.x.multiply(b.y).add(b.x.multiply(a.y)).multiply(BigInteger.ONE.add(xxyy).modInverse(P_BI)).mod(P_BI);
      o.y = a.y.multiply(b.y).add(a.x.multiply(b.x)).multiply(BigInteger.ONE.subtract(xxyy).modInverse(P_BI)).mod(P_BI);
      return o;
   }

   private static byte[] toLittleEndian(BigInteger n) {
      byte[] b = new byte[32];
      byte[] nBytes = n.toByteArray();
      System.arraycopy(nBytes, 0, b, 32 - nBytes.length, nBytes.length);

      for (int i = 0; i < b.length / 2; i++) {
         byte t = b[i];
         b[i] = b[b.length - i - 1];
         b[b.length - i - 1] = t;
      }

      return b;
   }

   private static Ed25519.CachedXYT getCachedXYT(Ed25519Constants.Point p) {
      return new Ed25519.CachedXYT(
         Field25519.expand(toLittleEndian(p.y.add(p.x).mod(P_BI))),
         Field25519.expand(toLittleEndian(p.y.subtract(p.x).mod(P_BI))),
         Field25519.expand(toLittleEndian(D2_BI.multiply(p.x).multiply(p.y).mod(P_BI)))
      );
   }

   private Ed25519Constants() {
   }

   static {
      Ed25519Constants.Point b = new Ed25519Constants.Point();
      b.y = BigInteger.valueOf(4L).multiply(BigInteger.valueOf(5L).modInverse(P_BI)).mod(P_BI);
      b.x = recoverX(b.y);
      Ed25519Constants.Point bi = b;

      for (int i = 0; i < 32; i++) {
         Ed25519Constants.Point bij = bi;

         for (int j = 0; j < 8; j++) {
            B_TABLE[i][j] = getCachedXYT(bij);
            bij = edwards(bij, bi);
         }

         for (int j = 0; j < 8; j++) {
            bi = edwards(bi, bi);
         }
      }

      bi = b;
      Ed25519Constants.Point b2 = edwards(b, b);
      B2 = new Ed25519.CachedXYT[8];

      for (int i = 0; i < 8; i++) {
         B2[i] = getCachedXYT(bi);
         bi = edwards(bi, b2);
      }
   }

   private static class Point {
      private BigInteger x;
      private BigInteger y;

      private Point() {
      }
   }
}
