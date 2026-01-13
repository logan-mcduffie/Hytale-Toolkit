package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.encoders.Hex;

public class ARIAEngine implements BlockCipher {
   private static final byte[][] C = new byte[][]{
      Hex.decodeStrict("517cc1b727220a94fe13abe8fa9a6ee0"),
      Hex.decodeStrict("6db14acc9e21c820ff28b1d5ef5de2b0"),
      Hex.decodeStrict("db92371d2126e9700324977504e8c90e")
   };
   private static final byte[] SB1_sbox = new byte[]{
      99,
      124,
      119,
      123,
      -14,
      107,
      111,
      -59,
      48,
      1,
      103,
      43,
      -2,
      -41,
      -85,
      118,
      -54,
      -126,
      -55,
      125,
      -6,
      89,
      71,
      -16,
      -83,
      -44,
      -94,
      -81,
      -100,
      -92,
      114,
      -64,
      -73,
      -3,
      -109,
      38,
      54,
      63,
      -9,
      -52,
      52,
      -91,
      -27,
      -15,
      113,
      -40,
      49,
      21,
      4,
      -57,
      35,
      -61,
      24,
      -106,
      5,
      -102,
      7,
      18,
      -128,
      -30,
      -21,
      39,
      -78,
      117,
      9,
      -125,
      44,
      26,
      27,
      110,
      90,
      -96,
      82,
      59,
      -42,
      -77,
      41,
      -29,
      47,
      -124,
      83,
      -47,
      0,
      -19,
      32,
      -4,
      -79,
      91,
      106,
      -53,
      -66,
      57,
      74,
      76,
      88,
      -49,
      -48,
      -17,
      -86,
      -5,
      67,
      77,
      51,
      -123,
      69,
      -7,
      2,
      127,
      80,
      60,
      -97,
      -88,
      81,
      -93,
      64,
      -113,
      -110,
      -99,
      56,
      -11,
      -68,
      -74,
      -38,
      33,
      16,
      -1,
      -13,
      -46,
      -51,
      12,
      19,
      -20,
      95,
      -105,
      68,
      23,
      -60,
      -89,
      126,
      61,
      100,
      93,
      25,
      115,
      96,
      -127,
      79,
      -36,
      34,
      42,
      -112,
      -120,
      70,
      -18,
      -72,
      20,
      -34,
      94,
      11,
      -37,
      -32,
      50,
      58,
      10,
      73,
      6,
      36,
      92,
      -62,
      -45,
      -84,
      98,
      -111,
      -107,
      -28,
      121,
      -25,
      -56,
      55,
      109,
      -115,
      -43,
      78,
      -87,
      108,
      86,
      -12,
      -22,
      101,
      122,
      -82,
      8,
      -70,
      120,
      37,
      46,
      28,
      -90,
      -76,
      -58,
      -24,
      -35,
      116,
      31,
      75,
      -67,
      -117,
      -118,
      112,
      62,
      -75,
      102,
      72,
      3,
      -10,
      14,
      97,
      53,
      87,
      -71,
      -122,
      -63,
      29,
      -98,
      -31,
      -8,
      -104,
      17,
      105,
      -39,
      -114,
      -108,
      -101,
      30,
      -121,
      -23,
      -50,
      85,
      40,
      -33,
      -116,
      -95,
      -119,
      13,
      -65,
      -26,
      66,
      104,
      65,
      -103,
      45,
      15,
      -80,
      84,
      -69,
      22
   };
   private static final byte[] SB2_sbox = new byte[]{
      -30,
      78,
      84,
      -4,
      -108,
      -62,
      74,
      -52,
      98,
      13,
      106,
      70,
      60,
      77,
      -117,
      -47,
      94,
      -6,
      100,
      -53,
      -76,
      -105,
      -66,
      43,
      -68,
      119,
      46,
      3,
      -45,
      25,
      89,
      -63,
      29,
      6,
      65,
      107,
      85,
      -16,
      -103,
      105,
      -22,
      -100,
      24,
      -82,
      99,
      -33,
      -25,
      -69,
      0,
      115,
      102,
      -5,
      -106,
      76,
      -123,
      -28,
      58,
      9,
      69,
      -86,
      15,
      -18,
      16,
      -21,
      45,
      127,
      -12,
      41,
      -84,
      -49,
      -83,
      -111,
      -115,
      120,
      -56,
      -107,
      -7,
      47,
      -50,
      -51,
      8,
      122,
      -120,
      56,
      92,
      -125,
      42,
      40,
      71,
      -37,
      -72,
      -57,
      -109,
      -92,
      18,
      83,
      -1,
      -121,
      14,
      49,
      54,
      33,
      88,
      72,
      1,
      -114,
      55,
      116,
      50,
      -54,
      -23,
      -79,
      -73,
      -85,
      12,
      -41,
      -60,
      86,
      66,
      38,
      7,
      -104,
      96,
      -39,
      -74,
      -71,
      17,
      64,
      -20,
      32,
      -116,
      -67,
      -96,
      -55,
      -124,
      4,
      73,
      35,
      -15,
      79,
      80,
      31,
      19,
      -36,
      -40,
      -64,
      -98,
      87,
      -29,
      -61,
      123,
      101,
      59,
      2,
      -113,
      62,
      -24,
      37,
      -110,
      -27,
      21,
      -35,
      -3,
      23,
      -87,
      -65,
      -44,
      -102,
      126,
      -59,
      57,
      103,
      -2,
      118,
      -99,
      67,
      -89,
      -31,
      -48,
      -11,
      104,
      -14,
      27,
      52,
      112,
      5,
      -93,
      -118,
      -43,
      121,
      -122,
      -88,
      48,
      -58,
      81,
      75,
      30,
      -90,
      39,
      -10,
      53,
      -46,
      110,
      36,
      22,
      -126,
      95,
      -38,
      -26,
      117,
      -94,
      -17,
      44,
      -78,
      28,
      -97,
      93,
      111,
      -128,
      10,
      114,
      68,
      -101,
      108,
      -112,
      11,
      91,
      51,
      125,
      90,
      82,
      -13,
      97,
      -95,
      -9,
      -80,
      -42,
      63,
      124,
      109,
      -19,
      20,
      -32,
      -91,
      61,
      34,
      -77,
      -8,
      -119,
      -34,
      113,
      26,
      -81,
      -70,
      -75,
      -127
   };
   private static final byte[] SB3_sbox = new byte[]{
      82,
      9,
      106,
      -43,
      48,
      54,
      -91,
      56,
      -65,
      64,
      -93,
      -98,
      -127,
      -13,
      -41,
      -5,
      124,
      -29,
      57,
      -126,
      -101,
      47,
      -1,
      -121,
      52,
      -114,
      67,
      68,
      -60,
      -34,
      -23,
      -53,
      84,
      123,
      -108,
      50,
      -90,
      -62,
      35,
      61,
      -18,
      76,
      -107,
      11,
      66,
      -6,
      -61,
      78,
      8,
      46,
      -95,
      102,
      40,
      -39,
      36,
      -78,
      118,
      91,
      -94,
      73,
      109,
      -117,
      -47,
      37,
      114,
      -8,
      -10,
      100,
      -122,
      104,
      -104,
      22,
      -44,
      -92,
      92,
      -52,
      93,
      101,
      -74,
      -110,
      108,
      112,
      72,
      80,
      -3,
      -19,
      -71,
      -38,
      94,
      21,
      70,
      87,
      -89,
      -115,
      -99,
      -124,
      -112,
      -40,
      -85,
      0,
      -116,
      -68,
      -45,
      10,
      -9,
      -28,
      88,
      5,
      -72,
      -77,
      69,
      6,
      -48,
      44,
      30,
      -113,
      -54,
      63,
      15,
      2,
      -63,
      -81,
      -67,
      3,
      1,
      19,
      -118,
      107,
      58,
      -111,
      17,
      65,
      79,
      103,
      -36,
      -22,
      -105,
      -14,
      -49,
      -50,
      -16,
      -76,
      -26,
      115,
      -106,
      -84,
      116,
      34,
      -25,
      -83,
      53,
      -123,
      -30,
      -7,
      55,
      -24,
      28,
      117,
      -33,
      110,
      71,
      -15,
      26,
      113,
      29,
      41,
      -59,
      -119,
      111,
      -73,
      98,
      14,
      -86,
      24,
      -66,
      27,
      -4,
      86,
      62,
      75,
      -58,
      -46,
      121,
      32,
      -102,
      -37,
      -64,
      -2,
      120,
      -51,
      90,
      -12,
      31,
      -35,
      -88,
      51,
      -120,
      7,
      -57,
      49,
      -79,
      18,
      16,
      89,
      39,
      -128,
      -20,
      95,
      96,
      81,
      127,
      -87,
      25,
      -75,
      74,
      13,
      45,
      -27,
      122,
      -97,
      -109,
      -55,
      -100,
      -17,
      -96,
      -32,
      59,
      77,
      -82,
      42,
      -11,
      -80,
      -56,
      -21,
      -69,
      60,
      -125,
      83,
      -103,
      97,
      23,
      43,
      4,
      126,
      -70,
      119,
      -42,
      38,
      -31,
      105,
      20,
      99,
      85,
      33,
      12,
      125
   };
   private static final byte[] SB4_sbox = new byte[]{
      48,
      104,
      -103,
      27,
      -121,
      -71,
      33,
      120,
      80,
      57,
      -37,
      -31,
      114,
      9,
      98,
      60,
      62,
      126,
      94,
      -114,
      -15,
      -96,
      -52,
      -93,
      42,
      29,
      -5,
      -74,
      -42,
      32,
      -60,
      -115,
      -127,
      101,
      -11,
      -119,
      -53,
      -99,
      119,
      -58,
      87,
      67,
      86,
      23,
      -44,
      64,
      26,
      77,
      -64,
      99,
      108,
      -29,
      -73,
      -56,
      100,
      106,
      83,
      -86,
      56,
      -104,
      12,
      -12,
      -101,
      -19,
      127,
      34,
      118,
      -81,
      -35,
      58,
      11,
      88,
      103,
      -120,
      6,
      -61,
      53,
      13,
      1,
      -117,
      -116,
      -62,
      -26,
      95,
      2,
      36,
      117,
      -109,
      102,
      30,
      -27,
      -30,
      84,
      -40,
      16,
      -50,
      122,
      -24,
      8,
      44,
      18,
      -105,
      50,
      -85,
      -76,
      39,
      10,
      35,
      -33,
      -17,
      -54,
      -39,
      -72,
      -6,
      -36,
      49,
      107,
      -47,
      -83,
      25,
      73,
      -67,
      81,
      -106,
      -18,
      -28,
      -88,
      65,
      -38,
      -1,
      -51,
      85,
      -122,
      54,
      -66,
      97,
      82,
      -8,
      -69,
      14,
      -126,
      72,
      105,
      -102,
      -32,
      71,
      -98,
      92,
      4,
      75,
      52,
      21,
      121,
      38,
      -89,
      -34,
      41,
      -82,
      -110,
      -41,
      -124,
      -23,
      -46,
      -70,
      93,
      -13,
      -59,
      -80,
      -65,
      -92,
      59,
      113,
      68,
      70,
      43,
      -4,
      -21,
      111,
      -43,
      -10,
      20,
      -2,
      124,
      112,
      90,
      125,
      -3,
      47,
      24,
      -125,
      22,
      -91,
      -111,
      31,
      5,
      -107,
      116,
      -87,
      -63,
      91,
      74,
      -123,
      109,
      19,
      7,
      79,
      78,
      69,
      -78,
      15,
      -55,
      28,
      -90,
      -68,
      -20,
      115,
      -112,
      123,
      -49,
      89,
      -113,
      -95,
      -7,
      45,
      -14,
      -79,
      0,
      -108,
      55,
      -97,
      -48,
      46,
      -100,
      110,
      40,
      63,
      -128,
      -16,
      61,
      -45,
      37,
      -118,
      -75,
      -25,
      66,
      -77,
      -57,
      -22,
      -9,
      76,
      17,
      51,
      3,
      -94,
      -84,
      96
   };
   protected static final int BLOCK_SIZE = 16;
   private byte[][] roundKeys;
   boolean forEncryption;

   public ARIAEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to ARIA init - " + var2.getClass().getName());
      } else {
         this.forEncryption = var1;
         this.roundKeys = keySchedule(var1, ((KeyParameter)var2).getKey());
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), var2, Utils.getPurpose(var1)));
      }
   }

   @Override
   public String getAlgorithmName() {
      return "ARIA";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.roundKeys == null) {
         throw new IllegalStateException("ARIA engine not initialised");
      } else if (var2 > var1.length - 16) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 > var3.length - 16) {
         throw new OutputLengthException("output buffer too short");
      } else {
         byte[] var5 = new byte[16];
         System.arraycopy(var1, var2, var5, 0, 16);
         int var6 = 0;
         int var7 = this.roundKeys.length - 3;

         while (var6 < var7) {
            FO(var5, this.roundKeys[var6++]);
            FE(var5, this.roundKeys[var6++]);
         }

         FO(var5, this.roundKeys[var6++]);
         xor(var5, this.roundKeys[var6++]);
         SL2(var5);
         xor(var5, this.roundKeys[var6]);
         System.arraycopy(var5, 0, var3, var4, 16);
         return 16;
      }
   }

   @Override
   public void reset() {
   }

   protected static void A(byte[] var0) {
      byte var1 = var0[0];
      byte var2 = var0[1];
      byte var3 = var0[2];
      byte var4 = var0[3];
      byte var5 = var0[4];
      byte var6 = var0[5];
      byte var7 = var0[6];
      byte var8 = var0[7];
      byte var9 = var0[8];
      byte var10 = var0[9];
      byte var11 = var0[10];
      byte var12 = var0[11];
      byte var13 = var0[12];
      byte var14 = var0[13];
      byte var15 = var0[14];
      byte var16 = var0[15];
      var0[0] = (byte)(var4 ^ var5 ^ var7 ^ var9 ^ var10 ^ var14 ^ var15);
      var0[1] = (byte)(var3 ^ var6 ^ var8 ^ var9 ^ var10 ^ var13 ^ var16);
      var0[2] = (byte)(var2 ^ var5 ^ var7 ^ var11 ^ var12 ^ var13 ^ var16);
      var0[3] = (byte)(var1 ^ var6 ^ var8 ^ var11 ^ var12 ^ var14 ^ var15);
      var0[4] = (byte)(var1 ^ var3 ^ var6 ^ var9 ^ var12 ^ var15 ^ var16);
      var0[5] = (byte)(var2 ^ var4 ^ var5 ^ var10 ^ var11 ^ var15 ^ var16);
      var0[6] = (byte)(var1 ^ var3 ^ var8 ^ var10 ^ var11 ^ var13 ^ var14);
      var0[7] = (byte)(var2 ^ var4 ^ var7 ^ var9 ^ var12 ^ var13 ^ var14);
      var0[8] = (byte)(var1 ^ var2 ^ var5 ^ var8 ^ var11 ^ var14 ^ var16);
      var0[9] = (byte)(var1 ^ var2 ^ var6 ^ var7 ^ var12 ^ var13 ^ var15);
      var0[10] = (byte)(var3 ^ var4 ^ var6 ^ var7 ^ var9 ^ var14 ^ var16);
      var0[11] = (byte)(var3 ^ var4 ^ var5 ^ var8 ^ var10 ^ var13 ^ var15);
      var0[12] = (byte)(var2 ^ var3 ^ var7 ^ var8 ^ var10 ^ var12 ^ var13);
      var0[13] = (byte)(var1 ^ var4 ^ var7 ^ var8 ^ var9 ^ var11 ^ var14);
      var0[14] = (byte)(var1 ^ var4 ^ var5 ^ var6 ^ var10 ^ var12 ^ var15);
      var0[15] = (byte)(var2 ^ var3 ^ var5 ^ var6 ^ var9 ^ var11 ^ var16);
   }

   protected static void FE(byte[] var0, byte[] var1) {
      xor(var0, var1);
      SL2(var0);
      A(var0);
   }

   protected static void FO(byte[] var0, byte[] var1) {
      xor(var0, var1);
      SL1(var0);
      A(var0);
   }

   protected static byte[][] keySchedule(boolean var0, byte[] var1) {
      int var2 = var1.length;
      if (var2 >= 16 && var2 <= 32 && (var2 & 7) == 0) {
         int var3 = (var2 >>> 3) - 2;
         byte[] var4 = C[var3];
         byte[] var5 = C[(var3 + 1) % 3];
         byte[] var6 = C[(var3 + 2) % 3];
         byte[] var7 = new byte[16];
         byte[] var8 = new byte[16];
         System.arraycopy(var1, 0, var7, 0, 16);
         System.arraycopy(var1, 16, var8, 0, var2 - 16);
         byte[] var9 = new byte[16];
         byte[] var10 = new byte[16];
         byte[] var11 = new byte[16];
         byte[] var12 = new byte[16];
         System.arraycopy(var7, 0, var9, 0, 16);
         System.arraycopy(var9, 0, var10, 0, 16);
         FO(var10, var4);
         xor(var10, var8);
         System.arraycopy(var10, 0, var11, 0, 16);
         FE(var11, var5);
         xor(var11, var9);
         System.arraycopy(var11, 0, var12, 0, 16);
         FO(var12, var6);
         xor(var12, var10);
         int var13 = 12 + var3 * 2;
         byte[][] var14 = new byte[var13 + 1][16];
         keyScheduleRound(var14[0], var9, var10, 19);
         keyScheduleRound(var14[1], var10, var11, 19);
         keyScheduleRound(var14[2], var11, var12, 19);
         keyScheduleRound(var14[3], var12, var9, 19);
         keyScheduleRound(var14[4], var9, var10, 31);
         keyScheduleRound(var14[5], var10, var11, 31);
         keyScheduleRound(var14[6], var11, var12, 31);
         keyScheduleRound(var14[7], var12, var9, 31);
         keyScheduleRound(var14[8], var9, var10, 67);
         keyScheduleRound(var14[9], var10, var11, 67);
         keyScheduleRound(var14[10], var11, var12, 67);
         keyScheduleRound(var14[11], var12, var9, 67);
         keyScheduleRound(var14[12], var9, var10, 97);
         if (var13 > 12) {
            keyScheduleRound(var14[13], var10, var11, 97);
            keyScheduleRound(var14[14], var11, var12, 97);
            if (var13 > 14) {
               keyScheduleRound(var14[15], var12, var9, 97);
               keyScheduleRound(var14[16], var9, var10, 109);
            }
         }

         if (!var0) {
            reverseKeys(var14);

            for (int var15 = 1; var15 < var13; var15++) {
               A(var14[var15]);
            }
         }

         return var14;
      } else {
         throw new IllegalArgumentException("Key length not 128/192/256 bits.");
      }
   }

   protected static void keyScheduleRound(byte[] var0, byte[] var1, byte[] var2, int var3) {
      int var4 = var3 >>> 3;
      int var5 = var3 & 7;
      int var6 = 8 - var5;
      int var7 = var2[15 - var4] & 255;

      for (int var8 = 0; var8 < 16; var8++) {
         int var9 = var2[var8 - var4 & 15] & 255;
         int var10 = var7 << var6 | var9 >>> var5;
         var10 ^= var1[var8] & 255;
         var0[var8] = (byte)var10;
         var7 = var9;
      }
   }

   protected static void reverseKeys(byte[][] var0) {
      int var1 = var0.length;
      int var2 = var1 / 2;
      int var3 = var1 - 1;

      for (int var4 = 0; var4 < var2; var4++) {
         byte[] var5 = var0[var4];
         var0[var4] = var0[var3 - var4];
         var0[var3 - var4] = var5;
      }
   }

   protected static byte SB1(byte var0) {
      return SB1_sbox[var0 & 255];
   }

   protected static byte SB2(byte var0) {
      return SB2_sbox[var0 & 255];
   }

   protected static byte SB3(byte var0) {
      return SB3_sbox[var0 & 255];
   }

   protected static byte SB4(byte var0) {
      return SB4_sbox[var0 & 255];
   }

   protected static void SL1(byte[] var0) {
      var0[0] = SB1(var0[0]);
      var0[1] = SB2(var0[1]);
      var0[2] = SB3(var0[2]);
      var0[3] = SB4(var0[3]);
      var0[4] = SB1(var0[4]);
      var0[5] = SB2(var0[5]);
      var0[6] = SB3(var0[6]);
      var0[7] = SB4(var0[7]);
      var0[8] = SB1(var0[8]);
      var0[9] = SB2(var0[9]);
      var0[10] = SB3(var0[10]);
      var0[11] = SB4(var0[11]);
      var0[12] = SB1(var0[12]);
      var0[13] = SB2(var0[13]);
      var0[14] = SB3(var0[14]);
      var0[15] = SB4(var0[15]);
   }

   protected static void SL2(byte[] var0) {
      var0[0] = SB3(var0[0]);
      var0[1] = SB4(var0[1]);
      var0[2] = SB1(var0[2]);
      var0[3] = SB2(var0[3]);
      var0[4] = SB3(var0[4]);
      var0[5] = SB4(var0[5]);
      var0[6] = SB1(var0[6]);
      var0[7] = SB2(var0[7]);
      var0[8] = SB3(var0[8]);
      var0[9] = SB4(var0[9]);
      var0[10] = SB1(var0[10]);
      var0[11] = SB2(var0[11]);
      var0[12] = SB3(var0[12]);
      var0[13] = SB4(var0[13]);
      var0[14] = SB1(var0[14]);
      var0[15] = SB2(var0[15]);
   }

   protected static void xor(byte[] var0, byte[] var1) {
      Bytes.xorTo(16, var1, var0);
   }

   private int bitsOfSecurity() {
      return this.roundKeys.length > 13 ? (this.roundKeys.length > 15 ? 256 : 192) : 128;
   }
}
