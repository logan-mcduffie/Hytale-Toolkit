package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC2Parameters;

public class RC2Engine implements BlockCipher {
   private static byte[] piTable = new byte[]{
      -39,
      120,
      -7,
      -60,
      25,
      -35,
      -75,
      -19,
      40,
      -23,
      -3,
      121,
      74,
      -96,
      -40,
      -99,
      -58,
      126,
      55,
      -125,
      43,
      118,
      83,
      -114,
      98,
      76,
      100,
      -120,
      68,
      -117,
      -5,
      -94,
      23,
      -102,
      89,
      -11,
      -121,
      -77,
      79,
      19,
      97,
      69,
      109,
      -115,
      9,
      -127,
      125,
      50,
      -67,
      -113,
      64,
      -21,
      -122,
      -73,
      123,
      11,
      -16,
      -107,
      33,
      34,
      92,
      107,
      78,
      -126,
      84,
      -42,
      101,
      -109,
      -50,
      96,
      -78,
      28,
      115,
      86,
      -64,
      20,
      -89,
      -116,
      -15,
      -36,
      18,
      117,
      -54,
      31,
      59,
      -66,
      -28,
      -47,
      66,
      61,
      -44,
      48,
      -93,
      60,
      -74,
      38,
      111,
      -65,
      14,
      -38,
      70,
      105,
      7,
      87,
      39,
      -14,
      29,
      -101,
      -68,
      -108,
      67,
      3,
      -8,
      17,
      -57,
      -10,
      -112,
      -17,
      62,
      -25,
      6,
      -61,
      -43,
      47,
      -56,
      102,
      30,
      -41,
      8,
      -24,
      -22,
      -34,
      -128,
      82,
      -18,
      -9,
      -124,
      -86,
      114,
      -84,
      53,
      77,
      106,
      42,
      -106,
      26,
      -46,
      113,
      90,
      21,
      73,
      116,
      75,
      -97,
      -48,
      94,
      4,
      24,
      -92,
      -20,
      -62,
      -32,
      65,
      110,
      15,
      81,
      -53,
      -52,
      36,
      -111,
      -81,
      80,
      -95,
      -12,
      112,
      57,
      -103,
      124,
      58,
      -123,
      35,
      -72,
      -76,
      122,
      -4,
      2,
      54,
      91,
      37,
      85,
      -105,
      49,
      45,
      93,
      -6,
      -104,
      -29,
      -118,
      -110,
      -82,
      5,
      -33,
      41,
      16,
      103,
      108,
      -70,
      -55,
      -45,
      0,
      -26,
      -49,
      -31,
      -98,
      -88,
      44,
      99,
      22,
      1,
      63,
      88,
      -30,
      -119,
      -87,
      13,
      56,
      52,
      27,
      -85,
      51,
      -1,
      -80,
      -69,
      72,
      12,
      95,
      -71,
      -79,
      -51,
      46,
      -59,
      -13,
      -37,
      71,
      -27,
      -91,
      -100,
      119,
      10,
      -90,
      32,
      104,
      -2,
      127,
      -63,
      -83
   };
   private static final int BLOCK_SIZE = 8;
   private int[] workingKey;
   private boolean encrypting;

   private int[] generateWorkingKey(byte[] var1, int var2) {
      int[] var4 = new int[128];

      for (int var5 = 0; var5 != var1.length; var5++) {
         var4[var5] = var1[var5] & 255;
      }

      int var9 = var1.length;
      if (var9 < 128) {
         int var6 = 0;
         int var3 = var4[var9 - 1];

         do {
            var3 = piTable[var3 + var4[var6++] & 0xFF] & 255;
            var4[var9++] = var3;
         } while (var9 < 128);
      }

      var9 = var2 + 7 >> 3;
      int var8 = piTable[var4[128 - var9] & 255 >> (7 & -var2)] & 255;
      var4[128 - var9] = var8;

      for (int var11 = 128 - var9 - 1; var11 >= 0; var11--) {
         var8 = piTable[var8 ^ var4[var11 + var9]] & 255;
         var4[var11] = var8;
      }

      int[] var12 = new int[64];

      for (int var7 = 0; var7 != var12.length; var7++) {
         var12[var7] = var4[2 * var7] + (var4[2 * var7 + 1] << 8);
      }

      return var12;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.encrypting = var1;
      byte[] var3;
      if (var2 instanceof RC2Parameters) {
         RC2Parameters var4 = (RC2Parameters)var2;
         this.workingKey = this.generateWorkingKey(var4.getKey(), var4.getEffectiveKeyBits());
         var3 = var4.getKey();
      } else {
         if (!(var2 instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to RC2 init - " + var2.getClass().getName());
         }

         var3 = ((KeyParameter)var2).getKey();
         this.workingKey = this.generateWorkingKey(var3, var3.length * 8);
      }

      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var3.length * 8, var2, Utils.getPurpose(var1)));
   }

   @Override
   public void reset() {
   }

   @Override
   public String getAlgorithmName() {
      return "RC2";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public final int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.workingKey == null) {
         throw new IllegalStateException("RC2 engine not initialised");
      } else if (var2 + 8 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 8 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.encrypting) {
            this.encryptBlock(var1, var2, var3, var4);
         } else {
            this.decryptBlock(var1, var2, var3, var4);
         }

         return 8;
      }
   }

   private int rotateWordLeft(int var1, int var2) {
      var1 &= 65535;
      return var1 << var2 | var1 >> 16 - var2;
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = ((var1[var2 + 7] & 255) << 8) + (var1[var2 + 6] & 255);
      int var6 = ((var1[var2 + 5] & 255) << 8) + (var1[var2 + 4] & 255);
      int var7 = ((var1[var2 + 3] & 255) << 8) + (var1[var2 + 2] & 255);
      int var8 = ((var1[var2 + 1] & 255) << 8) + (var1[var2 + 0] & 255);

      for (byte var9 = 0; var9 <= 16; var9 += 4) {
         var8 = this.rotateWordLeft(var8 + (var7 & ~var5) + (var6 & var5) + this.workingKey[var9], 1);
         var7 = this.rotateWordLeft(var7 + (var6 & ~var8) + (var5 & var8) + this.workingKey[var9 + 1], 2);
         var6 = this.rotateWordLeft(var6 + (var5 & ~var7) + (var8 & var7) + this.workingKey[var9 + 2], 3);
         var5 = this.rotateWordLeft(var5 + (var8 & ~var6) + (var7 & var6) + this.workingKey[var9 + 3], 5);
      }

      var8 += this.workingKey[var5 & 63];
      var7 += this.workingKey[var8 & 63];
      var6 += this.workingKey[var7 & 63];
      var5 += this.workingKey[var6 & 63];

      for (byte var18 = 20; var18 <= 40; var18 += 4) {
         var8 = this.rotateWordLeft(var8 + (var7 & ~var5) + (var6 & var5) + this.workingKey[var18], 1);
         var7 = this.rotateWordLeft(var7 + (var6 & ~var8) + (var5 & var8) + this.workingKey[var18 + 1], 2);
         var6 = this.rotateWordLeft(var6 + (var5 & ~var7) + (var8 & var7) + this.workingKey[var18 + 2], 3);
         var5 = this.rotateWordLeft(var5 + (var8 & ~var6) + (var7 & var6) + this.workingKey[var18 + 3], 5);
      }

      var8 += this.workingKey[var5 & 63];
      var7 += this.workingKey[var8 & 63];
      var6 += this.workingKey[var7 & 63];
      var5 += this.workingKey[var6 & 63];

      for (byte var19 = 44; var19 < 64; var19 += 4) {
         var8 = this.rotateWordLeft(var8 + (var7 & ~var5) + (var6 & var5) + this.workingKey[var19], 1);
         var7 = this.rotateWordLeft(var7 + (var6 & ~var8) + (var5 & var8) + this.workingKey[var19 + 1], 2);
         var6 = this.rotateWordLeft(var6 + (var5 & ~var7) + (var8 & var7) + this.workingKey[var19 + 2], 3);
         var5 = this.rotateWordLeft(var5 + (var8 & ~var6) + (var7 & var6) + this.workingKey[var19 + 3], 5);
      }

      var3[var4 + 0] = (byte)var8;
      var3[var4 + 1] = (byte)(var8 >> 8);
      var3[var4 + 2] = (byte)var7;
      var3[var4 + 3] = (byte)(var7 >> 8);
      var3[var4 + 4] = (byte)var6;
      var3[var4 + 5] = (byte)(var6 >> 8);
      var3[var4 + 6] = (byte)var5;
      var3[var4 + 7] = (byte)(var5 >> 8);
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = ((var1[var2 + 7] & 255) << 8) + (var1[var2 + 6] & 255);
      int var6 = ((var1[var2 + 5] & 255) << 8) + (var1[var2 + 4] & 255);
      int var7 = ((var1[var2 + 3] & 255) << 8) + (var1[var2 + 2] & 255);
      int var8 = ((var1[var2 + 1] & 255) << 8) + (var1[var2 + 0] & 255);

      for (byte var9 = 60; var9 >= 44; var9 -= 4) {
         var5 = this.rotateWordLeft(var5, 11) - ((var8 & ~var6) + (var7 & var6) + this.workingKey[var9 + 3]);
         var6 = this.rotateWordLeft(var6, 13) - ((var5 & ~var7) + (var8 & var7) + this.workingKey[var9 + 2]);
         var7 = this.rotateWordLeft(var7, 14) - ((var6 & ~var8) + (var5 & var8) + this.workingKey[var9 + 1]);
         var8 = this.rotateWordLeft(var8, 15) - ((var7 & ~var5) + (var6 & var5) + this.workingKey[var9]);
      }

      var5 -= this.workingKey[var6 & 63];
      var6 -= this.workingKey[var7 & 63];
      var7 -= this.workingKey[var8 & 63];
      var8 -= this.workingKey[var5 & 63];

      for (byte var18 = 40; var18 >= 20; var18 -= 4) {
         var5 = this.rotateWordLeft(var5, 11) - ((var8 & ~var6) + (var7 & var6) + this.workingKey[var18 + 3]);
         var6 = this.rotateWordLeft(var6, 13) - ((var5 & ~var7) + (var8 & var7) + this.workingKey[var18 + 2]);
         var7 = this.rotateWordLeft(var7, 14) - ((var6 & ~var8) + (var5 & var8) + this.workingKey[var18 + 1]);
         var8 = this.rotateWordLeft(var8, 15) - ((var7 & ~var5) + (var6 & var5) + this.workingKey[var18]);
      }

      var5 -= this.workingKey[var6 & 63];
      var6 -= this.workingKey[var7 & 63];
      var7 -= this.workingKey[var8 & 63];
      var8 -= this.workingKey[var5 & 63];

      for (byte var19 = 16; var19 >= 0; var19 -= 4) {
         var5 = this.rotateWordLeft(var5, 11) - ((var8 & ~var6) + (var7 & var6) + this.workingKey[var19 + 3]);
         var6 = this.rotateWordLeft(var6, 13) - ((var5 & ~var7) + (var8 & var7) + this.workingKey[var19 + 2]);
         var7 = this.rotateWordLeft(var7, 14) - ((var6 & ~var8) + (var5 & var8) + this.workingKey[var19 + 1]);
         var8 = this.rotateWordLeft(var8, 15) - ((var7 & ~var5) + (var6 & var5) + this.workingKey[var19]);
      }

      var3[var4 + 0] = (byte)var8;
      var3[var4 + 1] = (byte)(var8 >> 8);
      var3[var4 + 2] = (byte)var7;
      var3[var4 + 3] = (byte)(var7 >> 8);
      var3[var4 + 4] = (byte)var6;
      var3[var4 + 5] = (byte)(var6 >> 8);
      var3[var4 + 6] = (byte)var5;
      var3[var4 + 7] = (byte)(var5 >> 8);
   }
}
