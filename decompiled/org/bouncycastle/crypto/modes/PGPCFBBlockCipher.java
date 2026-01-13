package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PGPCFBBlockCipher implements BlockCipher {
   private byte[] IV;
   private byte[] FR;
   private byte[] FRE;
   private byte[] tmp;
   private BlockCipher cipher;
   private int count;
   private int blockSize;
   private boolean forEncryption;
   private boolean inlineIv;

   public PGPCFBBlockCipher(BlockCipher var1, boolean var2) {
      this.cipher = var1;
      this.inlineIv = var2;
      this.blockSize = var1.getBlockSize();
      this.IV = new byte[this.blockSize];
      this.FR = new byte[this.blockSize];
      this.FRE = new byte[this.blockSize];
      this.tmp = new byte[this.blockSize];
   }

   public BlockCipher getUnderlyingCipher() {
      return this.cipher;
   }

   @Override
   public String getAlgorithmName() {
      return this.inlineIv ? this.cipher.getAlgorithmName() + "/PGPCFBwithIV" : this.cipher.getAlgorithmName() + "/PGPCFB";
   }

   @Override
   public int getBlockSize() {
      return this.cipher.getBlockSize();
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.inlineIv) {
         return this.forEncryption ? this.encryptBlockWithIV(var1, var2, var3, var4) : this.decryptBlockWithIV(var1, var2, var3, var4);
      } else {
         return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
      }
   }

   @Override
   public void reset() {
      this.count = 0;

      for (int var1 = 0; var1 != this.FR.length; var1++) {
         if (this.inlineIv) {
            this.FR[var1] = 0;
         } else {
            this.FR[var1] = this.IV[var1];
         }
      }

      this.cipher.reset();
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4.length < this.IV.length) {
            System.arraycopy(var4, 0, this.IV, this.IV.length - var4.length, var4.length);

            for (int var5 = 0; var5 < this.IV.length - var4.length; var5++) {
               this.IV[var5] = 0;
            }
         } else {
            System.arraycopy(var4, 0, this.IV, 0, this.IV.length);
         }

         this.reset();
         this.cipher.init(true, var3.getParameters());
      } else {
         this.reset();
         this.cipher.init(true, var2);
      }
   }

   private byte encryptByte(byte var1, int var2) {
      return (byte)(this.FRE[var2] ^ var1);
   }

   private int encryptBlockWithIV(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (this.count == 0) {
         if (var4 + 2 * this.blockSize + 2 > var3.length) {
            throw new OutputLengthException("output buffer too short");
         } else {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);

            for (int var6 = 0; var6 < this.blockSize; var6++) {
               var3[var4 + var6] = this.encryptByte(this.IV[var6], var6);
            }

            System.arraycopy(var3, var4, this.FR, 0, this.blockSize);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            var3[var4 + this.blockSize] = this.encryptByte(this.IV[this.blockSize - 2], 0);
            var3[var4 + this.blockSize + 1] = this.encryptByte(this.IV[this.blockSize - 1], 1);
            System.arraycopy(var3, var4 + 2, this.FR, 0, this.blockSize);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);

            for (int var7 = 0; var7 < this.blockSize; var7++) {
               var3[var4 + this.blockSize + 2 + var7] = this.encryptByte(var1[var2 + var7], var7);
            }

            System.arraycopy(var3, var4 + this.blockSize + 2, this.FR, 0, this.blockSize);
            this.count = this.count + 2 * this.blockSize + 2;
            return 2 * this.blockSize + 2;
         }
      } else {
         if (this.count >= this.blockSize + 2) {
            if (var4 + this.blockSize > var3.length) {
               throw new OutputLengthException("output buffer too short");
            }

            this.cipher.processBlock(this.FR, 0, this.FRE, 0);

            for (int var5 = 0; var5 < this.blockSize; var5++) {
               var3[var4 + var5] = this.encryptByte(var1[var2 + var5], var5);
            }

            System.arraycopy(var3, var4, this.FR, 0, this.blockSize);
         }

         return this.blockSize;
      }
   }

   private int decryptBlockWithIV(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else if (this.count == 0) {
         for (int var7 = 0; var7 < this.blockSize; var7++) {
            this.FR[var7] = var1[var2 + var7];
         }

         this.cipher.processBlock(this.FR, 0, this.FRE, 0);
         this.count = this.count + this.blockSize;
         return 0;
      } else if (this.count == this.blockSize) {
         System.arraycopy(var1, var2, this.tmp, 0, this.blockSize);
         System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
         this.FR[this.blockSize - 2] = this.tmp[0];
         this.FR[this.blockSize - 1] = this.tmp[1];
         this.cipher.processBlock(this.FR, 0, this.FRE, 0);

         for (int var6 = 0; var6 < this.blockSize - 2; var6++) {
            var3[var4 + var6] = this.encryptByte(this.tmp[var6 + 2], var6);
         }

         System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
         this.count += 2;
         return this.blockSize - 2;
      } else {
         if (this.count >= this.blockSize + 2) {
            System.arraycopy(var1, var2, this.tmp, 0, this.blockSize);
            var3[var4 + 0] = this.encryptByte(this.tmp[0], this.blockSize - 2);
            var3[var4 + 1] = this.encryptByte(this.tmp[1], this.blockSize - 1);
            System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);

            for (int var5 = 0; var5 < this.blockSize - 2; var5++) {
               var3[var4 + var5 + 2] = this.encryptByte(this.tmp[var5 + 2], var5);
            }

            System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
         }

         return this.blockSize;
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.cipher.processBlock(this.FR, 0, this.FRE, 0);

         for (int var5 = 0; var5 < this.blockSize; var5++) {
            var3[var4 + var5] = this.encryptByte(var1[var2 + var5], var5);
         }

         for (int var6 = 0; var6 < this.blockSize; var6++) {
            this.FR[var6] = var3[var4 + var6];
         }

         return this.blockSize;
      }
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.cipher.processBlock(this.FR, 0, this.FRE, 0);

         for (int var5 = 0; var5 < this.blockSize; var5++) {
            var3[var4 + var5] = this.encryptByte(var1[var2 + var5], var5);
         }

         for (int var6 = 0; var6 < this.blockSize; var6++) {
            this.FR[var6] = var1[var2 + var6];
         }

         return this.blockSize;
      }
   }
}
