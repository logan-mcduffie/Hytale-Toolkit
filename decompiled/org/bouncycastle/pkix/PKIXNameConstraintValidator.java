package org.bouncycastle.pkix;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraintValidatorException;

public class PKIXNameConstraintValidator {
   ASN1PKIXNameConstraintValidator validator = new ASN1PKIXNameConstraintValidator();

   @Override
   public int hashCode() {
      return this.validator.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PKIXNameConstraintValidator)) {
         return false;
      } else {
         PKIXNameConstraintValidator var2 = (PKIXNameConstraintValidator)var1;
         return this.validator.equals(var2.validator);
      }
   }

   public void checkPermittedDN(ASN1Sequence var1) throws PKIXNameConstraintValidatorException {
      try {
         this.validator.checkPermittedDN(X500Name.getInstance(var1));
      } catch (NameConstraintValidatorException var3) {
         throw new PKIXNameConstraintValidatorException(var3.getMessage(), var3);
      }
   }

   public void checkExcludedDN(ASN1Sequence var1) throws PKIXNameConstraintValidatorException {
      try {
         this.validator.checkExcludedDN(X500Name.getInstance(var1));
      } catch (NameConstraintValidatorException var3) {
         throw new PKIXNameConstraintValidatorException(var3.getMessage(), var3);
      }
   }

   public void checkPermitted(GeneralName var1) throws PKIXNameConstraintValidatorException {
      try {
         this.validator.checkPermitted(var1);
      } catch (NameConstraintValidatorException var3) {
         throw new PKIXNameConstraintValidatorException(var3.getMessage(), var3);
      }
   }

   public void checkExcluded(GeneralName var1) throws PKIXNameConstraintValidatorException {
      try {
         this.validator.checkExcluded(var1);
      } catch (NameConstraintValidatorException var3) {
         throw new PKIXNameConstraintValidatorException(var3.getMessage(), var3);
      }
   }

   public void intersectPermittedSubtree(GeneralSubtree var1) {
      this.validator.intersectPermittedSubtree(var1);
   }

   public void intersectPermittedSubtree(GeneralSubtree[] var1) {
      this.validator.intersectPermittedSubtree(var1);
   }

   public void intersectEmptyPermittedSubtree(int var1) {
      this.validator.intersectEmptyPermittedSubtree(var1);
   }

   public void addExcludedSubtree(GeneralSubtree var1) {
      this.validator.addExcludedSubtree(var1);
   }

   @Override
   public String toString() {
      return this.validator.toString();
   }
}
