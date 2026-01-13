package org.bouncycastle.jcajce.provider.keystore.bcfks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAKey;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.EncryptedObjectStoreData;
import org.bouncycastle.asn1.bc.EncryptedPrivateKeyData;
import org.bouncycastle.asn1.bc.EncryptedSecretKeyData;
import org.bouncycastle.asn1.bc.ObjectData;
import org.bouncycastle.asn1.bc.ObjectDataSequence;
import org.bouncycastle.asn1.bc.ObjectStore;
import org.bouncycastle.asn1.bc.ObjectStoreData;
import org.bouncycastle.asn1.bc.ObjectStoreIntegrityCheck;
import org.bouncycastle.asn1.bc.PbkdMacIntegrityCheck;
import org.bouncycastle.asn1.bc.SecretKeyData;
import org.bouncycastle.asn1.bc.SignatureCheck;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.crypto.util.PBKDFConfig;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.ScryptParams;
import org.bouncycastle.internal.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.bouncycastle.jcajce.BCFKSStoreParameter;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class BcFKSKeyStoreSpi extends KeyStoreSpi {
   private static final Map<String, ASN1ObjectIdentifier> oidMap = new HashMap<>();
   private static final Map<ASN1ObjectIdentifier, String> publicAlgMap = new HashMap<>();
   private PublicKey verificationKey;
   private BCFKSLoadStoreParameter.CertChainValidator validator;
   private static final BigInteger CERTIFICATE = BigInteger.valueOf(0L);
   private static final BigInteger PRIVATE_KEY = BigInteger.valueOf(1L);
   private static final BigInteger SECRET_KEY = BigInteger.valueOf(2L);
   private static final BigInteger PROTECTED_PRIVATE_KEY = BigInteger.valueOf(3L);
   private static final BigInteger PROTECTED_SECRET_KEY = BigInteger.valueOf(4L);
   private final JcaJceHelper helper;
   private final Map<String, ObjectData> entries = new HashMap<>();
   private final Map<String, PrivateKey> privateKeyCache = new HashMap<>();
   private AlgorithmIdentifier hmacAlgorithm;
   private KeyDerivationFunc hmacPkbdAlgorithm;
   private AlgorithmIdentifier signatureAlgorithm;
   private Date creationDate;
   private Date lastModifiedDate;
   private ASN1ObjectIdentifier storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;

   private static String getPublicKeyAlg(ASN1ObjectIdentifier var0) {
      String var1 = publicAlgMap.get(var0);
      return var1 != null ? var1 : var0.getId();
   }

   BcFKSKeyStoreSpi(JcaJceHelper var1) {
      this.helper = var1;
   }

   @Override
   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectData var3 = this.entries.get(var1);
      if (var3 != null) {
         if (var3.getType().equals(PRIVATE_KEY) || var3.getType().equals(PROTECTED_PRIVATE_KEY)) {
            PrivateKey var12 = this.privateKeyCache.get(var1);
            if (var12 != null) {
               return var12;
            } else {
               EncryptedPrivateKeyData var13 = EncryptedPrivateKeyData.getInstance(var3.getData());
               EncryptedPrivateKeyInfo var14 = EncryptedPrivateKeyInfo.getInstance(var13.getEncryptedPrivateKeyInfo());

               try {
                  PrivateKeyInfo var7 = PrivateKeyInfo.getInstance(
                     this.decryptData("PRIVATE_KEY_ENCRYPTION", var14.getEncryptionAlgorithm(), var2, var14.getEncryptedData())
                  );
                  KeyFactory var8 = this.helper.createKeyFactory(getPublicKeyAlg(var7.getPrivateKeyAlgorithm().getAlgorithm()));
                  PrivateKey var9 = var8.generatePrivate(new PKCS8EncodedKeySpec(var7.getEncoded()));
                  this.privateKeyCache.put(var1, var9);
                  return var9;
               } catch (Exception var11) {
                  throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover private key (" + var1 + "): " + var11.getMessage());
               }
            }
         } else if (!var3.getType().equals(SECRET_KEY) && !var3.getType().equals(PROTECTED_SECRET_KEY)) {
            throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + var1 + "): type not recognized");
         } else {
            EncryptedSecretKeyData var4 = EncryptedSecretKeyData.getInstance(var3.getData());

            try {
               SecretKeyData var5 = SecretKeyData.getInstance(
                  this.decryptData("SECRET_KEY_ENCRYPTION", var4.getKeyEncryptionAlgorithm(), var2, var4.getEncryptedKeyData())
               );
               SecretKeyFactory var6 = this.helper.createSecretKeyFactory(var5.getKeyAlgorithm().getId());
               return var6.generateSecret(new SecretKeySpec(var5.getKeyBytes(), var5.getKeyAlgorithm().getId()));
            } catch (Exception var10) {
               throw new UnrecoverableKeyException("BCFKS KeyStore unable to recover secret key (" + var1 + "): " + var10.getMessage());
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public Certificate[] engineGetCertificateChain(String var1) {
      ObjectData var2 = this.entries.get(var1);
      if (var2 != null && (var2.getType().equals(PRIVATE_KEY) || var2.getType().equals(PROTECTED_PRIVATE_KEY))) {
         EncryptedPrivateKeyData var3 = EncryptedPrivateKeyData.getInstance(var2.getData());
         org.bouncycastle.asn1.x509.Certificate[] var4 = var3.getCertificateChain();
         X509Certificate[] var5 = new X509Certificate[var4.length];

         for (int var6 = 0; var6 != var5.length; var6++) {
            var5[var6] = this.decodeCertificate(var4[var6]);
         }

         return var5;
      } else {
         return null;
      }
   }

   @Override
   public Certificate engineGetCertificate(String var1) {
      ObjectData var2 = this.entries.get(var1);
      if (var2 != null) {
         if (var2.getType().equals(PRIVATE_KEY) || var2.getType().equals(PROTECTED_PRIVATE_KEY)) {
            EncryptedPrivateKeyData var3 = EncryptedPrivateKeyData.getInstance(var2.getData());
            org.bouncycastle.asn1.x509.Certificate[] var4 = var3.getCertificateChain();
            return this.decodeCertificate(var4[0]);
         }

         if (var2.getType().equals(CERTIFICATE)) {
            return this.decodeCertificate(var2.getData());
         }
      }

      return null;
   }

   private Certificate decodeCertificate(Object var1) {
      if (this.helper != null) {
         try {
            CertificateFactory var5 = this.helper.createCertificateFactory("X.509");
            return var5.generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(var1).getEncoded()));
         } catch (Exception var3) {
            return null;
         }
      } else {
         try {
            CertificateFactory var2 = CertificateFactory.getInstance("X.509");
            return var2.generateCertificate(new ByteArrayInputStream(org.bouncycastle.asn1.x509.Certificate.getInstance(var1).getEncoded()));
         } catch (Exception var4) {
            return null;
         }
      }
   }

   @Override
   public Date engineGetCreationDate(String var1) {
      ObjectData var2 = this.entries.get(var1);
      if (var2 != null) {
         try {
            return var2.getLastModifiedDate().getDate();
         } catch (ParseException var4) {
            return new Date();
         }
      } else {
         return null;
      }
   }

   @Override
   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      Date var5 = new Date();
      Date var6 = var5;
      ObjectData var7 = this.entries.get(var1);
      if (var7 != null) {
         var5 = this.extractCreationDate(var7, var5);
      }

      this.privateKeyCache.remove(var1);
      if (var2 instanceof PrivateKey) {
         if (var4 == null) {
            throw new KeyStoreException("BCFKS KeyStore requires a certificate chain for private key storage.");
         }

         try {
            byte[] var8 = var2.getEncoded();
            KeyDerivationFunc var9 = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
            byte[] var10 = this.generateKey(var9, "PRIVATE_KEY_ENCRYPTION", var3 != null ? var3 : new char[0], 32);
            EncryptedPrivateKeyInfo var11;
            if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
               Cipher var12 = this.createCipher("AES/CCM/NoPadding", var10);
               byte[] var13 = var12.doFinal(var8);
               AlgorithmParameters var14 = var12.getParameters();
               PBES2Parameters var15 = new PBES2Parameters(
                  var9, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(var14.getEncoded()))
               );
               var11 = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var15), var13);
            } else {
               Cipher var24 = this.createCipher("AESKWP", var10);
               byte[] var27 = var24.doFinal(var8);
               PBES2Parameters var31 = new PBES2Parameters(var9, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
               var11 = new EncryptedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var31), var27);
            }

            EncryptedPrivateKeyData var25 = this.createPrivateKeySequence(var11, var4);
            this.entries.put(var1, new ObjectData(PRIVATE_KEY, var1, var5, var6, var25.getEncoded(), null));
         } catch (Exception var18) {
            throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + var18.toString(), var18);
         }
      } else {
         if (!(var2 instanceof SecretKey)) {
            throw new KeyStoreException("BCFKS KeyStore unable to recognize key.");
         }

         if (var4 != null) {
            throw new KeyStoreException("BCFKS KeyStore cannot store certificate chain with secret key.");
         }

         try {
            byte[] var20 = var2.getEncoded();
            KeyDerivationFunc var21 = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 32);
            byte[] var22 = this.generateKey(var21, "SECRET_KEY_ENCRYPTION", var3 != null ? var3 : new char[0], 32);
            String var23 = Strings.toUpperCase(var2.getAlgorithm());
            SecretKeyData var26;
            if (var23.indexOf("AES") > -1) {
               var26 = new SecretKeyData(NISTObjectIdentifiers.aes, var20);
            } else {
               ASN1ObjectIdentifier var28 = oidMap.get(var23);
               if (var28 != null) {
                  var26 = new SecretKeyData(var28, var20);
               } else {
                  var28 = oidMap.get(var23 + "." + var20.length * 8);
                  if (var28 == null) {
                     throw new KeyStoreException("BCFKS KeyStore cannot recognize secret key (" + var23 + ") for storage.");
                  }

                  var26 = new SecretKeyData(var28, var20);
               }
            }

            EncryptedSecretKeyData var30;
            if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
               Cipher var32 = this.createCipher("AES/CCM/NoPadding", var22);
               byte[] var34 = var32.doFinal(var26.getEncoded());
               AlgorithmParameters var16 = var32.getParameters();
               PBES2Parameters var17 = new PBES2Parameters(
                  var21, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(var16.getEncoded()))
               );
               var30 = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var17), var34);
            } else {
               Cipher var33 = this.createCipher("AESKWP", var22);
               byte[] var35 = var33.doFinal(var26.getEncoded());
               PBES2Parameters var36 = new PBES2Parameters(var21, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
               var30 = new EncryptedSecretKeyData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var36), var35);
            }

            this.entries.put(var1, new ObjectData(SECRET_KEY, var1, var5, var6, var30.getEncoded(), null));
         } catch (Exception var19) {
            throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore exception storing private key: " + var19.toString(), var19);
         }
      }

      this.lastModifiedDate = var6;
   }

   private Cipher createCipher(String var1, byte[] var2) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
      Cipher var3 = this.helper.createCipher(var1);
      var3.init(1, new SecretKeySpec(var2, "AES"));
      return var3;
   }

   private SecureRandom getDefaultSecureRandom() {
      return CryptoServicesRegistrar.getSecureRandom();
   }

   private EncryptedPrivateKeyData createPrivateKeySequence(EncryptedPrivateKeyInfo var1, Certificate[] var2) throws CertificateEncodingException {
      org.bouncycastle.asn1.x509.Certificate[] var3 = new org.bouncycastle.asn1.x509.Certificate[var2.length];

      for (int var4 = 0; var4 != var2.length; var4++) {
         var3[var4] = org.bouncycastle.asn1.x509.Certificate.getInstance(var2[var4].getEncoded());
      }

      return new EncryptedPrivateKeyData(var1, var3);
   }

   @Override
   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      Date var4 = new Date();
      Date var5 = var4;
      ObjectData var6 = this.entries.get(var1);
      if (var6 != null) {
         var4 = this.extractCreationDate(var6, var4);
      }

      if (var3 != null) {
         EncryptedPrivateKeyInfo var7;
         try {
            var7 = EncryptedPrivateKeyInfo.getInstance(var2);
         } catch (Exception var11) {
            throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore private key encoding must be an EncryptedPrivateKeyInfo.", var11);
         }

         try {
            this.privateKeyCache.remove(var1);
            this.entries.put(var1, new ObjectData(PROTECTED_PRIVATE_KEY, var1, var4, var5, this.createPrivateKeySequence(var7, var3).getEncoded(), null));
         } catch (Exception var10) {
            throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + var10.toString(), var10);
         }
      } else {
         try {
            this.entries.put(var1, new ObjectData(PROTECTED_SECRET_KEY, var1, var4, var5, var2, null));
         } catch (Exception var9) {
            throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore exception storing protected private key: " + var9.toString(), var9);
         }
      }

      this.lastModifiedDate = var5;
   }

   @Override
   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      ObjectData var3 = this.entries.get(var1);
      Date var4 = new Date();
      Date var5 = var4;
      if (var3 != null) {
         if (!var3.getType().equals(CERTIFICATE)) {
            throw new KeyStoreException("BCFKS KeyStore already has a key entry with alias " + var1);
         }

         var4 = this.extractCreationDate(var3, var4);
      }

      try {
         this.entries.put(var1, new ObjectData(CERTIFICATE, var1, var4, var5, var2.getEncoded(), null));
      } catch (CertificateEncodingException var7) {
         throw new BcFKSKeyStoreSpi.ExtKeyStoreException("BCFKS KeyStore unable to handle certificate: " + var7.getMessage(), var7);
      }

      this.lastModifiedDate = var5;
   }

   private Date extractCreationDate(ObjectData var1, Date var2) {
      try {
         var2 = var1.getCreationDate().getDate();
      } catch (ParseException var4) {
      }

      return var2;
   }

   @Override
   public void engineDeleteEntry(String var1) throws KeyStoreException {
      ObjectData var2 = this.entries.get(var1);
      if (var2 != null) {
         this.privateKeyCache.remove(var1);
         this.entries.remove(var1);
         this.lastModifiedDate = new Date();
      }
   }

   @Override
   public Enumeration<String> engineAliases() {
      final Iterator var1 = new HashSet<>(this.entries.keySet()).iterator();
      return new Enumeration() {
         @Override
         public boolean hasMoreElements() {
            return var1.hasNext();
         }

         @Override
         public Object nextElement() {
            return var1.next();
         }
      };
   }

   @Override
   public boolean engineContainsAlias(String var1) {
      if (var1 == null) {
         throw new NullPointerException("alias value is null");
      } else {
         return this.entries.containsKey(var1);
      }
   }

   @Override
   public int engineSize() {
      return this.entries.size();
   }

   @Override
   public boolean engineIsKeyEntry(String var1) {
      ObjectData var2 = this.entries.get(var1);
      if (var2 == null) {
         return false;
      } else {
         BigInteger var3 = var2.getType();
         return var3.equals(PRIVATE_KEY) || var3.equals(SECRET_KEY) || var3.equals(PROTECTED_PRIVATE_KEY) || var3.equals(PROTECTED_SECRET_KEY);
      }
   }

   @Override
   public boolean engineIsCertificateEntry(String var1) {
      ObjectData var2 = this.entries.get(var1);
      return var2 != null ? var2.getType().equals(CERTIFICATE) : false;
   }

   @Override
   public String engineGetCertificateAlias(Certificate var1) {
      if (var1 == null) {
         return null;
      } else {
         byte[] var2;
         try {
            var2 = var1.getEncoded();
         } catch (CertificateEncodingException var8) {
            return null;
         }

         for (String var4 : this.entries.keySet()) {
            ObjectData var5 = this.entries.get(var4);
            if (var5.getType().equals(CERTIFICATE)) {
               if (Arrays.areEqual(var5.getData(), var2)) {
                  return var4;
               }
            } else if (var5.getType().equals(PRIVATE_KEY) || var5.getType().equals(PROTECTED_PRIVATE_KEY)) {
               try {
                  EncryptedPrivateKeyData var6 = EncryptedPrivateKeyData.getInstance(var5.getData());
                  if (Arrays.areEqual(var6.getCertificateChain()[0].toASN1Primitive().getEncoded(), var2)) {
                     return var4;
                  }
               } catch (IOException var7) {
               }
            }
         }

         return null;
      }
   }

   private byte[] generateKey(KeyDerivationFunc var1, String var2, char[] var3, int var4) throws IOException {
      byte[] var5 = PBEParametersGenerator.PKCS12PasswordToBytes(var3);
      byte[] var6 = PBEParametersGenerator.PKCS12PasswordToBytes(var2.toCharArray());
      int var7 = var4;
      if (MiscObjectIdentifiers.id_scrypt.equals(var1.getAlgorithm())) {
         ScryptParams var10 = ScryptParams.getInstance(var1.getParameters());
         if (var10.getKeyLength() != null) {
            var7 = var10.getKeyLength().intValue();
         } else if (var4 == -1) {
            throw new IOException("no keyLength found in ScryptParams");
         }

         return SCrypt.generate(
            Arrays.concatenate(var5, var6),
            var10.getSalt(),
            var10.getCostParameter().intValue(),
            var10.getBlockSize().intValue(),
            var10.getBlockSize().intValue(),
            var7
         );
      } else if (var1.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBKDF2)) {
         PBKDF2Params var8 = PBKDF2Params.getInstance(var1.getParameters());
         if (var8.getKeyLength() != null) {
            var7 = var8.getKeyLength().intValue();
         } else if (var4 == -1) {
            throw new IOException("no keyLength found in PBKDF2Params");
         }

         if (var8.getPrf().getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA512)) {
            PKCS5S2ParametersGenerator var11 = new PKCS5S2ParametersGenerator(new SHA512Digest());
            var11.init(Arrays.concatenate(var5, var6), var8.getSalt(), var8.getIterationCount().intValue());
            return ((KeyParameter)var11.generateDerivedParameters(var7 * 8)).getKey();
         } else if (var8.getPrf().getAlgorithm().equals(NISTObjectIdentifiers.id_hmacWithSHA3_512)) {
            PKCS5S2ParametersGenerator var9 = new PKCS5S2ParametersGenerator(new SHA3Digest(512));
            var9.init(Arrays.concatenate(var5, var6), var8.getSalt(), var8.getIterationCount().intValue());
            return ((KeyParameter)var9.generateDerivedParameters(var7 * 8)).getKey();
         } else {
            throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD PRF: " + var8.getPrf().getAlgorithm());
         }
      } else {
         throw new IOException("BCFKS KeyStore: unrecognized MAC PBKD.");
      }
   }

   private void verifySig(ASN1Encodable var1, SignatureCheck var2, PublicKey var3) throws GeneralSecurityException, IOException {
      Signature var4 = this.helper.createSignature(var2.getSignatureAlgorithm().getAlgorithm().getId());
      var4.initVerify(var3);
      var4.update(var1.toASN1Primitive().getEncoded("DER"));
      if (!var4.verify(var2.getSignature().getOctets())) {
         throw new IOException("BCFKS KeyStore corrupted: signature calculation failed");
      }
   }

   private void verifyMac(byte[] var1, PbkdMacIntegrityCheck var2, char[] var3) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
      byte[] var4 = this.calculateMac(var1, var2.getMacAlgorithm(), var2.getPbkdAlgorithm(), var3);
      if (!Arrays.constantTimeAreEqual(var4, var2.getMac())) {
         throw new IOException("BCFKS KeyStore corrupted: MAC calculation failed");
      }
   }

   private byte[] calculateMac(byte[] var1, AlgorithmIdentifier var2, KeyDerivationFunc var3, char[] var4) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
      String var5 = var2.getAlgorithm().getId();
      Mac var6 = this.helper.createMac(var5);

      try {
         var6.init(new SecretKeySpec(this.generateKey(var3, "INTEGRITY_CHECK", var4 != null ? var4 : new char[0], -1), var5));
      } catch (InvalidKeyException var8) {
         throw new IOException("Cannot set up MAC calculation: " + var8.getMessage());
      }

      return var6.doFinal(var1);
   }

   @Override
   public void engineStore(LoadStoreParameter var1) throws CertificateException, NoSuchAlgorithmException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("'parameter' arg cannot be null");
      } else {
         if (var1 instanceof BCFKSStoreParameter) {
            BCFKSStoreParameter var2 = (BCFKSStoreParameter)var1;
            char[] var3 = ParameterUtil.extractPassword(var1);
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(var2.getStorePBKDFConfig(), 64);
            this.engineStore(var2.getOutputStream(), var3);
         } else if (var1 instanceof BCFKSLoadStoreParameter) {
            BCFKSLoadStoreParameter var11 = (BCFKSLoadStoreParameter)var1;
            if (var11.getStoreSignatureKey() != null) {
               this.signatureAlgorithm = this.generateSignatureAlgId(var11.getStoreSignatureKey(), var11.getStoreSignatureAlgorithm());
               this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(var11.getStorePBKDFConfig(), 64);
               if (var11.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
                  this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
               } else {
                  this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
               }

               if (var11.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
                  this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
               } else {
                  this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
               }

               char[] var13 = ParameterUtil.extractPassword(var11);
               EncryptedObjectStoreData var4 = this.getEncryptedObjectStoreData(this.signatureAlgorithm, var13);

               try {
                  Signature var5 = this.helper.createSignature(this.signatureAlgorithm.getAlgorithm().getId());
                  var5.initSign((PrivateKey)var11.getStoreSignatureKey());
                  var5.update(var4.getEncoded());
                  X509Certificate[] var7 = var11.getStoreCertificates();
                  SignatureCheck var6;
                  if (var7 == null) {
                     var6 = new SignatureCheck(this.signatureAlgorithm, var5.sign());
                  } else {
                     org.bouncycastle.asn1.x509.Certificate[] var8 = new org.bouncycastle.asn1.x509.Certificate[var7.length];

                     for (int var9 = 0; var9 != var8.length; var9++) {
                        var8[var9] = org.bouncycastle.asn1.x509.Certificate.getInstance(var7[var9].getEncoded());
                     }

                     var6 = new SignatureCheck(this.signatureAlgorithm, var8, var5.sign());
                  }

                  ObjectStore var15 = new ObjectStore(var4, new ObjectStoreIntegrityCheck(var6));
                  var11.getOutputStream().write(var15.getEncoded());
                  var11.getOutputStream().flush();
               } catch (GeneralSecurityException var10) {
                  throw new IOException("error creating signature: " + var10.getMessage(), var10);
               }
            } else {
               char[] var14 = ParameterUtil.extractPassword(var11);
               this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(var11.getStorePBKDFConfig(), 64);
               if (var11.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
                  this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
               } else {
                  this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
               }

               if (var11.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
                  this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
               } else {
                  this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
               }

               this.engineStore(var11.getOutputStream(), var14);
            }
         } else {
            if (!(var1 instanceof BCLoadStoreParameter)) {
               throw new IllegalArgumentException("no support for 'parameter' of type " + var1.getClass().getName());
            }

            BCLoadStoreParameter var12 = (BCLoadStoreParameter)var1;
            this.engineStore(var12.getOutputStream(), ParameterUtil.extractPassword(var1));
         }
      }
   }

   @Override
   public void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (this.creationDate == null) {
         throw new IOException("KeyStore not initialized");
      } else {
         EncryptedObjectStoreData var3 = this.getEncryptedObjectStoreData(this.hmacAlgorithm, var2);
         if (MiscObjectIdentifiers.id_scrypt.equals(this.hmacPkbdAlgorithm.getAlgorithm())) {
            ScryptParams var4 = ScryptParams.getInstance(this.hmacPkbdAlgorithm.getParameters());
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, var4.getKeyLength().intValue());
         } else {
            PBKDF2Params var7 = PBKDF2Params.getInstance(this.hmacPkbdAlgorithm.getParameters());
            this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, var7.getKeyLength().intValue());
         }

         byte[] var8;
         try {
            var8 = this.calculateMac(var3.getEncoded(), this.hmacAlgorithm, this.hmacPkbdAlgorithm, var2);
         } catch (NoSuchProviderException var6) {
            throw new IOException("cannot calculate mac: " + var6.getMessage());
         }

         ObjectStore var5 = new ObjectStore(var3, new ObjectStoreIntegrityCheck(new PbkdMacIntegrityCheck(this.hmacAlgorithm, this.hmacPkbdAlgorithm, var8)));
         var1.write(var5.getEncoded());
         var1.flush();
      }
   }

   private EncryptedObjectStoreData getEncryptedObjectStoreData(AlgorithmIdentifier var1, char[] var2) throws IOException, NoSuchAlgorithmException {
      ObjectData[] var3 = this.entries.values().toArray(new ObjectData[this.entries.size()]);
      KeyDerivationFunc var4 = this.generatePkbdAlgorithmIdentifier(this.hmacPkbdAlgorithm, 32);
      byte[] var5 = this.generateKey(var4, "STORE_ENCRYPTION", var2 != null ? var2 : new char[0], 32);
      ObjectStoreData var6 = new ObjectStoreData(var1, this.creationDate, this.lastModifiedDate, new ObjectDataSequence(var3), null);

      try {
         EncryptedObjectStoreData var7;
         if (this.storeEncryptionAlgorithm.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
            Cipher var8 = this.createCipher("AES/CCM/NoPadding", var5);
            byte[] var9 = var8.doFinal(var6.getEncoded());
            AlgorithmParameters var10 = var8.getParameters();
            PBES2Parameters var11 = new PBES2Parameters(
               var4, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_CCM, CCMParameters.getInstance(var10.getEncoded()))
            );
            var7 = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var11), var9);
         } else {
            Cipher var17 = this.createCipher("AESKWP", var5);
            byte[] var18 = var17.doFinal(var6.getEncoded());
            PBES2Parameters var19 = new PBES2Parameters(var4, new EncryptionScheme(NISTObjectIdentifiers.id_aes256_wrap_pad));
            var7 = new EncryptedObjectStoreData(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, var19), var18);
         }

         return var7;
      } catch (NoSuchPaddingException var12) {
         throw new NoSuchAlgorithmException(var12.toString());
      } catch (BadPaddingException var13) {
         throw new IOException(var13.toString());
      } catch (IllegalBlockSizeException var14) {
         throw new IOException(var14.toString());
      } catch (InvalidKeyException var15) {
         throw new IOException(var15.toString());
      } catch (NoSuchProviderException var16) {
         throw new IOException(var16.toString());
      }
   }

   @Override
   public void engineLoad(LoadStoreParameter var1) throws CertificateException, NoSuchAlgorithmException, IOException {
      if (var1 == null) {
         this.engineLoad(null, null);
      } else if (var1 instanceof BCFKSLoadStoreParameter) {
         BCFKSLoadStoreParameter var2 = (BCFKSLoadStoreParameter)var1;
         char[] var3 = ParameterUtil.extractPassword(var2);
         this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(var2.getStorePBKDFConfig(), 64);
         if (var2.getStoreEncryptionAlgorithm() == BCFKSLoadStoreParameter.EncryptionAlgorithm.AES256_CCM) {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_CCM;
         } else {
            this.storeEncryptionAlgorithm = NISTObjectIdentifiers.id_aes256_wrap_pad;
         }

         if (var2.getStoreMacAlgorithm() == BCFKSLoadStoreParameter.MacAlgorithm.HmacSHA512) {
            this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
         } else {
            this.hmacAlgorithm = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
         }

         this.verificationKey = (PublicKey)var2.getStoreSignatureKey();
         this.validator = var2.getCertChainValidator();
         this.signatureAlgorithm = this.generateSignatureAlgId(this.verificationKey, var2.getStoreSignatureAlgorithm());
         AlgorithmIdentifier var4 = this.hmacAlgorithm;
         ASN1ObjectIdentifier var5 = this.storeEncryptionAlgorithm;
         InputStream var6 = var2.getInputStream();
         this.engineLoad(var6, var3);
         if (var6 != null && (!this.isSimilarHmacPbkd(var2.getStorePBKDFConfig(), this.hmacPkbdAlgorithm) || !var5.equals(this.storeEncryptionAlgorithm))) {
            throw new IOException("configuration parameters do not match existing store");
         }
      } else {
         if (!(var1 instanceof BCLoadStoreParameter)) {
            throw new IllegalArgumentException("no support for 'parameter' of type " + var1.getClass().getName());
         }

         BCLoadStoreParameter var7 = (BCLoadStoreParameter)var1;
         this.engineLoad(var7.getInputStream(), ParameterUtil.extractPassword(var1));
      }
   }

   private boolean isSimilarHmacPbkd(PBKDFConfig var1, KeyDerivationFunc var2) {
      if (!var1.getAlgorithm().equals(var2.getAlgorithm())) {
         return false;
      } else {
         if (MiscObjectIdentifiers.id_scrypt.equals(var2.getAlgorithm())) {
            if (!(var1 instanceof ScryptConfig)) {
               return false;
            }

            ScryptConfig var3 = (ScryptConfig)var1;
            ScryptParams var4 = ScryptParams.getInstance(var2.getParameters());
            if (var3.getSaltLength() != var4.getSalt().length
               || var3.getBlockSize() != var4.getBlockSize().intValue()
               || var3.getCostParameter() != var4.getCostParameter().intValue()
               || var3.getParallelizationParameter() != var4.getParallelizationParameter().intValue()) {
               return false;
            }
         } else {
            if (!(var1 instanceof PBKDF2Config)) {
               return false;
            }

            PBKDF2Config var5 = (PBKDF2Config)var1;
            PBKDF2Params var6 = PBKDF2Params.getInstance(var2.getParameters());
            if (var5.getSaltLength() != var6.getSalt().length || var5.getIterationCount() != var6.getIterationCount().intValue()) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      this.entries.clear();
      this.privateKeyCache.clear();
      this.lastModifiedDate = this.creationDate = null;
      this.hmacAlgorithm = null;
      if (var1 == null) {
         this.lastModifiedDate = this.creationDate = new Date();
         this.verificationKey = null;
         this.validator = null;
         this.hmacAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
         this.hmacPkbdAlgorithm = this.generatePkbdAlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, 64);
      } else {
         ASN1InputStream var3 = new ASN1InputStream(var1);

         ObjectStore var4;
         try {
            var4 = ObjectStore.getInstance(var3.readObject());
         } catch (Exception var14) {
            throw new IOException(var14.getMessage());
         }

         ObjectStoreIntegrityCheck var5 = var4.getIntegrityCheck();
         AlgorithmIdentifier var6;
         if (var5.getType() == 0) {
            PbkdMacIntegrityCheck var7 = PbkdMacIntegrityCheck.getInstance(var5.getIntegrityCheck());
            this.hmacAlgorithm = var7.getMacAlgorithm();
            this.hmacPkbdAlgorithm = var7.getPbkdAlgorithm();
            var6 = this.hmacAlgorithm;

            try {
               this.verifyMac(var4.getStoreData().toASN1Primitive().getEncoded(), var7, var2);
            } catch (NoSuchProviderException var13) {
               throw new IOException(var13.getMessage());
            }
         } else {
            if (var5.getType() != 1) {
               throw new IOException("BCFKS KeyStore unable to recognize integrity check.");
            }

            SignatureCheck var16 = SignatureCheck.getInstance(var5.getIntegrityCheck());
            var6 = var16.getSignatureAlgorithm();

            try {
               org.bouncycastle.asn1.x509.Certificate[] var8 = var16.getCertificates();
               if (this.validator != null) {
                  if (var8 == null) {
                     throw new IOException("validator specified but no certifcates in store");
                  }

                  CertificateFactory var9 = this.helper.createCertificateFactory("X.509");
                  X509Certificate[] var10 = new X509Certificate[var8.length];

                  for (int var11 = 0; var11 != var10.length; var11++) {
                     var10[var11] = (X509Certificate)var9.generateCertificate(new ByteArrayInputStream(var8[var11].getEncoded()));
                  }

                  if (!this.validator.isValid(var10)) {
                     throw new IOException("certificate chain in key store signature not valid");
                  }

                  this.verifySig(var4.getStoreData(), var16, var10[0].getPublicKey());
               } else {
                  this.verifySig(var4.getStoreData(), var16, this.verificationKey);
               }
            } catch (GeneralSecurityException var15) {
               throw new IOException("error verifying signature: " + var15.getMessage(), var15);
            }
         }

         ASN1Encodable var17 = var4.getStoreData();
         ObjectStoreData var18;
         if (var17 instanceof EncryptedObjectStoreData) {
            EncryptedObjectStoreData var19 = (EncryptedObjectStoreData)var17;
            AlgorithmIdentifier var21 = var19.getEncryptionAlgorithm();
            var18 = ObjectStoreData.getInstance(this.decryptData("STORE_ENCRYPTION", var21, var2, var19.getEncryptedContent().getOctets()));
         } else {
            var18 = ObjectStoreData.getInstance(var17);
         }

         try {
            this.creationDate = var18.getCreationDate().getDate();
            this.lastModifiedDate = var18.getLastModifiedDate().getDate();
         } catch (ParseException var12) {
            throw new IOException("BCFKS KeyStore unable to parse store data information.");
         }

         if (!var18.getIntegrityAlgorithm().equals(var6)) {
            throw new IOException("BCFKS KeyStore storeData integrity algorithm does not match store integrity algorithm.");
         } else {
            Iterator var20 = var18.getObjectDataSequence().iterator();

            while (var20.hasNext()) {
               ObjectData var22 = ObjectData.getInstance(var20.next());
               this.entries.put(var22.getIdentifier(), var22);
            }
         }
      }
   }

   private byte[] decryptData(String var1, AlgorithmIdentifier var2, char[] var3, byte[] var4) throws IOException {
      if (!var2.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2)) {
         throw new IOException("BCFKS KeyStore cannot recognize protection algorithm.");
      } else {
         PBES2Parameters var5 = PBES2Parameters.getInstance(var2.getParameters());
         EncryptionScheme var6 = var5.getEncryptionScheme();

         try {
            Cipher var7;
            AlgorithmParameters var8;
            if (var6.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_CCM)) {
               var7 = this.helper.createCipher("AES/CCM/NoPadding");
               var8 = this.helper.createAlgorithmParameters("CCM");
               CCMParameters var9 = CCMParameters.getInstance(var6.getParameters());
               var8.init(var9.getEncoded());
            } else {
               if (!var6.getAlgorithm().equals(NISTObjectIdentifiers.id_aes256_wrap_pad)) {
                  throw new IOException("BCFKS KeyStore cannot recognize protection encryption algorithm.");
               }

               var7 = this.helper.createCipher("AESKWP");
               var8 = null;
            }

            byte[] var13 = this.generateKey(var5.getKeyDerivationFunc(), var1, var3 != null ? var3 : new char[0], 32);
            var7.init(2, new SecretKeySpec(var13, "AES"), var8);
            return var7.doFinal(var4);
         } catch (IOException var11) {
            throw var11;
         } catch (Exception var12) {
            throw new IOException(var12.toString());
         }
      }
   }

   private AlgorithmIdentifier generateSignatureAlgId(Key var1, BCFKSLoadStoreParameter.SignatureAlgorithm var2) throws IOException {
      if (var1 == null) {
         return null;
      } else {
         if (var1 instanceof ECKey) {
            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withECDSA) {
               return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512);
            }

            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withECDSA) {
               return new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
            }
         }

         if (var1 instanceof DSAKey) {
            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withDSA) {
               return new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512);
            }

            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withDSA) {
               return new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512);
            }
         }

         if (var1 instanceof RSAKey) {
            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA512withRSA) {
               return new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE);
            }

            if (var2 == BCFKSLoadStoreParameter.SignatureAlgorithm.SHA3_512withRSA) {
               return new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, DERNull.INSTANCE);
            }
         }

         throw new IOException("unknown signature algorithm");
      }
   }

   private KeyDerivationFunc generatePkbdAlgorithmIdentifier(PBKDFConfig var1, int var2) {
      if (MiscObjectIdentifiers.id_scrypt.equals(var1.getAlgorithm())) {
         ScryptConfig var6 = (ScryptConfig)var1;
         byte[] var7 = new byte[var6.getSaltLength()];
         this.getDefaultSecureRandom().nextBytes(var7);
         ScryptParams var5 = new ScryptParams(var7, var6.getCostParameter(), var6.getBlockSize(), var6.getParallelizationParameter(), var2);
         return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, var5);
      } else {
         PBKDF2Config var3 = (PBKDF2Config)var1;
         byte[] var4 = new byte[var3.getSaltLength()];
         this.getDefaultSecureRandom().nextBytes(var4);
         return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, new PBKDF2Params(var4, var3.getIterationCount(), var2, var3.getPRF()));
      }
   }

   private KeyDerivationFunc generatePkbdAlgorithmIdentifier(KeyDerivationFunc var1, int var2) {
      if (MiscObjectIdentifiers.id_scrypt.equals(var1.getAlgorithm())) {
         ScryptParams var6 = ScryptParams.getInstance(var1.getParameters());
         byte[] var7 = new byte[var6.getSalt().length];
         this.getDefaultSecureRandom().nextBytes(var7);
         ScryptParams var8 = new ScryptParams(var7, var6.getCostParameter(), var6.getBlockSize(), var6.getParallelizationParameter(), BigInteger.valueOf(var2));
         return new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, var8);
      } else {
         PBKDF2Params var3 = PBKDF2Params.getInstance(var1.getParameters());
         byte[] var4 = new byte[var3.getSalt().length];
         this.getDefaultSecureRandom().nextBytes(var4);
         PBKDF2Params var5 = new PBKDF2Params(var4, var3.getIterationCount().intValue(), var2, var3.getPrf());
         return new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, var5);
      }
   }

   private KeyDerivationFunc generatePkbdAlgorithmIdentifier(ASN1ObjectIdentifier var1, int var2) {
      byte[] var3 = new byte[64];
      this.getDefaultSecureRandom().nextBytes(var3);
      if (PKCSObjectIdentifiers.id_PBKDF2.equals(var1)) {
         return new KeyDerivationFunc(
            PKCSObjectIdentifiers.id_PBKDF2,
            new PBKDF2Params(var3, 51200, var2, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE))
         );
      } else {
         throw new IllegalStateException("unknown derivation algorithm: " + var1);
      }
   }

   static {
      oidMap.put("DESEDE", OIWObjectIdentifiers.desEDE);
      oidMap.put("TRIPLEDES", OIWObjectIdentifiers.desEDE);
      oidMap.put("TDEA", OIWObjectIdentifiers.desEDE);
      oidMap.put("HMACSHA1", PKCSObjectIdentifiers.id_hmacWithSHA1);
      oidMap.put("HMACSHA224", PKCSObjectIdentifiers.id_hmacWithSHA224);
      oidMap.put("HMACSHA256", PKCSObjectIdentifiers.id_hmacWithSHA256);
      oidMap.put("HMACSHA384", PKCSObjectIdentifiers.id_hmacWithSHA384);
      oidMap.put("HMACSHA512", PKCSObjectIdentifiers.id_hmacWithSHA512);
      oidMap.put("HMACSHA512/224", PKCSObjectIdentifiers.id_hmacWithSHA512_224);
      oidMap.put("HMACSHA512/256", PKCSObjectIdentifiers.id_hmacWithSHA512_256);
      oidMap.put("HMACSHA512(224)", PKCSObjectIdentifiers.id_hmacWithSHA512_224);
      oidMap.put("HMACSHA512(256)", PKCSObjectIdentifiers.id_hmacWithSHA512_256);
      oidMap.put("HMACSHA3-224", NISTObjectIdentifiers.id_hmacWithSHA3_224);
      oidMap.put("HMACSHA3-256", NISTObjectIdentifiers.id_hmacWithSHA3_256);
      oidMap.put("HMACSHA3-384", NISTObjectIdentifiers.id_hmacWithSHA3_384);
      oidMap.put("HMACSHA3-512", NISTObjectIdentifiers.id_hmacWithSHA3_512);
      oidMap.put("KMAC128", NISTObjectIdentifiers.id_Kmac128);
      oidMap.put("KMAC256", NISTObjectIdentifiers.id_Kmac256);
      oidMap.put("SEED", KISAObjectIdentifiers.id_seedCBC);
      oidMap.put("CAMELLIA.128", NTTObjectIdentifiers.id_camellia128_cbc);
      oidMap.put("CAMELLIA.192", NTTObjectIdentifiers.id_camellia192_cbc);
      oidMap.put("CAMELLIA.256", NTTObjectIdentifiers.id_camellia256_cbc);
      oidMap.put("ARIA.128", NSRIObjectIdentifiers.id_aria128_cbc);
      oidMap.put("ARIA.192", NSRIObjectIdentifiers.id_aria192_cbc);
      oidMap.put("ARIA.256", NSRIObjectIdentifiers.id_aria256_cbc);
      publicAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
      publicAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "EC");
      publicAlgMap.put(OIWObjectIdentifiers.elGamalAlgorithm, "DH");
      publicAlgMap.put(PKCSObjectIdentifiers.dhKeyAgreement, "DH");
      publicAlgMap.put(X9ObjectIdentifiers.id_dsa, "DSA");
   }

   public static class Def extends BcFKSKeyStoreSpi {
      public Def() {
         super(new DefaultJcaJceHelper());
      }
   }

   public static class DefCompat extends AdaptingKeyStoreSpi {
      public DefCompat() {
         super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
      }
   }

   public static class DefShared extends BcFKSKeyStoreSpi.SharedKeyStoreSpi {
      public DefShared() {
         super(new DefaultJcaJceHelper());
      }
   }

   public static class DefSharedCompat extends AdaptingKeyStoreSpi {
      public DefSharedCompat() {
         super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new DefaultJcaJceHelper()));
      }
   }

   private static class ExtKeyStoreException extends KeyStoreException {
      private final Throwable cause;

      ExtKeyStoreException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }

   private static class SharedKeyStoreSpi extends BcFKSKeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
      private final Map<String, byte[]> cache;
      private final byte[] seedKey;

      public SharedKeyStoreSpi(JcaJceHelper var1) {
         super(var1);

         try {
            this.seedKey = new byte[32];
            var1.createSecureRandom("DEFAULT").nextBytes(this.seedKey);
         } catch (GeneralSecurityException var3) {
            throw new IllegalArgumentException("can't create random - " + var3.toString());
         }

         this.cache = new HashMap<>();
      }

      @Override
      public void engineDeleteEntry(String var1) throws KeyStoreException {
         throw new KeyStoreException("delete operation not supported in shared mode");
      }

      @Override
      public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
         throw new KeyStoreException("set operation not supported in shared mode");
      }

      @Override
      public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
         throw new KeyStoreException("set operation not supported in shared mode");
      }

      @Override
      public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
         throw new KeyStoreException("set operation not supported in shared mode");
      }

      @Override
      public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
         byte[] var3;
         try {
            var3 = this.calculateMac(var1, var2);
         } catch (InvalidKeyException var5) {
            throw new UnrecoverableKeyException("unable to recover key (" + var1 + "): " + var5.getMessage());
         }

         if (this.cache.containsKey(var1)) {
            byte[] var4 = this.cache.get(var1);
            if (!Arrays.constantTimeAreEqual(var4, var3)) {
               throw new UnrecoverableKeyException("unable to recover key (" + var1 + ")");
            }
         }

         Key var6 = super.engineGetKey(var1, var2);
         if (var6 != null && !this.cache.containsKey(var1)) {
            this.cache.put(var1, var3);
         }

         return var6;
      }

      private byte[] calculateMac(String var1, char[] var2) throws NoSuchAlgorithmException, InvalidKeyException {
         byte[] var3;
         if (var2 != null) {
            var3 = Arrays.concatenate(Strings.toUTF8ByteArray(var2), Strings.toUTF8ByteArray(var1));
         } else {
            var3 = Arrays.concatenate(this.seedKey, Strings.toUTF8ByteArray(var1));
         }

         return SCrypt.generate(var3, this.seedKey, 16384, 8, 1, 32);
      }
   }

   public static class Std extends BcFKSKeyStoreSpi {
      public Std() {
         super(new BCJcaJceHelper());
      }
   }

   public static class StdCompat extends AdaptingKeyStoreSpi {
      public StdCompat() {
         super(new DefaultJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
      }
   }

   public static class StdShared extends BcFKSKeyStoreSpi.SharedKeyStoreSpi {
      public StdShared() {
         super(new BCJcaJceHelper());
      }
   }

   public static class StdSharedCompat extends AdaptingKeyStoreSpi {
      public StdSharedCompat() {
         super(new BCJcaJceHelper(), new BcFKSKeyStoreSpi(new BCJcaJceHelper()));
      }
   }
}
