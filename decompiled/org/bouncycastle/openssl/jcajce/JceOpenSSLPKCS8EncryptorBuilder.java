package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceOpenSSLPKCS8EncryptorBuilder {
   public static final String AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC.getId();
   public static final String AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC.getId();
   public static final String AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC.getId();
   public static final String DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC.getId();
   public static final String PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId();
   public static final String PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId();
   public static final String PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId();
   public static final String PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId();
   public static final String PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId();
   public static final String PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC.getId();
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private AlgorithmParameters params;
   private ASN1ObjectIdentifier algOID;
   byte[] salt;
   int iterationCount;
   private Cipher cipher;
   private SecureRandom random;
   private AlgorithmParameterGenerator paramGen;
   private char[] password;
   private SecretKey key;
   private AlgorithmIdentifier prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);

   public JceOpenSSLPKCS8EncryptorBuilder(ASN1ObjectIdentifier var1) {
      this.algOID = var1;
      this.iterationCount = 2048;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   /** @deprecated */
   public JceOpenSSLPKCS8EncryptorBuilder setPasssword(char[] var1) {
      this.password = var1;
      return this;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setPassword(char[] var1) {
      this.password = var1;
      return this;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setPRF(AlgorithmIdentifier var1) {
      this.prf = var1;
      return this;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setIterationCount(int var1) {
      this.iterationCount = var1;
      return this;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JceOpenSSLPKCS8EncryptorBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public OutputEncryptor build() throws OperatorCreationException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      try {
         this.cipher = this.helper.createCipher(PEMUtilities.getCipherName(this.algOID));
         if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
            this.paramGen = this.helper.createAlgorithmParameterGenerator(this.algOID.getId());
         }
      } catch (GeneralSecurityException var8) {
         throw new OperatorCreationException(this.algOID + " not available: " + var8.getMessage(), var8);
      }

      final AlgorithmIdentifier var1;
      if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
         this.salt = new byte[PEMUtilities.getSaltSize(this.prf.getAlgorithm())];
         this.random.nextBytes(this.salt);
         this.params = this.paramGen.generateParameters();

         try {
            EncryptionScheme var2 = new EncryptionScheme(this.algOID, ASN1Primitive.fromByteArray(this.params.getEncoded()));
            KeyDerivationFunc var3 = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(this.salt, this.iterationCount, this.prf));
            ASN1EncodableVector var4 = new ASN1EncodableVector();
            var4.add(var3);
            var4.add(var2);
            var1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, PBES2Parameters.getInstance(new DERSequence(var4)));
         } catch (IOException var7) {
            throw new OperatorCreationException(var7.getMessage(), var7);
         }

         try {
            if (PEMUtilities.isHmacSHA1(this.prf)) {
               this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount);
            } else {
               this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(
                  this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount, this.prf
               );
            }

            this.cipher.init(1, this.key, this.params);
         } catch (GeneralSecurityException var6) {
            throw new OperatorCreationException(var6.getMessage(), var6);
         }
      } else {
         if (!PEMUtilities.isPKCS12(this.algOID)) {
            throw new OperatorCreationException("unknown algorithm: " + this.algOID, null);
         }

         ASN1EncodableVector var9 = new ASN1EncodableVector();
         this.salt = new byte[20];
         this.random.nextBytes(this.salt);
         var9.add(new DEROctetString(this.salt));
         var9.add(new ASN1Integer(this.iterationCount));
         var1 = new AlgorithmIdentifier(this.algOID, PKCS12PBEParams.getInstance(new DERSequence(var9)));

         try {
            this.cipher.init(1, new PKCS12KeyWithParameters(this.password, this.salt, this.iterationCount));
         } catch (GeneralSecurityException var5) {
            throw new OperatorCreationException(var5.getMessage(), var5);
         }
      }

      return new OutputEncryptor() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var1;
         }

         @Override
         public OutputStream getOutputStream(OutputStream var1x) {
            return new CipherOutputStream(var1x, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
         }

         @Override
         public GenericKey getKey() {
            return new JceGenericKey(var1, JceOpenSSLPKCS8EncryptorBuilder.this.key);
         }
      };
   }
}
