package org.bouncycastle.crypto;

public abstract class DefaultMultiBlockCipher implements MultiBlockCipher {
   protected DefaultMultiBlockCipher() {
   }

   @Override
   public int getMultiBlockSize() {
      return this.getBlockSize();
   }

   @Override
   public int processBlocks(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException {
      int var6 = 0;
      int var7 = this.getBlockSize();
      int var8 = var3 * var7;
      if (var1 == var4) {
         var1 = new byte[var8];
         System.arraycopy(var4, var2, var1, 0, var8);
         var2 = 0;
      }

      for (int var9 = 0; var9 != var3; var9++) {
         var6 += this.processBlock(var1, var2, var4, var5 + var6);
         var2 += var7;
      }

      return var6;
   }
}
