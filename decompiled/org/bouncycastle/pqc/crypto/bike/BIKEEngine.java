package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

class BIKEEngine {
   private int r;
   private int w;
   private int hw;
   private int t;
   private int nbIter;
   private int tau;
   private final BIKERing bikeRing;
   private int L_BYTE;
   private int R_BYTE;
   private int R2_BYTE;

   public BIKEEngine(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.r = var1;
      this.w = var2;
      this.t = var3;
      this.nbIter = var5;
      this.tau = var6;
      this.hw = this.w / 2;
      this.L_BYTE = var4 / 8;
      this.R_BYTE = var1 + 7 >>> 3;
      this.R2_BYTE = 2 * var1 + 7 >>> 3;
      this.bikeRing = new BIKERing(var1);
   }

   public int getSessionKeySize() {
      return this.L_BYTE;
   }

   private byte[] functionH(byte[] var1) {
      byte[] var2 = new byte[2 * this.R_BYTE];
      SHAKEDigest var3 = new SHAKEDigest(256);
      var3.update(var1, 0, var1.length);
      BIKEUtils.generateRandomByteArray(var2, 2 * this.r, this.t, var3);
      return var2;
   }

   private void functionL(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = new byte[48];
      SHA3Digest var5 = new SHA3Digest(384);
      var5.update(var1, 0, var1.length);
      var5.update(var2, 0, var2.length);
      var5.doFinal(var4, 0);
      System.arraycopy(var4, 0, var3, 0, this.L_BYTE);
   }

   private void functionK(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      byte[] var5 = new byte[48];
      SHA3Digest var6 = new SHA3Digest(384);
      var6.update(var1, 0, var1.length);
      var6.update(var2, 0, var2.length);
      var6.update(var3, 0, var3.length);
      var6.doFinal(var5, 0);
      System.arraycopy(var5, 0, var4, 0, this.L_BYTE);
   }

   public void genKeyPair(byte[] var1, byte[] var2, byte[] var3, byte[] var4, SecureRandom var5) {
      byte[] var6 = new byte[64];
      var5.nextBytes(var6);
      SHAKEDigest var7 = new SHAKEDigest(256);
      var7.update(var6, 0, this.L_BYTE);
      BIKEUtils.generateRandomByteArray(var1, this.r, this.hw, var7);
      BIKEUtils.generateRandomByteArray(var2, this.r, this.hw, var7);
      long[] var8 = this.bikeRing.create();
      long[] var9 = this.bikeRing.create();
      this.bikeRing.decodeBytes(var1, var8);
      this.bikeRing.decodeBytes(var2, var9);
      long[] var10 = this.bikeRing.create();
      this.bikeRing.inv(var8, var10);
      this.bikeRing.multiply(var10, var9, var10);
      this.bikeRing.encodeBytes(var10, var4);
      System.arraycopy(var6, this.L_BYTE, var3, 0, var3.length);
   }

   public void encaps(byte[] var1, byte[] var2, byte[] var3, byte[] var4, SecureRandom var5) {
      byte[] var6 = new byte[this.L_BYTE];
      var5.nextBytes(var6);
      byte[] var7 = this.functionH(var6);
      byte[] var8 = new byte[this.R_BYTE];
      byte[] var9 = new byte[this.R_BYTE];
      this.splitEBytes(var7, var8, var9);
      long[] var10 = this.bikeRing.create();
      long[] var11 = this.bikeRing.create();
      this.bikeRing.decodeBytes(var8, var10);
      this.bikeRing.decodeBytes(var9, var11);
      long[] var12 = this.bikeRing.create();
      this.bikeRing.decodeBytes(var4, var12);
      this.bikeRing.multiply(var12, var11, var12);
      this.bikeRing.add(var12, var10, var12);
      this.bikeRing.encodeBytes(var12, var1);
      this.functionL(var8, var9, var2);
      Bytes.xorTo(this.L_BYTE, var6, var2);
      this.functionK(var6, var1, var2, var3);
   }

   public void decaps(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      int[] var7 = new int[this.hw];
      int[] var8 = new int[this.hw];
      this.convertToCompact(var7, var2);
      this.convertToCompact(var8, var3);
      byte[] var9 = this.computeSyndrome(var5, var2);
      byte[] var10 = this.BGFDecoder(var9, var7, var8);
      byte[] var11 = new byte[2 * this.R_BYTE];
      BIKEUtils.fromBitArrayToByteArray(var11, var10, 0, 2 * this.r);
      byte[] var12 = new byte[this.R_BYTE];
      byte[] var13 = new byte[this.R_BYTE];
      this.splitEBytes(var11, var12, var13);
      byte[] var14 = new byte[this.L_BYTE];
      this.functionL(var12, var13, var14);
      Bytes.xorTo(this.L_BYTE, var6, var14);
      byte[] var15 = this.functionH(var14);
      if (Arrays.areEqual(var11, 0, this.R2_BYTE, var15, 0, this.R2_BYTE)) {
         this.functionK(var14, var5, var6, var1);
      } else {
         this.functionK(var4, var5, var6, var1);
      }
   }

   private byte[] computeSyndrome(byte[] var1, byte[] var2) {
      long[] var3 = this.bikeRing.create();
      long[] var4 = this.bikeRing.create();
      this.bikeRing.decodeBytes(var1, var3);
      this.bikeRing.decodeBytes(var2, var4);
      this.bikeRing.multiply(var3, var4, var3);
      return this.bikeRing.encodeBitsTransposed(var3);
   }

   private byte[] BGFDecoder(byte[] var1, int[] var2, int[] var3) {
      byte[] var4 = new byte[2 * this.r];
      int[] var5 = this.getColumnFromCompactVersion(var2);
      int[] var6 = this.getColumnFromCompactVersion(var3);
      byte[] var7 = new byte[2 * this.r];
      byte[] var8 = new byte[this.r];
      byte[] var9 = new byte[2 * this.r];
      int var10 = this.threshold(BIKEUtils.getHammingWeight(var1), this.r);
      this.BFIter(var1, var4, var10, var2, var3, var5, var6, var7, var9, var8);
      this.BFMaskedIter(var1, var4, var7, (this.hw + 1) / 2 + 1, var2, var3, var5, var6);
      this.BFMaskedIter(var1, var4, var9, (this.hw + 1) / 2 + 1, var2, var3, var5, var6);

      for (int var11 = 1; var11 < this.nbIter; var11++) {
         Arrays.fill(var7, (byte)0);
         var10 = this.threshold(BIKEUtils.getHammingWeight(var1), this.r);
         this.BFIter2(var1, var4, var10, var2, var3, var5, var6, var8);
      }

      return BIKEUtils.getHammingWeight(var1) == 0 ? var4 : null;
   }

   private void BFIter(byte[] var1, byte[] var2, int var3, int[] var4, int[] var5, int[] var6, int[] var7, byte[] var8, byte[] var9, byte[] var10) {
      this.ctrAll(var6, var1, var10);
      int var11 = var10[0] & 255;
      int var12 = (var11 - var3 >> 31) + 1;
      int var13 = (var11 - (var3 - this.tau) >> 31) + 1;
      var2[0] ^= (byte)var12;
      var8[0] = (byte)var12;
      var9[0] = (byte)var13;

      for (int var15 = 1; var15 < this.r; var15++) {
         var12 = var10[var15] & 255;
         var13 = (var12 - var3 >> 31) + 1;
         int var14 = (var12 - (var3 - this.tau) >> 31) + 1;
         var2[this.r - var15] = (byte)(var2[this.r - var15] ^ (byte)var13);
         var8[var15] = (byte)var13;
         var9[var15] = (byte)var14;
      }

      this.ctrAll(var7, var1, var10);
      var11 = var10[0] & 255;
      var12 = (var11 - var3 >> 31) + 1;
      var13 = (var11 - (var3 - this.tau) >> 31) + 1;
      var2[this.r] = (byte)(var2[this.r] ^ (byte)var12);
      var8[this.r] = (byte)var12;
      var9[this.r] = (byte)var13;

      for (int var17 = 1; var17 < this.r; var17++) {
         var12 = var10[var17] & 255;
         var13 = (var12 - var3 >> 31) + 1;
         int var25 = (var12 - (var3 - this.tau) >> 31) + 1;
         var2[this.r + this.r - var17] = (byte)(var2[this.r + this.r - var17] ^ (byte)var13);
         var8[this.r + var17] = (byte)var13;
         var9[this.r + var17] = (byte)var25;
      }

      for (int var18 = 0; var18 < 2 * this.r; var18++) {
         this.recomputeSyndrome(var1, var18, var4, var5, var8[var18] != 0);
      }
   }

   private void BFIter2(byte[] var1, byte[] var2, int var3, int[] var4, int[] var5, int[] var6, int[] var7, byte[] var8) {
      int[] var9 = new int[2 * this.r];
      this.ctrAll(var6, var1, var8);
      int var10 = var8[0] & 255;
      int var11 = (var10 - var3 >> 31) + 1;
      var2[0] ^= (byte)var11;
      var9[0] = var11;

      for (int var13 = 1; var13 < this.r; var13++) {
         var11 = var8[var13] & 255;
         int var12 = (var11 - var3 >> 31) + 1;
         var2[this.r - var13] = (byte)(var2[this.r - var13] ^ (byte)var12);
         var9[var13] = var12;
      }

      this.ctrAll(var7, var1, var8);
      var10 = var8[0] & 255;
      var11 = (var10 - var3 >> 31) + 1;
      var2[this.r] = (byte)(var2[this.r] ^ (byte)var11);
      var9[this.r] = var11;

      for (int var15 = 1; var15 < this.r; var15++) {
         var11 = var8[var15] & 255;
         int var20 = (var11 - var3 >> 31) + 1;
         var2[this.r + this.r - var15] = (byte)(var2[this.r + this.r - var15] ^ (byte)var20);
         var9[this.r + var15] = var20;
      }

      for (int var16 = 0; var16 < 2 * this.r; var16++) {
         this.recomputeSyndrome(var1, var16, var4, var5, var9[var16] == 1);
      }
   }

   private void BFMaskedIter(byte[] var1, byte[] var2, byte[] var3, int var4, int[] var5, int[] var6, int[] var7, int[] var8) {
      int[] var9 = new int[2 * this.r];

      for (int var10 = 0; var10 < this.r; var10++) {
         if (var3[var10] == 1) {
            boolean var11 = this.ctr(var7, var1, var10) >= var4;
            this.updateNewErrorIndex(var2, var10, var11);
            var9[var10] = var11 ? 1 : 0;
         }
      }

      for (int var12 = 0; var12 < this.r; var12++) {
         if (var3[this.r + var12] == 1) {
            boolean var14 = this.ctr(var8, var1, var12) >= var4;
            this.updateNewErrorIndex(var2, this.r + var12, var14);
            var9[this.r + var12] = var14 ? 1 : 0;
         }
      }

      for (int var13 = 0; var13 < 2 * this.r; var13++) {
         this.recomputeSyndrome(var1, var13, var5, var6, var9[var13] == 1);
      }
   }

   private int threshold(int var1, int var2) {
      switch (var2) {
         case 12323:
            return thresholdFromParameters(var1, 0.0069722, 13.53, 36);
         case 24659:
            return thresholdFromParameters(var1, 0.005265, 15.2588, 52);
         case 40973:
            return thresholdFromParameters(var1, 0.00402312, 17.8785, 69);
         default:
            throw new IllegalArgumentException();
      }
   }

   private static int thresholdFromParameters(int var0, double var1, double var3, int var5) {
      return Math.max(var5, (int)Math.floor(var1 * var0 + var3));
   }

   private int ctr(int[] var1, byte[] var2, int var3) {
      int var4 = 0;
      int var5 = 0;

      for (int var6 = this.hw - 4; var5 <= var6; var5 += 4) {
         int var7 = var1[var5 + 0] + var3 - this.r;
         int var8 = var1[var5 + 1] + var3 - this.r;
         int var9 = var1[var5 + 2] + var3 - this.r;
         int var10 = var1[var5 + 3] + var3 - this.r;
         var7 += var7 >> 31 & this.r;
         var8 += var8 >> 31 & this.r;
         var9 += var9 >> 31 & this.r;
         var10 += var10 >> 31 & this.r;
         var4 += var2[var7] & 255;
         var4 += var2[var8] & 255;
         var4 += var2[var9] & 255;
         var4 += var2[var10] & 255;
      }

      while (var5 < this.hw) {
         int var15 = var1[var5] + var3 - this.r;
         var15 += var15 >> 31 & this.r;
         var4 += var2[var15] & 255;
         var5++;
      }

      return var4;
   }

   private void ctrAll(int[] var1, byte[] var2, byte[] var3) {
      int var4 = var1[0];
      int var5 = this.r - var4;
      System.arraycopy(var2, var4, var3, 0, var5);
      System.arraycopy(var2, 0, var3, var5, var4);

      for (int var10 = 1; var10 < this.hw; var10++) {
         var5 = var1[var10];
         int var6 = this.r - var5;
         int var7 = 0;

         for (int var8 = var6 - 4; var7 <= var8; var7 += 4) {
            var3[var7 + 0] = (byte)(var3[var7 + 0] + (var2[var5 + var7 + 0] & 255));
            var3[var7 + 1] = (byte)(var3[var7 + 1] + (var2[var5 + var7 + 1] & 255));
            var3[var7 + 2] = (byte)(var3[var7 + 2] + (var2[var5 + var7 + 2] & 255));
            var3[var7 + 3] = (byte)(var3[var7 + 3] + (var2[var5 + var7 + 3] & 255));
         }

         while (var7 < var6) {
            var3[var7] = (byte)(var3[var7] + (var2[var5 + var7] & 255));
            var7++;
         }

         int var12 = var6;

         for (int var9 = this.r - 4; var12 <= var9; var12 += 4) {
            var3[var12 + 0] = (byte)(var3[var12 + 0] + (var2[var12 + 0 - var6] & 255));
            var3[var12 + 1] = (byte)(var3[var12 + 1] + (var2[var12 + 1 - var6] & 255));
            var3[var12 + 2] = (byte)(var3[var12 + 2] + (var2[var12 + 2 - var6] & 255));
            var3[var12 + 3] = (byte)(var3[var12 + 3] + (var2[var12 + 3 - var6] & 255));
         }

         while (var12 < this.r) {
            var3[var12] = (byte)(var3[var12] + (var2[var12 - var6] & 255));
            var12++;
         }
      }
   }

   private void convertToCompact(int[] var1, byte[] var2) {
      int var3 = 0;

      for (int var5 = 0; var5 < this.R_BYTE; var5++) {
         for (int var6 = 0; var6 < 8 && var5 * 8 + var6 != this.r; var6++) {
            int var4 = var2[var5] >> var6 & 1;
            var1[var3] = var5 * 8 + var6 & -var4 | var1[var3] & ~(-var4);
            var3 = (var3 + var4) % this.hw;
         }
      }
   }

   private int[] getColumnFromCompactVersion(int[] var1) {
      int[] var2 = new int[this.hw];
      if (var1[0] == 0) {
         var2[0] = 0;

         for (int var3 = 1; var3 < this.hw; var3++) {
            var2[var3] = this.r - var1[this.hw - var3];
         }
      } else {
         for (int var4 = 0; var4 < this.hw; var4++) {
            var2[var4] = this.r - var1[this.hw - 1 - var4];
         }
      }

      return var2;
   }

   private void recomputeSyndrome(byte[] var1, int var2, int[] var3, int[] var4, boolean var5) {
      int var6 = var5 ? 1 : 0;
      if (var2 < this.r) {
         for (int var7 = 0; var7 < this.hw; var7++) {
            if (var3[var7] <= var2) {
               var1[var2 - var3[var7]] = (byte)(var1[var2 - var3[var7]] ^ var6);
            } else {
               var1[this.r + var2 - var3[var7]] = (byte)(var1[this.r + var2 - var3[var7]] ^ var6);
            }
         }
      } else {
         for (int var8 = 0; var8 < this.hw; var8++) {
            if (var4[var8] <= var2 - this.r) {
               var1[var2 - this.r - var4[var8]] = (byte)(var1[var2 - this.r - var4[var8]] ^ var6);
            } else {
               var1[this.r - var4[var8] + (var2 - this.r)] = (byte)(var1[this.r - var4[var8] + (var2 - this.r)] ^ var6);
            }
         }
      }
   }

   private void splitEBytes(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = this.r & 7;
      System.arraycopy(var1, 0, var2, 0, this.R_BYTE - 1);
      byte var5 = var1[this.R_BYTE - 1];
      byte var6 = (byte)(-1 << var4);
      var2[this.R_BYTE - 1] = (byte)(var5 & ~var6);
      byte var7 = (byte)(var5 & var6);

      for (int var8 = 0; var8 < this.R_BYTE; var8++) {
         byte var9 = var1[this.R_BYTE + var8];
         var3[var8] = (byte)(var9 << 8 - var4 | (var7 & 255) >>> var4);
         var7 = var9;
      }
   }

   private void updateNewErrorIndex(byte[] var1, int var2, boolean var3) {
      int var4 = var2;
      if (var2 != 0 && var2 != this.r) {
         if (var2 > this.r) {
            var4 = 2 * this.r - var2 + this.r;
         } else {
            var4 = this.r - var2;
         }
      }

      var1[var4] = (byte)(var1[var4] ^ (var3 ? 1 : 0));
   }
}
