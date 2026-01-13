package org.bouncycastle.crypto;

public interface MultiBlockCipher extends BlockCipher {
   int getMultiBlockSize();

   int processBlocks(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException;
}
