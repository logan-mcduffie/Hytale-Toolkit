package org.bouncycastle.operator.jcajce;

import java.io.InputStream;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class JceInputDecryptorProviderBuilder {
   private JcaJceHelper helper = new DefaultJcaJceHelper();

   public JceInputDecryptorProviderBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JceInputDecryptorProviderBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public InputDecryptorProvider build(byte[] var1) {
      final byte[] var2 = Arrays.clone(var1);
      return new InputDecryptorProvider() {
         private Cipher cipher;
         private AlgorithmIdentifier encryptionAlg;

         @Override
         public InputDecryptor get(AlgorithmIdentifier var1) throws OperatorCreationException {
            this.encryptionAlg = var1;
            ASN1ObjectIdentifier var2x = var1.getAlgorithm();

            try {
               this.cipher = JceInputDecryptorProviderBuilder.this.helper.createCipher(var2x.getId());
               SecretKeySpec var3 = new SecretKeySpec(var2, var2x.getId());
               ASN1Encodable var4 = var1.getParameters();
               if (var4 instanceof ASN1OctetString) {
                  this.cipher.init(2, var3, new IvParameterSpec(ASN1OctetString.getInstance(var4).getOctets()));
               } else {
                  GOST28147Parameters var5 = GOST28147Parameters.getInstance(var4);
                  this.cipher.init(2, var3, new GOST28147ParameterSpec(var5.getEncryptionParamSet(), var5.getIV()));
               }
            } catch (Exception var6) {
               throw new OperatorCreationException("unable to create InputDecryptor: " + var6.getMessage(), var6);
            }

            return new InputDecryptor() {
               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return encryptionAlg;
               }

               @Override
               public InputStream getInputStream(InputStream var1) {
                  return new CipherInputStream(var1, cipher);
               }
            };
         }
      };
   }
}
