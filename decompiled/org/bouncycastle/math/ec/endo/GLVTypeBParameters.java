package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public class GLVTypeBParameters {
   protected final BigInteger beta;
   protected final BigInteger lambda;
   protected final ScalarSplitParameters splitParams;

   public GLVTypeBParameters(BigInteger var1, BigInteger var2, ScalarSplitParameters var3) {
      this.beta = var1;
      this.lambda = var2;
      this.splitParams = var3;
   }

   public BigInteger getBeta() {
      return this.beta;
   }

   public BigInteger getLambda() {
      return this.lambda;
   }

   public ScalarSplitParameters getSplitParams() {
      return this.splitParams;
   }
}
