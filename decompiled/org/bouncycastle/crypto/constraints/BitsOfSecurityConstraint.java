package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;

public class BitsOfSecurityConstraint extends ServicesConstraint {
   private final int requiredBitsOfSecurity;

   public BitsOfSecurityConstraint(int var1) {
      super(Collections.EMPTY_SET);
      this.requiredBitsOfSecurity = var1;
   }

   public BitsOfSecurityConstraint(int var1, Set<String> var2) {
      super(var2);
      this.requiredBitsOfSecurity = var1;
   }

   @Override
   public void check(CryptoServiceProperties var1) {
      if (!this.isException(var1.getServiceName())) {
         if (var1.bitsOfSecurity() < this.requiredBitsOfSecurity) {
            throw new CryptoServiceConstraintsException(
               "service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + var1.bitsOfSecurity()
            );
         }
      }
   }
}
