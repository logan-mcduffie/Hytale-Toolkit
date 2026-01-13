package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public final class TwofishEngine implements BlockCipher {
   private static final byte[][] P = new byte[][]{
      {
            -87,
            103,
            -77,
            -24,
            4,
            -3,
            -93,
            118,
            -102,
            -110,
            -128,
            120,
            -28,
            -35,
            -47,
            56,
            13,
            -58,
            53,
            -104,
            24,
            -9,
            -20,
            108,
            67,
            117,
            55,
            38,
            -6,
            19,
            -108,
            72,
            -14,
            -48,
            -117,
            48,
            -124,
            84,
            -33,
            35,
            25,
            91,
            61,
            89,
            -13,
            -82,
            -94,
            -126,
            99,
            1,
            -125,
            46,
            -39,
            81,
            -101,
            124,
            -90,
            -21,
            -91,
            -66,
            22,
            12,
            -29,
            97,
            -64,
            -116,
            58,
            -11,
            115,
            44,
            37,
            11,
            -69,
            78,
            -119,
            107,
            83,
            106,
            -76,
            -15,
            -31,
            -26,
            -67,
            69,
            -30,
            -12,
            -74,
            102,
            -52,
            -107,
            3,
            86,
            -44,
            28,
            30,
            -41,
            -5,
            -61,
            -114,
            -75,
            -23,
            -49,
            -65,
            -70,
            -22,
            119,
            57,
            -81,
            51,
            -55,
            98,
            113,
            -127,
            121,
            9,
            -83,
            36,
            -51,
            -7,
            -40,
            -27,
            -59,
            -71,
            77,
            68,
            8,
            -122,
            -25,
            -95,
            29,
            -86,
            -19,
            6,
            112,
            -78,
            -46,
            65,
            123,
            -96,
            17,
            49,
            -62,
            39,
            -112,
            32,
            -10,
            96,
            -1,
            -106,
            92,
            -79,
            -85,
            -98,
            -100,
            82,
            27,
            95,
            -109,
            10,
            -17,
            -111,
            -123,
            73,
            -18,
            45,
            79,
            -113,
            59,
            71,
            -121,
            109,
            70,
            -42,
            62,
            105,
            100,
            42,
            -50,
            -53,
            47,
            -4,
            -105,
            5,
            122,
            -84,
            127,
            -43,
            26,
            75,
            14,
            -89,
            90,
            40,
            20,
            63,
            41,
            -120,
            60,
            76,
            2,
            -72,
            -38,
            -80,
            23,
            85,
            31,
            -118,
            125,
            87,
            -57,
            -115,
            116,
            -73,
            -60,
            -97,
            114,
            126,
            21,
            34,
            18,
            88,
            7,
            -103,
            52,
            110,
            80,
            -34,
            104,
            101,
            -68,
            -37,
            -8,
            -56,
            -88,
            43,
            64,
            -36,
            -2,
            50,
            -92,
            -54,
            16,
            33,
            -16,
            -45,
            93,
            15,
            0,
            111,
            -99,
            54,
            66,
            74,
            94,
            -63,
            -32
      },
      {
            117,
            -13,
            -58,
            -12,
            -37,
            123,
            -5,
            -56,
            74,
            -45,
            -26,
            107,
            69,
            125,
            -24,
            75,
            -42,
            50,
            -40,
            -3,
            55,
            113,
            -15,
            -31,
            48,
            15,
            -8,
            27,
            -121,
            -6,
            6,
            63,
            94,
            -70,
            -82,
            91,
            -118,
            0,
            -68,
            -99,
            109,
            -63,
            -79,
            14,
            -128,
            93,
            -46,
            -43,
            -96,
            -124,
            7,
            20,
            -75,
            -112,
            44,
            -93,
            -78,
            115,
            76,
            84,
            -110,
            116,
            54,
            81,
            56,
            -80,
            -67,
            90,
            -4,
            96,
            98,
            -106,
            108,
            66,
            -9,
            16,
            124,
            40,
            39,
            -116,
            19,
            -107,
            -100,
            -57,
            36,
            70,
            59,
            112,
            -54,
            -29,
            -123,
            -53,
            17,
            -48,
            -109,
            -72,
            -90,
            -125,
            32,
            -1,
            -97,
            119,
            -61,
            -52,
            3,
            111,
            8,
            -65,
            64,
            -25,
            43,
            -30,
            121,
            12,
            -86,
            -126,
            65,
            58,
            -22,
            -71,
            -28,
            -102,
            -92,
            -105,
            126,
            -38,
            122,
            23,
            102,
            -108,
            -95,
            29,
            61,
            -16,
            -34,
            -77,
            11,
            114,
            -89,
            28,
            -17,
            -47,
            83,
            62,
            -113,
            51,
            38,
            95,
            -20,
            118,
            42,
            73,
            -127,
            -120,
            -18,
            33,
            -60,
            26,
            -21,
            -39,
            -59,
            57,
            -103,
            -51,
            -83,
            49,
            -117,
            1,
            24,
            35,
            -35,
            31,
            78,
            45,
            -7,
            72,
            79,
            -14,
            101,
            -114,
            120,
            92,
            88,
            25,
            -115,
            -27,
            -104,
            87,
            103,
            127,
            5,
            100,
            -81,
            99,
            -74,
            -2,
            -11,
            -73,
            60,
            -91,
            -50,
            -23,
            104,
            68,
            -32,
            77,
            67,
            105,
            41,
            46,
            -84,
            21,
            89,
            -88,
            10,
            -98,
            110,
            71,
            -33,
            52,
            53,
            106,
            -49,
            -36,
            34,
            -55,
            -64,
            -101,
            -119,
            -44,
            -19,
            -85,
            18,
            -94,
            13,
            82,
            -69,
            2,
            47,
            -87,
            -41,
            97,
            30,
            -76,
            80,
            4,
            -10,
            -62,
            22,
            37,
            -122,
            86,
            85,
            9,
            -66,
            -111
      }
   };
   private static final int P_00 = 1;
   private static final int P_01 = 0;
   private static final int P_02 = 0;
   private static final int P_03 = 1;
   private static final int P_04 = 1;
   private static final int P_10 = 0;
   private static final int P_11 = 0;
   private static final int P_12 = 1;
   private static final int P_13 = 1;
   private static final int P_14 = 0;
   private static final int P_20 = 1;
   private static final int P_21 = 1;
   private static final int P_22 = 0;
   private static final int P_23 = 0;
   private static final int P_24 = 0;
   private static final int P_30 = 0;
   private static final int P_31 = 1;
   private static final int P_32 = 1;
   private static final int P_33 = 0;
   private static final int P_34 = 1;
   private static final int GF256_FDBK = 361;
   private static final int GF256_FDBK_2 = 180;
   private static final int GF256_FDBK_4 = 90;
   private static final int RS_GF_FDBK = 333;
   private static final int ROUNDS = 16;
   private static final int MAX_ROUNDS = 16;
   private static final int BLOCK_SIZE = 16;
   private static final int MAX_KEY_BITS = 256;
   private static final int INPUT_WHITEN = 0;
   private static final int OUTPUT_WHITEN = 4;
   private static final int ROUND_SUBKEYS = 8;
   private static final int TOTAL_SUBKEYS = 40;
   private static final int SK_STEP = 33686018;
   private static final int SK_BUMP = 16843009;
   private static final int SK_ROTL = 9;
   private boolean encrypting = false;
   private int[] gMDS0 = new int[256];
   private int[] gMDS1 = new int[256];
   private int[] gMDS2 = new int[256];
   private int[] gMDS3 = new int[256];
   private int[] gSubKeys;
   private int[] gSBox;
   private int k64Cnt = 0;
   private byte[] workingKey = null;

   public TwofishEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
      int[] var1 = new int[2];
      int[] var2 = new int[2];
      int[] var3 = new int[2];

      for (int var5 = 0; var5 < 256; var5++) {
         int var4 = P[0][var5] & 255;
         var1[0] = var4;
         var2[0] = this.Mx_X(var4) & 0xFF;
         var3[0] = this.Mx_Y(var4) & 0xFF;
         var4 = P[1][var5] & 255;
         var1[1] = var4;
         var2[1] = this.Mx_X(var4) & 0xFF;
         var3[1] = this.Mx_Y(var4) & 0xFF;
         this.gMDS0[var5] = var1[1] | var2[1] << 8 | var3[1] << 16 | var3[1] << 24;
         this.gMDS1[var5] = var3[0] | var3[0] << 8 | var2[0] << 16 | var1[0] << 24;
         this.gMDS2[var5] = var2[1] | var3[1] << 8 | var1[1] << 16 | var3[1] << 24;
         this.gMDS3[var5] = var2[0] | var1[0] << 8 | var3[0] << 16 | var2[0] << 24;
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof KeyParameter) {
         this.encrypting = var1;
         this.workingKey = ((KeyParameter)var2).getKey();
         int var3 = this.workingKey.length * 8;
         switch (var3) {
            case 128:
            case 192:
            case 256:
               CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var3, var2, Utils.getPurpose(var1)));
               this.k64Cnt = this.workingKey.length / 8;
               this.setKey(this.workingKey);
               return;
            default:
               throw new IllegalArgumentException("Key length not 128/192/256 bits.");
         }
      } else {
         throw new IllegalArgumentException("invalid parameter passed to Twofish init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "Twofish";
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      if (this.workingKey == null) {
         throw new IllegalStateException("Twofish not initialised");
      } else if (var2 + 16 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 16 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.encrypting) {
            this.encryptBlock(var1, var2, var3, var4);
         } else {
            this.decryptBlock(var1, var2, var3, var4);
         }

         return 16;
      }
   }

   @Override
   public void reset() {
      if (this.workingKey != null) {
         this.setKey(this.workingKey);
      }
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   private void setKey(byte[] var1) {
      int[] var2 = new int[4];
      int[] var3 = new int[4];
      int[] var4 = new int[4];
      this.gSubKeys = new int[40];

      for (int var5 = 0; var5 < this.k64Cnt; var5++) {
         int var6 = var5 * 8;
         var2[var5] = Pack.littleEndianToInt(var1, var6);
         var3[var5] = Pack.littleEndianToInt(var1, var6 + 4);
         var4[this.k64Cnt - 1 - var5] = this.RS_MDS_Encode(var2[var5], var3[var5]);
      }

      for (int var8 = 0; var8 < 20; var8++) {
         int var17 = var8 * 33686018;
         int var18 = this.F32(var17, var2);
         int var7 = this.F32(var17 + 16843009, var3);
         var7 = Integers.rotateLeft(var7, 8);
         var18 += var7;
         this.gSubKeys[var8 * 2] = var18;
         var18 += var7;
         this.gSubKeys[var8 * 2 + 1] = var18 << 9 | var18 >>> 23;
      }

      int var22 = var4[0];
      int var9 = var4[1];
      int var10 = var4[2];
      int var11 = var4[3];
      this.gSBox = new int[1024];

      for (int var16 = 0; var16 < 256; var16++) {
         int var15 = var16;
         int var14 = var16;
         int var13 = var16;
         int var12 = var16;
         switch (this.k64Cnt & 3) {
            case 0:
               var12 = P[1][var16] & 255 ^ this.b0(var11);
               var13 = P[0][var16] & 255 ^ this.b1(var11);
               var14 = P[0][var16] & 255 ^ this.b2(var11);
               var15 = P[1][var16] & 255 ^ this.b3(var11);
            case 3:
               var12 = P[1][var12] & 255 ^ this.b0(var10);
               var13 = P[1][var13] & 255 ^ this.b1(var10);
               var14 = P[0][var14] & 255 ^ this.b2(var10);
               var15 = P[0][var15] & 255 ^ this.b3(var10);
            case 2:
               this.gSBox[var16 * 2] = this.gMDS0[P[0][P[0][var12] & 255 ^ this.b0(var9)] & 255 ^ this.b0(var22)];
               this.gSBox[var16 * 2 + 1] = this.gMDS1[P[0][P[1][var13] & 255 ^ this.b1(var9)] & 255 ^ this.b1(var22)];
               this.gSBox[var16 * 2 + 512] = this.gMDS2[P[1][P[0][var14] & 255 ^ this.b2(var9)] & 255 ^ this.b2(var22)];
               this.gSBox[var16 * 2 + 513] = this.gMDS3[P[1][P[1][var15] & 255 ^ this.b3(var9)] & 255 ^ this.b3(var22)];
               break;
            case 1:
               this.gSBox[var16 * 2] = this.gMDS0[P[0][var16] & 255 ^ this.b0(var22)];
               this.gSBox[var16 * 2 + 1] = this.gMDS1[P[0][var16] & 255 ^ this.b1(var22)];
               this.gSBox[var16 * 2 + 512] = this.gMDS2[P[1][var16] & 255 ^ this.b2(var22)];
               this.gSBox[var16 * 2 + 513] = this.gMDS3[P[1][var16] & 255 ^ this.b3(var22)];
         }
      }
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = Pack.littleEndianToInt(var1, var2) ^ this.gSubKeys[0];
      int var6 = Pack.littleEndianToInt(var1, var2 + 4) ^ this.gSubKeys[1];
      int var7 = Pack.littleEndianToInt(var1, var2 + 8) ^ this.gSubKeys[2];
      int var8 = Pack.littleEndianToInt(var1, var2 + 12) ^ this.gSubKeys[3];
      int var9 = 8;

      for (byte var12 = 0; var12 < 16; var12 += 2) {
         int var10 = this.Fe32_0(var5);
         int var11 = this.Fe32_3(var6);
         int var14 = var7 ^ var10 + var11 + this.gSubKeys[var9++];
         var7 = Integers.rotateRight(var14, 1);
         var8 = Integers.rotateLeft(var8, 1) ^ var10 + 2 * var11 + this.gSubKeys[var9++];
         var10 = this.Fe32_0(var7);
         var11 = this.Fe32_3(var8);
         var5 ^= var10 + var11 + this.gSubKeys[var9++];
         var5 = Integers.rotateRight(var5, 1);
         var6 = Integers.rotateLeft(var6, 1) ^ var10 + 2 * var11 + this.gSubKeys[var9++];
      }

      Pack.intToLittleEndian(var7 ^ this.gSubKeys[4], var3, var4);
      Pack.intToLittleEndian(var8 ^ this.gSubKeys[5], var3, var4 + 4);
      Pack.intToLittleEndian(var5 ^ this.gSubKeys[6], var3, var4 + 8);
      Pack.intToLittleEndian(var6 ^ this.gSubKeys[7], var3, var4 + 12);
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = Pack.littleEndianToInt(var1, var2) ^ this.gSubKeys[4];
      int var6 = Pack.littleEndianToInt(var1, var2 + 4) ^ this.gSubKeys[5];
      int var7 = Pack.littleEndianToInt(var1, var2 + 8) ^ this.gSubKeys[6];
      int var8 = Pack.littleEndianToInt(var1, var2 + 12) ^ this.gSubKeys[7];
      int var9 = 39;

      for (byte var12 = 0; var12 < 16; var12 += 2) {
         int var10 = this.Fe32_0(var5);
         int var11 = this.Fe32_3(var6);
         int var14 = var8 ^ var10 + 2 * var11 + this.gSubKeys[var9--];
         var7 = Integers.rotateLeft(var7, 1) ^ var10 + var11 + this.gSubKeys[var9--];
         var8 = Integers.rotateRight(var14, 1);
         var10 = this.Fe32_0(var7);
         var11 = this.Fe32_3(var8);
         var6 ^= var10 + 2 * var11 + this.gSubKeys[var9--];
         var5 = Integers.rotateLeft(var5, 1) ^ var10 + var11 + this.gSubKeys[var9--];
         var6 = Integers.rotateRight(var6, 1);
      }

      Pack.intToLittleEndian(var7 ^ this.gSubKeys[0], var3, var4);
      Pack.intToLittleEndian(var8 ^ this.gSubKeys[1], var3, var4 + 4);
      Pack.intToLittleEndian(var5 ^ this.gSubKeys[2], var3, var4 + 8);
      Pack.intToLittleEndian(var6 ^ this.gSubKeys[3], var3, var4 + 12);
   }

   private int F32(int var1, int[] var2) {
      int var3 = this.b0(var1);
      int var4 = this.b1(var1);
      int var5 = this.b2(var1);
      int var6 = this.b3(var1);
      int var7 = var2[0];
      int var8 = var2[1];
      int var9 = var2[2];
      int var10 = var2[3];
      int var11 = 0;
      switch (this.k64Cnt & 3) {
         case 0:
            var3 = P[1][var3] & 255 ^ this.b0(var10);
            var4 = P[0][var4] & 255 ^ this.b1(var10);
            var5 = P[0][var5] & 255 ^ this.b2(var10);
            var6 = P[1][var6] & 255 ^ this.b3(var10);
         case 3:
            var3 = P[1][var3] & 255 ^ this.b0(var9);
            var4 = P[1][var4] & 255 ^ this.b1(var9);
            var5 = P[0][var5] & 255 ^ this.b2(var9);
            var6 = P[0][var6] & 255 ^ this.b3(var9);
         case 2:
            var11 = this.gMDS0[P[0][P[0][var3] & 255 ^ this.b0(var8)] & 255 ^ this.b0(var7)]
               ^ this.gMDS1[P[0][P[1][var4] & 255 ^ this.b1(var8)] & 255 ^ this.b1(var7)]
               ^ this.gMDS2[P[1][P[0][var5] & 255 ^ this.b2(var8)] & 255 ^ this.b2(var7)]
               ^ this.gMDS3[P[1][P[1][var6] & 255 ^ this.b3(var8)] & 255 ^ this.b3(var7)];
            break;
         case 1:
            var11 = this.gMDS0[P[0][var3] & 255 ^ this.b0(var7)]
               ^ this.gMDS1[P[0][var4] & 255 ^ this.b1(var7)]
               ^ this.gMDS2[P[1][var5] & 255 ^ this.b2(var7)]
               ^ this.gMDS3[P[1][var6] & 255 ^ this.b3(var7)];
      }

      return var11;
   }

   private int RS_MDS_Encode(int var1, int var2) {
      int var3 = var2;

      for (int var4 = 0; var4 < 4; var4++) {
         var3 = this.RS_rem(var3);
      }

      var3 ^= var1;

      for (int var6 = 0; var6 < 4; var6++) {
         var3 = this.RS_rem(var3);
      }

      return var3;
   }

   private int RS_rem(int var1) {
      int var2 = var1 >>> 24 & 0xFF;
      int var3 = (var2 << 1 ^ ((var2 & 128) != 0 ? 333 : 0)) & 0xFF;
      int var4 = var2 >>> 1 ^ ((var2 & 1) != 0 ? 166 : 0) ^ var3;
      return var1 << 8 ^ var4 << 24 ^ var3 << 16 ^ var4 << 8 ^ var2;
   }

   private int LFSR1(int var1) {
      return var1 >> 1 ^ ((var1 & 1) != 0 ? 180 : 0);
   }

   private int LFSR2(int var1) {
      return var1 >> 2 ^ ((var1 & 2) != 0 ? 180 : 0) ^ ((var1 & 1) != 0 ? 90 : 0);
   }

   private int Mx_X(int var1) {
      return var1 ^ this.LFSR2(var1);
   }

   private int Mx_Y(int var1) {
      return var1 ^ this.LFSR1(var1) ^ this.LFSR2(var1);
   }

   private int b0(int var1) {
      return var1 & 0xFF;
   }

   private int b1(int var1) {
      return var1 >>> 8 & 0xFF;
   }

   private int b2(int var1) {
      return var1 >>> 16 & 0xFF;
   }

   private int b3(int var1) {
      return var1 >>> 24 & 0xFF;
   }

   private int Fe32_0(int var1) {
      return this.gSBox[0 + 2 * (var1 & 0xFF)]
         ^ this.gSBox[1 + 2 * (var1 >>> 8 & 0xFF)]
         ^ this.gSBox[512 + 2 * (var1 >>> 16 & 0xFF)]
         ^ this.gSBox[513 + 2 * (var1 >>> 24 & 0xFF)];
   }

   private int Fe32_3(int var1) {
      return this.gSBox[0 + 2 * (var1 >>> 24 & 0xFF)]
         ^ this.gSBox[1 + 2 * (var1 & 0xFF)]
         ^ this.gSBox[512 + 2 * (var1 >>> 8 & 0xFF)]
         ^ this.gSBox[513 + 2 * (var1 >>> 16 & 0xFF)];
   }
}
