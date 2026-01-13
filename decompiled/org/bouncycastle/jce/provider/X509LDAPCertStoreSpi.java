package org.bouncycastle.jce.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.util.Strings;

public class X509LDAPCertStoreSpi extends CertStoreSpi {
   private static String[] FILTER_ESCAPE_TABLE = new String[93];
   private static String LDAP_PROVIDER;
   private static String REFERRALS_IGNORE;
   private static final String SEARCH_SECURITY_LEVEL = "none";
   private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
   private X509LDAPCertStoreParameters params;

   public X509LDAPCertStoreSpi(CertStoreParameters var1) throws InvalidAlgorithmParameterException {
      super(var1);
      if (!(var1 instanceof X509LDAPCertStoreParameters)) {
         throw new InvalidAlgorithmParameterException(
            X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + var1.toString()
         );
      } else {
         this.params = (X509LDAPCertStoreParameters)var1;
      }
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
      int var4 = Strings.toLowerCase(var1).indexOf(Strings.toLowerCase(var2));
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

      return this.filterEncode(var3);
   }

   @Override
   public Collection engineGetCertificates(CertSelector var1) throws CertStoreException {
      if (!(var1 instanceof X509CertSelector)) {
         throw new CertStoreException("selector is not a X509CertSelector");
      } else {
         X509CertSelector var2 = (X509CertSelector)var1;
         HashSet var3 = new HashSet();
         Set var4 = this.getEndCertificates(var2);
         var4.addAll(this.getCACertificates(var2));
         var4.addAll(this.getCrossCertificates(var2));
         Iterator var5 = var4.iterator();

         try {
            CertificateFactory var6 = CertificateFactory.getInstance("X.509", "BC");

            while (var5.hasNext()) {
               byte[] var7 = (byte[])var5.next();
               if (var7 != null && var7.length != 0) {
                  ArrayList var8 = new ArrayList();
                  var8.add(var7);

                  try {
                     CertificatePair var9 = CertificatePair.getInstance(new ASN1InputStream(var7).readObject());
                     var8.clear();
                     if (var9.getForward() != null) {
                        var8.add(var9.getForward().getEncoded());
                     }

                     if (var9.getReverse() != null) {
                        var8.add(var9.getReverse().getEncoded());
                     }
                  } catch (IOException var13) {
                  } catch (IllegalArgumentException var14) {
                  }

                  Iterator var16 = var8.iterator();

                  while (var16.hasNext()) {
                     ByteArrayInputStream var10 = new ByteArrayInputStream((byte[])var16.next());

                     try {
                        Certificate var11 = var6.generateCertificate(var10);
                        if (var2.match(var11)) {
                           var3.add(var11);
                        }
                     } catch (Exception var12) {
                     }
                  }
               }
            }

            return var3;
         } catch (Exception var15) {
            throw new CertStoreException("certificate cannot be constructed from LDAP result: " + var15);
         }
      }
   }

   private Set certSubjectSerialSearch(X509CertSelector var1, String[] var2, String var3, String var4) throws CertStoreException {
      HashSet var5 = new HashSet();

      try {
         if (var1.getSubjectAsBytes() == null && var1.getSubjectAsString() == null && var1.getCertificate() == null) {
            var5.addAll(this.search(var3, "*", var2));
         } else {
            Object var6 = null;
            String var7 = null;
            if (var1.getCertificate() != null) {
               var6 = var1.getCertificate().getSubjectX500Principal().getName("RFC1779");
               var7 = var1.getCertificate().getSerialNumber().toString();
            } else if (var1.getSubjectAsBytes() != null) {
               var6 = new X500Principal(var1.getSubjectAsBytes()).getName("RFC1779");
            } else {
               var6 = var1.getSubjectAsString();
            }

            String var8 = this.parseDN((String)var6, var4);
            var5.addAll(this.search(var3, "*" + var8 + "*", var2));
            if (var7 != null && this.params.getSearchForSerialNumberIn() != null) {
               var3 = this.params.getSearchForSerialNumberIn();
               var5.addAll(this.search(var3, "*" + var7 + "*", var2));
            }
         }

         return var5;
      } catch (IOException var9) {
         throw new CertStoreException("exception processing selector: " + var9);
      }
   }

   private Set getEndCertificates(X509CertSelector var1) throws CertStoreException {
      String[] var2 = new String[]{this.params.getUserCertificateAttribute()};
      String var3 = this.params.getLdapUserCertificateAttributeName();
      String var4 = this.params.getUserCertificateSubjectAttributeName();
      return this.certSubjectSerialSearch(var1, var2, var3, var4);
   }

   private Set getCACertificates(X509CertSelector var1) throws CertStoreException {
      String[] var2 = new String[]{this.params.getCACertificateAttribute()};
      String var3 = this.params.getLdapCACertificateAttributeName();
      String var4 = this.params.getCACertificateSubjectAttributeName();
      Set var5 = this.certSubjectSerialSearch(var1, var2, var3, var4);
      if (var5.isEmpty()) {
         var5.addAll(this.search(null, "*", var2));
      }

      return var5;
   }

   private Set getCrossCertificates(X509CertSelector var1) throws CertStoreException {
      String[] var2 = new String[]{this.params.getCrossCertificateAttribute()};
      String var3 = this.params.getLdapCrossCertificateAttributeName();
      String var4 = this.params.getCrossCertificateSubjectAttributeName();
      Set var5 = this.certSubjectSerialSearch(var1, var2, var3, var4);
      if (var5.isEmpty()) {
         var5.addAll(this.search(null, "*", var2));
      }

      return var5;
   }

   @Override
   public Collection engineGetCRLs(CRLSelector var1) throws CertStoreException {
      String[] var2 = new String[]{this.params.getCertificateRevocationListAttribute()};
      if (!(var1 instanceof X509CRLSelector)) {
         throw new CertStoreException("selector is not a X509CRLSelector");
      } else {
         X509CRLSelector var3 = (X509CRLSelector)var1;
         HashSet var4 = new HashSet();
         String var5 = this.params.getLdapCertificateRevocationListAttributeName();
         HashSet var6 = new HashSet();
         if (var3.getIssuerNames() != null) {
            for (Object var8 : var3.getIssuerNames()) {
               Object var9 = null;
               if (var8 instanceof String) {
                  String var10 = this.params.getCertificateRevocationListIssuerAttributeName();
                  var9 = this.parseDN((String)var8, var10);
               } else {
                  String var16 = this.params.getCertificateRevocationListIssuerAttributeName();
                  var9 = this.parseDN(new X500Principal((byte[])var8).getName("RFC1779"), var16);
               }

               var6.addAll(this.search(var5, "*" + var9 + "*", var2));
            }
         } else {
            var6.addAll(this.search(var5, "*", var2));
         }

         var6.addAll(this.search(null, "*", var2));
         Iterator var12 = var6.iterator();

         try {
            CertificateFactory var13 = CertificateFactory.getInstance("X.509", "BC");

            while (var12.hasNext()) {
               CRL var15 = var13.generateCRL(new ByteArrayInputStream((byte[])var12.next()));
               if (var3.match(var15)) {
                  var4.add(var15);
               }
            }

            return var4;
         } catch (Exception var11) {
            throw new CertStoreException("CRL cannot be constructed from LDAP result " + var11);
         }
      }
   }

   private String filterEncode(String var1) {
      if (var1 == null) {
         return null;
      } else {
         StringBuilder var2 = new StringBuilder(var1.length() * 2);
         int var3 = var1.length();

         for (int var4 = 0; var4 < var3; var4++) {
            char var5 = var1.charAt(var4);
            if (var5 < FILTER_ESCAPE_TABLE.length) {
               var2.append(FILTER_ESCAPE_TABLE[var5]);
            } else {
               var2.append(var5);
            }
         }

         return var2.toString();
      }
   }

   private Set search(String var1, String var2, String[] var3) throws CertStoreException {
      String var4 = var1 + "=" + var2;
      if (var1 == null) {
         var4 = null;
      }

      DirContext var5 = null;
      HashSet var6 = new HashSet();

      try {
         var5 = this.connectLDAP();
         SearchControls var7 = new SearchControls();
         var7.setSearchScope(2);
         var7.setCountLimit(0L);

         for (int var8 = 0; var8 < var3.length; var8++) {
            String[] var9 = new String[]{var3[var8]};
            var7.setReturningAttributes(var9);
            String var10 = "(&(" + var4 + ")(" + var9[0] + "=*))";
            if (var4 == null) {
               var10 = "(" + var9[0] + "=*)";
            }

            NamingEnumeration var11 = var5.search(this.params.getBaseDN(), var10, var7);

            while (var11.hasMoreElements()) {
               SearchResult var12 = (SearchResult)var11.next();
               NamingEnumeration var13 = var12.getAttributes().getAll().next().getAll();

               while (var13.hasMore()) {
                  Object var14 = var13.next();
                  var6.add(var14);
               }
            }
         }
      } catch (Exception var22) {
         throw new CertStoreException("Error getting results from LDAP directory " + var22);
      } finally {
         try {
            if (null != var5) {
               var5.close();
            }
         } catch (Exception var21) {
         }
      }

      return var6;
   }

   static {
      for (char var0 = 0; var0 < FILTER_ESCAPE_TABLE.length; var0++) {
         FILTER_ESCAPE_TABLE[var0] = String.valueOf(var0);
      }

      FILTER_ESCAPE_TABLE[42] = "\\2a";
      FILTER_ESCAPE_TABLE[40] = "\\28";
      FILTER_ESCAPE_TABLE[41] = "\\29";
      FILTER_ESCAPE_TABLE[92] = "\\5c";
      FILTER_ESCAPE_TABLE[0] = "\\00";
      LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
      REFERRALS_IGNORE = "ignore";
   }
}
