package org.bouncycastle.cert.dane;

import org.bouncycastle.util.Selector;

public class DANEEntrySelector implements Selector {
   private final String domainName;

   DANEEntrySelector(String var1) {
      this.domainName = var1;
   }

   @Override
   public boolean match(Object var1) {
      DANEEntry var2 = (DANEEntry)var1;
      return var2.getDomainName().equals(this.domainName);
   }

   @Override
   public Object clone() {
      return this;
   }

   public String getDomainName() {
      return this.domainName;
   }
}
