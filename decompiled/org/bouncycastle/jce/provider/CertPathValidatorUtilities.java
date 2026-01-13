package org.bouncycastle.jce.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.X509AttributeCertificate;

class CertPathValidatorUtilities {
   protected static final String CERTIFICATE_POLICIES = Extension.certificatePolicies.getId();
   protected static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
   protected static final String POLICY_MAPPINGS = Extension.policyMappings.getId();
   protected static final String SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
   protected static final String NAME_CONSTRAINTS = Extension.nameConstraints.getId();
   protected static final String KEY_USAGE = Extension.keyUsage.getId();
   protected static final String INHIBIT_ANY_POLICY = Extension.inhibitAnyPolicy.getId();
   protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
   protected static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
   protected static final String POLICY_CONSTRAINTS = Extension.policyConstraints.getId();
   protected static final String FRESHEST_CRL = Extension.freshestCRL.getId();
   protected static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
   protected static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
   protected static final String ANY_POLICY = "2.5.29.32.0";
   protected static final String CRL_NUMBER = Extension.cRLNumber.getId();
   protected static final int KEY_CERT_SIGN = 5;
   protected static final int CRL_SIGN = 6;
   protected static final String[] crlReasons = new String[]{
      "unspecified",
      "keyCompromise",
      "cACompromise",
      "affiliationChanged",
      "superseded",
      "cessationOfOperation",
      "certificateHold",
      "unknown",
      "removeFromCRL",
      "privilegeWithdrawn",
      "aACompromise"
   };

   static Collection findTargets(PKIXExtendedBuilderParameters var0) throws CertPathBuilderException {
      PKIXExtendedParameters var1 = var0.getBaseParameters();
      PKIXCertStoreSelector var2 = var1.getTargetConstraints();
      LinkedHashSet var3 = new LinkedHashSet();

      try {
         findCertificates(var3, var2, var1.getCertificateStores());
         findCertificates(var3, var2, var1.getCertStores());
      } catch (AnnotatedException var5) {
         throw new ExtCertPathBuilderException("Error finding target certificate.", var5);
      }

      if (!var3.isEmpty()) {
         return var3;
      } else {
         Certificate var4 = var2.getCertificate();
         if (null == var4) {
            throw new CertPathBuilderException("No certificate found matching targetConstraints.");
         } else {
            return Collections.singleton(var4);
         }
      }
   }

   protected static TrustAnchor findTrustAnchor(X509Certificate var0, Set var1) throws AnnotatedException {
      return findTrustAnchor(var0, var1, null);
   }

   protected static TrustAnchor findTrustAnchor(X509Certificate var0, Set var1, String var2) throws AnnotatedException {
      TrustAnchor var3 = null;
      PublicKey var4 = null;
      Exception var5 = null;
      X509CertSelector var6 = new X509CertSelector();
      X500Principal var7 = var0.getIssuerX500Principal();
      var6.setSubject(var7);
      X500Name var8 = null;
      Iterator var9 = var1.iterator();

      while (var9.hasNext() && var3 == null) {
         var3 = (TrustAnchor)var9.next();
         if (var3.getTrustedCert() != null) {
            if (var6.match(var3.getTrustedCert())) {
               var4 = var3.getTrustedCert().getPublicKey();
            } else {
               var3 = null;
            }
         } else if (var3.getCA() != null && var3.getCAName() != null && var3.getCAPublicKey() != null) {
            if (var8 == null) {
               var8 = X500Name.getInstance(var7.getEncoded());
            }

            try {
               X500Name var10 = X500Name.getInstance(var3.getCA().getEncoded());
               if (var8.equals(var10)) {
                  var4 = var3.getCAPublicKey();
               } else {
                  var3 = null;
               }
            } catch (IllegalArgumentException var12) {
               var3 = null;
            }
         } else {
            var3 = null;
         }

         if (var4 != null) {
            try {
               verifyX509Certificate(var0, var4, var2);
            } catch (Exception var11) {
               var5 = var11;
               var3 = null;
               var4 = null;
            }
         }
      }

      if (var3 == null && var5 != null) {
         throw new AnnotatedException("TrustAnchor found but certificate validation failed.", var5);
      } else {
         return var3;
      }
   }

   static boolean isIssuerTrustAnchor(X509Certificate var0, Set var1, String var2) throws AnnotatedException {
      try {
         return findTrustAnchor(var0, var1, var2) != null;
      } catch (Exception var4) {
         return false;
      }
   }

   static List<PKIXCertStore> getAdditionalStoresFromAltNames(byte[] var0, Map<GeneralName, PKIXCertStore> var1) throws CertificateParsingException {
      if (var0 == null) {
         return Collections.EMPTY_LIST;
      } else {
         GeneralNames var2 = GeneralNames.getInstance(ASN1OctetString.getInstance(var0).getOctets());
         GeneralName[] var3 = var2.getNames();
         ArrayList var4 = new ArrayList();

         for (int var5 = 0; var5 != var3.length; var5++) {
            GeneralName var6 = var3[var5];
            PKIXCertStore var7 = (PKIXCertStore)var1.get(var6);
            if (var7 != null) {
               var4.add(var7);
            }
         }

         return var4;
      }
   }

   protected static Date getValidityDate(PKIXExtendedParameters var0, Date var1) {
      Date var2 = var0.getValidityDate();
      return null == var2 ? var1 : var2;
   }

   protected static boolean isSelfIssued(X509Certificate var0) {
      return var0.getSubjectDN().equals(var0.getIssuerDN());
   }

   protected static ASN1Primitive getExtensionValue(X509Extension var0, String var1) throws AnnotatedException {
      byte[] var2 = var0.getExtensionValue(var1);
      return null == var2 ? null : getObject(var1, var2);
   }

   private static ASN1Primitive getObject(String var0, byte[] var1) throws AnnotatedException {
      try {
         ASN1OctetString var2 = ASN1OctetString.getInstance(var1);
         return ASN1Primitive.fromByteArray(var2.getOctets());
      } catch (Exception var3) {
         throw new AnnotatedException("exception processing extension " + var0, var3);
      }
   }

   protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey var0) throws CertPathValidatorException {
      try {
         return SubjectPublicKeyInfo.getInstance(var0.getEncoded()).getAlgorithm();
      } catch (Exception var2) {
         throw new ExtCertPathValidatorException("Subject public key cannot be decoded.", var2);
      }
   }

   protected static final Set getQualifierSet(ASN1Sequence var0) throws CertPathValidatorException {
      HashSet var1 = new HashSet();
      if (var0 == null) {
         return var1;
      } else {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         ASN1OutputStream var3 = ASN1OutputStream.create(var2);

         for (Enumeration var4 = var0.getObjects(); var4.hasMoreElements(); var2.reset()) {
            try {
               var3.writeObject((ASN1Encodable)var4.nextElement());
               var1.add(new PolicyQualifierInfo(var2.toByteArray()));
            } catch (IOException var6) {
               throw new ExtCertPathValidatorException("Policy qualifier info cannot be decoded.", var6);
            }
         }

         return var1;
      }
   }

   static PKIXPolicyNode removeChildlessPolicyNodes(PKIXPolicyNode var0, List[] var1, int var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var2;

         while (--var3 >= 0) {
            List var4 = var1[var3];
            int var5 = var4.size();

            while (--var5 >= 0) {
               PKIXPolicyNode var6 = (PKIXPolicyNode)var4.get(var5);
               if (!var6.hasChildren()) {
                  var4.remove(var5);
                  PKIXPolicyNode var7 = (PKIXPolicyNode)var6.getParent();
                  if (var7 == null) {
                     return null;
                  }

                  var7.removeChild(var6);
               }
            }
         }

         return var0;
      }
   }

   static PKIXPolicyNode removePolicyNode(PKIXPolicyNode var0, List[] var1, PKIXPolicyNode var2) {
      if (var0 == null) {
         return null;
      } else {
         PKIXPolicyNode var3 = (PKIXPolicyNode)var2.getParent();
         if (var3 != null) {
            var3.removeChild(var2);
            removePolicyNodeRecurse(var1, var2);
            return var0;
         } else {
            for (int var4 = 0; var4 < var1.length; var4++) {
               var1[var4].clear();
            }

            return null;
         }
      }
   }

   private static void removePolicyNodeRecurse(List[] var0, PKIXPolicyNode var1) {
      var0[var1.getDepth()].remove(var1);
      if (var1.hasChildren()) {
         Iterator var2 = var1.getChildren();

         while (var2.hasNext()) {
            PKIXPolicyNode var3 = (PKIXPolicyNode)var2.next();
            removePolicyNodeRecurse(var0, var3);
         }
      }
   }

   protected static boolean processCertD1i(int var0, List[] var1, ASN1ObjectIdentifier var2, Set var3) {
      List var4 = var1[var0 - 1];

      for (int var5 = 0; var5 < var4.size(); var5++) {
         PKIXPolicyNode var6 = (PKIXPolicyNode)var4.get(var5);
         Set var7 = var6.getExpectedPolicies();
         if (var7.contains(var2.getId())) {
            HashSet var8 = new HashSet();
            var8.add(var2.getId());
            PKIXPolicyNode var9 = new PKIXPolicyNode(new ArrayList(), var0, var8, var6, var3, var2.getId(), false);
            var6.addChild(var9);
            var1[var0].add(var9);
            return true;
         }
      }

      return false;
   }

   static void processCertD1ii(int var0, List[] var1, ASN1ObjectIdentifier var2, Set var3) {
      PKIXPolicyNode var4 = findValidPolicy(var1[var0 - 1].iterator(), "2.5.29.32.0");
      if (var4 != null) {
         String var5 = var2.getId();
         HashSet var6 = new HashSet();
         var6.add(var5);
         PKIXPolicyNode var7 = new PKIXPolicyNode(new ArrayList(), var0, var6, var4, var3, var5, false);
         var4.addChild(var7);
         var1[var0].add(var7);
      }
   }

   protected static boolean isAnyPolicy(Set var0) {
      return var0 == null || var0.contains("2.5.29.32.0") || var0.isEmpty();
   }

   protected static void findCertificates(Set var0, PKIXCertStoreSelector var1, List var2) throws AnnotatedException {
      for (Object var4 : var2) {
         if (var4 instanceof Store) {
            Store var5 = (Store)var4;

            try {
               var0.addAll(var5.getMatches(var1));
            } catch (StoreException var7) {
               throw new AnnotatedException("Problem while picking certificates from X.509 store.", var7);
            }
         } else {
            CertStore var9 = (CertStore)var4;

            try {
               var0.addAll(PKIXCertStoreSelector.getCertificates(var1, var9));
            } catch (CertStoreException var8) {
               throw new AnnotatedException("Problem while picking certificates from certificate store.", var8);
            }
         }
      }
   }

   static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint var0, Map<GeneralName, PKIXCRLStore> var1, Date var2, JcaJceHelper var3) throws AnnotatedException {
      if (null == var0) {
         return Collections.EMPTY_LIST;
      } else {
         DistributionPoint[] var4;
         try {
            var4 = var0.getDistributionPoints();
         } catch (Exception var15) {
            throw new AnnotatedException("Distribution points could not be read.", var15);
         }

         ArrayList var5 = new ArrayList();

         for (int var6 = 0; var6 < var4.length; var6++) {
            DistributionPointName var7 = var4[var6].getDistributionPoint();
            if (var7 != null && var7.getType() == 0) {
               GeneralName[] var8 = GeneralNames.getInstance(var7.getName()).getNames();

               for (int var9 = 0; var9 < var8.length; var9++) {
                  PKIXCRLStore var10 = (PKIXCRLStore)var1.get(var8[var9]);
                  if (var10 != null) {
                     var5.add(var10);
                  }
               }
            }
         }

         if (var5.isEmpty() && Properties.isOverrideSet("org.bouncycastle.x509.enableCRLDP")) {
            CertificateFactory var17;
            try {
               var17 = var3.createCertificateFactory("X.509");
            } catch (Exception var14) {
               throw new AnnotatedException("cannot create certificate factory: " + var14.getMessage(), var14);
            }

            for (int var18 = 0; var18 < var4.length; var18++) {
               DistributionPointName var19 = var4[var18].getDistributionPoint();
               if (var19 != null && var19.getType() == 0) {
                  GeneralName[] var20 = GeneralNames.getInstance(var19.getName()).getNames();

                  for (int var21 = 0; var21 < var20.length; var21++) {
                     GeneralName var11 = var20[var21];
                     if (var11.getTagNo() == 6) {
                        try {
                           URI var12 = new URI(((ASN1String)var11.getName()).getString());
                           PKIXCRLStore var13 = CrlCache.getCrl(var17, var2, var12);
                           if (var13 != null) {
                              var5.add(var13);
                           }
                           break;
                        } catch (Exception var16) {
                        }
                     }
                  }
               }
            }
         }

         return var5;
      }
   }

   protected static void getCRLIssuersFromDistributionPoint(DistributionPoint var0, Collection var1, X509CRLSelector var2) throws AnnotatedException {
      ArrayList var3 = new ArrayList();
      if (var0.getCRLIssuer() != null) {
         GeneralName[] var4 = var0.getCRLIssuer().getNames();

         for (int var5 = 0; var5 < var4.length; var5++) {
            if (var4[var5].getTagNo() == 4) {
               try {
                  var3.add(X500Name.getInstance(var4[var5].getName().toASN1Primitive().getEncoded()));
               } catch (IOException var8) {
                  throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", var8);
               }
            }
         }
      } else {
         if (var0.getDistributionPoint() == null) {
            throw new AnnotatedException("CRL issuer is omitted from distribution point but no distributionPoint field present.");
         }

         Iterator var9 = var1.iterator();

         while (var9.hasNext()) {
            var3.add(var9.next());
         }
      }

      Iterator var10 = var3.iterator();

      while (var10.hasNext()) {
         try {
            var2.addIssuerName(((X500Name)var10.next()).getEncoded());
         } catch (IOException var7) {
            throw new AnnotatedException("Cannot decode CRL issuer information.", var7);
         }
      }
   }

   private static BigInteger getSerialNumber(Object var0) {
      return ((X509Certificate)var0).getSerialNumber();
   }

   protected static void getCertStatus(Date var0, X509CRL var1, Object var2, CertStatus var3) throws AnnotatedException {
      boolean var4;
      try {
         var4 = X509CRLObject.isIndirectCRL(var1);
      } catch (CRLException var9) {
         throw new AnnotatedException("Failed check for indirect CRL.", var9);
      }

      X509CRLEntry var5;
      if (var4) {
         var5 = var1.getRevokedCertificate(getSerialNumber(var2));
         if (var5 == null) {
            return;
         }

         X500Principal var6 = var5.getCertificateIssuer();
         X500Name var7;
         if (var6 == null) {
            var7 = PrincipalUtils.getIssuerPrincipal(var1);
         } else {
            var7 = PrincipalUtils.getX500Name(var6);
         }

         if (!PrincipalUtils.getEncodedIssuerPrincipal(var2).equals(var7)) {
            return;
         }
      } else {
         if (!PrincipalUtils.getEncodedIssuerPrincipal(var2).equals(PrincipalUtils.getIssuerPrincipal(var1))) {
            return;
         }

         var5 = var1.getRevokedCertificate(getSerialNumber(var2));
         if (var5 == null) {
            return;
         }
      }

      ASN1Enumerated var10 = null;
      if (var5.hasExtensions()) {
         checkCRLEntryCriticalExtensions(var5, "CRL entry has unsupported critical extensions.");

         try {
            var10 = ASN1Enumerated.getInstance(getExtensionValue(var5, Extension.reasonCode.getId()));
         } catch (Exception var8) {
            throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", var8);
         }
      }

      int var11 = null == var10 ? 0 : var10.intValueExact();
      if (var0.getTime() >= var5.getRevocationDate().getTime() || var11 == 0 || var11 == 1 || var11 == 2 || var11 == 10) {
         var3.setCertStatus(var11);
         var3.setRevocationDate(var5.getRevocationDate());
      }
   }

   protected static Set getDeltaCRLs(Date var0, X509CRL var1, List<CertStore> var2, List<PKIXCRLStore> var3, JcaJceHelper var4) throws AnnotatedException {
      X509CRLSelector var5 = new X509CRLSelector();

      try {
         var5.addIssuerName(PrincipalUtils.getIssuerPrincipal(var1).getEncoded());
      } catch (IOException var22) {
         throw new AnnotatedException("Cannot extract issuer from CRL.", var22);
      }

      BigInteger var6 = null;

      try {
         ASN1Primitive var7 = getExtensionValue(var1, CRL_NUMBER);
         if (var7 != null) {
            var6 = ASN1Integer.getInstance(var7).getPositiveValue();
         }
      } catch (Exception var24) {
         throw new AnnotatedException("CRL number extension could not be extracted from CRL.", var24);
      }

      byte[] var25;
      try {
         var25 = var1.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
      } catch (Exception var21) {
         throw new AnnotatedException("Issuing distribution point extension value could not be read.", var21);
      }

      var5.setMinCRLNumber(var6 == null ? null : var6.add(BigInteger.valueOf(1L)));
      PKIXCRLStoreSelector.Builder var8 = new PKIXCRLStoreSelector.Builder(var5);
      var8.setIssuingDistributionPoint(var25);
      var8.setIssuingDistributionPointEnabled(true);
      var8.setMaxBaseCRLNumber(var6);
      var8.setDeltaCRLIndicatorEnabled(true);
      PKIXCRLStoreSelector var9 = var8.build();
      Set var10 = getDeltaCRLs(PKIXCRLUtil.findCRLs(var9, var0, var2, var3));
      if (var10.isEmpty() && Properties.isOverrideSet("org.bouncycastle.x509.enableCRLDP")) {
         CertificateFactory var11;
         try {
            var11 = var4.createCertificateFactory("X.509");
         } catch (Exception var20) {
            throw new AnnotatedException("cannot create certificate factory: " + var20.getMessage(), var20);
         }

         CRLDistPoint var12 = CRLDistPoint.getInstance(var25);
         DistributionPoint[] var13 = var12.getDistributionPoints();

         for (int var14 = 0; var14 < var13.length; var14++) {
            DistributionPointName var15 = var13[var14].getDistributionPoint();
            if (var15 != null && var15.getType() == 0) {
               GeneralName[] var16 = GeneralNames.getInstance(var15.getName()).getNames();

               for (int var17 = 0; var17 < var16.length; var17++) {
                  GeneralName var18 = var16[var17];
                  if (var18.getTagNo() == 6) {
                     try {
                        PKIXCRLStore var19 = CrlCache.getCrl(var11, var0, new URI(((ASN1String)var18.getName()).getString()));
                        if (var19 != null) {
                           var10 = getDeltaCRLs(PKIXCRLUtil.findCRLs(var9, var0, Collections.EMPTY_LIST, Collections.singletonList(var19)));
                        }
                        break;
                     } catch (Exception var23) {
                     }
                  }
               }
            }
         }
      }

      return var10;
   }

   private static Set getDeltaCRLs(Set var0) {
      HashSet var1 = new HashSet();

      for (X509CRL var3 : var0) {
         if (isDeltaCRL(var3)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   private static boolean isDeltaCRL(X509CRL var0) {
      return hasCriticalExtension(var0, Extension.deltaCRLIndicator.getId());
   }

   protected static Set getCompleteCRLs(PKIXCertRevocationCheckerParameters var0, DistributionPoint var1, Object var2, PKIXExtendedParameters var3, Date var4) throws AnnotatedException, RecoverableCertPathValidatorException {
      X509CRLSelector var5 = new X509CRLSelector();

      try {
         HashSet var6 = new HashSet();
         var6.add(PrincipalUtils.getEncodedIssuerPrincipal(var2));
         getCRLIssuersFromDistributionPoint(var1, var6, var5);
      } catch (AnnotatedException var8) {
         throw new AnnotatedException("Could not get issuer information from distribution point.", var8);
      }

      if (var2 instanceof X509Certificate) {
         var5.setCertificateChecking((X509Certificate)var2);
      }

      PKIXCRLStoreSelector var9 = new PKIXCRLStoreSelector.Builder(var5).setCompleteCRLEnabled(true).build();
      Set var7 = PKIXCRLUtil.findCRLs(var9, var4, var3.getCertStores(), var3.getCRLStores());
      checkCRLsNotEmpty(var0, var7, var2);
      return var7;
   }

   protected static Date getValidCertDateFromValidityModel(Date var0, int var1, CertPath var2, int var3) throws AnnotatedException {
      if (1 == var1 && var3 > 0) {
         X509Certificate var4 = (X509Certificate)var2.getCertificates().get(var3 - 1);
         if (var3 - 1 == 0) {
            ASN1GeneralizedTime var5 = null;

            try {
               byte[] var6 = ((X509Certificate)var2.getCertificates().get(var3 - 1))
                  .getExtensionValue(ISISMTTObjectIdentifiers.id_isismtt_at_dateOfCertGen.getId());
               if (var6 != null) {
                  var5 = ASN1GeneralizedTime.getInstance(ASN1Primitive.fromByteArray(var6));
               }
            } catch (IOException var8) {
               throw new AnnotatedException("Date of cert gen extension could not be read.");
            } catch (IllegalArgumentException var9) {
               throw new AnnotatedException("Date of cert gen extension could not be read.");
            }

            if (var5 != null) {
               try {
                  return var5.getDate();
               } catch (ParseException var7) {
                  throw new AnnotatedException("Date from date of cert gen extension could not be parsed.", var7);
               }
            }
         }

         return var4.getNotBefore();
      } else {
         return var0;
      }
   }

   protected static PublicKey getNextWorkingKey(List var0, int var1, JcaJceHelper var2) throws CertPathValidatorException {
      Certificate var3 = (Certificate)var0.get(var1);
      PublicKey var4 = var3.getPublicKey();
      if (!(var4 instanceof DSAPublicKey)) {
         return var4;
      } else {
         DSAPublicKey var5 = (DSAPublicKey)var4;
         if (var5.getParams() != null) {
            return var5;
         } else {
            for (int var6 = var1 + 1; var6 < var0.size(); var6++) {
               X509Certificate var7 = (X509Certificate)var0.get(var6);
               var4 = var7.getPublicKey();
               if (!(var4 instanceof DSAPublicKey)) {
                  throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
               }

               DSAPublicKey var8 = (DSAPublicKey)var4;
               if (var8.getParams() != null) {
                  DSAParams var9 = var8.getParams();
                  DSAPublicKeySpec var10 = new DSAPublicKeySpec(var5.getY(), var9.getP(), var9.getQ(), var9.getG());

                  try {
                     KeyFactory var11 = var2.createKeyFactory("DSA");
                     return var11.generatePublic(var10);
                  } catch (Exception var12) {
                     throw new RuntimeException(var12.getMessage());
                  }
               }
            }

            throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
         }
      }
   }

   static Collection findIssuerCerts(X509Certificate var0, List<CertStore> var1, List<PKIXCertStore> var2) throws AnnotatedException {
      X509CertSelector var3 = new X509CertSelector();

      try {
         var3.setSubject(PrincipalUtils.getIssuerPrincipal(var0).getEncoded());
      } catch (Exception var8) {
         throw new AnnotatedException("Subject criteria for certificate selector to find issuer certificate could not be set.", var8);
      }

      PKIXCertStoreSelector var4 = new PKIXCertStoreSelector.Builder(var3).build();
      LinkedHashSet var5 = new LinkedHashSet();

      try {
         findCertificates(var5, var4, var1);
         findCertificates(var5, var4, var2);
         return var5;
      } catch (AnnotatedException var7) {
         throw new AnnotatedException("Issuer certificate cannot be searched.", var7);
      }
   }

   protected static void verifyX509Certificate(X509Certificate var0, PublicKey var1, String var2) throws GeneralSecurityException {
      if (var2 == null) {
         var0.verify(var1);
      } else {
         var0.verify(var1, var2);
      }
   }

   static void checkCRLsNotEmpty(PKIXCertRevocationCheckerParameters var0, Set var1, Object var2) throws RecoverableCertPathValidatorException {
      if (var1.isEmpty()) {
         if (var2 instanceof X509AttributeCertificate) {
            X509AttributeCertificate var4 = (X509AttributeCertificate)var2;
            throw new RecoverableCertPathValidatorException(
               "No CRLs found for issuer \"" + var4.getIssuer().getPrincipals()[0] + "\"", null, var0.getCertPath(), var0.getIndex()
            );
         } else {
            X509Certificate var3 = (X509Certificate)var2;
            throw new RecoverableCertPathValidatorException(
               "No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(PrincipalUtils.getIssuerPrincipal(var3)) + "\"",
               null,
               var0.getCertPath(),
               var0.getIndex()
            );
         }
      }
   }

   static void checkCRLCriticalExtensions(X509CRL var0, String var1) throws AnnotatedException {
      Set var2 = var0.getCriticalExtensionOIDs();
      if (var2 != null) {
         int var3 = var2.size();
         if (var3 > 0) {
            if (var2.contains(Extension.issuingDistributionPoint.getId())) {
               var3--;
            }

            if (var2.contains(Extension.deltaCRLIndicator.getId())) {
               var3--;
            }

            if (var3 > 0) {
               throw new AnnotatedException(var1);
            }
         }
      }
   }

   static void checkCRLEntryCriticalExtensions(X509CRLEntry var0, String var1) throws AnnotatedException {
      if (var0.hasUnsupportedCriticalExtension()) {
         throw new AnnotatedException(var1);
      }
   }

   static PKIXPolicyNode findValidPolicy(Iterator var0, String var1) {
      while (var0.hasNext()) {
         PKIXPolicyNode var2 = (PKIXPolicyNode)var0.next();
         if (var1.equals(var2.getValidPolicy())) {
            return var2;
         }
      }

      return null;
   }

   static boolean hasCriticalExtension(X509Certificate var0, String var1) {
      return hasCriticalExtension(var0.getCriticalExtensionOIDs(), var1);
   }

   static boolean hasCriticalExtension(X509CRL var0, String var1) {
      return hasCriticalExtension(var0.getCriticalExtensionOIDs(), var1);
   }

   private static boolean hasCriticalExtension(Set var0, String var1) {
      return var0 != null && var0.contains(var1);
   }
}
