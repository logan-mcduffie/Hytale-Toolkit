package org.bouncycastle.cms;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public abstract class KEMKeyWrapper extends AsymmetricKeyWrapper {
   protected KEMKeyWrapper(AlgorithmIdentifier var1) {
      super(var1);
   }

   public abstract byte[] getEncapsulation();

   public abstract AlgorithmIdentifier getKdfAlgorithmIdentifier();

   public abstract int getKekLength();

   public abstract AlgorithmIdentifier getWrapAlgorithmIdentifier();
}
