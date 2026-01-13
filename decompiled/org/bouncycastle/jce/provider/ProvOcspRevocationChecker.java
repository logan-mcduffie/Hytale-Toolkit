package org.bouncycastle.jce.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.internal.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

class ProvOcspRevocationChecker implements PKIXCertRevocationChecker {
   private static final int DEFAULT_OCSP_TIMEOUT = 15000;
   private static final int DEFAULT_OCSP_MAX_RESPONSE_SIZE = 32768;
   private static final Map oids = new HashMap();
   private final ProvRevocationChecker parent;
   private final JcaJceHelper helper;
   private PKIXCertRevocationCheckerParameters parameters;
   private boolean isEnabledOCSP;
   private String ocspURL;

   public ProvOcspRevocationChecker(ProvRevocationChecker var1, JcaJceHelper var2) {
      this.parent = var1;
      this.helper = var2;
   }

   @Override
   public void setParameter(String var1, Object var2) {
   }

   @Override
   public void initialize(PKIXCertRevocationCheckerParameters var1) {
      this.parameters = var1;
      this.isEnabledOCSP = Properties.isOverrideSet("ocsp.enable");
      this.ocspURL = Properties.getPropertyValue("ocsp.responderURL");
   }

   public List<CertPathValidatorException> getSoftFailExceptions() {
      return null;
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (var1) {
         throw new CertPathValidatorException("forward checking not supported");
      } else {
         this.parameters = null;
         this.isEnabledOCSP = Properties.isOverrideSet("ocsp.enable");
         this.ocspURL = Properties.getPropertyValue("ocsp.responderURL");
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      return null;
   }

   @Override
   public void check(Certificate var1) throws CertPathValidatorException {
      X509Certificate var2 = (X509Certificate)var1;
      Map var3 = this.parent.getOcspResponses();
      URI var4 = this.parent.getOcspResponder();
      label83:
      if (var4 != null) {
         byte[] var5 = null;
         boolean var6 = false;
         if (var3.get(var2) == null && var4 != null) {
            if (this.ocspURL == null && this.parent.getOcspResponder() == null && !this.isEnabledOCSP) {
               throw new RecoverableCertPathValidatorException(
                  "OCSP disabled by \"ocsp.enable\" setting", null, this.parameters.getCertPath(), this.parameters.getIndex()
               );
            }

            org.bouncycastle.asn1.x509.Certificate var23 = this.extractCert();
            CertID var25 = this.createCertID(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), var23, new ASN1Integer(var2.getSerialNumber()));
            OCSPResponse var27 = OcspCache.getOcspResponse(
               var25, this.parameters, var4, this.parent.getOcspResponderCert(), this.parent.getOcspExtensions(), this.helper
            );

            try {
               var3.put(var2, var27.getEncoded());
               var6 = true;
            } catch (IOException var19) {
               throw new CertPathValidatorException("unable to encode OCSP response", var19, this.parameters.getCertPath(), this.parameters.getIndex());
            }
         } else {
            List var7 = this.parent.getOcspExtensions();

            for (int var8 = 0; var8 != var7.size(); var8++) {
               Extension var9 = (Extension)var7.get(var8);
               byte[] var10 = var9.getValue();
               if (OCSPObjectIdentifiers.id_pkix_ocsp_nonce.getId().equals(var9.getId())) {
                  var5 = var10;
               }
            }
         }

         if (var3.isEmpty()) {
            throw new RecoverableCertPathValidatorException(
               "no OCSP response found for any certificate", null, this.parameters.getCertPath(), this.parameters.getIndex()
            );
         } else {
            OCSPResponse var24 = OCSPResponse.getInstance(var3.get(var2));
            ASN1Integer var26 = new ASN1Integer(var2.getSerialNumber());
            if (var24 != null) {
               if (0 != var24.getResponseStatus().getIntValue()) {
                  throw new CertPathValidatorException(
                     "OCSP response failed: " + var24.getResponseStatus().getValue(), null, this.parameters.getCertPath(), this.parameters.getIndex()
                  );
               } else {
                  ResponseBytes var28 = ResponseBytes.getInstance(var24.getResponseBytes());
                  if (var28.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
                     try {
                        BasicOCSPResponse var29 = BasicOCSPResponse.getInstance(var28.getResponse().getOctets());
                        if (var6 || validatedOcspResponse(var29, this.parameters, var5, this.parent.getOcspResponderCert(), this.helper)) {
                           ResponseData var11 = ResponseData.getInstance(var29.getTbsResponseData());
                           ASN1Sequence var12 = var11.getResponses();
                           CertID var13 = null;

                           for (int var14 = 0; var14 != var12.size(); var14++) {
                              SingleResponse var15 = SingleResponse.getInstance(var12.getObjectAt(var14));
                              if (var26.equals(var15.getCertID().getSerialNumber())) {
                                 ASN1GeneralizedTime var16 = var15.getNextUpdate();
                                 if (var16 != null && this.parameters.getValidDate().after(var16.getDate())) {
                                    throw new ExtCertPathValidatorException("OCSP response expired");
                                 }

                                 if (var13 == null || !isEqualAlgId(var13.getHashAlgorithm(), var15.getCertID().getHashAlgorithm())) {
                                    org.bouncycastle.asn1.x509.Certificate var17 = this.extractCert();
                                    var13 = this.createCertID(var15.getCertID(), var17, var26);
                                 }

                                 if (var13.equals(var15.getCertID())) {
                                    if (var15.getCertStatus().getTagNo() == 0) {
                                       return;
                                    }

                                    if (var15.getCertStatus().getTagNo() == 1) {
                                       RevokedInfo var30 = RevokedInfo.getInstance(var15.getCertStatus().getStatus());
                                       CRLReason var18 = var30.getRevocationReason();
                                       throw new CertPathValidatorException(
                                          "certificate revoked, reason=(" + var18 + "), date=" + var30.getRevocationTime().getDate(),
                                          null,
                                          this.parameters.getCertPath(),
                                          this.parameters.getIndex()
                                       );
                                    }

                                    throw new CertPathValidatorException(
                                       "certificate revoked, details unknown", null, this.parameters.getCertPath(), this.parameters.getIndex()
                                    );
                                 }
                              }
                           }
                        }
                     } catch (CertPathValidatorException var21) {
                        throw var21;
                     } catch (Exception var22) {
                        throw new CertPathValidatorException(
                           "unable to process OCSP response", var22, this.parameters.getCertPath(), this.parameters.getIndex()
                        );
                     }
                  }
               }
            } else {
               throw new RecoverableCertPathValidatorException(
                  "no OCSP response found for certificate", null, this.parameters.getCertPath(), this.parameters.getIndex()
               );
            }
         }
      } else if (this.ocspURL != null) {
         try {
            var4 = new URI(this.ocspURL);
            break label83;
         } catch (URISyntaxException var20) {
            throw new CertPathValidatorException("configuration error: " + var20.getMessage(), var20, this.parameters.getCertPath(), this.parameters.getIndex());
         }
      } else {
         var4 = getOcspResponderURI(var2);
         break label83;
      }
   }

   private static boolean isEqualAlgId(AlgorithmIdentifier var0, AlgorithmIdentifier var1) {
      if (var0 != var1 && !var0.equals(var1)) {
         if (var0.getAlgorithm().equals(var1.getAlgorithm())) {
            ASN1Encodable var2 = var0.getParameters();
            ASN1Encodable var3 = var1.getParameters();
            if (var2 == var3) {
               return true;
            } else if (var2 == null) {
               return DERNull.INSTANCE.equals(var3);
            } else {
               return DERNull.INSTANCE.equals(var2) && var3 == null ? true : var2.equals(var3);
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   static URI getOcspResponderURI(X509Certificate var0) {
      byte[] var1 = var0.getExtensionValue(org.bouncycastle.asn1.x509.Extension.authorityInfoAccess.getId());
      if (var1 == null) {
         return null;
      } else {
         AuthorityInformationAccess var2 = AuthorityInformationAccess.getInstance(ASN1OctetString.getInstance(var1).getOctets());
         AccessDescription[] var3 = var2.getAccessDescriptions();

         for (int var4 = 0; var4 != var3.length; var4++) {
            AccessDescription var5 = var3[var4];
            if (AccessDescription.id_ad_ocsp.equals(var5.getAccessMethod())) {
               GeneralName var6 = var5.getAccessLocation();
               if (var6.getTagNo() == 6) {
                  try {
                     return new URI(((ASN1String)var6.getName()).getString());
                  } catch (URISyntaxException var8) {
                  }
               }
            }
         }

         return null;
      }
   }

   static boolean validatedOcspResponse(BasicOCSPResponse var0, PKIXCertRevocationCheckerParameters var1, byte[] var2, X509Certificate var3, JcaJceHelper var4) throws CertPathValidatorException {
      try {
         ASN1Sequence var5 = var0.getCerts();
         Signature var6 = var4.createSignature(getSignatureName(var0.getSignatureAlgorithm()));
         X509Certificate var7 = getSignerCert(var0, var1.getSigningCert(), var3, var4);
         if (var7 == null && var5 == null) {
            throw new CertPathValidatorException("OCSP responder certificate not found");
         } else {
            if (var7 != null) {
               var6.initVerify(var7.getPublicKey());
            } else {
               CertificateFactory var8 = var4.createCertificateFactory("X.509");
               X509Certificate var9 = (X509Certificate)var8.generateCertificate(new ByteArrayInputStream(var5.getObjectAt(0).toASN1Primitive().getEncoded()));
               var9.verify(var1.getSigningCert().getPublicKey());
               var9.checkValidity(var1.getValidDate());
               if (!responderMatches(var0.getTbsResponseData().getResponderID(), var9, var4)) {
                  throw new CertPathValidatorException("responder certificate does not match responderID", null, var1.getCertPath(), var1.getIndex());
               }

               List var10 = var9.getExtendedKeyUsage();
               if (var10 == null || !var10.contains(KeyPurposeId.id_kp_OCSPSigning.getId())) {
                  throw new CertPathValidatorException("responder certificate not valid for signing OCSP responses", null, var1.getCertPath(), var1.getIndex());
               }

               var6.initVerify(var9);
            }

            var6.update(var0.getTbsResponseData().getEncoded("DER"));
            if (var6.verify(var0.getSignature().getOctets())) {
               if (var2 != null) {
                  Extensions var14 = var0.getTbsResponseData().getResponseExtensions();
                  org.bouncycastle.asn1.x509.Extension var15 = var14.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
                  if (!Arrays.areEqual(var2, var15.getExtnValue().getOctets())) {
                     throw new CertPathValidatorException("nonce mismatch in OCSP response", null, var1.getCertPath(), var1.getIndex());
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } catch (CertPathValidatorException var11) {
         throw var11;
      } catch (GeneralSecurityException var12) {
         throw new CertPathValidatorException("OCSP response failure: " + var12.getMessage(), var12, var1.getCertPath(), var1.getIndex());
      } catch (IOException var13) {
         throw new CertPathValidatorException("OCSP response failure: " + var13.getMessage(), var13, var1.getCertPath(), var1.getIndex());
      }
   }

   private static X509Certificate getSignerCert(BasicOCSPResponse var0, X509Certificate var1, X509Certificate var2, JcaJceHelper var3) throws NoSuchProviderException, NoSuchAlgorithmException {
      ResponderID var4 = var0.getTbsResponseData().getResponderID();
      byte[] var5 = var4.getKeyHash();
      if (var5 != null) {
         MessageDigest var6 = var3.createMessageDigest("SHA1");
         if (var2 != null && Arrays.areEqual(var5, calcKeyHash(var6, var2.getPublicKey()))) {
            return var2;
         }

         if (var1 != null && Arrays.areEqual(var5, calcKeyHash(var6, var1.getPublicKey()))) {
            return var1;
         }
      } else {
         X500Name var8 = X500Name.getInstance(BCStrictStyle.INSTANCE, var4.getName());
         if (var2 != null && var8.equals(X500Name.getInstance(BCStrictStyle.INSTANCE, var2.getSubjectX500Principal().getEncoded()))) {
            return var2;
         }

         if (var1 != null && var8.equals(X500Name.getInstance(BCStrictStyle.INSTANCE, var1.getSubjectX500Principal().getEncoded()))) {
            return var1;
         }
      }

      return null;
   }

   private static boolean responderMatches(ResponderID var0, X509Certificate var1, JcaJceHelper var2) throws NoSuchProviderException, NoSuchAlgorithmException {
      byte[] var3 = var0.getKeyHash();
      if (var3 != null) {
         MessageDigest var5 = var2.createMessageDigest("SHA1");
         return Arrays.areEqual(var3, calcKeyHash(var5, var1.getPublicKey()));
      } else {
         X500Name var4 = X500Name.getInstance(BCStrictStyle.INSTANCE, var0.getName());
         return var4.equals(X500Name.getInstance(BCStrictStyle.INSTANCE, var1.getSubjectX500Principal().getEncoded()));
      }
   }

   private static byte[] calcKeyHash(MessageDigest var0, PublicKey var1) {
      SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(var1.getEncoded());
      return var0.digest(var2.getPublicKeyData().getBytes());
   }

   private org.bouncycastle.asn1.x509.Certificate extractCert() throws CertPathValidatorException {
      try {
         return org.bouncycastle.asn1.x509.Certificate.getInstance(this.parameters.getSigningCert().getEncoded());
      } catch (Exception var2) {
         throw new CertPathValidatorException(
            "cannot process signing cert: " + var2.getMessage(), var2, this.parameters.getCertPath(), this.parameters.getIndex()
         );
      }
   }

   private CertID createCertID(CertID var1, org.bouncycastle.asn1.x509.Certificate var2, ASN1Integer var3) throws CertPathValidatorException {
      return this.createCertID(var1.getHashAlgorithm(), var2, var3);
   }

   private CertID createCertID(AlgorithmIdentifier var1, org.bouncycastle.asn1.x509.Certificate var2, ASN1Integer var3) throws CertPathValidatorException {
      try {
         MessageDigest var4 = this.helper.createMessageDigest(MessageDigestUtils.getDigestName(var1.getAlgorithm()));
         DEROctetString var5 = new DEROctetString(var4.digest(var2.getSubject().getEncoded("DER")));
         DEROctetString var6 = new DEROctetString(var4.digest(var2.getSubjectPublicKeyInfo().getPublicKeyData().getBytes()));
         return new CertID(var1, var5, var6, var3);
      } catch (Exception var7) {
         throw new CertPathValidatorException("problem creating ID: " + var7, var7);
      }
   }

   private static String getDigestName(ASN1ObjectIdentifier var0) {
      String var1 = MessageDigestUtils.getDigestName(var0);
      int var2 = var1.indexOf(45);
      return var2 > 0 && !var1.startsWith("SHA3") ? var1.substring(0, var2) + var1.substring(var2 + 1) : var1;
   }

   private static String getSignatureName(AlgorithmIdentifier var0) {
      ASN1Encodable var1 = var0.getParameters();
      if (var1 != null && !DERNull.INSTANCE.equals(var1) && var0.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
         RSASSAPSSparams var2 = RSASSAPSSparams.getInstance(var1);
         return getDigestName(var2.getHashAlgorithm().getAlgorithm()) + "WITHRSAANDMGF1";
      } else {
         return oids.containsKey(var0.getAlgorithm()) ? (String)oids.get(var0.getAlgorithm()) : var0.getAlgorithm().getId();
      }
   }

   static {
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
      oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
      oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
      oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
      oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
      oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
      oids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
      oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
      oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
      oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
      oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
      oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
      oids.put(IsaraObjectIdentifiers.id_alg_xmss, "XMSS");
      oids.put(IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT");
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
      oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
      oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
      oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
      oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
      oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
      oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
      oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
   }
}
