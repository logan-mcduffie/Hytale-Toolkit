package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class GOST3412_2015Engine implements BlockCipher {
   private static final byte[] PI = new byte[]{
      -4,
      -18,
      -35,
      17,
      -49,
      110,
      49,
      22,
      -5,
      -60,
      -6,
      -38,
      35,
      -59,
      4,
      77,
      -23,
      119,
      -16,
      -37,
      -109,
      46,
      -103,
      -70,
      23,
      54,
      -15,
      -69,
      20,
      -51,
      95,
      -63,
      -7,
      24,
      101,
      90,
      -30,
      92,
      -17,
      33,
      -127,
      28,
      60,
      66,
      -117,
      1,
      -114,
      79,
      5,
      -124,
      2,
      -82,
      -29,
      106,
      -113,
      -96,
      6,
      11,
      -19,
      -104,
      127,
      -44,
      -45,
      31,
      -21,
      52,
      44,
      81,
      -22,
      -56,
      72,
      -85,
      -14,
      42,
      104,
      -94,
      -3,
      58,
      -50,
      -52,
      -75,
      112,
      14,
      86,
      8,
      12,
      118,
      18,
      -65,
      114,
      19,
      71,
      -100,
      -73,
      93,
      -121,
      21,
      -95,
      -106,
      41,
      16,
      123,
      -102,
      -57,
      -13,
      -111,
      120,
      111,
      -99,
      -98,
      -78,
      -79,
      50,
      117,
      25,
      61,
      -1,
      53,
      -118,
      126,
      109,
      84,
      -58,
      -128,
      -61,
      -67,
      13,
      87,
      -33,
      -11,
      36,
      -87,
      62,
      -88,
      67,
      -55,
      -41,
      121,
      -42,
      -10,
      124,
      34,
      -71,
      3,
      -32,
      15,
      -20,
      -34,
      122,
      -108,
      -80,
      -68,
      -36,
      -24,
      40,
      80,
      78,
      51,
      10,
      74,
      -89,
      -105,
      96,
      115,
      30,
      0,
      98,
      68,
      26,
      -72,
      56,
      -126,
      100,
      -97,
      38,
      65,
      -83,
      69,
      70,
      -110,
      39,
      94,
      85,
      47,
      -116,
      -93,
      -91,
      125,
      105,
      -43,
      -107,
      59,
      7,
      88,
      -77,
      64,
      -122,
      -84,
      29,
      -9,
      48,
      55,
      107,
      -28,
      -120,
      -39,
      -25,
      -119,
      -31,
      27,
      -125,
      73,
      76,
      63,
      -8,
      -2,
      -115,
      83,
      -86,
      -112,
      -54,
      -40,
      -123,
      97,
      32,
      113,
      103,
      -92,
      45,
      43,
      9,
      91,
      -53,
      -101,
      37,
      -48,
      -66,
      -27,
      108,
      82,
      89,
      -90,
      116,
      -46,
      -26,
      -12,
      -76,
      -64,
      -47,
      102,
      -81,
      -62,
      57,
      75,
      99,
      -74
   };
   private static final byte[] inversePI = new byte[]{
      -91,
      45,
      50,
      -113,
      14,
      48,
      56,
      -64,
      84,
      -26,
      -98,
      57,
      85,
      126,
      82,
      -111,
      100,
      3,
      87,
      90,
      28,
      96,
      7,
      24,
      33,
      114,
      -88,
      -47,
      41,
      -58,
      -92,
      63,
      -32,
      39,
      -115,
      12,
      -126,
      -22,
      -82,
      -76,
      -102,
      99,
      73,
      -27,
      66,
      -28,
      21,
      -73,
      -56,
      6,
      112,
      -99,
      65,
      117,
      25,
      -55,
      -86,
      -4,
      77,
      -65,
      42,
      115,
      -124,
      -43,
      -61,
      -81,
      43,
      -122,
      -89,
      -79,
      -78,
      91,
      70,
      -45,
      -97,
      -3,
      -44,
      15,
      -100,
      47,
      -101,
      67,
      -17,
      -39,
      121,
      -74,
      83,
      127,
      -63,
      -16,
      35,
      -25,
      37,
      94,
      -75,
      30,
      -94,
      -33,
      -90,
      -2,
      -84,
      34,
      -7,
      -30,
      74,
      -68,
      53,
      -54,
      -18,
      120,
      5,
      107,
      81,
      -31,
      89,
      -93,
      -14,
      113,
      86,
      17,
      106,
      -119,
      -108,
      101,
      -116,
      -69,
      119,
      60,
      123,
      40,
      -85,
      -46,
      49,
      -34,
      -60,
      95,
      -52,
      -49,
      118,
      44,
      -72,
      -40,
      46,
      54,
      -37,
      105,
      -77,
      20,
      -107,
      -66,
      98,
      -95,
      59,
      22,
      102,
      -23,
      92,
      108,
      109,
      -83,
      55,
      97,
      75,
      -71,
      -29,
      -70,
      -15,
      -96,
      -123,
      -125,
      -38,
      71,
      -59,
      -80,
      51,
      -6,
      -106,
      111,
      110,
      -62,
      -10,
      80,
      -1,
      93,
      -87,
      -114,
      23,
      27,
      -105,
      125,
      -20,
      88,
      -9,
      31,
      -5,
      124,
      9,
      13,
      122,
      103,
      69,
      -121,
      -36,
      -24,
      79,
      29,
      78,
      4,
      -21,
      -8,
      -13,
      62,
      61,
      -67,
      -118,
      -120,
      -35,
      -51,
      11,
      19,
      -104,
      2,
      -109,
      -128,
      -112,
      -48,
      36,
      52,
      -53,
      -19,
      -12,
      -50,
      -103,
      16,
      68,
      64,
      -110,
      58,
      1,
      38,
      18,
      26,
      72,
      104,
      -11,
      -127,
      -117,
      -57,
      -42,
      32,
      10,
      8,
      0,
      76,
      -41,
      116
   };
   private final byte[] lFactors = new byte[]{-108, 32, -123, 16, -62, -64, 1, -5, 1, -64, -62, 16, -123, 32, -108, 1};
   protected static final int BLOCK_SIZE = 16;
   private int KEY_LENGTH = 32;
   private int SUB_LENGTH = this.KEY_LENGTH / 2;
   private byte[][] subKeys = null;
   private boolean forEncryption;
   private byte[][] _gf_mul = init_gf256_mul_table();

   private static byte[][] init_gf256_mul_table() {
      byte[][] var0 = new byte[256][];

      for (int var1 = 0; var1 < 256; var1++) {
         var0[var1] = new byte[256];

         for (int var2 = 0; var2 < 256; var2++) {
            var0[var1][var2] = kuz_mul_gf256_slow((byte)var1, (byte)var2);
         }
      }

      return var0;
   }

   private static byte kuz_mul_gf256_slow(byte var0, byte var1) {
      byte var2 = 0;

      for (byte var3 = 0; var3 < 8 && var0 != 0 && var1 != 0; var3++) {
         if ((var1 & 1) != 0) {
            var2 ^= var0;
         }

         byte var4 = (byte)(var0 & 128);
         var0 = (byte)(var0 << 1);
         if (var4 != 0) {
            var0 = (byte)(var0 ^ 195);
         }

         var1 = (byte)(var1 >> 1);
      }

      return var2;
   }

   @Override
   public String getAlgorithmName() {
      return "GOST3412_2015";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (var2 instanceof KeyParameter) {
         this.forEncryption = var1;
         this.generateSubKeys(((KeyParameter)var2).getKey());
      } else if (var2 != null) {
         throw new IllegalArgumentException("invalid parameter passed to GOST3412_2015 init - " + var2.getClass().getName());
      }
   }

   private void generateSubKeys(byte[] var1) {
      if (var1.length != this.KEY_LENGTH) {
         throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
      } else {
         this.subKeys = new byte[10][];

         for (int var2 = 0; var2 < 10; var2++) {
            this.subKeys[var2] = new byte[this.SUB_LENGTH];
         }

         byte[] var7 = new byte[this.SUB_LENGTH];
         byte[] var3 = new byte[this.SUB_LENGTH];

         for (int var4 = 0; var4 < this.SUB_LENGTH; var4++) {
            this.subKeys[0][var4] = var7[var4] = var1[var4];
            this.subKeys[1][var4] = var3[var4] = var1[var4 + this.SUB_LENGTH];
         }

         byte[] var8 = new byte[this.SUB_LENGTH];

         for (int var5 = 1; var5 < 5; var5++) {
            for (int var6 = 1; var6 <= 8; var6++) {
               this.C(var8, 8 * (var5 - 1) + var6);
               this.F(var8, var7, var3);
            }

            System.arraycopy(var7, 0, this.subKeys[2 * var5], 0, this.SUB_LENGTH);
            System.arraycopy(var3, 0, this.subKeys[2 * var5 + 1], 0, this.SUB_LENGTH);
         }
      }
   }

   private void C(byte[] var1, int var2) {
      Arrays.clear(var1);
      var1[15] = (byte)var2;
      this.L(var1);
   }

   private void F(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = this.LSX(var1, var2);
      this.X(var4, var3);
      System.arraycopy(var2, 0, var3, 0, this.SUB_LENGTH);
      System.arraycopy(var4, 0, var2, 0, this.SUB_LENGTH);
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.subKeys == null) {
         throw new IllegalStateException("GOST3412_2015 engine not initialised");
      } else if (var2 + 16 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 16 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.GOST3412_2015Func(var1, var2, var3, var4);
         return 16;
      }
   }

   private void GOST3412_2015Func(byte[] var1, int var2, byte[] var3, int var4) {
      byte[] var5 = new byte[16];
      System.arraycopy(var1, var2, var5, 0, 16);
      if (this.forEncryption) {
         for (int var6 = 0; var6 < 9; var6++) {
            byte[] var7 = this.LSX(this.subKeys[var6], var5);
            var5 = Arrays.copyOf(var7, 16);
         }

         this.X(var5, this.subKeys[9]);
      } else {
         for (int var8 = 9; var8 > 0; var8--) {
            byte[] var9 = this.XSL(this.subKeys[var8], var5);
            var5 = Arrays.copyOf(var9, 16);
         }

         this.X(var5, this.subKeys[0]);
      }

      System.arraycopy(var5, 0, var3, var4, 16);
   }

   private byte[] LSX(byte[] var1, byte[] var2) {
      byte[] var3 = Arrays.copyOf(var1, var1.length);
      this.X(var3, var2);
      this.S(var3);
      this.L(var3);
      return var3;
   }

   private byte[] XSL(byte[] var1, byte[] var2) {
      byte[] var3 = Arrays.copyOf(var1, var1.length);
      this.X(var3, var2);
      this.inverseL(var3);
      this.inverseS(var3);
      return var3;
   }

   private void X(byte[] var1, byte[] var2) {
      for (int var3 = 0; var3 < var1.length; var3++) {
         var1[var3] ^= var2[var3];
      }
   }

   private void S(byte[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = PI[this.unsignedByte(var1[var2])];
      }
   }

   private void inverseS(byte[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = inversePI[this.unsignedByte(var1[var2])];
      }
   }

   private int unsignedByte(byte var1) {
      return var1 & 0xFF;
   }

   private void L(byte[] var1) {
      for (int var2 = 0; var2 < 16; var2++) {
         this.R(var1);
      }
   }

   private void inverseL(byte[] var1) {
      for (int var2 = 0; var2 < 16; var2++) {
         this.inverseR(var1);
      }
   }

   private void R(byte[] var1) {
      byte var2 = this.l(var1);
      System.arraycopy(var1, 0, var1, 1, 15);
      var1[0] = var2;
   }

   private void inverseR(byte[] var1) {
      byte[] var2 = new byte[16];
      System.arraycopy(var1, 1, var2, 0, 15);
      var2[15] = var1[0];
      byte var3 = this.l(var2);
      System.arraycopy(var1, 1, var1, 0, 15);
      var1[15] = var3;
   }

   private byte l(byte[] var1) {
      byte var2 = var1[15];

      for (int var3 = 14; var3 >= 0; var3--) {
         var2 ^= this._gf_mul[this.unsignedByte(var1[var3])][this.unsignedByte(this.lFactors[var3])];
      }

      return var2;
   }

   @Override
   public void reset() {
   }
}
