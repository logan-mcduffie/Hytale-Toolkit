package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;

public interface DSAKCalculator {
   boolean isDeterministic();

   void init(BigInteger var1, SecureRandom var2);

   void init(BigInteger var1, BigInteger var2, byte[] var3);

   BigInteger nextK();
}
