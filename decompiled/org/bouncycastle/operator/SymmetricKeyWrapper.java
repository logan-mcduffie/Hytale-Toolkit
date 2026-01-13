package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class SymmetricKeyWrapper implements KeyWrapper {
   private AlgorithmIdentifier algorithmId;

   protected SymmetricKeyWrapper(AlgorithmIdentifier var1) {
      this.algorithmId = var1;
   }

   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmId;
   }
}
