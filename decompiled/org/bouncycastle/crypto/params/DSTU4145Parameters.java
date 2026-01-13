package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DSTU4145Parameters extends ECDomainParameters {
   private final byte[] dke;

   public DSTU4145Parameters(ECDomainParameters var1, byte[] var2) {
      super(var1.getCurve(), var1.getG(), var1.getN(), var1.getH(), var1.getSeed());
      this.dke = Arrays.clone(var2);
   }

   public byte[] getDKE() {
      return Arrays.clone(this.dke);
   }
}
