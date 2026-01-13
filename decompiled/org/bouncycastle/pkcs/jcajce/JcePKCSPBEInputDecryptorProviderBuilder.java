package org.bouncycastle.pkcs.jcajce;

import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JcePKCSPBEInputDecryptorProviderBuilder {
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private boolean wrongPKCS12Zero = false;
   private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;

   public JcePKCSPBEInputDecryptorProviderBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePKCSPBEInputDecryptorProviderBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JcePKCSPBEInputDecryptorProviderBuilder setTryWrongPKCS12Zero(boolean var1) {
      this.wrongPKCS12Zero = var1;
      return this;
   }

   public JcePKCSPBEInputDecryptorProviderBuilder setKeySizeProvider(SecretKeySizeProvider var1) {
      this.keySizeProvider = var1;
      return this;
   }

   public InputDecryptorProvider build(final char[] var1) {
      return new InputDecryptorProvider() {
         private Cipher cipher;
         private AlgorithmIdentifier encryptionAlg;

         @Override
         public InputDecryptor get(AlgorithmIdentifier var1x) throws OperatorCreationException {
            ASN1ObjectIdentifier var3 = var1x.getAlgorithm();

            try {
               if (var3.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                  PKCS12PBEParams var4 = PKCS12PBEParams.getInstance(var1x.getParameters());
                  this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(var3.getId());
                  this.cipher
                     .init(
                        2,
                        new PKCS12KeyWithParameters(
                           var1, JcePKCSPBEInputDecryptorProviderBuilder.this.wrongPKCS12Zero, var4.getIV(), var4.getIterations().intValue()
                        )
                     );
                  this.encryptionAlg = var1x;
               } else if (var3.equals(PKCSObjectIdentifiers.id_PBES2)) {
                  PBES2Parameters var9 = PBES2Parameters.getInstance(var1x.getParameters());
                  SecretKey var2;
                  if (MiscObjectIdentifiers.id_scrypt.equals(var9.getKeyDerivationFunc().getAlgorithm())) {
                     ScryptParams var5 = ScryptParams.getInstance(var9.getKeyDerivationFunc().getParameters());
                     AlgorithmIdentifier var6 = AlgorithmIdentifier.getInstance(var9.getEncryptionScheme());
                     SecretKeyFactory var7 = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory("SCRYPT");
                     var2 = var7.generateSecret(
                        new ScryptKeySpec(
                           var1,
                           var5.getSalt(),
                           var5.getCostParameter().intValue(),
                           var5.getBlockSize().intValue(),
                           var5.getParallelizationParameter().intValue(),
                           JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(var6)
                        )
                     );
                  } else {
                     SecretKeyFactory var11 = JcePKCSPBEInputDecryptorProviderBuilder.this.helper
                        .createSecretKeyFactory(var9.getKeyDerivationFunc().getAlgorithm().getId());
                     PBKDF2Params var13 = PBKDF2Params.getInstance(var9.getKeyDerivationFunc().getParameters());
                     AlgorithmIdentifier var16 = AlgorithmIdentifier.getInstance(var9.getEncryptionScheme());
                     if (var13.isDefaultPrf()) {
                        var2 = var11.generateSecret(
                           new PBEKeySpec(
                              var1,
                              var13.getSalt(),
                              var13.getIterationCount().intValue(),
                              JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(var16)
                           )
                        );
                     } else {
                        var2 = var11.generateSecret(
                           new PBKDF2KeySpec(
                              var1,
                              var13.getSalt(),
                              var13.getIterationCount().intValue(),
                              JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(var16),
                              var13.getPrf()
                           )
                        );
                     }
                  }

                  this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(var9.getEncryptionScheme().getAlgorithm().getId());
                  this.encryptionAlg = AlgorithmIdentifier.getInstance(var9.getEncryptionScheme());
                  ASN1Encodable var12 = var9.getEncryptionScheme().getParameters();
                  if (var12 instanceof ASN1OctetString) {
                     this.cipher.init(2, var2, new IvParameterSpec(ASN1OctetString.getInstance(var12).getOctets()));
                  } else if (var12 instanceof ASN1Sequence && JcePKCSPBEInputDecryptorProviderBuilder.this.isCCMorGCM(var9.getEncryptionScheme())) {
                     AlgorithmParameters var15 = AlgorithmParameters.getInstance(var9.getEncryptionScheme().getAlgorithm().getId());
                     var15.init(((ASN1Sequence)var12).getEncoded());
                     this.cipher.init(2, var2, var15);
                  } else if (var12 == null) {
                     this.cipher.init(2, var2);
                  } else {
                     GOST28147Parameters var14 = GOST28147Parameters.getInstance(var12);
                     this.cipher.init(2, var2, new GOST28147ParameterSpec(var14.getEncryptionParamSet(), var14.getIV()));
                  }
               } else {
                  if (!var3.equals(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC) && !var3.equals(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC)) {
                     throw new OperatorCreationException("unable to create InputDecryptor: algorithm " + var3 + " unknown.");
                  }

                  PBEParameter var10 = PBEParameter.getInstance(var1x.getParameters());
                  this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(var3.getId());
                  this.cipher
                     .init(2, new PBKDF1Key(var1, PasswordConverter.ASCII), new PBEParameterSpec(var10.getSalt(), var10.getIterationCount().intValue()));
               }
            } catch (Exception var8) {
               throw new OperatorCreationException("unable to create InputDecryptor: " + var8.getMessage(), var8);
            }

            return new InputDecryptor() {
               @Override
               public AlgorithmIdentifier getAlgorithmIdentifier() {
                  return encryptionAlg;
               }

               @Override
               public InputStream getInputStream(InputStream var1x) {
                  return new CipherInputStream(var1x, cipher);
               }
            };
         }
      };
   }

   private boolean isCCMorGCM(ASN1Encodable var1) {
      AlgorithmIdentifier var2 = AlgorithmIdentifier.getInstance(var1);
      ASN1Encodable var3 = var2.getParameters();
      if (var3 instanceof ASN1Sequence) {
         ASN1Sequence var4 = ASN1Sequence.getInstance(var3);
         if (var4.size() == 2) {
            return var4.getObjectAt(1) instanceof ASN1Integer;
         }
      }

      return false;
   }
}
