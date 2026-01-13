package org.bouncycastle.cert.crmf.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCRMFEncryptorBuilder {
   private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
   private final ASN1ObjectIdentifier encryptionOID;
   private final int keySize;
   private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());
   private SecureRandom random;

   public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier var1) {
      this(var1, -1);
   }

   public JceCRMFEncryptorBuilder(ASN1ObjectIdentifier var1, int var2) {
      this.encryptionOID = var1;
      this.keySize = var2;
   }

   public JceCRMFEncryptorBuilder setProvider(Provider var1) {
      this.helper = new CRMFHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JceCRMFEncryptorBuilder setProvider(String var1) {
      this.helper = new CRMFHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   public JceCRMFEncryptorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public OutputEncryptor build() throws CRMFException {
      return new JceCRMFEncryptorBuilder.CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
   }

   private class CRMFOutputEncryptor implements OutputEncryptor {
      private SecretKey encKey;
      private AlgorithmIdentifier algorithmIdentifier;
      private Cipher cipher;

      CRMFOutputEncryptor(ASN1ObjectIdentifier nullx, int nullxx, SecureRandom nullxxx) throws CRMFException {
         KeyGenerator var5 = JceCRMFEncryptorBuilder.this.helper.createKeyGenerator(nullx);
         if (nullxxx == null) {
            nullxxx = new SecureRandom();
         }

         if (nullxx < 0) {
            nullxx = JceCRMFEncryptorBuilder.KEY_SIZE_PROVIDER.getKeySize(nullx);
         }

         if (nullxx < 0) {
            var5.init(nullxxx);
         } else {
            var5.init(nullxx, nullxxx);
         }

         this.cipher = JceCRMFEncryptorBuilder.this.helper.createCipher(nullx);
         this.encKey = var5.generateKey();
         AlgorithmParameters var6 = JceCRMFEncryptorBuilder.this.helper.generateParameters(nullx, this.encKey, nullxxx);

         try {
            this.cipher.init(1, this.encKey, var6, nullxxx);
         } catch (GeneralSecurityException var8) {
            throw new CRMFException("unable to initialize cipher: " + var8.getMessage(), var8);
         }

         if (var6 == null) {
            var6 = this.cipher.getParameters();
         }

         this.algorithmIdentifier = JceCRMFEncryptorBuilder.this.helper.getAlgorithmIdentifier(nullx, var6);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         return new CipherOutputStream(var1, this.cipher);
      }

      @Override
      public GenericKey getKey() {
         return new JceGenericKey(this.algorithmIdentifier, this.encKey);
      }
   }
}
