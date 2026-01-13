package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.ExtendedPKIXParameters;

public class PKIXCertPathValidatorSpi_8 extends CertPathValidatorSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private final boolean isForCRLCheck;

   public PKIXCertPathValidatorSpi_8() {
      this(false);
   }

   public PKIXCertPathValidatorSpi_8(boolean var1) {
      this.isForCRLCheck = var1;
   }

   public PKIXCertPathChecker engineGetRevocationChecker() {
      return new ProvRevocationChecker(this.helper);
   }

   @Override
   public CertPathValidatorResult engineValidate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException {
      PKIXExtendedParameters var3;
      if (var2 instanceof PKIXParameters) {
         PKIXExtendedParameters.Builder var4 = new PKIXExtendedParameters.Builder((PKIXParameters)var2);
         if (var2 instanceof ExtendedPKIXParameters) {
            ExtendedPKIXParameters var5 = (ExtendedPKIXParameters)var2;
            var4.setUseDeltasEnabled(var5.isUseDeltasEnabled());
            var4.setValidityModel(var5.getValidityModel());
         }

         var3 = var4.build();
      } else if (var2 instanceof PKIXExtendedBuilderParameters) {
         var3 = ((PKIXExtendedBuilderParameters)var2).getBaseParameters();
      } else {
         if (!(var2 instanceof PKIXExtendedParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
         }

         var3 = (PKIXExtendedParameters)var2;
      }

      if (var3.getTrustAnchors() == null) {
         throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation.");
      } else {
         List var40 = var1.getCertificates();
         int var41 = var40.size();
         if (var40.isEmpty()) {
            throw new CertPathValidatorException("Certification path is empty.", null, var1, -1);
         } else {
            Date var6 = new Date();
            Date var7 = CertPathValidatorUtilities.getValidityDate(var3, var6);
            Set var8 = var3.getInitialPolicies();

            TrustAnchor var9;
            try {
               var9 = CertPathValidatorUtilities.findTrustAnchor((X509Certificate)var40.get(var40.size() - 1), var3.getTrustAnchors(), var3.getSigProvider());
               if (var9 == null) {
                  throw new CertPathValidatorException("Trust anchor for certification path not found.", null, var1, -1);
               }

               checkCertificate(var9.getTrustedCert());
            } catch (AnnotatedException var38) {
               throw new CertPathValidatorException(var38.getMessage(), var38.getUnderlyingException(), var1, var40.size() - 1);
            }

            var3 = new PKIXExtendedParameters.Builder(var3).setTrustAnchor(var9).build();
            Object var10 = null;
            ArrayList var11 = new ArrayList();

            for (PKIXCertPathChecker var13 : var3.getCertPathCheckers()) {
               var13.init(false);
               if (var13 instanceof PKIXRevocationChecker) {
                  if (var10 != null) {
                     throw new CertPathValidatorException("only one PKIXRevocationChecker allowed");
                  }

                  var10 = var13 instanceof PKIXCertRevocationChecker ? (PKIXCertRevocationChecker)var13 : new WrappedRevocationChecker(var13);
               } else {
                  var11.add(var13);
               }
            }

            if (var3.isRevocationEnabled() && var10 == null) {
               var10 = new ProvRevocationChecker(this.helper);
            }

            int var42 = 0;
            ArrayList[] var15 = new ArrayList[var41 + 1];

            for (int var16 = 0; var16 < var15.length; var16++) {
               var15[var16] = new ArrayList();
            }

            HashSet var44 = new HashSet();
            var44.add("2.5.29.32.0");
            PKIXPolicyNode var17 = new PKIXPolicyNode(new ArrayList(), 0, var44, null, new HashSet(), "2.5.29.32.0", false);
            var15[0].add(var17);
            PKIXNameConstraintValidator var18 = new PKIXNameConstraintValidator();
            HashSet var20 = new HashSet();
            int var19;
            if (var3.isExplicitPolicyRequired()) {
               var19 = 0;
            } else {
               var19 = var41 + 1;
            }

            int var21;
            if (var3.isAnyPolicyInhibited()) {
               var21 = 0;
            } else {
               var21 = var41 + 1;
            }

            int var22;
            if (var3.isPolicyMappingInhibited()) {
               var22 = 0;
            } else {
               var22 = var41 + 1;
            }

            X509Certificate var25 = var9.getTrustedCert();

            PublicKey var23;
            X500Name var24;
            try {
               if (var25 != null) {
                  var24 = PrincipalUtils.getSubjectPrincipal(var25);
                  var23 = var25.getPublicKey();
               } else {
                  var24 = PrincipalUtils.getCA(var9);
                  var23 = var9.getCAPublicKey();
               }
            } catch (RuntimeException var37) {
               throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", var37, var1, -1);
            }

            AlgorithmIdentifier var26 = null;

            try {
               var26 = CertPathValidatorUtilities.getAlgorithmIdentifier(var23);
            } catch (CertPathValidatorException var36) {
               throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", var36, var1, -1);
            }

            ASN1ObjectIdentifier var27 = var26.getAlgorithm();
            ASN1Encodable var28 = var26.getParameters();
            int var29 = var41;
            if (var3.getTargetConstraints() != null && !var3.getTargetConstraints().match((Certificate)((X509Certificate)var40.get(0)))) {
               throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, var1, 0);
            } else {
               X509Certificate var30 = null;

               for (var42 = var40.size() - 1; var42 >= 0; var42--) {
                  int var14 = var41 - var42;
                  var30 = (X509Certificate)var40.get(var42);
                  boolean var31 = var42 == var40.size() - 1;

                  try {
                     checkCertificate(var30);
                  } catch (AnnotatedException var34) {
                     throw new CertPathValidatorException(var34.getMessage(), var34.getUnderlyingException(), var1, var42);
                  }

                  RFC3280CertPathUtilities.processCertA(var1, var3, var7, (PKIXCertRevocationChecker)var10, var42, var23, var31, var24, var25);
                  RFC3280CertPathUtilities.processCertBC(var1, var42, var18, this.isForCRLCheck);
                  PKIXPolicyNode var45 = RFC3280CertPathUtilities.processCertD(var1, var42, var20, var17, var15, var21, this.isForCRLCheck);
                  var17 = RFC3280CertPathUtilities.processCertE(var1, var42, var45);
                  RFC3280CertPathUtilities.processCertF(var1, var42, var17, var19);
                  if (var14 != var41) {
                     if (var30 != null && var30.getVersion() == 1) {
                        if (var14 != 1 || !var30.equals(var9.getTrustedCert())) {
                           throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, var1, var42);
                        }
                     } else {
                        RFC3280CertPathUtilities.prepareNextCertA(var1, var42);
                        var17 = RFC3280CertPathUtilities.prepareCertB(var1, var42, var15, var17, var22);
                        RFC3280CertPathUtilities.prepareNextCertG(var1, var42, var18);
                        var19 = RFC3280CertPathUtilities.prepareNextCertH1(var1, var42, var19);
                        var22 = RFC3280CertPathUtilities.prepareNextCertH2(var1, var42, var22);
                        var21 = RFC3280CertPathUtilities.prepareNextCertH3(var1, var42, var21);
                        var19 = RFC3280CertPathUtilities.prepareNextCertI1(var1, var42, var19);
                        var22 = RFC3280CertPathUtilities.prepareNextCertI2(var1, var42, var22);
                        var21 = RFC3280CertPathUtilities.prepareNextCertJ(var1, var42, var21);
                        RFC3280CertPathUtilities.prepareNextCertK(var1, var42);
                        var29 = RFC3280CertPathUtilities.prepareNextCertL(var1, var42, var29);
                        var29 = RFC3280CertPathUtilities.prepareNextCertM(var1, var42, var29);
                        RFC3280CertPathUtilities.prepareNextCertN(var1, var42);
                        Set var32 = var30.getCriticalExtensionOIDs();
                        HashSet var58;
                        if (var32 != null) {
                           var58 = new HashSet(var32);
                           var58.remove(RFC3280CertPathUtilities.KEY_USAGE);
                           var58.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                           var58.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                           var58.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                           var58.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                           var58.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                           var58.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                           var58.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                           var58.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                           var58.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
                        } else {
                           var58 = new HashSet();
                        }

                        RFC3280CertPathUtilities.prepareNextCertO(var1, var42, var58, var11);
                        var25 = var30;
                        var24 = PrincipalUtils.getSubjectPrincipal(var30);

                        try {
                           var23 = CertPathValidatorUtilities.getNextWorkingKey(var1.getCertificates(), var42, this.helper);
                        } catch (CertPathValidatorException var35) {
                           throw new CertPathValidatorException("Next working key could not be retrieved.", var35, var1, var42);
                        }

                        var26 = CertPathValidatorUtilities.getAlgorithmIdentifier(var23);
                        var27 = var26.getAlgorithm();
                        var28 = var26.getParameters();
                     }
                  }
               }

               var19 = RFC3280CertPathUtilities.wrapupCertA(var19, var30);
               var19 = RFC3280CertPathUtilities.wrapupCertB(var1, var42 + 1, var19);
               Set var56 = var30.getCriticalExtensionOIDs();
               HashSet var57;
               if (var56 != null) {
                  var57 = new HashSet(var56);
                  var57.remove(RFC3280CertPathUtilities.KEY_USAGE);
                  var57.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                  var57.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                  var57.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                  var57.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                  var57.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                  var57.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                  var57.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                  var57.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                  var57.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
                  var57.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
                  var57.remove(Extension.extendedKeyUsage.getId());
               } else {
                  var57 = new HashSet();
               }

               RFC3280CertPathUtilities.wrapupCertF(var1, var42 + 1, var11, var57);
               PKIXPolicyNode var59 = RFC3280CertPathUtilities.wrapupCertG(var1, var3, var8, var42 + 1, var15, var17, var20);
               if (var19 <= 0 && var59 == null) {
                  throw new CertPathValidatorException("Path processing failed on policy.", null, var1, var42);
               } else {
                  return new PKIXCertPathValidatorResult(var9, var59, var30.getPublicKey());
               }
            }
         }
      }
   }

   static void checkCertificate(X509Certificate var0) throws AnnotatedException {
      if (var0 instanceof BCX509Certificate) {
         RuntimeException var1 = null;

         try {
            if (null != ((BCX509Certificate)var0).getTBSCertificateNative()) {
               return;
            }
         } catch (RuntimeException var3) {
            var1 = var3;
         }

         throw new AnnotatedException("unable to process TBSCertificate", var1);
      } else {
         try {
            TBSCertificate.getInstance(var0.getTBSCertificate());
         } catch (CertificateEncodingException var4) {
            throw new AnnotatedException("unable to process TBSCertificate", var4);
         } catch (IllegalArgumentException var5) {
            throw new AnnotatedException(var5.getMessage());
         }
      }
   }
}
