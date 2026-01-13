package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class DSTU7564Digest implements ExtendedDigest, Memoable {
   private static final int NB_512 = 8;
   private static final int NB_1024 = 16;
   private static final int NR_512 = 10;
   private static final int NR_1024 = 14;
   private final CryptoServicePurpose purpose;
   private int hashSize;
   private int blockSize;
   private int columns;
   private int rounds;
   private long[] state;
   private long[] tempState1;
   private long[] tempState2;
   private long inputBlocks;
   private int bufOff;
   private byte[] buf;
   private static final byte[] S0 = new byte[]{
      -88,
      67,
      95,
      6,
      107,
      117,
      108,
      89,
      113,
      -33,
      -121,
      -107,
      23,
      -16,
      -40,
      9,
      109,
      -13,
      29,
      -53,
      -55,
      77,
      44,
      -81,
      121,
      -32,
      -105,
      -3,
      111,
      75,
      69,
      57,
      62,
      -35,
      -93,
      79,
      -76,
      -74,
      -102,
      14,
      31,
      -65,
      21,
      -31,
      73,
      -46,
      -109,
      -58,
      -110,
      114,
      -98,
      97,
      -47,
      99,
      -6,
      -18,
      -12,
      25,
      -43,
      -83,
      88,
      -92,
      -69,
      -95,
      -36,
      -14,
      -125,
      55,
      66,
      -28,
      122,
      50,
      -100,
      -52,
      -85,
      74,
      -113,
      110,
      4,
      39,
      46,
      -25,
      -30,
      90,
      -106,
      22,
      35,
      43,
      -62,
      101,
      102,
      15,
      -68,
      -87,
      71,
      65,
      52,
      72,
      -4,
      -73,
      106,
      -120,
      -91,
      83,
      -122,
      -7,
      91,
      -37,
      56,
      123,
      -61,
      30,
      34,
      51,
      36,
      40,
      54,
      -57,
      -78,
      59,
      -114,
      119,
      -70,
      -11,
      20,
      -97,
      8,
      85,
      -101,
      76,
      -2,
      96,
      92,
      -38,
      24,
      70,
      -51,
      125,
      33,
      -80,
      63,
      27,
      -119,
      -1,
      -21,
      -124,
      105,
      58,
      -99,
      -41,
      -45,
      112,
      103,
      64,
      -75,
      -34,
      93,
      48,
      -111,
      -79,
      120,
      17,
      1,
      -27,
      0,
      104,
      -104,
      -96,
      -59,
      2,
      -90,
      116,
      45,
      11,
      -94,
      118,
      -77,
      -66,
      -50,
      -67,
      -82,
      -23,
      -118,
      49,
      28,
      -20,
      -15,
      -103,
      -108,
      -86,
      -10,
      38,
      47,
      -17,
      -24,
      -116,
      53,
      3,
      -44,
      127,
      -5,
      5,
      -63,
      94,
      -112,
      32,
      61,
      -126,
      -9,
      -22,
      10,
      13,
      126,
      -8,
      80,
      26,
      -60,
      7,
      87,
      -72,
      60,
      98,
      -29,
      -56,
      -84,
      82,
      100,
      16,
      -48,
      -39,
      19,
      12,
      18,
      41,
      81,
      -71,
      -49,
      -42,
      115,
      -115,
      -127,
      84,
      -64,
      -19,
      78,
      68,
      -89,
      42,
      -123,
      37,
      -26,
      -54,
      124,
      -117,
      86,
      -128
   };
   private static final byte[] S1 = new byte[]{
      -50,
      -69,
      -21,
      -110,
      -22,
      -53,
      19,
      -63,
      -23,
      58,
      -42,
      -78,
      -46,
      -112,
      23,
      -8,
      66,
      21,
      86,
      -76,
      101,
      28,
      -120,
      67,
      -59,
      92,
      54,
      -70,
      -11,
      87,
      103,
      -115,
      49,
      -10,
      100,
      88,
      -98,
      -12,
      34,
      -86,
      117,
      15,
      2,
      -79,
      -33,
      109,
      115,
      77,
      124,
      38,
      46,
      -9,
      8,
      93,
      68,
      62,
      -97,
      20,
      -56,
      -82,
      84,
      16,
      -40,
      -68,
      26,
      107,
      105,
      -13,
      -67,
      51,
      -85,
      -6,
      -47,
      -101,
      104,
      78,
      22,
      -107,
      -111,
      -18,
      76,
      99,
      -114,
      91,
      -52,
      60,
      25,
      -95,
      -127,
      73,
      123,
      -39,
      111,
      55,
      96,
      -54,
      -25,
      43,
      72,
      -3,
      -106,
      69,
      -4,
      65,
      18,
      13,
      121,
      -27,
      -119,
      -116,
      -29,
      32,
      48,
      -36,
      -73,
      108,
      74,
      -75,
      63,
      -105,
      -44,
      98,
      45,
      6,
      -92,
      -91,
      -125,
      95,
      42,
      -38,
      -55,
      0,
      126,
      -94,
      85,
      -65,
      17,
      -43,
      -100,
      -49,
      14,
      10,
      61,
      81,
      125,
      -109,
      27,
      -2,
      -60,
      71,
      9,
      -122,
      11,
      -113,
      -99,
      106,
      7,
      -71,
      -80,
      -104,
      24,
      50,
      113,
      75,
      -17,
      59,
      112,
      -96,
      -28,
      64,
      -1,
      -61,
      -87,
      -26,
      120,
      -7,
      -117,
      70,
      -128,
      30,
      56,
      -31,
      -72,
      -88,
      -32,
      12,
      35,
      118,
      29,
      37,
      36,
      5,
      -15,
      110,
      -108,
      40,
      -102,
      -124,
      -24,
      -93,
      79,
      119,
      -45,
      -123,
      -30,
      82,
      -14,
      -126,
      80,
      122,
      47,
      116,
      83,
      -77,
      97,
      -81,
      57,
      53,
      -34,
      -51,
      31,
      -103,
      -84,
      -83,
      114,
      44,
      -35,
      -48,
      -121,
      -66,
      94,
      -90,
      -20,
      4,
      -58,
      3,
      52,
      -5,
      -37,
      89,
      -74,
      -62,
      1,
      -16,
      90,
      -19,
      -89,
      102,
      33,
      127,
      -118,
      39,
      -57,
      -64,
      41,
      -41
   };
   private static final byte[] S2 = new byte[]{
      -109,
      -39,
      -102,
      -75,
      -104,
      34,
      69,
      -4,
      -70,
      106,
      -33,
      2,
      -97,
      -36,
      81,
      89,
      74,
      23,
      43,
      -62,
      -108,
      -12,
      -69,
      -93,
      98,
      -28,
      113,
      -44,
      -51,
      112,
      22,
      -31,
      73,
      60,
      -64,
      -40,
      92,
      -101,
      -83,
      -123,
      83,
      -95,
      122,
      -56,
      45,
      -32,
      -47,
      114,
      -90,
      44,
      -60,
      -29,
      118,
      120,
      -73,
      -76,
      9,
      59,
      14,
      65,
      76,
      -34,
      -78,
      -112,
      37,
      -91,
      -41,
      3,
      17,
      0,
      -61,
      46,
      -110,
      -17,
      78,
      18,
      -99,
      125,
      -53,
      53,
      16,
      -43,
      79,
      -98,
      77,
      -87,
      85,
      -58,
      -48,
      123,
      24,
      -105,
      -45,
      54,
      -26,
      72,
      86,
      -127,
      -113,
      119,
      -52,
      -100,
      -71,
      -30,
      -84,
      -72,
      47,
      21,
      -92,
      124,
      -38,
      56,
      30,
      11,
      5,
      -42,
      20,
      110,
      108,
      126,
      102,
      -3,
      -79,
      -27,
      96,
      -81,
      94,
      51,
      -121,
      -55,
      -16,
      93,
      109,
      63,
      -120,
      -115,
      -57,
      -9,
      29,
      -23,
      -20,
      -19,
      -128,
      41,
      39,
      -49,
      -103,
      -88,
      80,
      15,
      55,
      36,
      40,
      48,
      -107,
      -46,
      62,
      91,
      64,
      -125,
      -77,
      105,
      87,
      31,
      7,
      28,
      -118,
      -68,
      32,
      -21,
      -50,
      -114,
      -85,
      -18,
      49,
      -94,
      115,
      -7,
      -54,
      58,
      26,
      -5,
      13,
      -63,
      -2,
      -6,
      -14,
      111,
      -67,
      -106,
      -35,
      67,
      82,
      -74,
      8,
      -13,
      -82,
      -66,
      25,
      -119,
      50,
      38,
      -80,
      -22,
      75,
      100,
      -124,
      -126,
      107,
      -11,
      121,
      -65,
      1,
      95,
      117,
      99,
      27,
      35,
      61,
      104,
      42,
      101,
      -24,
      -111,
      -10,
      -1,
      19,
      88,
      -15,
      71,
      10,
      127,
      -59,
      -89,
      -25,
      97,
      90,
      6,
      70,
      68,
      66,
      4,
      -96,
      -37,
      57,
      -122,
      84,
      -86,
      -116,
      52,
      33,
      -117,
      -8,
      12,
      116,
      103
   };
   private static final byte[] S3 = new byte[]{
      104,
      -115,
      -54,
      77,
      115,
      75,
      78,
      42,
      -44,
      82,
      38,
      -77,
      84,
      30,
      25,
      31,
      34,
      3,
      70,
      61,
      45,
      74,
      83,
      -125,
      19,
      -118,
      -73,
      -43,
      37,
      121,
      -11,
      -67,
      88,
      47,
      13,
      2,
      -19,
      81,
      -98,
      17,
      -14,
      62,
      85,
      94,
      -47,
      22,
      60,
      102,
      112,
      93,
      -13,
      69,
      64,
      -52,
      -24,
      -108,
      86,
      8,
      -50,
      26,
      58,
      -46,
      -31,
      -33,
      -75,
      56,
      110,
      14,
      -27,
      -12,
      -7,
      -122,
      -23,
      79,
      -42,
      -123,
      35,
      -49,
      50,
      -103,
      49,
      20,
      -82,
      -18,
      -56,
      72,
      -45,
      48,
      -95,
      -110,
      65,
      -79,
      24,
      -60,
      44,
      113,
      114,
      68,
      21,
      -3,
      55,
      -66,
      95,
      -86,
      -101,
      -120,
      -40,
      -85,
      -119,
      -100,
      -6,
      96,
      -22,
      -68,
      98,
      12,
      36,
      -90,
      -88,
      -20,
      103,
      32,
      -37,
      124,
      40,
      -35,
      -84,
      91,
      52,
      126,
      16,
      -15,
      123,
      -113,
      99,
      -96,
      5,
      -102,
      67,
      119,
      33,
      -65,
      39,
      9,
      -61,
      -97,
      -74,
      -41,
      41,
      -62,
      -21,
      -64,
      -92,
      -117,
      -116,
      29,
      -5,
      -1,
      -63,
      -78,
      -105,
      46,
      -8,
      101,
      -10,
      117,
      7,
      4,
      73,
      51,
      -28,
      -39,
      -71,
      -48,
      66,
      -57,
      108,
      -112,
      0,
      -114,
      111,
      80,
      1,
      -59,
      -38,
      71,
      63,
      -51,
      105,
      -94,
      -30,
      122,
      -89,
      -58,
      -109,
      15,
      10,
      6,
      -26,
      43,
      -106,
      -93,
      28,
      -81,
      106,
      18,
      -124,
      57,
      -25,
      -80,
      -126,
      -9,
      -2,
      -99,
      -121,
      92,
      -127,
      53,
      -34,
      -76,
      -91,
      -4,
      -128,
      -17,
      -53,
      -69,
      107,
      118,
      -70,
      90,
      125,
      120,
      11,
      -107,
      -29,
      -83,
      116,
      -104,
      59,
      54,
      100,
      109,
      -36,
      -16,
      89,
      -87,
      76,
      23,
      127,
      -111,
      -72,
      -55,
      87,
      27,
      -32,
      97
   };

   public DSTU7564Digest(DSTU7564Digest var1) {
      this.purpose = var1.purpose;
      this.copyIn(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
   }

   private void copyIn(DSTU7564Digest var1) {
      this.hashSize = var1.hashSize;
      this.blockSize = var1.blockSize;
      this.rounds = var1.rounds;
      if (this.columns > 0 && this.columns == var1.columns) {
         System.arraycopy(var1.state, 0, this.state, 0, this.columns);
         System.arraycopy(var1.buf, 0, this.buf, 0, this.blockSize);
      } else {
         this.columns = var1.columns;
         this.state = Arrays.clone(var1.state);
         this.tempState1 = new long[this.columns];
         this.tempState2 = new long[this.columns];
         this.buf = Arrays.clone(var1.buf);
      }

      this.inputBlocks = var1.inputBlocks;
      this.bufOff = var1.bufOff;
   }

   public DSTU7564Digest(int var1) {
      this(var1, CryptoServicePurpose.ANY);
   }

   public DSTU7564Digest(int var1, CryptoServicePurpose var2) {
      this.purpose = var2;
      if (var1 != 256 && var1 != 384 && var1 != 512) {
         throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
      } else {
         this.hashSize = var1 >>> 3;
         if (var1 > 256) {
            this.columns = 16;
            this.rounds = 14;
         } else {
            this.columns = 8;
            this.rounds = 10;
         }

         this.blockSize = this.columns << 3;
         this.state = new long[this.columns];
         this.state[0] = this.blockSize;
         this.tempState1 = new long[this.columns];
         this.tempState2 = new long[this.columns];
         this.buf = new byte[this.blockSize];
         CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DSTU7564";
   }

   @Override
   public int getDigestSize() {
      return this.hashSize;
   }

   @Override
   public int getByteLength() {
      return this.blockSize;
   }

   @Override
   public void update(byte var1) {
      this.buf[this.bufOff++] = var1;
      if (this.bufOff == this.blockSize) {
         this.processBlock(this.buf, 0);
         this.bufOff = 0;
         this.inputBlocks++;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      while (this.bufOff != 0 && var3 > 0) {
         this.update(var1[var2++]);
         var3--;
      }

      if (var3 > 0) {
         while (var3 >= this.blockSize) {
            this.processBlock(var1, var2);
            var2 += this.blockSize;
            var3 -= this.blockSize;
            this.inputBlocks++;
         }

         while (var3 > 0) {
            this.update(var1[var2++]);
            var3--;
         }
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      int var3 = this.bufOff;
      this.buf[this.bufOff++] = -128;
      int var4 = this.blockSize - 12;
      if (this.bufOff > var4) {
         while (this.bufOff < this.blockSize) {
            this.buf[this.bufOff++] = 0;
         }

         this.bufOff = 0;
         this.processBlock(this.buf, 0);
      }

      while (this.bufOff < var4) {
         this.buf[this.bufOff++] = 0;
      }

      long var5 = (this.inputBlocks & 4294967295L) * this.blockSize + var3 << 3;
      Pack.intToLittleEndian((int)var5, this.buf, this.bufOff);
      this.bufOff += 4;
      var5 >>>= 32;
      var5 += (this.inputBlocks >>> 32) * this.blockSize << 3;
      Pack.longToLittleEndian(var5, this.buf, this.bufOff);
      this.processBlock(this.buf, 0);
      System.arraycopy(this.state, 0, this.tempState1, 0, this.columns);
      this.P(this.tempState1);

      for (int var7 = 0; var7 < this.columns; var7++) {
         this.state[var7] = this.state[var7] ^ this.tempState1[var7];
      }

      var3 = this.hashSize >>> 3;

      for (int var9 = this.columns - var3; var9 < this.columns; var9++) {
         Pack.longToLittleEndian(this.state[var9], var1, var2);
         var2 += 8;
      }

      this.reset();
      return this.hashSize;
   }

   @Override
   public void reset() {
      Arrays.fill(this.state, 0L);
      this.state[0] = this.blockSize;
      this.inputBlocks = 0L;
      this.bufOff = 0;
   }

   private void processBlock(byte[] var1, int var2) {
      int var3 = var2;

      for (int var4 = 0; var4 < this.columns; var4++) {
         long var5 = Pack.littleEndianToLong(var1, var3);
         var3 += 8;
         this.tempState1[var4] = this.state[var4] ^ var5;
         this.tempState2[var4] = var5;
      }

      this.P(this.tempState1);
      this.Q(this.tempState2);

      for (int var7 = 0; var7 < this.columns; var7++) {
         this.state[var7] = this.state[var7] ^ this.tempState1[var7] ^ this.tempState2[var7];
      }
   }

   private void P(long[] var1) {
      for (int var2 = 0; var2 < this.rounds; var2++) {
         long var3 = var2;

         for (int var5 = 0; var5 < this.columns; var5++) {
            var1[var5] ^= var3;
            var3 += 16L;
         }

         this.shiftRows(var1);
         this.subBytes(var1);
         this.mixColumns(var1);
      }
   }

   private void Q(long[] var1) {
      for (int var2 = 0; var2 < this.rounds; var2++) {
         long var3 = (long)(this.columns - 1 << 4 ^ var2) << 56 | 67818912035696883L;

         for (int var5 = 0; var5 < this.columns; var5++) {
            var1[var5] += var3;
            var3 -= 1152921504606846976L;
         }

         this.shiftRows(var1);
         this.subBytes(var1);
         this.mixColumns(var1);
      }
   }

   private static long mixColumn(long var0) {
      long var2 = (var0 & 9187201950435737471L) << 1 ^ ((var0 & -9187201950435737472L) >>> 7) * 29L;
      long var4 = rotate(8, var0) ^ var0;
      var4 ^= rotate(16, var4);
      var4 ^= rotate(48, var0);
      long var6 = var4 ^ var0 ^ var2;
      var6 = (var6 & 4557430888798830399L) << 2 ^ ((var6 & -9187201950435737472L) >>> 6) * 29L ^ ((var6 & 4629771061636907072L) >>> 6) * 29L;
      return var4 ^ rotate(32, var6) ^ rotate(40, var2) ^ rotate(48, var2);
   }

   private void mixColumns(long[] var1) {
      for (int var2 = 0; var2 < this.columns; var2++) {
         var1[var2] = mixColumn(var1[var2]);
      }
   }

   private static long rotate(int var0, long var1) {
      return var1 >>> var0 | var1 << -var0;
   }

   private void shiftRows(long[] var1) {
      switch (this.columns) {
         case 8:
            long var40 = var1[0];
            long var48 = var1[1];
            long var56 = var1[2];
            long var64 = var1[3];
            long var72 = var1[4];
            long var80 = var1[5];
            long var88 = var1[6];
            long var96 = var1[7];
            long var104 = (var40 ^ var72) & -4294967296L;
            var40 ^= var104;
            var72 ^= var104;
            var104 = (var48 ^ var80) & 72057594021150720L;
            var48 ^= var104;
            var80 ^= var104;
            var104 = (var56 ^ var88) & 281474976645120L;
            var56 ^= var104;
            var88 ^= var104;
            var104 = (var64 ^ var96) & 1099511627520L;
            var64 ^= var104;
            var96 ^= var104;
            var104 = (var40 ^ var56) & -281470681808896L;
            var40 ^= var104;
            var56 ^= var104;
            var104 = (var48 ^ var64) & 72056494543077120L;
            var48 ^= var104;
            var64 ^= var104;
            var104 = (var72 ^ var88) & -281470681808896L;
            var72 ^= var104;
            var88 ^= var104;
            var104 = (var80 ^ var96) & 72056494543077120L;
            var80 ^= var104;
            var96 ^= var104;
            var104 = (var40 ^ var48) & -71777214294589696L;
            var40 ^= var104;
            var48 ^= var104;
            var104 = (var56 ^ var64) & -71777214294589696L;
            var56 ^= var104;
            var64 ^= var104;
            var104 = (var72 ^ var80) & -71777214294589696L;
            var72 ^= var104;
            var80 ^= var104;
            var104 = (var88 ^ var96) & -71777214294589696L;
            var88 ^= var104;
            var96 ^= var104;
            var1[0] = var40;
            var1[1] = var48;
            var1[2] = var56;
            var1[3] = var64;
            var1[4] = var72;
            var1[5] = var80;
            var1[6] = var88;
            var1[7] = var96;
            break;
         case 16:
            long var2 = var1[0];
            long var4 = var1[1];
            long var6 = var1[2];
            long var8 = var1[3];
            long var10 = var1[4];
            long var12 = var1[5];
            long var14 = var1[6];
            long var16 = var1[7];
            long var18 = var1[8];
            long var20 = var1[9];
            long var22 = var1[10];
            long var24 = var1[11];
            long var26 = var1[12];
            long var28 = var1[13];
            long var30 = var1[14];
            long var32 = var1[15];
            long var34 = (var2 ^ var18) & -72057594037927936L;
            var2 ^= var34;
            var18 ^= var34;
            var34 = (var4 ^ var20) & -72057594037927936L;
            var4 ^= var34;
            var20 ^= var34;
            var34 = (var6 ^ var22) & -281474976710656L;
            var6 ^= var34;
            var22 ^= var34;
            var34 = (var8 ^ var24) & -1099511627776L;
            var8 ^= var34;
            var24 ^= var34;
            var34 = (var10 ^ var26) & -4294967296L;
            var10 ^= var34;
            var26 ^= var34;
            var34 = (var12 ^ var28) & 72057594021150720L;
            var12 ^= var34;
            var28 ^= var34;
            var34 = (var14 ^ var30) & 72057594037862400L;
            var14 ^= var34;
            var30 ^= var34;
            var34 = (var16 ^ var32) & 72057594037927680L;
            var16 ^= var34;
            var32 ^= var34;
            var34 = (var2 ^ var10) & 72057589742960640L;
            var2 ^= var34;
            var10 ^= var34;
            var34 = (var4 ^ var12) & -16777216L;
            var4 ^= var34;
            var12 ^= var34;
            var34 = (var6 ^ var14) & -71776119061282816L;
            var6 ^= var34;
            var14 ^= var34;
            var34 = (var8 ^ var16) & -72056494526300416L;
            var8 ^= var34;
            var16 ^= var34;
            var34 = (var18 ^ var26) & 72057589742960640L;
            var18 ^= var34;
            var26 ^= var34;
            var34 = (var20 ^ var28) & -16777216L;
            var20 ^= var34;
            var28 ^= var34;
            var34 = (var22 ^ var30) & -71776119061282816L;
            var22 ^= var34;
            var30 ^= var34;
            var34 = (var24 ^ var32) & -72056494526300416L;
            var24 ^= var34;
            var32 ^= var34;
            var34 = (var2 ^ var6) & -281470681808896L;
            var2 ^= var34;
            var6 ^= var34;
            var34 = (var4 ^ var8) & 72056494543077120L;
            var4 ^= var34;
            var8 ^= var34;
            var34 = (var10 ^ var14) & -281470681808896L;
            var10 ^= var34;
            var14 ^= var34;
            var34 = (var12 ^ var16) & 72056494543077120L;
            var12 ^= var34;
            var16 ^= var34;
            var34 = (var18 ^ var22) & -281470681808896L;
            var18 ^= var34;
            var22 ^= var34;
            var34 = (var20 ^ var24) & 72056494543077120L;
            var20 ^= var34;
            var24 ^= var34;
            var34 = (var26 ^ var30) & -281470681808896L;
            var26 ^= var34;
            var30 ^= var34;
            var34 = (var28 ^ var32) & 72056494543077120L;
            var28 ^= var34;
            var32 ^= var34;
            var34 = (var2 ^ var4) & -71777214294589696L;
            var2 ^= var34;
            var4 ^= var34;
            var34 = (var6 ^ var8) & -71777214294589696L;
            var6 ^= var34;
            var8 ^= var34;
            var34 = (var10 ^ var12) & -71777214294589696L;
            var10 ^= var34;
            var12 ^= var34;
            var34 = (var14 ^ var16) & -71777214294589696L;
            var14 ^= var34;
            var16 ^= var34;
            var34 = (var18 ^ var20) & -71777214294589696L;
            var18 ^= var34;
            var20 ^= var34;
            var34 = (var22 ^ var24) & -71777214294589696L;
            var22 ^= var34;
            var24 ^= var34;
            var34 = (var26 ^ var28) & -71777214294589696L;
            var26 ^= var34;
            var28 ^= var34;
            var34 = (var30 ^ var32) & -71777214294589696L;
            var30 ^= var34;
            var32 ^= var34;
            var1[0] = var2;
            var1[1] = var4;
            var1[2] = var6;
            var1[3] = var8;
            var1[4] = var10;
            var1[5] = var12;
            var1[6] = var14;
            var1[7] = var16;
            var1[8] = var18;
            var1[9] = var20;
            var1[10] = var22;
            var1[11] = var24;
            var1[12] = var26;
            var1[13] = var28;
            var1[14] = var30;
            var1[15] = var32;
            break;
         default:
            throw new IllegalStateException("unsupported state size: only 512/1024 are allowed");
      }
   }

   private void subBytes(long[] var1) {
      for (int var2 = 0; var2 < this.columns; var2++) {
         long var3 = var1[var2];
         int var5 = (int)var3;
         int var6 = (int)(var3 >>> 32);
         byte var7 = S0[var5 & 0xFF];
         byte var8 = S1[var5 >>> 8 & 0xFF];
         byte var9 = S2[var5 >>> 16 & 0xFF];
         byte var10 = S3[var5 >>> 24];
         var5 = var7 & 255 | (var8 & 255) << 8 | (var9 & 255) << 16 | var10 << 24;
         byte var11 = S0[var6 & 0xFF];
         byte var12 = S1[var6 >>> 8 & 0xFF];
         byte var13 = S2[var6 >>> 16 & 0xFF];
         byte var14 = S3[var6 >>> 24];
         var6 = var11 & 255 | (var12 & 255) << 8 | (var13 & 255) << 16 | var14 << 24;
         var1[var2] = var5 & 4294967295L | (long)var6 << 32;
      }
   }

   @Override
   public Memoable copy() {
      return new DSTU7564Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      DSTU7564Digest var2 = (DSTU7564Digest)var1;
      this.copyIn(var2);
   }

   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, 256, this.purpose);
   }
}
