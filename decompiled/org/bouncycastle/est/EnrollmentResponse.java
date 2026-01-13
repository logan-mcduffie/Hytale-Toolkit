package org.bouncycastle.est;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class EnrollmentResponse {
   private final Store<X509CertificateHolder> store;
   private final long notBefore;
   private final ESTRequest requestToRetry;
   private final Source source;
   private final PrivateKeyInfo privateKeyInfo;

   public EnrollmentResponse(Store<X509CertificateHolder> var1, long var2, ESTRequest var4, Source var5) {
      this.store = var1;
      this.notBefore = var2;
      this.requestToRetry = var4;
      this.source = var5;
      this.privateKeyInfo = null;
   }

   public EnrollmentResponse(Store<X509CertificateHolder> var1, long var2, ESTRequest var4, Source var5, PrivateKeyInfo var6) {
      this.store = var1;
      this.notBefore = var2;
      this.requestToRetry = var4;
      this.source = var5;
      this.privateKeyInfo = var6;
   }

   public boolean canRetry() {
      return this.notBefore < System.currentTimeMillis();
   }

   public Store<X509CertificateHolder> getStore() {
      return this.store;
   }

   public long getNotBefore() {
      return this.notBefore;
   }

   public ESTRequest getRequestToRetry() {
      return this.requestToRetry;
   }

   public Object getSession() {
      return this.source.getSession();
   }

   public Source getSource() {
      return this.source;
   }

   public boolean isCompleted() {
      return this.requestToRetry == null;
   }

   public PrivateKeyInfo getPrivateKeyInfo() {
      return this.privateKeyInfo;
   }
}
