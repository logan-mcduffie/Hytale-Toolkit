package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Strings;

public class JceOpenSSLPKCS8DecryptorProviderBuilder {
   private JcaJceHelper helper = new DefaultJcaJceHelper();

   public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public InputDecryptorProvider build(final char[] var1) throws OperatorCreationException {
      return new InputDecryptorProvider() {
         @Override
         public InputDecryptor get(final AlgorithmIdentifier var1x) throws OperatorCreationException {
            try {
               final Cipher var2;
               if (PEMUtilities.isPKCS5Scheme2(var1x.getAlgorithm())) {
                  PBES2Parameters var3 = PBES2Parameters.getInstance(var1x.getParameters());
                  KeyDerivationFunc var4 = var3.getKeyDerivationFunc();
                  EncryptionScheme var5 = var3.getEncryptionScheme();
                  PBKDF2Params var6 = (PBKDF2Params)var4.getParameters();
                  int var7 = var6.getIterationCount().intValue();
                  byte[] var8 = var6.getSalt();
                  String var9 = var5.getAlgorithm().getId();
                  SecretKey var10;
                  if (PEMUtilities.isHmacSHA1(var6.getPrf())) {
                     var10 = PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, var9, var1, var8, var7);
                  } else {
                     var10 = PEMUtilities.generateSecretKeyForPKCS5Scheme2(
                        JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, var9, var1, var8, var7, var6.getPrf()
                     );
                  }

                  var2 = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(var5.getAlgorithm()));
                  AlgorithmParameters var11 = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(var9);
                  var11.init(var5.getParameters().toASN1Primitive().getEncoded());
                  var2.init(2, var10, var11);
               } else if (PEMUtilities.isPKCS12(var1x.getAlgorithm())) {
                  PKCS12PBEParams var14 = PKCS12PBEParams.getInstance(var1x.getParameters());
                  var2 = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(var1x.getAlgorithm()));
                  var2.init(2, new PKCS12KeyWithParameters(var1, var14.getIV(), var14.getIterations().intValue()));
               } else {
                  if (!PEMUtilities.isPKCS5Scheme1(var1x.getAlgorithm())) {
                     throw new PEMException("Unknown algorithm: " + var1x.getAlgorithm());
                  }

                  PBEParameter var15 = PBEParameter.getInstance(var1x.getParameters());
                  var2 = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(var1x.getAlgorithm()));
                  var2.init(2, new PBKDF1KeyWithParameters(var1, new CharToByteConverter() {
                     @Override
                     public String getType() {
                        return "ASCII";
                     }

                     @Override
                     public byte[] convert(char[] var1x) {
                        return Strings.toByteArray(var1x);
                     }
                  }, var15.getSalt(), var15.getIterationCount().intValue()));
               }

               return new InputDecryptor() {
                  @Override
                  public AlgorithmIdentifier getAlgorithmIdentifier() {
                     return var1x;
                  }

                  @Override
                  public InputStream getInputStream(InputStream var1xx) {
                     return new CipherInputStream(var1xx, var2);
                  }
               };
            } catch (IOException var12) {
               throw new OperatorCreationException(var1x.getAlgorithm() + " not available: " + var12.getMessage(), var12);
            } catch (GeneralSecurityException var13) {
               throw new OperatorCreationException(var1x.getAlgorithm() + " not available: " + var13.getMessage(), var13);
            }
         }
      };
   }
}
