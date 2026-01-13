package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKCS12KeyStoreSpi extends KeyStoreSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers, BCKeyStore {
   static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private static final int SALT_SIZE = 20;
   private static final int MIN_ITERATIONS = 51200;
   private static final PKCS12KeyStoreSpi.DefaultSecretKeyProvider keySizeProvider = new PKCS12KeyStoreSpi.DefaultSecretKeyProvider();
   private PKCS12KeyStoreSpi.IgnoresCaseHashtable keys = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
   private PKCS12KeyStoreSpi.IgnoresCaseHashtable localIds = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
   private PKCS12KeyStoreSpi.IgnoresCaseHashtable certs = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
   private Hashtable chainCerts = new Hashtable();
   private Hashtable keyCerts = new Hashtable();
   static final int NULL = 0;
   static final int CERTIFICATE = 1;
   static final int KEY = 2;
   static final int SECRET = 3;
   static final int SEALED = 4;
   static final int KEY_PRIVATE = 0;
   static final int KEY_PUBLIC = 1;
   static final int KEY_SECRET = 2;
   protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   private CertificateFactory certFact;
   private ASN1ObjectIdentifier keyAlgorithm;
   private ASN1ObjectIdentifier certAlgorithm;
   private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
   private int itCount = 102400;
   private int saltLength = 20;

   private static boolean isPBKDF2(ASN1ObjectIdentifier var0) {
      return var0.equals(NISTObjectIdentifiers.id_aes256_CBC)
         || var0.equals(NISTObjectIdentifiers.id_aes256_GCM)
         || var0.equals(NISTObjectIdentifiers.id_aes128_CBC)
         || var0.equals(NISTObjectIdentifiers.id_aes128_GCM);
   }

   private static int getKeyLength(ASN1ObjectIdentifier var0) {
      return !var0.equals(NISTObjectIdentifiers.id_aes256_CBC) && !var0.equals(NISTObjectIdentifiers.id_aes256_GCM) ? 16 : 32;
   }

   public PKCS12KeyStoreSpi(JcaJceHelper var1, ASN1ObjectIdentifier var2, ASN1ObjectIdentifier var3) {
      this.keyAlgorithm = var2;
      this.certAlgorithm = var3;

      try {
         this.certFact = var1.createCertificateFactory("X.509");
      } catch (Exception var5) {
         throw new IllegalArgumentException("can't create cert factory - " + var5.toString());
      }
   }

   private SubjectKeyIdentifier createSubjectKeyId(PublicKey var1) {
      try {
         SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(var1.getEncoded());
         return new SubjectKeyIdentifier(getDigest(var2));
      } catch (Exception var3) {
         throw new RuntimeException("error creating key");
      }
   }

   private static byte[] getDigest(SubjectPublicKeyInfo var0) {
      Digest var1 = DigestFactory.createSHA1();
      byte[] var2 = new byte[var1.getDigestSize()];
      byte[] var3 = var0.getPublicKeyData().getBytes();
      var1.update(var3, 0, var3.length);
      var1.doFinal(var2, 0);
      return var2;
   }

   @Override
   public void setRandom(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public boolean engineProbe(InputStream var1) throws IOException {
      return false;
   }

   @Override
   public Enumeration engineAliases() {
      Hashtable var1 = new Hashtable();
      Enumeration var2 = this.certs.keys();

      while (var2.hasMoreElements()) {
         var1.put(var2.nextElement(), "cert");
      }

      var2 = this.keys.keys();

      while (var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (var1.get(var3) == null) {
            var1.put(var3, "key");
         }
      }

      return var1.keys();
   }

   @Override
   public boolean engineContainsAlias(String var1) {
      return this.certs.get(var1) != null || this.keys.get(var1) != null;
   }

   @Override
   public void engineDeleteEntry(String var1) throws KeyStoreException {
      Certificate var2 = (Certificate)this.certs.remove(var1);
      if (var2 != null) {
         this.chainCerts.remove(new PKCS12KeyStoreSpi.CertId(var2.getPublicKey()));
      }

      Key var3 = (Key)this.keys.remove(var1);
      if (var3 != null) {
         String var4 = (String)this.localIds.remove(var1);
         if (var4 != null) {
            Certificate var5 = (Certificate)this.keyCerts.remove(var4);
            if (var5 != null) {
               this.chainCerts.remove(new PKCS12KeyStoreSpi.CertId(var5.getPublicKey()));
            }
         }
      }
   }

   @Override
   public Certificate engineGetCertificate(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null alias passed to getCertificate.");
      } else {
         Certificate var2 = (Certificate)this.certs.get(var1);
         if (var2 == null) {
            String var3 = (String)this.localIds.get(var1);
            if (var3 != null) {
               var2 = (Certificate)this.keyCerts.get(var3);
            } else {
               var2 = (Certificate)this.keyCerts.get(var1);
            }
         }

         return var2;
      }
   }

   @Override
   public String engineGetCertificateAlias(Certificate var1) {
      Enumeration var2 = this.certs.elements();
      Enumeration var3 = this.certs.keys();

      while (var2.hasMoreElements()) {
         Certificate var4 = (Certificate)var2.nextElement();
         String var5 = (String)var3.nextElement();
         if (var4.equals(var1)) {
            return var5;
         }
      }

      var2 = this.keyCerts.elements();
      var3 = this.keyCerts.keys();

      while (var2.hasMoreElements()) {
         Certificate var8 = (Certificate)var2.nextElement();
         String var9 = (String)var3.nextElement();
         if (var8.equals(var1)) {
            return var9;
         }
      }

      return null;
   }

   @Override
   public Certificate[] engineGetCertificateChain(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null alias passed to getCertificateChain.");
      } else if (!this.engineIsKeyEntry(var1)) {
         return null;
      } else {
         Object var2 = this.engineGetCertificate(var1);
         if (var2 == null) {
            return null;
         } else {
            Vector var3 = new Vector();

            while (var2 != null) {
               X509Certificate var4 = (X509Certificate)var2;
               Object var5 = null;
               byte[] var6 = var4.getExtensionValue(Extension.authorityKeyIdentifier.getId());
               if (var6 != null) {
                  AuthorityKeyIdentifier var7 = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(var6).getOctets());
                  byte[] var8 = var7.getKeyIdentifierOctets();
                  if (null != var8) {
                     var5 = (Certificate)this.chainCerts.get(new PKCS12KeyStoreSpi.CertId(var8));
                  }
               }

               if (var5 == null) {
                  Principal var16 = var4.getIssuerDN();
                  Principal var17 = var4.getSubjectDN();
                  if (!var16.equals(var17)) {
                     Enumeration var9 = this.chainCerts.keys();

                     while (var9.hasMoreElements()) {
                        X509Certificate var10 = (X509Certificate)this.chainCerts.get(var9.nextElement());
                        Principal var11 = var10.getSubjectDN();
                        if (var11.equals(var16)) {
                           try {
                              var4.verify(var10.getPublicKey());
                              var5 = var10;
                              break;
                           } catch (Exception var13) {
                           }
                        }
                     }
                  }
               }

               if (var3.contains(var2)) {
                  var2 = null;
               } else {
                  var3.addElement(var2);
                  if (var5 != var2) {
                     var2 = var5;
                  } else {
                     var2 = null;
                  }
               }
            }

            Certificate[] var14 = new Certificate[var3.size()];

            for (int var15 = 0; var15 != var14.length; var15++) {
               var14[var15] = (Certificate)var3.elementAt(var15);
            }

            return var14;
         }
      }
   }

   @Override
   public Date engineGetCreationDate(String var1) {
      if (var1 == null) {
         throw new NullPointerException("alias == null");
      } else {
         return this.keys.get(var1) == null && this.certs.get(var1) == null ? null : new Date();
      }
   }

   @Override
   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      if (var1 == null) {
         throw new IllegalArgumentException("null alias passed to getKey.");
      } else {
         return (Key)this.keys.get(var1);
      }
   }

   @Override
   public boolean engineIsCertificateEntry(String var1) {
      return this.certs.get(var1) != null && this.keys.get(var1) == null;
   }

   @Override
   public boolean engineIsKeyEntry(String var1) {
      return this.keys.get(var1) != null;
   }

   @Override
   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      if (this.keys.get(var1) != null) {
         throw new KeyStoreException("There is a key entry with the name " + var1 + ".");
      } else {
         this.certs.put(var1, var2);
         this.chainCerts.put(new PKCS12KeyStoreSpi.CertId(var2.getPublicKey()), var2);
      }
   }

   @Override
   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      throw new RuntimeException("operation not supported");
   }

   @Override
   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      if (!(var2 instanceof PrivateKey)) {
         throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
      } else if (var2 instanceof PrivateKey && var4 == null) {
         throw new KeyStoreException("no certificate chain for private key");
      } else {
         if (this.keys.get(var1) != null) {
            this.engineDeleteEntry(var1);
         }

         this.keys.put(var1, var2);
         if (var4 != null) {
            this.certs.put(var1, var4[0]);

            for (int var5 = 0; var5 != var4.length; var5++) {
               this.chainCerts.put(new PKCS12KeyStoreSpi.CertId(var4[var5].getPublicKey()), var4[var5]);
            }
         }
      }
   }

   @Override
   public int engineSize() {
      Hashtable var1 = new Hashtable();
      Enumeration var2 = this.certs.keys();

      while (var2.hasMoreElements()) {
         var1.put(var2.nextElement(), "cert");
      }

      var2 = this.keys.keys();

      while (var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         if (var1.get(var3) == null) {
            var1.put(var3, "key");
         }
      }

      return var1.size();
   }

   protected PrivateKey unwrapKey(AlgorithmIdentifier var1, byte[] var2, char[] var3, boolean var4) throws IOException {
      ASN1ObjectIdentifier var5 = var1.getAlgorithm();

      try {
         if (var5.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams var12 = PKCS12PBEParams.getInstance(var1.getParameters());
            PBEParameterSpec var7 = new PBEParameterSpec(var12.getIV(), this.validateIterationCount(var12.getIterations()));
            Cipher var8 = this.helper.createCipher(var5.getId());
            PKCS12Key var9 = new PKCS12Key(var3, var4);
            var8.init(4, var9, var7);
            return (PrivateKey)var8.unwrap(var2, "", 2);
         }

         if (var5.equals(PKCSObjectIdentifiers.id_PBES2)) {
            Cipher var6 = this.createCipher(4, var3, var1);
            return (PrivateKey)var6.unwrap(var2, "", 2);
         }
      } catch (InvalidKeyException var10) {
         throw Exceptions.ioException("exception unwrapping private key:" + var10.getMessage(), new UnrecoverableKeyException(var10.toString()));
      } catch (Exception var11) {
         throw Exceptions.ioException("exception unwrapping private key: " + var11.getMessage(), var11);
      }

      throw new IOException("exception unwrapping private key - cannot recognise: " + var5);
   }

   protected byte[] wrapKey(String var1, Key var2, PKCS12PBEParams var3, char[] var4) throws IOException {
      PBEKeySpec var5 = new PBEKeySpec(var4);

      try {
         SecretKeyFactory var7 = this.helper.createSecretKeyFactory(var1);
         PBEParameterSpec var8 = new PBEParameterSpec(var3.getIV(), BigIntegers.intValueExact(var3.getIterations()));
         Cipher var9 = this.helper.createCipher(var1);
         var9.init(3, var7.generateSecret(var5), var8);
         return var9.wrap(var2);
      } catch (Exception var10) {
         throw new IOException("exception encrypting data - " + var10.toString());
      }
   }

   protected byte[] wrapKey(EncryptionScheme var1, Key var2, PBKDF2Params var3, char[] var4) throws IOException {
      PBEKeySpec var5 = new PBEKeySpec(
         var4, var3.getSalt(), BigIntegers.intValueExact(var3.getIterationCount()), BigIntegers.intValueExact(var3.getKeyLength()) * 8
      );

      try {
         SecretKeyFactory var7 = this.helper.createSecretKeyFactory("PBKDF2withHMacSHA256");
         Cipher var8 = this.helper.createCipher(var1.getAlgorithm().getId());
         AlgorithmParameters var9 = AlgorithmParameters.getInstance(var1.getAlgorithm().getId());
         var9.init(var1.getParameters().toASN1Primitive().getEncoded());
         var8.init(3, var7.generateSecret(var5), var9);
         return var8.wrap(var2);
      } catch (Exception var10) {
         throw new IOException("exception encrypting data - " + var10.toString());
      }
   }

   protected byte[] cryptData(boolean var1, AlgorithmIdentifier var2, char[] var3, boolean var4, byte[] var5) throws IOException {
      ASN1ObjectIdentifier var6 = var2.getAlgorithm();
      int var7 = var1 ? 1 : 2;
      if (var6.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
         PKCS12PBEParams var20 = PKCS12PBEParams.getInstance(var2.getParameters());
         PKCS12Key var9 = new PKCS12Key(var3, var4);

         byte[] var12;
         try {
            PBEParameterSpec var10 = new PBEParameterSpec(var20.getIV(), BigIntegers.intValueExact(var20.getIterations()));
            Cipher var11 = this.helper.createCipher(var6.getId());
            var11.init(var7, var9, var10);
            var12 = var11.doFinal(var5);
         } catch (Exception var17) {
            throw new IOException("exception decrypting data - " + var17.toString());
         } finally {
            Arrays.clear(var9.getPassword());
         }

         return var12;
      } else if (var6.equals(PKCSObjectIdentifiers.id_PBES2)) {
         try {
            Cipher var8 = this.createCipher(var7, var3, var2);
            return var8.doFinal(var5);
         } catch (Exception var19) {
            throw new IOException("exception decrypting data - " + var19.toString());
         }
      } else {
         throw new IOException("unknown PBE algorithm: " + var6);
      }
   }

   private Cipher createCipher(int var1, char[] var2, AlgorithmIdentifier var3) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
      PBES2Parameters var4 = PBES2Parameters.getInstance(var3.getParameters());
      PBKDF2Params var5 = PBKDF2Params.getInstance(var4.getKeyDerivationFunc().getParameters());
      AlgorithmIdentifier var6 = AlgorithmIdentifier.getInstance(var4.getEncryptionScheme());
      SecretKeyFactory var7 = this.helper.createSecretKeyFactory(var4.getKeyDerivationFunc().getAlgorithm().getId());
      SecretKey var8;
      if (var5.isDefaultPrf()) {
         var8 = var7.generateSecret(
            new PBEKeySpec(var2, var5.getSalt(), this.validateIterationCount(var5.getIterationCount()), keySizeProvider.getKeySize(var6))
         );
      } else {
         var8 = var7.generateSecret(
            new PBKDF2KeySpec(var2, var5.getSalt(), this.validateIterationCount(var5.getIterationCount()), keySizeProvider.getKeySize(var6), var5.getPrf())
         );
      }

      Cipher var9 = this.helper.createCipher(var4.getEncryptionScheme().getAlgorithm().getId());
      ASN1Encodable var10 = var4.getEncryptionScheme().getParameters();
      if (var10 instanceof ASN1OctetString) {
         var9.init(var1, var8, new IvParameterSpec(ASN1OctetString.getInstance(var10).getOctets()));
      } else {
         ASN1Sequence var11 = ASN1Sequence.getInstance(var10);
         if (var11.getObjectAt(1) instanceof ASN1ObjectIdentifier) {
            GOST28147Parameters var12 = GOST28147Parameters.getInstance(var10);
            var9.init(var1, var8, new GOST28147ParameterSpec(var12.getEncryptionParamSet(), var12.getIV()));
         } else {
            AlgorithmParameters var15 = AlgorithmParameters.getInstance(var6.getAlgorithm().getId(), "BC");

            try {
               var15.init(var11.getEncoded());
            } catch (IOException var14) {
               throw new InvalidKeySpecException(var14.getMessage());
            }

            var9.init(var1, var8, var15);
         }
      }

      return var9;
   }

   @Override
   public void engineLoad(LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var1 == null) {
         this.engineLoad(null, null);
      } else {
         if (!(var1 instanceof BCLoadStoreParameter)) {
            throw new IllegalArgumentException("no support for 'param' of type " + var1.getClass().getName());
         }

         BCLoadStoreParameter var2 = (BCLoadStoreParameter)var1;
         this.engineLoad(var2.getInputStream(), ParameterUtil.extractPassword(var1));
      }
   }

   @Override
   public void engineLoad(InputStream var1, char[] var2) throws IOException {
      if (var1 != null) {
         boolean var3 = true;
         boolean var4 = true;
         BufferedInputStream var5 = new BufferedInputStream(var1);
         var5.mark(10);
         int var6 = var5.read();
         if (var6 < 0) {
            throw new EOFException("no data in keystore stream");
         } else if (var6 != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
         } else {
            var5.reset();
            ASN1InputStream var7 = new ASN1InputStream(var5);

            Pfx var8;
            try {
               var8 = Pfx.getInstance(var7.readObject());
            } catch (Exception var30) {
               throw new IOException(var30.getMessage());
            }

            ContentInfo var9 = var8.getAuthSafe();
            Vector var10 = new Vector();
            boolean var11 = false;
            boolean var12 = false;
            label138:
            if (var8.getMacData() == null) {
               this.keys = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
               this.localIds = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
               if (var9.getContentType().equals(data)) {
                  ASN1OctetString var31 = ASN1OctetString.getInstance(var9.getContent());
                  AuthenticatedSafe var33 = AuthenticatedSafe.getInstance(var31.getOctets());
                  ContentInfo[] var35 = var33.getContentInfo();

                  for (int var37 = 0; var37 != var35.length; var37++) {
                     if (var35[var37].getContentType().equals(data)) {
                        ASN1OctetString var41 = ASN1OctetString.getInstance(var35[var37].getContent());
                        ASN1Sequence var45 = ASN1Sequence.getInstance(var41.getOctets());

                        for (int var47 = 0; var47 != var45.size(); var47++) {
                           SafeBag var51 = SafeBag.getInstance(var45.getObjectAt(var47));
                           if (var51.getBagId().equals(pkcs8ShroudedKeyBag)) {
                              var11 = this.processShroudedKeyBag(var51, var2, var12);
                              var4 = false;
                           } else if (var51.getBagId().equals(certBag)) {
                              var10.addElement(var51);
                           } else if (var51.getBagId().equals(keyBag)) {
                              this.processKeyBag(var51);
                           } else {
                              System.out.println("extra in data " + var51.getBagId());
                              System.out.println(ASN1Dump.dumpAsString(var51));
                           }
                        }
                     } else if (var35[var37].getContentType().equals(encryptedData)) {
                        EncryptedData var40 = EncryptedData.getInstance(var35[var37].getContent());
                        byte[] var44 = this.cryptData(false, var40.getEncryptionAlgorithm(), var2, var12, var40.getContent().getOctets());
                        ASN1Sequence var19 = ASN1Sequence.getInstance(var44);
                        var4 = false;

                        for (int var20 = 0; var20 != var19.size(); var20++) {
                           SafeBag var21 = SafeBag.getInstance(var19.getObjectAt(var20));
                           if (var21.getBagId().equals(certBag)) {
                              var10.addElement(var21);
                           } else if (var21.getBagId().equals(pkcs8ShroudedKeyBag)) {
                              var11 = this.processShroudedKeyBag(var21, var2, var12);
                           } else if (var21.getBagId().equals(keyBag)) {
                              this.processKeyBag(var21);
                           } else {
                              System.out.println("extra in encryptedData " + var21.getBagId());
                              System.out.println(ASN1Dump.dumpAsString(var21));
                           }
                        }
                     } else {
                        System.out.println("extra " + var35[var37].getContentType().getId());
                        System.out.println("extra " + ASN1Dump.dumpAsString(var35[var37].getContent()));
                     }
                  }
               }

               this.certs = new PKCS12KeyStoreSpi.IgnoresCaseHashtable();
               this.chainCerts = new Hashtable();
               this.keyCerts = new Hashtable();

               for (int var32 = 0; var32 != var10.size(); var32++) {
                  SafeBag var34 = (SafeBag)var10.elementAt(var32);
                  CertBag var36 = CertBag.getInstance(var34.getBagValue());
                  if (!var36.getCertId().equals(x509Certificate)) {
                     throw new RuntimeException("Unsupported certificate type: " + var36.getCertId());
                  }

                  Certificate var38;
                  try {
                     ByteArrayInputStream var42 = new ByteArrayInputStream(((ASN1OctetString)var36.getCertValue()).getOctets());
                     var38 = this.certFact.generateCertificate(var42);
                  } catch (Exception var27) {
                     throw new RuntimeException(var27.toString());
                  }

                  ASN1OctetString var43 = null;
                  String var46 = null;
                  if (var34.getBagAttributes() != null) {
                     Enumeration var48 = var34.getBagAttributes().getObjects();

                     while (var48.hasMoreElements()) {
                        ASN1Sequence var52 = ASN1Sequence.getInstance(var48.nextElement());
                        ASN1ObjectIdentifier var53 = ASN1ObjectIdentifier.getInstance(var52.getObjectAt(0));
                        ASN1Set var22 = ASN1Set.getInstance(var52.getObjectAt(1));
                        if (var22.size() > 0) {
                           ASN1Primitive var23 = (ASN1Primitive)var22.getObjectAt(0);
                           PKCS12BagAttributeCarrier var24 = null;
                           if (var38 instanceof PKCS12BagAttributeCarrier) {
                              var24 = (PKCS12BagAttributeCarrier)var38;
                              ASN1Encodable var25 = var24.getBagAttribute(var53);
                              if (var25 != null) {
                                 if (var53.equals(pkcs_9_at_localKeyId)) {
                                    String var26 = Hex.toHexString(((ASN1OctetString)var23).getOctets());
                                    if (!this.keys.keys.containsKey(var26) && !this.localIds.keys.containsKey(var26)) {
                                       continue;
                                    }
                                 }

                                 if (!var25.toASN1Primitive().equals(var23)) {
                                    throw new IOException("attempt to add existing attribute with different value");
                                 }
                              } else if (var22.size() > 1) {
                                 var24.setBagAttribute(var53, var22);
                              } else {
                                 var24.setBagAttribute(var53, var23);
                              }
                           }

                           if (var53.equals(pkcs_9_at_friendlyName)) {
                              var46 = ((ASN1BMPString)var23).getString();
                           } else if (var53.equals(pkcs_9_at_localKeyId)) {
                              var43 = (ASN1OctetString)var23;
                           }
                        }
                     }
                  }

                  this.chainCerts.put(new PKCS12KeyStoreSpi.CertId(var38.getPublicKey()), var38);
                  if (var11) {
                     if (this.keyCerts.isEmpty()) {
                        String var49 = new String(Hex.encode(this.createSubjectKeyId(var38.getPublicKey()).getKeyIdentifier()));
                        this.keyCerts.put(var49, var38);
                        this.keys.put(var49, this.keys.remove("unmarked"));
                     }
                  } else {
                     if (var43 != null) {
                        String var50 = new String(Hex.encode(var43.getOctets()));
                        this.keyCerts.put(var50, var38);
                     }

                     if (var46 != null) {
                        this.certs.put(var46, var38);
                     }
                  }
               }

               if (var3 && var4 && var2 != null && var2.length != 0 && !Properties.isOverrideSet("org.bouncycastle.pkcs12.ignore_useless_passwd")) {
                  throw new IOException("password supplied for keystore that does not require one");
               }
            } else if (var2 == null) {
               throw new NullPointerException("no password supplied when one expected");
            } else {
               var3 = false;
               MacData var13 = var8.getMacData();
               DigestInfo var14 = var13.getMac();
               this.macAlgorithm = var14.getAlgorithmId();
               byte[] var15 = var13.getSalt();
               this.itCount = this.validateIterationCount(var13.getIterationCount());
               this.saltLength = var15.length;
               byte[] var16 = ((ASN1OctetString)var9.getContent()).getOctets();

               try {
                  byte[] var17 = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), var15, this.itCount, var2, false, var16);
                  byte[] var18 = var14.getDigest();
                  if (!Arrays.constantTimeAreEqual(var17, var18)) {
                     if (var2.length > 0) {
                        throw Exceptions.ioException(
                           "PKCS12 key store mac invalid - wrong password or corrupted file", new UnrecoverableKeyException("PKCS12 key store mac invalid")
                        );
                     } else {
                        var17 = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), var15, this.itCount, var2, true, var16);
                        if (!Arrays.constantTimeAreEqual(var17, var18)) {
                           throw Exceptions.ioException(
                              "PKCS12 key store mac invalid - wrong password or corrupted file", new UnrecoverableKeyException("PKCS12 key store mac invalid")
                           );
                        } else {
                           var12 = true;
                        }
                     }
                  }
                  break label138;
               } catch (IOException var28) {
                  throw var28;
               } catch (Exception var29) {
                  throw new IOException("error constructing MAC: " + var29.toString());
               }
            }
         }
      }
   }

   private boolean processShroudedKeyBag(SafeBag var1, char[] var2, boolean var3) throws IOException {
      EncryptedPrivateKeyInfo var4 = EncryptedPrivateKeyInfo.getInstance(var1.getBagValue());
      PrivateKey var5 = this.unwrapKey(var4.getEncryptionAlgorithm(), var4.getEncryptedData(), var2, var3);
      String var6 = null;
      ASN1OctetString var7 = null;
      if (var1.getBagAttributes() != null) {
         Enumeration var8 = var1.getBagAttributes().getObjects();

         while (var8.hasMoreElements()) {
            ASN1Sequence var9 = (ASN1Sequence)var8.nextElement();
            ASN1ObjectIdentifier var10 = (ASN1ObjectIdentifier)var9.getObjectAt(0);
            ASN1Set var11 = (ASN1Set)var9.getObjectAt(1);
            ASN1Primitive var12 = null;
            if (var11.size() > 0) {
               var12 = (ASN1Primitive)var11.getObjectAt(0);
               if (var5 instanceof PKCS12BagAttributeCarrier) {
                  PKCS12BagAttributeCarrier var13 = (PKCS12BagAttributeCarrier)var5;
                  ASN1Encodable var14 = var13.getBagAttribute(var10);
                  if (var14 != null) {
                     if (!var14.toASN1Primitive().equals(var12)) {
                        throw new IOException("attempt to add existing attribute with different value");
                     }
                  } else {
                     var13.setBagAttribute(var10, var12);
                  }
               }
            }

            if (var10.equals(pkcs_9_at_friendlyName)) {
               var6 = ((ASN1BMPString)var12).getString();
               this.keys.put(var6, var5);
            } else if (var10.equals(pkcs_9_at_localKeyId)) {
               var7 = (ASN1OctetString)var12;
            }
         }
      }

      if (var7 != null) {
         String var15 = new String(Hex.encode(var7.getOctets()));
         if (var6 == null) {
            this.keys.put(var15, var5);
         } else {
            this.localIds.put(var6, var15);
         }

         return false;
      } else {
         this.keys.put("unmarked", var5);
         return true;
      }
   }

   private void processKeyBag(SafeBag var1) throws IOException {
      PrivateKeyInfo var2 = PrivateKeyInfo.getInstance(var1.getBagValue());
      PrivateKey var3 = BouncyCastleProvider.getPrivateKey(var2);
      String var4 = null;
      ASN1OctetString var5 = null;
      if (var3 instanceof PKCS12BagAttributeCarrier) {
         PKCS12BagAttributeCarrier var6 = (PKCS12BagAttributeCarrier)var3;
         Enumeration var7 = var1.getBagAttributes().getObjects();

         while (var7.hasMoreElements()) {
            ASN1Sequence var8 = ASN1Sequence.getInstance(var7.nextElement());
            ASN1ObjectIdentifier var9 = ASN1ObjectIdentifier.getInstance(var8.getObjectAt(0));
            ASN1Set var10 = ASN1Set.getInstance(var8.getObjectAt(1));
            Object var11 = null;
            if (var10.size() > 0) {
               var11 = (ASN1Primitive)var10.getObjectAt(0);
               ASN1Encodable var12 = var6.getBagAttribute(var9);
               if (var12 != null) {
                  if (!var12.toASN1Primitive().equals((ASN1Primitive)var11)) {
                     throw new IOException("attempt to add existing attribute with different value");
                  }
               } else {
                  var6.setBagAttribute(var9, (ASN1Encodable)var11);
               }

               if (var9.equals(pkcs_9_at_friendlyName)) {
                  var4 = ((ASN1BMPString)var11).getString();
                  this.keys.put(var4, var3);
               } else if (var9.equals(pkcs_9_at_localKeyId)) {
                  var5 = (ASN1OctetString)var11;
               }
            }
         }
      }

      String var13 = new String(Hex.encode(var5.getOctets()));
      if (var4 == null) {
         this.keys.put(var13, var3);
      } else {
         this.localIds.put(var4, var13);
      }
   }

   private int validateIterationCount(BigInteger var1) {
      int var2 = BigIntegers.intValueExact(var1);
      if (var2 < 0) {
         throw new IllegalStateException("negative iteration count found");
      } else {
         BigInteger var3 = Properties.asBigInteger("org.bouncycastle.pkcs12.max_it_count");
         if (var3 != null && BigIntegers.intValueExact(var3) < var2) {
            throw new IllegalStateException("iteration count " + var2 + " greater than " + BigIntegers.intValueExact(var3));
         } else {
            return var2;
         }
      }
   }

   private ASN1Primitive getAlgParams(ASN1ObjectIdentifier var1) {
      if (var1.equals(NISTObjectIdentifiers.id_aes128_CBC) || var1.equals(NISTObjectIdentifiers.id_aes256_CBC)) {
         byte[] var3 = new byte[16];
         this.random.nextBytes(var3);
         return new DEROctetString(var3);
      } else if (!var1.equals(NISTObjectIdentifiers.id_aes128_GCM) && !var1.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
         throw new IllegalStateException("unknown encryption OID in getAlgParams()");
      } else {
         byte[] var2 = new byte[12];
         this.random.nextBytes(var2);
         return new GCMParameters(var2, 16).toASN1Primitive();
      }
   }

   @Override
   public void engineStore(LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var1 == null) {
         throw new IllegalArgumentException("'param' arg cannot be null");
      } else if (!(var1 instanceof PKCS12StoreParameter) && !(var1 instanceof JDKPKCS12StoreParameter)) {
         throw new IllegalArgumentException("No support for 'param' of type " + var1.getClass().getName());
      } else {
         PKCS12StoreParameter var2;
         if (var1 instanceof PKCS12StoreParameter) {
            var2 = (PKCS12StoreParameter)var1;
         } else {
            var2 = new PKCS12StoreParameter(
               ((JDKPKCS12StoreParameter)var1).getOutputStream(),
               var1.getProtectionParameter(),
               ((JDKPKCS12StoreParameter)var1).isUseDEREncoding(),
               ((JDKPKCS12StoreParameter)var1).isOverwriteFriendlyName()
            );
         }

         ProtectionParameter var4 = var1.getProtectionParameter();
         char[] var3;
         if (var4 == null) {
            var3 = null;
         } else {
            if (!(var4 instanceof PasswordProtection)) {
               throw new IllegalArgumentException("No support for protection parameter of type " + var4.getClass().getName());
            }

            var3 = ((PasswordProtection)var4).getPassword();
         }

         this.doStore(var2.getOutputStream(), var3, var2.isForDEREncoding(), var2.isOverwriteFriendlyName());
      }
   }

   @Override
   public void engineStore(OutputStream var1, char[] var2) throws IOException {
      this.doStore(var1, var2, false, true);
   }

   private void syncFriendlyName() {
      Enumeration var1 = this.keys.keys();

      while (var1.hasMoreElements()) {
         String var2 = (String)var1.nextElement();
         PrivateKey var3 = (PrivateKey)this.keys.get(var2);
         if (var3 instanceof PKCS12BagAttributeCarrier) {
            ASN1Encodable var4 = ((PKCS12BagAttributeCarrier)var3).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
            if (var4 != null && !var2.equals(var4.toString())) {
               this.keys.put(var4.toString(), var3);
               this.keys.remove(var2);
            }
         }
      }

      var1 = this.certs.keys();

      while (var1.hasMoreElements()) {
         String var7 = (String)var1.nextElement();
         Certificate var9 = (Certificate)this.certs.get(var7);
         if (var9 instanceof PKCS12BagAttributeCarrier) {
            ASN1Encodable var11 = ((PKCS12BagAttributeCarrier)var9).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
            if (var11 != null && !var7.equals(var11.toString())) {
               this.certs.put(var11.toString(), var9);
               this.certs.remove(var7);
            }
         }
      }

      var1 = this.keyCerts.keys();

      while (var1.hasMoreElements()) {
         String var8 = (String)var1.nextElement();
         Certificate var10 = (Certificate)this.keyCerts.get(var8);
         if (var10 instanceof PKCS12BagAttributeCarrier) {
            ASN1Encodable var12 = ((PKCS12BagAttributeCarrier)var10).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
            if (var12 != null && !var8.equals(var12.toString())) {
               this.keyCerts.put(var12.toString(), var10);
               this.keyCerts.remove(var8);
            }
         }
      }
   }

   private void doStore(OutputStream var1, char[] var2, boolean var3, boolean var4) throws IOException {
      if (!var4) {
         this.syncFriendlyName();
      }

      if (this.keys.size() == 0) {
         if (var2 == null) {
            Enumeration var5 = this.certs.keys();
            ASN1EncodableVector var6 = new ASN1EncodableVector();

            while (var5.hasMoreElements()) {
               try {
                  String var7 = (String)var5.nextElement();
                  Certificate var8 = (Certificate)this.certs.get(var7);
                  SafeBag var9 = this.createSafeBag(var7, var8, var4);
                  var6.add(var9);
               } catch (CertificateEncodingException var27) {
                  throw new IOException("Error encoding certificate: " + var27.toString());
               }
            }

            if (var3) {
               ContentInfo var34 = new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(var6).getEncoded()));
               Pfx var38 = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(var34).getEncoded())), null);
               var38.encodeTo(var1, "DER");
            } else {
               ContentInfo var35 = new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(var6).getEncoded()));
               Pfx var39 = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(var35).getEncoded())), null);
               var39.encodeTo(var1, "BER");
            }

            return;
         }
      } else if (var2 == null) {
         throw new NullPointerException("no password supplied for PKCS#12 KeyStore");
      }

      ASN1EncodableVector var32 = new ASN1EncodableVector();
      Enumeration var33 = this.keys.keys();

      while (var33.hasMoreElements()) {
         byte[] var36 = new byte[20];
         this.random.nextBytes(var36);
         String var40 = (String)var33.nextElement();
         PrivateKey var42 = (PrivateKey)this.keys.get(var40);
         AlgorithmIdentifier var10;
         byte[] var11;
         if (isPBKDF2(this.keyAlgorithm)) {
            PBKDF2Params var12 = new PBKDF2Params(
               var36, 51200, getKeyLength(this.keyAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE)
            );
            EncryptionScheme var13 = new EncryptionScheme(this.keyAlgorithm, this.getAlgParams(this.keyAlgorithm));
            var10 = new AlgorithmIdentifier(
               PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, var12), var13)
            );
            var11 = this.wrapKey(var13, var42, var12, var2);
         } else {
            PKCS12PBEParams var46 = new PKCS12PBEParams(var36, 51200);
            var11 = this.wrapKey(this.keyAlgorithm.getId(), var42, var46, var2);
            var10 = new AlgorithmIdentifier(this.keyAlgorithm, var46.toASN1Primitive());
         }

         EncryptedPrivateKeyInfo var47 = new EncryptedPrivateKeyInfo(var10, var11);
         boolean var51 = false;
         ASN1EncodableVector var14 = new ASN1EncodableVector();
         if (var42 instanceof PKCS12BagAttributeCarrier) {
            PKCS12BagAttributeCarrier var15 = (PKCS12BagAttributeCarrier)var42;
            ASN1BMPString var16 = (ASN1BMPString)var15.getBagAttribute(pkcs_9_at_friendlyName);
            if (var4 && (var16 == null || !var16.getString().equals(var40))) {
               var15.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(var40));
            }

            if (var15.getBagAttribute(pkcs_9_at_localKeyId) == null) {
               Certificate var17 = this.engineGetCertificate(var40);
               var15.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(var17.getPublicKey()));
            }

            Enumeration var70 = var15.getBagAttributeKeys();

            while (var70.hasMoreElements()) {
               ASN1ObjectIdentifier var18 = (ASN1ObjectIdentifier)var70.nextElement();
               ASN1EncodableVector var19 = new ASN1EncodableVector();
               var19.add(var18);
               var19.add(new DERSet(var15.getBagAttribute(var18)));
               var51 = true;
               var14.add(new DERSequence(var19));
            }
         }

         if (!var51) {
            ASN1EncodableVector var58 = new ASN1EncodableVector();
            Certificate var65 = this.engineGetCertificate(var40);
            var58.add(pkcs_9_at_localKeyId);
            var58.add(new DERSet(this.createSubjectKeyId(var65.getPublicKey())));
            var14.add(new DERSequence(var58));
            var58 = new ASN1EncodableVector();
            var58.add(pkcs_9_at_friendlyName);
            var58.add(new DERSet(new DERBMPString(var40)));
            var14.add(new DERSequence(var58));
         }

         SafeBag var60 = new SafeBag(pkcs8ShroudedKeyBag, var47.toASN1Primitive(), new DERSet(var14));
         var32.add(var60);
      }

      byte[] var37 = new DERSequence(var32).getEncoded("DER");
      BEROctetString var41 = new BEROctetString(var37);
      byte[] var43 = new byte[20];
      this.random.nextBytes(var43);
      ASN1EncodableVector var44 = new ASN1EncodableVector();
      AlgorithmIdentifier var45;
      if (isPBKDF2(this.certAlgorithm)) {
         PBKDF2Params var48 = new PBKDF2Params(
            var43, 51200, getKeyLength(this.certAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE)
         );
         var45 = new AlgorithmIdentifier(
            PKCSObjectIdentifiers.id_PBES2,
            new PBES2Parameters(
               new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, var48), new EncryptionScheme(this.certAlgorithm, this.getAlgParams(this.certAlgorithm))
            )
         );
      } else {
         PKCS12PBEParams var49 = new PKCS12PBEParams(var43, 51200);
         var45 = new AlgorithmIdentifier(this.certAlgorithm, var49.toASN1Primitive());
      }

      Hashtable var50 = new Hashtable();
      Enumeration var52 = this.keys.keys();

      while (var52.hasMoreElements()) {
         try {
            String var55 = (String)var52.nextElement();
            Certificate var61 = this.engineGetCertificate(var55);
            boolean var66 = false;
            CertBag var71 = new CertBag(x509Certificate, new DEROctetString(var61.getEncoded()));
            ASN1EncodableVector var74 = new ASN1EncodableVector();
            if (var61 instanceof PKCS12BagAttributeCarrier) {
               PKCS12BagAttributeCarrier var77 = (PKCS12BagAttributeCarrier)var61;
               ASN1BMPString var20 = (ASN1BMPString)var77.getBagAttribute(pkcs_9_at_friendlyName);
               if (var4 && (var20 == null || !var20.getString().equals(var55))) {
                  var77.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(var55));
               }

               if (var77.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                  var77.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(var61.getPublicKey()));
               }

               for (Enumeration var21 = var77.getBagAttributeKeys(); var21.hasMoreElements(); var66 = true) {
                  ASN1ObjectIdentifier var22 = (ASN1ObjectIdentifier)var21.nextElement();
                  ASN1EncodableVector var23 = new ASN1EncodableVector();
                  var23.add(var22);
                  var23.add(new DERSet(var77.getBagAttribute(var22)));
                  var74.add(new DERSequence(var23));
               }
            }

            if (!var66) {
               ASN1EncodableVector var78 = new ASN1EncodableVector();
               var78.add(pkcs_9_at_localKeyId);
               var78.add(new DERSet(this.createSubjectKeyId(var61.getPublicKey())));
               var74.add(new DERSequence(var78));
               var78 = new ASN1EncodableVector();
               var78.add(pkcs_9_at_friendlyName);
               var78.add(new DERSet(new DERBMPString(var55)));
               var74.add(new DERSequence(var78));
            }

            SafeBag var80 = new SafeBag(certBag, var71.toASN1Primitive(), new DERSet(var74));
            var44.add(var80);
            var50.put(var61, var61);
         } catch (CertificateEncodingException var31) {
            throw new IOException("Error encoding certificate: " + var31.toString());
         }
      }

      var52 = this.certs.keys();

      while (var52.hasMoreElements()) {
         try {
            String var56 = (String)var52.nextElement();
            Certificate var62 = (Certificate)this.certs.get(var56);
            if (this.keys.get(var56) == null) {
               SafeBag var67 = this.createSafeBag(var56, var62, var4);
               var44.add(var67);
               var50.put(var62, var62);
            }
         } catch (CertificateEncodingException var29) {
            throw new IOException("Error encoding certificate: " + var29.toString());
         }
      }

      Set var57 = this.getUsedCertificateSet();
      var52 = this.chainCerts.keys();

      while (var52.hasMoreElements()) {
         try {
            PKCS12KeyStoreSpi.CertId var63 = (PKCS12KeyStoreSpi.CertId)var52.nextElement();
            Certificate var68 = (Certificate)this.chainCerts.get(var63);
            if (var57.contains(var68) && var50.get(var68) == null) {
               CertBag var72 = new CertBag(x509Certificate, new DEROctetString(var68.getEncoded()));
               ASN1EncodableVector var75 = new ASN1EncodableVector();
               if (var68 instanceof PKCS12BagAttributeCarrier) {
                  PKCS12BagAttributeCarrier var81 = (PKCS12BagAttributeCarrier)var68;
                  Enumeration var84 = var81.getBagAttributeKeys();

                  while (var84.hasMoreElements()) {
                     ASN1ObjectIdentifier var86 = (ASN1ObjectIdentifier)var84.nextElement();
                     if (!var86.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) {
                        ASN1EncodableVector var88 = new ASN1EncodableVector();
                        var88.add(var86);
                        var88.add(new DERSet(var81.getBagAttribute(var86)));
                        var75.add(new DERSequence(var88));
                     }
                  }
               }

               SafeBag var82 = new SafeBag(certBag, var72.toASN1Primitive(), new DERSet(var75));
               var44.add(var82);
            }
         } catch (CertificateEncodingException var30) {
            throw new IOException("Error encoding certificate: " + var30.toString());
         }
      }

      byte[] var64 = new DERSequence(var44).getEncoded("DER");
      byte[] var69 = this.cryptData(true, var45, var2, false, var64);
      EncryptedData var73 = new EncryptedData(data, var45, new BEROctetString(var69));
      ContentInfo[] var76 = new ContentInfo[]{new ContentInfo(data, var41), new ContentInfo(encryptedData, var73.toASN1Primitive())};
      AuthenticatedSafe var83 = new AuthenticatedSafe(var76);
      byte[] var85 = var83.getEncoded(var3 ? "DER" : "BER");
      ContentInfo var87 = new ContentInfo(data, new BEROctetString(var85));
      byte[] var89 = new byte[this.saltLength];
      this.random.nextBytes(var89);
      byte[] var90 = ((ASN1OctetString)var87.getContent()).getOctets();
      MacData var24;
      if (this.keyAlgorithm.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
         var24 = null;
      } else {
         try {
            byte[] var25 = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), var89, this.itCount, var2, false, var90);
            DigestInfo var26 = new DigestInfo(this.macAlgorithm, var25);
            var24 = new MacData(var26, var89, this.itCount);
         } catch (Exception var28) {
            throw new IOException("error constructing MAC: " + var28.toString());
         }
      }

      Pfx var91 = new Pfx(var87, var24);
      var91.encodeTo(var1, var3 ? "DER" : "BER");
   }

   private SafeBag createSafeBag(String var1, Certificate var2, boolean var3) throws CertificateEncodingException {
      CertBag var4 = new CertBag(x509Certificate, new DEROctetString(var2.getEncoded()));
      ASN1EncodableVector var5 = new ASN1EncodableVector();
      boolean var6 = false;
      if (var2 instanceof PKCS12BagAttributeCarrier) {
         PKCS12BagAttributeCarrier var7 = (PKCS12BagAttributeCarrier)var2;
         ASN1BMPString var8 = (ASN1BMPString)var7.getBagAttribute(pkcs_9_at_friendlyName);
         if (var3 && (var8 == null || !var8.getString().equals(var1)) && var1 != null) {
            var7.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(var1));
         }

         Enumeration var9 = var7.getBagAttributeKeys();

         while (var9.hasMoreElements()) {
            ASN1ObjectIdentifier var10 = (ASN1ObjectIdentifier)var9.nextElement();
            if (!var10.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId) && !var10.equals(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage)) {
               ASN1EncodableVector var11 = new ASN1EncodableVector();
               var11.add(var10);
               var11.add(new DERSet(var7.getBagAttribute(var10)));
               var5.add(new DERSequence(var11));
               var6 = true;
            }
         }
      }

      if (!var6) {
         ASN1EncodableVector var12 = new ASN1EncodableVector();
         var12.add(pkcs_9_at_friendlyName);
         var12.add(new DERSet(new DERBMPString(var1)));
         var5.add(new DERSequence(var12));
      }

      if (var2 instanceof X509Certificate) {
         TBSCertificate var13 = TBSCertificate.getInstance(((X509Certificate)var2).getTBSCertificate());
         ASN1OctetString var14 = Extensions.getExtensionValue(var13.getExtensions(), Extension.extendedKeyUsage);
         DERSet var15;
         if (var14 != null) {
            var15 = new DERSet(ExtendedKeyUsage.getInstance(var14.getOctets()).getUsages());
         } else {
            var15 = new DERSet(KeyPurposeId.anyExtendedKeyUsage);
         }

         var5.add(new DERSequence(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage, var15));
      }

      return new SafeBag(certBag, var4.toASN1Primitive(), new DERSet(var5));
   }

   private Set getUsedCertificateSet() {
      HashSet var1 = new HashSet();
      Enumeration var2 = this.keys.keys();

      while (var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         Certificate[] var4 = this.engineGetCertificateChain(var3);

         for (int var5 = 0; var5 != var4.length; var5++) {
            var1.add(var4[var5]);
         }
      }

      var2 = this.certs.keys();

      while (var2.hasMoreElements()) {
         String var7 = (String)var2.nextElement();
         Certificate var8 = this.engineGetCertificate(var7);
         var1.add(var8);
      }

      return var1;
   }

   private byte[] calculatePbeMac(ASN1ObjectIdentifier var1, byte[] var2, int var3, char[] var4, boolean var5, byte[] var6) throws Exception {
      if (PKCSObjectIdentifiers.id_PBMAC1.equals(var1)) {
         PBMAC1Params var7 = PBMAC1Params.getInstance(this.macAlgorithm.getParameters());
         if (var7 == null) {
            throw new IOException("If the DigestAlgorithmIdentifier is id-PBMAC1, then the parameters field must contain valid PBMAC1-params parameters.");
         }

         if (PKCSObjectIdentifiers.id_PBKDF2.equals(var7.getKeyDerivationFunc().getAlgorithm())) {
            PBKDF2Params var17 = PBKDF2Params.getInstance(var7.getKeyDerivationFunc().getParameters());
            if (var17.getKeyLength() == null) {
               throw new IOException("Key length must be present when using PBMAC1.");
            }

            HMac var18 = new HMac(getPrf(var7.getMessageAuthScheme().getAlgorithm()));
            PKCS5S2ParametersGenerator var19 = new PKCS5S2ParametersGenerator(getPrf(var17.getPrf().getAlgorithm()));
            var19.init(Strings.toUTF8ByteArray(var4), var17.getSalt(), BigIntegers.intValueExact(var17.getIterationCount()));
            CipherParameters var11 = var19.generateDerivedParameters(BigIntegers.intValueExact(var17.getKeyLength()) * 8);
            Arrays.clear(var19.getPassword());
            var18.init(var11);
            var18.update(var6, 0, var6.length);
            byte[] var12 = new byte[var18.getMacSize()];
            var18.doFinal(var12, 0);
            return var12;
         }
      }

      PBEParameterSpec var16 = new PBEParameterSpec(var2, var3);
      PKCS12Key var8 = new PKCS12Key(var4, var5);

      byte[] var10;
      try {
         Mac var9 = this.helper.createMac(var1.getId());
         var9.init(var8, var16);
         var9.update(var6);
         var10 = var9.doFinal();
      } finally {
         Arrays.clear(var8.getPassword());
      }

      return var10;
   }

   private static Digest getPrf(ASN1ObjectIdentifier var0) {
      if (PKCSObjectIdentifiers.id_hmacWithSHA256.equals(var0)) {
         return new SHA256Digest();
      } else if (PKCSObjectIdentifiers.id_hmacWithSHA512.equals(var0)) {
         return new SHA512Digest();
      } else {
         throw new IllegalArgumentException("unknown prf id " + var0);
      }
   }

   public static class BCPKCS12KeyStore extends AdaptingKeyStoreSpi {
      public BCPKCS12KeyStore() {
         super(
            new BCJcaJceHelper(),
            new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)
         );
      }
   }

   public static class BCPKCS12KeyStore3DES extends AdaptingKeyStoreSpi {
      public BCPKCS12KeyStore3DES() {
         super(
            new BCJcaJceHelper(),
            new PKCS12KeyStoreSpi(
               new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC
            )
         );
      }
   }

   public static class BCPKCS12KeyStoreAES256 extends AdaptingKeyStoreSpi {
      public BCPKCS12KeyStoreAES256() {
         super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
      }
   }

   public static class BCPKCS12KeyStoreAES256GCM extends AdaptingKeyStoreSpi {
      public BCPKCS12KeyStoreAES256GCM() {
         super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
      }
   }

   private class CertId {
      byte[] id;

      CertId(PublicKey nullx) {
         this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(nullx).getKeyIdentifier();
      }

      CertId(byte[] nullx) {
         this.id = nullx;
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(this.id);
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PKCS12KeyStoreSpi.CertId)) {
            return false;
         } else {
            PKCS12KeyStoreSpi.CertId var2 = (PKCS12KeyStoreSpi.CertId)var1;
            return Arrays.areEqual(this.id, var2.id);
         }
      }
   }

   public static class DefPKCS12KeyStore extends AdaptingKeyStoreSpi {
      public DefPKCS12KeyStore() {
         super(
            new DefaultJcaJceHelper(),
            new PKCS12KeyStoreSpi(
               new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC
            )
         );
      }
   }

   public static class DefPKCS12KeyStore3DES extends AdaptingKeyStoreSpi {
      public DefPKCS12KeyStore3DES() {
         super(
            new DefaultJcaJceHelper(),
            new PKCS12KeyStoreSpi(
               new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC
            )
         );
      }
   }

   public static class DefPKCS12KeyStoreAES256 extends AdaptingKeyStoreSpi {
      public DefPKCS12KeyStoreAES256() {
         super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
      }
   }

   public static class DefPKCS12KeyStoreAES256GCM extends AdaptingKeyStoreSpi {
      public DefPKCS12KeyStoreAES256GCM() {
         super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
      }
   }

   private static class DefaultSecretKeyProvider {
      private final Map KEY_SIZES;

      DefaultSecretKeyProvider() {
         HashMap var1 = new HashMap();
         var1.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
         var1.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
         var1.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
         var1.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
         var1.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
         var1.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf(128));
         var1.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf(256));
         var1.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
         var1.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
         var1.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
         var1.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
         this.KEY_SIZES = Collections.unmodifiableMap(var1);
      }

      public int getKeySize(AlgorithmIdentifier var1) {
         Integer var2 = (Integer)this.KEY_SIZES.get(var1.getAlgorithm());
         return var2 != null ? var2 : -1;
      }
   }

   private static class IgnoresCaseHashtable {
      private Hashtable orig = new Hashtable();
      private Hashtable keys = new Hashtable();

      private IgnoresCaseHashtable() {
      }

      public void put(String var1, Object var2) {
         String var3 = var1 == null ? null : Strings.toLowerCase(var1);
         String var4 = (String)this.keys.get(var3);
         if (var4 != null) {
            this.orig.remove(var4);
         }

         this.keys.put(var3, var1);
         this.orig.put(var1, var2);
      }

      public Enumeration keys() {
         return new Hashtable(this.orig).keys();
      }

      public Object remove(String var1) {
         String var2 = (String)this.keys.remove(var1 == null ? null : Strings.toLowerCase(var1));
         return var2 == null ? null : this.orig.remove(var2);
      }

      public Object get(String var1) {
         String var2 = (String)this.keys.get(var1 == null ? null : Strings.toLowerCase(var1));
         return var2 == null ? null : this.orig.get(var2);
      }

      public Enumeration elements() {
         return this.orig.elements();
      }

      public int size() {
         return this.orig.size();
      }
   }
}
