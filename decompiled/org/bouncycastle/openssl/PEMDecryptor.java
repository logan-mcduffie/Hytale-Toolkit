package org.bouncycastle.openssl;

public interface PEMDecryptor {
   byte[] decrypt(byte[] var1, byte[] var2) throws PEMException;
}
