package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.util.BigIntegers;

public class RandomDSAKCalculator implements DSAKCalculator {
   private static final BigInteger ZERO = BigInteger.valueOf(0L);
   private BigInteger q;
   private SecureRandom random;

   @Override
   public boolean isDeterministic() {
      return false;
   }

   @Override
   public void init(BigInteger var1, SecureRandom var2) {
      this.q = var1;
      this.random = var2;
   }

   @Override
   public void init(BigInteger var1, BigInteger var2, byte[] var3) {
      throw new IllegalStateException("Operation not supported");
   }

   @Override
   public BigInteger nextK() {
      int var1 = this.q.bitLength();

      BigInteger var2;
      do {
         var2 = BigIntegers.createRandomBigInteger(var1, this.random);
      } while (var2.equals(ZERO) || var2.compareTo(this.q) >= 0);

      return var2;
   }
}
