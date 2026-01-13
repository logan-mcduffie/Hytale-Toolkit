package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.PhotonBeetleDigest;
import org.bouncycastle.util.Bytes;

public class PhotonBeetleEngine extends AEADBaseEngine {
   private boolean input_empty;
   private byte[] K;
   private byte[] N;
   private byte[] state;
   private final int RATE_INBYTES_HALF;
   private final int STATE_INBYTES;
   private final int LAST_THREE_BITS_OFFSET;
   private static final int D = 8;
   private static final byte[][] RC = new byte[][]{
      {1, 3, 7, 14, 13, 11, 6, 12, 9, 2, 5, 10},
      {0, 2, 6, 15, 12, 10, 7, 13, 8, 3, 4, 11},
      {2, 0, 4, 13, 14, 8, 5, 15, 10, 1, 6, 9},
      {6, 4, 0, 9, 10, 12, 1, 11, 14, 5, 2, 13},
      {14, 12, 8, 1, 2, 4, 9, 3, 6, 13, 10, 5},
      {15, 13, 9, 0, 3, 5, 8, 2, 7, 12, 11, 4},
      {13, 15, 11, 2, 1, 7, 10, 0, 5, 14, 9, 6},
      {9, 11, 15, 6, 5, 3, 14, 4, 1, 10, 13, 2}
   };
   private static final byte[][] MixColMatrix = new byte[][]{
      {2, 4, 2, 11, 2, 8, 5, 6},
      {12, 9, 8, 13, 7, 7, 5, 2},
      {4, 4, 13, 13, 9, 4, 13, 9},
      {1, 6, 5, 1, 12, 13, 15, 14},
      {15, 12, 9, 13, 14, 5, 14, 13},
      {9, 14, 5, 15, 4, 12, 9, 6},
      {12, 2, 2, 10, 3, 1, 1, 14},
      {15, 1, 13, 10, 5, 10, 2, 3}
   };
   private static final byte[] sbox = new byte[]{12, 5, 6, 11, 9, 0, 10, 13, 3, 14, 15, 8, 4, 7, 1, 2};

   public PhotonBeetleEngine(PhotonBeetleEngine.PhotonBeetleParameters var1) {
      this.KEY_SIZE = this.IV_SIZE = this.MAC_SIZE = 16;
      short var2 = 0;
      short var3 = 0;
      switch (var1) {
         case pb32:
            var3 = 32;
            var2 = 224;
            break;
         case pb128:
            var3 = 128;
            var2 = 128;
      }

      this.AADBufferSize = this.BlockSize = var3 + 7 >>> 3;
      this.RATE_INBYTES_HALF = this.BlockSize >>> 1;
      int var4 = var3 + var2;
      this.STATE_INBYTES = var4 + 7 >>> 3;
      this.LAST_THREE_BITS_OFFSET = var4 - (this.STATE_INBYTES - 1 << 3) - 3;
      this.algorithmName = "Photon-Beetle AEAD";
      this.state = new byte[this.STATE_INBYTES];
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Counter, AEADBaseEngine.DataOperatorType.Counter);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.K = var1;
      this.N = var2;
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      photonPermutation(this.state);
      Bytes.xorTo(this.BlockSize, var1, var2, this.state);
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD3(var1, var2);
   }

   @Override
   protected void processFinalAAD() {
      int var1 = this.aadOperator.getLen();
      if (var1 != 0) {
         if (this.m_aadPos != 0) {
            photonPermutation(this.state);
            Bytes.xorTo(this.m_aadPos, this.m_aad, this.state);
            if (this.m_aadPos < this.BlockSize) {
               this.state[this.m_aadPos] = (byte)(this.state[this.m_aadPos] ^ 1);
            }
         }

         this.state[this.STATE_INBYTES - 1] = (byte)(
            this.state[this.STATE_INBYTES - 1]
               ^ this.select(this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE) > 0, var1 % this.BlockSize == 0, (byte)3, (byte)4)
                  << this.LAST_THREE_BITS_OFFSET
         );
      }
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.rhoohr(var3, var4, var1, var2, this.BlockSize);
      Bytes.xorTo(this.BlockSize, var1, var2, this.state);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.rhoohr(var3, var4, var1, var2, this.BlockSize);
      Bytes.xorTo(this.BlockSize, var3, var4, this.state);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      int var3 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
      int var4 = this.m_bufPos;
      int var5 = this.aadOperator.getLen();
      if (var5 != 0 || var3 != 0) {
         this.input_empty = false;
      }

      byte var6 = this.select(var5 != 0, var3 % this.BlockSize == 0, (byte)5, (byte)6);
      if (var3 != 0) {
         if (var4 != 0) {
            this.rhoohr(var1, var2, this.m_buf, 0, var4);
            if (this.forEncryption) {
               Bytes.xorTo(var4, this.m_buf, this.state);
            } else {
               Bytes.xorTo(var4, var1, var2, this.state);
            }

            if (var4 < this.BlockSize) {
               this.state[var4] = (byte)(this.state[var4] ^ 1);
            }
         }

         this.state[this.STATE_INBYTES - 1] = (byte)(this.state[this.STATE_INBYTES - 1] ^ var6 << this.LAST_THREE_BITS_OFFSET);
      } else if (this.input_empty) {
         this.state[this.STATE_INBYTES - 1] = (byte)(this.state[this.STATE_INBYTES - 1] ^ 1 << this.LAST_THREE_BITS_OFFSET);
      }

      photonPermutation(this.state);
      System.arraycopy(this.state, 0, this.mac, 0, this.MAC_SIZE);
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      this.input_empty = true;
      System.arraycopy(this.K, 0, this.state, 0, this.K.length);
      System.arraycopy(this.N, 0, this.state, this.K.length, this.N.length);
   }

   private static void photonPermutation(byte[] var0) {
      byte var4 = 3;
      byte var5 = 7;
      byte var6 = 64;
      byte[][] var7 = new byte[8][8];

      for (int var1 = 0; var1 < var6; var1++) {
         var7[var1 >>> var4][var1 & var5] = (byte)((var0[var1 >> 1] & 255) >>> 4 * (var1 & 1) & 15);
      }

      byte var8 = 12;

      for (int var9 = 0; var9 < var8; var9++) {
         for (int var13 = 0; var13 < 8; var13++) {
            var7[var13][0] = (byte)(var7[var13][0] ^ RC[var13][var9]);
         }

         for (int var14 = 0; var14 < 8; var14++) {
            for (int var2 = 0; var2 < 8; var2++) {
               var7[var14][var2] = sbox[var7[var14][var2]];
            }
         }

         for (int var15 = 1; var15 < 8; var15++) {
            System.arraycopy(var7[var15], 0, var0, 0, 8);
            System.arraycopy(var0, var15, var7[var15], 0, 8 - var15);
            System.arraycopy(var0, 0, var7[var15], 8 - var15, var15);
         }

         for (int var19 = 0; var19 < 8; var19++) {
            for (int var16 = 0; var16 < 8; var16++) {
               int var10 = 0;

               for (int var3 = 0; var3 < 8; var3++) {
                  byte var11 = MixColMatrix[var16][var3];
                  byte var12 = var7[var3][var19];
                  var10 ^= var11 * (var12 & 1);
                  var10 ^= var11 * (var12 & 2);
                  var10 ^= var11 * (var12 & 4);
                  var10 ^= var11 * (var12 & 8);
               }

               int var25 = var10 >>> 4;
               var10 = var10 & 15 ^ var25 ^ var25 << 1;
               int var26 = var10 >>> 4;
               var10 = var10 & 15 ^ var26 ^ var26 << 1;
               var0[var16] = (byte)var10;
            }

            for (int var17 = 0; var17 < 8; var17++) {
               var7[var17][var19] = var0[var17];
            }
         }
      }

      for (byte var18 = 0; var18 < var6; var18 += 2) {
         var0[var18 >>> 1] = (byte)(var7[var18 >>> var4][var18 & var5] & 15 | (var7[var18 >>> var4][var18 + 1 & var5] & 15) << 4);
      }
   }

   private byte select(boolean var1, boolean var2, byte var3, byte var4) {
      if (var1 && var2) {
         return 1;
      } else if (var1) {
         return 2;
      } else {
         return var2 ? var3 : var4;
      }
   }

   private void rhoohr(byte[] var1, int var2, byte[] var3, int var4, int var5) {
      photonPermutation(this.state);
      byte[] var6 = new byte[8];
      int var8 = Math.min(var5, this.RATE_INBYTES_HALF);

      int var7;
      for (var7 = 0; var7 < this.RATE_INBYTES_HALF - 1; var7++) {
         var6[var7] = (byte)((this.state[var7] & 255) >>> 1 | (this.state[var7 + 1] & 1) << 7);
      }

      var6[this.RATE_INBYTES_HALF - 1] = (byte)((this.state[var7] & 255) >>> 1 | (this.state[0] & 1) << 7);
      Bytes.xor(var8, this.state, this.RATE_INBYTES_HALF, var3, var4, var1, var2);
      Bytes.xor(var5 - var8, var6, var8 - this.RATE_INBYTES_HALF, var3, var4 + var8, var1, var2 + var8);
   }

   public static void photonPermutation(PhotonBeetleDigest.Friend var0, byte[] var1) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by PhotonBeetleDigest");
      } else {
         photonPermutation(var1);
      }
   }

   public static enum PhotonBeetleParameters {
      pb32,
      pb128;
   }
}
