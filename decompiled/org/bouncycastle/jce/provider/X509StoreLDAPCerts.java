package org.bouncycastle.jce.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509CertificatePair;
import org.bouncycastle.x509.X509StoreParameters;
import org.bouncycastle.x509.X509StoreSpi;
import org.bouncycastle.x509.util.LDAPStoreHelper;

public class X509StoreLDAPCerts extends X509StoreSpi {
   private LDAPStoreHelper helper;

   @Override
   public void engineInit(X509StoreParameters var1) {
      if (!(var1 instanceof X509LDAPCertStoreParameters)) {
         throw new IllegalArgumentException("Initialization parameters must be an instance of " + X509LDAPCertStoreParameters.class.getName() + ".");
      } else {
         this.helper = new LDAPStoreHelper((X509LDAPCertStoreParameters)var1);
      }
   }

   @Override
   public Collection engineGetMatches(Selector var1) throws StoreException {
      if (!(var1 instanceof X509CertStoreSelector)) {
         return Collections.EMPTY_SET;
      } else {
         X509CertStoreSelector var2 = (X509CertStoreSelector)var1;
         HashSet var3 = new HashSet();
         if (var2.getBasicConstraints() > 0) {
            var3.addAll(this.helper.getCACertificates(var2));
            var3.addAll(this.getCertificatesFromCrossCertificatePairs(var2));
         } else if (var2.getBasicConstraints() == -2) {
            var3.addAll(this.helper.getUserCertificates(var2));
         } else {
            var3.addAll(this.helper.getUserCertificates(var2));
            var3.addAll(this.helper.getCACertificates(var2));
            var3.addAll(this.getCertificatesFromCrossCertificatePairs(var2));
         }

         return var3;
      }
   }

   private Collection getCertificatesFromCrossCertificatePairs(X509CertStoreSelector var1) throws StoreException {
      HashSet var2 = new HashSet();
      X509CertPairStoreSelector var3 = new X509CertPairStoreSelector();
      var3.setForwardSelector(var1);
      var3.setReverseSelector(new X509CertStoreSelector());
      HashSet var4 = new HashSet(this.helper.getCrossCertificatePairs(var3));
      HashSet var5 = new HashSet();
      HashSet var6 = new HashSet();

      for (X509CertificatePair var8 : var4) {
         if (var8.getForward() != null) {
            var5.add(var8.getForward());
         }

         if (var8.getReverse() != null) {
            var6.add(var8.getReverse());
         }
      }

      var2.addAll(var5);
      var2.addAll(var6);
      return var2;
   }
}
