package org.bouncycastle.crypto.engines;

import java.util.Arrays;
import org.bouncycastle.util.Bytes;

public class ElephantEngine extends AEADBaseEngine {
   private byte[] npub;
   private byte[] expanded_key;
   private int nb_its;
   private byte[] ad;
   private int adOff;
   private int adlen;
   private final byte[] tag_buffer;
   private byte[] previous_mask;
   private byte[] current_mask;
   private byte[] next_mask;
   private final byte[] buffer;
   private final byte[] previous_outputMessage;
   private final ElephantEngine.Permutation instance;

   public ElephantEngine(ElephantEngine.ElephantParameters var1) {
      this.KEY_SIZE = 16;
      this.IV_SIZE = 12;
      switch (var1) {
         case elephant160:
            this.BlockSize = 20;
            this.instance = new ElephantEngine.Dumbo();
            this.MAC_SIZE = 8;
            this.algorithmName = "Elephant 160 AEAD";
            break;
         case elephant176:
            this.BlockSize = 22;
            this.instance = new ElephantEngine.Jumbo();
            this.algorithmName = "Elephant 176 AEAD";
            this.MAC_SIZE = 8;
            break;
         case elephant200:
            this.BlockSize = 25;
            this.instance = new ElephantEngine.Delirium();
            this.algorithmName = "Elephant 200 AEAD";
            this.MAC_SIZE = 16;
            break;
         default:
            throw new IllegalArgumentException("Invalid parameter settings for Elephant");
      }

      this.tag_buffer = new byte[this.BlockSize];
      this.previous_mask = new byte[this.BlockSize];
      this.current_mask = new byte[this.BlockSize];
      this.next_mask = new byte[this.BlockSize];
      this.buffer = new byte[this.BlockSize];
      this.previous_outputMessage = new byte[this.BlockSize];
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Stream, AEADBaseEngine.DataOperatorType.Counter);
   }

   private byte rotl(byte var1) {
      return (byte)(var1 << 1 | (var1 & 255) >>> 7);
   }

   private void lfsr_step() {
      this.instance.lfsr_step();
      System.arraycopy(this.current_mask, 1, this.next_mask, 0, this.BlockSize - 1);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.npub = var2;
      this.expanded_key = new byte[this.BlockSize];
      System.arraycopy(var1, 0, this.expanded_key, 0, this.KEY_SIZE);
      this.instance.permutation(this.expanded_key);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.processBuffer(var1, var2, var3, var4, AEADBaseEngine.State.EncData);
      System.arraycopy(var3, var4, this.previous_outputMessage, 0, this.BlockSize);
   }

   private void processBuffer(byte[] var1, int var2, byte[] var3, int var4, AEADBaseEngine.State var5) {
      if (this.m_state == AEADBaseEngine.State.DecInit || this.m_state == AEADBaseEngine.State.EncInit) {
         this.processFinalAAD();
      }

      this.lfsr_step();
      this.computeCipherBlock(var1, var2, this.BlockSize, var3, var4);
      if (this.nb_its > 0) {
         System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, this.BlockSize);
         this.absorbCiphertext();
      }

      if (this.m_state != var5) {
         this.absorbAAD();
      }

      this.swapMasks();
      this.nb_its++;
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.processBuffer(var1, var2, var3, var4, AEADBaseEngine.State.DecData);
      System.arraycopy(var1, var2, this.previous_outputMessage, 0, this.BlockSize);
   }

   private void computeCipherBlock(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      System.arraycopy(this.npub, 0, this.buffer, 0, this.IV_SIZE);
      Arrays.fill(this.buffer, this.IV_SIZE, this.BlockSize, (byte)0);
      xorTo(this.BlockSize, this.current_mask, this.next_mask, this.buffer);
      this.instance.permutation(this.buffer);
      xorTo(this.BlockSize, this.current_mask, this.next_mask, this.buffer);
      Bytes.xorTo(var3, var1, var2, this.buffer);
      System.arraycopy(this.buffer, 0, var4, var5, var3);
   }

   private void swapMasks() {
      byte[] var1 = this.previous_mask;
      this.previous_mask = this.current_mask;
      this.current_mask = this.next_mask;
      this.next_mask = var1;
   }

   private void absorbAAD() {
      this.processAADBytes(this.buffer);
      Bytes.xorTo(this.BlockSize, this.next_mask, this.buffer);
      this.instance.permutation(this.buffer);
      Bytes.xorTo(this.BlockSize, this.next_mask, this.buffer);
      Bytes.xorTo(this.BlockSize, this.buffer, this.tag_buffer);
   }

   private void absorbCiphertext() {
      xorTo(this.BlockSize, this.previous_mask, this.next_mask, this.buffer);
      this.instance.permutation(this.buffer);
      xorTo(this.BlockSize, this.previous_mask, this.next_mask, this.buffer);
      Bytes.xorTo(this.BlockSize, this.buffer, this.tag_buffer);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      int var3 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
      this.processFinalAAD();
      int var4 = 1 + var3 / this.BlockSize;
      int var5 = var3 % this.BlockSize != 0 ? var4 : var4 - 1;
      int var6 = 1 + (this.IV_SIZE + this.adlen) / this.BlockSize;
      int var7 = Math.max(var4 + 1, var6 - 1);
      this.processBytes(this.m_buf, var1, var2, var7, var5, var4, var3, var6);
      Bytes.xorTo(this.BlockSize, this.expanded_key, this.tag_buffer);
      this.instance.permutation(this.tag_buffer);
      Bytes.xorTo(this.BlockSize, this.expanded_key, this.tag_buffer);
      System.arraycopy(this.tag_buffer, 0, this.mac, 0, this.MAC_SIZE);
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      switch (this.m_state.ord) {
         case 0:
            throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
         case 1:
         case 2:
         case 3:
            int var3 = this.m_bufPos + var1;
            return var3 - var3 % this.BlockSize;
         case 4:
         case 8:
            return 0;
         case 5:
         case 6:
         case 7:
            int var2 = Math.max(0, this.m_bufPos + var1 - this.MAC_SIZE);
            return var2 - var2 % this.BlockSize;
         default:
            return Math.max(0, var1 + this.m_bufPos - this.MAC_SIZE);
      }
   }

   @Override
   public int getOutputSize(int var1) {
      switch (this.m_state.ord) {
         case 0:
            throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
         case 1:
         case 2:
         case 3:
            return var1 + this.m_bufPos + this.MAC_SIZE;
         case 4:
         case 8:
            return 0;
         case 5:
         case 6:
         case 7:
         default:
            return Math.max(0, var1 + this.m_bufPos - this.MAC_SIZE);
      }
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD2(var1);
   }

   @Override
   protected void processFinalAAD() {
      if (this.adOff == -1) {
         this.ad = ((AEADBaseEngine.StreamAADOperator)this.aadOperator).getBytes();
         this.adOff = 0;
         this.adlen = this.aadOperator.getLen();
         this.aadOperator.reset();
      }

      switch (this.m_state.ord) {
         case 1:
         case 5:
            this.processAADBytes(this.tag_buffer);
      }
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      Arrays.fill(this.tag_buffer, (byte)0);
      Arrays.fill(this.previous_outputMessage, (byte)0);
      this.nb_its = 0;
      this.adOff = -1;
   }

   @Override
   protected void checkAAD() {
      switch (this.m_state.ord) {
         case 3:
            throw new IllegalArgumentException(
               this.algorithmName + " cannot process AAD when the length of the ciphertext to be processed exceeds the a block size"
            );
         case 4:
            throw new IllegalArgumentException(this.algorithmName + " cannot be reused for encryption");
         case 5:
         case 6:
         default:
            return;
         case 7:
            throw new IllegalArgumentException(
               this.algorithmName + " cannot process AAD when the length of the plaintext to be processed exceeds the a block size"
            );
      }
   }

   @Override
   protected boolean checkData(boolean var1) {
      switch (this.m_state.ord) {
         case 1:
         case 2:
         case 3:
            return true;
         case 4:
            throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
         case 5:
         case 6:
         case 7:
            return false;
         default:
            throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
      }
   }

   private void processAADBytes(byte[] var1) {
      int var2 = 0;
      switch (this.m_state.ord) {
         case 1:
            System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BlockSize);
            System.arraycopy(this.npub, 0, var1, 0, this.IV_SIZE);
            var2 += this.IV_SIZE;
            this.m_state = AEADBaseEngine.State.EncAad;
            break;
         case 2:
         case 6:
            if (this.adOff == this.adlen) {
               Arrays.fill(var1, 0, this.BlockSize, (byte)0);
               var1[0] = 1;
               return;
            }
         case 3:
         case 4:
         default:
            break;
         case 5:
            System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BlockSize);
            System.arraycopy(this.npub, 0, var1, 0, this.IV_SIZE);
            var2 += this.IV_SIZE;
            this.m_state = AEADBaseEngine.State.DecAad;
      }

      int var3 = this.BlockSize - var2;
      int var4 = this.adlen - this.adOff;
      if (var3 <= var4) {
         System.arraycopy(this.ad, this.adOff, var1, var2, var3);
         this.adOff += var3;
      } else {
         if (var4 > 0) {
            System.arraycopy(this.ad, this.adOff, var1, var2, var4);
            this.adOff += var4;
         }

         Arrays.fill(var1, var2 + var4, var2 + var3, (byte)0);
         var1[var2 + var4] = 1;
         switch (this.m_state.ord) {
            case 2:
               this.m_state = AEADBaseEngine.State.EncData;
               break;
            case 6:
               this.m_state = AEADBaseEngine.State.DecData;
         }
      }
   }

   private void processBytes(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      int var9 = 0;
      byte[] var10 = new byte[this.BlockSize];

      int var11;
      for (var11 = this.nb_its; var11 < var4; var11++) {
         int var12 = var11 == var5 - 1 ? var7 - var11 * this.BlockSize : this.BlockSize;
         this.lfsr_step();
         if (var11 < var5) {
            this.computeCipherBlock(var1, var9, var12, var2, var3);
            if (this.forEncryption) {
               System.arraycopy(this.buffer, 0, var10, 0, var12);
            } else {
               System.arraycopy(var1, var9, var10, 0, var12);
            }

            var3 += var12;
            var9 += var12;
         }

         if (var11 > 0 && var11 <= var6) {
            int var13 = (var11 - 1) * this.BlockSize;
            if (var13 == var7) {
               Arrays.fill(this.buffer, 1, this.BlockSize, (byte)0);
               this.buffer[0] = 1;
            } else {
               int var14 = var7 - var13;
               if (this.BlockSize <= var14) {
                  System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, this.BlockSize);
               } else if (var14 > 0) {
                  System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, var14);
                  Arrays.fill(this.buffer, var14, this.BlockSize, (byte)0);
                  this.buffer[var14] = 1;
               }
            }

            this.absorbCiphertext();
         }

         if (var11 + 1 < var8) {
            this.absorbAAD();
         }

         this.swapMasks();
         System.arraycopy(var10, 0, this.previous_outputMessage, 0, this.BlockSize);
      }

      this.nb_its = var11;
   }

   public static void xorTo(int var0, byte[] var1, byte[] var2, byte[] var3) {
      for (int var4 = 0; var4 < var0; var4++) {
         var3[var4] = (byte)(var3[var4] ^ var1[var4] ^ var2[var4]);
      }
   }

   private class Delirium implements ElephantEngine.Permutation {
      private static final int nRounds = 18;
      private final byte[] KeccakRoundConstants = new byte[]{1, -126, -118, 0, -117, 1, -127, 9, -118, -120, 9, 10, -117, -117, -119, 3, 2, -128};
      private final int[] KeccakRhoOffsets = new int[]{0, 1, 6, 4, 3, 4, 4, 6, 7, 4, 3, 2, 3, 1, 7, 1, 5, 7, 5, 0, 2, 2, 5, 0, 6};

      private Delirium() {
      }

      @Override
      public void permutation(byte[] var1) {
         for (int var2 = 0; var2 < 18; var2++) {
            this.KeccakP200Round(var1, var2);
         }
      }

      @Override
      public void lfsr_step() {
         ElephantEngine.this.next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(
            ElephantEngine.this.rotl(ElephantEngine.this.current_mask[0])
               ^ ElephantEngine.this.rotl(ElephantEngine.this.current_mask[2])
               ^ ElephantEngine.this.current_mask[13] << 1
         );
      }

      private void KeccakP200Round(byte[] var1, int var2) {
         byte[] var5 = new byte[25];

         for (int var3 = 0; var3 < 5; var3++) {
            for (int var4 = 0; var4 < 5; var4++) {
               var5[var3] ^= var1[this.index(var3, var4)];
            }
         }

         for (int var6 = 0; var6 < 5; var6++) {
            var5[var6 + 5] = (byte)(this.ROL8(var5[(var6 + 1) % 5], 1) ^ var5[(var6 + 4) % 5]);
         }

         for (int var7 = 0; var7 < 5; var7++) {
            for (int var12 = 0; var12 < 5; var12++) {
               var1[this.index(var7, var12)] ^= var5[var7 + 5];
            }
         }

         for (int var8 = 0; var8 < 5; var8++) {
            for (int var13 = 0; var13 < 5; var13++) {
               var5[this.index(var8, var13)] = this.ROL8(var1[this.index(var8, var13)], this.KeccakRhoOffsets[this.index(var8, var13)]);
            }
         }

         for (int var9 = 0; var9 < 5; var9++) {
            for (int var14 = 0; var14 < 5; var14++) {
               var1[this.index(var14, (2 * var9 + 3 * var14) % 5)] = var5[this.index(var9, var14)];
            }
         }

         for (int var15 = 0; var15 < 5; var15++) {
            for (int var10 = 0; var10 < 5; var10++) {
               var5[var10] = (byte)(var1[this.index(var10, var15)] ^ ~var1[this.index((var10 + 1) % 5, var15)] & var1[this.index((var10 + 2) % 5, var15)]);
            }

            for (int var11 = 0; var11 < 5; var11++) {
               var1[this.index(var11, var15)] = var5[var11];
            }
         }

         var1[0] ^= this.KeccakRoundConstants[var2];
      }

      private byte ROL8(byte var1, int var2) {
         return (byte)(var1 << var2 | (var1 & 255) >>> 8 - var2);
      }

      private int index(int var1, int var2) {
         return var1 + var2 * 5;
      }
   }

   private class Dumbo extends ElephantEngine.Spongent {
      public Dumbo() {
         super(160, 20, 80, (byte)117);
      }

      @Override
      public void lfsr_step() {
         ElephantEngine.this.next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(
            ((ElephantEngine.this.current_mask[0] & 255) << 3 | (ElephantEngine.this.current_mask[0] & 255) >>> 5)
               ^ (ElephantEngine.this.current_mask[3] & 255) << 7
               ^ (ElephantEngine.this.current_mask[13] & 255) >>> 7
         );
      }
   }

   public static enum ElephantParameters {
      elephant160,
      elephant176,
      elephant200;
   }

   private class Jumbo extends ElephantEngine.Spongent {
      public Jumbo() {
         super(176, 22, 90, (byte)69);
      }

      @Override
      public void lfsr_step() {
         ElephantEngine.this.next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(
            ElephantEngine.this.rotl(ElephantEngine.this.current_mask[0])
               ^ (ElephantEngine.this.current_mask[3] & 255) << 7
               ^ (ElephantEngine.this.current_mask[19] & 255) >>> 7
         );
      }
   }

   private interface Permutation {
      void permutation(byte[] var1);

      void lfsr_step();
   }

   private abstract static class Spongent implements ElephantEngine.Permutation {
      private final byte lfsrIV;
      private final int nRounds;
      private final int nBits;
      private final int nSBox;
      private final byte[] sBoxLayer = new byte[]{
         -18,
         -19,
         -21,
         -32,
         -30,
         -31,
         -28,
         -17,
         -25,
         -22,
         -24,
         -27,
         -23,
         -20,
         -29,
         -26,
         -34,
         -35,
         -37,
         -48,
         -46,
         -47,
         -44,
         -33,
         -41,
         -38,
         -40,
         -43,
         -39,
         -36,
         -45,
         -42,
         -66,
         -67,
         -69,
         -80,
         -78,
         -79,
         -76,
         -65,
         -73,
         -70,
         -72,
         -75,
         -71,
         -68,
         -77,
         -74,
         14,
         13,
         11,
         0,
         2,
         1,
         4,
         15,
         7,
         10,
         8,
         5,
         9,
         12,
         3,
         6,
         46,
         45,
         43,
         32,
         34,
         33,
         36,
         47,
         39,
         42,
         40,
         37,
         41,
         44,
         35,
         38,
         30,
         29,
         27,
         16,
         18,
         17,
         20,
         31,
         23,
         26,
         24,
         21,
         25,
         28,
         19,
         22,
         78,
         77,
         75,
         64,
         66,
         65,
         68,
         79,
         71,
         74,
         72,
         69,
         73,
         76,
         67,
         70,
         -2,
         -3,
         -5,
         -16,
         -14,
         -15,
         -12,
         -1,
         -9,
         -6,
         -8,
         -11,
         -7,
         -4,
         -13,
         -10,
         126,
         125,
         123,
         112,
         114,
         113,
         116,
         127,
         119,
         122,
         120,
         117,
         121,
         124,
         115,
         118,
         -82,
         -83,
         -85,
         -96,
         -94,
         -95,
         -92,
         -81,
         -89,
         -86,
         -88,
         -91,
         -87,
         -84,
         -93,
         -90,
         -114,
         -115,
         -117,
         -128,
         -126,
         -127,
         -124,
         -113,
         -121,
         -118,
         -120,
         -123,
         -119,
         -116,
         -125,
         -122,
         94,
         93,
         91,
         80,
         82,
         81,
         84,
         95,
         87,
         90,
         88,
         85,
         89,
         92,
         83,
         86,
         -98,
         -99,
         -101,
         -112,
         -110,
         -111,
         -108,
         -97,
         -105,
         -102,
         -104,
         -107,
         -103,
         -100,
         -109,
         -106,
         -50,
         -51,
         -53,
         -64,
         -62,
         -63,
         -60,
         -49,
         -57,
         -54,
         -56,
         -59,
         -55,
         -52,
         -61,
         -58,
         62,
         61,
         59,
         48,
         50,
         49,
         52,
         63,
         55,
         58,
         56,
         53,
         57,
         60,
         51,
         54,
         110,
         109,
         107,
         96,
         98,
         97,
         100,
         111,
         103,
         106,
         104,
         101,
         105,
         108,
         99,
         102
      };

      public Spongent(int var1, int var2, int var3, byte var4) {
         this.nRounds = var3;
         this.nSBox = var2;
         this.lfsrIV = var4;
         this.nBits = var1;
      }

      @Override
      public void permutation(byte[] var1) {
         byte var2 = this.lfsrIV;
         byte[] var3 = new byte[this.nSBox];

         for (int var4 = 0; var4 < this.nRounds; var4++) {
            var1[0] ^= var2;
            var1[this.nSBox - 1] = (byte)(
               var1[this.nSBox - 1]
                  ^ (byte)(
                     (var2 & 1) << 7
                        | (var2 & 2) << 5
                        | (var2 & 4) << 3
                        | (var2 & 8) << 1
                        | (var2 & 16) >>> 1
                        | (var2 & 32) >>> 3
                        | (var2 & 64) >>> 5
                        | (var2 & 128) >>> 7
                  )
            );
            var2 = (byte)((var2 << 1 | (64 & var2) >>> 6 ^ (32 & var2) >>> 5) & 127);

            for (int var5 = 0; var5 < this.nSBox; var5++) {
               var1[var5] = this.sBoxLayer[var1[var5] & 255];
            }

            Arrays.fill(var3, (byte)0);

            for (int var6 = 0; var6 < this.nSBox; var6++) {
               for (int var7 = 0; var7 < 8; var7++) {
                  int var8 = (var6 << 3) + var7;
                  if (var8 != this.nBits - 1) {
                     var8 = (var8 * this.nBits >> 2) % (this.nBits - 1);
                  }

                  var3[var8 >>> 3] = (byte)(var3[var8 >>> 3] ^ ((var1[var6] & 255) >>> var7 & 1) << (var8 & 7));
               }
            }

            System.arraycopy(var3, 0, var1, 0, this.nSBox);
         }
      }
   }
}
