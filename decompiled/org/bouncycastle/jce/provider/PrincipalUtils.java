package org.bouncycastle.jce.provider;

import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.x509.X509AttributeCertificate;

class PrincipalUtils {
   static X500Name getCA(TrustAnchor var0) {
      return getX500Name(notNull(var0).getCA());
   }

   static X500Name getEncodedIssuerPrincipal(Object var0) {
      return var0 instanceof X509Certificate
         ? getIssuerPrincipal((X509Certificate)var0)
         : getX500Name((X500Principal)((X509AttributeCertificate)var0).getIssuer().getPrincipals()[0]);
   }

   static X500Name getIssuerPrincipal(X509Certificate var0) {
      return var0 instanceof BCX509Certificate ? notNull(((BCX509Certificate)var0).getIssuerX500Name()) : getX500Name(notNull(var0).getIssuerX500Principal());
   }

   static X500Name getIssuerPrincipal(X509CRL var0) {
      return getX500Name(notNull(var0).getIssuerX500Principal());
   }

   static X500Name getSubjectPrincipal(X509Certificate var0) {
      return var0 instanceof BCX509Certificate ? notNull(((BCX509Certificate)var0).getSubjectX500Name()) : getX500Name(notNull(var0).getSubjectX500Principal());
   }

   static X500Name getX500Name(X500Principal var0) {
      X500Name var1 = X500Name.getInstance(getEncoded(var0));
      return notNull(var1);
   }

   static X500Name getX500Name(X500NameStyle var0, X500Principal var1) {
      X500Name var2 = X500Name.getInstance(var0, getEncoded(var1));
      return notNull(var2);
   }

   private static byte[] getEncoded(X500Principal var0) {
      byte[] var1 = notNull(var0).getEncoded();
      return notNull(var1);
   }

   private static byte[] notNull(byte[] var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   private static TrustAnchor notNull(TrustAnchor var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   private static X509Certificate notNull(X509Certificate var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   private static X509CRL notNull(X509CRL var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   private static X500Name notNull(X500Name var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }

   private static X500Principal notNull(X500Principal var0) {
      if (null == var0) {
         throw new IllegalStateException();
      } else {
         return var0;
      }
   }
}
