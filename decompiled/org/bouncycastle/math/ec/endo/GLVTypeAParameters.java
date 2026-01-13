package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public class GLVTypeAParameters {
   protected final BigInteger i;
   protected final BigInteger lambda;
   protected final ScalarSplitParameters splitParams;

   public GLVTypeAParameters(BigInteger var1, BigInteger var2, ScalarSplitParameters var3) {
      this.i = var1;
      this.lambda = var2;
      this.splitParams = var3;
   }

   public BigInteger getI() {
      return this.i;
   }

   public BigInteger getLambda() {
      return this.lambda;
   }

   public ScalarSplitParameters getSplitParams() {
      return this.splitParams;
   }
}
