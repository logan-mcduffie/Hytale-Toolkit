package org.bouncycastle.crypto;

public interface Signer {
   void init(boolean var1, CipherParameters var2);

   void update(byte var1);

   void update(byte[] var1, int var2, int var3);

   byte[] generateSignature() throws CryptoException, DataLengthException;

   boolean verifySignature(byte[] var1);

   void reset();
}
