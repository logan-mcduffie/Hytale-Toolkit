package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.path.CertPath;

public class CertificatePoliciesValidationBuilder {
   private boolean isExplicitPolicyRequired;
   private boolean isAnyPolicyInhibited;
   private boolean isPolicyMappingInhibited;

   public void setAnyPolicyInhibited(boolean var1) {
      this.isAnyPolicyInhibited = var1;
   }

   public void setExplicitPolicyRequired(boolean var1) {
      this.isExplicitPolicyRequired = var1;
   }

   public void setPolicyMappingInhibited(boolean var1) {
      this.isPolicyMappingInhibited = var1;
   }

   public CertificatePoliciesValidation build(int var1) {
      return new CertificatePoliciesValidation(var1, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
   }

   public CertificatePoliciesValidation build(CertPath var1) {
      return this.build(var1.length());
   }
}
