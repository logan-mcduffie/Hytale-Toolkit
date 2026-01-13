package org.bouncycastle.jce.spec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeySpec extends ECKeySpec {
   private ECPoint q;

   public ECPublicKeySpec(ECPoint var1, ECParameterSpec var2) {
      super(var2);
      if (var1.getCurve() != null) {
         this.q = var1.normalize();
      } else {
         this.q = var1;
      }
   }

   public ECPoint getQ() {
      return this.q;
   }
}
