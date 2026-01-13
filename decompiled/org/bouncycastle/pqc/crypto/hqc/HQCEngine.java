package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

class HQCEngine {
   private final int n;
   private final int n1;
   private final int k;
   private final int delta;
   private final int w;
   private final int wr;
   private final int g;
   private final int fft;
   private final int mulParam;
   private static final int SEED_BYTES = 32;
   private final int N_BYTE;
   private final int N_BYTE_64;
   private final int K_BYTE;
   private final int N1N2_BYTE_64;
   private final int N1N2_BYTE;
   private static final int SALT_SIZE_BYTES = 16;
   private final int[] generatorPoly;
   private final int N_MU;
   private final int pkSize;
   private final GF2PolynomialCalculator gf;
   private final long rejectionThreshold;

   public HQCEngine(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int[] var12) {
      this.n = var1;
      this.k = var4;
      this.delta = var6;
      this.w = var7;
      this.wr = var8;
      this.n1 = var2;
      this.generatorPoly = var12;
      this.g = var5;
      this.fft = var9;
      this.N_MU = var10;
      this.pkSize = var11;
      this.mulParam = var3 >> 7;
      this.N_BYTE = Utils.getByteSizeFromBitSize(var1);
      this.K_BYTE = var4;
      this.N_BYTE_64 = Utils.getByte64SizeFromBitSize(var1);
      this.N1N2_BYTE_64 = Utils.getByte64SizeFromBitSize(var2 * var3);
      this.N1N2_BYTE = Utils.getByteSizeFromBitSize(var2 * var3);
      long var13 = (1L << (var1 & 63)) - 1L;
      this.gf = new GF2PolynomialCalculator(this.N_BYTE_64, var1, var13);
      this.rejectionThreshold = 16777216L / var1 * var1;
   }

   public void genKeyPair(byte[] var1, byte[] var2, SecureRandom var3) {
      byte[] var4 = new byte[32];
      byte[] var5 = new byte[64];
      long[] var6 = new long[this.N_BYTE_64];
      long[] var7 = new long[this.N_BYTE_64];
      long[] var8 = new long[this.N_BYTE_64];
      var3.nextBytes(var4);
      Shake256RandomGenerator var9 = new Shake256RandomGenerator(var4, (byte)1);
      System.arraycopy(var4, 0, var2, this.pkSize + 32 + this.K_BYTE, 32);
      var9.nextBytes(var4);
      var9.nextBytes(var2, this.pkSize + 32, this.K_BYTE);
      hashHI(var5, 512, var4, var4.length, (byte)2);
      var9.init(var5, 0, 32, (byte)1);
      this.vectSampleFixedWeight1(var7, var9, this.w);
      this.vectSampleFixedWeight1(var6, var9, this.w);
      System.arraycopy(var5, 32, var1, 0, 32);
      var9.init(var5, 32, 32, (byte)1);
      this.vectSetRandom(var9, var8);
      this.gf.vectMul(var8, var7, var8);
      Longs.xorTo(this.N_BYTE_64, var6, 0, var8, 0);
      Utils.fromLongArrayToByteArray(var1, 32, var1.length - 32, var8);
      System.arraycopy(var5, 0, var2, this.pkSize, 32);
      System.arraycopy(var1, 0, var2, 0, this.pkSize);
      Arrays.clear(var5);
      Arrays.clear(var6);
      Arrays.clear(var7);
      Arrays.clear(var8);
   }

   public void encaps(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, SecureRandom var6) {
      byte[] var7 = new byte[this.K_BYTE];
      byte[] var8 = new byte[32];
      long[] var9 = new long[this.N_BYTE_64];
      long[] var10 = new long[this.N1N2_BYTE_64];
      var6.nextBytes(var7);
      var6.nextBytes(var5);
      hashHI(var8, 256, var4, var4.length, (byte)1);
      this.hashGJ(var3, 512, var8, var7, 0, var7.length, var5, 0, 16, (byte)0);
      this.pkeEncrypt(var9, var10, var4, var7, var3, 32);
      Utils.fromLongArrayToByteArray(var1, var9);
      Utils.fromLongArrayToByteArray(var2, var10);
      Arrays.clear(var9);
      Arrays.clear(var10);
      Arrays.clear(var7);
      Arrays.clear(var8);
   }

   public int decaps(byte[] var1, byte[] var2, byte[] var3) {
      long[] var4 = new long[this.N_BYTE_64];
      long[] var5 = new long[this.N_BYTE_64];
      long[] var6 = new long[this.N_BYTE_64];
      long[] var7 = new long[this.N_BYTE_64];
      byte[] var8 = new byte[32];
      byte[] var9 = new byte[64];
      byte[] var10 = new byte[this.k];
      byte[] var11 = new byte[32];
      byte[] var12 = new byte[this.n1];
      Shake256RandomGenerator var13 = new Shake256RandomGenerator(var3, this.pkSize, 32, (byte)1);
      this.vectSampleFixedWeight1(var7, var13, this.w);
      Utils.fromByteArrayToLongArray(var4, var2, 0, this.N_BYTE);
      Utils.fromByteArrayToLongArray(var5, var2, this.N_BYTE, this.N1N2_BYTE);
      this.gf.vectMul(var6, var7, var4);
      this.vectTruncate(var6);
      Longs.xorTo(this.N_BYTE_64, var5, 0, var6, 0);
      ReedMuller.decode(var12, var6, this.n1, this.mulParam);
      ReedSolomon.decode(var10, var12, this.n1, this.fft, this.delta, this.k, this.g);
      int var14 = 0;
      hashHI(var8, 256, var3, this.pkSize, (byte)1);
      this.hashGJ(var9, 512, var8, var10, 0, var10.length, var2, this.N_BYTE + this.N1N2_BYTE, 16, (byte)0);
      System.arraycopy(var9, 0, var1, 0, 32);
      Arrays.fill(var7, 0L);
      this.pkeEncrypt(var6, var7, var3, var10, var9, 32);
      this.hashGJ(var11, 256, var8, var3, this.pkSize + 32, this.K_BYTE, var2, 0, var2.length, (byte)3);
      if (!Arrays.constantTimeAreEqual(this.N_BYTE_64, var4, 0, var6, 0)) {
         var14 = 1;
      }

      if (!Arrays.constantTimeAreEqual(this.N_BYTE_64, var5, 0, var7, 0)) {
         var14 = 1;
      }

      var14--;

      for (int var15 = 0; var15 < this.K_BYTE; var15++) {
         var1[var15] = (byte)((var1[var15] & var14 ^ var11[var15] & ~var14) & 0xFF);
      }

      Arrays.clear(var4);
      Arrays.clear(var5);
      Arrays.clear(var6);
      Arrays.clear(var7);
      Arrays.clear(var8);
      Arrays.clear(var9);
      Arrays.clear(var10);
      Arrays.clear(var11);
      Arrays.clear(var12);
      return -var14;
   }

   private void pkeEncrypt(long[] var1, long[] var2, byte[] var3, byte[] var4, byte[] var5, int var6) {
      long[] var7 = new long[this.N_BYTE_64];
      long[] var8 = new long[this.N_BYTE_64];
      byte[] var9 = new byte[this.n1];
      ReedSolomon.encode(var9, var4, this.n1, this.k, this.g, this.generatorPoly);
      ReedMuller.encode(var2, var9, this.n1, this.mulParam);
      Shake256RandomGenerator var10 = new Shake256RandomGenerator(var3, 0, 32, (byte)1);
      this.vectSetRandom(var10, var8);
      var10.init(var5, var6, 32, (byte)1);
      this.vectSampleFixedWeights2(var10, var7, this.wr);
      this.gf.vectMul(var1, var7, var8);
      Utils.fromByteArrayToLongArray(var8, var3, 32, this.pkSize - 32);
      this.gf.vectMul(var8, var7, var8);
      this.vectSampleFixedWeights2(var10, var7, this.wr);
      Longs.xorTo(this.N_BYTE_64, var7, 0, var8, 0);
      this.vectTruncate(var8);
      Longs.xorTo(this.N1N2_BYTE_64, var8, 0, var2, 0);
      this.vectSampleFixedWeights2(var10, var8, this.wr);
      Longs.xorTo(this.N_BYTE_64, var8, 0, var1, 0);
      Arrays.clear(var7);
      Arrays.clear(var8);
      Arrays.clear(var9);
   }

   private int barrettReduce(int var1) {
      long var2 = (long)var1 * this.N_MU >>> 32;
      int var4 = var1 - (int)(var2 * this.n);
      return var4 - (-(var4 - this.n >>> 31 ^ 1) & this.n);
   }

   private void generateRandomSupport(int[] var1, int var2, Shake256RandomGenerator var3) {
      int var4 = 3 * var2;
      byte[] var5 = new byte[var4];
      int var6 = var4;
      int var7 = 0;

      while (var7 < var2) {
         if (var6 == var4) {
            var3.xofGetBytes(var5, var4);
            var6 = 0;
         }

         int var8 = (var5[var6++] & 255) << 16 | (var5[var6++] & 255) << 8 | var5[var6++] & 255;
         if (var8 < this.rejectionThreshold) {
            var8 = this.barrettReduce(var8);
            boolean var9 = false;

            for (int var10 = 0; var10 < var7; var10++) {
               if (var1[var10] == var8) {
                  var9 = true;
                  break;
               }
            }

            if (!var9) {
               var1[var7++] = var8;
            }
         }
      }
   }

   private void writeSupportToVector(long[] var1, int[] var2, int var3) {
      int[] var4 = new int[this.wr];
      long[] var5 = new long[this.wr];

      for (int var6 = 0; var6 < var3; var6++) {
         var4[var6] = var2[var6] >>> 6;
         var5[var6] = 1L << (var2[var6] & 63);
      }

      for (int var11 = 0; var11 < var1.length; var11++) {
         long var7 = 0L;

         for (int var9 = 0; var9 < var3; var9++) {
            int var10 = var11 - var4[var9];
            var7 |= var5[var9] & -(1 ^ (var10 | -var10) >>> 31);
         }

         var1[var11] = var7;
      }
   }

   public void vectSampleFixedWeight1(long[] var1, Shake256RandomGenerator var2, int var3) {
      int[] var4 = new int[this.wr];
      this.generateRandomSupport(var4, var3, var2);
      this.writeSupportToVector(var1, var4, var3);
   }

   private static void hashHI(byte[] var0, int var1, byte[] var2, int var3, byte var4) {
      SHA3Digest var5 = new SHA3Digest(var1);
      var5.update(var2, 0, var3);
      var5.update(var4);
      var5.doFinal(var0, 0);
   }

   private void hashGJ(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, byte var10) {
      SHA3Digest var11 = new SHA3Digest(var2);
      var11.update(var3, 0, var3.length);
      var11.update(var4, var5, var6);
      var11.update(var7, var8, var9);
      var11.update(var10);
      var11.doFinal(var1, 0);
   }

   private void vectSetRandom(Shake256RandomGenerator var1, long[] var2) {
      byte[] var3 = new byte[var2.length << 3];
      var1.xofGetBytes(var3, this.N_BYTE);
      Pack.littleEndianToLong(var3, 0, var2);
      var2[this.N_BYTE_64 - 1] = var2[this.N_BYTE_64 - 1] & Utils.bitMask(this.n, 64L);
   }

   private void vectSampleFixedWeights2(Shake256RandomGenerator var1, long[] var2, int var3) {
      int[] var4 = new int[this.wr];
      byte[] var5 = new byte[this.wr << 2];
      var1.xofGetBytes(var5, var5.length);
      Pack.littleEndianToInt(var5, 0, var4);

      for (int var6 = 0; var6 < var3; var6++) {
         var4[var6] = var6 + (int)((var4[var6] & 4294967295L) * (this.n - var6) >> 32);
      }

      int var9 = var3 - 1;

      while (var9-- > 0) {
         int var7 = 0;

         for (int var8 = var9 + 1; var8 < var3; var8++) {
            var7 |= compareU32(var4[var8], var4[var9]);
         }

         var7 = -var7;
         var4[var9] = var7 & var9 ^ ~var7 & var4[var9];
      }

      this.writeSupportToVector(var2, var4, var3);
   }

   private static int compareU32(int var0, int var1) {
      return 1 ^ (var0 - var1 | var1 - var0) >>> 31;
   }

   private void vectTruncate(long[] var1) {
      Arrays.fill(var1, this.N1N2_BYTE_64, this.n + 63 >> 6, 0L);
   }
}
