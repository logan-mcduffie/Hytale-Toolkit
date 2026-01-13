package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.security.cert.X509Extension;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

class RevocationUtilities {
   protected static final String ISSUING_DISTRIBUTION_POINT = Extension.issuingDistributionPoint.getId();

   protected static Date getValidityDate(PKIXExtendedParameters var0, Date var1) {
      Date var2 = var0.getValidityDate();
      return null == var2 ? var1 : var2;
   }

   protected static ASN1Primitive getExtensionValue(X509Extension var0, ASN1ObjectIdentifier var1) throws AnnotatedException {
      byte[] var2 = var0.getExtensionValue(var1.getId());
      return null == var2 ? null : getObject(var1, var2);
   }

   private static ASN1Primitive getObject(ASN1ObjectIdentifier var0, byte[] var1) throws AnnotatedException {
      try {
         return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(var1).getOctets());
      } catch (Exception var3) {
         throw new AnnotatedException("exception processing extension " + var0, var3);
      }
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

   static List<PKIXCRLStore> getAdditionalStoresFromCRLDistributionPoint(CRLDistPoint var0, Map<GeneralName, PKIXCRLStore> var1) throws AnnotatedException {
      if (var0 == null) {
         return Collections.emptyList();
      } else {
         DistributionPoint[] var2;
         try {
            var2 = var0.getDistributionPoints();
         } catch (Exception var15) {
            throw new AnnotatedException("Distribution points could not be read.", var15);
         }

         ArrayList var3 = new ArrayList();

         for (DistributionPoint var7 : var2) {
            DistributionPointName var8 = var7.getDistributionPoint();
            if (var8 != null && var8.getType() == 0) {
               GeneralName[] var9 = GeneralNames.getInstance(var8.getName()).getNames();

               for (GeneralName var13 : var9) {
                  PKIXCRLStore var14 = (PKIXCRLStore)var1.get(var13);
                  if (var14 != null) {
                     var3.add(var14);
                  }
               }
            }
         }

         return var3;
      }
   }

   protected static void getCRLIssuersFromDistributionPoint(DistributionPoint var0, Collection var1, X509CRLSelector var2) throws AnnotatedException {
      ArrayList var3 = new ArrayList();
      if (var0.getCRLIssuer() != null) {
         GeneralName[] var4 = var0.getCRLIssuer().getNames();

         for (int var5 = 0; var5 < var4.length; var5++) {
            if (var4[var5].getTagNo() == 4) {
               try {
                  var3.add(X500Name.getInstance(var4[var5].getName()));
               } catch (IllegalArgumentException var8) {
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

   protected static void getCertStatus(Date var0, X509CRL var1, Object var2, CertStatus var3) throws AnnotatedException {
      boolean var4;
      try {
         var4 = isIndirectCRL(var1);
      } catch (CRLException var12) {
         throw new AnnotatedException("Failed check for indirect CRL.", var12);
      }

      X509Certificate var5 = (X509Certificate)var2;
      X500Name var6 = getIssuer(var5);
      if (!var4) {
         X500Name var7 = getIssuer(var1);
         if (!var6.equals(var7)) {
            return;
         }
      }

      X509CRLEntry var13 = var1.getRevokedCertificate(var5.getSerialNumber());
      if (null != var13) {
         if (var4) {
            X500Principal var8 = var13.getCertificateIssuer();
            X500Name var9;
            if (null == var8) {
               var9 = getIssuer(var1);
            } else {
               var9 = getX500Name(var8);
            }

            if (!var6.equals(var9)) {
               return;
            }
         }

         int var14 = 0;
         if (var13.hasExtensions()) {
            try {
               ASN1Primitive var15 = getExtensionValue(var13, Extension.reasonCode);
               ASN1Enumerated var10 = ASN1Enumerated.getInstance(var15);
               if (null != var10) {
                  var14 = var10.intValueExact();
               }
            } catch (Exception var11) {
               throw new AnnotatedException("Reason code CRL entry extension could not be decoded.", var11);
            }
         }

         Date var16 = var13.getRevocationDate();
         if (var0.before(var16)) {
            switch (var14) {
               case 0:
               case 1:
               case 2:
               case 10:
                  break;
               default:
                  return;
            }
         }

         var3.setCertStatus(var14);
         var3.setRevocationDate(var16);
      }
   }

   protected static Set getDeltaCRLs(Date var0, X509CRL var1, List<CertStore> var2, List<PKIXCRLStore> var3) throws AnnotatedException {
      X509CRLSelector var4 = new X509CRLSelector();

      try {
         var4.addIssuerName(var1.getIssuerX500Principal().getEncoded());
      } catch (IOException var14) {
         throw new AnnotatedException("cannot extract issuer from CRL.", var14);
      }

      BigInteger var5 = null;

      try {
         ASN1Primitive var6 = getExtensionValue(var1, Extension.cRLNumber);
         if (var6 != null) {
            var5 = ASN1Integer.getInstance(var6).getPositiveValue();
         }
      } catch (Exception var15) {
         throw new AnnotatedException("cannot extract CRL number extension from CRL", var15);
      }

      byte[] var16;
      try {
         var16 = var1.getExtensionValue(ISSUING_DISTRIBUTION_POINT);
      } catch (Exception var13) {
         throw new AnnotatedException("issuing distribution point extension value could not be read", var13);
      }

      var4.setMinCRLNumber(var5 == null ? null : var5.add(BigInteger.valueOf(1L)));
      PKIXCRLStoreSelector.Builder var7 = new PKIXCRLStoreSelector.Builder(var4);
      var7.setIssuingDistributionPoint(var16);
      var7.setIssuingDistributionPointEnabled(true);
      var7.setMaxBaseCRLNumber(var5);
      PKIXCRLStoreSelector var8 = var7.build();
      Set var9 = PKIXCRLUtil.findCRLs(var8, var0, var2, var3);
      HashSet var10 = new HashSet();

      for (X509CRL var12 : var9) {
         if (isDeltaCRL(var12)) {
            var10.add(var12);
         }
      }

      return var10;
   }

   private static boolean isDeltaCRL(X509CRL var0) {
      Set var1 = var0.getCriticalExtensionOIDs();
      return null == var1 ? false : var1.contains(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
   }

   protected static Set getCompleteCRLs(DistributionPoint var0, Object var1, Date var2, List var3, List var4) throws AnnotatedException, CRLNotFoundException {
      X509CRLSelector var5 = new X509CRLSelector();

      try {
         HashSet var6 = new HashSet();
         var6.add(getIssuer((X509Certificate)var1));
         getCRLIssuersFromDistributionPoint(var0, var6, var5);
      } catch (AnnotatedException var8) {
         throw new AnnotatedException("Could not get issuer information from distribution point.", var8);
      }

      if (var1 instanceof X509Certificate) {
         var5.setCertificateChecking((X509Certificate)var1);
      }

      PKIXCRLStoreSelector var9 = new PKIXCRLStoreSelector.Builder(var5).setCompleteCRLEnabled(true).build();
      Set var7 = PKIXCRLUtil.findCRLs(var9, var2, var3, var4);
      checkCRLsNotEmpty(var7, var1);
      return var7;
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

   static void checkCRLsNotEmpty(Set var0, Object var1) throws CRLNotFoundException {
      if (var0.isEmpty()) {
         X500Name var2 = getIssuer((X509Certificate)var1);
         throw new CRLNotFoundException("No CRLs found for issuer \"" + RFC4519Style.INSTANCE.toString(var2) + "\"");
      }
   }

   public static boolean isIndirectCRL(X509CRL var0) throws CRLException {
      try {
         byte[] var1 = var0.getExtensionValue(Extension.issuingDistributionPoint.getId());
         return var1 != null && IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(var1).getOctets()).isIndirectCRL();
      } catch (Exception var2) {
         throw new CRLException("exception reading IssuingDistributionPoint", var2);
      }
   }

   private static X500Name getIssuer(X509Certificate var0) {
      return getX500Name(var0.getIssuerX500Principal());
   }

   private static X500Name getIssuer(X509CRL var0) {
      return getX500Name(var0.getIssuerX500Principal());
   }

   private static X500Name getX500Name(X500Principal var0) {
      return X500Name.getInstance(var0.getEncoded());
   }
}
