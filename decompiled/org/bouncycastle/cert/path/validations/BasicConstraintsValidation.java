package org.bouncycastle.cert.path.validations;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

public class BasicConstraintsValidation implements CertPathValidation {
   private boolean previousCertWasCA = true;
   private Integer maxPathLength = null;
   private boolean isMandatory = true;

   public BasicConstraintsValidation() {
      this(true);
   }

   public BasicConstraintsValidation(boolean var1) {
      this.isMandatory = var1;
   }

   @Override
   public void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException {
      var1.addHandledExtension(Extension.basicConstraints);
      if (!this.previousCertWasCA) {
         throw new CertPathValidationException("Basic constraints violated: issuer is not a CA");
      } else {
         BasicConstraints var3 = BasicConstraints.fromExtensions(var2.getExtensions());
         this.previousCertWasCA = var3 != null && var3.isCA() || var3 == null && !this.isMandatory;
         if (this.maxPathLength != null && !var2.getSubject().equals(var2.getIssuer())) {
            if (this.maxPathLength < 0) {
               throw new CertPathValidationException("Basic constraints violated: path length exceeded");
            }

            this.maxPathLength = Integers.valueOf(this.maxPathLength - 1);
         }

         if (var3 != null && var3.isCA()) {
            ASN1Integer var4 = var3.getPathLenConstraintInteger();
            if (var4 != null) {
               int var5 = var4.intPositiveValueExact();
               if (this.maxPathLength == null || var5 < this.maxPathLength) {
                  this.maxPathLength = Integers.valueOf(var5);
               }
            }
         }
      }
   }

   @Override
   public Memoable copy() {
      BasicConstraintsValidation var1 = new BasicConstraintsValidation();
      var1.isMandatory = this.isMandatory;
      var1.previousCertWasCA = this.previousCertWasCA;
      var1.maxPathLength = this.maxPathLength;
      return var1;
   }

   @Override
   public void reset(Memoable var1) {
      BasicConstraintsValidation var2 = (BasicConstraintsValidation)var1;
      this.isMandatory = var2.isMandatory;
      this.previousCertWasCA = var2.previousCertWasCA;
      this.maxPathLength = var2.maxPathLength;
   }
}
