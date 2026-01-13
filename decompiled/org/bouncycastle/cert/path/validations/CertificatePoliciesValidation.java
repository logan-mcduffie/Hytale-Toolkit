package org.bouncycastle.cert.path.validations;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.util.Memoable;

public class CertificatePoliciesValidation implements CertPathValidation {
   private int explicitPolicy;
   private int policyMapping;
   private int inhibitAnyPolicy;

   CertificatePoliciesValidation(int var1) {
      this(var1, false, false, false);
   }

   CertificatePoliciesValidation(int var1, boolean var2, boolean var3, boolean var4) {
      if (var2) {
         this.explicitPolicy = 0;
      } else {
         this.explicitPolicy = var1 + 1;
      }

      if (var3) {
         this.inhibitAnyPolicy = 0;
      } else {
         this.inhibitAnyPolicy = var1 + 1;
      }

      if (var4) {
         this.policyMapping = 0;
      } else {
         this.policyMapping = var1 + 1;
      }
   }

   @Override
   public void validate(CertPathValidationContext var1, X509CertificateHolder var2) throws CertPathValidationException {
      var1.addHandledExtension(Extension.policyConstraints);
      var1.addHandledExtension(Extension.inhibitAnyPolicy);
      if (!var1.isEndEntity() && !ValidationUtils.isSelfIssued(var2)) {
         this.explicitPolicy = this.countDown(this.explicitPolicy);
         this.policyMapping = this.countDown(this.policyMapping);
         this.inhibitAnyPolicy = this.countDown(this.inhibitAnyPolicy);
         PolicyConstraints var3 = PolicyConstraints.fromExtensions(var2.getExtensions());
         if (var3 != null) {
            BigInteger var4 = var3.getRequireExplicitPolicyMapping();
            if (var4 != null && var4.intValue() < this.explicitPolicy) {
               this.explicitPolicy = var4.intValue();
            }

            BigInteger var5 = var3.getInhibitPolicyMapping();
            if (var5 != null && var5.intValue() < this.policyMapping) {
               this.policyMapping = var5.intValue();
            }
         }

         Extension var6 = var2.getExtension(Extension.inhibitAnyPolicy);
         if (var6 != null) {
            int var7 = ASN1Integer.getInstance(var6.getParsedValue()).intValueExact();
            if (var7 < this.inhibitAnyPolicy) {
               this.inhibitAnyPolicy = var7;
            }
         }
      }
   }

   private int countDown(int var1) {
      return var1 != 0 ? var1 - 1 : 0;
   }

   @Override
   public Memoable copy() {
      CertificatePoliciesValidation var1 = new CertificatePoliciesValidation(0);
      var1.explicitPolicy = this.explicitPolicy;
      var1.policyMapping = this.policyMapping;
      var1.inhibitAnyPolicy = this.inhibitAnyPolicy;
      return var1;
   }

   @Override
   public void reset(Memoable var1) {
      CertificatePoliciesValidation var2 = (CertificatePoliciesValidation)var1;
      this.explicitPolicy = var2.explicitPolicy;
      this.policyMapping = var2.policyMapping;
      this.inhibitAnyPolicy = var2.inhibitAnyPolicy;
   }
}
