package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

class RFC3280CertPathUtilities {
   public static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();
   public static final String FRESHEST_CRL = Extension.freshestCRL.getId();
   public static final String DELTA_CRL_INDICATOR = Extension.deltaCRLIndicator.getId();
   public static final String BASIC_CONSTRAINTS = Extension.basicConstraints.getId();
   public static final String AUTHORITY_KEY_IDENTIFIER = Extension.authorityKeyIdentifier.getId();
   protected static final int KEY_CERT_SIGN = 5;
   protected static final int CRL_SIGN = 6;

   protected static void processCRLB2(DistributionPoint var0, Object var1, X509CRL var2) throws AnnotatedException {
      Object var3 = null;

      try {
         var3 = IssuingDistributionPoint.getInstance(RevocationUtilities.getExtensionValue(var2, Extension.issuingDistributionPoint));
      } catch (Exception var13) {
         throw new AnnotatedException("Issuing distribution point extension could not be decoded.", var13);
      }

      if (var3 != null) {
         if (((IssuingDistributionPoint)var3).getDistributionPoint() != null) {
            DistributionPointName var4 = IssuingDistributionPoint.getInstance(var3).getDistributionPoint();
            ArrayList var5 = new ArrayList();
            if (var4.getType() == 0) {
               GeneralName[] var6 = GeneralNames.getInstance(var4.getName()).getNames();

               for (int var7 = 0; var7 < var6.length; var7++) {
                  var5.add(var6[var7]);
               }
            }

            if (var4.getType() == 1) {
               ASN1EncodableVector var19 = new ASN1EncodableVector();

               try {
                  Enumeration var21 = ASN1Sequence.getInstance(var2.getIssuerX500Principal().getEncoded()).getObjects();

                  while (var21.hasMoreElements()) {
                     var19.add((ASN1Encodable)var21.nextElement());
                  }
               } catch (Exception var14) {
                  throw new AnnotatedException("Could not read CRL issuer.", var14);
               }

               var19.add(var4.getName());
               var5.add(new GeneralName(X500Name.getInstance(new DERSequence(var19))));
            }

            boolean var20 = false;
            if (var0.getDistributionPoint() != null) {
               var4 = var0.getDistributionPoint();
               GeneralName[] var23 = null;
               if (var4.getType() == 0) {
                  var23 = GeneralNames.getInstance(var4.getName()).getNames();
               }

               if (var4.getType() == 1) {
                  if (var0.getCRLIssuer() != null) {
                     var23 = var0.getCRLIssuer().getNames();
                  } else {
                     var23 = new GeneralName[1];

                     try {
                        var23[0] = new GeneralName(X500Name.getInstance(((X509Certificate)var1).getIssuerX500Principal().getEncoded()));
                     } catch (Exception var12) {
                        throw new AnnotatedException("Could not read certificate issuer.", var12);
                     }
                  }

                  for (int var24 = 0; var24 < var23.length; var24++) {
                     Enumeration var9 = ASN1Sequence.getInstance(var23[var24].getName().toASN1Primitive()).getObjects();
                     ASN1EncodableVector var10 = new ASN1EncodableVector();

                     while (var9.hasMoreElements()) {
                        var10.add((ASN1Encodable)var9.nextElement());
                     }

                     var10.add(var4.getName());
                     var23[var24] = new GeneralName(X500Name.getInstance(new DERSequence(var10)));
                  }
               }

               if (var23 != null) {
                  for (int var25 = 0; var25 < var23.length; var25++) {
                     if (var5.contains(var23[var25])) {
                        var20 = true;
                        break;
                     }
                  }
               }

               if (!var20) {
                  throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
               }
            } else {
               if (var0.getCRLIssuer() == null) {
                  throw new AnnotatedException("Either the cRLIssuer or the distributionPoint field must be contained in DistributionPoint.");
               }

               GeneralName[] var22 = var0.getCRLIssuer().getNames();

               for (int var8 = 0; var8 < var22.length; var8++) {
                  if (var5.contains(var22[var8])) {
                     var20 = true;
                     break;
                  }
               }

               if (!var20) {
                  throw new AnnotatedException("No match for certificate CRL issuing distribution point name to cRLIssuer CRL distribution point.");
               }
            }
         }

         Object var17 = null;

         try {
            var17 = BasicConstraints.getInstance(RevocationUtilities.getExtensionValue((X509Extension)var1, Extension.basicConstraints));
         } catch (Exception var11) {
            throw new AnnotatedException("Basic constraints extension could not be decoded.", var11);
         }

         if (var1 instanceof X509Certificate) {
            if (((IssuingDistributionPoint)var3).onlyContainsUserCerts() && var17 != null && ((BasicConstraints)var17).isCA()) {
               throw new AnnotatedException("CA Cert CRL only contains user certificates.");
            }

            if (((IssuingDistributionPoint)var3).onlyContainsCACerts() && (var17 == null || !((BasicConstraints)var17).isCA())) {
               throw new AnnotatedException("End CRL only contains CA certificates.");
            }
         }

         if (((IssuingDistributionPoint)var3).onlyContainsAttributeCerts()) {
            throw new AnnotatedException("onlyContainsAttributeCerts boolean is asserted.");
         }
      }
   }

   protected static void processCRLB1(DistributionPoint var0, Object var1, X509CRL var2) throws AnnotatedException {
      ASN1Primitive var3 = RevocationUtilities.getExtensionValue(var2, Extension.issuingDistributionPoint);
      boolean var4 = false;
      if (var3 != null && IssuingDistributionPoint.getInstance(var3).isIndirectCRL()) {
         var4 = true;
      }

      byte[] var5 = var2.getIssuerX500Principal().getEncoded();
      boolean var6 = false;
      if (var0.getCRLIssuer() != null) {
         GeneralName[] var7 = var0.getCRLIssuer().getNames();

         for (int var8 = 0; var8 < var7.length; var8++) {
            if (var7[var8].getTagNo() == 4) {
               try {
                  if (Arrays.areEqual(var7[var8].getName().toASN1Primitive().getEncoded(), var5)) {
                     var6 = true;
                  }
               } catch (IOException var10) {
                  throw new AnnotatedException("CRL issuer information from distribution point cannot be decoded.", var10);
               }
            }
         }

         if (var6 && !var4) {
            throw new AnnotatedException("Distribution point contains cRLIssuer field but CRL is not indirect.");
         }

         if (!var6) {
            throw new AnnotatedException("CRL issuer of CRL does not match CRL issuer of distribution point.");
         }
      } else if (var2.getIssuerX500Principal().equals(((X509Certificate)var1).getIssuerX500Principal())) {
         var6 = true;
      }

      if (!var6) {
         throw new AnnotatedException("Cannot find matching CRL issuer for certificate.");
      }
   }

   protected static ReasonsMask processCRLD(X509CRL var0, DistributionPoint var1) throws AnnotatedException {
      Object var2 = null;

      try {
         var2 = IssuingDistributionPoint.getInstance(RevocationUtilities.getExtensionValue(var0, Extension.issuingDistributionPoint));
      } catch (Exception var4) {
         throw new AnnotatedException("Issuing distribution point extension could not be decoded.", var4);
      }

      if (var2 != null && ((IssuingDistributionPoint)var2).getOnlySomeReasons() != null && var1.getReasons() != null) {
         return new ReasonsMask(var1.getReasons()).intersect(new ReasonsMask(((IssuingDistributionPoint)var2).getOnlySomeReasons()));
      } else {
         return (var2 == null || ((IssuingDistributionPoint)var2).getOnlySomeReasons() == null) && var1.getReasons() == null
            ? ReasonsMask.allReasons
            : (var1.getReasons() == null ? ReasonsMask.allReasons : new ReasonsMask(var1.getReasons()))
               .intersect(var2 == null ? ReasonsMask.allReasons : new ReasonsMask(((IssuingDistributionPoint)var2).getOnlySomeReasons()));
      }
   }

   protected static Set processCRLF(X509CRL var0, Object var1, X509Certificate var2, PublicKey var3, PKIXExtendedParameters var4, List var5, JcaJceHelper var6) throws AnnotatedException {
      X509CertSelector var7 = new X509CertSelector();

      try {
         byte[] var8 = var0.getIssuerX500Principal().getEncoded();
         var7.setSubject(var8);
      } catch (IOException var23) {
         throw new AnnotatedException("subject criteria for certificate selector to find issuer certificate for CRL could not be set", var23);
      }

      PKIXCertStoreSelector var24 = new PKIXCertStoreSelector.Builder(var7).build();
      LinkedHashSet var9 = new LinkedHashSet();

      try {
         RevocationUtilities.findCertificates(var9, var24, var4.getCertificateStores());
         RevocationUtilities.findCertificates(var9, var24, var4.getCertStores());
      } catch (AnnotatedException var22) {
         throw new AnnotatedException("Issuer certificate for CRL cannot be searched.", var22);
      }

      var9.add(var2);
      ArrayList var10 = new ArrayList();
      ArrayList var11 = new ArrayList();

      for (X509Certificate var13 : var9) {
         if (var13.equals(var2)) {
            var10.add(var13);
            var11.add(var3);
         } else {
            try {
               CertPathBuilder var14 = var6.createCertPathBuilder("PKIX");
               X509CertSelector var15 = new X509CertSelector();
               var15.setCertificate(var13);
               PKIXExtendedParameters.Builder var16 = new PKIXExtendedParameters.Builder(var4)
                  .setTargetConstraints(new PKIXCertStoreSelector.Builder(var15).build());
               if (var5.contains(var13)) {
                  var16.setRevocationEnabled(false);
               } else {
                  var16.setRevocationEnabled(true);
               }

               PKIXExtendedBuilderParameters var17 = new PKIXExtendedBuilderParameters.Builder(var16.build()).build();
               List var18 = var14.build(var17).getCertPath().getCertificates();
               var10.add(var13);
               var11.add(RevocationUtilities.getNextWorkingKey(var18, 0, var6));
            } catch (CertPathBuilderException var19) {
               throw new AnnotatedException("CertPath for CRL signer failed to validate.", var19);
            } catch (CertPathValidatorException var20) {
               throw new AnnotatedException("Public key of issuer certificate of CRL could not be retrieved.", var20);
            } catch (Exception var21) {
               throw new AnnotatedException(var21.getMessage());
            }
         }
      }

      HashSet var25 = new HashSet();
      AnnotatedException var26 = null;

      for (int var27 = 0; var27 < var10.size(); var27++) {
         X509Certificate var28 = (X509Certificate)var10.get(var27);
         boolean[] var29 = var28.getKeyUsage();
         if (var29 == null) {
            if (Properties.isOverrideSet("org.bouncycastle.x509.allow_ca_without_crl_sign", true)) {
               var25.add(var11.get(var27));
            } else {
               var26 = new AnnotatedException("No key usage extension on issuer certificate.");
            }
         } else if (var29.length > 6 && var29[6]) {
            var25.add(var11.get(var27));
         } else {
            var26 = new AnnotatedException("Issuer certificate key usage extension does not permit CRL signing.");
         }
      }

      if (var25.isEmpty() && var26 == null) {
         throw new AnnotatedException("Cannot find a valid issuer certificate.");
      } else if (var25.isEmpty() && var26 != null) {
         throw var26;
      } else {
         return var25;
      }
   }

   protected static PublicKey processCRLG(X509CRL var0, Set var1) throws AnnotatedException {
      Exception var2 = null;

      for (PublicKey var4 : var1) {
         try {
            var0.verify(var4);
            return var4;
         } catch (Exception var6) {
            var2 = var6;
         }
      }

      throw new AnnotatedException("Cannot verify CRL.", var2);
   }

   protected static X509CRL processCRLH(Set var0, PublicKey var1) throws AnnotatedException {
      Exception var2 = null;

      for (X509CRL var4 : var0) {
         try {
            var4.verify(var1);
            return var4;
         } catch (Exception var6) {
            var2 = var6;
         }
      }

      if (var2 != null) {
         throw new AnnotatedException("Cannot verify delta CRL.", var2);
      } else {
         return null;
      }
   }

   protected static Set processCRLA1i(PKIXExtendedParameters var0, Date var1, X509Certificate var2, X509CRL var3) throws AnnotatedException {
      HashSet var4 = new HashSet();
      if (var0.isUseDeltasEnabled()) {
         Object var5 = null;

         try {
            var5 = CRLDistPoint.getInstance(RevocationUtilities.getExtensionValue(var2, Extension.freshestCRL));
         } catch (AnnotatedException var11) {
            throw new AnnotatedException("Freshest CRL extension could not be decoded from certificate.", var11);
         }

         if (var5 == null) {
            try {
               var5 = CRLDistPoint.getInstance(RevocationUtilities.getExtensionValue(var3, Extension.freshestCRL));
            } catch (AnnotatedException var10) {
               throw new AnnotatedException("Freshest CRL extension could not be decoded from CRL.", var10);
            }
         }

         if (var5 != null) {
            ArrayList var6 = new ArrayList();
            var6.addAll(var0.getCRLStores());

            try {
               var6.addAll(RevocationUtilities.getAdditionalStoresFromCRLDistributionPoint((CRLDistPoint)var5, var0.getNamedCRLStoreMap()));
            } catch (AnnotatedException var9) {
               throw new AnnotatedException("No new delta CRL locations could be added from Freshest CRL extension.", var9);
            }

            try {
               var4.addAll(RevocationUtilities.getDeltaCRLs(var1, var3, var0.getCertStores(), var6));
            } catch (AnnotatedException var8) {
               throw new AnnotatedException("Exception obtaining delta CRLs.", var8);
            }
         }
      }

      return var4;
   }

   protected static Set[] processCRLA1ii(PKIXExtendedParameters var0, Date var1, Date var2, X509Certificate var3, X509CRL var4) throws AnnotatedException {
      X509CRLSelector var5 = new X509CRLSelector();
      var5.setCertificateChecking(var3);

      try {
         var5.addIssuerName(var4.getIssuerX500Principal().getEncoded());
      } catch (IOException var11) {
         throw new AnnotatedException("Cannot extract issuer from CRL." + var11, var11);
      }

      PKIXCRLStoreSelector var6 = new PKIXCRLStoreSelector.Builder(var5).setCompleteCRLEnabled(true).build();
      Set var7 = PKIXCRLUtil.findCRLs(var6, var2, var0.getCertStores(), var0.getCRLStores());
      HashSet var8 = new HashSet();
      if (var0.isUseDeltasEnabled()) {
         try {
            var8.addAll(RevocationUtilities.getDeltaCRLs(var2, var4, var0.getCertStores(), var0.getCRLStores()));
         } catch (AnnotatedException var10) {
            throw new AnnotatedException("Exception obtaining delta CRLs.", var10);
         }
      }

      return new Set[]{var7, var8};
   }

   protected static void processCRLC(X509CRL var0, X509CRL var1, PKIXExtendedParameters var2) throws AnnotatedException {
      if (var0 != null) {
         Object var3 = null;

         try {
            var3 = IssuingDistributionPoint.getInstance(RevocationUtilities.getExtensionValue(var1, Extension.issuingDistributionPoint));
         } catch (Exception var12) {
            throw new AnnotatedException("issuing distribution point extension could not be decoded.", var12);
         }

         if (var2.isUseDeltasEnabled()) {
            if (!var0.getIssuerX500Principal().equals(var1.getIssuerX500Principal())) {
               throw new AnnotatedException("complete CRL issuer does not match delta CRL issuer");
            }

            Object var4 = null;

            try {
               var4 = IssuingDistributionPoint.getInstance(RevocationUtilities.getExtensionValue(var0, Extension.issuingDistributionPoint));
            } catch (Exception var11) {
               throw new AnnotatedException("Issuing distribution point extension from delta CRL could not be decoded.", var11);
            }

            boolean var5 = false;
            if (var3 == null) {
               if (var4 == null) {
                  var5 = true;
               }
            } else if (((IssuingDistributionPoint)var3).equals(var4)) {
               var5 = true;
            }

            if (!var5) {
               throw new AnnotatedException("Issuing distribution point extension from delta CRL and complete CRL does not match.");
            }

            Object var6 = null;

            try {
               var6 = RevocationUtilities.getExtensionValue(var1, Extension.authorityKeyIdentifier);
            } catch (AnnotatedException var10) {
               throw new AnnotatedException("Authority key identifier extension could not be extracted from complete CRL.", var10);
            }

            Object var7 = null;

            try {
               var7 = RevocationUtilities.getExtensionValue(var0, Extension.authorityKeyIdentifier);
            } catch (AnnotatedException var9) {
               throw new AnnotatedException("Authority key identifier extension could not be extracted from delta CRL.", var9);
            }

            if (var6 == null) {
               throw new AnnotatedException("CRL authority key identifier is null.");
            }

            if (var7 == null) {
               throw new AnnotatedException("Delta CRL authority key identifier is null.");
            }

            if (!((ASN1Primitive)var6).equals((ASN1Primitive)var7)) {
               throw new AnnotatedException("Delta CRL authority key identifier does not match complete CRL authority key identifier.");
            }
         }
      }
   }

   protected static void processCRLI(Date var0, X509CRL var1, Object var2, CertStatus var3, PKIXExtendedParameters var4) throws AnnotatedException {
      if (var4.isUseDeltasEnabled() && var1 != null) {
         RevocationUtilities.getCertStatus(var0, var1, var2, var3);
      }
   }

   protected static void processCRLJ(Date var0, X509CRL var1, Object var2, CertStatus var3) throws AnnotatedException {
      if (var3.getCertStatus() == 11) {
         RevocationUtilities.getCertStatus(var0, var1, var2, var3);
      }
   }

   static void checkCRL(
      DistributionPoint var0,
      PKIXExtendedParameters var1,
      Date var2,
      Date var3,
      X509Certificate var4,
      X509Certificate var5,
      PublicKey var6,
      CertStatus var7,
      ReasonsMask var8,
      List var9,
      JcaJceHelper var10
   ) throws AnnotatedException, CRLNotFoundException {
      if (var3.getTime() > var2.getTime()) {
         throw new AnnotatedException("Validation time is in future.");
      } else {
         Set var11 = RevocationUtilities.getCompleteCRLs(var0, var4, var3, var1.getCertStores(), var1.getCRLStores());
         boolean var12 = false;
         AnnotatedException var13 = null;
         Iterator var14 = var11.iterator();

         while (var14.hasNext() && var7.getCertStatus() == 11 && !var8.isAllReasons()) {
            try {
               X509CRL var15 = (X509CRL)var14.next();
               ReasonsMask var16 = processCRLD(var15, var0);
               if (var16.hasNewReasons(var8)) {
                  Set var17 = processCRLF(var15, var4, var5, var6, var1, var9, var10);
                  PublicKey var18 = processCRLG(var15, var17);
                  X509CRL var19 = null;
                  if (var1.isUseDeltasEnabled()) {
                     Set var20 = RevocationUtilities.getDeltaCRLs(var3, var15, var1.getCertStores(), var1.getCRLStores());
                     var19 = processCRLH(var20, var18);
                  }

                  if (var1.getValidityModel() != 1 && var4.getNotAfter().getTime() < var15.getThisUpdate().getTime()) {
                     throw new AnnotatedException("No valid CRL for current time found.");
                  }

                  processCRLB1(var0, var4, var15);
                  processCRLB2(var0, var4, var15);
                  processCRLC(var19, var15, var1);
                  processCRLI(var3, var19, var4, var7, var1);
                  processCRLJ(var3, var15, var4, var7);
                  if (var7.getCertStatus() == 8) {
                     var7.setCertStatus(11);
                  }

                  var8.addReasons(var16);
                  Set var22 = var15.getCriticalExtensionOIDs();
                  if (var22 != null) {
                     HashSet var23 = new HashSet(var22);
                     var23.remove(Extension.issuingDistributionPoint.getId());
                     var23.remove(Extension.deltaCRLIndicator.getId());
                     if (!var23.isEmpty()) {
                        throw new AnnotatedException("CRL contains unsupported critical extensions.");
                     }
                  }

                  if (var19 != null) {
                     var22 = var19.getCriticalExtensionOIDs();
                     if (var22 != null) {
                        HashSet var25 = new HashSet(var22);
                        var25.remove(Extension.issuingDistributionPoint.getId());
                        var25.remove(Extension.deltaCRLIndicator.getId());
                        if (!var25.isEmpty()) {
                           throw new AnnotatedException("Delta CRL contains unsupported critical extension.");
                        }
                     }
                  }

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
