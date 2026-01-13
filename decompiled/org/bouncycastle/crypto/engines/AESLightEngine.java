package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class AESLightEngine implements BlockCipher {
   private static final byte[] S = new byte[]{
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
   private static final byte[] Si = new byte[]{
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
   private static final int[] rcon = new int[]{
      1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145
   };
   private static final int m1 = -2139062144;
   private static final int m2 = 2139062143;
   private static final int m3 = 27;
   private static final int m4 = -1061109568;
   private static final int m5 = 1061109567;
   private int ROUNDS;
   private int[][] WorkingKey = null;
   private boolean forEncryption;
   private static final int BLOCK_SIZE = 16;

   private static int shift(int var0, int var1) {
      return var0 >>> var1 | var0 << -var1;
   }

   private static int FFmulX(int var0) {
      return (var0 & 2139062143) << 1 ^ ((var0 & -2139062144) >>> 7) * 27;
   }

   private static int FFmulX2(int var0) {
      int var1 = (var0 & 1061109567) << 2;
      int var2 = var0 & -1061109568;
      var2 ^= var2 >>> 1;
      return var1 ^ var2 >>> 2 ^ var2 >>> 5;
   }

   private static int mcol(int var0) {
      int var1 = shift(var0, 8);
      int var2 = var0 ^ var1;
      return shift(var2, 16) ^ var1 ^ FFmulX(var2);
   }

   private static int inv_mcol(int var0) {
      int var2 = var0 ^ shift(var0, 8);
      int var1 = var0 ^ FFmulX(var2);
      var2 ^= FFmulX2(var1);
      return var1 ^ var2 ^ shift(var2, 16);
   }

   private static int subWord(int var0) {
      return S[var0 & 0xFF] & 0xFF | (S[var0 >> 8 & 0xFF] & 0xFF) << 8 | (S[var0 >> 16 & 0xFF] & 0xFF) << 16 | S[var0 >> 24 & 0xFF] << 24;
   }

   private int[][] generateWorkingKey(byte[] var1, boolean var2) {
      int var3 = var1.length;
      if (var3 >= 16 && var3 <= 32 && (var3 & 7) == 0) {
         int[][] var5;
         int var4 = var3 >>> 2;
         this.ROUNDS = var4 + 6;
         var5 = new int[this.ROUNDS + 1][4];
         label58:
         switch (var4) {
            case 4:
               int var19 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var19;
               int var23 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var23;
               int var27 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var27;
               int var30 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var30;

               for (int var33 = 1; var33 <= 10; var33++) {
                  int var36 = subWord(shift(var30, 8)) ^ rcon[var33 - 1];
                  var19 ^= var36;
                  var5[var33][0] = var19;
                  var23 ^= var19;
                  var5[var33][1] = var23;
                  var27 ^= var23;
                  var5[var33][2] = var27;
                  var30 ^= var27;
                  var5[var33][3] = var30;
               }
               break;
            case 5:
            case 7:
            default:
               throw new IllegalStateException("Should never get here");
            case 6:
               int var17 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var17;
               int var21 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var21;
               int var25 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var25;
               int var28 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var28;
               int var31 = Pack.littleEndianToInt(var1, 16);
               int var34 = Pack.littleEndianToInt(var1, 20);
               byte var37 = 1;
               int var38 = 1;

               while (true) {
                  var5[var37][0] = var31;
                  var5[var37][1] = var34;
                  int var41 = subWord(shift(var34, 8)) ^ var38;
                  var38 <<= 1;
                  int var18 = var17 ^ var41;
                  var5[var37][2] = var18;
                  int var22 = var21 ^ var18;
                  var5[var37][3] = var22;
                  int var26 = var25 ^ var22;
                  var5[var37 + 1][0] = var26;
                  int var29 = var28 ^ var26;
                  var5[var37 + 1][1] = var29;
                  var31 ^= var29;
                  var5[var37 + 1][2] = var31;
                  var34 ^= var31;
                  var5[var37 + 1][3] = var34;
                  var41 = subWord(shift(var34, 8)) ^ var38;
                  var38 <<= 1;
                  var17 = var18 ^ var41;
                  var5[var37 + 2][0] = var17;
                  var21 = var22 ^ var17;
                  var5[var37 + 2][1] = var21;
                  var25 = var26 ^ var21;
                  var5[var37 + 2][2] = var25;
                  var28 = var29 ^ var25;
                  var5[var37 + 2][3] = var28;
                  var37 += 3;
                  if (var37 >= 13) {
                     break label58;
                  }

                  var31 ^= var28;
                  var34 ^= var31;
               }
            case 8:
               int var6 = Pack.littleEndianToInt(var1, 0);
               var5[0][0] = var6;
               int var7 = Pack.littleEndianToInt(var1, 4);
               var5[0][1] = var7;
               int var8 = Pack.littleEndianToInt(var1, 8);
               var5[0][2] = var8;
               int var9 = Pack.littleEndianToInt(var1, 12);
               var5[0][3] = var9;
               int var10 = Pack.littleEndianToInt(var1, 16);
               var5[1][0] = var10;
               int var11 = Pack.littleEndianToInt(var1, 20);
               var5[1][1] = var11;
               int var12 = Pack.littleEndianToInt(var1, 24);
               var5[1][2] = var12;
               int var13 = Pack.littleEndianToInt(var1, 28);
               var5[1][3] = var13;
               int var14 = 2;
               byte var15 = 1;

               while (true) {
                  int var16 = subWord(shift(var13, 8)) ^ var15;
                  var15 <<= 1;
                  var6 ^= var16;
                  var5[var14][0] = var6;
                  var7 ^= var6;
                  var5[var14][1] = var7;
                  var8 ^= var7;
                  var5[var14][2] = var8;
                  var9 ^= var8;
                  var5[var14][3] = var9;
                  if (++var14 >= 15) {
                     break;
                  }

                  var16 = subWord(var9);
                  var10 ^= var16;
                  var5[var14][0] = var10;
                  var11 ^= var10;
                  var5[var14][1] = var11;
                  var12 ^= var11;
                  var5[var14][2] = var12;
                  var13 ^= var12;
                  var5[var14][3] = var13;
                  var14++;
               }
         }

         if (!var2) {
            for (int var20 = 1; var20 < this.ROUNDS; var20++) {
               for (int var24 = 0; var24 < 4; var24++) {
                  var5[var20][var24] = inv_mcol(var5[var20][var24]);
               }
            }
         }

         return var5;
      } else {
         throw new IllegalArgumentException("Key length not 128/192/256 bits.");
      }
   }

   public AESLightEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         this.WorkingKey = this.generateWorkingKey(((KeyParameter)var2).getKey(), var1);
         this.forEncryption = var1;
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), var2, Utils.getPurpose(var1)));
      } else {
         throw new IllegalArgumentException("invalid parameter passed to AES init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "AES";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.WorkingKey == null) {
         throw new IllegalStateException("AES engine not initialised");
      } else if (var2 > var1.length - 16) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 > var3.length - 16) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.forEncryption) {
            this.encryptBlock(var1, var2, var3, var4, this.WorkingKey);
         } else {
            this.decryptBlock(var1, var2, var3, var4, this.WorkingKey);
         }

         return 16;
      }
   }

   @Override
   public void reset() {
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4, int[][] var5) {
      int var6 = Pack.littleEndianToInt(var1, var2 + 0);
      int var7 = Pack.littleEndianToInt(var1, var2 + 4);
      int var8 = Pack.littleEndianToInt(var1, var2 + 8);
      int var9 = Pack.littleEndianToInt(var1, var2 + 12);
      int var10 = var6 ^ var5[0][0];
      int var11 = var7 ^ var5[0][1];
      int var12 = var8 ^ var5[0][2];
      int var13 = 1;
      int var17 = var9 ^ var5[0][3];

      while (var13 < this.ROUNDS - 1) {
         int var14 = mcol(S[var10 & 0xFF] & 255 ^ (S[var11 >> 8 & 0xFF] & 255) << 8 ^ (S[var12 >> 16 & 0xFF] & 255) << 16 ^ S[var17 >> 24 & 0xFF] << 24)
            ^ var5[var13][0];
         int var15 = mcol(S[var11 & 0xFF] & 255 ^ (S[var12 >> 8 & 0xFF] & 255) << 8 ^ (S[var17 >> 16 & 0xFF] & 255) << 16 ^ S[var10 >> 24 & 0xFF] << 24)
            ^ var5[var13][1];
         int var16 = mcol(S[var12 & 0xFF] & 255 ^ (S[var17 >> 8 & 0xFF] & 255) << 8 ^ (S[var10 >> 16 & 0xFF] & 255) << 16 ^ S[var11 >> 24 & 0xFF] << 24)
            ^ var5[var13][2];
         var17 = mcol(S[var17 & 0xFF] & 255 ^ (S[var10 >> 8 & 0xFF] & 255) << 8 ^ (S[var11 >> 16 & 0xFF] & 255) << 16 ^ S[var12 >> 24 & 0xFF] << 24)
            ^ var5[var13++][3];
         var10 = mcol(S[var14 & 0xFF] & 255 ^ (S[var15 >> 8 & 0xFF] & 255) << 8 ^ (S[var16 >> 16 & 0xFF] & 255) << 16 ^ S[var17 >> 24 & 0xFF] << 24)
            ^ var5[var13][0];
         var11 = mcol(S[var15 & 0xFF] & 255 ^ (S[var16 >> 8 & 0xFF] & 255) << 8 ^ (S[var17 >> 16 & 0xFF] & 255) << 16 ^ S[var14 >> 24 & 0xFF] << 24)
            ^ var5[var13][1];
         var12 = mcol(S[var16 & 0xFF] & 255 ^ (S[var17 >> 8 & 0xFF] & 255) << 8 ^ (S[var14 >> 16 & 0xFF] & 255) << 16 ^ S[var15 >> 24 & 0xFF] << 24)
            ^ var5[var13][2];
         var17 = mcol(S[var17 & 0xFF] & 255 ^ (S[var14 >> 8 & 0xFF] & 255) << 8 ^ (S[var15 >> 16 & 0xFF] & 255) << 16 ^ S[var16 >> 24 & 0xFF] << 24)
            ^ var5[var13++][3];
      }

      int var24 = mcol(S[var10 & 0xFF] & 255 ^ (S[var11 >> 8 & 0xFF] & 255) << 8 ^ (S[var12 >> 16 & 0xFF] & 255) << 16 ^ S[var17 >> 24 & 0xFF] << 24)
         ^ var5[var13][0];
      int var25 = mcol(S[var11 & 0xFF] & 255 ^ (S[var12 >> 8 & 0xFF] & 255) << 8 ^ (S[var17 >> 16 & 0xFF] & 255) << 16 ^ S[var10 >> 24 & 0xFF] << 24)
         ^ var5[var13][1];
      int var26 = mcol(S[var12 & 0xFF] & 255 ^ (S[var17 >> 8 & 0xFF] & 255) << 8 ^ (S[var10 >> 16 & 0xFF] & 255) << 16 ^ S[var11 >> 24 & 0xFF] << 24)
         ^ var5[var13][2];
      var17 = mcol(S[var17 & 0xFF] & 255 ^ (S[var10 >> 8 & 0xFF] & 255) << 8 ^ (S[var11 >> 16 & 0xFF] & 255) << 16 ^ S[var12 >> 24 & 0xFF] << 24)
         ^ var5[var13++][3];
      var6 = S[var24 & 0xFF] & 255 ^ (S[var25 >> 8 & 0xFF] & 255) << 8 ^ (S[var26 >> 16 & 0xFF] & 255) << 16 ^ S[var17 >> 24 & 0xFF] << 24 ^ var5[var13][0];
      var7 = S[var25 & 0xFF] & 255 ^ (S[var26 >> 8 & 0xFF] & 255) << 8 ^ (S[var17 >> 16 & 0xFF] & 255) << 16 ^ S[var24 >> 24 & 0xFF] << 24 ^ var5[var13][1];
      var8 = S[var26 & 0xFF] & 255 ^ (S[var17 >> 8 & 0xFF] & 255) << 8 ^ (S[var24 >> 16 & 0xFF] & 255) << 16 ^ S[var25 >> 24 & 0xFF] << 24 ^ var5[var13][2];
      var9 = S[var17 & 0xFF] & 255 ^ (S[var24 >> 8 & 0xFF] & 255) << 8 ^ (S[var25 >> 16 & 0xFF] & 255) << 16 ^ S[var26 >> 24 & 0xFF] << 24 ^ var5[var13][3];
      Pack.intToLittleEndian(var6, var3, var4 + 0);
      Pack.intToLittleEndian(var7, var3, var4 + 4);
      Pack.intToLittleEndian(var8, var3, var4 + 8);
      Pack.intToLittleEndian(var9, var3, var4 + 12);
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4, int[][] var5) {
      int var6 = Pack.littleEndianToInt(var1, var2 + 0);
      int var7 = Pack.littleEndianToInt(var1, var2 + 4);
      int var8 = Pack.littleEndianToInt(var1, var2 + 8);
      int var9 = Pack.littleEndianToInt(var1, var2 + 12);
      int var10 = var6 ^ var5[this.ROUNDS][0];
      int var11 = var7 ^ var5[this.ROUNDS][1];
      int var12 = var8 ^ var5[this.ROUNDS][2];
      int var13 = this.ROUNDS - 1;
      int var17 = var9 ^ var5[this.ROUNDS][3];

      while (var13 > 1) {
         int var14 = inv_mcol(Si[var10 & 0xFF] & 255 ^ (Si[var17 >> 8 & 0xFF] & 255) << 8 ^ (Si[var12 >> 16 & 0xFF] & 255) << 16 ^ Si[var11 >> 24 & 0xFF] << 24)
            ^ var5[var13][0];
         int var15 = inv_mcol(Si[var11 & 0xFF] & 255 ^ (Si[var10 >> 8 & 0xFF] & 255) << 8 ^ (Si[var17 >> 16 & 0xFF] & 255) << 16 ^ Si[var12 >> 24 & 0xFF] << 24)
            ^ var5[var13][1];
         int var16 = inv_mcol(Si[var12 & 0xFF] & 255 ^ (Si[var11 >> 8 & 0xFF] & 255) << 8 ^ (Si[var10 >> 16 & 0xFF] & 255) << 16 ^ Si[var17 >> 24 & 0xFF] << 24)
            ^ var5[var13][2];
         var17 = inv_mcol(Si[var17 & 0xFF] & 255 ^ (Si[var12 >> 8 & 0xFF] & 255) << 8 ^ (Si[var11 >> 16 & 0xFF] & 255) << 16 ^ Si[var10 >> 24 & 0xFF] << 24)
            ^ var5[var13--][3];
         var10 = inv_mcol(Si[var14 & 0xFF] & 255 ^ (Si[var17 >> 8 & 0xFF] & 255) << 8 ^ (Si[var16 >> 16 & 0xFF] & 255) << 16 ^ Si[var15 >> 24 & 0xFF] << 24)
            ^ var5[var13][0];
         var11 = inv_mcol(Si[var15 & 0xFF] & 255 ^ (Si[var14 >> 8 & 0xFF] & 255) << 8 ^ (Si[var17 >> 16 & 0xFF] & 255) << 16 ^ Si[var16 >> 24 & 0xFF] << 24)
            ^ var5[var13][1];
         var12 = inv_mcol(Si[var16 & 0xFF] & 255 ^ (Si[var15 >> 8 & 0xFF] & 255) << 8 ^ (Si[var14 >> 16 & 0xFF] & 255) << 16 ^ Si[var17 >> 24 & 0xFF] << 24)
            ^ var5[var13][2];
         var17 = inv_mcol(Si[var17 & 0xFF] & 255 ^ (Si[var16 >> 8 & 0xFF] & 255) << 8 ^ (Si[var15 >> 16 & 0xFF] & 255) << 16 ^ Si[var14 >> 24 & 0xFF] << 24)
            ^ var5[var13--][3];
      }

      int var23 = inv_mcol(Si[var10 & 0xFF] & 255 ^ (Si[var17 >> 8 & 0xFF] & 255) << 8 ^ (Si[var12 >> 16 & 0xFF] & 255) << 16 ^ Si[var11 >> 24 & 0xFF] << 24)
         ^ var5[var13][0];
      int var24 = inv_mcol(Si[var11 & 0xFF] & 255 ^ (Si[var10 >> 8 & 0xFF] & 255) << 8 ^ (Si[var17 >> 16 & 0xFF] & 255) << 16 ^ Si[var12 >> 24 & 0xFF] << 24)
         ^ var5[var13][1];
      int var25 = inv_mcol(Si[var12 & 0xFF] & 255 ^ (Si[var11 >> 8 & 0xFF] & 255) << 8 ^ (Si[var10 >> 16 & 0xFF] & 255) << 16 ^ Si[var17 >> 24 & 0xFF] << 24)
         ^ var5[var13][2];
      var17 = inv_mcol(Si[var17 & 0xFF] & 255 ^ (Si[var12 >> 8 & 0xFF] & 255) << 8 ^ (Si[var11 >> 16 & 0xFF] & 255) << 16 ^ Si[var10 >> 24 & 0xFF] << 24)
         ^ var5[var13][3];
      var6 = Si[var23 & 0xFF] & 255 ^ (Si[var17 >> 8 & 0xFF] & 255) << 8 ^ (Si[var25 >> 16 & 0xFF] & 255) << 16 ^ Si[var24 >> 24 & 0xFF] << 24 ^ var5[0][0];
      var7 = Si[var24 & 0xFF] & 255 ^ (Si[var23 >> 8 & 0xFF] & 255) << 8 ^ (Si[var17 >> 16 & 0xFF] & 255) << 16 ^ Si[var25 >> 24 & 0xFF] << 24 ^ var5[0][1];
      var8 = Si[var25 & 0xFF] & 255 ^ (Si[var24 >> 8 & 0xFF] & 255) << 8 ^ (Si[var23 >> 16 & 0xFF] & 255) << 16 ^ Si[var17 >> 24 & 0xFF] << 24 ^ var5[0][2];
      var9 = Si[var17 & 0xFF] & 255 ^ (Si[var25 >> 8 & 0xFF] & 255) << 8 ^ (Si[var24 >> 16 & 0xFF] & 255) << 16 ^ Si[var23 >> 24 & 0xFF] << 24 ^ var5[0][3];
      Pack.intToLittleEndian(var6, var3, var4 + 0);
      Pack.intToLittleEndian(var7, var3, var4 + 4);
      Pack.intToLittleEndian(var8, var3, var4 + 8);
      Pack.intToLittleEndian(var9, var3, var4 + 12);
   }

   private int bitsOfSecurity() {
      return this.WorkingKey == null ? 256 : this.WorkingKey.length - 7 << 5;
   }
}
