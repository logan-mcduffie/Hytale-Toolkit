package org.bouncycastle.cert.dane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class DANEEntryStore implements Store {
   private final Map entries;

   DANEEntryStore(List var1) {
      HashMap var2 = new HashMap();

      for (DANEEntry var4 : var1) {
         var2.put(var4.getDomainName(), var4);
      }

      this.entries = Collections.unmodifiableMap(var2);
   }

   @Override
   public Collection getMatches(Selector var1) throws StoreException {
      if (var1 == null) {
         return this.entries.values();
      } else {
         ArrayList var2 = new ArrayList();

         for (Object var4 : this.entries.values()) {
            if (var1.match(var4)) {
               var2.add(var4);
            }
         }

         return Collections.unmodifiableList(var2);
      }
   }

   public Store toCertificateStore() {
      Collection var1 = this.getMatches(null);
      ArrayList var2 = new ArrayList(var1.size());

      for (DANEEntry var4 : var1) {
         var2.add(var4.getCertificate());
      }

      return new CollectionStore(var2);
   }
}
