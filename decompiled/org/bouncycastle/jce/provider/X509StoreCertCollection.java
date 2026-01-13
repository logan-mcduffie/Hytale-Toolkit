package org.bouncycastle.jce.provider;

import java.util.Collection;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Selector;
import org.bouncycastle.x509.X509CollectionStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.X509StoreSpi;

public class X509StoreCertCollection extends X509StoreSpi {
   private CollectionStore _store;

   @Override
   public void engineInit(X509StoreParameters var1) {
      if (!(var1 instanceof X509CollectionStoreParameters)) {
         throw new IllegalArgumentException(var1.toString());
      } else {
         this._store = new CollectionStore(((X509CollectionStoreParameters)var1).getCollection());
      }
   }

   @Override
   public Collection engineGetMatches(Selector var1) {
      return this._store.getMatches(var1);
   }
}
