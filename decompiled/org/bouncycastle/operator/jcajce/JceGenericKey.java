package org.bouncycastle.operator.jcajce;

import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public class JceGenericKey extends GenericKey {
   private static Object getRepresentation(Key var0) {
      byte[] var1 = var0.getEncoded();
      return var1 != null ? var1 : var0;
   }

   public JceGenericKey(AlgorithmIdentifier var1, Key var2) {
      super(var1, getRepresentation(var2));
   }
}
