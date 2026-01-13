package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class AESUtil {
   static AlgorithmIdentifier determineKeyEncAlg(KeyParameter var0) {
      int var1 = var0.getKey().length * 8;
      ASN1ObjectIdentifier var2;
      if (var1 == 128) {
         var2 = NISTObjectIdentifiers.id_aes128_wrap;
      } else if (var1 == 192) {
         var2 = NISTObjectIdentifiers.id_aes192_wrap;
      } else {
         if (var1 != 256) {
            throw new IllegalArgumentException("illegal keysize in AES");
         }

         var2 = NISTObjectIdentifiers.id_aes256_wrap;
      }

      return new AlgorithmIdentifier(var2);
   }
}
