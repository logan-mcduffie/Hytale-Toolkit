package org.bouncycastle.crypto.modes;

import java.util.Vector;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Longs;

public class OCBBlockCipher implements AEADBlockCipher {
   private static final int BLOCK_SIZE = 16;
   private BlockCipher hashCipher;
   private BlockCipher mainCipher;
   private boolean forEncryption;
   private int macSize;
   private byte[] initialAssociatedText;
   private Vector L;
   private byte[] L_Asterisk;
   private byte[] L_Dollar;
   private byte[] KtopInput = null;
   private byte[] Stretch = new byte[24];
   private byte[] OffsetMAIN_0 = new byte[16];
   private byte[] hashBlock;
   private byte[] mainBlock;
   private int hashBlockPos;
   private int mainBlockPos;
   private long hashBlockCount;
   private long mainBlockCount;
   private byte[] OffsetHASH;
   private byte[] Sum;
   private byte[] OffsetMAIN = new byte[16];
   private byte[] Checksum;
   private byte[] macBlock;

   public OCBBlockCipher(BlockCipher var1, BlockCipher var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("'hashCipher' cannot be null");
      } else if (var1.getBlockSize() != 16) {
         throw new IllegalArgumentException("'hashCipher' must have a block size of 16");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'mainCipher' cannot be null");
      } else if (var2.getBlockSize() != 16) {
         throw new IllegalArgumentException("'mainCipher' must have a block size of 16");
      } else if (!var1.getAlgorithmName().equals(var2.getAlgorithmName())) {
         throw new IllegalArgumentException("'hashCipher' and 'mainCipher' must be the same algorithm");
      } else {
         this.hashCipher = var1;
         this.mainCipher = var2;
      }
   }

   @Override
   public BlockCipher getUnderlyingCipher() {
      return this.mainCipher;
   }

   @Override
   public String getAlgorithmName() {
      return this.mainCipher.getAlgorithmName() + "/OCB";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      boolean var3 = this.forEncryption;
      this.forEncryption = var1;
      this.macBlock = null;
      KeyParameter var4;
      byte[] var5;
      if (var2 instanceof AEADParameters) {
         AEADParameters var6 = (AEADParameters)var2;
         var5 = var6.getNonce();
         this.initialAssociatedText = var6.getAssociatedText();
         int var7 = var6.getMacSize();
         if (var7 < 64 || var7 > 128 || var7 % 8 != 0) {
            throw new IllegalArgumentException("Invalid value for MAC size: " + var7);
         }

         this.macSize = var7 / 8;
         var4 = var6.getKey();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to OCB");
         }

         ParametersWithIV var12 = (ParametersWithIV)var2;
         var5 = var12.getIV();
         this.initialAssociatedText = null;
         this.macSize = 16;
         var4 = (KeyParameter)var12.getParameters();
      }

      this.hashBlock = new byte[16];
      this.mainBlock = new byte[var1 ? 16 : 16 + this.macSize];
      if (var5 == null) {
         var5 = new byte[0];
      }

      if (var5.length > 15) {
         throw new IllegalArgumentException("IV must be no more than 15 bytes");
      } else {
         if (var4 != null) {
            this.hashCipher.init(true, var4);
            this.mainCipher.init(var1, var4);
            this.KtopInput = null;
         } else if (var3 != var1) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
         }

         this.L_Asterisk = new byte[16];
         this.hashCipher.processBlock(this.L_Asterisk, 0, this.L_Asterisk, 0);
         this.L_Dollar = OCB_double(this.L_Asterisk);
         this.L = new Vector();
         this.L.addElement(OCB_double(this.L_Dollar));
         int var13 = this.processNonce(var5);
         int var14 = var13 % 8;
         int var8 = var13 / 8;
         if (var14 == 0) {
            System.arraycopy(this.Stretch, var8, this.OffsetMAIN_0, 0, 16);
         } else {
            for (int var9 = 0; var9 < 16; var9++) {
               int var10 = this.Stretch[var8] & 255;
               int var11 = this.Stretch[++var8] & 255;
               this.OffsetMAIN_0[var9] = (byte)(var10 << var14 | var11 >>> 8 - var14);
            }
         }

         this.hashBlockPos = 0;
         this.mainBlockPos = 0;
         this.hashBlockCount = 0L;
         this.mainBlockCount = 0L;
         this.OffsetHASH = new byte[16];
         this.Sum = new byte[16];
         System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
         this.Checksum = new byte[16];
         if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
         }
      }
   }

   protected int processNonce(byte[] var1) {
      byte[] var2 = new byte[16];
      System.arraycopy(var1, 0, var2, var2.length - var1.length, var1.length);
      var2[0] = (byte)(this.macSize << 4);
      var2[15 - var1.length] = (byte)(var2[15 - var1.length] | 1);
      int var3 = var2[15] & 63;
      var2[15] = (byte)(var2[15] & 192);
      if (this.KtopInput == null || !Arrays.areEqual(var2, this.KtopInput)) {
         byte[] var4 = new byte[16];
         this.KtopInput = var2;
         this.hashCipher.processBlock(this.KtopInput, 0, var4, 0);
         System.arraycopy(var4, 0, this.Stretch, 0, 16);

         for (int var5 = 0; var5 < 8; var5++) {
            this.Stretch[16 + var5] = (byte)(var4[var5] ^ var4[var5 + 1]);
         }
      }

      return var3;
   }

   @Override
   public byte[] getMac() {
      return this.macBlock == null ? new byte[this.macSize] : Arrays.clone(this.macBlock);
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = var1 + this.mainBlockPos;
      if (this.forEncryption) {
         return var2 + this.macSize;
      } else {
         return var2 < this.macSize ? 0 : var2 - this.macSize;
      }
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = var1 + this.mainBlockPos;
      if (!this.forEncryption) {
         if (var2 < this.macSize) {
            return 0;
         }

         var2 -= this.macSize;
      }

      return var2 - var2 % 16;
   }

   @Override
   public void processAADByte(byte var1) {
      this.hashBlock[this.hashBlockPos] = var1;
      if (++this.hashBlockPos == this.hashBlock.length) {
         this.processHashBlock();
      }
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         this.hashBlock[this.hashBlockPos] = var1[var2 + var4];
         if (++this.hashBlockPos == this.hashBlock.length) {
            this.processHashBlock();
         }
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      this.mainBlock[this.mainBlockPos] = var1;
      if (++this.mainBlockPos == this.mainBlock.length) {
         this.processMainBlock(var2, var3);
         return 16;
      } else {
         return 0;
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (var1.length < var2 + var3) {
         throw new DataLengthException("Input buffer too short");
      } else {
         byte var6 = 0;
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, this.getUpdateOutputSize(var3))) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         for (int var7 = 0; var7 < var3; var7++) {
            this.mainBlock[this.mainBlockPos] = var1[var2 + var7];
            if (++this.mainBlockPos == this.mainBlock.length) {
               this.processMainBlock(var4, var5 + var6);
               var6 += 16;
            }
         }

         return var6;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      byte[] var3 = null;
      if (!this.forEncryption) {
         if (this.mainBlockPos < this.macSize) {
            throw new InvalidCipherTextException("data too short");
         }

         this.mainBlockPos = this.mainBlockPos - this.macSize;
         var3 = new byte[this.macSize];
         System.arraycopy(this.mainBlock, this.mainBlockPos, var3, 0, this.macSize);
      }

      if (this.hashBlockPos > 0) {
         OCB_extend(this.hashBlock, this.hashBlockPos);
         this.updateHASH(this.L_Asterisk);
      }

      if (this.mainBlockPos > 0) {
         if (this.forEncryption) {
            OCB_extend(this.mainBlock, this.mainBlockPos);
            xor(this.Checksum, this.mainBlock);
         }

         xor(this.OffsetMAIN, this.L_Asterisk);
         byte[] var4 = new byte[16];
         this.hashCipher.processBlock(this.OffsetMAIN, 0, var4, 0);
         xor(this.mainBlock, var4);
         if (var1.length < var2 + this.mainBlockPos) {
            throw new OutputLengthException("Output buffer too short");
         }

         System.arraycopy(this.mainBlock, 0, var1, var2, this.mainBlockPos);
         if (!this.forEncryption) {
            OCB_extend(this.mainBlock, this.mainBlockPos);
            xor(this.Checksum, this.mainBlock);
         }
      }

      xor(this.Checksum, this.OffsetMAIN);
      xor(this.Checksum, this.L_Dollar);
      this.hashCipher.processBlock(this.Checksum, 0, this.Checksum, 0);
      xor(this.Checksum, this.Sum);
      this.macBlock = new byte[this.macSize];
      System.arraycopy(this.Checksum, 0, this.macBlock, 0, this.macSize);
      int var5 = this.mainBlockPos;
      if (this.forEncryption) {
         if (var1.length < var2 + var5 + this.macSize) {
            throw new OutputLengthException("Output buffer too short");
         }

         System.arraycopy(this.macBlock, 0, var1, var2 + var5, this.macSize);
         var5 += this.macSize;
      } else if (!Arrays.constantTimeAreEqual(this.macBlock, var3)) {
         throw new InvalidCipherTextException("mac check in OCB failed");
      }

      this.reset(false);
      return var5;
   }

   @Override
   public void reset() {
      this.reset(true);
   }

   protected void clear(byte[] var1) {
      if (var1 != null) {
         Arrays.fill(var1, (byte)0);
      }
   }

   protected byte[] getLSub(int var1) {
      while (var1 >= this.L.size()) {
         this.L.addElement(OCB_double((byte[])this.L.lastElement()));
      }

      return (byte[])this.L.elementAt(var1);
   }

   protected void processHashBlock() {
      this.updateHASH(this.getLSub(OCB_ntz(++this.hashBlockCount)));
      this.hashBlockPos = 0;
   }

   protected void processMainBlock(byte[] var1, int var2) {
      if (var1.length < var2 + 16) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            this.mainBlockPos = 0;
         }

         xor(this.OffsetMAIN, this.getLSub(OCB_ntz(++this.mainBlockCount)));
         xor(this.mainBlock, this.OffsetMAIN);
         this.mainCipher.processBlock(this.mainBlock, 0, this.mainBlock, 0);
         xor(this.mainBlock, this.OffsetMAIN);
         System.arraycopy(this.mainBlock, 0, var1, var2, 16);
         if (!this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            System.arraycopy(this.mainBlock, 16, this.mainBlock, 0, this.macSize);
            this.mainBlockPos = this.macSize;
         }
      }
   }

   protected void reset(boolean var1) {
      this.hashCipher.reset();
      this.mainCipher.reset();
      this.clear(this.hashBlock);
      this.clear(this.mainBlock);
      this.hashBlockPos = 0;
      this.mainBlockPos = 0;
      this.hashBlockCount = 0L;
      this.mainBlockCount = 0L;
      this.clear(this.OffsetHASH);
      this.clear(this.Sum);
      System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
      this.clear(this.Checksum);
      if (var1) {
         this.macBlock = null;
      }

      if (this.initialAssociatedText != null) {
         this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
      }
   }

   protected void updateHASH(byte[] var1) {
      xor(this.OffsetHASH, var1);
      xor(this.hashBlock, this.OffsetHASH);
      this.hashCipher.processBlock(this.hashBlock, 0, this.hashBlock, 0);
      xor(this.Sum, this.hashBlock);
   }

   protected static byte[] OCB_double(byte[] var0) {
      byte[] var1 = new byte[16];
      int var2 = shiftLeft(var0, var1);
      var1[15] = (byte)(var1[15] ^ 135 >>> (1 - var2 << 3));
      return var1;
   }

   protected static void OCB_extend(byte[] var0, int var1) {
      var0[var1] = -128;

      while (++var1 < 16) {
         var0[var1] = 0;
      }
   }

   protected static int OCB_ntz(long var0) {
      return Longs.numberOfTrailingZeros(var0);
   }

   protected static int shiftLeft(byte[] var0, byte[] var1) {
      int var2 = 16;
      int var3 = 0;

      while (--var2 >= 0) {
         int var4 = var0[var2] & 255;
         var1[var2] = (byte)(var4 << 1 | var3);
         var3 = var4 >>> 7 & 1;
      }

      return var3;
   }

   protected static void xor(byte[] var0, byte[] var1) {
      Bytes.xorTo(16, var1, var0);
   }
}
