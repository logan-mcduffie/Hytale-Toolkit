package org.bouncycastle.cms.bc;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.crypto.util.CipherKeyGeneratorFactory;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

class EnvelopedDataHelper {
   protected static final Map BASE_CIPHER_NAMES = new HashMap();
   protected static final Map MAC_ALG_NAMES = new HashMap();
   private static final Set authEnvelopedAlgorithms = new HashSet();
   private static final Map prfs = createTable();

   private static Map createTable() {
      HashMap var0 = new HashMap();
      var0.put(PKCSObjectIdentifiers.id_hmacWithSHA1, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA1Digest();
         }
      });
      var0.put(PKCSObjectIdentifiers.id_hmacWithSHA224, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA224Digest();
         }
      });
      var0.put(PKCSObjectIdentifiers.id_hmacWithSHA256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return SHA256Digest.newInstance();
         }
      });
      var0.put(PKCSObjectIdentifiers.id_hmacWithSHA384, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA384Digest();
         }
      });
      var0.put(PKCSObjectIdentifiers.id_hmacWithSHA512, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA512Digest();
         }
      });
      return Collections.unmodifiableMap(var0);
   }

   static ExtendedDigest getPRF(AlgorithmIdentifier var0) throws OperatorCreationException {
      return ((BcDigestProvider)prfs.get(var0.getAlgorithm())).get(null);
   }

   static Wrapper createRFC3211Wrapper(ASN1ObjectIdentifier var0) throws CMSException {
      if (NISTObjectIdentifiers.id_aes128_CBC.equals(var0)
         || NISTObjectIdentifiers.id_aes192_CBC.equals(var0)
         || NISTObjectIdentifiers.id_aes256_CBC.equals(var0)) {
         return new RFC3211WrapEngine(AESEngine.newInstance());
      } else if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(var0)) {
         return new RFC3211WrapEngine(new DESedeEngine());
      } else if (OIWObjectIdentifiers.desCBC.equals(var0)) {
         return new RFC3211WrapEngine(new DESEngine());
      } else if (PKCSObjectIdentifiers.RC2_CBC.equals(var0)) {
         return new RFC3211WrapEngine(new RC2Engine());
      } else {
         throw new CMSException("cannot recognise wrapper: " + var0);
      }
   }

   static Object createContentCipher(boolean var0, CipherParameters var1, AlgorithmIdentifier var2) throws CMSException {
      try {
         return CipherFactory.createContentCipher(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new CMSException(var4.getMessage(), var4);
      }
   }

   AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier var1, KeyParameter var2, SecureRandom var3) throws CMSException {
      try {
         return AlgorithmIdentifierFactory.generateEncryptionAlgID(var1, var2.getKey().length * 8, var3);
      } catch (IllegalArgumentException var5) {
         throw new CMSException(var5.getMessage(), var5);
      }
   }

   CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier var1, int var2, SecureRandom var3) throws CMSException {
      try {
         return CipherKeyGeneratorFactory.createKeyGenerator(var1, var3);
      } catch (IllegalArgumentException var5) {
         throw new CMSException(var5.getMessage(), var5);
      }
   }

   boolean isAuthEnveloped(ASN1ObjectIdentifier var1) {
      return authEnvelopedAlgorithms.contains(var1);
   }

   static {
      BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
      MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES128_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES192_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES256_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES128_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES192_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES256_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.ChaCha20Poly1305);
   }
}
