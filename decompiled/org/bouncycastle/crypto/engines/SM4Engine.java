package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class SM4Engine implements BlockCipher {
   private static final int BLOCK_SIZE = 16;
   private static final byte[] Sbox = new byte[]{
      -42,
      -112,
      -23,
      -2,
      -52,
      -31,
      61,
      -73,
      22,
      -74,
      20,
      -62,
      40,
      -5,
      44,
      5,
      43,
      103,
      -102,
      118,
      42,
      -66,
      4,
      -61,
      -86,
      68,
      19,
      38,
      73,
      -122,
      6,
      -103,
      -100,
      66,
      80,
      -12,
      -111,
      -17,
      -104,
      122,
      51,
      84,
      11,
      67,
      -19,
      -49,
      -84,
      98,
      -28,
      -77,
      28,
      -87,
      -55,
      8,
      -24,
      -107,
      -128,
      -33,
      -108,
      -6,
      117,
      -113,
      63,
      -90,
      71,
      7,
      -89,
      -4,
      -13,
      115,
      23,
      -70,
      -125,
      89,
      60,
      25,
      -26,
      -123,
      79,
      -88,
      104,
      107,
      -127,
      -78,
      113,
      100,
      -38,
      -117,
      -8,
      -21,
      15,
      75,
      112,
      86,
      -99,
      53,
      30,
      36,
      14,
      94,
      99,
      88,
      -47,
      -94,
      37,
      34,
      124,
      59,
      1,
      33,
      120,
      -121,
      -44,
      0,
      70,
      87,
      -97,
      -45,
      39,
      82,
      76,
      54,
      2,
      -25,
      -96,
      -60,
      -56,
      -98,
      -22,
      -65,
      -118,
      -46,
      64,
      -57,
      56,
      -75,
      -93,
      -9,
      -14,
      -50,
      -7,
      97,
      21,
      -95,
      -32,
      -82,
      93,
      -92,
      -101,
      52,
      26,
      85,
      -83,
      -109,
      50,
      48,
      -11,
      -116,
      -79,
      -29,
      29,
      -10,
      -30,
      46,
      -126,
      102,
      -54,
      96,
      -64,
      41,
      35,
      -85,
      13,
      83,
      78,
      111,
      -43,
      -37,
      55,
      69,
      -34,
      -3,
      -114,
      47,
      3,
      -1,
      106,
      114,
      109,
      108,
      91,
      81,
      -115,
      27,
      -81,
      -110,
      -69,
      -35,
      -68,
      127,
      17,
      -39,
      92,
      65,
      31,
      16,
      90,
      -40,
      10,
      -63,
      49,
      -120,
      -91,
      -51,
      123,
      -67,
      45,
      116,
      -48,
      18,
      -72,
      -27,
      -76,
      -80,
      -119,
      105,
      -105,
      74,
      12,
      -106,
      119,
      126,
      101,
      -71,
      -15,
      9,
      -59,
      110,
      -58,
      -124,
      24,
      -16,
      125,
      -20,
      58,
      -36,
      77,
      32,
      121,
      -18,
      95,
      62,
      -41,
      -53,
      57,
      72
   };
   private static final int[] CK = new int[]{
      462357,
      472066609,
      943670861,
      1415275113,
      1886879365,
      -1936483679,
      -1464879427,
      -993275175,
      -521670923,
      -66909679,
      404694573,
      876298825,
      1347903077,
      1819507329,
      -2003855715,
      -1532251463,
      -1060647211,
      -589042959,
      -117504499,
      337322537,
      808926789,
      1280531041,
      1752135293,
      -2071227751,
      -1599623499,
      -1128019247,
      -656414995,
      -184876535,
      269950501,
      741554753,
      1213159005,
      1684763257
   };
   private static final int[] FK = new int[]{-1548633402, 1453994832, 1736282519, -1301273892};
   private final int[] X = new int[4];
   private int[] rk;

   private int rotateLeft(int var1, int var2) {
      return var1 << var2 | var1 >>> -var2;
   }

   private int tau(int var1) {
      int var2 = Sbox[var1 >> 24 & 0xFF] & 255;
      int var3 = Sbox[var1 >> 16 & 0xFF] & 255;
      int var4 = Sbox[var1 >> 8 & 0xFF] & 255;
      int var5 = Sbox[var1 & 0xFF] & 255;
      return var2 << 24 | var3 << 16 | var4 << 8 | var5;
   }

   private int L_ap(int var1) {
      return var1 ^ this.rotateLeft(var1, 13) ^ this.rotateLeft(var1, 23);
   }

   private int T_ap(int var1) {
      return this.L_ap(this.tau(var1));
   }

   private int[] expandKey(boolean var1, byte[] var2) {
      int[] var3 = new int[32];
      int[] var4 = new int[]{Pack.bigEndianToInt(var2, 0), Pack.bigEndianToInt(var2, 4), Pack.bigEndianToInt(var2, 8), Pack.bigEndianToInt(var2, 12)};
      int[] var6 = new int[]{var4[0] ^ FK[0], var4[1] ^ FK[1], var4[2] ^ FK[2], var4[3] ^ FK[3]};
      if (var1) {
         var3[0] = var6[0] ^ this.T_ap(var6[1] ^ var6[2] ^ var6[3] ^ CK[0]);
         var3[1] = var6[1] ^ this.T_ap(var6[2] ^ var6[3] ^ var3[0] ^ CK[1]);
         var3[2] = var6[2] ^ this.T_ap(var6[3] ^ var3[0] ^ var3[1] ^ CK[2]);
         var3[3] = var6[3] ^ this.T_ap(var3[0] ^ var3[1] ^ var3[2] ^ CK[3]);

         for (int var5 = 4; var5 < 32; var5++) {
            var3[var5] = var3[var5 - 4] ^ this.T_ap(var3[var5 - 3] ^ var3[var5 - 2] ^ var3[var5 - 1] ^ CK[var5]);
         }
      } else {
         var3[31] = var6[0] ^ this.T_ap(var6[1] ^ var6[2] ^ var6[3] ^ CK[0]);
         var3[30] = var6[1] ^ this.T_ap(var6[2] ^ var6[3] ^ var3[31] ^ CK[1]);
         var3[29] = var6[2] ^ this.T_ap(var6[3] ^ var3[31] ^ var3[30] ^ CK[2]);
         var3[28] = var6[3] ^ this.T_ap(var3[31] ^ var3[30] ^ var3[29] ^ CK[3]);

         for (int var7 = 27; var7 >= 0; var7--) {
            var3[var7] = var3[var7 + 4] ^ this.T_ap(var3[var7 + 3] ^ var3[var7 + 2] ^ var3[var7 + 1] ^ CK[31 - var7]);
         }
      }

      return var3;
   }

   private int L(int var1) {
      return var1 ^ this.rotateLeft(var1, 2) ^ this.rotateLeft(var1, 10) ^ this.rotateLeft(var1, 18) ^ this.rotateLeft(var1, 24);
   }

   private int T(int var1) {
      return this.L(this.tau(var1));
   }

   private int F0(int[] var1, int var2) {
      return var1[0] ^ this.T(var1[1] ^ var1[2] ^ var1[3] ^ var2);
   }

   private int F1(int[] var1, int var2) {
      return var1[1] ^ this.T(var1[2] ^ var1[3] ^ var1[0] ^ var2);
   }

   private int F2(int[] var1, int var2) {
      return var1[2] ^ this.T(var1[3] ^ var1[0] ^ var1[1] ^ var2);
   }

   private int F3(int[] var1, int var2) {
      return var1[3] ^ this.T(var1[0] ^ var1[1] ^ var1[2] ^ var2);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (var2 instanceof KeyParameter) {
         byte[] var3 = ((KeyParameter)var2).getKey();
         if (var3.length != 16) {
            throw new IllegalArgumentException("SM4 requires a 128 bit key");
         } else {
            this.rk = this.expandKey(var1, var3);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
         }
      } else {
         throw new IllegalArgumentException("invalid parameter passed to SM4 init - " + var2.getClass().getName());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "SM4";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.rk == null) {
         throw new IllegalStateException("SM4 not initialised");
      } else if (var2 + 16 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 16 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.X[0] = Pack.bigEndianToInt(var1, var2);
         this.X[1] = Pack.bigEndianToInt(var1, var2 + 4);
         this.X[2] = Pack.bigEndianToInt(var1, var2 + 8);
         this.X[3] = Pack.bigEndianToInt(var1, var2 + 12);

         for (byte var5 = 0; var5 < 32; var5 += 4) {
            this.X[0] = this.F0(this.X, this.rk[var5]);
            this.X[1] = this.F1(this.X, this.rk[var5 + 1]);
            this.X[2] = this.F2(this.X, this.rk[var5 + 2]);
            this.X[3] = this.F3(this.X, this.rk[var5 + 3]);
         }

         Pack.intToBigEndian(this.X[3], var3, var4);
         Pack.intToBigEndian(this.X[2], var3, var4 + 4);
         Pack.intToBigEndian(this.X[1], var3, var4 + 8);
         Pack.intToBigEndian(this.X[0], var3, var4 + 12);
         return 16;
      }
   }

   @Override
   public void reset() {
   }
}
