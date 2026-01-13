package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface AEADCipher {
   void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

   String getAlgorithmName();

   void processAADByte(byte var1);

   void processAADBytes(byte[] var1, int var2, int var3);

   int processByte(byte var1, byte[] var2, int var3) throws DataLengthException;

   int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException;

   int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException;

   byte[] getMac();

   int getUpdateOutputSize(int var1);

   int getOutputSize(int var1);

   void reset();
}
