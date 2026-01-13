package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class SymmetricKeyUnwrapper implements KeyUnwrapper {
   private AlgorithmIdentifier algorithmId;

   protected SymmetricKeyUnwrapper(AlgorithmIdentifier var1) {
      this.algorithmId = var1;
   }

   @Override
   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmId;
   }
}
