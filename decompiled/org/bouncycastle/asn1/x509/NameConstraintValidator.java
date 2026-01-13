package org.bouncycastle.asn1.x509;

public interface NameConstraintValidator {
   void checkPermitted(GeneralName var1) throws NameConstraintValidatorException;

   void checkExcluded(GeneralName var1) throws NameConstraintValidatorException;

   void intersectPermittedSubtree(GeneralSubtree var1);

   void intersectPermittedSubtree(GeneralSubtree[] var1);

   void intersectEmptyPermittedSubtree(int var1);

   void addExcludedSubtree(GeneralSubtree var1);
}
