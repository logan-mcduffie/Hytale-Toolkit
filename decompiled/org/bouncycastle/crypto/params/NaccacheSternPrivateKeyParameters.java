package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.util.Vector;

public class NaccacheSternPrivateKeyParameters extends NaccacheSternKeyParameters {
   private BigInteger phi_n;
   private Vector smallPrimes;

   public NaccacheSternPrivateKeyParameters(BigInteger var1, BigInteger var2, int var3, Vector var4, BigInteger var5) {
      super(true, var1, var2, var3);
      this.smallPrimes = var4;
      this.phi_n = var5;
   }

   public BigInteger getPhi_n() {
      return this.phi_n;
   }

   public Vector getSmallPrimes() {
      return this.smallPrimes;
   }
}
