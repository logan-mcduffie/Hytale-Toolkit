package org.bouncycastle.crypto;

import java.math.BigInteger;

public interface BasicAgreement {
   void init(CipherParameters var1);

   int getFieldSize();

   BigInteger calculateAgreement(CipherParameters var1);
}
