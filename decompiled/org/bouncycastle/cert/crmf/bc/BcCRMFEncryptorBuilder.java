package org.bouncycastle.cert.crmf.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class BcCRMFEncryptorBuilder {
   private final ASN1ObjectIdentifier encryptionOID;
   private final int keySize;
   private CRMFHelper helper = new CRMFHelper();
   private SecureRandom random;

   public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier var1) {
      this(var1, -1);
   }

   public BcCRMFEncryptorBuilder(ASN1ObjectIdentifier var1, int var2) {
      this.encryptionOID = var1;
      this.keySize = var2;
   }

   public BcCRMFEncryptorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public OutputEncryptor build() throws CRMFException {
      return new BcCRMFEncryptorBuilder.CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
   }

   private class CRMFOutputEncryptor implements OutputEncryptor {
      private KeyParameter encKey;
      private AlgorithmIdentifier algorithmIdentifier;
      private Object cipher;

      CRMFOutputEncryptor(ASN1ObjectIdentifier nullx, int nullxx, SecureRandom nullxxx) throws CRMFException {
         nullxxx = CryptoServicesRegistrar.getSecureRandom(nullxxx);
         CipherKeyGenerator var5 = BcCRMFEncryptorBuilder.this.helper.createKeyGenerator(nullx, nullxxx);
         this.encKey = new KeyParameter(var5.generateKey());
         this.algorithmIdentifier = BcCRMFEncryptorBuilder.this.helper.generateEncryptionAlgID(nullx, this.encKey, nullxxx);
         BcCRMFEncryptorBuilder.this.helper;
         this.cipher = CRMFHelper.createContentCipher(true, this.encKey, this.algorithmIdentifier);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         return CipherFactory.createOutputStream(var1, this.cipher);
      }

      @Override
      public GenericKey getKey() {
         return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
      }
   }
}
