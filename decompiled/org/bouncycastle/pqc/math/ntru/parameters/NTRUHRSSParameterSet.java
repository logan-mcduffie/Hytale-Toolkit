package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HRSS1373Polynomial;
import org.bouncycastle.pqc.math.ntru.HRSSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;

public abstract class NTRUHRSSParameterSet extends NTRUParameterSet {
   NTRUHRSSParameterSet(int var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public Polynomial createPolynomial() {
      return (Polynomial)(this.n() == 1373 ? new HRSS1373Polynomial(this) : new HRSSPolynomial(this));
   }

   @Override
   public int sampleFgBytes() {
      return 2 * this.sampleIidBytes();
   }

   @Override
   public int sampleRmBytes() {
      return 2 * this.sampleIidBytes();
   }
}
