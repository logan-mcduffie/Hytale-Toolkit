package org.bouncycastle.crypto.params;

import org.bouncycastle.math.ec.ECPoint;

public class ECCSIPublicKeyParameters extends AsymmetricKeyParameter {
   private final ECPoint pvt;

   public ECCSIPublicKeyParameters(ECPoint var1) {
      super(false);
      this.pvt = var1;
   }

   public final ECPoint getPVT() {
      return this.pvt;
   }
}
