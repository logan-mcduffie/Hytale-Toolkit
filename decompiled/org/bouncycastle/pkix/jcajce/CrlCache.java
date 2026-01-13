package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

class CrlCache {
   private static final int DEFAULT_TIMEOUT = 15000;
   private static Map<URI, WeakReference<PKIXCRLStore>> cache = Collections.synchronizedMap(new WeakHashMap<>());

   static synchronized PKIXCRLStore getCrl(CertificateFactory var0, Date var1, URI var2) throws IOException, CRLException {
      PKIXCRLStore var3 = null;
      WeakReference var4 = cache.get(var2);
      if (var4 != null) {
         var3 = (PKIXCRLStore)var4.get();
      }

      if (var3 != null) {
         boolean var5 = false;

         for (X509CRL var7 : var3.getMatches(null)) {
            Date var8 = var7.getNextUpdate();
            if (var8 != null && var8.before(var1)) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            return var3;
         }
      }

      Collection var9;
      if (var2.getScheme().equals("ldap")) {
         var9 = getCrlsFromLDAP(var0, var2);
      } else {
         var9 = getCrls(var0, var2);
      }

      CrlCache.LocalCRLStore var10 = new CrlCache.LocalCRLStore(new CollectionStore<>(var9));
      cache.put(var2, new WeakReference<>(var10));
      return var10;
   }

   private static Collection getCrlsFromLDAP(CertificateFactory var0, URI var1) throws IOException, CRLException {
      Hashtable var2 = new Hashtable();
      var2.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
      var2.put("java.naming.provider.url", var1.toString());
      Object var3 = null;

      try {
         InitialDirContext var4 = new InitialDirContext(var2);
         Attributes var5 = var4.getAttributes("");
         Attribute var6 = var5.get("certificateRevocationList;binary");
         var3 = (byte[])var6.get();
      } catch (NamingException var7) {
         throw new CRLException("issue connecting to: " + var1.toString(), var7);
      }

      if (var3 != null && ((Object[])var3).length != 0) {
         return var0.generateCRLs(new ByteArrayInputStream((byte[])var3));
      } else {
         throw new CRLException("no CRL returned from: " + var1);
      }
   }

   private static Collection getCrls(CertificateFactory var0, URI var1) throws IOException, CRLException {
      HttpURLConnection var2 = (HttpURLConnection)var1.toURL().openConnection();
      var2.setConnectTimeout(15000);
      var2.setReadTimeout(15000);
      InputStream var3 = var2.getInputStream();
      Collection var4 = var0.generateCRLs(var3);
      var3.close();
      return var4;
   }

   private static class LocalCRLStore<T extends CRL> implements PKIXCRLStore, Iterable<CRL> {
      private Collection<CRL> _local;

      public LocalCRLStore(Store<CRL> var1) {
         this._local = new ArrayList<>(var1.getMatches(null));
      }

      @Override
      public Collection getMatches(Selector var1) {
         if (var1 == null) {
            return new ArrayList<>(this._local);
         } else {
            ArrayList var2 = new ArrayList();

            for (CRL var4 : this._local) {
               if (var1.match(var4)) {
                  var2.add(var4);
               }
            }

            return var2;
         }
      }

      @Override
      public Iterator<CRL> iterator() {
         return this.getMatches(null).iterator();
      }
   }
}
