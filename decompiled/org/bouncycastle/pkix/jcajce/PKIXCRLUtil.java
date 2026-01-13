package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

abstract class PKIXCRLUtil {
   static Set findCRLs(X509CRLStoreSelector var0, PKIXParameters var1) throws AnnotatedException {
      return findCRLs(new PKIXCRLStoreSelector.Builder(var0).build(), var1);
   }

   static Set findCRLs(PKIXCRLStoreSelector var0, PKIXParameters var1) throws AnnotatedException {
      HashSet var2 = new HashSet();

      try {
         findCRLs(var2, var0, var1.getCertStores());
         return var2;
      } catch (AnnotatedException var4) {
         throw new AnnotatedException("Exception obtaining complete CRLs.", var4);
      }
   }

   static Set findCRLs(PKIXCRLStoreSelector var0, Date var1, List var2, List var3) throws AnnotatedException {
      HashSet var4 = new HashSet();

      try {
         findCRLs(var4, var0, var3);
         findCRLs(var4, var0, var2);
      } catch (AnnotatedException var11) {
         throw new AnnotatedException("Exception obtaining complete CRLs.", var11);
      }

      HashSet var5 = new HashSet();

      for (Object var7 : var4) {
         X509CRL var8 = (X509CRL)var7;
         Date var9 = var8.getNextUpdate();
         if (var9 == null || var9.after(var1)) {
            X509Certificate var10 = var0.getCertificateChecking();
            if (null == var10 || var8.getThisUpdate().before(var10.getNotAfter())) {
               var5.add(var8);
            }
         }
      }

      return var5;
   }

   private static void findCRLs(Set var0, PKIXCRLStoreSelector var1, List var2) throws AnnotatedException {
      AnnotatedException var3 = null;
      boolean var4 = false;

      for (Object var6 : var2) {
         if (var6 instanceof Store) {
            Store var7 = (Store)var6;

            try {
               var0.addAll(var7.getMatches(var1));
               var4 = true;
            } catch (StoreException var9) {
               var3 = new AnnotatedException("Exception searching in X.509 CRL store.", var9);
            }
         } else {
            CertStore var11 = (CertStore)var6;

            try {
               var0.addAll(PKIXCRLStoreSelector.getCRLs(var1, var11));
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
