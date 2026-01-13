package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;

public class DANECertificateFetcher {
   private final DANEEntryFetcherFactory fetcherFactory;
   private final DANEEntrySelectorFactory selectorFactory;

   public DANECertificateFetcher(DANEEntryFetcherFactory var1, DigestCalculator var2) {
      this.fetcherFactory = var1;
      this.selectorFactory = new DANEEntrySelectorFactory(var2);
   }

   public List fetch(String var1) throws DANEException {
      DANEEntrySelector var2 = this.selectorFactory.createSelector(var1);
      List var3 = this.fetcherFactory.build(var2.getDomainName()).getEntries();
      ArrayList var4 = new ArrayList(var3.size());

      for (DANEEntry var6 : var3) {
         if (var2.match(var6)) {
            var4.add(var6.getCertificate());
         }
      }

      return Collections.unmodifiableList(var4);
   }
}
