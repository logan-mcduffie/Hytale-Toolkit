package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

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

   protected static X500Principal getEncodedIssuerPrincipal(Object var0) {
      if (var0 instanceof X509Certificate) {
         return ((X509Certificate)var0).getIssuerX500Principal();
      } else {
         throw new IllegalArgumentException("unknown certificate type");
      }
   }

   protected static Date getValidDate(PKIXParameters var0) {
      Date var1 = var0.getDate();
      if (var1 == null) {
         var1 = new Date();
      }

      return var1;
   }

   protected static X500Principal getSubjectPrincipal(X509Certificate var0) {
      return var0.getSubjectX500Principal();
   }

   protected static boolean isSelfIssued(X509Certificate var0) {
      return var0.getSubjectDN().equals(var0.getIssuerDN());
   }

   protected static ASN1Primitive getExtensionValue(X509Extension var0, String var1) throws AnnotatedException {
      byte[] var2 = var0.getExtensionValue(var1);
      return var2 == null ? null : getObject(var1, var2);
   }

   private static ASN1Primitive getObject(String var0, byte[] var1) throws AnnotatedException {
      try {
         ASN1InputStream var2 = new ASN1InputStream(var1);
         ASN1OctetString var3 = (ASN1OctetString)var2.readObject();
         var2 = new ASN1InputStream(var3.getOctets());
         return var2.readObject();
      } catch (Exception var4) {
         throw new AnnotatedException("exception processing extension " + var0, var4);
      }
   }

   protected static X500Principal getIssuerPrincipal(X509CRL var0) {
      return var0.getIssuerX500Principal();
   }

   protected static AlgorithmIdentifier getAlgorithmIdentifier(PublicKey var0) throws CertPathValidatorException {
      try {
         ASN1InputStream var1 = new ASN1InputStream(var0.getEncoded());
         SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(var1.readObject());
         return var2.getAlgorithm();
      } catch (Exception var3) {
         throw new CertPathValidatorException("Subject public key cannot be decoded.", var3);
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
               throw new CertPathValidatorException("Policy qualifier info cannot be decoded.", var6);
            }
         }

         return var1;
      }
   }

   protected static PKIXPolicyNode removePolicyNode(PKIXPolicyNode var0, List[] var1, PKIXPolicyNode var2) {
      PKIXPolicyNode var3 = (PKIXPolicyNode)var2.getParent();
      if (var0 == null) {
         return null;
      } else if (var3 != null) {
         var3.removeChild(var2);
         removePolicyNodeRecurse(var1, var2);
         return var0;
      } else {
         for (int var4 = 0; var4 < var1.length; var4++) {
            var1[var4] = new ArrayList();
         }

         return null;
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

   protected static void processCertD1ii(int var0, List[] var1, ASN1ObjectIdentifier var2, Set var3) {
      List var4 = var1[var0 - 1];

      for (int var5 = 0; var5 < var4.size(); var5++) {
         PKIXPolicyNode var6 = (PKIXPolicyNode)var4.get(var5);
         if ("2.5.29.32.0".equals(var6.getValidPolicy())) {
            HashSet var7 = new HashSet();
            var7.add(var2.getId());
            PKIXPolicyNode var8 = new PKIXPolicyNode(new ArrayList(), var0, var7, var6, var3, var2.getId(), false);
            var6.addChild(var8);
            var1[var0].add(var8);
            return;
         }
      }
   }

   protected static void prepareNextCertB1(int var0, List[] var1, String var2, Map var3, X509Certificate var4) throws AnnotatedException, CertPathValidatorException {
      boolean var5 = false;

      for (PKIXPolicyNode var7 : var1[var0]) {
         if (var7.getValidPolicy().equals(var2)) {
            var5 = true;
            var7.setExpectedPolicies((Set)var3.get(var2));
            break;
         }
      }

      if (!var5) {
         for (PKIXPolicyNode var18 : var1[var0]) {
            if ("2.5.29.32.0".equals(var18.getValidPolicy())) {
               Set var8 = null;
               ASN1Sequence var9 = null;

               try {
                  var9 = DERSequence.getInstance(getExtensionValue(var4, CERTIFICATE_POLICIES));
               } catch (Exception var16) {
                  throw new AnnotatedException("Certificate policies cannot be decoded.", var16);
               }

               Enumeration var10 = var9.getObjects();

               while (var10.hasMoreElements()) {
                  Object var11 = null;

                  try {
                     var11 = PolicyInformation.getInstance(var10.nextElement());
                  } catch (Exception var15) {
                     throw new AnnotatedException("Policy information cannot be decoded.", var15);
                  }

                  if ("2.5.29.32.0".equals(((PolicyInformation)var11).getPolicyIdentifier().getId())) {
                     try {
                        var8 = getQualifierSet(((PolicyInformation)var11).getPolicyQualifiers());
                        break;
                     } catch (CertPathValidatorException var14) {
                        throw new CertPathValidatorException("Policy qualifier info set could not be built.", var14);
                     }
                  }
               }

               boolean var21 = false;
               if (var4.getCriticalExtensionOIDs() != null) {
                  var21 = var4.getCriticalExtensionOIDs().contains(CERTIFICATE_POLICIES);
               }

               PKIXPolicyNode var12 = (PKIXPolicyNode)var18.getParent();
               if ("2.5.29.32.0".equals(var12.getValidPolicy())) {
                  PKIXPolicyNode var13 = new PKIXPolicyNode(new ArrayList(), var0, (Set)var3.get(var2), var12, var8, var2, var21);
                  var12.addChild(var13);
                  var1[var0].add(var13);
               }
               break;
            }
         }
      }
   }

   protected static PKIXPolicyNode prepareNextCertB2(int var0, List[] var1, String var2, PKIXPolicyNode var3) {
      Iterator var4 = var1[var0].iterator();

      while (var4.hasNext()) {
         PKIXPolicyNode var5 = (PKIXPolicyNode)var4.next();
         if (var5.getValidPolicy().equals(var2)) {
            PKIXPolicyNode var6 = (PKIXPolicyNode)var5.getParent();
            var6.removeChild(var5);
            var4.remove();

            for (int var7 = var0 - 1; var7 >= 0; var7--) {
               List var8 = var1[var7];

               for (int var9 = 0; var9 < var8.size(); var9++) {
                  PKIXPolicyNode var10 = (PKIXPolicyNode)var8.get(var9);
                  if (!var10.hasChildren()) {
                     var3 = removePolicyNode(var3, var1, var10);
                     if (var3 == null) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return var3;
   }

   protected static boolean isAnyPolicy(Set var0) {
      return var0 == null || var0.contains("2.5.29.32.0") || var0.isEmpty();
   }

   protected static Collection findCertificates(X509CertStoreSelector var0, List var1) throws AnnotatedException {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();
      Object var4 = null;

      try {
         var4 = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var10) {
         throw new AnnotatedException(var10.getMessage(), var10);
      }

      while (var3.hasNext()) {
         Object var5 = var3.next();
         if (var5 instanceof Store) {
            Store var15 = (Store)var5;

            try {
               for (Object var8 : var15.getMatches(var0)) {
                  if (var8 instanceof Encodable) {
                     var2.add(var4.generateCertificate(new ByteArrayInputStream(((Encodable)var8).getEncoded())));
                  } else {
                     if (!(var8 instanceof Certificate)) {
                        throw new AnnotatedException("Unknown object found in certificate store.");
                     }

                     var2.add(var8);
                  }
               }
            } catch (StoreException var11) {
               throw new AnnotatedException("Problem while picking certificates from X.509 store.", var11);
            } catch (IOException var12) {
               throw new AnnotatedException("Problem while extracting certificates from X.509 store.", var12);
            } catch (CertificateException var13) {
               throw new AnnotatedException("Problem while extracting certificates from X.509 store.", var13);
            }
         } else {
            CertStore var6 = (CertStore)var5;

            try {
               var2.addAll(var6.getCertificates(var0));
            } catch (CertStoreException var9) {
               throw new AnnotatedException("Problem while picking certificates from certificate store.", var9);
            }
         }
      }

      return var2;
   }

   protected static Collection findCertificates(PKIXCertStoreSelector var0, List var1) throws AnnotatedException {
      HashSet var2 = new HashSet();

      for (Object var4 : var1) {
         if (var4 instanceof Store) {
            Store var5 = (Store)var4;

            try {
               var2.addAll(var5.getMatches(var0));
            } catch (StoreException var7) {
               throw new AnnotatedException("Problem while picking certificates from X.509 store.", var7);
            }
         } else {
            CertStore var9 = (CertStore)var4;

            try {
               var2.addAll(PKIXCertStoreSelector.getCertificates(var0, var9));
            } catch (CertStoreException var8) {
               throw new AnnotatedException("Problem while picking certificates from certificate store.", var8);
            }
         }
      }

      return var2;
   }

   private static BigInteger getSerialNumber(Object var0) {
      return ((X509Certificate)var0).getSerialNumber();
   }

   protected static void getCertStatus(Date var0, X509CRL var1, Object var2, CertStatus var3) throws AnnotatedException {
      X509CRLEntry var4 = null;

      boolean var5;
      try {
         var5 = isIndirectCRL(var1);
      } catch (CRLException var9) {
         throw new AnnotatedException("Failed check for indirect CRL.", var9);
      }

      if (var5) {
         var4 = var1.getRevokedCertificate(getSerialNumber(var2));
         if (var4 == null) {
            return;
         }

         X500Principal var6 = var4.getCertificateIssuer();
         if (var6 == null) {
            var6 = getIssuerPrincipal(var1);
         }

         if (!getEncodedIssuerPrincipal(var2).equals(var6)) {
            return;
         }
      } else {
         if (!getEncodedIssuerPrincipal(var2).equals(getIssuerPrincipal(var1))) {
            return;
         }

         var4 = var1.getRevokedCertificate(getSerialNumber(var2));
         if (var4 == null) {
            return;
         }
      }

      ASN1Enumerated var11 = null;
      if (var4.hasExtensions()) {
         try {
            var11 = ASN1Enumerated.getInstance(getExtensionValue(var4, Extension.reasonCode.getId()));
         } catch (Exception var8) {
            throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", var8);
         }
      }

      int var7 = null == var11 ? 0 : var11.getValue().intValue();
      if (var0.getTime() >= var4.getRevocationDate().getTime() || var7 == 0 || var7 == 1 || var7 == 2 || var7 == 10) {
         var3.setCertStatus(var7);
         var3.setRevocationDate(var4.getRevocationDate());
      }
   }

   protected static PublicKey getNextWorkingKey(List var0, int var1) throws CertPathValidatorException {
      Certificate var2 = (Certificate)var0.get(var1);
      PublicKey var3 = var2.getPublicKey();
      if (!(var3 instanceof DSAPublicKey)) {
         return var3;
      } else {
         DSAPublicKey var4 = (DSAPublicKey)var3;
         if (var4.getParams() != null) {
            return var4;
         } else {
            for (int var5 = var1 + 1; var5 < var0.size(); var5++) {
               X509Certificate var6 = (X509Certificate)var0.get(var5);
               var3 = var6.getPublicKey();
               if (!(var3 instanceof DSAPublicKey)) {
                  throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
               }

               DSAPublicKey var7 = (DSAPublicKey)var3;
               if (var7.getParams() != null) {
                  DSAParams var8 = var7.getParams();
                  DSAPublicKeySpec var9 = new DSAPublicKeySpec(var4.getY(), var8.getP(), var8.getQ(), var8.getG());

                  try {
                     KeyFactory var10 = KeyFactory.getInstance("DSA");
                     return var10.generatePublic(var9);
                  } catch (Exception var11) {
                     throw new RuntimeException(var11.getMessage());
                  }
               }
            }

            throw new CertPathValidatorException("DSA parameters cannot be inherited from previous certificate.");
         }
      }
   }

   protected static void verifyX509Certificate(X509Certificate var0, PublicKey var1, String var2) throws GeneralSecurityException {
      if (var2 == null) {
         var0.verify(var1);
      } else {
         var0.verify(var1, var2);
      }
   }

   static boolean isIndirectCRL(X509CRL var0) throws CRLException {
      try {
         byte[] var1 = var0.getExtensionValue(Extension.issuingDistributionPoint.getId());
         return var1 != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(var1).getOctets()).isIndirectCRL();
      } catch (Exception var2) {
         throw new CRLException("Exception reading IssuingDistributionPoint: " + var2);
      }
   }

   protected static Date getValidityDate(PKIXParameters var0, Date var1) {
      Date var2 = var0.getDate();
      return null == var2 ? var1 : var2;
   }
}
