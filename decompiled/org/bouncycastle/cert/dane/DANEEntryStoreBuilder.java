package org.bouncycastle.cert.dane;

public class DANEEntryStoreBuilder {
   private final DANEEntryFetcherFactory daneEntryFetcher;

   public DANEEntryStoreBuilder(DANEEntryFetcherFactory var1) {
      this.daneEntryFetcher = var1;
   }

   public DANEEntryStore build(String var1) throws DANEException {
      return new DANEEntryStore(this.daneEntryFetcher.build(var1).getEntries());
   }
}
