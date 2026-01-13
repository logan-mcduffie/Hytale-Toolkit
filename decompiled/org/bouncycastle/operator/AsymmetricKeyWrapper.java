package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class AsymmetricKeyWrapper implements KeyWrapper {
   private AlgorithmIdentifier algorithmId;

   protected AsymmetricKeyWrapper(AlgorithmIdentifier var1) {
      this.algorithmId = var1;
   }

   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmId;
   }
}
