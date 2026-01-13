package org.bouncycastle.crypto;

public interface Wrapper {
   void init(boolean var1, CipherParameters var2);

   String getAlgorithmName();

   byte[] wrap(byte[] var1, int var2, int var3);

   byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException;
}
