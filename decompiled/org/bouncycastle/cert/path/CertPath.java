package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;

public class CertPath {
   private final X509CertificateHolder[] certificates;

   public CertPath(X509CertificateHolder[] var1) {
      this.certificates = this.copyArray(var1);
   }

   public X509CertificateHolder[] getCertificates() {
      return this.copyArray(this.certificates);
   }

   public CertPathValidationResult validate(CertPathValidation[] var1) {
      CertPathValidationContext var2 = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));

      for (int var3 = 0; var3 != var1.length; var3++) {
         for (int var4 = this.certificates.length - 1; var4 >= 0; var4--) {
            try {
               var2.setIsEndEntity(var4 == 0);
               var1[var3].validate(var2, this.certificates[var4]);
            } catch (CertPathValidationException var6) {
               return new CertPathValidationResult(var2, var4, var3, var6);
            }
         }
      }

      return new CertPathValidationResult(var2);
   }

   public CertPathValidationResult evaluate(CertPathValidation[] var1) {
      CertPathValidationContext var2 = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
      CertPathValidationResultBuilder var3 = new CertPathValidationResultBuilder(var2);

      for (int var4 = 0; var4 != var1.length; var4++) {
         for (int var5 = this.certificates.length - 1; var5 >= 0; var5--) {
            try {
               var2.setIsEndEntity(var5 == 0);
               var1[var4].validate(var2, this.certificates[var5]);
            } catch (CertPathValidationException var7) {
               var3.addException(var5, var4, var7);
            }
         }
      }

      return var3.build();
   }

   private X509CertificateHolder[] copyArray(X509CertificateHolder[] var1) {
      X509CertificateHolder[] var2 = new X509CertificateHolder[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   public int length() {
      return this.certificates.length;
   }
}
