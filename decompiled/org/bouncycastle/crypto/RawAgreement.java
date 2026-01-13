package org.bouncycastle.crypto;

public interface RawAgreement {
   void init(CipherParameters var1);

   int getAgreementSize();

   void calculateAgreement(CipherParameters var1, byte[] var2, int var3);
}
