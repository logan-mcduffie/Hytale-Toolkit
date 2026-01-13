package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class CamelliaLightEngine implements BlockCipher {
   private static final int BLOCK_SIZE = 16;
   private static final int MASK8 = 255;
   private boolean initialized = false;
   private int _keySize;
   private boolean forEncryption;
   private int[] subkey = new int[96];
   private int[] kw = new int[8];
   private int[] ke = new int[12];
   private static final int[] SIGMA = new int[]{
      -1600231809, 1003262091, -1233459112, 1286239154, -957401297, -380665154, 1426019237, -237801700, 283453434, -563598051, -1336506174, -1276722691
   };
   private static final byte[] SBOX1 = new byte[]{
      112,
      -126,
      44,
      -20,
      -77,
      39,
      -64,
      -27,
      -28,
      -123,
      87,
      53,
      -22,
      12,
      -82,
      65,
      35,
      -17,
      107,
      -109,
      69,
      25,
      -91,
      33,
      -19,
      14,
      79,
      78,
      29,
      101,
      -110,
      -67,
      -122,
      -72,
      -81,
      -113,
      124,
      -21,
      31,
      -50,
      62,
      48,
      -36,
      95,
      94,
      -59,
      11,
      26,
      -90,
      -31,
      57,
      -54,
      -43,
      71,
      93,
      61,
      -39,
      1,
      90,
      -42,
      81,
      86,
      108,
      77,
      -117,
      13,
      -102,
      102,
      -5,
      -52,
      -80,
      45,
      116,
      18,
      43,
      32,
      -16,
      -79,
      -124,
      -103,
      -33,
      76,
      -53,
      -62,
      52,
      126,
      118,
      5,
      109,
      -73,
      -87,
      49,
      -47,
      23,
      4,
      -41,
      20,
      88,
      58,
      97,
      -34,
      27,
      17,
      28,
      50,
      15,
      -100,
      22,
      83,
      24,
      -14,
      34,
      -2,
      68,
      -49,
      -78,
      -61,
      -75,
      122,
      -111,
      36,
      8,
      -24,
      -88,
      96,
      -4,
      105,
      80,
      -86,
      -48,
      -96,
      125,
      -95,
      -119,
      98,
      -105,
      84,
      91,
      30,
      -107,
      -32,
      -1,
      100,
      -46,
      16,
      -60,
      0,
      72,
      -93,
      -9,
      117,
      -37,
      -118,
      3,
      -26,
      -38,
      9,
      63,
      -35,
      -108,
      -121,
      92,
      -125,
      2,
      -51,
      74,
      -112,
      51,
      115,
      103,
      -10,
      -13,
      -99,
      127,
      -65,
      -30,
      82,
      -101,
      -40,
      38,
      -56,
      55,
      -58,
      59,
      -127,
      -106,
      111,
      75,
      19,
      -66,
      99,
      46,
      -23,
      121,
      -89,
      -116,
      -97,
      110,
      -68,
      -114,
      41,
      -11,
      -7,
      -74,
      47,
      -3,
      -76,
      89,
      120,
      -104,
      6,
      106,
      -25,
      70,
      113,
      -70,
      -44,
      37,
      -85,
      66,
      -120,
      -94,
      -115,
      -6,
      114,
      7,
      -71,
      85,
      -8,
      -18,
      -84,
      10,
      54,
      73,
      42,
      104,
      60,
      56,
      -15,
      -92,
      64,
      40,
      -45,
      123,
      -69,
      -55,
      67,
      -63,
      21,
      -29,
      -83,
      -12,
      119,
      -57,
      -128,
      -98
   };

   private static int rightRotate(int var0, int var1) {
      return (var0 >>> var1) + (var0 << 32 - var1);
   }

   private static int leftRotate(int var0, int var1) {
      return (var0 << var1) + (var0 >>> 32 - var1);
   }

   private static void roldq(int var0, int[] var1, int var2, int[] var3, int var4) {
      var3[0 + var4] = var1[0 + var2] << var0 | var1[1 + var2] >>> 32 - var0;
      var3[1 + var4] = var1[1 + var2] << var0 | var1[2 + var2] >>> 32 - var0;
      var3[2 + var4] = var1[2 + var2] << var0 | var1[3 + var2] >>> 32 - var0;
      var3[3 + var4] = var1[3 + var2] << var0 | var1[0 + var2] >>> 32 - var0;
      var1[0 + var2] = var3[0 + var4];
      var1[1 + var2] = var3[1 + var4];
      var1[2 + var2] = var3[2 + var4];
      var1[3 + var2] = var3[3 + var4];
   }

   private static void decroldq(int var0, int[] var1, int var2, int[] var3, int var4) {
      var3[2 + var4] = var1[0 + var2] << var0 | var1[1 + var2] >>> 32 - var0;
      var3[3 + var4] = var1[1 + var2] << var0 | var1[2 + var2] >>> 32 - var0;
      var3[0 + var4] = var1[2 + var2] << var0 | var1[3 + var2] >>> 32 - var0;
      var3[1 + var4] = var1[3 + var2] << var0 | var1[0 + var2] >>> 32 - var0;
      var1[0 + var2] = var3[2 + var4];
      var1[1 + var2] = var3[3 + var4];
      var1[2 + var2] = var3[0 + var4];
      var1[3 + var2] = var3[1 + var4];
   }

   private static void roldqo32(int var0, int[] var1, int var2, int[] var3, int var4) {
      var3[0 + var4] = var1[1 + var2] << var0 - 32 | var1[2 + var2] >>> 64 - var0;
      var3[1 + var4] = var1[2 + var2] << var0 - 32 | var1[3 + var2] >>> 64 - var0;
      var3[2 + var4] = var1[3 + var2] << var0 - 32 | var1[0 + var2] >>> 64 - var0;
      var3[3 + var4] = var1[0 + var2] << var0 - 32 | var1[1 + var2] >>> 64 - var0;
      var1[0 + var2] = var3[0 + var4];
      var1[1 + var2] = var3[1 + var4];
      var1[2 + var2] = var3[2 + var4];
      var1[3 + var2] = var3[3 + var4];
   }

   private static void decroldqo32(int var0, int[] var1, int var2, int[] var3, int var4) {
      var3[2 + var4] = var1[1 + var2] << var0 - 32 | var1[2 + var2] >>> 64 - var0;
      var3[3 + var4] = var1[2 + var2] << var0 - 32 | var1[3 + var2] >>> 64 - var0;
      var3[0 + var4] = var1[3 + var2] << var0 - 32 | var1[0 + var2] >>> 64 - var0;
      var3[1 + var4] = var1[0 + var2] << var0 - 32 | var1[1 + var2] >>> 64 - var0;
      var1[0 + var2] = var3[2 + var4];
      var1[1 + var2] = var3[3 + var4];
      var1[2 + var2] = var3[0 + var4];
      var1[3 + var2] = var3[1 + var4];
   }

   private int bytes2int(byte[] var1, int var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < 4; var4++) {
         var3 = (var3 << 8) + (var1[var4 + var2] & 255);
      }

      return var3;
   }

   private void int2bytes(int var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 < 4; var4++) {
         var2[3 - var4 + var3] = (byte)var1;
         var1 >>>= 8;
      }
   }

   private byte lRot8(byte var1, int var2) {
      return (byte)(var1 << var2 | (var1 & 255) >>> 8 - var2);
   }

   private int sbox2(int var1) {
      return this.lRot8(SBOX1[var1], 1) & 0xFF;
   }

   private int sbox3(int var1) {
      return this.lRot8(SBOX1[var1], 7) & 0xFF;
   }

   private int sbox4(int var1) {
      return SBOX1[this.lRot8((byte)var1, 1) & 0xFF] & 0xFF;
   }

   private void camelliaF2(int[] var1, int[] var2, int var3) {
      int var4 = var1[0] ^ var2[0 + var3];
      int var6 = this.sbox4(var4 & 0xFF);
      var6 |= this.sbox3(var4 >>> 8 & 0xFF) << 8;
      var6 |= this.sbox2(var4 >>> 16 & 0xFF) << 16;
      var6 |= (SBOX1[var4 >>> 24 & 0xFF] & 255) << 24;
      int var5 = var1[1] ^ var2[1 + var3];
      int var7 = SBOX1[var5 & 0xFF] & 255;
      var7 |= this.sbox4(var5 >>> 8 & 0xFF) << 8;
      var7 |= this.sbox3(var5 >>> 16 & 0xFF) << 16;
      var7 |= this.sbox2(var5 >>> 24 & 0xFF) << 24;
      var7 = leftRotate(var7, 8);
      var6 ^= var7;
      var7 = leftRotate(var7, 8) ^ var6;
      var6 = rightRotate(var6, 8) ^ var7;
      var1[2] ^= leftRotate(var7, 16) ^ var6;
      var1[3] ^= leftRotate(var6, 8);
      var4 = var1[2] ^ var2[2 + var3];
      var6 = this.sbox4(var4 & 0xFF);
      var6 |= this.sbox3(var4 >>> 8 & 0xFF) << 8;
      var6 |= this.sbox2(var4 >>> 16 & 0xFF) << 16;
      var6 |= (SBOX1[var4 >>> 24 & 0xFF] & 255) << 24;
      var5 = var1[3] ^ var2[3 + var3];
      var7 = SBOX1[var5 & 0xFF] & 255;
      var7 |= this.sbox4(var5 >>> 8 & 0xFF) << 8;
      var7 |= this.sbox3(var5 >>> 16 & 0xFF) << 16;
      var7 |= this.sbox2(var5 >>> 24 & 0xFF) << 24;
      var7 = leftRotate(var7, 8);
      var6 ^= var7;
      var7 = leftRotate(var7, 8) ^ var6;
      var6 = rightRotate(var6, 8) ^ var7;
      var1[0] ^= leftRotate(var7, 16) ^ var6;
      var1[1] ^= leftRotate(var6, 8);
   }

   private void camelliaFLs(int[] var1, int[] var2, int var3) {
      var1[1] ^= leftRotate(var1[0] & var2[0 + var3], 1);
      var1[0] ^= var2[1 + var3] | var1[1];
      var1[2] ^= var2[3 + var3] | var1[3];
      var1[3] ^= leftRotate(var2[2 + var3] & var1[2], 1);
   }

   private void setKey(boolean var1, byte[] var2) {
      this.forEncryption = var1;
      int[] var3 = new int[8];
      int[] var4 = new int[4];
      int[] var5 = new int[4];
      int[] var6 = new int[4];
      this._keySize = var2.length;
      switch (var2.length) {
         case 16:
            var3[0] = this.bytes2int(var2, 0);
            var3[1] = this.bytes2int(var2, 4);
            var3[2] = this.bytes2int(var2, 8);
            var3[3] = this.bytes2int(var2, 12);
            var3[4] = var3[5] = var3[6] = var3[7] = 0;
            break;
         case 24:
            var3[0] = this.bytes2int(var2, 0);
            var3[1] = this.bytes2int(var2, 4);
            var3[2] = this.bytes2int(var2, 8);
            var3[3] = this.bytes2int(var2, 12);
            var3[4] = this.bytes2int(var2, 16);
            var3[5] = this.bytes2int(var2, 20);
            var3[6] = ~var3[4];
            var3[7] = ~var3[5];
            break;
         case 32:
            var3[0] = this.bytes2int(var2, 0);
            var3[1] = this.bytes2int(var2, 4);
            var3[2] = this.bytes2int(var2, 8);
            var3[3] = this.bytes2int(var2, 12);
            var3[4] = this.bytes2int(var2, 16);
            var3[5] = this.bytes2int(var2, 20);
            var3[6] = this.bytes2int(var2, 24);
            var3[7] = this.bytes2int(var2, 28);
            break;
         default:
            throw new IllegalArgumentException("key sizes are only 16/24/32 bytes.");
      }

      for (int var7 = 0; var7 < 4; var7++) {
         var4[var7] = var3[var7] ^ var3[var7 + 4];
      }

      this.camelliaF2(var4, SIGMA, 0);

      for (int var8 = 0; var8 < 4; var8++) {
         var4[var8] ^= var3[var8];
      }

      this.camelliaF2(var4, SIGMA, 4);
      if (this._keySize == 16) {
         if (var1) {
            this.kw[0] = var3[0];
            this.kw[1] = var3[1];
            this.kw[2] = var3[2];
            this.kw[3] = var3[3];
            roldq(15, var3, 0, this.subkey, 4);
            roldq(30, var3, 0, this.subkey, 12);
            roldq(15, var3, 0, var6, 0);
            this.subkey[18] = var6[2];
            this.subkey[19] = var6[3];
            roldq(17, var3, 0, this.ke, 4);
            roldq(17, var3, 0, this.subkey, 24);
            roldq(17, var3, 0, this.subkey, 32);
            this.subkey[0] = var4[0];
            this.subkey[1] = var4[1];
            this.subkey[2] = var4[2];
            this.subkey[3] = var4[3];
            roldq(15, var4, 0, this.subkey, 8);
            roldq(15, var4, 0, this.ke, 0);
            roldq(15, var4, 0, var6, 0);
            this.subkey[16] = var6[0];
            this.subkey[17] = var6[1];
            roldq(15, var4, 0, this.subkey, 20);
            roldqo32(34, var4, 0, this.subkey, 28);
            roldq(17, var4, 0, this.kw, 4);
         } else {
            this.kw[4] = var3[0];
            this.kw[5] = var3[1];
            this.kw[6] = var3[2];
            this.kw[7] = var3[3];
            decroldq(15, var3, 0, this.subkey, 28);
            decroldq(30, var3, 0, this.subkey, 20);
            decroldq(15, var3, 0, var6, 0);
            this.subkey[16] = var6[0];
            this.subkey[17] = var6[1];
            decroldq(17, var3, 0, this.ke, 0);
            decroldq(17, var3, 0, this.subkey, 8);
            decroldq(17, var3, 0, this.subkey, 0);
            this.subkey[34] = var4[0];
            this.subkey[35] = var4[1];
            this.subkey[32] = var4[2];
            this.subkey[33] = var4[3];
            decroldq(15, var4, 0, this.subkey, 24);
            decroldq(15, var4, 0, this.ke, 4);
            decroldq(15, var4, 0, var6, 0);
            this.subkey[18] = var6[2];
            this.subkey[19] = var6[3];
            decroldq(15, var4, 0, this.subkey, 12);
            decroldqo32(34, var4, 0, this.subkey, 4);
            roldq(17, var4, 0, this.kw, 0);
         }
      } else {
         for (int var9 = 0; var9 < 4; var9++) {
            var5[var9] = var4[var9] ^ var3[var9 + 4];
         }

         this.camelliaF2(var5, SIGMA, 8);
         if (var1) {
            this.kw[0] = var3[0];
            this.kw[1] = var3[1];
            this.kw[2] = var3[2];
            this.kw[3] = var3[3];
            roldqo32(45, var3, 0, this.subkey, 16);
            roldq(15, var3, 0, this.ke, 4);
            roldq(17, var3, 0, this.subkey, 32);
            roldqo32(34, var3, 0, this.subkey, 44);
            roldq(15, var3, 4, this.subkey, 4);
            roldq(15, var3, 4, this.ke, 0);
            roldq(30, var3, 4, this.subkey, 24);
            roldqo32(34, var3, 4, this.subkey, 36);
            roldq(15, var4, 0, this.subkey, 8);
            roldq(30, var4, 0, this.subkey, 20);
            this.ke[8] = var4[1];
            this.ke[9] = var4[2];
            this.ke[10] = var4[3];
            this.ke[11] = var4[0];
            roldqo32(49, var4, 0, this.subkey, 40);
            this.subkey[0] = var5[0];
            this.subkey[1] = var5[1];
            this.subkey[2] = var5[2];
            this.subkey[3] = var5[3];
            roldq(30, var5, 0, this.subkey, 12);
            roldq(30, var5, 0, this.subkey, 28);
            roldqo32(51, var5, 0, this.kw, 4);
         } else {
            this.kw[4] = var3[0];
            this.kw[5] = var3[1];
            this.kw[6] = var3[2];
            this.kw[7] = var3[3];
            decroldqo32(45, var3, 0, this.subkey, 28);
            decroldq(15, var3, 0, this.ke, 4);
            decroldq(17, var3, 0, this.subkey, 12);
            decroldqo32(34, var3, 0, this.subkey, 0);
            decroldq(15, var3, 4, this.subkey, 40);
            decroldq(15, var3, 4, this.ke, 8);
            decroldq(30, var3, 4, this.subkey, 20);
            decroldqo32(34, var3, 4, this.subkey, 8);
            decroldq(15, var4, 0, this.subkey, 36);
            decroldq(30, var4, 0, this.subkey, 24);
            this.ke[2] = var4[1];
            this.ke[3] = var4[2];
            this.ke[0] = var4[3];
            this.ke[1] = var4[0];
            decroldqo32(49, var4, 0, this.subkey, 4);
            this.subkey[46] = var5[0];
            this.subkey[47] = var5[1];
            this.subkey[44] = var5[2];
            this.subkey[45] = var5[3];
            decroldq(30, var5, 0, this.subkey, 32);
            decroldq(30, var5, 0, this.subkey, 16);
            roldqo32(51, var5, 0, this.kw, 0);
         }
      }
   }

   private int processBlock128(byte[] var1, int var2, byte[] var3, int var4) {
      int[] var5 = new int[4];

      for (int var6 = 0; var6 < 4; var6++) {
         var5[var6] = this.bytes2int(var1, var2 + var6 * 4) ^ this.kw[var6];
      }

      this.camelliaF2(var5, this.subkey, 0);
      this.camelliaF2(var5, this.subkey, 4);
      this.camelliaF2(var5, this.subkey, 8);
      this.camelliaFLs(var5, this.ke, 0);
      this.camelliaF2(var5, this.subkey, 12);
      this.camelliaF2(var5, this.subkey, 16);
      this.camelliaF2(var5, this.subkey, 20);
      this.camelliaFLs(var5, this.ke, 4);
      this.camelliaF2(var5, this.subkey, 24);
      this.camelliaF2(var5, this.subkey, 28);
      this.camelliaF2(var5, this.subkey, 32);
      var5[2] ^= this.kw[4];
      var5[3] ^= this.kw[5];
      var5[0] ^= this.kw[6];
      var5[1] ^= this.kw[7];
      this.int2bytes(var5[2], var3, var4);
      this.int2bytes(var5[3], var3, var4 + 4);
      this.int2bytes(var5[0], var3, var4 + 8);
      this.int2bytes(var5[1], var3, var4 + 12);
      return 16;
   }

   private int processBlock192or256(byte[] var1, int var2, byte[] var3, int var4) {
      int[] var5 = new int[4];

      for (int var6 = 0; var6 < 4; var6++) {
         var5[var6] = this.bytes2int(var1, var2 + var6 * 4) ^ this.kw[var6];
      }

      this.camelliaF2(var5, this.subkey, 0);
      this.camelliaF2(var5, this.subkey, 4);
      this.camelliaF2(var5, this.subkey, 8);
      this.camelliaFLs(var5, this.ke, 0);
      this.camelliaF2(var5, this.subkey, 12);
      this.camelliaF2(var5, this.subkey, 16);
      this.camelliaF2(var5, this.subkey, 20);
      this.camelliaFLs(var5, this.ke, 4);
      this.camelliaF2(var5, this.subkey, 24);
      this.camelliaF2(var5, this.subkey, 28);
      this.camelliaF2(var5, this.subkey, 32);
      this.camelliaFLs(var5, this.ke, 8);
      this.camelliaF2(var5, this.subkey, 36);
      this.camelliaF2(var5, this.subkey, 40);
      this.camelliaF2(var5, this.subkey, 44);
      var5[2] ^= this.kw[4];
      var5[3] ^= this.kw[5];
      var5[0] ^= this.kw[6];
      var5[1] ^= this.kw[7];
      this.int2bytes(var5[2], var3, var4);
      this.int2bytes(var5[3], var3, var4 + 4);
      this.int2bytes(var5[0], var3, var4 + 8);
      this.int2bytes(var5[1], var3, var4 + 12);
      return 16;
   }

   public CamelliaLightEngine() {
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
   }

   @Override
   public String getAlgorithmName() {
      return "Camellia";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("only simple KeyParameter expected.");
      } else {
         this.setKey(var1, ((KeyParameter)var2).getKey());
         this.initialized = true;
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), var2, Utils.getPurpose(var1)));
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws IllegalStateException {
      if (!this.initialized) {
         throw new IllegalStateException("Camellia is not initialized");
      } else if (var2 + 16 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 16 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         return this._keySize == 16 ? this.processBlock128(var1, var2, var3, var4) : this.processBlock192or256(var1, var2, var3, var4);
      }
   }

   @Override
   public void reset() {
   }

   private int bitsOfSecurity() {
      return this._keySize * 8;
   }
}
