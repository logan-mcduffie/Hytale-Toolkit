package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class Blake2sDigest implements ExtendedDigest {
   private static final int[] blake2s_IV = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
   private static final byte[][] blake2s_sigma = new byte[][]{
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
      {14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3},
      {11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4},
      {7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8},
      {9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13},
      {2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9},
      {12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11},
      {13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10},
      {6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5},
      {10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}
   };
   private static final int ROUNDS = 10;
   private static final int BLOCK_LENGTH_BYTES = 64;
   private int digestLength = 32;
   private int keyLength = 0;
   private byte[] salt = null;
   private byte[] personalization = null;
   private byte[] key = null;
   private int fanout = 1;
   private int depth = 1;
   private int leafLength = 0;
   private long nodeOffset = 0L;
   private int nodeDepth = 0;
   private int innerHashLength = 0;
   private boolean isLastNode = false;
   private byte[] buffer = null;
   private int bufferPos = 0;
   private int[] internalState = new int[16];
   private int[] chainValue = null;
   private int t0 = 0;
   private int t1 = 0;
   private int f0 = 0;
   private int f1 = 0;
   private final CryptoServicePurpose purpose;

   public Blake2sDigest() {
      this(256, CryptoServicePurpose.ANY);
   }

   public Blake2sDigest(int var1) {
      this(var1, CryptoServicePurpose.ANY);
   }

   public Blake2sDigest(Blake2sDigest var1) {
      this.bufferPos = var1.bufferPos;
      this.buffer = Arrays.clone(var1.buffer);
      this.keyLength = var1.keyLength;
      this.key = Arrays.clone(var1.key);
      this.digestLength = var1.digestLength;
      this.internalState = Arrays.clone(var1.internalState);
      this.chainValue = Arrays.clone(var1.chainValue);
      this.t0 = var1.t0;
      this.t1 = var1.t1;
      this.f0 = var1.f0;
      this.salt = Arrays.clone(var1.salt);
      this.personalization = Arrays.clone(var1.personalization);
      this.fanout = var1.fanout;
      this.depth = var1.depth;
      this.leafLength = var1.leafLength;
      this.nodeOffset = var1.nodeOffset;
      this.nodeDepth = var1.nodeDepth;
      this.innerHashLength = var1.innerHashLength;
      this.purpose = var1.purpose;
   }

   public Blake2sDigest(int var1, CryptoServicePurpose var2) {
      if (var1 >= 8 && var1 <= 256 && var1 % 8 == 0) {
         this.digestLength = var1 / 8;
         this.purpose = var2;
         CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1, var2));
         this.init(null, null, null);
      } else {
         throw new IllegalArgumentException("BLAKE2s digest bit length must be a multiple of 8 and not greater than 256");
      }
   }

   public Blake2sDigest(byte[] var1) {
      this(var1, CryptoServicePurpose.ANY);
   }

   public Blake2sDigest(byte[] var1, CryptoServicePurpose var2) {
      this.purpose = var2;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1.length * 8, var2));
      this.init(null, null, var1);
   }

   public Blake2sDigest(byte[] var1, int var2, byte[] var3, byte[] var4) {
      this(var1, var2, var3, var4, CryptoServicePurpose.ANY);
   }

   public Blake2sDigest(byte[] var1, int var2, byte[] var3, byte[] var4, CryptoServicePurpose var5) {
      if (var2 >= 1 && var2 <= 32) {
         this.digestLength = var2;
         this.purpose = var5;
         CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var2 * 8, var5));
         this.init(var3, var4, var1);
      } else {
         throw new IllegalArgumentException("Invalid digest length (required: 1 - 32)");
      }
   }

   Blake2sDigest(int var1, byte[] var2, byte[] var3, byte[] var4, long var5, CryptoServicePurpose var7) {
      this.digestLength = var1;
      this.nodeOffset = var5;
      this.purpose = var7;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1 * 8, var7));
      this.init(var3, var4, var2);
   }

   Blake2sDigest(int var1, int var2, long var3) {
      this(var1, var2, var3, CryptoServicePurpose.ANY);
   }

   Blake2sDigest(int var1, int var2, long var3, CryptoServicePurpose var5) {
      this.digestLength = var1;
      this.nodeOffset = var3;
      this.fanout = 0;
      this.depth = 0;
      this.leafLength = var2;
      this.innerHashLength = var2;
      this.nodeDepth = 0;
      this.purpose = var5;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1 * 8, var5));
      this.init(null, null, null);
   }

   Blake2sDigest(byte[] var1, byte[] var2) {
      this.purpose = CryptoServicePurpose.ANY;
      this.digestLength = var2[0];
      this.keyLength = var2[1];
      this.fanout = var2[2];
      this.depth = var2[3];
      this.leafLength = Pack.littleEndianToInt(var2, 4);
      this.nodeOffset = this.nodeOffset | Pack.littleEndianToInt(var2, 8);
      this.nodeDepth = var2[14];
      this.innerHashLength = var2[15];
      byte[] var3 = new byte[8];
      byte[] var4 = new byte[8];
      System.arraycopy(var2, 16, var3, 0, 8);
      System.arraycopy(var2, 24, var4, 0, 8);
      this.init(var3, var4, var1);
   }

   private void init(byte[] var1, byte[] var2, byte[] var3) {
      this.buffer = new byte[64];
      if (var3 != null && var3.length > 0) {
         this.keyLength = var3.length;
         if (this.keyLength > 32) {
            throw new IllegalArgumentException("Keys > 32 bytes are not supported");
         }

         this.key = new byte[this.keyLength];
         System.arraycopy(var3, 0, this.key, 0, this.keyLength);
         System.arraycopy(var3, 0, this.buffer, 0, this.keyLength);
         this.bufferPos = 64;
      }

      if (this.chainValue == null) {
         this.chainValue = new int[8];
         this.chainValue[0] = blake2s_IV[0] ^ (this.digestLength | this.keyLength << 8 | this.fanout << 16 | this.depth << 24);
         this.chainValue[1] = blake2s_IV[1] ^ this.leafLength;
         int var4 = (int)(this.nodeOffset >> 32);
         int var5 = (int)this.nodeOffset;
         this.chainValue[2] = blake2s_IV[2] ^ var5;
         this.chainValue[3] = blake2s_IV[3] ^ (var4 | this.nodeDepth << 16 | this.innerHashLength << 24);
         this.chainValue[4] = blake2s_IV[4];
         this.chainValue[5] = blake2s_IV[5];
         if (var1 != null) {
            if (var1.length != 8) {
               throw new IllegalArgumentException("Salt length must be exactly 8 bytes");
            }

            this.salt = new byte[8];
            System.arraycopy(var1, 0, this.salt, 0, var1.length);
            this.chainValue[4] = this.chainValue[4] ^ Pack.littleEndianToInt(var1, 0);
            this.chainValue[5] = this.chainValue[5] ^ Pack.littleEndianToInt(var1, 4);
         }

         this.chainValue[6] = blake2s_IV[6];
         this.chainValue[7] = blake2s_IV[7];
         if (var2 != null) {
            if (var2.length != 8) {
               throw new IllegalArgumentException("Personalization length must be exactly 8 bytes");
            }

            this.personalization = new byte[8];
            System.arraycopy(var2, 0, this.personalization, 0, var2.length);
            this.chainValue[6] = this.chainValue[6] ^ Pack.littleEndianToInt(var2, 0);
            this.chainValue[7] = this.chainValue[7] ^ Pack.littleEndianToInt(var2, 4);
         }
      }
   }

   private void initializeInternalState() {
      System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
      System.arraycopy(blake2s_IV, 0, this.internalState, this.chainValue.length, 4);
      this.internalState[12] = this.t0 ^ blake2s_IV[4];
      this.internalState[13] = this.t1 ^ blake2s_IV[5];
      this.internalState[14] = this.f0 ^ blake2s_IV[6];
      this.internalState[15] = this.f1 ^ blake2s_IV[7];
   }

   @Override
   public void update(byte var1) {
      int var2 = 64 - this.bufferPos;
      if (var2 == 0) {
         this.t0 += 64;
         if (this.t0 == 0) {
            this.t1++;
         }

         this.compress(this.buffer, 0);
         Arrays.fill(this.buffer, (byte)0);
         this.buffer[0] = var1;
         this.bufferPos = 1;
      } else {
         this.buffer[this.bufferPos] = var1;
         this.bufferPos++;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (var1 != null && var3 != 0) {
         int var4 = 0;
         if (this.bufferPos != 0) {
            var4 = 64 - this.bufferPos;
            if (var4 >= var3) {
               System.arraycopy(var1, var2, this.buffer, this.bufferPos, var3);
               this.bufferPos += var3;
               return;
            }

            System.arraycopy(var1, var2, this.buffer, this.bufferPos, var4);
            this.t0 += 64;
            if (this.t0 == 0) {
               this.t1++;
            }

            this.compress(this.buffer, 0);
            this.bufferPos = 0;
            Arrays.fill(this.buffer, (byte)0);
         }

         int var6 = var2 + var3 - 64;

         int var5;
         for (var5 = var2 + var4; var5 < var6; var5 += 64) {
            this.t0 += 64;
            if (this.t0 == 0) {
               this.t1++;
            }

            this.compress(var1, var5);
         }

         System.arraycopy(var1, var5, this.buffer, 0, var2 + var3 - var5);
         this.bufferPos += var2 + var3 - var5;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      if (var2 > var1.length - this.digestLength) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.f0 = -1;
         if (this.isLastNode) {
            this.f1 = -1;
         }

         this.t0 = this.t0 + this.bufferPos;
         if (this.t0 < 0 && this.bufferPos > -this.t0) {
            this.t1++;
         }

         this.compress(this.buffer, 0);
         Arrays.fill(this.buffer, (byte)0);
         Arrays.fill(this.internalState, 0);
         int var3 = this.digestLength >>> 2;
         int var4 = this.digestLength & 3;
         Pack.intToLittleEndian(this.chainValue, 0, var3, var1, var2);
         if (var4 > 0) {
            byte[] var5 = new byte[4];
            Pack.intToLittleEndian(this.chainValue[var3], var5, 0);
            System.arraycopy(var5, 0, var1, var2 + this.digestLength - var4, var4);
         }

         Arrays.fill(this.chainValue, 0);
         this.reset();
         return this.digestLength;
      }
   }

   @Override
   public void reset() {
      this.bufferPos = 0;
      this.f0 = 0;
      this.f1 = 0;
      this.t0 = 0;
      this.t1 = 0;
      this.isLastNode = false;
      this.chainValue = null;
      Arrays.fill(this.buffer, (byte)0);
      if (this.key != null) {
         System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
         this.bufferPos = 64;
      }

      this.init(this.salt, this.personalization, this.key);
   }

   private void compress(byte[] var1, int var2) {
      this.initializeInternalState();
      int[] var3 = new int[16];
      Pack.littleEndianToInt(var1, var2, var3);

      for (int var4 = 0; var4 < 10; var4++) {
         this.G(var3[blake2s_sigma[var4][0]], var3[blake2s_sigma[var4][1]], 0, 4, 8, 12);
         this.G(var3[blake2s_sigma[var4][2]], var3[blake2s_sigma[var4][3]], 1, 5, 9, 13);
         this.G(var3[blake2s_sigma[var4][4]], var3[blake2s_sigma[var4][5]], 2, 6, 10, 14);
         this.G(var3[blake2s_sigma[var4][6]], var3[blake2s_sigma[var4][7]], 3, 7, 11, 15);
         this.G(var3[blake2s_sigma[var4][8]], var3[blake2s_sigma[var4][9]], 0, 5, 10, 15);
         this.G(var3[blake2s_sigma[var4][10]], var3[blake2s_sigma[var4][11]], 1, 6, 11, 12);
         this.G(var3[blake2s_sigma[var4][12]], var3[blake2s_sigma[var4][13]], 2, 7, 8, 13);
         this.G(var3[blake2s_sigma[var4][14]], var3[blake2s_sigma[var4][15]], 3, 4, 9, 14);
      }

      for (int var5 = 0; var5 < this.chainValue.length; var5++) {
         this.chainValue[var5] = this.chainValue[var5] ^ this.internalState[var5] ^ this.internalState[var5 + 8];
      }
   }

   private void G(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.internalState[var3] = this.internalState[var3] + this.internalState[var4] + var1;
      this.internalState[var6] = Integers.rotateRight(this.internalState[var6] ^ this.internalState[var3], 16);
      this.internalState[var5] = this.internalState[var5] + this.internalState[var6];
      this.internalState[var4] = Integers.rotateRight(this.internalState[var4] ^ this.internalState[var5], 12);
      this.internalState[var3] = this.internalState[var3] + this.internalState[var4] + var2;
      this.internalState[var6] = Integers.rotateRight(this.internalState[var6] ^ this.internalState[var3], 8);
      this.internalState[var5] = this.internalState[var5] + this.internalState[var6];
      this.internalState[var4] = Integers.rotateRight(this.internalState[var4] ^ this.internalState[var5], 7);
   }

   protected void setAsLastNode() {
      this.isLastNode = true;
   }

   @Override
   public String getAlgorithmName() {
      return "BLAKE2s";
   }

   @Override
   public int getDigestSize() {
      return this.digestLength;
   }

   @Override
   public int getByteLength() {
      return 64;
   }

   public void clearKey() {
      if (this.key != null) {
         Arrays.fill(this.key, (byte)0);
         Arrays.fill(this.buffer, (byte)0);
      }
   }

   public void clearSalt() {
      if (this.salt != null) {
         Arrays.fill(this.salt, (byte)0);
      }
   }
}
