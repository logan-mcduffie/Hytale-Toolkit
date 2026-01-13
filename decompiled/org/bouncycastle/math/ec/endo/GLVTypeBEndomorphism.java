package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.ScaleXPointMap;

public class GLVTypeBEndomorphism implements GLVEndomorphism {
   protected final GLVTypeBParameters parameters;
   protected final ECPointMap pointMap;

   public GLVTypeBEndomorphism(ECCurve var1, GLVTypeBParameters var2) {
      this.parameters = var2;
      this.pointMap = new ScaleXPointMap(var1.fromBigInteger(var2.getBeta()));
   }

   @Override
   public BigInteger[] decomposeScalar(BigInteger var1) {
      return EndoUtil.decomposeScalar(this.parameters.getSplitParams(), var1);
   }

   @Override
   public ECPointMap getPointMap() {
      return this.pointMap;
   }

   @Override
   public boolean hasEfficientPointMap() {
      return true;
   }
}
