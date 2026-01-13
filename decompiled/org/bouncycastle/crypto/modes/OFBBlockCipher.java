package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class OFBBlockCipher extends StreamBlockCipher {
   private int byteCount;
   private byte[] IV;
   private byte[] ofbV;
   private byte[] ofbOutV;
   private final int blockSize;
   private final BlockCipher cipher;

   public OFBBlockCipher(BlockCipher var1, int var2) {
      super(var1);
      if (var2 <= var1.getBlockSize() * 8 && var2 >= 8 && var2 % 8 == 0) {
         this.cipher = var1;
         this.blockSize = var2 / 8;
         this.IV = new byte[var1.getBlockSize()];
         this.ofbV = new byte[var1.getBlockSize()];
         this.ofbOutV = new byte[var1.getBlockSize()];
      } else {
         throw new IllegalArgumentException("0FB" + var2 + " not supported");
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
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
         if (var3.getParameters() != null) {
            this.cipher.init(true, var3.getParameters());
         }
      } else {
         this.reset();
         if (var2 != null) {
            this.cipher.init(true, var2);
         }
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/OFB" + this.blockSize * 8;
   }

   @Override
   public int getBlockSize() {
      return this.blockSize;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      this.processBytes(var1, var2, this.blockSize, var3, var4);
      return this.blockSize;
   }

   @Override
   public void reset() {
      System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
      this.byteCount = 0;
      this.cipher.reset();
   }

   @Override
   protected byte calculateByte(byte var1) throws DataLengthException, IllegalStateException {
      if (this.byteCount == 0) {
         this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
      }

      byte var2 = (byte)(this.ofbOutV[this.byteCount++] ^ var1);
      if (this.byteCount == this.blockSize) {
         this.byteCount = 0;
         System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
         System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
      }

      return var2;
   }
}
