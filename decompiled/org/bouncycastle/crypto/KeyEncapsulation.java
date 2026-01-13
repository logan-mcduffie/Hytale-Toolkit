package org.bouncycastle.crypto;

/** @deprecated */
public interface KeyEncapsulation {
   void init(CipherParameters var1);

   CipherParameters encrypt(byte[] var1, int var2, int var3);

   CipherParameters decrypt(byte[] var1, int var2, int var3, int var4);
}
