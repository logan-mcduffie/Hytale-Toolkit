package org.bouncycastle.crypto.constraints;

import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.crypto.CryptoServiceProperties;

public class LoggingConstraint extends ServicesConstraint {
   protected LoggingConstraint(Set<String> var1) {
      super(var1);
   }

   @Override
   public void check(CryptoServiceProperties var1) {
      if (!this.isException(var1.getServiceName())) {
         if (LOG.isLoggable(Level.INFO)) {
            LOG.info("service " + var1.getServiceName() + " referenced [" + var1.getServiceName() + ", " + var1.bitsOfSecurity() + ", " + var1.getPurpose());
         }
      }
   }
}
