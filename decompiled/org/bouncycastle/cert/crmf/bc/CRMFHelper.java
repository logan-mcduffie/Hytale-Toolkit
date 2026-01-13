package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.crypto.util.CipherKeyGeneratorFactory;

class CRMFHelper {
   CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier var1, SecureRandom var2) throws CRMFException {
      try {
         return CipherKeyGeneratorFactory.createKeyGenerator(var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new CRMFException(var4.getMessage(), var4);
      }
   }

   static Object createContentCipher(boolean var0, CipherParameters var1, AlgorithmIdentifier var2) throws CRMFException {
      try {
         return CipherFactory.createContentCipher(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         throw new CRMFException(var4.getMessage(), var4);
      }
   }

   AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier var1, KeyParameter var2, SecureRandom var3) throws CRMFException {
      try {
         return AlgorithmIdentifierFactory.generateEncryptionAlgID(var1, var2.getKey().length * 8, var3);
      } catch (IllegalArgumentException var5) {
         throw new CRMFException(var5.getMessage(), var5);
      }
   }
}
