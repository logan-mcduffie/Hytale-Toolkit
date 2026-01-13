package org.bouncycastle.its.operator;

public interface ETSIDataEncryptor {
   byte[] encrypt(byte[] var1);

   byte[] getKey();

   byte[] getNonce();
}
