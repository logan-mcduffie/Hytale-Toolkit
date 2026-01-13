package org.bouncycastle.crypto.constraints;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.bouncycastle.crypto.CryptoServicesConstraints;
import org.bouncycastle.util.Strings;

public abstract class ServicesConstraint implements CryptoServicesConstraints {
   protected static final Logger LOG = Logger.getLogger(ServicesConstraint.class.getName());
   private final Set<String> exceptions;

   protected ServicesConstraint(Set<String> var1) {
      if (var1.isEmpty()) {
         this.exceptions = Collections.EMPTY_SET;
      } else {
         this.exceptions = new HashSet<>(var1.size());
         Iterator var2 = var1.iterator();

         while (var2.hasNext()) {
            this.exceptions.add(Strings.toUpperCase(var2.next().toString()));
         }

         Utils.addAliases(this.exceptions);
      }
   }

   protected boolean isException(String var1) {
      return this.exceptions.isEmpty() ? false : this.exceptions.contains(Strings.toUpperCase(var1));
   }
}
