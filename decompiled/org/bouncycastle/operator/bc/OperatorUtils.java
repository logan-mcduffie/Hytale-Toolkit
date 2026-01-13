package org.bouncycastle.operator.bc;

import java.security.Key;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils {
   static byte[] getKeyBytes(GenericKey var0) {
      if (var0.getRepresentation() instanceof Key) {
         return ((Key)var0.getRepresentation()).getEncoded();
      } else if (var0.getRepresentation() instanceof byte[]) {
         return (byte[])var0.getRepresentation();
      } else {
         throw new IllegalArgumentException("unknown generic key type");
      }
   }
}
