package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AlgorithmNameFinder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JcePKCSPBEOutputEncryptorBuilder {
   private final PBKDFConfig pbkdf;
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private ASN1ObjectIdentifier algorithm;
   private ASN1ObjectIdentifier keyEncAlgorithm;
   private SecureRandom random;
   private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
   private AlgorithmNameFinder algorithmNameFinder = new DefaultAlgorithmNameFinder();
   private int iterationCount = 1024;
   private PBKDF2Config.Builder pbkdfBuilder = new PBKDF2Config.Builder();

   public JcePKCSPBEOutputEncryptorBuilder(ASN1ObjectIdentifier var1) {
      this.pbkdf = null;
      if (this.isPKCS12(var1)) {
         this.algorithm = var1;
         this.keyEncAlgorithm = var1;
      } else {
         this.algorithm = PKCSObjectIdentifiers.id_PBES2;
         this.keyEncAlgorithm = var1;
      }
   }

   public JcePKCSPBEOutputEncryptorBuilder(PBKDFConfig var1, ASN1ObjectIdentifier var2) {
      this.algorithm = PKCSObjectIdentifiers.id_PBES2;
      this.pbkdf = var1;
      this.keyEncAlgorithm = var2;
   }

   public JcePKCSPBEOutputEncryptorBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePKCSPBEOutputEncryptorBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JcePKCSPBEOutputEncryptorBuilder setRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(SecretKeySizeProvider var1) {
      this.keySizeProvider = var1;
      return this;
   }

   public JcePKCSPBEOutputEncryptorBuilder setPRF(AlgorithmIdentifier var1) {
      if (this.pbkdf != null) {
         throw new IllegalStateException("set PRF count using PBKDFDef");
      } else {
         this.pbkdfBuilder.withPRF(var1);
         return this;
      }
   }

   public JcePKCSPBEOutputEncryptorBuilder setIterationCount(int var1) {
      if (this.pbkdf != null) {
         throw new IllegalStateException("set iteration count using PBKDFDef");
      } else {
         this.iterationCount = var1;
         this.pbkdfBuilder.withIterationCount(var1);
         return this;
      }
   }

   public OutputEncryptor build(final char[] var1) throws OperatorCreationException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      try {
         final Cipher var2;
         final AlgorithmIdentifier var4;
         if (this.isPKCS12(this.algorithm)) {
            byte[] var5 = new byte[20];
            this.random.nextBytes(var5);
            var2 = this.helper.createCipher(this.algorithm.getId());
            var2.init(1, new PKCS12KeyWithParameters(var1, var5, this.iterationCount));
            var4 = new AlgorithmIdentifier(this.algorithm, new PKCS12PBEParams(var5, this.iterationCount));
         } else {
            if (!this.algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
               throw new OperatorCreationException("unrecognised algorithm");
            }

            Object var14 = this.pbkdf == null ? this.pbkdfBuilder.build() : this.pbkdf;
            if (MiscObjectIdentifiers.id_scrypt.equals(((PBKDFConfig)var14).getAlgorithm())) {
               ScryptConfig var6 = (ScryptConfig)var14;
               byte[] var7 = new byte[var6.getSaltLength()];
               this.random.nextBytes(var7);
               ScryptParams var8 = new ScryptParams(var7, var6.getCostParameter(), var6.getBlockSize(), var6.getParallelizationParameter());
               SecretKeyFactory var9 = this.helper.createSecretKeyFactory("SCRYPT");
               SecretKey var3 = var9.generateSecret(
                  new ScryptKeySpec(
                     var1,
                     var7,
                     var6.getCostParameter(),
                     var6.getBlockSize(),
                     var6.getParallelizationParameter(),
                     this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))
                  )
               );
               var2 = this.helper.createCipher(this.keyEncAlgorithm.getId());
               var2.init(1, this.simplifyPbeKey(var3), this.random);
               AlgorithmParameters var10 = var2.getParameters();
               PBES2Parameters var11;
               if (var10 != null) {
                  var11 = new PBES2Parameters(
                     new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, var8),
                     new EncryptionScheme(this.keyEncAlgorithm, ASN1Primitive.fromByteArray(var2.getParameters().getEncoded()))
                  );
               } else {
                  var11 = new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, var8), new EncryptionScheme(this.keyEncAlgorithm));
               }

               var4 = new AlgorithmIdentifier(this.algorithm, var11);
            } else {
               PBKDF2Config var15 = (PBKDF2Config)var14;
               byte[] var16 = new byte[var15.getSaltLength()];
               this.random.nextBytes(var16);
               SecretKeyFactory var17 = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(var15.getPRF().getAlgorithm()));
               SecretKey var13 = var17.generateSecret(
                  new PBEKeySpec(var1, var16, var15.getIterationCount(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm)))
               );
               var2 = this.helper.createCipher(this.keyEncAlgorithm.getId());
               var2.init(1, this.simplifyPbeKey(var13), this.random);
               AlgorithmParameters var18 = var2.getParameters();
               PBES2Parameters var19;
               if (var18 != null) {
                  var19 = new PBES2Parameters(
                     new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(var16, var15.getIterationCount(), var15.getPRF())),
                     new EncryptionScheme(this.keyEncAlgorithm, ASN1Primitive.fromByteArray(var2.getParameters().getEncoded()))
                  );
               } else {
                  var19 = new PBES2Parameters(
                     new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(var16, var15.getIterationCount(), var15.getPRF())),
                     new EncryptionScheme(this.keyEncAlgorithm)
                  );
               }

               var4 = new AlgorithmIdentifier(this.algorithm, var19);
            }
         }

         return new OutputEncryptor() {
            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return var4;
            }

            @Override
            public OutputStream getOutputStream(OutputStream var1x) {
               return new CipherOutputStream(var1x, var2);
            }

            @Override
            public GenericKey getKey() {
               return JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(var4.getAlgorithm())
                  ? new GenericKey(var4, JcePKCSPBEOutputEncryptorBuilder.PKCS12PasswordToBytes(var1))
                  : new GenericKey(var4, JcePKCSPBEOutputEncryptorBuilder.PKCS5PasswordToBytes(var1));
            }
         };
      } catch (Exception var12) {
         throw new OperatorCreationException("unable to create OutputEncryptor: " + var12.getMessage(), var12);
      }
   }

   private SecretKey simplifyPbeKey(SecretKey var1) {
      if (this.algorithmNameFinder.hasAlgorithmName(this.keyEncAlgorithm)) {
         String var2 = this.algorithmNameFinder.getAlgorithmName(this.keyEncAlgorithm);
         if (var2.indexOf("AES") >= 0) {
            var1 = new SecretKeySpec(var1.getEncoded(), "AES");
         }
      }

      return (SecretKey)var1;
   }

   private boolean isPKCS12(ASN1ObjectIdentifier var1) {
      return var1.on(PKCSObjectIdentifiers.pkcs_12PbeIds)
         || var1.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12)
         || var1.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12);
   }

   private static byte[] PKCS5PasswordToBytes(char[] var0) {
      if (var0 == null) {
         return new byte[0];
      } else {
         byte[] var1 = new byte[var0.length];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = (byte)var0[var2];
         }

         return var1;
      }
   }

   private static byte[] PKCS12PasswordToBytes(char[] var0) {
      if (var0 != null && var0.length > 0) {
         byte[] var1 = new byte[(var0.length + 1) * 2];

         for (int var2 = 0; var2 != var0.length; var2++) {
            var1[var2 * 2] = (byte)(var0[var2] >>> '\b');
            var1[var2 * 2 + 1] = (byte)var0[var2];
         }

         return var1;
      } else {
         return new byte[0];
      }
   }
}
