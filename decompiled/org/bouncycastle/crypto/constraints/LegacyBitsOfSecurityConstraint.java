package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;

public class LegacyBitsOfSecurityConstraint extends ServicesConstraint {
   private final int requiredBitsOfSecurity;
   private final int legacyRequiredBitsOfSecurity;

   public LegacyBitsOfSecurityConstraint(int var1) {
      this(var1, 0);
   }

   public LegacyBitsOfSecurityConstraint(int var1, int var2) {
      super(Collections.EMPTY_SET);
      this.requiredBitsOfSecurity = var1;
      this.legacyRequiredBitsOfSecurity = var2;
   }

   public LegacyBitsOfSecurityConstraint(int var1, Set<String> var2) {
      this(var1, 0, var2);
   }

   public LegacyBitsOfSecurityConstraint(int var1, int var2, Set<String> var3) {
      super(var3);
      this.requiredBitsOfSecurity = var1;
      this.legacyRequiredBitsOfSecurity = var2;
   }

   @Override
   public void check(CryptoServiceProperties var1) {
      if (!this.isException(var1.getServiceName())) {
         CryptoServicePurpose var2 = var1.getPurpose();
         switch (var2) {
            case ANY:
            case VERIFYING:
            case DECRYPTION:
            case VERIFICATION:
               if (var1.bitsOfSecurity() < this.legacyRequiredBitsOfSecurity) {
                  throw new CryptoServiceConstraintsException(
                     "service does not provide " + this.legacyRequiredBitsOfSecurity + " bits of security only " + var1.bitsOfSecurity()
                  );
               }

               if (var2 != CryptoServicePurpose.ANY && LOG.isLoggable(Level.FINE)) {
                  LOG.fine("usage of legacy cryptography service for algorithm " + var1.getServiceName());
               }

               return;
            default:
               if (var1.bitsOfSecurity() < this.requiredBitsOfSecurity) {
                  throw new CryptoServiceConstraintsException(
                     "service does not provide " + this.requiredBitsOfSecurity + " bits of security only " + var1.bitsOfSecurity()
                  );
               }
         }
      }
   }
}
