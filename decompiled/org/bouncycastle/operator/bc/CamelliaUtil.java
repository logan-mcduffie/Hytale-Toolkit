package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class CamelliaUtil {
   static AlgorithmIdentifier determineKeyEncAlg(KeyParameter var0) {
      int var1 = var0.getKey().length * 8;
      ASN1ObjectIdentifier var2;
      if (var1 == 128) {
         var2 = NTTObjectIdentifiers.id_camellia128_wrap;
      } else if (var1 == 192) {
         var2 = NTTObjectIdentifiers.id_camellia192_wrap;
      } else {
         if (var1 != 256) {
            throw new IllegalArgumentException("illegal keysize in Camellia");
         }

         var2 = NTTObjectIdentifiers.id_camellia256_wrap;
      }

      return new AlgorithmIdentifier(var2);
   }
}
