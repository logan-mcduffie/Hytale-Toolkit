package org.bouncycastle.crypto.util;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DESKeyGenerator;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class CipherKeyGeneratorFactory {
   private CipherKeyGeneratorFactory() {
   }

   public static CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier var0, SecureRandom var1) throws IllegalArgumentException {
      if (NISTObjectIdentifiers.id_aes128_CBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (NISTObjectIdentifiers.id_aes192_CBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 192);
      } else if (NISTObjectIdentifiers.id_aes256_CBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 256);
      } else if (NISTObjectIdentifiers.id_aes128_GCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (NISTObjectIdentifiers.id_aes192_GCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 192);
      } else if (NISTObjectIdentifiers.id_aes256_GCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 256);
      } else if (NISTObjectIdentifiers.id_aes128_CCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (NISTObjectIdentifiers.id_aes192_CCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 192);
      } else if (NISTObjectIdentifiers.id_aes256_CCM.equals(var0)) {
         return createCipherKeyGenerator(var1, 256);
      } else if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(var0)) {
         DESedeKeyGenerator var3 = new DESedeKeyGenerator();
         var3.init(new KeyGenerationParameters(var1, 192));
         return var3;
      } else if (NTTObjectIdentifiers.id_camellia128_cbc.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (NTTObjectIdentifiers.id_camellia192_cbc.equals(var0)) {
         return createCipherKeyGenerator(var1, 192);
      } else if (NTTObjectIdentifiers.id_camellia256_cbc.equals(var0)) {
         return createCipherKeyGenerator(var1, 256);
      } else if (KISAObjectIdentifiers.id_seedCBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (AlgorithmIdentifierFactory.CAST5_CBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (OIWObjectIdentifiers.desCBC.equals(var0)) {
         DESKeyGenerator var2 = new DESKeyGenerator();
         var2.init(new KeyGenerationParameters(var1, 64));
         return var2;
      } else if (PKCSObjectIdentifiers.rc4.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else if (PKCSObjectIdentifiers.RC2_CBC.equals(var0)) {
         return createCipherKeyGenerator(var1, 128);
      } else {
         throw new IllegalArgumentException("cannot recognise cipher: " + var0);
      }
   }

   private static CipherKeyGenerator createCipherKeyGenerator(SecureRandom var0, int var1) {
      CipherKeyGenerator var2 = new CipherKeyGenerator();
      var2.init(new KeyGenerationParameters(var0, var1));
      return var2;
   }
}
