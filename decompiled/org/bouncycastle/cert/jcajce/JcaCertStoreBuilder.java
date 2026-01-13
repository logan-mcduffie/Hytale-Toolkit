package org.bouncycastle.cert.jcajce;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;

public class JcaCertStoreBuilder {
   private List certs = new ArrayList();
   private List crls = new ArrayList();
   private Object provider;
   private JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
   private JcaX509CRLConverter crlConverter = new JcaX509CRLConverter();
   private String type = "Collection";

   public JcaCertStoreBuilder addCertificates(Store var1) {
      this.certs.addAll(var1.getMatches(null));
      return this;
   }

   public JcaCertStoreBuilder addCertificate(X509CertificateHolder var1) {
      this.certs.add(var1);
      return this;
   }

   public JcaCertStoreBuilder addCRLs(Store var1) {
      this.crls.addAll(var1.getMatches(null));
      return this;
   }

   public JcaCertStoreBuilder addCRL(X509CRLHolder var1) {
      this.crls.add(var1);
      return this;
   }

   public JcaCertStoreBuilder setProvider(String var1) {
      this.certificateConverter.setProvider(var1);
      this.crlConverter.setProvider(var1);
      this.provider = var1;
      return this;
   }

   public JcaCertStoreBuilder setProvider(Provider var1) {
      this.certificateConverter.setProvider(var1);
      this.crlConverter.setProvider(var1);
      this.provider = var1;
      return this;
   }

   public JcaCertStoreBuilder setType(String var1) {
      this.type = var1;
      return this;
   }

   public CertStore build() throws GeneralSecurityException {
      CollectionCertStoreParameters var1 = this.convertHolders(this.certificateConverter, this.crlConverter);
      if (this.provider instanceof String) {
         return CertStore.getInstance(this.type, var1, (String)this.provider);
      } else {
         return this.provider instanceof Provider ? CertStore.getInstance(this.type, var1, (Provider)this.provider) : CertStore.getInstance(this.type, var1);
      }
   }

   private CollectionCertStoreParameters convertHolders(JcaX509CertificateConverter var1, JcaX509CRLConverter var2) throws CertificateException, CRLException {
      ArrayList var3 = new ArrayList(this.certs.size() + this.crls.size());
      Iterator var4 = this.certs.iterator();

      while (var4.hasNext()) {
         var3.add(var1.getCertificate((X509CertificateHolder)var4.next()));
      }

      var4 = this.crls.iterator();

      while (var4.hasNext()) {
         var3.add(var2.getCRL((X509CRLHolder)var4.next()));
      }

      return new CollectionCertStoreParameters(var3);
   }
}
