package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;

public class DHExtendedPrivateKeySpec extends DHPrivateKeySpec {
   private final DHParameterSpec params;

   public DHExtendedPrivateKeySpec(BigInteger var1, DHParameterSpec var2) {
      super(var1, var2.getP(), var2.getG());
      this.params = var2;
   }

   public DHParameterSpec getParams() {
      return this.params;
   }
}
