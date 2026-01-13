package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.BigIntegers;

class ECUtil {
   static BigInteger generateK(BigInteger var0, SecureRandom var1) {
      int var2 = var0.bitLength();

      BigInteger var3;
      do {
         var3 = BigIntegers.createRandomBigInteger(var2, var1);
      } while (var3.equals(ECConstants.ZERO) || var3.compareTo(var0) >= 0);

      return var3;
   }
}
