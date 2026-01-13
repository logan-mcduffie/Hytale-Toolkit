package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;

class MacCFBBlockCipher {
   private byte[] IV;
   private byte[] cfbV;
   private byte[] cfbOutV;
   private int blockSize;
   private BlockCipher cipher = null;

   public MacCFBBlockCipher(BlockCipher var1, int var2) {
      this.cipher = var1;
      this.blockSize = var2 / 8;
      this.IV = new byte[var1.getBlockSize()];
      this.cfbV = new byte[var1.getBlockSize()];
      this.cfbOutV = new byte[var1.getBlockSize()];
   }

   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (var1 instanceof ParametersWithIV) {
         ParametersWithIV var2 = (ParametersWithIV)var1;
         byte[] var3 = var2.getIV();
         if (var3.length < this.IV.length) {
            System.arraycopy(var3, 0, this.IV, this.IV.length - var3.length, var3.length);
         } else {
            System.arraycopy(var3, 0, this.IV, 0, this.IV.length);
         }

         this.reset();
         this.cipher.init(true, var2.getParameters());
      } else {
         this.reset();
         this.cipher.init(true, var1);
      }
   }

   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
   }

   public int getBlockSize() {
      return this.blockSize;
   }

   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);

         for (int var5 = 0; var5 < this.blockSize; var5++) {
            var3[var4 + var5] = (byte)(this.cfbOutV[var5] ^ var1[var2 + var5]);
         }

         System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
         System.arraycopy(var3, var4, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
         return this.blockSize;
      }
   }

   public void reset() {
      System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
      this.cipher.reset();
   }

   void getMacBlock(byte[] var1) {
      this.cipher.processBlock(this.cfbV, 0, var1, 0);
   }
}
