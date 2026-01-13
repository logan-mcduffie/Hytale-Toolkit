package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Bytes;

public class GiftCofbEngine extends AEADBaseEngine {
   private byte[] npub;
   private byte[] k;
   private byte[] Y;
   private byte[] input;
   private byte[] offset;
   private static final byte[] GIFT_RC = new byte[]{
      1, 3, 7, 15, 31, 62, 61, 59, 55, 47, 30, 60, 57, 51, 39, 14, 29, 58, 53, 43, 22, 44, 24, 48, 33, 2, 5, 11, 23, 46, 28, 56, 49, 35, 6, 13, 27, 54, 45, 26
   };

   public GiftCofbEngine() {
      this.AADBufferSize = this.BlockSize = this.MAC_SIZE = this.IV_SIZE = this.KEY_SIZE = 16;
      this.algorithmName = "GIFT-COFB AEAD";
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
   }

   private int rowperm(int var1, int var2, int var3, int var4, int var5) {
      int var6 = 0;

      for (int var7 = 0; var7 < 8; var7++) {
         var6 |= (var1 >>> 4 * var7 & 1) << var7 + 8 * var2;
         var6 |= (var1 >>> 4 * var7 + 1 & 1) << var7 + 8 * var3;
         var6 |= (var1 >>> 4 * var7 + 2 & 1) << var7 + 8 * var4;
         var6 |= (var1 >>> 4 * var7 + 3 & 1) << var7 + 8 * var5;
      }

      return var6;
   }

   private void giftb128(byte[] var1, byte[] var2, byte[] var3) {
      int[] var6 = new int[4];
      short[] var7 = new short[8];
      var6[0] = (var1[0] & 255) << 24 | (var1[1] & 255) << 16 | (var1[2] & 255) << 8 | var1[3] & 255;
      var6[1] = (var1[4] & 255) << 24 | (var1[5] & 255) << 16 | (var1[6] & 255) << 8 | var1[7] & 255;
      var6[2] = (var1[8] & 255) << 24 | (var1[9] & 255) << 16 | (var1[10] & 255) << 8 | var1[11] & 255;
      var6[3] = (var1[12] & 255) << 24 | (var1[13] & 255) << 16 | (var1[14] & 255) << 8 | var1[15] & 255;
      var7[0] = (short)((var2[0] & 255) << 8 | var2[1] & 255);
      var7[1] = (short)((var2[2] & 255) << 8 | var2[3] & 255);
      var7[2] = (short)((var2[4] & 255) << 8 | var2[5] & 255);
      var7[3] = (short)((var2[6] & 255) << 8 | var2[7] & 255);
      var7[4] = (short)((var2[8] & 255) << 8 | var2[9] & 255);
      var7[5] = (short)((var2[10] & 255) << 8 | var2[11] & 255);
      var7[6] = (short)((var2[12] & 255) << 8 | var2[13] & 255);
      var7[7] = (short)((var2[14] & 255) << 8 | var2[15] & 255);

      for (int var4 = 0; var4 < 40; var4++) {
         var6[1] ^= var6[0] & var6[2];
         var6[0] ^= var6[1] & var6[3];
         var6[2] ^= var6[0] | var6[1];
         var6[3] ^= var6[2];
         var6[1] ^= var6[3];
         var6[3] = ~var6[3];
         var6[2] ^= var6[0] & var6[1];
         int var5 = var6[0];
         var6[0] = var6[3];
         var6[3] = var5;
         var6[0] = this.rowperm(var6[0], 0, 3, 2, 1);
         var6[1] = this.rowperm(var6[1], 1, 0, 3, 2);
         var6[2] = this.rowperm(var6[2], 2, 1, 0, 3);
         var6[3] = this.rowperm(var6[3], 3, 2, 1, 0);
         var6[2] ^= (var7[2] & '\uffff') << 16 | var7[3] & '\uffff';
         var6[1] ^= (var7[6] & '\uffff') << 16 | var7[7] & '\uffff';
         var6[3] ^= Integer.MIN_VALUE ^ GIFT_RC[var4] & 255;
         short var8 = (short)((var7[6] & '\uffff') >>> 2 | (var7[6] & '\uffff') << 14);
         short var9 = (short)((var7[7] & '\uffff') >>> 12 | (var7[7] & '\uffff') << 4);
         var7[7] = var7[5];
         var7[6] = var7[4];
         var7[5] = var7[3];
         var7[4] = var7[2];
         var7[3] = var7[1];
         var7[2] = var7[0];
         var7[1] = var9;
         var7[0] = var8;
      }

      var3[0] = (byte)(var6[0] >>> 24);
      var3[1] = (byte)(var6[0] >>> 16);
      var3[2] = (byte)(var6[0] >>> 8);
      var3[3] = (byte)var6[0];
      var3[4] = (byte)(var6[1] >>> 24);
      var3[5] = (byte)(var6[1] >>> 16);
      var3[6] = (byte)(var6[1] >>> 8);
      var3[7] = (byte)var6[1];
      var3[8] = (byte)(var6[2] >>> 24);
      var3[9] = (byte)(var6[2] >>> 16);
      var3[10] = (byte)(var6[2] >>> 8);
      var3[11] = (byte)var6[2];
      var3[12] = (byte)(var6[3] >>> 24);
      var3[13] = (byte)(var6[3] >>> 16);
      var3[14] = (byte)(var6[3] >>> 8);
      var3[15] = (byte)var6[3];
   }

   private void double_half_block(byte[] var1) {
      int var2 = ((var1[0] & 255) >>> 7) * 27;

      for (int var3 = 0; var3 < 7; var3++) {
         var1[var3] = (byte)((var1[var3] & 255) << 1 | (var1[var3 + 1] & 255) >>> 7);
      }

      var1[7] = (byte)((var1[7] & 255) << 1 ^ var2);
   }

   private void triple_half_block(byte[] var1) {
      byte[] var2 = new byte[8];

      for (int var3 = 0; var3 < 7; var3++) {
         var2[var3] = (byte)((var1[var3] & 255) << 1 | (var1[var3 + 1] & 255) >>> 7);
      }

      var2[7] = (byte)((var1[7] & 255) << 1 ^ ((var1[0] & 255) >>> 7) * 27);
      Bytes.xorTo(8, var2, var1);
   }

   private void pho1(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      byte[] var6 = new byte[16];
      byte[] var7 = new byte[16];
      if (var5 == 0) {
         var6[0] = -128;
      } else if (var5 < 16) {
         System.arraycopy(var3, var4, var6, 0, var5);
         var6[var5] = -128;
      } else {
         System.arraycopy(var3, var4, var6, 0, var5);
      }

      System.arraycopy(var2, 8, var7, 0, 8);

      for (int var8 = 0; var8 < 7; var8++) {
         var7[var8 + 8] = (byte)((var2[var8] & 255) << 1 | (var2[var8 + 1] & 255) >>> 7);
      }

      var7[15] = (byte)((var2[7] & 255) << 1 | (var2[0] & 255) >>> 7);
      System.arraycopy(var7, 0, var2, 0, 16);
      Bytes.xor(16, var2, var6, var1);
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      this.pho1(this.input, this.Y, var1, var2, 16);
      this.double_half_block(this.offset);
      Bytes.xorTo(8, this.offset, this.input);
      this.giftb128(this.input, this.k, this.Y);
   }

   @Override
   protected void processFinalAAD() {
      int var1 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
      this.triple_half_block(this.offset);
      if ((this.m_aadPos & 15) != 0 || this.m_state == AEADBaseEngine.State.DecInit || this.m_state == AEADBaseEngine.State.EncInit) {
         this.triple_half_block(this.offset);
      }

      if (var1 == 0) {
         this.triple_half_block(this.offset);
         this.triple_half_block(this.offset);
      }

      this.pho1(this.input, this.Y, this.m_aad, 0, this.m_aadPos);
      Bytes.xorTo(8, this.offset, this.input);
      this.giftb128(this.input, this.k, this.Y);
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD3(var1, var2);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) {
      this.npub = var2;
      this.k = var1;
      this.Y = new byte[this.BlockSize];
      this.input = new byte[16];
      this.offset = new byte[8];
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      int var3 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
      if (var3 != 0) {
         this.triple_half_block(this.offset);
         if ((var3 & 15) != 0) {
            this.triple_half_block(this.offset);
         }

         Bytes.xor(this.m_bufPos, this.Y, this.m_buf, 0, var1, var2);
         if (this.forEncryption) {
            this.pho1(this.input, this.Y, this.m_buf, 0, this.m_bufPos);
         } else {
            this.pho1(this.input, this.Y, var1, var2, this.m_bufPos);
         }

         Bytes.xorTo(8, this.offset, this.input);
         this.giftb128(this.input, this.k, this.Y);
      }

      System.arraycopy(this.Y, 0, this.mac, 0, this.BlockSize);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.double_half_block(this.offset);
      Bytes.xor(this.BlockSize, this.Y, var1, var2, var3, var4);
      this.pho1(this.input, this.Y, var1, var2, this.BlockSize);
      Bytes.xorTo(8, this.offset, this.input);
      this.giftb128(this.input, this.k, this.Y);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.double_half_block(this.offset);
      Bytes.xor(this.BlockSize, this.Y, var1, var2, var3, var4);
      this.pho1(this.input, this.Y, var3, var4, this.BlockSize);
      Bytes.xorTo(8, this.offset, this.input);
      this.giftb128(this.input, this.k, this.Y);
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      System.arraycopy(this.npub, 0, this.input, 0, this.IV_SIZE);
      this.giftb128(this.input, this.k, this.Y);
      System.arraycopy(this.Y, 0, this.offset, 0, 8);
   }
}
