package org.bouncycastle.crypto;

public interface BlockCipher {
   void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

   String getAlgorithmName();

   int getBlockSize();

   int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException;

   void reset();
}
