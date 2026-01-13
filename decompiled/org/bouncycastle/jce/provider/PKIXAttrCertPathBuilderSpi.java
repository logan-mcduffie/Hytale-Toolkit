package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CertStoreSelector;

public class PKIXAttrCertPathBuilderSpi extends CertPathBuilderSpi {
   private Exception certPathException;

   @Override
   public CertPathBuilderResult engineBuild(CertPathParameters var1) throws CertPathBuilderException, InvalidAlgorithmParameterException {
      if (!(var1 instanceof PKIXBuilderParameters) && !(var1 instanceof ExtendedPKIXBuilderParameters) && !(var1 instanceof PKIXExtendedBuilderParameters)) {
         throw new InvalidAlgorithmParameterException(
            "Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + "."
         );
      } else {
         Object var2 = new ArrayList();
         PKIXExtendedBuilderParameters var3;
         if (var1 instanceof PKIXBuilderParameters) {
            PKIXExtendedBuilderParameters.Builder var4 = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)var1);
            if (var1 instanceof ExtendedPKIXParameters) {
               ExtendedPKIXBuilderParameters var5 = (ExtendedPKIXBuilderParameters)var1;
               var4.addExcludedCerts(var5.getExcludedCerts());
               var4.setMaxPathLength(var5.getMaxPathLength());
               var2 = var5.getStores();
            }

            var3 = var4.build();
         } else {
            var3 = (PKIXExtendedBuilderParameters)var1;
         }

         ArrayList var6 = new ArrayList();
         PKIXExtendedParameters var8 = var3.getBaseParameters();
         PKIXCertStoreSelector var9 = var8.getTargetConstraints();
         if (!(var9 instanceof X509AttributeCertStoreSelector)) {
            throw new CertPathBuilderException(
               "TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + this.getClass().getName() + " class."
            );
         } else {
            Collection var19;
            try {
               var19 = findCertificates((X509AttributeCertStoreSelector)var9, (List)var2);
            } catch (AnnotatedException var16) {
               throw new ExtCertPathBuilderException("Error finding target attribute certificate.", var16);
            }

            if (var19.isEmpty()) {
               throw new CertPathBuilderException("No attribute certificate found matching targetConstraints.");
            } else {
               CertPathBuilderResult var10 = null;
               Iterator var20 = var19.iterator();

               while (var20.hasNext() && var10 == null) {
                  X509AttributeCertificate var7 = (X509AttributeCertificate)var20.next();
                  X509CertStoreSelector var11 = new X509CertStoreSelector();
                  Principal[] var12 = var7.getIssuer().getPrincipals();
                  LinkedHashSet var13 = new LinkedHashSet();

                  for (int var14 = 0; var14 < var12.length; var14++) {
                     try {
                        if (var12[var14] instanceof X500Principal) {
                           var11.setSubject(((X500Principal)var12[var14]).getEncoded());
                        }

                        PKIXCertStoreSelector var15 = new PKIXCertStoreSelector.Builder(var11).build();
                        CertPathValidatorUtilities.findCertificates(var13, var15, var8.getCertStores());
                        CertPathValidatorUtilities.findCertificates(var13, var15, var8.getCertificateStores());
                     } catch (AnnotatedException var17) {
                        throw new ExtCertPathBuilderException("Public key certificate for attribute certificate cannot be searched.", var17);
                     } catch (IOException var18) {
                        throw new ExtCertPathBuilderException("cannot encode X500Principal.", var18);
                     }
                  }

                  if (var13.isEmpty()) {
                     throw new CertPathBuilderException("Public key certificate for attribute certificate cannot be found.");
                  }

                  Iterator var21 = var13.iterator();

                  while (var21.hasNext() && var10 == null) {
                     var10 = this.build(var7, (X509Certificate)var21.next(), var3, var6);
                  }
               }

               if (var10 == null && this.certPathException != null) {
                  throw new ExtCertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
               } else if (var10 == null && this.certPathException == null) {
                  throw new CertPathBuilderException("Unable to find certificate chain.");
               } else {
                  return var10;
               }
            }
         }
      }
   }

   private CertPathBuilderResult build(X509AttributeCertificate var1, X509Certificate var2, PKIXExtendedBuilderParameters var3, List var4) {
      if (var4.contains(var2)) {
         return null;
      } else if (var3.getExcludedCerts().contains(var2)) {
         return null;
      } else if (var3.getMaxPathLength() != -1 && var4.size() - 1 > var3.getMaxPathLength()) {
         return null;
      } else {
         var4.add(var2);
         CertPathBuilderResult var7 = null;

         CertificateFactory var5;
         CertPathValidator var6;
         try {
            var5 = CertificateFactory.getInstance("X.509", "BC");
            var6 = CertPathValidator.getInstance("RFC3281", "BC");
         } catch (Exception var17) {
            throw new RuntimeException("Exception creating support classes.");
         }

         try {
            PKIXExtendedParameters var8 = var3.getBaseParameters();
            if (CertPathValidatorUtilities.isIssuerTrustAnchor(var2, var8.getTrustAnchors(), var8.getSigProvider())) {
               CertPath var19;
               try {
                  var19 = var5.generateCertPath(var4);
               } catch (Exception var14) {
                  throw new AnnotatedException("Certification path could not be constructed from certificate list.", var14);
               }

               PKIXCertPathValidatorResult var20;
               try {
                  var20 = (PKIXCertPathValidatorResult)var6.validate(var19, var3);
               } catch (Exception var13) {
                  throw new AnnotatedException("Certification path could not be validated.", var13);
               }

               return new PKIXCertPathBuilderResult(var19, var20.getTrustAnchor(), var20.getPolicyTree(), var20.getPublicKey());
            }

            ArrayList var9 = new ArrayList();
            var9.addAll(var8.getCertificateStores());

            try {
               var9.addAll(
                  CertPathValidatorUtilities.getAdditionalStoresFromAltNames(
                     var2.getExtensionValue(Extension.issuerAlternativeName.getId()), var8.getNamedCertificateStoreMap()
                  )
               );
            } catch (CertificateParsingException var16) {
               throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", var16);
            }

            HashSet var10 = new HashSet();

            try {
               var10.addAll(CertPathValidatorUtilities.findIssuerCerts(var2, var8.getCertStores(), var9));
            } catch (AnnotatedException var15) {
               throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", var15);
            }

            if (var10.isEmpty()) {
               throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
            }

            Iterator var11 = var10.iterator();

            while (var11.hasNext() && var7 == null) {
               X509Certificate var12 = (X509Certificate)var11.next();
               if (!var12.getIssuerX500Principal().equals(var12.getSubjectX500Principal())) {
                  var7 = this.build(var1, var12, var3, var4);
               }
            }
         } catch (AnnotatedException var18) {
            this.certPathException = new AnnotatedException("No valid certification path could be build.", var18);
         }

         if (var7 == null) {
            var4.remove(var2);
         }

         return var7;
      }
   }

   protected static Collection findCertificates(X509AttributeCertStoreSelector var0, List var1) throws AnnotatedException {
      HashSet var2 = new HashSet();

      for (Object var4 : var1) {
         if (var4 instanceof Store) {
            Store var5 = (Store)var4;

            try {
               var2.addAll(var5.getMatches(var0));
            } catch (StoreException var7) {
               throw new AnnotatedException("Problem while picking certificates from X.509 store.", var7);
            }
         }
      }

      return var2;
   }
}
