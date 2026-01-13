package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.GenericKey;

class CMSUtils {
   static CipherParameters getBcKey(GenericKey var0) {
      if (var0.getRepresentation() instanceof CipherParameters) {
         return (CipherParameters)var0.getRepresentation();
      } else if (var0.getRepresentation() instanceof byte[]) {
         return new KeyParameter((byte[])var0.getRepresentation());
      } else {
         throw new IllegalArgumentException("unknown generic key type");
      }
   }
}
