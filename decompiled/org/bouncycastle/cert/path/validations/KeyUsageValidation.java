package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;

public class KeyUsageValidation implements CertPathValidation {
   private boolean isMandatory;

   public KeyUsageValidation() {
      this(true);
   }

   public KeyUsageValidation(boolean var1) {
      this.isMandatory = var1;
   }

   @Override
   public void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException {
      var1.addHandledExtension(Extension.keyUsage);
      if (!var1.isEndEntity()) {
         KeyUsage var3 = KeyUsage.fromExtensions(var2.getExtensions());
         if (var3 != null) {
            if (!var3.hasUsages(4)) {
               throw new CertPathValidationException("Issuer certificate KeyUsage extension does not permit key signing");
            }
         } else if (this.isMandatory) {
            throw new CertPathValidationException("KeyUsage extension not present in CA certificate");
         }
      }
   }

   @Override
   public Memoable copy() {
      return new KeyUsageValidation(this.isMandatory);
   }

   @Override
   public void reset(Memoable var1) {
      KeyUsageValidation var2 = (KeyUsageValidation)var1;
      this.isMandatory = var2.isMandatory;
   }
}
