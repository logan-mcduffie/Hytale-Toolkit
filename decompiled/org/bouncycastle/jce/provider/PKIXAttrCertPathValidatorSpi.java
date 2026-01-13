package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;

public class PKIXAttrCertPathValidatorSpi extends CertPathValidatorSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();

   @Override
   public CertPathValidatorResult engineValidate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException {
      if (!(var2 instanceof ExtendedPKIXParameters) && !(var2 instanceof PKIXExtendedParameters)) {
         throw new InvalidAlgorithmParameterException("Parameters must be a " + ExtendedPKIXParameters.class.getName() + " instance.");
      } else {
         Object var3 = new HashSet();
         Object var4 = new HashSet();
         Object var5 = new HashSet();
         HashSet var6 = new HashSet();
         PKIXExtendedParameters var7;
         if (var2 instanceof PKIXParameters) {
            PKIXExtendedParameters.Builder var8 = new PKIXExtendedParameters.Builder((PKIXParameters)var2);
            if (var2 instanceof ExtendedPKIXParameters) {
               ExtendedPKIXParameters var9 = (ExtendedPKIXParameters)var2;
               var8.setUseDeltasEnabled(var9.isUseDeltasEnabled());
               var8.setValidityModel(var9.getValidityModel());
               var3 = var9.getAttrCertCheckers();
               var4 = var9.getProhibitedACAttributes();
               var5 = var9.getNecessaryACAttributes();
            }

            var7 = var8.build();
         } else {
            var7 = (PKIXExtendedParameters)var2;
         }

         Date var15 = new Date();
         Date var16 = CertPathValidatorUtilities.getValidityDate(var7, var15);
         PKIXCertStoreSelector var10 = var7.getTargetConstraints();
         if (!(var10 instanceof X509AttributeCertStoreSelector)) {
            throw new InvalidAlgorithmParameterException(
               "TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + this.getClass().getName() + " class."
            );
         } else {
            X509AttributeCertificate var11 = ((X509AttributeCertStoreSelector)var10).getAttributeCert();
            CertPath var12 = RFC3281CertPathUtilities.processAttrCert1(var11, var7);
            CertPathValidatorResult var13 = RFC3281CertPathUtilities.processAttrCert2(var1, var7);
            X509Certificate var14 = (X509Certificate)var1.getCertificates().get(0);
            RFC3281CertPathUtilities.processAttrCert3(var14, var7);
            RFC3281CertPathUtilities.processAttrCert4(var14, var6);
            RFC3281CertPathUtilities.processAttrCert5(var11, var16);
            RFC3281CertPathUtilities.processAttrCert7(var11, var1, var12, var7, (Set)var3);
            RFC3281CertPathUtilities.additionalChecks(var11, (Set)var4, (Set)var5);
            RFC3281CertPathUtilities.checkCRLs(var11, var7, var15, var16, var14, var1.getCertificates(), this.helper);
            return var13;
         }
      }
   }
}
