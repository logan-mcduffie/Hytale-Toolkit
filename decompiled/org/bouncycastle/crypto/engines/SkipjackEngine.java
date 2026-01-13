package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class SkipjackEngine implements BlockCipher {
   static final int BLOCK_SIZE = 8;
   static short[] ftable = new short[]{
      163,
      215,
      9,
      131,
      248,
      72,
      246,
      244,
      179,
      33,
      21,
      120,
      153,
      177,
      175,
      249,
      231,
      45,
      77,
      138,
      206,
      76,
      202,
      46,
      82,
      149,
      217,
      30,
      78,
      56,
      68,
      40,
      10,
      223,
      2,
      160,
      23,
      241,
      96,
      104,
      18,
      183,
      122,
      195,
      233,
      250,
      61,
      83,
      150,
      132,
      107,
      186,
      242,
      99,
      154,
      25,
      124,
      174,
      229,
      245,
      247,
      22,
      106,
      162,
      57,
      182,
      123,
      15,
      193,
      147,
      129,
      27,
      238,
      180,
      26,
      234,
      208,
      145,
      47,
      184,
      85,
      185,
      218,
      133,
      63,
      65,
      191,
      224,
      90,
      88,
      128,
      95,
      102,
      11,
      216,
      144,
      53,
      213,
      192,
      167,
      51,
      6,
      101,
      105,
      69,
      0,
      148,
      86,
      109,
      152,
      155,
      118,
      151,
      252,
      178,
      194,
      176,
      254,
      219,
      32,
      225,
      235,
      214,
      228,
      221,
      71,
      74,
      29,
      66,
      237,
      158,
      110,
      73,
      60,
      205,
      67,
      39,
      210,
      7,
      212,
      222,
      199,
      103,
      24,
      137,
      203,
      48,
      31,
      141,
      198,
      143,
      170,
      200,
      116,
      220,
      201,
      93,
      92,
      49,
      164,
      112,
      136,
      97,
      44,
      159,
      13,
      43,
      135,
      80,
      130,
      84,
      100,
      38,
      125,
      3,
      64,
      52,
      75,
      28,
      115,
      209,
      196,
      253,
      59,
      204,
      251,
      127,
      171,
      230,
      62,
      91,
      165,
      173,
      4,
      35,
      156,
      20,
      81,
      34,
      240,
      41,
      121,
      113,
      126,
      255,
      140,
      14,
      226,
      12,
      239,
      188,
      114,
      117,
      111,
      55,
      161,
      236,
      211,
      142,
      98,
      139,
      134,
      16,
      232,
      8,
      119,
      17,
      190,
      146,
      79,
      36,
      197,
      50,
      54,
      157,
      207,
      243,
      166,
      187,
      172,
      94,
      108,
      169,
      19,
      87,
      37,
      181,
      227,
      189,
      168,
      58,
      1,
      5,
      89,
      42,
      70
   };
   private int[] key0;
   private int[] key1;
   private int[] key2;
   private int[] key3;
   private boolean encrypting;

   public SkipjackEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 80));
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to SKIPJACK init - " + var2.getClass().getName());
      } else {
         byte[] var3 = ((KeyParameter)var2).getKey();
         this.encrypting = var1;
         this.key0 = new int[32];
         this.key1 = new int[32];
         this.key2 = new int[32];
         this.key3 = new int[32];

         for (int var4 = 0; var4 < 32; var4++) {
            this.key0[var4] = var3[var4 * 4 % 10] & 255;
            this.key1[var4] = var3[(var4 * 4 + 1) % 10] & 255;
            this.key2[var4] = var3[(var4 * 4 + 2) % 10] & 255;
            this.key3[var4] = var3[(var4 * 4 + 3) % 10] & 255;
         }

         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 80, var2, this.getPurpose()));
      }
   }

   @Override
   public String getAlgorithmName() {
      return "SKIPJACK";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.key1 == null) {
         throw new IllegalStateException("SKIPJACK engine not initialised");
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

   @Override
   public void reset() {
   }

   private int g(int var1, int var2) {
      int var3 = var2 >> 8 & 0xFF;
      int var4 = var2 & 0xFF;
      int var5 = ftable[var4 ^ this.key0[var1]] ^ var3;
      int var6 = ftable[var5 ^ this.key1[var1]] ^ var4;
      int var7 = ftable[var6 ^ this.key2[var1]] ^ var5;
      int var8 = ftable[var7 ^ this.key3[var1]] ^ var6;
      return (var7 << 8) + var8;
   }

   public int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = (var1[var2 + 0] << 8) + (var1[var2 + 1] & 255);
      int var6 = (var1[var2 + 2] << 8) + (var1[var2 + 3] & 255);
      int var7 = (var1[var2 + 4] << 8) + (var1[var2 + 5] & 255);
      int var8 = (var1[var2 + 6] << 8) + (var1[var2 + 7] & 255);
      int var9 = 0;

      for (int var10 = 0; var10 < 2; var10++) {
         for (int var11 = 0; var11 < 8; var11++) {
            int var12 = var8;
            var8 = var7;
            var7 = var6;
            var6 = this.g(var9, var5);
            var5 = var6 ^ var12 ^ var9 + 1;
            var9++;
         }

         for (int var13 = 0; var13 < 8; var13++) {
            int var14 = var8;
            var8 = var7;
            var7 = var5 ^ var6 ^ var9 + 1;
            var6 = this.g(var9, var5);
            var5 = var14;
            var9++;
         }
      }

      var3[var4 + 0] = (byte)(var5 >> 8);
      var3[var4 + 1] = (byte)var5;
      var3[var4 + 2] = (byte)(var6 >> 8);
      var3[var4 + 3] = (byte)var6;
      var3[var4 + 4] = (byte)(var7 >> 8);
      var3[var4 + 5] = (byte)var7;
      var3[var4 + 6] = (byte)(var8 >> 8);
      var3[var4 + 7] = (byte)var8;
      return 8;
   }

   private int h(int var1, int var2) {
      int var3 = var2 & 0xFF;
      int var4 = var2 >> 8 & 0xFF;
      int var5 = ftable[var4 ^ this.key3[var1]] ^ var3;
      int var6 = ftable[var5 ^ this.key2[var1]] ^ var4;
      int var7 = ftable[var6 ^ this.key1[var1]] ^ var5;
      int var8 = ftable[var7 ^ this.key0[var1]] ^ var6;
      return (var8 << 8) + var7;
   }

   public int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = (var1[var2 + 0] << 8) + (var1[var2 + 1] & 255);
      int var6 = (var1[var2 + 2] << 8) + (var1[var2 + 3] & 255);
      int var7 = (var1[var2 + 4] << 8) + (var1[var2 + 5] & 255);
      int var8 = (var1[var2 + 6] << 8) + (var1[var2 + 7] & 255);
      int var9 = 31;

      for (int var10 = 0; var10 < 2; var10++) {
         for (int var11 = 0; var11 < 8; var11++) {
            int var12 = var7;
            var7 = var8;
            var8 = var5;
            var5 = this.h(var9, var6);
            var6 = var5 ^ var12 ^ var9 + 1;
            var9--;
         }

         for (int var13 = 0; var13 < 8; var13++) {
            int var14 = var7;
            var7 = var8;
            var8 = var6 ^ var5 ^ var9 + 1;
            var5 = this.h(var9, var6);
            var6 = var14;
            var9--;
         }
      }

      var3[var4 + 0] = (byte)(var5 >> 8);
      var3[var4 + 1] = (byte)var5;
      var3[var4 + 2] = (byte)(var6 >> 8);
      var3[var4 + 3] = (byte)var6;
      var3[var4 + 4] = (byte)(var7 >> 8);
      var3[var4 + 5] = (byte)var7;
      var3[var4 + 6] = (byte)(var8 >> 8);
      var3[var4 + 7] = (byte)var8;
      return 8;
   }

   private CryptoServicePurpose getPurpose() {
      if (this.key0 == null) {
         return CryptoServicePurpose.ANY;
      } else {
         return this.encrypting ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
      }
   }
}
