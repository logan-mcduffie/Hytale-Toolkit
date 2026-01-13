package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.bouncycastle.jce.MultiCertStoreParameters;

public class MultiCertStoreSpi extends CertStoreSpi {
   private MultiCertStoreParameters params;

   public MultiCertStoreSpi(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof MultiCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException(
            "org.bouncycastle.jce.provider.MultiCertStoreSpi: parameter must be a MultiCertStoreParameters object\n" + var1.toString()
         );
      } else {
         this.params = (MultiCertStoreParameters)var1;
      }
   }

   @Override
   public Collection engineGetCertificates(CertSelector var1) throws CertStoreException {
      boolean var2 = this.params.getSearchAllStores();
      Iterator var3 = this.params.getCertStores().iterator();
      Object var4 = var2 ? new ArrayList() : Collections.EMPTY_LIST;

      while (var3.hasNext()) {
         CertStore var5 = (CertStore)var3.next();
         Collection var6 = var5.getCertificates(var1);
         if (var2) {
            var4.addAll(var6);
         } else if (!var6.isEmpty()) {
            return var6;
         }
      }

      return (Collection)var4;
   }

   @Override
   public Collection engineGetCRLs(CRLSelector var1) throws CertStoreException {
      boolean var2 = this.params.getSearchAllStores();
      Iterator var3 = this.params.getCertStores().iterator();
      Object var4 = var2 ? new ArrayList() : Collections.EMPTY_LIST;

      while (var3.hasNext()) {
         CertStore var5 = (CertStore)var3.next();
         Collection var6 = var5.getCRLs(var1);
         if (var2) {
            var4.addAll(var6);
         } else if (!var6.isEmpty()) {
            return var6;
         }
      }

      return (Collection)var4;
   }
}
