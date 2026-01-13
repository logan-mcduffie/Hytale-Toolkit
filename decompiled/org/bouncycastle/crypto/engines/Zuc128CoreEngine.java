package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Memoable;

public class Zuc128CoreEngine implements StreamCipher, Memoable {
   private static final byte[] S0 = new byte[]{
      62,
      114,
      91,
      71,
      -54,
      -32,
      0,
      51,
      4,
      -47,
      84,
      -104,
      9,
      -71,
      109,
      -53,
      123,
      27,
      -7,
      50,
      -81,
      -99,
      106,
      -91,
      -72,
      45,
      -4,
      29,
      8,
      83,
      3,
      -112,
      77,
      78,
      -124,
      -103,
      -28,
      -50,
      -39,
      -111,
      -35,
      -74,
      -123,
      72,
      -117,
      41,
      110,
      -84,
      -51,
      -63,
      -8,
      30,
      115,
      67,
      105,
      -58,
      -75,
      -67,
      -3,
      57,
      99,
      32,
      -44,
      56,
      118,
      125,
      -78,
      -89,
      -49,
      -19,
      87,
      -59,
      -13,
      44,
      -69,
      20,
      33,
      6,
      85,
      -101,
      -29,
      -17,
      94,
      49,
      79,
      127,
      90,
      -92,
      13,
      -126,
      81,
      73,
      95,
      -70,
      88,
      28,
      74,
      22,
      -43,
      23,
      -88,
      -110,
      36,
      31,
      -116,
      -1,
      -40,
      -82,
      46,
      1,
      -45,
      -83,
      59,
      75,
      -38,
      70,
      -21,
      -55,
      -34,
      -102,
      -113,
      -121,
      -41,
      58,
      -128,
      111,
      47,
      -56,
      -79,
      -76,
      55,
      -9,
      10,
      34,
      19,
      40,
      124,
      -52,
      60,
      -119,
      -57,
      -61,
      -106,
      86,
      7,
      -65,
      126,
      -16,
      11,
      43,
      -105,
      82,
      53,
      65,
      121,
      97,
      -90,
      76,
      16,
      -2,
      -68,
      38,
      -107,
      -120,
      -118,
      -80,
      -93,
      -5,
      -64,
      24,
      -108,
      -14,
      -31,
      -27,
      -23,
      93,
      -48,
      -36,
      17,
      102,
      100,
      92,
      -20,
      89,
      66,
      117,
      18,
      -11,
      116,
      -100,
      -86,
      35,
      14,
      -122,
      -85,
      -66,
      42,
      2,
      -25,
      103,
      -26,
      68,
      -94,
      108,
      -62,
      -109,
      -97,
      -15,
      -10,
      -6,
      54,
      -46,
      80,
      104,
      -98,
      98,
      113,
      21,
      61,
      -42,
      64,
      -60,
      -30,
      15,
      -114,
      -125,
      119,
      107,
      37,
      5,
      63,
      12,
      48,
      -22,
      112,
      -73,
      -95,
      -24,
      -87,
      101,
      -115,
      39,
      26,
      -37,
      -127,
      -77,
      -96,
      -12,
      69,
      122,
      25,
      -33,
      -18,
      120,
      52,
      96
   };
   private static final byte[] S1 = new byte[]{
      85,
      -62,
      99,
      113,
      59,
      -56,
      71,
      -122,
      -97,
      60,
      -38,
      91,
      41,
      -86,
      -3,
      119,
      -116,
      -59,
      -108,
      12,
      -90,
      26,
      19,
      0,
      -29,
      -88,
      22,
      114,
      64,
      -7,
      -8,
      66,
      68,
      38,
      104,
      -106,
      -127,
      -39,
      69,
      62,
      16,
      118,
      -58,
      -89,
      -117,
      57,
      67,
      -31,
      58,
      -75,
      86,
      42,
      -64,
      109,
      -77,
      5,
      34,
      102,
      -65,
      -36,
      11,
      -6,
      98,
      72,
      -35,
      32,
      17,
      6,
      54,
      -55,
      -63,
      -49,
      -10,
      39,
      82,
      -69,
      105,
      -11,
      -44,
      -121,
      127,
      -124,
      76,
      -46,
      -100,
      87,
      -92,
      -68,
      79,
      -102,
      -33,
      -2,
      -42,
      -115,
      122,
      -21,
      43,
      83,
      -40,
      92,
      -95,
      20,
      23,
      -5,
      35,
      -43,
      125,
      48,
      103,
      115,
      8,
      9,
      -18,
      -73,
      112,
      63,
      97,
      -78,
      25,
      -114,
      78,
      -27,
      75,
      -109,
      -113,
      93,
      -37,
      -87,
      -83,
      -15,
      -82,
      46,
      -53,
      13,
      -4,
      -12,
      45,
      70,
      110,
      29,
      -105,
      -24,
      -47,
      -23,
      77,
      55,
      -91,
      117,
      94,
      -125,
      -98,
      -85,
      -126,
      -99,
      -71,
      28,
      -32,
      -51,
      73,
      -119,
      1,
      -74,
      -67,
      88,
      36,
      -94,
      95,
      56,
      120,
      -103,
      21,
      -112,
      80,
      -72,
      -107,
      -28,
      -48,
      -111,
      -57,
      -50,
      -19,
      15,
      -76,
      111,
      -96,
      -52,
      -16,
      2,
      74,
      121,
      -61,
      -34,
      -93,
      -17,
      -22,
      81,
      -26,
      107,
      24,
      -20,
      27,
      44,
      -128,
      -9,
      116,
      -25,
      -1,
      33,
      90,
      106,
      84,
      30,
      65,
      49,
      -110,
      53,
      -60,
      51,
      7,
      10,
      -70,
      126,
      14,
      52,
      -120,
      -79,
      -104,
      124,
      -13,
      61,
      96,
      108,
      123,
      -54,
      -45,
      31,
      50,
      101,
      4,
      40,
      100,
      -66,
      -123,
      -101,
      47,
      89,
      -118,
      -41,
      -80,
      37,
      -84,
      -81,
      18,
      3,
      -30,
      -14
   };
   private static final short[] EK_d = new short[]{17623, 9916, 25195, 4958, 22409, 13794, 28981, 2479, 19832, 12051, 27588, 6897, 24102, 15437, 30874, 18348};
   private final int[] LFSR = new int[16];
   private final int[] F = new int[2];
   private final int[] BRC = new int[4];
   private int theIndex;
   private final byte[] keyStream = new byte[4];
   private int theIterations;
   private Zuc128CoreEngine theResetState;

   protected Zuc128CoreEngine() {
   }

   protected Zuc128CoreEngine(Zuc128CoreEngine var1) {
      this.reset(var1);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      CipherParameters var3 = var2;
      byte[] var4 = null;
      byte[] var5 = null;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var6 = (ParametersWithIV)var2;
         var5 = var6.getIV();
         var3 = var6.getParameters();
      }

      if (var3 instanceof KeyParameter) {
         KeyParameter var7 = (KeyParameter)var3;
         var4 = var7.getKey();
      }

      this.theIndex = 0;
      this.theIterations = 0;
      this.setKeyAndIV(var4, var5);
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(this.getAlgorithmName(), var4.length * 8, var2, var1 ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION)
      );
      this.theResetState = (Zuc128CoreEngine)this.copy();
   }

   protected int getMaxIterations() {
      return 2047;
   }

   @Override
   public String getAlgorithmName() {
      return "Zuc-128";
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (this.theResetState == null) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            var4[var6 + var5] = this.returnByte(var1[var6 + var2]);
         }

         return var3;
      }
   }

   @Override
   public void reset() {
      if (this.theResetState != null) {
         this.reset(this.theResetState);
      }
   }

   @Override
   public byte returnByte(byte var1) {
      if (this.theIndex == 0) {
         this.makeKeyStream();
      }

      byte var2 = (byte)(this.keyStream[this.theIndex] ^ var1);
      this.theIndex = (this.theIndex + 1) % 4;
      return var2;
   }

   public static void encode32be(int var0, byte[] var1, int var2) {
      var1[var2] = (byte)(var0 >> 24);
      var1[var2 + 1] = (byte)(var0 >> 16);
      var1[var2 + 2] = (byte)(var0 >> 8);
      var1[var2 + 3] = (byte)var0;
   }

   private int AddM(int var1, int var2) {
      int var3 = var1 + var2;
      return (var3 & 2147483647) + (var3 >>> 31);
   }

   private static int MulByPow2(int var0, int var1) {
      return (var0 << var1 | var0 >>> 31 - var1) & 2147483647;
   }

   private void LFSRWithInitialisationMode(int var1) {
      int var2 = this.LFSR[0];
      int var3 = MulByPow2(this.LFSR[0], 8);
      var2 = this.AddM(var2, var3);
      var3 = MulByPow2(this.LFSR[4], 20);
      var2 = this.AddM(var2, var3);
      var3 = MulByPow2(this.LFSR[10], 21);
      var2 = this.AddM(var2, var3);
      var3 = MulByPow2(this.LFSR[13], 17);
      var2 = this.AddM(var2, var3);
      var3 = MulByPow2(this.LFSR[15], 15);
      var2 = this.AddM(var2, var3);
      var2 = this.AddM(var2, var1);
      this.LFSR[0] = this.LFSR[1];
      this.LFSR[1] = this.LFSR[2];
      this.LFSR[2] = this.LFSR[3];
      this.LFSR[3] = this.LFSR[4];
      this.LFSR[4] = this.LFSR[5];
      this.LFSR[5] = this.LFSR[6];
      this.LFSR[6] = this.LFSR[7];
      this.LFSR[7] = this.LFSR[8];
      this.LFSR[8] = this.LFSR[9];
      this.LFSR[9] = this.LFSR[10];
      this.LFSR[10] = this.LFSR[11];
      this.LFSR[11] = this.LFSR[12];
      this.LFSR[12] = this.LFSR[13];
      this.LFSR[13] = this.LFSR[14];
      this.LFSR[14] = this.LFSR[15];
      this.LFSR[15] = var2;
   }

   private void LFSRWithWorkMode() {
      int var1 = this.LFSR[0];
      int var2 = MulByPow2(this.LFSR[0], 8);
      var1 = this.AddM(var1, var2);
      var2 = MulByPow2(this.LFSR[4], 20);
      var1 = this.AddM(var1, var2);
      var2 = MulByPow2(this.LFSR[10], 21);
      var1 = this.AddM(var1, var2);
      var2 = MulByPow2(this.LFSR[13], 17);
      var1 = this.AddM(var1, var2);
      var2 = MulByPow2(this.LFSR[15], 15);
      var1 = this.AddM(var1, var2);
      this.LFSR[0] = this.LFSR[1];
      this.LFSR[1] = this.LFSR[2];
      this.LFSR[2] = this.LFSR[3];
      this.LFSR[3] = this.LFSR[4];
      this.LFSR[4] = this.LFSR[5];
      this.LFSR[5] = this.LFSR[6];
      this.LFSR[6] = this.LFSR[7];
      this.LFSR[7] = this.LFSR[8];
      this.LFSR[8] = this.LFSR[9];
      this.LFSR[9] = this.LFSR[10];
      this.LFSR[10] = this.LFSR[11];
      this.LFSR[11] = this.LFSR[12];
      this.LFSR[12] = this.LFSR[13];
      this.LFSR[13] = this.LFSR[14];
      this.LFSR[14] = this.LFSR[15];
      this.LFSR[15] = var1;
   }

   private void BitReorganization() {
      this.BRC[0] = (this.LFSR[15] & 2147450880) << 1 | this.LFSR[14] & 65535;
      this.BRC[1] = (this.LFSR[11] & 65535) << 16 | this.LFSR[9] >>> 15;
      this.BRC[2] = (this.LFSR[7] & 65535) << 16 | this.LFSR[5] >>> 15;
      this.BRC[3] = (this.LFSR[2] & 65535) << 16 | this.LFSR[0] >>> 15;
   }

   static int ROT(int var0, int var1) {
      return var0 << var1 | var0 >>> 32 - var1;
   }

   private static int L1(int var0) {
      return var0 ^ ROT(var0, 2) ^ ROT(var0, 10) ^ ROT(var0, 18) ^ ROT(var0, 24);
   }

   private static int L2(int var0) {
      return var0 ^ ROT(var0, 8) ^ ROT(var0, 14) ^ ROT(var0, 22) ^ ROT(var0, 30);
   }

   private static int MAKEU32(byte var0, byte var1, byte var2, byte var3) {
      return (var0 & 0xFF) << 24 | (var1 & 0xFF) << 16 | (var2 & 0xFF) << 8 | var3 & 0xFF;
   }

   int F() {
      int var1 = (this.BRC[0] ^ this.F[0]) + this.F[1];
      int var2 = this.F[0] + this.BRC[1];
      int var3 = this.F[1] ^ this.BRC[2];
      int var4 = L1(var2 << 16 | var3 >>> 16);
      int var5 = L2(var3 << 16 | var2 >>> 16);
      this.F[0] = MAKEU32(S0[var4 >>> 24], S1[var4 >>> 16 & 0xFF], S0[var4 >>> 8 & 0xFF], S1[var4 & 0xFF]);
      this.F[1] = MAKEU32(S0[var5 >>> 24], S1[var5 >>> 16 & 0xFF], S0[var5 >>> 8 & 0xFF], S1[var5 & 0xFF]);
      return var1;
   }

   private static int MAKEU31(byte var0, short var1, byte var2) {
      return (var0 & 0xFF) << 23 | (var1 & 65535) << 8 | var2 & 0xFF;
   }

   protected void setKeyAndIV(int[] var1, byte[] var2, byte[] var3) {
      if (var2 == null || var2.length != 16) {
         throw new IllegalArgumentException("A key of 16 bytes is needed");
      } else if (var3 != null && var3.length == 16) {
         this.LFSR[0] = MAKEU31(var2[0], EK_d[0], var3[0]);
         this.LFSR[1] = MAKEU31(var2[1], EK_d[1], var3[1]);
         this.LFSR[2] = MAKEU31(var2[2], EK_d[2], var3[2]);
         this.LFSR[3] = MAKEU31(var2[3], EK_d[3], var3[3]);
         this.LFSR[4] = MAKEU31(var2[4], EK_d[4], var3[4]);
         this.LFSR[5] = MAKEU31(var2[5], EK_d[5], var3[5]);
         this.LFSR[6] = MAKEU31(var2[6], EK_d[6], var3[6]);
         this.LFSR[7] = MAKEU31(var2[7], EK_d[7], var3[7]);
         this.LFSR[8] = MAKEU31(var2[8], EK_d[8], var3[8]);
         this.LFSR[9] = MAKEU31(var2[9], EK_d[9], var3[9]);
         this.LFSR[10] = MAKEU31(var2[10], EK_d[10], var3[10]);
         this.LFSR[11] = MAKEU31(var2[11], EK_d[11], var3[11]);
         this.LFSR[12] = MAKEU31(var2[12], EK_d[12], var3[12]);
         this.LFSR[13] = MAKEU31(var2[13], EK_d[13], var3[13]);
         this.LFSR[14] = MAKEU31(var2[14], EK_d[14], var3[14]);
         this.LFSR[15] = MAKEU31(var2[15], EK_d[15], var3[15]);
      } else {
         throw new IllegalArgumentException("An IV of 16 bytes is needed");
      }
   }

   private void setKeyAndIV(byte[] var1, byte[] var2) {
      this.setKeyAndIV(this.LFSR, var1, var2);
      this.F[0] = 0;
      this.F[1] = 0;

      for (int var3 = 32; var3 > 0; var3--) {
         this.BitReorganization();
         int var4 = this.F();
         this.LFSRWithInitialisationMode(var4 >>> 1);
      }

      this.BitReorganization();
      this.F();
      this.LFSRWithWorkMode();
   }

   private void makeKeyStream() {
      encode32be(this.makeKeyStreamWord(), this.keyStream, 0);
   }

   protected int makeKeyStreamWord() {
      if (this.theIterations++ >= this.getMaxIterations()) {
         throw new IllegalStateException("Too much data processed by singleKey/IV");
      } else {
         this.BitReorganization();
         int var1 = this.F() ^ this.BRC[3];
         this.LFSRWithWorkMode();
         return var1;
      }
   }

   @Override
   public Memoable copy() {
      return new Zuc128CoreEngine(this);
   }

   @Override
   public void reset(Memoable var1) {
      Zuc128CoreEngine var2 = (Zuc128CoreEngine)var1;
      System.arraycopy(var2.LFSR, 0, this.LFSR, 0, this.LFSR.length);
      System.arraycopy(var2.F, 0, this.F, 0, this.F.length);
      System.arraycopy(var2.BRC, 0, this.BRC, 0, this.BRC.length);
      System.arraycopy(var2.keyStream, 0, this.keyStream, 0, this.keyStream.length);
      this.theIndex = var2.theIndex;
      this.theIterations = var2.theIterations;
      this.theResetState = var2;
   }
}
