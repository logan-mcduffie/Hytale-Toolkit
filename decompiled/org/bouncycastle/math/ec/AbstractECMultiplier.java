package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class AbstractECMultiplier implements ECMultiplier {
   @Override
   public ECPoint multiply(ECPoint var1, BigInteger var2) {
      int var3 = var2.signum();
      if (var3 != 0 && !var1.isInfinity()) {
         ECPoint var4 = this.multiplyPositive(var1, var2.abs());
         ECPoint var5 = var3 > 0 ? var4 : var4.negate();
         return this.checkResult(var5);
      } else {
         return var1.getCurve().getInfinity();
      }
   }

   protected abstract ECPoint multiplyPositive(ECPoint var1, BigInteger var2);

   protected ECPoint checkResult(ECPoint var1) {
      return ECAlgorithms.implCheckResult(var1);
   }
}
