package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

public class SM2KeyPairGenerator extends ECKeyPairGenerator {
   public SM2KeyPairGenerator() {
      super("SM2KeyGen");
   }

   @Override
   protected boolean isOutOfRangeD(BigInteger var1, BigInteger var2) {
      return var1.compareTo(ONE) < 0 || var1.compareTo(var2.subtract(BigIntegers.ONE)) >= 0;
   }
}
