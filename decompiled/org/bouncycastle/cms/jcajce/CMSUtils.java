package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;

class CMSUtils {
   private static final Set mqvAlgs = new HashSet();
   private static final Set ecAlgs = new HashSet();
   private static final Set gostAlgs = new HashSet();
   private static final Map asymmetricWrapperAlgNames = new HashMap();
   private static Map<ASN1ObjectIdentifier, String> wrapAlgNames = new HashMap<>();

   static boolean isMQV(ASN1ObjectIdentifier var0) {
      return mqvAlgs.contains(var0);
   }

   static boolean isEC(ASN1ObjectIdentifier var0) {
      return ecAlgs.contains(var0);
   }

   static boolean isGOST(ASN1ObjectIdentifier var0) {
      return gostAlgs.contains(var0);
   }

   static boolean isRFC2631(ASN1ObjectIdentifier var0) {
      return var0.equals(PKCSObjectIdentifiers.id_alg_ESDH) || var0.equals(PKCSObjectIdentifiers.id_alg_SSDH);
   }

   static String getWrapAlgorithmName(ASN1ObjectIdentifier var0) {
      return wrapAlgNames.get(var0);
   }

   static PrivateKey cleanPrivateKey(PrivateKey var0) {
      return var0 instanceof AnnotatedPrivateKey ? cleanPrivateKey(((AnnotatedPrivateKey)var0).getKey()) : var0;
   }

   static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate var0) throws CertificateEncodingException {
      Certificate var1 = Certificate.getInstance(var0.getEncoded());
      return new IssuerAndSerialNumber(var1.getIssuer(), var0.getSerialNumber());
   }

   static byte[] getSubjectKeyId(X509Certificate var0) {
      byte[] var1 = var0.getExtensionValue(Extension.subjectKeyIdentifier.getId());
      return var1 != null ? ASN1OctetString.getInstance(ASN1OctetString.getInstance(var1).getOctets()).getOctets() : null;
   }

   static EnvelopedDataHelper createContentHelper(Provider var0) {
      return var0 != null ? new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var0)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   }

   static EnvelopedDataHelper createContentHelper(String var0) {
      return var0 != null ? new EnvelopedDataHelper(new NamedJcaJceExtHelper(var0)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   }

   static ASN1Encodable extractParameters(AlgorithmParameters var0) throws CMSException {
      try {
         return AlgorithmParametersUtils.extractParameters(var0);
      } catch (IOException var2) {
         throw new CMSException("cannot extract parameters: " + var2.getMessage(), var2);
      }
   }

   static void loadParameters(AlgorithmParameters var0, ASN1Encodable var1) throws CMSException {
      try {
         AlgorithmParametersUtils.loadParameters(var0, var1);
      } catch (IOException var3) {
         throw new CMSException("error encoding algorithm parameters.", var3);
      }
   }

   static Key getJceKey(GenericKey var0) {
      if (var0.getRepresentation() instanceof Key) {
         return (Key)var0.getRepresentation();
      } else if (var0.getRepresentation() instanceof byte[]) {
         return new SecretKeySpec((byte[])var0.getRepresentation(), "ENC");
      } else {
         throw new IllegalArgumentException("unknown generic key type");
      }
   }

   static Cipher createAsymmetricWrapper(JcaJceHelper var0, ASN1ObjectIdentifier var1, Map var2) throws OperatorCreationException {
      try {
         String var3 = null;
         if (!var2.isEmpty()) {
            var3 = (String)var2.get(var1);
         }

         if (var3 == null) {
            var3 = (String)asymmetricWrapperAlgNames.get(var1);
         }

         if (var3 != null) {
            try {
               return var0.createCipher(var3);
            } catch (NoSuchAlgorithmException var7) {
               if (var3.equals("RSA/ECB/PKCS1Padding")) {
                  try {
                     return var0.createCipher("RSA/NONE/PKCS1Padding");
                  } catch (NoSuchAlgorithmException var6) {
                  }
               }
            }
         }

         return var0.createCipher(var1.getId());
      } catch (GeneralSecurityException var8) {
         throw new OperatorCreationException("cannot create cipher: " + var8.getMessage(), var8);
      }
   }

   public static int getKekSize(ASN1ObjectIdentifier var0) {
      if (var0.equals(CMSAlgorithm.AES256_WRAP) || var0.equals(CMSAlgorithm.AES256_WRAP_PAD)) {
         return 32;
      } else if (var0.equals(CMSAlgorithm.AES128_WRAP) || var0.equals(CMSAlgorithm.AES128_WRAP_PAD)) {
         return 16;
      } else if (!var0.equals(CMSAlgorithm.AES192_WRAP) && !var0.equals(CMSAlgorithm.AES192_WRAP_PAD)) {
         throw new IllegalArgumentException("unknown wrap algorithm");
      } else {
         return 24;
      }
   }

   static {
      wrapAlgNames.put(CMSAlgorithm.AES128_WRAP, "AESWRAP");
      wrapAlgNames.put(CMSAlgorithm.AES192_WRAP, "AESWRAP");
      wrapAlgNames.put(CMSAlgorithm.AES256_WRAP, "AESWRAP");
      wrapAlgNames.put(CMSAlgorithm.AES128_WRAP_PAD, "AES-KWP");
      wrapAlgNames.put(CMSAlgorithm.AES192_WRAP_PAD, "AES-KWP");
      wrapAlgNames.put(CMSAlgorithm.AES256_WRAP_PAD, "AES-KWP");
      mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
      ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
      ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
      gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
      gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
      asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
      asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
      asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
      asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
      asymmetricWrapperAlgNames.put(ISOIECObjectIdentifiers.id_kem_rsa, "RSA-KTS-KEM-KWS");
   }
}
