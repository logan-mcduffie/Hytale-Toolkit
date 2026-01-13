package org.bouncycastle.operator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Integers;

public class DefaultSecretKeySizeProvider implements SecretKeySizeProvider {
   public static final SecretKeySizeProvider INSTANCE = new DefaultSecretKeySizeProvider();
   private static final Map KEY_SIZES;

   @Override
   public int getKeySize(AlgorithmIdentifier var1) {
      int var2 = this.getKeySize(var1.getAlgorithm());
      return var2 > 0 ? var2 : -1;
   }

   @Override
   public int getKeySize(ASN1ObjectIdentifier var1) {
      Integer var2 = (Integer)KEY_SIZES.get(var1);
      return var2 != null ? var2 : -1;
   }

   static {
      HashMap var0 = new HashMap();
      var0.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
      var0.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
      var0.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
      var0.put(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, Integers.valueOf(64));
      var0.put(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC, Integers.valueOf(64));
      var0.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
      var0.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
      var0.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
      var0.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf(128));
      var0.put(NISTObjectIdentifiers.id_aes192_GCM, Integers.valueOf(192));
      var0.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf(256));
      var0.put(NISTObjectIdentifiers.id_aes128_CCM, Integers.valueOf(128));
      var0.put(NISTObjectIdentifiers.id_aes192_CCM, Integers.valueOf(192));
      var0.put(NISTObjectIdentifiers.id_aes256_CCM, Integers.valueOf(256));
      var0.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
      var0.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
      var0.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
      var0.put(NISTObjectIdentifiers.id_aes128_wrap_pad, Integers.valueOf(128));
      var0.put(NISTObjectIdentifiers.id_aes192_wrap_pad, Integers.valueOf(192));
      var0.put(NISTObjectIdentifiers.id_aes256_wrap_pad, Integers.valueOf(256));
      var0.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
      var0.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
      var0.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
      var0.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
      var0.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
      var0.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
      var0.put(KISAObjectIdentifiers.id_seedCBC, Integers.valueOf(128));
      var0.put(OIWObjectIdentifiers.desCBC, Integers.valueOf(64));
      var0.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
      KEY_SIZES = Collections.unmodifiableMap(var0);
   }
}
