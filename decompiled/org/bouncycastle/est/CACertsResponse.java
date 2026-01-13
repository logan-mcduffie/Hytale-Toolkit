package org.bouncycastle.est;

import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class CACertsResponse {
   private final Store<X509CertificateHolder> store;
   private Store<X509CRLHolder> crlHolderStore;
   private final ESTRequest requestToRetry;
   private final Source session;
   private final boolean trusted;

   public CACertsResponse(Store<X509CertificateHolder> var1, Store<X509CRLHolder> var2, ESTRequest var3, Source var4, boolean var5) {
      this.store = var1;
      this.requestToRetry = var3;
      this.session = var4;
      this.trusted = var5;
      this.crlHolderStore = var2;
   }

   public boolean hasCertificates() {
      return this.store != null;
   }

   public Store<X509CertificateHolder> getCertificateStore() {
      if (this.store == null) {
         throw new IllegalStateException("Response has no certificates.");
      } else {
         return this.store;
      }
   }

   public boolean hasCRLs() {
      return this.crlHolderStore != null;
   }

   public Store<X509CRLHolder> getCrlStore() {
      if (this.crlHolderStore == null) {
         throw new IllegalStateException("Response has no CRLs.");
      } else {
         return this.crlHolderStore;
      }
   }

   public ESTRequest getRequestToRetry() {
      return this.requestToRetry;
   }

   public Object getSession() {
      return this.session.getSession();
   }

   public boolean isTrusted() {
      return this.trusted;
   }
}
