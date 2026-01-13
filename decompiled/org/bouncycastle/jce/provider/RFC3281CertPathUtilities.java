package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.PKIXAttrCertChecker;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

class RFC3281CertPathUtilities {
   private static final String TARGET_INFORMATION = Extension.targetInformation.getId();
   private static final String NO_REV_AVAIL = Extension.noRevAvail.getId();
   private static final String CRL_DISTRIBUTION_POINTS = Extension.cRLDistributionPoints.getId();
   private static final String AUTHORITY_INFO_ACCESS = Extension.authorityInfoAccess.getId();

   protected static void processAttrCert7(X509AttributeCertificate var0, CertPath var1, CertPath var2, PKIXExtendedParameters var3, Set var4) throws CertPathValidatorException {
      Set var5 = var0.getCriticalExtensionOIDs();
      if (var5.contains(TARGET_INFORMATION)) {
         try {
            TargetInformation.getInstance(CertPathValidatorUtilities.getExtensionValue(var0, TARGET_INFORMATION));
         } catch (AnnotatedException var7) {
            throw new ExtCertPathValidatorException("Target information extension could not be read.", var7);
         } catch (IllegalArgumentException var8) {
            throw new ExtCertPathValidatorException("Target information extension could not be read.", var8);
         }
      }

      var5.remove(TARGET_INFORMATION);
      Iterator var6 = var4.iterator();

      while (var6.hasNext()) {
         ((PKIXAttrCertChecker)var6.next()).check(var0, var1, var2, var5);
      }

      if (!var5.isEmpty()) {
         throw new CertPathValidatorException("Attribute certificate contains unsupported critical extensions: " + var5);
      }
   }

   protected static void checkCRLs(
      X509AttributeCertificate var0, PKIXExtendedParameters var1, Date var2, Date var3, X509Certificate var4, List var5, JcaJceHelper var6
   ) throws CertPathValidatorException {
      if (var1.isRevocationEnabled()) {
         if (var0.getExtensionValue(NO_REV_AVAIL) == null) {
            CRLDistPoint var7 = null;

            try {
               var7 = CRLDistPoint.getInstance(CertPathValidatorUtilities.getExtensionValue(var0, CRL_DISTRIBUTION_POINTS));
            } catch (AnnotatedException var21) {
               throw new CertPathValidatorException("CRL distribution point extension could not be read.", var21);
            }

            ArrayList var8 = new ArrayList();

            try {
               var8.addAll(CertPathValidatorUtilities.getAdditionalStoresFromCRLDistributionPoint(var7, var1.getNamedCRLStoreMap(), var3, var6));
            } catch (AnnotatedException var20) {
               throw new CertPathValidatorException("No additional CRL locations could be decoded from CRL distribution point extension.", var20);
            }

            PKIXExtendedParameters.Builder var9 = new PKIXExtendedParameters.Builder(var1);
            Iterator var10 = var8.iterator();

            while (var10.hasNext()) {
               var9.addCRLStore((PKIXCRLStore)var8);
            }

            var1 = var9.build();
            CertStatus var25 = new CertStatus();
            ReasonsMask var11 = new ReasonsMask();
            AnnotatedException var12 = null;
            boolean var13 = false;
            if (var7 != null) {
               Object var14 = null;

               try {
                  var14 = var7.getDistributionPoints();
               } catch (Exception var19) {
                  throw new ExtCertPathValidatorException("Distribution points could not be read.", var19);
               }

               try {
                  for (int var15 = 0; var15 < ((Object[])var14).length && var25.getCertStatus() == 11 && !var11.isAllReasons(); var15++) {
                     PKIXExtendedParameters var16 = (PKIXExtendedParameters)var1.clone();
                     checkCRL((DistributionPoint)((Object[])var14)[var15], var0, var16, var2, var3, var4, var25, var11, var5, var6);
                     var13 = true;
                  }
               } catch (AnnotatedException var22) {
                  var12 = new AnnotatedException("No valid CRL for distribution point found.", var22);
               }
            }

            if (var25.getCertStatus() == 11 && !var11.isAllReasons()) {
               try {
                  X500Name var27;
                  try {
                     var27 = PrincipalUtils.getEncodedIssuerPrincipal(var0);
                  } catch (Exception var17) {
                     throw new AnnotatedException("Issuer from certificate for CRL could not be reencoded.", var17);
                  }

                  DistributionPoint var30 = new DistributionPoint(new DistributionPointName(0, new GeneralNames(new GeneralName(4, var27))), null, null);
                  PKIXExtendedParameters var31 = (PKIXExtendedParameters)var1.clone();
                  checkCRL(var30, var0, var31, var2, var3, var4, var25, var11, var5, var6);
                  var13 = true;
               } catch (AnnotatedException var18) {
                  var12 = new AnnotatedException("No valid CRL for distribution point found.", var18);
               }
            }

            if (!var13) {
               throw new ExtCertPathValidatorException("No valid CRL found.", var12);
            }

            if (var25.getCertStatus() != 11) {
               String var28 = "Attribute certificate revocation after " + var25.getRevocationDate();
               var28 = var28 + ", reason: " + RFC3280CertPathUtilities.crlReasons[var25.getCertStatus()];
               throw new CertPathValidatorException(var28);
            }

            if (!var11.isAllReasons() && var25.getCertStatus() == 11) {
               var25.setCertStatus(12);
            }

            if (var25.getCertStatus() == 12) {
               throw new CertPathValidatorException("Attribute certificate status could not be determined.");
            }
         } else if (var0.getExtensionValue(CRL_DISTRIBUTION_POINTS) != null || var0.getExtensionValue(AUTHORITY_INFO_ACCESS) != null) {
            throw new CertPathValidatorException("No rev avail extension is set, but also an AC revocation pointer.");
         }
      }
   }

   protected static void additionalChecks(X509AttributeCertificate var0, Set var1, Set var2) throws CertPathValidatorException {
      for (String var4 : var1) {
         if (var0.getAttributes(var4) != null) {
            throw new CertPathValidatorException("Attribute certificate contains prohibited attribute: " + var4 + ".");
         }
      }

      for (String var6 : var2) {
         if (var0.getAttributes(var6) == null) {
            throw new CertPathValidatorException("Attribute certificate does not contain necessary attribute: " + var6 + ".");
         }
      }
   }

   protected static void processAttrCert5(X509AttributeCertificate var0, Date var1) throws CertPathValidatorException {
      try {
         var0.checkValidity(var1);
      } catch (CertificateExpiredException var3) {
         throw new ExtCertPathValidatorException("Attribute certificate is not valid.", var3);
      } catch (CertificateNotYetValidException var4) {
         throw new ExtCertPathValidatorException("Attribute certificate is not valid.", var4);
      }
   }

   protected static void processAttrCert4(X509Certificate var0, Set var1) throws CertPathValidatorException {
      boolean var3 = false;

      for (TrustAnchor var5 : var1) {
         if (var0.getSubjectX500Principal().getName("RFC2253").equals(var5.getCAName()) || var0.equals(var5.getTrustedCert())) {
            var3 = true;
         }
      }

      if (!var3) {
         throw new CertPathValidatorException("Attribute certificate issuer is not directly trusted.");
      }
   }

   protected static void processAttrCert3(X509Certificate var0, PKIXExtendedParameters var1) throws CertPathValidatorException {
      boolean[] var2 = var0.getKeyUsage();
      if (var2 == null || var2.length > 0 && var2[0] || var2.length > 1 && var2[1]) {
         if (var0.getBasicConstraints() != -1) {
            throw new CertPathValidatorException("Attribute certificate issuer is also a public key certificate issuer.");
         }
      } else {
         throw new CertPathValidatorException("Attribute certificate issuer public key cannot be used to validate digital signatures.");
      }
   }

   protected static CertPathValidatorResult processAttrCert2(CertPath var0, PKIXExtendedParameters var1) throws CertPathValidatorException {
      Object var2 = null;

      try {
         var2 = CertPathValidator.getInstance("PKIX", "BC");
      } catch (NoSuchProviderException var6) {
         throw new ExtCertPathValidatorException("Support class could not be created.", var6);
      } catch (NoSuchAlgorithmException var7) {
         throw new ExtCertPathValidatorException("Support class could not be created.", var7);
      }

      try {
         return var2.validate(var0, var1);
      } catch (CertPathValidatorException var4) {
         throw new ExtCertPathValidatorException("Certification path for issuer certificate of attribute certificate could not be validated.", var4);
      } catch (InvalidAlgorithmParameterException var5) {
         throw new RuntimeException(var5.getMessage());
      }
   }

   protected static CertPath processAttrCert1(X509AttributeCertificate var0, PKIXExtendedParameters var1) throws CertPathValidatorException {
      CertPathBuilderResult var2 = null;
      LinkedHashSet var3 = new LinkedHashSet();
      if (var0.getHolder().getIssuer() != null) {
         X509CertSelector var4 = new X509CertSelector();
         var4.setSerialNumber(var0.getHolder().getSerialNumber());
         Principal[] var5 = var0.getHolder().getIssuer();

         for (int var6 = 0; var6 < var5.length; var6++) {
            try {
               if (var5[var6] instanceof X500Principal) {
                  var4.setIssuer(((X500Principal)var5[var6]).getEncoded());
               }

               PKIXCertStoreSelector var7 = new PKIXCertStoreSelector.Builder(var4).build();
               CertPathValidatorUtilities.findCertificates(var3, var7, var1.getCertStores());
            } catch (AnnotatedException var16) {
               throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", var16);
            } catch (IOException var17) {
               throw new ExtCertPathValidatorException("Unable to encode X500 principal.", var17);
            }
         }

         if (var3.isEmpty()) {
            throw new CertPathValidatorException("Public key certificate specified in base certificate ID for attribute certificate cannot be found.");
         }
      }

      if (var0.getHolder().getEntityNames() != null) {
         X509CertStoreSelector var18 = new X509CertStoreSelector();
         Principal[] var20 = var0.getHolder().getEntityNames();

         for (int var22 = 0; var22 < var20.length; var22++) {
            try {
               if (var20[var22] instanceof X500Principal) {
                  var18.setIssuer(((X500Principal)var20[var22]).getEncoded());
               }

               PKIXCertStoreSelector var24 = new PKIXCertStoreSelector.Builder(var18).build();
               CertPathValidatorUtilities.findCertificates(var3, var24, var1.getCertStores());
            } catch (AnnotatedException var14) {
               throw new ExtCertPathValidatorException("Public key certificate for attribute certificate cannot be searched.", var14);
            } catch (IOException var15) {
               throw new ExtCertPathValidatorException("Unable to encode X500 principal.", var15);
            }
         }

         if (var3.isEmpty()) {
            throw new CertPathValidatorException("Public key certificate specified in entity name for attribute certificate cannot be found.");
         }
      }

      PKIXExtendedParameters.Builder var19 = new PKIXExtendedParameters.Builder(var1);
      ExtCertPathValidatorException var21 = null;
      Iterator var23 = var3.iterator();

      while (var23.hasNext()) {
         X509CertStoreSelector var25 = new X509CertStoreSelector();
         var25.setCertificate((X509Certificate)var23.next());
         var19.setTargetConstraints(new PKIXCertStoreSelector.Builder(var25).build());
         CertPathBuilder var8 = null;

         try {
            var8 = CertPathBuilder.getInstance("PKIX", "BC");
         } catch (NoSuchProviderException var12) {
            throw new ExtCertPathValidatorException("Support class could not be created.", var12);
         } catch (NoSuchAlgorithmException var13) {
            throw new ExtCertPathValidatorException("Support class could not be created.", var13);
         }

         try {
            var2 = var8.build(new PKIXExtendedBuilderParameters.Builder(var19.build()).build());
         } catch (CertPathBuilderException var10) {
            var21 = new ExtCertPathValidatorException("Certification path for public key certificate of attribute certificate could not be build.", var10);
         } catch (InvalidAlgorithmParameterException var11) {
            throw new RuntimeException(var11.getMessage());
         }
      }

      if (var21 != null) {
         throw var21;
      } else {
         return var2.getCertPath();
      }
   }

   private static void checkCRL(
      DistributionPoint var0,
      X509AttributeCertificate var1,
      PKIXExtendedParameters var2,
      Date var3,
      Date var4,
      X509Certificate var5,
      CertStatus var6,
      ReasonsMask var7,
      List var8,
      JcaJceHelper var9
   ) throws AnnotatedException, RecoverableCertPathValidatorException {
      if (var1.getExtensionValue(X509Extensions.NoRevAvail.getId()) == null) {
         if (var4.getTime() > var3.getTime()) {
            throw new AnnotatedException("Validation time is in future.");
         } else {
            PKIXCertRevocationCheckerParameters var10 = new PKIXCertRevocationCheckerParameters(var2, var4, null, -1, var5, null);
            Set var11 = CertPathValidatorUtilities.getCompleteCRLs(var10, var0, var1, var2, var4);
            boolean var12 = false;
            AnnotatedException var13 = null;
            Iterator var14 = var11.iterator();

            while (var14.hasNext() && var6.getCertStatus() == 11 && !var7.isAllReasons()) {
               try {
                  X509CRL var15 = (X509CRL)var14.next();
                  ReasonsMask var16 = RFC3280CertPathUtilities.processCRLD(var15, var0);
                  if (var16.hasNewReasons(var7)) {
                     Set var17 = RFC3280CertPathUtilities.processCRLF(var15, var1, null, null, var2, var8, var9);
                     PublicKey var18 = RFC3280CertPathUtilities.processCRLG(var15, var17);
                     X509CRL var19 = null;
                     if (var2.isUseDeltasEnabled()) {
                        Set var20 = CertPathValidatorUtilities.getDeltaCRLs(var3, var15, var2.getCertStores(), var2.getCRLStores(), var9);
                        var19 = RFC3280CertPathUtilities.processCRLH(var20, var18);
                     }

                     if (var2.getValidityModel() != 1 && var1.getNotAfter().getTime() < var15.getThisUpdate().getTime()) {
                        throw new AnnotatedException("No valid CRL for current time found.");
                     }

                     RFC3280CertPathUtilities.processCRLB1(var0, var1, var15);
                     RFC3280CertPathUtilities.processCRLB2(var0, var1, var15);
                     RFC3280CertPathUtilities.processCRLC(var19, var15, var2);
                     RFC3280CertPathUtilities.processCRLI(var4, var19, var1, var6, var2);
                     RFC3280CertPathUtilities.processCRLJ(var4, var15, var1, var6);
                     if (var6.getCertStatus() == 8) {
                        var6.setCertStatus(11);
                     }

                     var7.addReasons(var16);
                     var12 = true;
                  }
               } catch (AnnotatedException var21) {
                  var13 = var21;
               }
            }

            if (!var12) {
               throw var13;
            }
         }
      }
   }
}
