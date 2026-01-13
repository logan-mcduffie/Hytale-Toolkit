package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericKey {
   private AlgorithmIdentifier algorithmIdentifier;
   private Object representation;

   /** @deprecated */
   public GenericKey(Object var1) {
      this.algorithmIdentifier = null;
      this.representation = var1;
   }

   public GenericKey(AlgorithmIdentifier var1, byte[] var2) {
      this.algorithmIdentifier = var1;
      this.representation = var2;
   }

   protected GenericKey(AlgorithmIdentifier var1, Object var2) {
      this.algorithmIdentifier = var1;
      this.representation = var2;
   }

   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algorithmIdentifier;
   }

   public Object getRepresentation() {
      return this.representation;
   }
}
