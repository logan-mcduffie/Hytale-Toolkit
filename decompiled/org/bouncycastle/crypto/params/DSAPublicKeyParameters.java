package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DSAPublicKeyParameters extends DSAKeyParameters {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private BigInteger y;

   public DSAPublicKeyParameters(BigInteger var1, DSAParameters var2) {
      super(false, var2);
      this.y = this.validate(var1, var2);
   }

   private BigInteger validate(BigInteger var1, DSAParameters var2) {
      if (var2 != null) {
         if (TWO.compareTo(var1) <= 0 && var2.getP().subtract(TWO).compareTo(var1) >= 0 && ONE.equals(var1.modPow(var2.getQ(), var2.getP()))) {
            return var1;
         } else {
            throw new IllegalArgumentException("y value does not appear to be in correct group");
         }
      } else {
         return var1;
      }
   }

   public BigInteger getY() {
      return this.y;
   }
}
