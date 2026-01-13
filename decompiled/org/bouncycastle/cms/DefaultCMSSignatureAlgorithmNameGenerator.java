package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public class DefaultCMSSignatureAlgorithmNameGenerator implements CMSSignatureAlgorithmNameGenerator {
   private final Map encryptionAlgs = new HashMap();
   private final Map digestAlgs = new HashMap();
   private final Map simpleAlgs = new HashMap();

   private void addEntries(ASN1ObjectIdentifier var1, String var2, String var3) {
      this.addDigestAlg(var1, var2);
      this.addEncryptionAlg(var1, var3);
   }

   private void addSimpleAlg(ASN1ObjectIdentifier var1, String var2) {
      if (this.simpleAlgs.containsKey(var1)) {
         throw new IllegalStateException("object identifier already present in addSimpleAlg");
      } else {
         this.simpleAlgs.put(var1, var2);
      }
   }

   private void addDigestAlg(ASN1ObjectIdentifier var1, String var2) {
      if (this.digestAlgs.containsKey(var1)) {
         throw new IllegalStateException("object identifier already present in addDigestAlg");
      } else {
         this.digestAlgs.put(var1, var2);
      }
   }

   private void addEncryptionAlg(ASN1ObjectIdentifier var1, String var2) {
      if (this.encryptionAlgs.containsKey(var1)) {
         throw new IllegalStateException("object identifier already present in addEncryptionAlg");
      } else {
         this.encryptionAlgs.put(var1, var2);
      }
   }

   public DefaultCMSSignatureAlgorithmNameGenerator() {
      this.addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
      this.addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
      this.addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
      this.addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
      this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_224, "SHA3-224", "DSA");
      this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_256, "SHA3-256", "DSA");
      this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_384, "SHA3-384", "DSA");
      this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_512, "SHA3-512", "DSA");
      this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, "SHA3-224", "ECDSA");
      this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, "SHA3-256", "ECDSA");
      this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, "SHA3-384", "ECDSA");
      this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, "SHA3-512", "ECDSA");
      this.addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
      this.addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
      this.addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
      this.addEntries(OIWObjectIdentifiers.md5WithRSA, "MD5", "RSA");
      this.addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", "RSA");
      this.addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", "RSA");
      this.addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
      this.addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha512_224WithRSAEncryption, "SHA512(224)", "RSA");
      this.addEntries(PKCSObjectIdentifiers.sha512_256WithRSAEncryption, "SHA512(256)", "RSA");
      this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, "SHA3-224", "RSA");
      this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, "SHA3-256", "RSA");
      this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, "SHA3-384", "RSA");
      this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, "SHA3-512", "RSA");
      this.addEntries(X509ObjectIdentifiers.id_rsassa_pss_shake128, "SHAKE128", "RSAPSS");
      this.addEntries(X509ObjectIdentifiers.id_rsassa_pss_shake256, "SHAKE256", "RSAPSS");
      this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128", "RSA");
      this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160", "RSA");
      this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256", "RSA");
      this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
      this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
      this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
      this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
      this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
      this.addEntries(X509ObjectIdentifiers.id_ecdsa_with_shake128, "SHAKE128", "ECDSA");
      this.addEntries(X509ObjectIdentifiers.id_ecdsa_with_shake256, "SHAKE256", "ECDSA");
      this.addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
      this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
      this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
      this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
      this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
      this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
      this.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", "RSA");
      this.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
      this.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
      this.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_224, "SHA3-224", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_256, "SHA3-256", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_384, "SHA3-384", "PLAIN-ECDSA");
      this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_512, "SHA3-512", "PLAIN-ECDSA");
      this.addEntries(GMObjectIdentifiers.sm2sign_with_sha256, "SHA256", "SM2");
      this.addEntries(GMObjectIdentifiers.sm2sign_with_sm3, "SM3", "SM2");
      this.addEntries(BCObjectIdentifiers.sphincs256_with_SHA512, "SHA512", "SPHINCS256");
      this.addEntries(BCObjectIdentifiers.sphincs256_with_SHA3_512, "SHA3-512", "SPHINCS256");
      this.addEntries(BCObjectIdentifiers.picnic_with_shake256, "SHAKE256", "Picnic");
      this.addEntries(BCObjectIdentifiers.picnic_with_sha512, "SHA512", "Picnic");
      this.addEntries(BCObjectIdentifiers.picnic_with_sha3_512, "SHA3-512", "Picnic");
      this.addEncryptionAlg(X9ObjectIdentifiers.id_dsa, "DSA");
      this.addEncryptionAlg(PKCSObjectIdentifiers.rsaEncryption, "RSA");
      this.addEncryptionAlg(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
      this.addEncryptionAlg(X509ObjectIdentifiers.id_ea_rsa, "RSA");
      this.addEncryptionAlg(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
      this.addEncryptionAlg(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
      this.addEncryptionAlg(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
      this.addEncryptionAlg(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
      this.addEncryptionAlg(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
      this.addEncryptionAlg(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
      this.addEncryptionAlg(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
      this.addEncryptionAlg(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
      this.addEncryptionAlg(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
      this.addEncryptionAlg(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
      this.addEncryptionAlg(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
      this.addEncryptionAlg(X9ObjectIdentifiers.id_ecPublicKey, "ECDSA");
      this.addDigestAlg(PKCSObjectIdentifiers.md2, "MD2");
      this.addDigestAlg(PKCSObjectIdentifiers.md4, "MD4");
      this.addDigestAlg(PKCSObjectIdentifiers.md5, "MD5");
      this.addDigestAlg(OIWObjectIdentifiers.idSHA1, "SHA1");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha224, "SHA224");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha256, "SHA256");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha384, "SHA384");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha512, "SHA512");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha512_224, "SHA512(224)");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha512_256, "SHA512(256)");
      this.addDigestAlg(NISTObjectIdentifiers.id_shake128, "SHAKE128");
      this.addDigestAlg(NISTObjectIdentifiers.id_shake256, "SHAKE256");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha3_224, "SHA3-224");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha3_384, "SHA3-384");
      this.addDigestAlg(NISTObjectIdentifiers.id_sha3_512, "SHA3-512");
      this.addDigestAlg(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
      this.addDigestAlg(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
      this.addDigestAlg(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
      this.addDigestAlg(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
      this.addDigestAlg(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.2.1"), "GOST3411");
      this.addDigestAlg(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
      this.addDigestAlg(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
      this.addDigestAlg(GMObjectIdentifiers.sm3, "SM3");
      this.addSimpleAlg(EdECObjectIdentifiers.id_Ed25519, "Ed25519");
      this.addSimpleAlg(EdECObjectIdentifiers.id_Ed448, "Ed448");
      this.addSimpleAlg(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
      this.addSimpleAlg(MiscObjectIdentifiers.id_alg_composite, "COMPOSITE");
      this.addSimpleAlg(BCObjectIdentifiers.falcon_512, "Falcon-512");
      this.addSimpleAlg(BCObjectIdentifiers.falcon_1024, "Falcon-1024");
      this.addSimpleAlg(BCObjectIdentifiers.dilithium2, "Dilithium2");
      this.addSimpleAlg(BCObjectIdentifiers.dilithium3, "Dilithium3");
      this.addSimpleAlg(BCObjectIdentifiers.dilithium5, "Dilithium5");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_128s, "SPHINCS+-SHA2-128s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_128f, "SPHINCS+-SHA2-128f");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_192s, "SPHINCS+-SHA2-192s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_192f, "SPHINCS+-SHA2-192f");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_256s, "SPHINCS+-SHA2-256s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_sha2_256f, "SPHINCS+-SHA2-256f");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_128s, "SPHINCS+-SHAKE-128s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_128f, "SPHINCS+-SHAKE-128f");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_192s, "SPHINCS+-SHAKE-192s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_192f, "SPHINCS+-SHAKE-192f");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_256s, "SPHINCS+-SHAKE-256s");
      this.addSimpleAlg(BCObjectIdentifiers.sphincsPlus_shake_256f, "SPHINCS+-SHAKE-256f");
      this.addSimpleAlg(NISTObjectIdentifiers.id_ml_dsa_44, "ML-DSA-44");
      this.addSimpleAlg(NISTObjectIdentifiers.id_ml_dsa_65, "ML-DSA-65");
      this.addSimpleAlg(NISTObjectIdentifiers.id_ml_dsa_87, "ML-DSA-87");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, "ML-DSA-44-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, "ML-DSA-65-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, "ML-DSA-87-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_128s, "SLH-DSA-SHA2-128S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_128f, "SLH-DSA-SHA2-128F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_192s, "SLH-DSA-SHA2-192S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_192f, "SLH-DSA-SHA2-192F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_256s, "SLH-DSA-SHA2-256S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_sha2_256f, "SLH-DSA-SHA2-256F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_128s, "SLH-DSA-SHAKE-128S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_128f, "SLH-DSA-SHAKE-128F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_192s, "SLH-DSA-SHAKE-192S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_192f, "SLH-DSA-SHAKE-192F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_256s, "SLH-DSA-SHAKE-256S");
      this.addSimpleAlg(NISTObjectIdentifiers.id_slh_dsa_shake_256f, "SLH-DSA-SHAKE-256F");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256, "SLH-DSA-SHA2-128S-WITH-SHA256");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256, "SLH-DSA-SHA2-128F-WITH-SHA256");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512, "SLH-DSA-SHA2-192S-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512, "SLH-DSA-SHA2-192F-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512, "SLH-DSA-SHA2-256S-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512, "SLH-DSA-SHA2-256F-WITH-SHA512");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128, "SLH-DSA-SHAKE-128S-WITH-SHAKE128");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128, "SLH-DSA-SHAKE-128F-WITH-SHAKE128");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256, "SLH-DSA-SHAKE-192S-WITH-SHAKE256");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256, "SLH-DSA-SHAKE-192F-WITH-SHAKE256");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256, "SLH-DSA-SHAKE-256S-WITH-SHAKE256");
      this.addSimpleAlg(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256, "SLH-DSA-SHAKE-256F-WITH-SHAKE256");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, "MLDSA44-RSA2048-PSS-SHA256");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, "MLDSA44-RSA2048-PKCS15-SHA256");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, "MLDSA44-Ed25519-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, "MLDSA44-ECDSA-P256-SHA256");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, "MLDSA65-RSA3072-PSS-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, "MLDSA65-RSA3072-PKCS15-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, "MLDSA65-RSA4096-PSS-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, "MLDSA65-RSA4096-PKCS15-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, "MLDSA65-ECDSA-P256-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, "MLDSA65-ECDSA-P384-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, "MLDSA65-ECDSA-brainpoolP256r1-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, "MLDSA65-Ed25519-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, "MLDSA87-ECDSA-P384-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, "MLDSA87-ECDSA-brainpoolP384r1-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, "MLDSA87-Ed448-SHAKE256");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, "MLDSA87-RSA3072-PSS-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, "MLDSA87-RSA4096-PSS-SHA512");
      this.addSimpleAlg(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, "MLDSA87-ECDSA-P521-SHA512");
      this.addSimpleAlg(BCObjectIdentifiers.picnic_signature, "Picnic");
   }

   private String getDigestAlgName(ASN1ObjectIdentifier var1) {
      String var2 = (String)this.digestAlgs.get(var1);
      return var2 != null ? var2 : var1.getId();
   }

   private String getEncryptionAlgName(ASN1ObjectIdentifier var1) {
      String var2 = (String)this.encryptionAlgs.get(var1);
      return var2 != null ? var2 : var1.getId();
   }

   protected void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.encryptionAlgs.put(var1, var2);
   }

   protected void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.digestAlgs.put(var1, var2);
   }

   @Override
   public String getSignatureName(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      ASN1ObjectIdentifier var3 = var2.getAlgorithm();
      String var4 = (String)this.simpleAlgs.get(var3);
      if (var4 != null) {
         return var4;
      } else if (var3.on(BCObjectIdentifiers.sphincsPlus)) {
         return "SPHINCSPlus";
      } else {
         String var5 = this.getDigestAlgName(var3);
         return !var5.equals(var3.getId())
            ? var5 + "with" + this.getEncryptionAlgName(var3)
            : this.getDigestAlgName(var1.getAlgorithm()) + "with" + this.getEncryptionAlgName(var3);
      }
   }
}
