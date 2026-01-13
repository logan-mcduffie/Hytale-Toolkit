package org.bouncycastle.x509;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.util.StoreException;

abstract class PKIXCRLUtil {
   static Set findCRLs(X509CRLStoreSelector var0, PKIXParameters var1) throws AnnotatedException {
      HashSet var2 = new HashSet();

      try {
         findCRLs(var2, var0, var1.getCertStores());
         return var2;
      } catch (AnnotatedException var4) {
         throw new AnnotatedException("Exception obtaining complete CRLs.", var4);
      }
   }

   private static void findCRLs(Set var0, X509CRLStoreSelector var1, List var2) throws AnnotatedException {
      AnnotatedException var3 = null;
      boolean var4 = false;

      for (Object var6 : var2) {
         if (var6 instanceof X509Store) {
            X509Store var7 = (X509Store)var6;

            try {
               var0.addAll(var7.getMatches(var1));
               var4 = true;
            } catch (StoreException var9) {
               var3 = new AnnotatedException("Exception searching in X.509 CRL store.", var9);
            }
         } else {
            CertStore var11 = (CertStore)var6;

            try {
               var0.addAll(var11.getCRLs(var1));
               var4 = true;
            } catch (CertStoreException var10) {
               var3 = new AnnotatedException("Exception searching in X.509 CRL store.", var10);
            }
         }
      }

      if (!var4 && var3 != null) {
         throw var3;
      }
   }
}
