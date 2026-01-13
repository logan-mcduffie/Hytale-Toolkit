package org.bouncycastle.pqc.math.ntru.parameters;

import org.bouncycastle.pqc.math.ntru.HPS4096Polynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;

public class NTRUHPS4096821 extends NTRUHPSParameterSet {
   public NTRUHPS4096821() {
      super(821, 12, 32, 32, 32);
   }

   @Override
   public Polynomial createPolynomial() {
      return new HPS4096Polynomial(this);
   }
}
