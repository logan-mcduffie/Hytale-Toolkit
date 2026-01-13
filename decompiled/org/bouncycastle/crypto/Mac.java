package org.bouncycastle.crypto;

public interface Mac {
   void init(CipherParameters var1) throws IllegalArgumentException;

   String getAlgorithmName();

   int getMacSize();

   void update(byte var1) throws IllegalStateException;

   void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException;

   int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException;

   void reset();
}
