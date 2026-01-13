package org.bouncycastle.cert.dane.fetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.cert.dane.DANEEntry;
import org.bouncycastle.cert.dane.DANEEntryFetcher;
import org.bouncycastle.cert.dane.DANEEntryFetcherFactory;
import org.bouncycastle.cert.dane.DANEException;

public class JndiDANEFetcherFactory implements DANEEntryFetcherFactory {
   private static final String DANE_TYPE = "53";
   private List dnsServerList = new ArrayList();
   private boolean isAuthoritative;

   public JndiDANEFetcherFactory usingDNSServer(String var1) {
      this.dnsServerList.add(var1);
      return this;
   }

   public JndiDANEFetcherFactory setAuthoritative(boolean var1) {
      this.isAuthoritative = var1;
      return this;
   }

   @Override
   public DANEEntryFetcher build(final String var1) {
      final Hashtable var2 = new Hashtable();
      var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      var2.put("java.naming.authoritative", this.isAuthoritative ? "true" : "false");
      if (this.dnsServerList.size() > 0) {
         StringBuilder var3 = new StringBuilder();

         for (Iterator var4 = this.dnsServerList.iterator(); var4.hasNext(); var3.append("dns://" + var4.next())) {
            if (var3.length() > 0) {
               var3.append(" ");
            }
         }

         var2.put("java.naming.provider.url", var3.toString());
      }

      return new DANEEntryFetcher() {
         @Override
         public List getEntries() throws DANEException {
            ArrayList var1x = new ArrayList();

            try {
               InitialDirContext var2x = new InitialDirContext(var2);
               if (var1.indexOf("_smimecert.") > 0) {
                  Attributes var4x = var2x.getAttributes(var1, new String[]{"53"});
                  Attribute var5 = var4x.get("53");
                  if (var5 != null) {
                     JndiDANEFetcherFactory.this.addEntries(var1x, var1, var5);
                  }
               } else {
                  NamingEnumeration var3 = var2x.listBindings("_smimecert." + var1);

                  while (var3.hasMore()) {
                     Binding var12 = (Binding)var3.next();
                     DirContext var13 = (DirContext)var12.getObject();
                     String var6 = var13.getNameInNamespace().substring(1, var13.getNameInNamespace().length() - 1);
                     Attributes var7 = var2x.getAttributes(var6, new String[]{"53"});
                     Attribute var8 = var7.get("53");
                     if (var8 != null) {
                        String var9 = var13.getNameInNamespace();
                        String var10 = var9.substring(1, var9.length() - 1);
                        JndiDANEFetcherFactory.this.addEntries(var1x, var10, var8);
                     }
                  }
               }

               return var1x;
            } catch (NamingException var11) {
               throw new DANEException("Exception dealing with DNS: " + var11.getMessage(), var11);
            }
         }
      };
   }

   private void addEntries(List var1, String var2, Attribute var3) throws NamingException, DANEException {
      for (int var4 = 0; var4 != var3.size(); var4++) {
         byte[] var5 = (byte[])var3.get(var4);
         if (DANEEntry.isValidCertificate(var5)) {
            try {
               var1.add(new DANEEntry(var2, var5));
            } catch (IOException var7) {
               throw new DANEException("Exception parsing entry: " + var7.getMessage(), var7);
            }
         }
      }
   }
}
