package org.bouncycastle.jce.provider;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class ProvCrlRevocationChecker implements PKIXCertRevocationChecker {
   private final JcaJceHelper helper;
   private PKIXCertRevocationCheckerParameters params;
   private Date currentDate = null;

   public ProvCrlRevocationChecker(JcaJceHelper var1) {
      this.helper = var1;
   }

   @Override
   public void setParameter(String var1, Object var2) {
   }

   @Override
   public void initialize(PKIXCertRevocationCheckerParameters var1) {
      this.params = var1;
      this.currentDate = new Date();
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (var1) {
         throw new CertPathValidatorException("forward checking not supported");
      } else {
         this.params = null;
         this.currentDate = new Date();
      }
   }

   @Override
   public void check(Certificate var1) throws CertPathValidatorException {
      try {
         RFC3280CertPathUtilities.checkCRLs(
            this.params,
            this.params.getParamsPKIX(),
            this.currentDate,
            this.params.getValidDate(),
            (X509Certificate)var1,
            this.params.getSigningCert(),
            this.params.getWorkingPublicKey(),
            this.params.getCertPath().getCertificates(),
            this.helper
         );
      } catch (AnnotatedException var4) {
         Object var3 = var4;
         if (null != var4.getCause()) {
            var3 = var4.getCause();
         }

         throw new CertPathValidatorException(var4.getMessage(), (Throwable)var3, this.params.getCertPath(), this.params.getIndex());
      }
   }
}
