package org.bouncycastle.cert.crmf.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.ValueDecryptorGenerator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;

public class JceAsymmetricValueDecryptorGenerator implements ValueDecryptorGenerator {
   private PrivateKey recipientKey;
   private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());
   private Provider provider = null;
   private String providerName = null;

   public JceAsymmetricValueDecryptorGenerator(PrivateKey var1) {
      this.recipientKey = var1;
   }

   public JceAsymmetricValueDecryptorGenerator setProvider(Provider var1) {
      this.helper = new CRMFHelper(new ProviderJcaJceHelper(var1));
      this.provider = var1;
      this.providerName = null;
      return this;
   }

   public JceAsymmetricValueDecryptorGenerator setProvider(String var1) {
      this.helper = new CRMFHelper(new NamedJcaJceHelper(var1));
      this.provider = null;
      this.providerName = var1;
      return this;
   }

   private Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CRMFException {
      try {
         JceAsymmetricKeyUnwrapper var4 = new JceAsymmetricKeyUnwrapper(var1, this.recipientKey);
         if (this.provider != null) {
            var4.setProvider(this.provider);
         }

         if (this.providerName != null) {
            var4.setProvider(this.providerName);
         }

         return new SecretKeySpec((byte[])var4.generateUnwrappedKey(var2, var3).getRepresentation(), var2.getAlgorithm().getId());
      } catch (OperatorException var5) {
         throw new CRMFException("key invalid in message: " + var5.getMessage(), var5);
      }
   }

   @Override
   public InputDecryptor getValueDecryptor(AlgorithmIdentifier var1, final AlgorithmIdentifier var2, byte[] var3) throws CRMFException {
      Key var4 = this.extractSecretKey(var1, var2, var3);
      final Cipher var5 = this.helper.createContentCipher(var4, var2);
      return new InputDecryptor() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var2;
         }

         @Override
         public InputStream getInputStream(InputStream var1) {
            return new CipherInputStream(var1, var5);
         }
      };
   }
}
