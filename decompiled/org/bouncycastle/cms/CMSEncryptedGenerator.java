package org.bouncycastle.cms;

public class CMSEncryptedGenerator {
   protected CMSAttributeTableGenerator unprotectedAttributeGenerator = null;

   protected CMSEncryptedGenerator() {
   }

   public void setUnprotectedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.unprotectedAttributeGenerator = var1;
   }
}
