package org.bouncycastle.operator.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

class OperatorHelper {
   private static final Map oids = new HashMap();
   private static final Map asymmetricWrapperAlgNames = new HashMap();
   private static final Map symmetricWrapperAlgNames = new HashMap();
   private static final Map symmetricKeyAlgNames = new HashMap();
   private static final Map symmetricWrapperKeySizes = new HashMap();
   private static final Map oaepParamsMap = new HashMap();
   private static DefaultSignatureNameFinder sigFinder = new DefaultSignatureNameFinder();
   private JcaJceHelper helper;

   OperatorHelper(JcaJceHelper var1) {
      this.helper = var1;
   }

   String getWrappingAlgorithmName(ASN1ObjectIdentifier var1) {
      return (String)symmetricWrapperAlgNames.get(var1);
   }

   int getKeySizeInBits(ASN1ObjectIdentifier var1) {
      return (Integer)symmetricWrapperKeySizes.get(var1);
   }

   KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier var1) throws CMSException {
      try {
         Object var2 = null;
         if (var2 != null) {
            try {
               return this.helper.createKeyPairGenerator((String)var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyPairGenerator(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CMSException("cannot create key agreement: " + var5.getMessage(), var5);
      }
   }

   Cipher createCipher(ASN1ObjectIdentifier var1) throws OperatorCreationException {
      try {
         return this.helper.createCipher(var1.getId());
      } catch (GeneralSecurityException var3) {
         throw new OperatorCreationException("cannot create cipher: " + var3.getMessage(), var3);
      }
   }

   KeyAgreement createKeyAgreement(ASN1ObjectIdentifier var1) throws OperatorCreationException {
      try {
         Object var2 = null;
         if (var2 != null) {
            try {
               return this.helper.createKeyAgreement((String)var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyAgreement(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new OperatorCreationException("cannot create key agreement: " + var5.getMessage(), var5);
      }
   }

   Cipher createAsymmetricWrapper(AlgorithmIdentifier var1, Map var2) throws OperatorCreationException {
      if (var1 == null) {
         throw new NullPointerException("'algorithmID' cannot be null");
      } else {
         ASN1ObjectIdentifier var3 = var1.getAlgorithm();

         try {
            String var4 = null;
            if (var2 != null && !var2.isEmpty()) {
               var4 = (String)var2.get(var3);
            }

            if (var4 == null) {
               var4 = (String)asymmetricWrapperAlgNames.get(var3);
            }

            if (var4 != null) {
               if (var4.indexOf("OAEPPadding") > 0) {
                  try {
                     RSAESOAEPparams var5 = RSAESOAEPparams.getInstance(var1.getParameters());
                     if (var5 != null) {
                        ASN1ObjectIdentifier var6 = var5.getHashAlgorithm().getAlgorithm();
                        OperatorHelper.OAEPParamsValue var7 = (OperatorHelper.OAEPParamsValue)oaepParamsMap.get(var6);
                        if (var7 != null && var7.matches(var5.withDefaultPSource())) {
                           var4 = var7.getCipherName();
                        }
                     }
                  } catch (Exception var8) {
                  }
               }

               try {
                  return this.helper.createCipher(var4);
               } catch (NoSuchAlgorithmException var11) {
                  if (var4.equals("RSA/ECB/PKCS1Padding")) {
                     try {
                        return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                     } catch (NoSuchAlgorithmException var10) {
                     }
                  } else if (var4.indexOf("ECB/OAEPWith") > 0) {
                     int var13 = var4.indexOf("ECB");

                     try {
                        return this.helper.createCipher(var4.substring(0, var13) + "NONE" + var4.substring(var13 + 3));
                     } catch (NoSuchAlgorithmException var9) {
                     }
                  }
               }
            }

            return this.helper.createCipher(var3.getId());
         } catch (GeneralSecurityException var12) {
            throw new OperatorCreationException("cannot create cipher: " + var12.getMessage(), var12);
         }
      }
   }

   Cipher createSymmetricWrapper(ASN1ObjectIdentifier var1) throws OperatorCreationException {
      try {
         String var2 = (String)symmetricWrapperAlgNames.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createCipher(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createCipher(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new OperatorCreationException("cannot create cipher: " + var5.getMessage(), var5);
      }
   }

   AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier var1) throws OperatorCreationException {
      AlgorithmParameters var2 = null;
      if (var1.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
         return null;
      } else {
         if (var1.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSAES_OAEP)) {
            try {
               var2 = this.helper.createAlgorithmParameters("OAEP");
            } catch (NoSuchAlgorithmException var7) {
            } catch (NoSuchProviderException var8) {
               throw new OperatorCreationException("cannot create algorithm parameters: " + var8.getMessage(), var8);
            }
         }

         if (var2 == null) {
            try {
               var2 = this.helper.createAlgorithmParameters(var1.getAlgorithm().getId());
            } catch (NoSuchAlgorithmException var5) {
               return null;
            } catch (NoSuchProviderException var6) {
               throw new OperatorCreationException("cannot create algorithm parameters: " + var6.getMessage(), var6);
            }
         }

         try {
            var2.init(var1.getParameters().toASN1Primitive().getEncoded());
            return var2;
         } catch (IOException var4) {
            throw new OperatorCreationException("cannot initialise algorithm parameters: " + var4.getMessage(), var4);
         }
      }
   }

   MessageDigest createDigest(AlgorithmIdentifier var1) throws GeneralSecurityException {
      MessageDigest var2;
      try {
         if (var1.getAlgorithm().equals(NISTObjectIdentifiers.id_shake256_len)) {
            var2 = this.helper.createMessageDigest("SHAKE256-" + ASN1Integer.getInstance(var1.getParameters()).getValue());
         } else if (var1.getAlgorithm().equals(NISTObjectIdentifiers.id_shake128_len)) {
            var2 = this.helper.createMessageDigest("SHAKE128-" + ASN1Integer.getInstance(var1.getParameters()).getValue());
         } else {
            var2 = this.helper.createMessageDigest(MessageDigestUtils.getDigestName(var1.getAlgorithm()));
         }
      } catch (NoSuchAlgorithmException var5) {
         if (oids.get(var1.getAlgorithm()) == null) {
            throw var5;
         }

         String var4 = (String)oids.get(var1.getAlgorithm());
         var2 = this.helper.createMessageDigest(var4);
      }

      return var2;
   }

   Signature createSignature(AlgorithmIdentifier var1) throws GeneralSecurityException {
      String var2 = getSignatureName(var1);

      Signature var3;
      try {
         var3 = this.helper.createSignature(var2);
      } catch (NoSuchAlgorithmException var7) {
         if (!var2.endsWith("WITHRSAANDMGF1")) {
            throw var7;
         }

         String var5 = var2.substring(0, var2.indexOf(87)) + "WITHRSASSA-PSS";
         var3 = this.helper.createSignature(var5);
      }

      if (var1.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
         ASN1Sequence var4 = ASN1Sequence.getInstance(var1.getParameters());
         if (this.notDefaultPSSParams(var4)) {
            try {
               AlgorithmParameters var8 = this.helper.createAlgorithmParameters("PSS");
               var8.init(var4.getEncoded());
               var3.setParameter(var8.getParameterSpec(PSSParameterSpec.class));
            } catch (IOException var6) {
               throw new GeneralSecurityException("unable to process PSS parameters: " + var6.getMessage());
            }
         }
      }

      return var3;
   }

   Signature createRawSignature(AlgorithmIdentifier var1) {
      try {
         String var3 = getSignatureName(var1);
         var3 = "NONE" + var3.substring(var3.indexOf("WITH"));
         Signature var2 = this.helper.createSignature(var3);
         if (var1.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            AlgorithmParameters var4 = this.helper.createAlgorithmParameters(var3);
            AlgorithmParametersUtils.loadParameters(var4, var1.getParameters());
            PSSParameterSpec var5 = var4.getParameterSpec(PSSParameterSpec.class);
            var2.setParameter(var5);
         }

         return var2;
      } catch (Exception var6) {
         return null;
      }
   }

   private static String getSignatureName(AlgorithmIdentifier var0) {
      return sigFinder.getAlgorithmName(var0);
   }

   static String getDigestName(ASN1ObjectIdentifier var0) {
      String var1 = MessageDigestUtils.getDigestName(var0);
      int var2 = var1.indexOf(45);
      return var2 > 0 && !var1.startsWith("SHA3") ? var1.substring(0, var2) + var1.substring(var2 + 1) : var1;
   }

   public X509Certificate convertCertificate(X509CertificateHolder var1) throws CertificateException {
      try {
         CertificateFactory var2 = this.helper.createCertificateFactory("X.509");
         return (X509Certificate)var2.generateCertificate(new ByteArrayInputStream(var1.getEncoded()));
      } catch (IOException var3) {
         throw new OperatorHelper.OpCertificateException("cannot get encoded form of certificate: " + var3.getMessage(), var3);
      } catch (NoSuchProviderException var4) {
         throw new OperatorHelper.OpCertificateException("cannot find factory provider: " + var4.getMessage(), var4);
      }
   }

   public PublicKey convertPublicKey(SubjectPublicKeyInfo var1) throws OperatorCreationException {
      try {
         KeyFactory var2 = this.helper.createKeyFactory(var1.getAlgorithm().getAlgorithm().getId());
         return var2.generatePublic(new X509EncodedKeySpec(var1.getEncoded()));
      } catch (IOException var3) {
         throw new OperatorCreationException("cannot get encoded form of key: " + var3.getMessage(), var3);
      } catch (NoSuchAlgorithmException var4) {
         throw new OperatorCreationException("cannot create key factory: " + var4.getMessage(), var4);
      } catch (NoSuchProviderException var5) {
         throw new OperatorCreationException("cannot find factory provider: " + var5.getMessage(), var5);
      } catch (InvalidKeySpecException var6) {
         throw new OperatorCreationException("cannot create key factory: " + var6.getMessage(), var6);
      }
   }

   String getKeyAlgorithmName(ASN1ObjectIdentifier var1) {
      String var2 = (String)symmetricKeyAlgNames.get(var1);
      return var2 != null ? var2 : var1.getId();
   }

   private boolean notDefaultPSSParams(ASN1Sequence var1) throws GeneralSecurityException {
      if (var1 != null && var1.size() != 0) {
         RSASSAPSSparams var2 = RSASSAPSSparams.getInstance(var1);
         if (!var2.getMaskGenAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1)) {
            return true;
         } else if (!var2.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(var2.getMaskGenAlgorithm().getParameters()))) {
            return true;
         } else {
            MessageDigest var3 = this.createDigest(var2.getHashAlgorithm());
            return var2.getSaltLength().intValue() != var3.getDigestLength();
         }
      } else {
         return false;
      }
   }

   static {
      oids.put(OIWObjectIdentifiers.idSHA1, "SHA1");
      oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
      oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
      oids.put(NISTObjectIdentifiers.id_sha384, "SHA384");
      oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
      oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
      oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
      oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
      asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
      asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
      asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
      asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
      symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
      symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
      symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
      symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
      symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
      symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
      symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
      symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
      symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
      symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
      symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
      symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
      symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
      symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
      symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
      symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
      symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
      symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
      symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
      symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
      symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
      symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
      symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
      symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
      symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
      OperatorHelper.OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding", OIWObjectIdentifiers.idSHA1);
      OperatorHelper.OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-224AndMGF1Padding", NISTObjectIdentifiers.id_sha224);
      OperatorHelper.OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", NISTObjectIdentifiers.id_sha256);
      OperatorHelper.OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-384AndMGF1Padding", NISTObjectIdentifiers.id_sha384);
      OperatorHelper.OAEPParamsValue.add(oaepParamsMap, "RSA/ECB/OAEPWithSHA-512AndMGF1Padding", NISTObjectIdentifiers.id_sha512);
   }

   private static class OAEPParamsValue {
      private String cipherName;
      private byte[] derEncoding;

      static void add(Map var0, String var1, ASN1ObjectIdentifier var2) {
         try {
            RSAESOAEPparams var3 = createOAEPParams(var2);
            byte[] var4 = getDEREncoding(var3);
            var0.put(var2, new OperatorHelper.OAEPParamsValue(var1, var4));
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      }

      private OAEPParamsValue(String var1, byte[] var2) {
         this.cipherName = var1;
         this.derEncoding = var2;
      }

      String getCipherName() {
         return this.cipherName;
      }

      boolean matches(RSAESOAEPparams var1) throws IOException {
         return Arrays.areEqual(this.derEncoding, getDEREncoding(var1));
      }

      private static RSAESOAEPparams createOAEPParams(ASN1ObjectIdentifier var0) {
         AlgorithmIdentifier var1 = new AlgorithmIdentifier(var0, DERNull.INSTANCE);
         AlgorithmIdentifier var2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, var1);
         return new RSAESOAEPparams(var1, var2, RSAESOAEPparams.DEFAULT_P_SOURCE_ALGORITHM);
      }

      private static byte[] getDEREncoding(RSAESOAEPparams var0) throws IOException {
         return var0.getEncoded("DER");
      }
   }

   private static class OpCertificateException extends CertificateException {
      private Throwable cause;

      public OpCertificateException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
