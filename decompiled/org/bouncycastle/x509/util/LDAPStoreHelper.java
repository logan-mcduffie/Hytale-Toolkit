package org.bouncycastle.x509.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.jce.provider.X509AttrCertParser;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.jce.provider.X509CertPairParser;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CRLStoreSelector;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509CertificatePair;

public class LDAPStoreHelper {
   private X509LDAPCertStoreParameters params;
   private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
   private static String REFERRALS_IGNORE = "ignore";
   private static final String SEARCH_SECURITY_LEVEL = "none";
   private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
   private Map cacheMap;
   private static int cacheSize = 32;
   private static long lifeTime = 60000L;

   public LDAPStoreHelper(X509LDAPCertStoreParameters var1) {
      this.cacheMap = new HashMap(cacheSize);
      this.params = var1;
   }

   private DirContext connectLDAP() throws NamingException {
      Properties var1 = new Properties();
      var1.setProperty("java.naming.factory.initial", LDAP_PROVIDER);
      var1.setProperty("java.naming.batchsize", "0");
      var1.setProperty("java.naming.provider.url", this.params.getLdapURL());
      var1.setProperty("java.naming.factory.url.pkgs", "com.sun.jndi.url");
      var1.setProperty("java.naming.referral", REFERRALS_IGNORE);
      var1.setProperty("java.naming.security.authentication", "none");
      return new InitialDirContext(var1);
   }

   private String parseDN(String var1, String var2) {
      int var4 = Strings.toLowerCase(var1).indexOf(Strings.toLowerCase(var2) + "=");
      if (var4 == -1) {
         return "";
      } else {
         String var3 = var1.substring(var4 + var2.length());
         int var5 = var3.indexOf(44);
         if (var5 == -1) {
            var5 = var3.length();
         }

         while (var3.charAt(var5 - 1) == '\\') {
            var5 = var3.indexOf(44, var5 + 1);
            if (var5 == -1) {
               var5 = var3.length();
            }
         }

         var3 = var3.substring(0, var5);
         var4 = var3.indexOf(61);
         var3 = var3.substring(var4 + 1);
         if (var3.charAt(0) == ' ') {
            var3 = var3.substring(1);
         }

         if (var3.startsWith("\"")) {
            var3 = var3.substring(1);
         }

         if (var3.endsWith("\"")) {
            var3 = var3.substring(0, var3.length() - 1);
         }

         return var3;
      }
   }

   private Set createCerts(List var1, X509CertStoreSelector var2) throws StoreException {
      HashSet var3 = new HashSet();
      Iterator var4 = var1.iterator();
      X509CertParser var5 = new X509CertParser();

      while (var4.hasNext()) {
         try {
            var5.engineInit(new ByteArrayInputStream((byte[])var4.next()));
            X509Certificate var6 = (X509Certificate)var5.engineRead();
            if (var2.match((Object)var6)) {
               var3.add(var6);
            }
         } catch (Exception var7) {
         }
      }

      return var3;
   }

   private List certSubjectSerialSearch(X509CertStoreSelector var1, String[] var2, String[] var3, String[] var4) throws StoreException {
      ArrayList var5 = new ArrayList();
      Object var6 = null;
      String var7 = null;
      var6 = this.getSubjectAsString(var1);
      if (var1.getSerialNumber() != null) {
         var7 = var1.getSerialNumber().toString();
      }

      if (var1.getCertificate() != null) {
         var6 = var1.getCertificate().getSubjectX500Principal().getName("RFC1779");
         var7 = var1.getCertificate().getSerialNumber().toString();
      }

      Object var8 = null;
      if (var6 != null) {
         for (int var9 = 0; var9 < var4.length; var9++) {
            var8 = this.parseDN((String)var6, var4[var9]);
            var5.addAll(this.search(var3, "*" + var8 + "*", var2));
         }
      }

      if (var7 != null && this.params.getSearchForSerialNumberIn() != null) {
         var5.addAll(this.search(this.splitString(this.params.getSearchForSerialNumberIn()), var7, var2));
      }

      if (var7 == null && var6 == null) {
         var5.addAll(this.search(var3, "*", var2));
      }

      return var5;
   }

   private List crossCertificatePairSubjectSearch(X509CertPairStoreSelector var1, String[] var2, String[] var3, String[] var4) throws StoreException {
      ArrayList var5 = new ArrayList();
      String var6 = null;
      if (var1.getForwardSelector() != null) {
         var6 = this.getSubjectAsString(var1.getForwardSelector());
      }

      if (var1.getCertPair() != null && var1.getCertPair().getForward() != null) {
         var6 = var1.getCertPair().getForward().getSubjectX500Principal().getName("RFC1779");
      }

      Object var7 = null;
      if (var6 != null) {
         for (int var8 = 0; var8 < var4.length; var8++) {
            var7 = this.parseDN(var6, var4[var8]);
            var5.addAll(this.search(var3, "*" + var7 + "*", var2));
         }
      }

      if (var6 == null) {
         var5.addAll(this.search(var3, "*", var2));
      }

      return var5;
   }

   private List attrCertSubjectSerialSearch(X509AttributeCertStoreSelector var1, String[] var2, String[] var3, String[] var4) throws StoreException {
      ArrayList var5 = new ArrayList();
      String var6 = null;
      Object var7 = null;
      HashSet var8 = new HashSet();
      Principal[] var9 = null;
      if (var1.getHolder() != null) {
         if (var1.getHolder().getSerialNumber() != null) {
            var8.add(var1.getHolder().getSerialNumber().toString());
         }

         if (var1.getHolder().getEntityNames() != null) {
            var9 = var1.getHolder().getEntityNames();
         }
      }

      if (var1.getAttributeCert() != null) {
         if (var1.getAttributeCert().getHolder().getEntityNames() != null) {
            var9 = var1.getAttributeCert().getHolder().getEntityNames();
         }

         var8.add(var1.getAttributeCert().getSerialNumber().toString());
      }

      if (var9 != null) {
         if (var9[0] instanceof X500Principal) {
            var6 = ((X500Principal)var9[0]).getName("RFC1779");
         } else {
            var6 = var9[0].getName();
         }
      }

      if (var1.getSerialNumber() != null) {
         var8.add(var1.getSerialNumber().toString());
      }

      Object var10 = null;
      if (var6 != null) {
         for (int var11 = 0; var11 < var4.length; var11++) {
            var10 = this.parseDN(var6, var4[var11]);
            var5.addAll(this.search(var3, "*" + var10 + "*", var2));
         }
      }

      if (var8.size() > 0 && this.params.getSearchForSerialNumberIn() != null) {
         for (String var12 : var8) {
            var5.addAll(this.search(this.splitString(this.params.getSearchForSerialNumberIn()), var12, var2));
         }
      }

      if (var8.size() == 0 && var6 == null) {
         var5.addAll(this.search(var3, "*", var2));
      }

      return var5;
   }

   private List cRLIssuerSearch(X509CRLStoreSelector var1, String[] var2, String[] var3, String[] var4) throws StoreException {
      ArrayList var5 = new ArrayList();
      String var6 = null;
      HashSet var7 = new HashSet();
      if (var1.getIssuers() != null) {
         var7.addAll(var1.getIssuers());
      }

      if (var1.getCertificateChecking() != null) {
         var7.add(this.getCertificateIssuer(var1.getCertificateChecking()));
      }

      if (var1.getAttrCertificateChecking() != null) {
         Principal[] var8 = var1.getAttrCertificateChecking().getIssuer().getPrincipals();

         for (int var9 = 0; var9 < var8.length; var9++) {
            if (var8[var9] instanceof X500Principal) {
               var7.add(var8[var9]);
            }
         }
      }

      Iterator var11 = var7.iterator();

      while (var11.hasNext()) {
         var6 = ((X500Principal)var11.next()).getName("RFC1779");
         Object var12 = null;

         for (int var10 = 0; var10 < var4.length; var10++) {
            var12 = this.parseDN(var6, var4[var10]);
            var5.addAll(this.search(var3, "*" + var12 + "*", var2));
         }
      }

      if (var6 == null) {
         var5.addAll(this.search(var3, "*", var2));
      }

      return var5;
   }

   private List search(String[] var1, String var2, String[] var3) throws StoreException {
      Object var4 = null;
      if (var1 == null) {
         var4 = null;
      } else {
         var4 = "";
         if (var2.equals("**")) {
            var2 = "*";
         }

         for (int var5 = 0; var5 < var1.length; var5++) {
            var4 = var4 + "(" + var1[var5] + "=" + var2 + ")";
         }

         var4 = "(|" + var4 + ")";
      }

      String var25 = "";

      for (int var6 = 0; var6 < var3.length; var6++) {
         var25 = var25 + "(" + var3[var6] + "=*)";
      }

      var25 = "(|" + var25 + ")";
      String var27 = "(&" + var4 + "" + var25 + ")";
      if (var4 == null) {
         var27 = var25;
      }

      List var7 = this.getFromCache(var27);
      if (var7 != null) {
         return var7;
      } else {
         DirContext var8 = null;
         ArrayList var28 = new ArrayList();

         try {
            var8 = this.connectLDAP();
            SearchControls var9 = new SearchControls();
            var9.setSearchScope(2);
            var9.setCountLimit(0L);
            var9.setReturningAttributes(var3);
            NamingEnumeration var10 = var8.search(this.params.getBaseDN(), var27, var9);

            while (var10.hasMoreElements()) {
               SearchResult var11 = (SearchResult)var10.next();
               NamingEnumeration var12 = var11.getAttributes().getAll().next().getAll();

               while (var12.hasMore()) {
                  var28.add(var12.next());
               }
            }

            this.addToCache(var27, var28);
         } catch (NamingException var21) {
         } finally {
            try {
               if (null != var8) {
                  var8.close();
               }
            } catch (Exception var20) {
            }
         }

         return var28;
      }
   }

   private Set createCRLs(List var1, X509CRLStoreSelector var2) throws StoreException {
      HashSet var3 = new HashSet();
      X509CRLParser var4 = new X509CRLParser();
      Iterator var5 = var1.iterator();

      while (var5.hasNext()) {
         try {
            var4.engineInit(new ByteArrayInputStream((byte[])var5.next()));
            X509CRL var6 = (X509CRL)var4.engineRead();
            if (var2.match((Object)var6)) {
               var3.add(var6);
            }
         } catch (StreamParsingException var7) {
         }
      }

      return var3;
   }

   private Set createCrossCertificatePairs(List var1, X509CertPairStoreSelector var2) throws StoreException {
      HashSet var3 = new HashSet();

      for (int var4 = 0; var4 < var1.size(); var4++) {
         try {
            X509CertificatePair var5;
            try {
               X509CertPairParser var6 = new X509CertPairParser();
               var6.engineInit(new ByteArrayInputStream((byte[])var1.get(var4)));
               var5 = (X509CertificatePair)var6.engineRead();
            } catch (StreamParsingException var9) {
               byte[] var7 = (byte[])var1.get(var4);
               byte[] var8 = (byte[])var1.get(var4 + 1);
               var5 = new X509CertificatePair(
                  new CertificatePair(
                     Certificate.getInstance(new ASN1InputStream(var7).readObject()), Certificate.getInstance(new ASN1InputStream(var8).readObject())
                  )
               );
               var4++;
            }

            if (var2.match(var5)) {
               var3.add(var5);
            }
         } catch (CertificateParsingException var10) {
         } catch (IOException var11) {
         }
      }

      return var3;
   }

   private Set createAttributeCertificates(List var1, X509AttributeCertStoreSelector var2) throws StoreException {
      HashSet var3 = new HashSet();
      Iterator var4 = var1.iterator();
      X509AttrCertParser var5 = new X509AttrCertParser();

      while (var4.hasNext()) {
         try {
            var5.engineInit(new ByteArrayInputStream((byte[])var4.next()));
            X509AttributeCertificate var6 = (X509AttributeCertificate)var5.engineRead();
            if (var2.match(var6)) {
               var3.add(var6);
            }
         } catch (StreamParsingException var7) {
         }
      }

      return var3;
   }

   public Collection getAuthorityRevocationLists(X509CRLStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAuthorityRevocationListAttribute());
      String[] var3 = this.splitString(this.params.getLdapAuthorityRevocationListAttributeName());
      String[] var4 = this.splitString(this.params.getAuthorityRevocationListIssuerAttributeName());
      List var5 = this.cRLIssuerSearch(var1, var2, var3, var4);
      Set var6 = this.createCRLs(var5, var1);
      if (var6.size() == 0) {
         X509CRLStoreSelector var7 = new X509CRLStoreSelector();
         var5 = this.cRLIssuerSearch(var7, var2, var3, var4);
         var6.addAll(this.createCRLs(var5, var1));
      }

      return var6;
   }

   public Collection getAttributeCertificateRevocationLists(X509CRLStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAttributeCertificateRevocationListAttribute());
      String[] var3 = this.splitString(this.params.getLdapAttributeCertificateRevocationListAttributeName());
      String[] var4 = this.splitString(this.params.getAttributeCertificateRevocationListIssuerAttributeName());
      List var5 = this.cRLIssuerSearch(var1, var2, var3, var4);
      Set var6 = this.createCRLs(var5, var1);
      if (var6.size() == 0) {
         X509CRLStoreSelector var7 = new X509CRLStoreSelector();
         var5 = this.cRLIssuerSearch(var7, var2, var3, var4);
         var6.addAll(this.createCRLs(var5, var1));
      }

      return var6;
   }

   public Collection getAttributeAuthorityRevocationLists(X509CRLStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAttributeAuthorityRevocationListAttribute());
      String[] var3 = this.splitString(this.params.getLdapAttributeAuthorityRevocationListAttributeName());
      String[] var4 = this.splitString(this.params.getAttributeAuthorityRevocationListIssuerAttributeName());
      List var5 = this.cRLIssuerSearch(var1, var2, var3, var4);
      Set var6 = this.createCRLs(var5, var1);
      if (var6.size() == 0) {
         X509CRLStoreSelector var7 = new X509CRLStoreSelector();
         var5 = this.cRLIssuerSearch(var7, var2, var3, var4);
         var6.addAll(this.createCRLs(var5, var1));
      }

      return var6;
   }

   public Collection getCrossCertificatePairs(X509CertPairStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getCrossCertificateAttribute());
      String[] var3 = this.splitString(this.params.getLdapCrossCertificateAttributeName());
      String[] var4 = this.splitString(this.params.getCrossCertificateSubjectAttributeName());
      List var5 = this.crossCertificatePairSubjectSearch(var1, var2, var3, var4);
      Set var6 = this.createCrossCertificatePairs(var5, var1);
      if (var6.size() == 0) {
         X509CertStoreSelector var7 = new X509CertStoreSelector();
         X509CertPairStoreSelector var8 = new X509CertPairStoreSelector();
         var8.setForwardSelector(var7);
         var8.setReverseSelector(var7);
         var5 = this.crossCertificatePairSubjectSearch(var8, var2, var3, var4);
         var6.addAll(this.createCrossCertificatePairs(var5, var1));
      }

      return var6;
   }

   public Collection getUserCertificates(X509CertStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getUserCertificateAttribute());
      String[] var3 = this.splitString(this.params.getLdapUserCertificateAttributeName());
      String[] var4 = this.splitString(this.params.getUserCertificateSubjectAttributeName());
      List var5 = this.certSubjectSerialSearch(var1, var2, var3, var4);
      Set var6 = this.createCerts(var5, var1);
      if (var6.size() == 0) {
         X509CertStoreSelector var7 = new X509CertStoreSelector();
         var5 = this.certSubjectSerialSearch(var7, var2, var3, var4);
         var6.addAll(this.createCerts(var5, var1));
      }

      return var6;
   }

   public Collection getAACertificates(X509AttributeCertStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAACertificateAttribute());
      String[] var3 = this.splitString(this.params.getLdapAACertificateAttributeName());
      String[] var4 = this.splitString(this.params.getAACertificateSubjectAttributeName());
      List var5 = this.attrCertSubjectSerialSearch(var1, var2, var3, var4);
      Set var6 = this.createAttributeCertificates(var5, var1);
      if (var6.size() == 0) {
         X509AttributeCertStoreSelector var7 = new X509AttributeCertStoreSelector();
         var5 = this.attrCertSubjectSerialSearch(var7, var2, var3, var4);
         var6.addAll(this.createAttributeCertificates(var5, var1));
      }

      return var6;
   }

   public Collection getAttributeDescriptorCertificates(X509AttributeCertStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAttributeDescriptorCertificateAttribute());
      String[] var3 = this.splitString(this.params.getLdapAttributeDescriptorCertificateAttributeName());
      String[] var4 = this.splitString(this.params.getAttributeDescriptorCertificateSubjectAttributeName());
      List var5 = this.attrCertSubjectSerialSearch(var1, var2, var3, var4);
      Set var6 = this.createAttributeCertificates(var5, var1);
      if (var6.size() == 0) {
         X509AttributeCertStoreSelector var7 = new X509AttributeCertStoreSelector();
         var5 = this.attrCertSubjectSerialSearch(var7, var2, var3, var4);
         var6.addAll(this.createAttributeCertificates(var5, var1));
      }

      return var6;
   }

   public Collection getCACertificates(X509CertStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getCACertificateAttribute());
      String[] var3 = this.splitString(this.params.getLdapCACertificateAttributeName());
      String[] var4 = this.splitString(this.params.getCACertificateSubjectAttributeName());
      List var5 = this.certSubjectSerialSearch(var1, var2, var3, var4);
      Set var6 = this.createCerts(var5, var1);
      if (var6.size() == 0) {
         X509CertStoreSelector var7 = new X509CertStoreSelector();
         var5 = this.certSubjectSerialSearch(var7, var2, var3, var4);
         var6.addAll(this.createCerts(var5, var1));
      }

      return var6;
   }

   public Collection getDeltaCertificateRevocationLists(X509CRLStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getDeltaRevocationListAttribute());
      String[] var3 = this.splitString(this.params.getLdapDeltaRevocationListAttributeName());
      String[] var4 = this.splitString(this.params.getDeltaRevocationListIssuerAttributeName());
      List var5 = this.cRLIssuerSearch(var1, var2, var3, var4);
      Set var6 = this.createCRLs(var5, var1);
      if (var6.size() == 0) {
         X509CRLStoreSelector var7 = new X509CRLStoreSelector();
         var5 = this.cRLIssuerSearch(var7, var2, var3, var4);
         var6.addAll(this.createCRLs(var5, var1));
      }

      return var6;
   }

   public Collection getAttributeCertificateAttributes(X509AttributeCertStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getAttributeCertificateAttributeAttribute());
      String[] var3 = this.splitString(this.params.getLdapAttributeCertificateAttributeAttributeName());
      String[] var4 = this.splitString(this.params.getAttributeCertificateAttributeSubjectAttributeName());
      List var5 = this.attrCertSubjectSerialSearch(var1, var2, var3, var4);
      Set var6 = this.createAttributeCertificates(var5, var1);
      if (var6.size() == 0) {
         X509AttributeCertStoreSelector var7 = new X509AttributeCertStoreSelector();
         var5 = this.attrCertSubjectSerialSearch(var7, var2, var3, var4);
         var6.addAll(this.createAttributeCertificates(var5, var1));
      }

      return var6;
   }

   public Collection getCertificateRevocationLists(X509CRLStoreSelector var1) throws StoreException {
      String[] var2 = this.splitString(this.params.getCertificateRevocationListAttribute());
      String[] var3 = this.splitString(this.params.getLdapCertificateRevocationListAttributeName());
      String[] var4 = this.splitString(this.params.getCertificateRevocationListIssuerAttributeName());
      List var5 = this.cRLIssuerSearch(var1, var2, var3, var4);
      Set var6 = this.createCRLs(var5, var1);
      if (var6.size() == 0) {
         X509CRLStoreSelector var7 = new X509CRLStoreSelector();
         var5 = this.cRLIssuerSearch(var7, var2, var3, var4);
         var6.addAll(this.createCRLs(var5, var1));
      }

      return var6;
   }

   private synchronized void addToCache(String var1, List var2) {
      Date var3 = new Date(System.currentTimeMillis());
      ArrayList var4 = new ArrayList();
      var4.add(var3);
      var4.add(var2);
      if (this.cacheMap.containsKey(var1)) {
         this.cacheMap.put(var1, var4);
      } else {
         if (this.cacheMap.size() >= cacheSize) {
            Iterator var5 = this.cacheMap.entrySet().iterator();
            long var6 = var3.getTime();
            Object var8 = null;

            while (var5.hasNext()) {
               Entry var9 = (Entry)var5.next();
               long var10 = ((Date)((List)var9.getValue()).get(0)).getTime();
               if (var10 < var6) {
                  var6 = var10;
                  var8 = var9.getKey();
               }
            }

            this.cacheMap.remove(var8);
         }

         this.cacheMap.put(var1, var4);
      }
   }

   private List getFromCache(String var1) {
      List var2 = (List)this.cacheMap.get(var1);
      long var3 = System.currentTimeMillis();
      if (var2 != null) {
         return ((Date)var2.get(0)).getTime() < var3 - lifeTime ? null : (List)var2.get(1);
      } else {
         return null;
      }
   }

   private String[] splitString(String var1) {
      return var1.split("\\s+");
   }

   private String getSubjectAsString(X509CertStoreSelector var1) {
      try {
         byte[] var2 = var1.getSubjectAsBytes();
         return var2 != null ? new X500Principal(var2).getName("RFC1779") : null;
      } catch (IOException var3) {
         throw new StoreException("exception processing name: " + var3.getMessage(), var3);
      }
   }

   private X500Principal getCertificateIssuer(X509Certificate var1) {
      return var1.getIssuerX500Principal();
   }
}
