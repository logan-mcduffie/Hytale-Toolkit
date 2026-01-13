package org.bouncycastle.crypto;

public interface AsymmetricBlockCipher {
   void init(boolean var1, CipherParameters var2);

   int getInputBlockSize();

   int getOutputBlockSize();

   byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException;
}
