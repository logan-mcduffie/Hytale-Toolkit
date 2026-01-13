package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

public class ECSchnorrZKP {
   private final ECPoint V;
   private final BigInteger r;

   ECSchnorrZKP(ECPoint var1, BigInteger var2) {
      this.V = var1;
      this.r = var2;
   }

   public ECPoint getV() {
      return this.V;
   }

   public BigInteger getr() {
      return this.r;
   }
}
