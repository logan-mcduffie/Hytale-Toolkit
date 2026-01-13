package org.bouncycastle.pqc.crypto.newhope;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;

class NewHope {
   private static final boolean STATISTICAL_TEST = false;
   public static final int AGREEMENT_SIZE = 32;
   public static final int POLY_SIZE = 1024;
   public static final int SENDA_BYTES = 1824;
   public static final int SENDB_BYTES = 2048;

   public static void keygen(SecureRandom var0, byte[] var1, short[] var2) {
      byte[] var3 = new byte[32];
      var0.nextBytes(var3);
      sha3(var3);
      short[] var4 = new short[1024];
      generateA(var4, var3);
      byte[] var5 = new byte[32];
      var0.nextBytes(var5);
      Poly.getNoise(var2, var5, (byte)0);
      Poly.toNTT(var2);
      short[] var6 = new short[1024];
      Poly.getNoise(var6, var5, (byte)1);
      Poly.toNTT(var6);
      short[] var7 = new short[1024];
      Poly.pointWise(var4, var2, var7);
      short[] var8 = new short[1024];
      Poly.add(var7, var6, var8);
      encodeA(var1, var8, var3);
   }

   public static void sharedB(SecureRandom var0, byte[] var1, byte[] var2, byte[] var3) {
      short[] var4 = new short[1024];
      byte[] var5 = new byte[32];
      decodeA(var4, var5, var3);
      short[] var6 = new short[1024];
      generateA(var6, var5);
      byte[] var7 = new byte[32];
      var0.nextBytes(var7);
      short[] var8 = new short[1024];
      Poly.getNoise(var8, var7, (byte)0);
      Poly.toNTT(var8);
      short[] var9 = new short[1024];
      Poly.getNoise(var9, var7, (byte)1);
      Poly.toNTT(var9);
      short[] var10 = new short[1024];
      Poly.pointWise(var6, var8, var10);
      Poly.add(var10, var9, var10);
      short[] var11 = new short[1024];
      Poly.pointWise(var4, var8, var11);
      Poly.fromNTT(var11);
      short[] var12 = new short[1024];
      Poly.getNoise(var12, var7, (byte)2);
      Poly.add(var11, var12, var11);
      short[] var13 = new short[1024];
      ErrorCorrection.helpRec(var13, var11, var7, (byte)3);
      encodeB(var2, var10, var13);
      ErrorCorrection.rec(var1, var11, var13);
      sha3(var1);
   }

   public static void sharedA(byte[] var0, short[] var1, byte[] var2) {
      short[] var3 = new short[1024];
      short[] var4 = new short[1024];
      decodeB(var3, var4, var2);
      short[] var5 = new short[1024];
      Poly.pointWise(var1, var3, var5);
      Poly.fromNTT(var5);
      ErrorCorrection.rec(var0, var5, var4);
      sha3(var0);
   }

   static void decodeA(short[] var0, byte[] var1, byte[] var2) {
      Poly.fromBytes(var0, var2);
      System.arraycopy(var2, 1792, var1, 0, 32);
   }

   static void decodeB(short[] var0, short[] var1, byte[] var2) {
      Poly.fromBytes(var0, var2);

      for (int var3 = 0; var3 < 256; var3++) {
         int var4 = 4 * var3;
         int var5 = var2[1792 + var3] & 255;
         var1[var4 + 0] = (short)(var5 & 3);
         var1[var4 + 1] = (short)(var5 >>> 2 & 3);
         var1[var4 + 2] = (short)(var5 >>> 4 & 3);
         var1[var4 + 3] = (short)(var5 >>> 6);
      }
   }

   static void encodeA(byte[] var0, short[] var1, byte[] var2) {
      Poly.toBytes(var0, var1);
      System.arraycopy(var2, 0, var0, 1792, 32);
   }

   static void encodeB(byte[] var0, short[] var1, short[] var2) {
      Poly.toBytes(var0, var1);

      for (int var3 = 0; var3 < 256; var3++) {
         int var4 = 4 * var3;
         var0[1792 + var3] = (byte)(var2[var4] | var2[var4 + 1] << 2 | var2[var4 + 2] << 4 | var2[var4 + 3] << 6);
      }
   }

   static void generateA(short[] var0, byte[] var1) {
      Poly.uniform(var0, var1);
   }

   static void sha3(byte[] var0) {
      SHA3Digest var1 = new SHA3Digest(256);
      var1.update(var0, 0, 32);
      var1.doFinal(var0, 0);
   }
}
