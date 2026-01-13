package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;

public abstract class NTRUHPSParameterSet extends NTRUParameterSet {
   NTRUHPSParameterSet(int var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   public Polynomial createPolynomial() {
      return new HPSPolynomial(this);
   }

   @Override
   public int sampleFgBytes() {
      return this.sampleIidBytes() + this.sampleFixedTypeBytes();
   }

   @Override
   public int sampleRmBytes() {
      return this.sampleIidBytes() + this.sampleFixedTypeBytes();
   }

   public int weight() {
      return this.q() / 8 - 2;
   }
}
