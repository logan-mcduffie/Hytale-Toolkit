package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.RomulusDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class RomulusEngine extends AEADBaseEngine {
   private byte[] k;
   private byte[] npub;
   private static final int AD_BLK_LEN_HALF = 16;
   private RomulusEngine.Instance instance;
   private final byte[] CNT;
   private static final byte[] sbox_8 = new byte[]{
      101,
      76,
      106,
      66,
      75,
      99,
      67,
      107,
      85,
      117,
      90,
      122,
      83,
      115,
      91,
      123,
      53,
      -116,
      58,
      -127,
      -119,
      51,
      -128,
      59,
      -107,
      37,
      -104,
      42,
      -112,
      35,
      -103,
      43,
      -27,
      -52,
      -24,
      -63,
      -55,
      -32,
      -64,
      -23,
      -43,
      -11,
      -40,
      -8,
      -48,
      -16,
      -39,
      -7,
      -91,
      28,
      -88,
      18,
      27,
      -96,
      19,
      -87,
      5,
      -75,
      10,
      -72,
      3,
      -80,
      11,
      -71,
      50,
      -120,
      60,
      -123,
      -115,
      52,
      -124,
      61,
      -111,
      34,
      -100,
      44,
      -108,
      36,
      -99,
      45,
      98,
      74,
      108,
      69,
      77,
      100,
      68,
      109,
      82,
      114,
      92,
      124,
      84,
      116,
      93,
      125,
      -95,
      26,
      -84,
      21,
      29,
      -92,
      20,
      -83,
      2,
      -79,
      12,
      -68,
      4,
      -76,
      13,
      -67,
      -31,
      -56,
      -20,
      -59,
      -51,
      -28,
      -60,
      -19,
      -47,
      -15,
      -36,
      -4,
      -44,
      -12,
      -35,
      -3,
      54,
      -114,
      56,
      -126,
      -117,
      48,
      -125,
      57,
      -106,
      38,
      -102,
      40,
      -109,
      32,
      -101,
      41,
      102,
      78,
      104,
      65,
      73,
      96,
      64,
      105,
      86,
      118,
      88,
      120,
      80,
      112,
      89,
      121,
      -90,
      30,
      -86,
      17,
      25,
      -93,
      16,
      -85,
      6,
      -74,
      8,
      -70,
      0,
      -77,
      9,
      -69,
      -26,
      -50,
      -22,
      -62,
      -53,
      -29,
      -61,
      -21,
      -42,
      -10,
      -38,
      -6,
      -45,
      -13,
      -37,
      -5,
      49,
      -118,
      62,
      -122,
      -113,
      55,
      -121,
      63,
      -110,
      33,
      -98,
      46,
      -105,
      39,
      -97,
      47,
      97,
      72,
      110,
      70,
      79,
      103,
      71,
      111,
      81,
      113,
      94,
      126,
      87,
      119,
      95,
      127,
      -94,
      24,
      -82,
      22,
      31,
      -89,
      23,
      -81,
      1,
      -78,
      14,
      -66,
      7,
      -73,
      15,
      -65,
      -30,
      -54,
      -18,
      -58,
      -49,
      -25,
      -57,
      -17,
      -46,
      -14,
      -34,
      -2,
      -41,
      -9,
      -33,
      -1
   };
   private static final byte[] TWEAKEY_P = new byte[]{9, 15, 8, 13, 10, 14, 12, 11, 0, 1, 2, 3, 4, 5, 6, 7};
   private static final byte[] RC = new byte[]{
      1, 3, 7, 15, 31, 62, 61, 59, 55, 47, 30, 60, 57, 51, 39, 14, 29, 58, 53, 43, 22, 44, 24, 48, 33, 2, 5, 11, 23, 46, 28, 56, 49, 35, 6, 13, 27, 54, 45, 26
   };

   public RomulusEngine(RomulusEngine.RomulusParameters var1) {
      this.KEY_SIZE = this.IV_SIZE = this.MAC_SIZE = this.BlockSize = this.AADBufferSize = 16;
      this.CNT = new byte[7];
      switch (var1.ord) {
         case 0:
            this.algorithmName = "Romulus-M";
            this.instance = new RomulusEngine.RomulusM();
            break;
         case 1:
            this.algorithmName = "Romulus-N";
            this.instance = new RomulusEngine.RomulusN();
            break;
         case 2:
            this.algorithmName = "Romulus-T";
            this.AADBufferSize = 32;
            this.instance = new RomulusEngine.RomulusT();
      }

      this.setInnerMembers(
         var1 == RomulusEngine.RomulusParameters.RomulusN ? AEADBaseEngine.ProcessingBufferType.Buffered : AEADBaseEngine.ProcessingBufferType.Immediate,
         AEADBaseEngine.AADOperatorType.Counter,
         var1 == RomulusEngine.RomulusParameters.RomulusM ? AEADBaseEngine.DataOperatorType.Stream : AEADBaseEngine.DataOperatorType.Counter
      );
   }

   private static void skinny_128_384_plus_enc(byte[] var0, byte[] var1) {
      byte[][] var2 = new byte[4][4];
      byte[][][] var3 = new byte[3][4][4];
      byte[][][] var10 = new byte[3][4][4];

      for (int var4 = 0; var4 < 4; var4++) {
         int var6 = var4 << 2;
         System.arraycopy(var0, var6, var2[var4], 0, 4);
         System.arraycopy(var1, var6, var3[0][var4], 0, 4);
         System.arraycopy(var1, var6 + 16, var3[1][var4], 0, 4);
         System.arraycopy(var1, var6 + 32, var3[2][var4], 0, 4);
      }

      for (int var11 = 0; var11 < 40; var11++) {
         for (int var12 = 0; var12 < 4; var12++) {
            for (int var5 = 0; var5 < 4; var5++) {
               var2[var12][var5] = sbox_8[var2[var12][var5] & 255];
            }
         }

         var2[0][0] = (byte)(var2[0][0] ^ RC[var11] & 15);
         var2[1][0] = (byte)(var2[1][0] ^ RC[var11] >>> 4 & 3);
         var2[2][0] = (byte)(var2[2][0] ^ 2);

         for (int var13 = 0; var13 <= 1; var13++) {
            for (int var17 = 0; var17 < 4; var17++) {
               var2[var13][var17] = (byte)(var2[var13][var17] ^ var3[0][var13][var17] ^ var3[1][var13][var17] ^ var3[2][var13][var17]);
            }
         }

         for (int var14 = 0; var14 < 4; var14++) {
            for (int var18 = 0; var18 < 4; var18++) {
               byte var8 = TWEAKEY_P[var18 + (var14 << 2)];
               int var22 = var8 >>> 2;
               int var7 = var8 & 3;
               var10[0][var14][var18] = var3[0][var22][var7];
               var10[1][var14][var18] = var3[1][var22][var7];
               var10[2][var14][var18] = var3[2][var22][var7];
            }
         }

         int var15;
         for (var15 = 0; var15 <= 1; var15++) {
            for (int var19 = 0; var19 < 4; var19++) {
               var3[0][var15][var19] = var10[0][var15][var19];
               byte var9 = var10[1][var15][var19];
               var3[1][var15][var19] = (byte)(var9 << 1 & 254 ^ var9 >>> 7 & 1 ^ var9 >>> 5 & 1);
               var9 = var10[2][var15][var19];
               var3[2][var15][var19] = (byte)(var9 >>> 1 & 127 ^ var9 << 7 & 128 ^ var9 << 1 & 128);
            }
         }

         while (var15 < 4) {
            for (int var20 = 0; var20 < 4; var20++) {
               var3[0][var15][var20] = var10[0][var15][var20];
               var3[1][var15][var20] = var10[1][var15][var20];
               var3[2][var15][var20] = var10[2][var15][var20];
            }

            var15++;
         }

         byte var24 = var2[1][3];
         var2[1][3] = var2[1][2];
         var2[1][2] = var2[1][1];
         var2[1][1] = var2[1][0];
         var2[1][0] = var24;
         var24 = var2[2][0];
         var2[2][0] = var2[2][2];
         var2[2][2] = var24;
         var24 = var2[2][1];
         var2[2][1] = var2[2][3];
         var2[2][3] = var24;
         var24 = var2[3][0];
         var2[3][0] = var2[3][1];
         var2[3][1] = var2[3][2];
         var2[3][2] = var2[3][3];
         var2[3][3] = var24;

         for (int var21 = 0; var21 < 4; var21++) {
            var2[1][var21] = (byte)(var2[1][var21] ^ var2[2][var21]);
            var2[2][var21] = (byte)(var2[2][var21] ^ var2[0][var21]);
            var2[3][var21] = (byte)(var2[3][var21] ^ var2[2][var21]);
            var24 = var2[3][var21];
            var2[3][var21] = var2[2][var21];
            var2[2][var21] = var2[1][var21];
            var2[1][var21] = var2[0][var21];
            var2[0][var21] = var24;
         }
      }

      for (int var16 = 0; var16 < 16; var16++) {
         var0[var16] = (byte)(var2[var16 >>> 2][var16 & 3] & 255);
      }
   }

   void pad(byte[] var1, int var2, byte[] var3, int var4, int var5) {
      var3[var4 - 1] = (byte)(var5 & 15);
      System.arraycopy(var1, var2, var3, 0, var5);
   }

   void g8A(byte[] var1, byte[] var2, int var3) {
      int var4 = Math.min(var2.length - var3, 16);

      for (int var5 = 0; var5 < var4; var5++) {
         var2[var5 + var3] = (byte)((var1[var5] & 255) >>> 1 ^ var1[var5] & 128 ^ (var1[var5] & 1) << 7);
      }
   }

   void rho(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      byte[] var7 = new byte[16];
      this.pad(var1, var2, var7, 16, var6);
      this.g8A(var5, var3, var4);
      if (this.forEncryption) {
         for (int var8 = 0; var8 < 16; var8++) {
            var5[var8] ^= var7[var8];
            if (var8 < var6) {
               var3[var8 + var4] = (byte)(var3[var8 + var4] ^ var7[var8]);
            } else {
               var3[var8 + var4] = 0;
            }
         }
      } else {
         for (int var9 = 0; var9 < 16; var9++) {
            var5[var9] ^= var7[var9];
            if (var9 < var6 && var9 + var4 < var3.length) {
               var5[var9] ^= var3[var9 + var4];
               var3[var9 + var4] = (byte)(var3[var9 + var4] ^ var7[var9]);
            }
         }
      }
   }

   void lfsr_gf56(byte[] var1) {
      byte var2 = (byte)((var1[6] & 255) >>> 7);
      var1[6] = (byte)((var1[6] & 255) << 1 | (var1[5] & 255) >>> 7);
      var1[5] = (byte)((var1[5] & 255) << 1 | (var1[4] & 255) >>> 7);
      var1[4] = (byte)((var1[4] & 255) << 1 | (var1[3] & 255) >>> 7);
      var1[3] = (byte)((var1[3] & 255) << 1 | (var1[2] & 255) >>> 7);
      var1[2] = (byte)((var1[2] & 255) << 1 | (var1[1] & 255) >>> 7);
      var1[1] = (byte)((var1[1] & 255) << 1 | (var1[0] & 255) >>> 7);
      if (var2 == 1) {
         var1[0] = (byte)((var1[0] & 255) << 1 ^ 149);
      } else {
         var1[0] = (byte)((var1[0] & 255) << 1);
      }
   }

   void block_cipher(byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, byte var6) {
      byte[] var7 = new byte[48];
      System.arraycopy(var5, 0, var7, 0, 7);
      var7[7] = var6;
      System.arraycopy(var3, var4, var7, 16, 16);
      System.arraycopy(var2, 0, var7, 32, 16);
      skinny_128_384_plus_enc(var1, var7);
   }

   private void reset_lfsr_gf56(byte[] var1) {
      var1[0] = 1;
      Arrays.fill(var1, 1, 7, (byte)0);
   }

   public static void hirose_128_128_256(RomulusDigest.Friend var0, byte[] var1, byte[] var2, byte[] var3, int var4) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by RomulusDigest");
      } else {
         hirose_128_128_256(var1, var2, var3, var4);
      }
   }

   static void hirose_128_128_256(byte[] var0, byte[] var1, byte[] var2, int var3) {
      byte[] var4 = new byte[48];
      byte[] var5 = new byte[16];
      System.arraycopy(var1, 0, var4, 0, 16);
      System.arraycopy(var0, 0, var1, 0, 16);
      System.arraycopy(var0, 0, var5, 0, 16);
      var1[0] = (byte)(var1[0] ^ 1);
      System.arraycopy(var2, var3, var4, 16, 32);
      skinny_128_384_plus_enc(var0, var4);
      skinny_128_384_plus_enc(var1, var4);

      for (int var6 = 0; var6 < 16; var6++) {
         var0[var6] ^= var5[var6];
         var1[var6] ^= var5[var6];
      }

      var1[0] = (byte)(var1[0] ^ 1);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.npub = var2;
      this.k = var1;
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD1(var1);
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      this.instance.processBufferAAD(var1, var2);
   }

   @Override
   protected void processFinalAAD() {
      this.instance.processFinalAAD();
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      this.instance.processFinalBlock(var1, var2);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.instance.processBufferEncrypt(var1, var2, var3, var4);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.instance.processBufferDecrypt(var1, var2, var3, var4);
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      this.instance.reset();
   }

   private interface Instance {
      void processFinalBlock(byte[] var1, int var2);

      void processBufferAAD(byte[] var1, int var2);

      void processFinalAAD();

      void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4);

      void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4);

      void reset();
   }

   private class RomulusM implements RomulusEngine.Instance {
      private final byte[] mac_s = new byte[16];
      private final byte[] mac_CNT = new byte[7];
      private final byte[] s = new byte[16];
      private int offset;
      private boolean twist = true;

      public RomulusM() {
      }

      @Override
      public void processFinalBlock(byte[] var1, int var2) {
         byte var3 = 48;
         int var4 = RomulusEngine.this.aadOperator.getLen();
         int var5 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
         byte[] var6 = ((AEADBaseEngine.StreamDataOperator)RomulusEngine.this.dataOperator).getBytes();
         int var8 = 0;
         int var9 = var2;
         int var7 = var5;
         if ((var4 & 31) == 0 && var4 != 0) {
            var3 = (byte)(var3 ^ 8);
         } else if ((var4 & 31) < 16) {
            var3 = (byte)(var3 ^ 2);
         } else if ((var4 & 31) != 16) {
            var3 = (byte)(var3 ^ 10);
         }

         if ((var5 & 31) == 0 && var5 != 0) {
            var3 = (byte)(var3 ^ 4);
         } else if ((var5 & 31) < 16) {
            var3 = (byte)(var3 ^ 1);
         } else if ((var5 & 31) != 16) {
            var3 = (byte)(var3 ^ 5);
         }

         if (RomulusEngine.this.forEncryption) {
            if ((var3 & 8) == 0) {
               byte[] var10 = new byte[16];
               int var11 = Math.min(var5, 16);
               var7 = var5 - var11;
               RomulusEngine.this.pad(var6, var8, var10, 16, var11);
               RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, var10, 0, this.mac_CNT, (byte)44);
               RomulusEngine.this.lfsr_gf56(this.mac_CNT);
               var8 += var11;
            } else if (var5 == 0) {
               RomulusEngine.this.lfsr_gf56(this.mac_CNT);
            }

            while (var7 > 0) {
               this.offset = var8;
               var7 = this.ad_encryption(var6, var8, this.mac_s, RomulusEngine.this.k, var7, this.mac_CNT);
               var8 = this.offset;
            }

            RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, this.mac_CNT, var3);
            RomulusEngine.this.g8A(this.mac_s, RomulusEngine.this.mac, 0);
            var8 -= var5;
         } else {
            System.arraycopy(var6, var5, RomulusEngine.this.mac, 0, RomulusEngine.this.MAC_SIZE);
         }

         RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
         System.arraycopy(RomulusEngine.this.mac, 0, this.s, 0, 16);
         if (var5 > 0) {
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)36);

            while (var5 > 16) {
               var5 -= 16;
               RomulusEngine.this.rho(var6, var8, var1, var2, this.s, 16);
               var2 += 16;
               var8 += 16;
               RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
               RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)36);
            }

            RomulusEngine.this.rho(var6, var8, var1, var2, this.s, var5);
         }

         if (!RomulusEngine.this.forEncryption) {
            if ((var3 & 8) == 0) {
               byte[] var12 = new byte[16];
               int var13 = Math.min(var7, 16);
               var7 -= var13;
               RomulusEngine.this.pad(var1, var9, var12, 16, var13);
               RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, var12, 0, this.mac_CNT, (byte)44);
               RomulusEngine.this.lfsr_gf56(this.mac_CNT);
               var9 += var13;
            } else if (var5 == 0) {
               RomulusEngine.this.lfsr_gf56(this.mac_CNT);
            }

            while (var7 > 0) {
               this.offset = var9;
               var7 = this.ad_encryption(var1, var9, this.mac_s, RomulusEngine.this.k, var7, this.mac_CNT);
               var9 = this.offset;
            }

            RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, this.mac_CNT, var3);
            RomulusEngine.this.g8A(this.mac_s, RomulusEngine.this.mac, 0);
            System.arraycopy(
               var6, RomulusEngine.this.dataOperator.getLen() - RomulusEngine.this.MAC_SIZE, RomulusEngine.this.m_buf, 0, RomulusEngine.this.MAC_SIZE
            );
            RomulusEngine.this.m_bufPos = 0;
         }
      }

      int ad_encryption(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, byte[] var6) {
         byte[] var7 = new byte[16];
         byte[] var8 = new byte[16];
         byte var9 = 16;
         int var10 = Math.min(var5, var9);
         var5 -= var10;
         RomulusEngine.this.pad(var1, var2, var8, var9, var10);
         Bytes.xorTo(var9, var8, var3);
         int var11;
         this.offset = var11 = var2 + var10;
         RomulusEngine.this.lfsr_gf56(var6);
         if (var5 != 0) {
            var10 = Math.min(var5, var9);
            var5 -= var10;
            RomulusEngine.this.pad(var1, var11, var7, var9, var10);
            this.offset = var11 + var10;
            RomulusEngine.this.block_cipher(var3, var4, var7, 0, var6, (byte)44);
            RomulusEngine.this.lfsr_gf56(var6);
         }

         return var5;
      }

      @Override
      public void processBufferAAD(byte[] var1, int var2) {
         if (this.twist) {
            Bytes.xorTo(RomulusEngine.this.MAC_SIZE, var1, var2, this.mac_s);
         } else {
            RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, var1, var2, this.mac_CNT, (byte)40);
         }

         this.twist = !this.twist;
         RomulusEngine.this.lfsr_gf56(this.mac_CNT);
      }

      @Override
      public void processFinalAAD() {
         if (RomulusEngine.this.aadOperator.getLen() == 0) {
            RomulusEngine.this.lfsr_gf56(this.mac_CNT);
         } else if (RomulusEngine.this.m_aadPos != 0) {
            Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize - 1, (byte)0);
            RomulusEngine.this.m_aad[RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 15);
            if (this.twist) {
               Bytes.xorTo(RomulusEngine.this.BlockSize, RomulusEngine.this.m_aad, this.mac_s);
            } else {
               RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.m_aad, 0, this.mac_CNT, (byte)40);
            }

            RomulusEngine.this.lfsr_gf56(this.mac_CNT);
         }

         RomulusEngine.this.m_aadPos = 0;
         RomulusEngine.this.m_bufPos = RomulusEngine.this.dataOperator.getLen();
      }

      @Override
      public void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      }

      @Override
      public void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      }

      @Override
      public void reset() {
         Arrays.clear(this.s);
         Arrays.clear(this.mac_s);
         RomulusEngine.this.reset_lfsr_gf56(this.mac_CNT);
         RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
         this.twist = true;
      }
   }

   private class RomulusN implements RomulusEngine.Instance {
      private final byte[] s = new byte[16];
      boolean twist;

      public RomulusN() {
      }

      @Override
      public void processFinalBlock(byte[] var1, int var2) {
         int var3 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
         if (var3 == 0) {
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)21);
         } else if (RomulusEngine.this.m_bufPos != 0) {
            int var4 = Math.min(RomulusEngine.this.m_bufPos, 16);
            RomulusEngine.this.rho(RomulusEngine.this.m_buf, 0, var1, var2, this.s, var4);
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            RomulusEngine.this.block_cipher(
               this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)(RomulusEngine.this.m_bufPos == 16 ? 20 : 21)
            );
         }

         RomulusEngine.this.g8A(this.s, RomulusEngine.this.mac, 0);
      }

      @Override
      public void processBufferAAD(byte[] var1, int var2) {
         if (this.twist) {
            Bytes.xorTo(16, var1, var2, this.s);
         } else {
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, var1, var2, RomulusEngine.this.CNT, (byte)8);
         }

         RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
         this.twist = !this.twist;
      }

      @Override
      public void processFinalAAD() {
         if (RomulusEngine.this.m_aadPos != 0) {
            byte[] var1 = new byte[16];
            int var2 = Math.min(RomulusEngine.this.m_aadPos, 16);
            RomulusEngine.this.pad(RomulusEngine.this.m_aad, 0, var1, 16, var2);
            if (this.twist) {
               Bytes.xorTo(16, var1, this.s);
            } else {
               RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, var1, 0, RomulusEngine.this.CNT, (byte)8);
            }

            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
         }

         if (RomulusEngine.this.aadOperator.getLen() == 0) {
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)26);
         } else if ((RomulusEngine.this.m_aadPos & 15) != 0) {
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)26);
         } else {
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)24);
         }

         RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
      }

      @Override
      public void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
         RomulusEngine.this.g8A(this.s, var3, var4);

         for (int var5 = 0; var5 < 16; var5++) {
            this.s[var5] = (byte)(this.s[var5] ^ var1[var5 + var2]);
            var3[var5 + var4] = (byte)(var3[var5 + var4] ^ var1[var5 + var2]);
         }

         RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
         RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)4);
      }

      @Override
      public void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
         RomulusEngine.this.g8A(this.s, var3, var4);

         for (int var5 = 0; var5 < 16; var5++) {
            var3[var5 + var4] = (byte)(var3[var5 + var4] ^ var1[var5 + var2]);
            this.s[var5] = (byte)(this.s[var5] ^ var3[var5 + var4]);
         }

         RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
         RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)4);
      }

      @Override
      public void reset() {
         Arrays.clear(this.s);
         RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
         this.twist = true;
      }
   }

   public static class RomulusParameters {
      public static final int ROMULUS_M = 0;
      public static final int ROMULUS_N = 1;
      public static final int ROMULUS_T = 2;
      public static final RomulusEngine.RomulusParameters RomulusM = new RomulusEngine.RomulusParameters(0);
      public static final RomulusEngine.RomulusParameters RomulusN = new RomulusEngine.RomulusParameters(1);
      public static final RomulusEngine.RomulusParameters RomulusT = new RomulusEngine.RomulusParameters(2);
      private final int ord;

      RomulusParameters(int var1) {
         this.ord = var1;
      }
   }

   private class RomulusT implements RomulusEngine.Instance {
      private final byte[] h = new byte[16];
      private final byte[] g = new byte[16];
      byte[] Z = new byte[16];
      byte[] CNT_Z = new byte[7];
      byte[] LR = new byte[32];
      byte[] T = new byte[16];
      byte[] S = new byte[16];

      private RomulusT() {
      }

      @Override
      public void processFinalBlock(byte[] var1, int var2) {
         byte var3 = 16;
         int var4 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
         if (RomulusEngine.this.m_bufPos != 0) {
            int var5 = Math.min(RomulusEngine.this.m_bufPos, 16);
            System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
            RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)64);
            Bytes.xor(var5, RomulusEngine.this.m_buf, this.S, var1, var2);
            System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            byte[] var6;
            int var7;
            if (RomulusEngine.this.forEncryption) {
               var6 = var1;
               var7 = var2;
            } else {
               var6 = RomulusEngine.this.m_buf;
               var7 = 0;
            }

            System.arraycopy(var6, var7, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.m_bufPos);
            Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos + RomulusEngine.this.m_bufPos, RomulusEngine.this.AADBufferSize - 1, (byte)0);
            RomulusEngine.this.m_aad[RomulusEngine.this.m_aadPos + RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_bufPos & 15);
            if (RomulusEngine.this.m_aadPos == 0) {
               System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.BlockSize);
               var3 = 0;
            }

            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            RomulusEngine.this.lfsr_gf56(this.CNT_Z);
         } else if (RomulusEngine.this.m_aadPos != 0) {
            if (var4 > 0) {
               Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.AADBufferSize, (byte)0);
            } else if (RomulusEngine.this.aadOperator.getLen() != 0) {
               System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, 16);
               var3 = 0;
               RomulusEngine.this.m_aadPos = 0;
            }

            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
         } else if (var4 > 0) {
            Arrays.fill(RomulusEngine.this.m_aad, 0, RomulusEngine.this.BlockSize, (byte)0);
            System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.BlockSize);
            var3 = 0;
            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
         }

         if (var3 == 16) {
            System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, 0, 16);
            System.arraycopy(RomulusEngine.this.CNT, 0, RomulusEngine.this.m_aad, 16, 7);
            Arrays.fill(RomulusEngine.this.m_aad, 23, 31, (byte)0);
            RomulusEngine.this.m_aad[31] = 23;
         } else {
            System.arraycopy(this.CNT_Z, 0, RomulusEngine.this.m_aad, 0, 7);
            Arrays.fill(RomulusEngine.this.m_aad, 7, 31, (byte)0);
            RomulusEngine.this.m_aad[31] = 7;
         }

         this.h[0] = (byte)(this.h[0] ^ 2);
         RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
         System.arraycopy(this.h, 0, this.LR, 0, 16);
         System.arraycopy(this.g, 0, this.LR, 16, 16);
         Arrays.clear(this.CNT_Z);
         RomulusEngine.this.block_cipher(this.LR, RomulusEngine.this.k, this.LR, 16, this.CNT_Z, (byte)68);
         System.arraycopy(this.LR, 0, RomulusEngine.this.mac, 0, RomulusEngine.this.MAC_SIZE);
      }

      @Override
      public void processBufferAAD(byte[] var1, int var2) {
         RomulusEngine.hirose_128_128_256(this.h, this.g, var1, var2);
      }

      @Override
      public void processFinalAAD() {
         Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.AADBufferSize - 1, (byte)0);
         if (RomulusEngine.this.m_aadPos >= 16) {
            RomulusEngine.this.m_aad[RomulusEngine.this.AADBufferSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 15);
            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            RomulusEngine.this.m_aadPos = 0;
         } else if (RomulusEngine.this.m_aadPos >= 0 && RomulusEngine.this.aadOperator.getLen() != 0) {
            RomulusEngine.this.m_aad[RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 15);
            RomulusEngine.this.m_aadPos = RomulusEngine.this.BlockSize;
         }
      }

      private void processBuffer(byte[] var1, int var2, byte[] var3, int var4) {
         System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
         RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)64);
         Bytes.xor(16, this.S, var1, var2, var3, var4);
         System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
         RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)65);
         System.arraycopy(this.S, 0, this.Z, 0, 16);
         RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
      }

      private void processAfterAbsorbCiphertext() {
         if (RomulusEngine.this.m_aadPos == RomulusEngine.this.BlockSize) {
            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            RomulusEngine.this.m_aadPos = 0;
         } else {
            RomulusEngine.this.m_aadPos = RomulusEngine.this.BlockSize;
         }

         RomulusEngine.this.lfsr_gf56(this.CNT_Z);
      }

      @Override
      public void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
         this.processBuffer(var1, var2, var3, var4);
         System.arraycopy(var3, var4, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize);
         this.processAfterAbsorbCiphertext();
      }

      @Override
      public void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
         this.processBuffer(var1, var2, var3, var4);
         System.arraycopy(var1, var2, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize);
         this.processAfterAbsorbCiphertext();
      }

      @Override
      public void reset() {
         Arrays.clear(this.h);
         Arrays.clear(this.g);
         Arrays.clear(this.LR);
         Arrays.clear(this.T);
         Arrays.clear(this.S);
         Arrays.clear(this.CNT_Z);
         RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
         System.arraycopy(RomulusEngine.this.npub, 0, this.Z, 0, RomulusEngine.this.IV_SIZE);
         RomulusEngine.this.block_cipher(this.Z, RomulusEngine.this.k, this.T, 0, this.CNT_Z, (byte)66);
         RomulusEngine.this.reset_lfsr_gf56(this.CNT_Z);
      }
   }
}
