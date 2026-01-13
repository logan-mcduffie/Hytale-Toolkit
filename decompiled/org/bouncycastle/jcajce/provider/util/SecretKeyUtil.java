package org.bouncycastle.jcajce.provider.util;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.util.Integers;

/** @deprecated */
public class SecretKeyUtil {
   private static Map<ASN1ObjectIdentifier, Integer> keySizes = new HashMap<>();

   public static int getKeySize(ASN1ObjectIdentifier var0) {
      Integer var1 = keySizes.get(var0);
      return var1 != null ? var1 : -1;
   }

   static {
      keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
      keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
      keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
      keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
      keySizes.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
      keySizes.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
      keySizes.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
   }
}
