package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public final class WhirlpoolDigest implements ExtendedDigest, Memoable {
   private static final int BITCOUNT_ARRAY_SIZE = 32;
   private static final int BYTE_LENGTH = 64;
   private static final int DIGEST_LENGTH_BYTES = 64;
   private static final int REDUCTION_POLYNOMIAL = 285;
   private static final int ROUNDS = 10;
   private static final int[] SBOX = new int[]{
      24,
      35,
      198,
      232,
      135,
      184,
      1,
      79,
      54,
      166,
      210,
      245,
      121,
      111,
      145,
      82,
      96,
      188,
      155,
      142,
      163,
      12,
      123,
      53,
      29,
      224,
      215,
      194,
      46,
      75,
      254,
      87,
      21,
      119,
      55,
      229,
      159,
      240,
      74,
      218,
      88,
      201,
      41,
      10,
      177,
      160,
      107,
      133,
      189,
      93,
      16,
      244,
      203,
      62,
      5,
      103,
      228,
      39,
      65,
      139,
      167,
      125,
      149,
      216,
      251,
      238,
      124,
      102,
      221,
      23,
      71,
      158,
      202,
      45,
      191,
      7,
      173,
      90,
      131,
      51,
      99,
      2,
      170,
      113,
      200,
      25,
      73,
      217,
      242,
      227,
      91,
      136,
      154,
      38,
      50,
      176,
      233,
      15,
      213,
      128,
      190,
      205,
      52,
      72,
      255,
      122,
      144,
      95,
      32,
      104,
      26,
      174,
      180,
      84,
      147,
      34,
      100,
      241,
      115,
      18,
      64,
      8,
      195,
      236,
      219,
      161,
      141,
      61,
      151,
      0,
      207,
      43,
      118,
      130,
      214,
      27,
      181,
      175,
      106,
      80,
      69,
      243,
      48,
      239,
      63,
      85,
      162,
      234,
      101,
      186,
      47,
      192,
      222,
      28,
      253,
      77,
      146,
      117,
      6,
      138,
      178,
      230,
      14,
      31,
      98,
      212,
      168,
      150,
      249,
      197,
      37,
      89,
      132,
      114,
      57,
      76,
      94,
      120,
      56,
      140,
      209,
      165,
      226,
      97,
      179,
      33,
      156,
      30,
      67,
      199,
      252,
      4,
      81,
      153,
      109,
      13,
      250,
      223,
      126,
      36,
      59,
      171,
      206,
      17,
      143,
      78,
      183,
      235,
      60,
      129,
      148,
      247,
      185,
      19,
      44,
      211,
      231,
      110,
      196,
      3,
      86,
      68,
      127,
      169,
      42,
      187,
      193,
      83,
      220,
      11,
      157,
      108,
      49,
      116,
      246,
      70,
      172,
      137,
      20,
      225,
      22,
      58,
      105,
      9,
      112,
      182,
      208,
      237,
      204,
      66,
      152,
      164,
      40,
      92,
      248,
      134
   };
   private static final long[] C0 = new long[256];
   private static final long[] C1 = new long[256];
   private static final long[] C2 = new long[256];
   private static final long[] C3 = new long[256];
   private static final long[] C4 = new long[256];
   private static final long[] C5 = new long[256];
   private static final long[] C6 = new long[256];
   private static final long[] C7 = new long[256];
   private static final short[] EIGHT = new short[32];
   private final long[] _rc = new long[11];
   private final CryptoServicePurpose purpose;
   private byte[] _buffer = new byte[64];
   private int _bufferPos = 0;
   private short[] _bitCount = new short[32];
   private long[] _hash = new long[8];
   private long[] _K = new long[8];
   private long[] _L = new long[8];
   private long[] _block = new long[8];
   private long[] _state = new long[8];

   private static int mulX(int var0) {
      return var0 << 1 ^ -(var0 >>> 7) & 285;
   }

   private static long packIntoLong(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return (long)var0 << 56 ^ (long)var1 << 48 ^ (long)var2 << 40 ^ (long)var3 << 32 ^ (long)var4 << 24 ^ (long)var5 << 16 ^ (long)var6 << 8 ^ var7;
   }

   public WhirlpoolDigest() {
      this(CryptoServicePurpose.ANY);
   }

   public WhirlpoolDigest(CryptoServicePurpose var1) {
      this._rc[0] = 0L;

      for (int var2 = 1; var2 <= 10; var2++) {
         int var3 = 8 * (var2 - 1);
         this._rc[var2] = C0[var3] & -72057594037927936L
            ^ C1[var3 + 1] & 71776119061217280L
            ^ C2[var3 + 2] & 280375465082880L
            ^ C3[var3 + 3] & 1095216660480L
            ^ C4[var3 + 4] & 4278190080L
            ^ C5[var3 + 5] & 16711680L
            ^ C6[var3 + 6] & 65280L
            ^ C7[var3 + 7] & 255L;
      }

      this.purpose = var1;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize(), var1));
   }

   public WhirlpoolDigest(WhirlpoolDigest var1) {
      this.purpose = var1.purpose;
      this.reset(var1);
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize(), this.purpose));
   }

   @Override
   public String getAlgorithmName() {
      return "Whirlpool";
   }

   @Override
   public int getDigestSize() {
      return 64;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      Pack.longToBigEndian(this._hash, var1, var2);
      this.reset();
      return this.getDigestSize();
   }

   @Override
   public void reset() {
      this._bufferPos = 0;
      Arrays.fill(this._bitCount, (short)0);
      Arrays.fill(this._buffer, (byte)0);
      Arrays.fill(this._hash, 0L);
      Arrays.fill(this._K, 0L);
      Arrays.fill(this._L, 0L);
      Arrays.fill(this._block, 0L);
      Arrays.fill(this._state, 0L);
   }

   private void processFilledBuffer(byte[] var1, int var2) {
      Pack.bigEndianToLong(this._buffer, 0, this._block);
      this.processBlock();
      this._bufferPos = 0;
      Arrays.fill(this._buffer, (byte)0);
   }

   protected void processBlock() {
      for (int var1 = 0; var1 < 8; var1++) {
         this._state[var1] = this._block[var1] ^ (this._K[var1] = this._hash[var1]);
      }

      for (int var3 = 1; var3 <= 10; var3++) {
         for (int var2 = 0; var2 < 8; var2++) {
            this._L[var2] = 0L;
            this._L[var2] = this._L[var2] ^ C0[(int)(this._K[var2 - 0 & 7] >>> 56) & 0xFF];
            this._L[var2] = this._L[var2] ^ C1[(int)(this._K[var2 - 1 & 7] >>> 48) & 0xFF];
            this._L[var2] = this._L[var2] ^ C2[(int)(this._K[var2 - 2 & 7] >>> 40) & 0xFF];
            this._L[var2] = this._L[var2] ^ C3[(int)(this._K[var2 - 3 & 7] >>> 32) & 0xFF];
            this._L[var2] = this._L[var2] ^ C4[(int)(this._K[var2 - 4 & 7] >>> 24) & 0xFF];
            this._L[var2] = this._L[var2] ^ C5[(int)(this._K[var2 - 5 & 7] >>> 16) & 0xFF];
            this._L[var2] = this._L[var2] ^ C6[(int)(this._K[var2 - 6 & 7] >>> 8) & 0xFF];
            this._L[var2] = this._L[var2] ^ C7[(int)this._K[var2 - 7 & 7] & 0xFF];
         }

         System.arraycopy(this._L, 0, this._K, 0, this._K.length);
         this._K[0] = this._K[0] ^ this._rc[var3];

         for (int var5 = 0; var5 < 8; var5++) {
            this._L[var5] = this._K[var5];
            this._L[var5] = this._L[var5] ^ C0[(int)(this._state[var5 - 0 & 7] >>> 56) & 0xFF];
            this._L[var5] = this._L[var5] ^ C1[(int)(this._state[var5 - 1 & 7] >>> 48) & 0xFF];
            this._L[var5] = this._L[var5] ^ C2[(int)(this._state[var5 - 2 & 7] >>> 40) & 0xFF];
            this._L[var5] = this._L[var5] ^ C3[(int)(this._state[var5 - 3 & 7] >>> 32) & 0xFF];
            this._L[var5] = this._L[var5] ^ C4[(int)(this._state[var5 - 4 & 7] >>> 24) & 0xFF];
            this._L[var5] = this._L[var5] ^ C5[(int)(this._state[var5 - 5 & 7] >>> 16) & 0xFF];
            this._L[var5] = this._L[var5] ^ C6[(int)(this._state[var5 - 6 & 7] >>> 8) & 0xFF];
            this._L[var5] = this._L[var5] ^ C7[(int)this._state[var5 - 7 & 7] & 0xFF];
         }

         System.arraycopy(this._L, 0, this._state, 0, this._state.length);
      }

      for (int var4 = 0; var4 < 8; var4++) {
         this._hash[var4] = this._hash[var4] ^ this._state[var4] ^ this._block[var4];
      }
   }

   @Override
   public void update(byte var1) {
      this._buffer[this._bufferPos] = var1;
      if (++this._bufferPos == this._buffer.length) {
         this.processFilledBuffer(this._buffer, 0);
      }

      this.increment();
   }

   private void increment() {
      int var1 = 0;

      for (int var2 = this._bitCount.length - 1; var2 >= 0; var2--) {
         int var3 = (this._bitCount[var2] & 255) + EIGHT[var2] + var1;
         var1 = var3 >>> 8;
         this._bitCount[var2] = (short)(var3 & 0xFF);
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      while (var3 > 0) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }
   }

   private void finish() {
      byte[] var1 = this.copyBitLength();
      this._buffer[this._bufferPos] = (byte)(this._buffer[this._bufferPos] | 128);
      if (++this._bufferPos == this._buffer.length) {
         this.processFilledBuffer(this._buffer, 0);
      }

      if (this._bufferPos > 32) {
         while (this._bufferPos != 0) {
            this.update((byte)0);
         }
      }

      while (this._bufferPos <= 32) {
         this.update((byte)0);
      }

      System.arraycopy(var1, 0, this._buffer, 32, var1.length);
      this.processFilledBuffer(this._buffer, 0);
   }

   private byte[] copyBitLength() {
      byte[] var1 = new byte[32];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (byte)(this._bitCount[var2] & 255);
      }

      return var1;
   }

   @Override
   public int getByteLength() {
      return 64;
   }

   @Override
   public Memoable copy() {
      return new WhirlpoolDigest(this);
   }

   @Override
   public void reset(Memoable var1) {
      WhirlpoolDigest var2 = (WhirlpoolDigest)var1;
      System.arraycopy(var2._rc, 0, this._rc, 0, this._rc.length);
      System.arraycopy(var2._buffer, 0, this._buffer, 0, this._buffer.length);
      this._bufferPos = var2._bufferPos;
      System.arraycopy(var2._bitCount, 0, this._bitCount, 0, this._bitCount.length);
      System.arraycopy(var2._hash, 0, this._hash, 0, this._hash.length);
      System.arraycopy(var2._K, 0, this._K, 0, this._K.length);
      System.arraycopy(var2._L, 0, this._L, 0, this._L.length);
      System.arraycopy(var2._block, 0, this._block, 0, this._block.length);
      System.arraycopy(var2._state, 0, this._state, 0, this._state.length);
   }

   static {
      EIGHT[31] = 8;

      for (int var0 = 0; var0 < 256; var0++) {
         int var1 = SBOX[var0];
         int var2 = mulX(var1);
         int var3 = mulX(var2);
         int var4 = var3 ^ var1;
         int var5 = mulX(var3);
         int var6 = var5 ^ var1;
         C0[var0] = packIntoLong(var1, var1, var3, var1, var5, var4, var2, var6);
         C1[var0] = packIntoLong(var6, var1, var1, var3, var1, var5, var4, var2);
         C2[var0] = packIntoLong(var2, var6, var1, var1, var3, var1, var5, var4);
         C3[var0] = packIntoLong(var4, var2, var6, var1, var1, var3, var1, var5);
         C4[var0] = packIntoLong(var5, var4, var2, var6, var1, var1, var3, var1);
         C5[var0] = packIntoLong(var1, var5, var4, var2, var6, var1, var1, var3);
         C6[var0] = packIntoLong(var3, var1, var5, var4, var2, var6, var1, var1);
         C7[var0] = packIntoLong(var1, var3, var1, var5, var4, var2, var6, var1);
      }
   }
}
