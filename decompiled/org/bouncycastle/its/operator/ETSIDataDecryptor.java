package org.bouncycastle.its.operator;

public interface ETSIDataDecryptor {
   byte[] decrypt(byte[] var1, byte[] var2, byte[] var3);

   byte[] getKey();
}
