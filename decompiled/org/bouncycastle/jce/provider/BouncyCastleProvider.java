package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Provider.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;
import org.bouncycastle.util.Strings;

public final class BouncyCastleProvider extends Provider implements ConfigurableProvider {
   private static final Logger LOG = Logger.getLogger(BouncyCastleProvider.class.getName());
   private static String info = "BouncyCastle Security Provider v1.83";
   public static final String PROVIDER_NAME = "BC";
   public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
   private static final Map keyInfoConverters = new HashMap();
   private static final Class revChkClass = ClassUtil.loadClass(BouncyCastleProvider.class, "java.security.cert.PKIXRevocationChecker");
   private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
   private static final String[] SYMMETRIC_GENERIC = new String[]{"PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF", "SCRYPT", "HKDF"};
   private static final String[] SYMMETRIC_MACS = new String[]{"SipHash", "SipHash128", "Poly1305"};
   private static final CryptoServiceProperties[] SYMMETRIC_CIPHERS = new CryptoServiceProperties[]{
      service("AES", 256),
      service("ARC4", 20),
      service("ARIA", 256),
      service("Blowfish", 128),
      service("Camellia", 256),
      service("CAST5", 128),
      service("CAST6", 256),
      service("ChaCha", 128),
      service("DES", 56),
      service("DESede", 112),
      service("GOST28147", 128),
      service("Grainv1", 128),
      service("Grain128", 128),
      service("HC128", 128),
      service("HC256", 256),
      service("IDEA", 128),
      service("Noekeon", 128),
      service("RC2", 128),
      service("RC5", 128),
      service("RC6", 256),
      service("Rijndael", 256),
      service("Salsa20", 128),
      service("SEED", 128),
      service("Serpent", 256),
      service("Shacal2", 128),
      service("Skipjack", 80),
      service("SM4", 128),
      service("TEA", 128),
      service("Twofish", 256),
      service("Threefish", 128),
      service("VMPC", 128),
      service("VMPCKSA3", 128),
      service("XTEA", 128),
      service("XSalsa20", 128),
      service("OpenSSLPBKDF", 128),
      service("DSTU7624", 256),
      service("GOST3412_2015", 256),
      service("Zuc", 128)
   };
   private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
   private static final String[] ASYMMETRIC_GENERIC = new String[]{"X509", "IES", "COMPOSITE", "EXTERNAL", "CompositeSignatures", "NoSig"};
   private static final String[] ASYMMETRIC_CIPHERS = new String[]{
      "DSA",
      "DH",
      "EC",
      "RSA",
      "GOST",
      "ECGOST",
      "ElGamal",
      "DSTU4145",
      "GM",
      "EdEC",
      "LMS",
      "SPHINCSPlus",
      "Dilithium",
      "Falcon",
      "NTRU",
      "CONTEXT",
      "SLHDSA",
      "MLDSA",
      "MLKEM"
   };
   private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
   private static final String[] DIGESTS = new String[]{
      "GOST3411",
      "Keccak",
      "MD2",
      "MD4",
      "MD5",
      "SHA1",
      "RIPEMD128",
      "RIPEMD160",
      "RIPEMD256",
      "RIPEMD320",
      "SHA224",
      "SHA256",
      "SHA384",
      "SHA512",
      "SHA3",
      "Skein",
      "SM3",
      "Tiger",
      "Whirlpool",
      "Blake2b",
      "Blake2s",
      "DSTU7564",
      "Haraka",
      "Blake3"
   };
   private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
   private static final String[] KEYSTORES = new String[]{"BC", "BCFKS", "PKCS12"};
   private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
   private static final String[] SECURE_RANDOMS = new String[]{"DRBG"};
   private static final String KDF_PACKAGE = "org.bouncycastle.jcajce.provider.kdf.";
   private static final String[] KDFS = new String[]{"HKDF", "PBEPBKDF2", "SCRYPT"};
   private Map<String, Service> serviceMap = new ConcurrentHashMap<>();

   public BouncyCastleProvider() {
      super("BC", 1.83, info);
      AccessController.doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            BouncyCastleProvider.this.setup();
            return null;
         }
      });
   }

   private void setup() {
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.digest.", DIGESTS);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_GENERIC);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_MACS);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.symmetric.", SYMMETRIC_CIPHERS);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", ASYMMETRIC_GENERIC);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.asymmetric.", ASYMMETRIC_CIPHERS);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.keystore.", KEYSTORES);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.drbg.", SECURE_RANDOMS);
      this.loadAlgorithms("org.bouncycastle.jcajce.provider.kdf.", KDFS);
      this.loadPQCKeys();
      this.put("X509Store.CERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertCollection");
      this.put("X509Store.ATTRIBUTECERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreAttrCertCollection");
      this.put("X509Store.CRL/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCRLCollection");
      this.put("X509Store.CERTIFICATEPAIR/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertPairCollection");
      this.put("X509Store.CERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCerts");
      this.put("X509Store.CRL/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCRLs");
      this.put("X509Store.ATTRIBUTECERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPAttrCerts");
      this.put("X509Store.CERTIFICATEPAIR/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCertPairs");
      this.put("X509StreamParser.CERTIFICATE", "org.bouncycastle.jce.provider.X509CertParser");
      this.put("X509StreamParser.ATTRIBUTECERTIFICATE", "org.bouncycastle.jce.provider.X509AttrCertParser");
      this.put("X509StreamParser.CRL", "org.bouncycastle.jce.provider.X509CRLParser");
      this.put("X509StreamParser.CERTIFICATEPAIR", "org.bouncycastle.jce.provider.X509CertPairParser");
      this.put("Cipher.BROKENPBEWITHMD5ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
      this.put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
      this.put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
      if (revChkClass != null) {
         this.put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
         this.put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
         this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
         this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
         this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
         this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
      } else {
         this.put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
         this.put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
         this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
         this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
         this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
         this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
      }

      this.put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
      this.put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
      this.put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
      this.put("Alg.Alias.CertStore.X509LDAP", "LDAP");
   }

   @Override
   public final Service getService(final String var1, final String var2) {
      String var3 = Strings.toUpperCase(var2);
      final String var4 = var1 + "." + var3;
      Service var5 = this.serviceMap.get(var4);
      if (var5 == null) {
         synchronized (this) {
            if (!this.serviceMap.containsKey(var4)) {
               var5 = AccessController.doPrivileged(new PrivilegedAction<Service>() {
                  public Service run() {
                     Service var1x = BouncyCastleProvider.super.getService(var1, var2);
                     if (var1x != null && var1x.getClassName() != null) {
                        BouncyCastleProvider.this.serviceMap.put(var4, var1x);
                        BouncyCastleProvider.super.remove(var1x.getType() + "." + var1x.getAlgorithm());
                        BouncyCastleProvider.super.putService(var1x);
                        return var1x;
                     } else {
                        return null;
                     }
                  }
               });
            } else {
               var5 = this.serviceMap.get(var4);
            }
         }
      }

      return var5;
   }

   private void loadAlgorithms(String var1, String[] var2) {
      for (int var3 = 0; var3 != var2.length; var3++) {
         this.loadServiceClass(var1, var2[var3]);
      }
   }

   private void loadAlgorithms(String var1, CryptoServiceProperties[] var2) {
      for (int var3 = 0; var3 != var2.length; var3++) {
         CryptoServiceProperties var4 = var2[var3];

         try {
            CryptoServicesRegistrar.checkConstraints(var4);
            this.loadServiceClass(var1, var4.getServiceName());
         } catch (CryptoServiceConstraintsException var6) {
            if (LOG.isLoggable(Level.FINE)) {
               LOG.fine("service for " + var4.getServiceName() + " ignored due to constraints");
            }
         }
      }
   }

   private void loadServiceClass(String var1, String var2) {
      Class var3 = ClassUtil.loadClass(BouncyCastleProvider.class, var1 + var2 + "$Mappings");
      if (var3 != null) {
         try {
            ((AlgorithmProvider)var3.newInstance()).configure(this);
         } catch (Exception var5) {
            throw new InternalError("cannot create instance of " + var1 + var2 + "$Mappings : " + var5);
         }
      }
   }

   private void loadPQCKeys() {
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(new ASN1ObjectIdentifier("1.3.9999.6.4.10"), new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f, new SPHINCSPlusKeyFactorySpi());
      this.addKeyInfoConverter(PQCObjectIdentifiers.sphincs256, new Sphincs256KeyFactorySpi());
      this.addKeyInfoConverter(PQCObjectIdentifiers.newHope, new NHKeyFactorySpi());
      this.addKeyInfoConverter(PQCObjectIdentifiers.xmss, new XMSSKeyFactorySpi());
      this.addKeyInfoConverter(IsaraObjectIdentifiers.id_alg_xmss, new XMSSKeyFactorySpi());
      this.addKeyInfoConverter(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyFactorySpi());
      this.addKeyInfoConverter(IsaraObjectIdentifiers.id_alg_xmssmt, new XMSSMTKeyFactorySpi());
      this.addKeyInfoConverter(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new LMSKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.picnic_key, new PicnicKeyFactorySpi());
      this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_512, new MLKEMKeyFactorySpi());
      this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_768, new MLKEMKeyFactorySpi());
      this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_1024, new MLKEMKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.old_falcon_512, new FalconKeyFactorySpi(BCObjectIdentifiers.old_falcon_512));
      this.addKeyInfoConverter(BCObjectIdentifiers.old_falcon_1024, new FalconKeyFactorySpi(BCObjectIdentifiers.old_falcon_1024));
      this.addKeyInfoConverter(BCObjectIdentifiers.falcon_512, new FalconKeyFactorySpi(BCObjectIdentifiers.falcon_512));
      this.addKeyInfoConverter(BCObjectIdentifiers.falcon_1024, new FalconKeyFactorySpi(BCObjectIdentifiers.falcon_1024));
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium2, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium3, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium5, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium2_aes, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium3_aes, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.dilithium5_aes, new DilithiumKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mceliece348864_r3, new CMCEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mceliece460896_r3, new CMCEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mceliece6688128_r3, new CMCEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mceliece6960119_r3, new CMCEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mceliece8192128_r3, new CMCEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.bike128, new BIKEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.bike192, new BIKEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.bike256, new BIKEKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.hqc128, new HQCKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.hqc192, new HQCKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.hqc256, new HQCKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.kyber512_aes, new KyberKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.kyber768_aes, new KyberKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.kyber1024_aes, new KyberKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps2048509, new NTRUKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps2048677, new NTRUKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps4096821, new NTRUKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.ntruhrss701, new NTRUKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mayo1, new MayoKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mayo2, new MayoKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mayo3, new MayoKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.mayo5, new MayoKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_shake_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_esk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_shake_ssk, new SnovaKeyFactorySpi());
      this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_shake_esk, new SnovaKeyFactorySpi());
   }

   @Override
   public void setParameter(String var1, Object var2) {
      synchronized (CONFIGURATION) {
         ((BouncyCastleProviderConfiguration)CONFIGURATION).setParameter(var1, var2);
      }
   }

   @Override
   public boolean hasAlgorithm(String var1, String var2) {
      return this.containsKey(var1 + "." + var2) || this.containsKey("Alg.Alias." + var1 + "." + var2);
   }

   @Override
   public void addAlgorithm(String var1, String var2) {
      if (this.containsKey(var1)) {
         throw new IllegalStateException("duplicate provider key (" + var1 + ") found");
      } else {
         this.put(var1, var2);
      }
   }

   @Override
   public void addAlgorithm(String var1, String var2, Map<String, String> var3) {
      this.addAlgorithm(var1, var2);
      this.addAttributes(var1, var3);
   }

   @Override
   public void addAlgorithm(String var1, ASN1ObjectIdentifier var2, String var3) {
      this.addAlgorithm(var1 + "." + var2, var3);
      this.addAlgorithm(var1 + ".OID." + var2, var3);
   }

   @Override
   public void addAlgorithm(String var1, ASN1ObjectIdentifier var2, String var3, Map<String, String> var4) {
      this.addAlgorithm(var1, var2, var3);
      this.addAttributes(var1 + "." + var2, var4);
      this.addAttributes(var1 + ".OID." + var2, var4);
   }

   @Override
   public void addKeyInfoConverter(ASN1ObjectIdentifier var1, AsymmetricKeyInfoConverter var2) {
      synchronized (keyInfoConverters) {
         keyInfoConverters.put(var1, var2);
      }
   }

   @Override
   public AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier var1) {
      return (AsymmetricKeyInfoConverter)keyInfoConverters.get(var1);
   }

   @Override
   public void addAttributes(String var1, Map<String, String> var2) {
      this.put(var1 + " ImplementedIn", "Software");

      for (String var4 : var2.keySet()) {
         String var5 = var1 + " " + var4;
         if (this.containsKey(var5)) {
            throw new IllegalStateException("duplicate provider attribute key (" + var5 + ") found");
         }

         this.put(var5, var2.get(var4));
      }
   }

   private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier var0) {
      synchronized (keyInfoConverters) {
         return (AsymmetricKeyInfoConverter)keyInfoConverters.get(var0);
      }
   }

   public static PublicKey getPublicKey(SubjectPublicKeyInfo var0) throws IOException {
      if (var0.getAlgorithm().getAlgorithm().on(BCObjectIdentifiers.picnic_key)) {
         return new PicnicKeyFactorySpi().generatePublic(var0);
      } else {
         AsymmetricKeyInfoConverter var1 = getAsymmetricKeyInfoConverter(var0.getAlgorithm().getAlgorithm());
         return var1 == null ? null : var1.generatePublic(var0);
      }
   }

   public static PrivateKey getPrivateKey(PrivateKeyInfo var0) throws IOException {
      AsymmetricKeyInfoConverter var1 = getAsymmetricKeyInfoConverter(var0.getPrivateKeyAlgorithm().getAlgorithm());
      return var1 == null ? null : var1.generatePrivate(var0);
   }

   private static CryptoServiceProperties service(String var0, int var1) {
      return new BouncyCastleProvider.JcaCryptoService(var0, var1);
   }

   private static class JcaCryptoService implements CryptoServiceProperties {
      private final String name;
      private final int bitsOfSecurity;

      JcaCryptoService(String var1, int var2) {
         this.name = var1;
         this.bitsOfSecurity = var2;
      }

      @Override
      public int bitsOfSecurity() {
         return this.bitsOfSecurity;
      }

      @Override
      public String getServiceName() {
         return this.name;
      }

      @Override
      public CryptoServicePurpose getPurpose() {
         return CryptoServicePurpose.ANY;
      }

      @Override
      public Object getParams() {
         return null;
      }
   }
}
