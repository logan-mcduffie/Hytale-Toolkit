package org.bouncycastle.openssl;

public interface PEMEncryptor {
   String getAlgorithm();

   byte[] getIV();

   byte[] encrypt(byte[] var1) throws PEMException;
}
