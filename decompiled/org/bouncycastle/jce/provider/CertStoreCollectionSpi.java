package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CertStoreCollectionSpi extends CertStoreSpi {
   private CollectionCertStoreParameters params;

   public CertStoreCollectionSpi(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof CollectionCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException(
            "org.bouncycastle.jce.provider.CertStoreCollectionSpi: parameter must be a CollectionCertStoreParameters object\n" + var1.toString()
         );
      } else {
         this.params = (CollectionCertStoreParameters)var1;
      }
   }

   @Override
   public Collection engineGetCertificates(CertSelector var1) throws CertStoreException {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.params.getCollection().iterator();
      if (var1 == null) {
         while (var3.hasNext()) {
            Object var5 = var3.next();
            if (var5 instanceof Certificate) {
               var2.add(var5);
            }
         }
      } else {
         while (var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof Certificate && var1.match((Certificate)var4)) {
               var2.add(var4);
            }
         }
      }

      return var2;
   }

   @Override
   public Collection engineGetCRLs(CRLSelector var1) throws CertStoreException {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.params.getCollection().iterator();
      if (var1 == null) {
         while (var3.hasNext()) {
            Object var5 = var3.next();
            if (var5 instanceof CRL) {
               var2.add(var5);
            }
         }
      } else {
         while (var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof CRL && var1.match((CRL)var4)) {
               var2.add(var4);
            }
         }
      }

      return var2;
   }
}
