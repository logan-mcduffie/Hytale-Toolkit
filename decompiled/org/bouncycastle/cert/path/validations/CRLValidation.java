package org.bouncycastle.cert.path.validations;

import java.util.Collection;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class CRLValidation implements CertPathValidation {
   private Store crls;
   private X500Name workingIssuerName;

   public CRLValidation(X500Name var1, Store var2) {
      this.workingIssuerName = var1;
      this.crls = var2;
   }

   @Override
   public void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException {
      Collection var3 = this.crls.getMatches(new Selector() {
         @Override
         public boolean match(Object var1) {
            X509CRLHolder var2x = (X509CRLHolder)var1;
            return var2x.getIssuer().equals(CRLValidation.this.workingIssuerName);
         }

         @Override
         public Object clone() {
            return this;
         }
      });
      if (var3.isEmpty()) {
         throw new CertPathValidationException("CRL for " + this.workingIssuerName + " not found");
      } else {
         for (X509CRLHolder var5 : var3) {
            if (var5.getRevokedCertificate(var2.getSerialNumber()) != null) {
               throw new CertPathValidationException("Certificate revoked");
            }
         }

         this.workingIssuerName = var2.getSubject();
      }
   }

   @Override
   public Memoable copy() {
      return new CRLValidation(this.workingIssuerName, this.crls);
   }

   @Override
   public void reset(Memoable var1) {
      CRLValidation var2 = (CRLValidation)var1;
      this.workingIssuerName = var2.workingIssuerName;
      this.crls = var2.crls;
   }
}
