package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ECCSIPrivateKeyParameters extends AsymmetricKeyParameter {
   private final BigInteger ssk;
   private final ECCSIPublicKeyParameters pub;

   public ECCSIPrivateKeyParameters(BigInteger var1, ECCSIPublicKeyParameters var2) {
      super(true);
      this.ssk = var1;
      this.pub = var2;
   }

   public ECCSIPublicKeyParameters getPublicKeyParameters() {
      return this.pub;
   }

   public BigInteger getSSK() {
      return this.ssk;
   }
}
