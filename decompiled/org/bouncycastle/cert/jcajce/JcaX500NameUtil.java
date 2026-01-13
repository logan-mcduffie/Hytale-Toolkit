package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;

public class JcaX500NameUtil {
   public static X500Name getIssuer(X509Certificate var0) {
      return var0 instanceof BCX509Certificate ? notNull(((BCX509Certificate)var0).getIssuerX500Name()) : getX500Name(var0.getIssuerX500Principal());
   }

   public static X500Name getIssuer(X500NameStyle var0, X509Certificate var1) {
      return var1 instanceof BCX509Certificate
         ? X500Name.getInstance(var0, notNull(((BCX509Certificate)var1).getIssuerX500Name()))
         : getX500Name(var0, var1.getIssuerX500Principal());
   }

   public static X500Name getSubject(X509Certificate var0) {
      return var0 instanceof BCX509Certificate ? notNull(((BCX509Certificate)var0).getSubjectX500Name()) : getX500Name(var0.getSubjectX500Principal());
   }

   public static X500Name getSubject(X500NameStyle var0, X509Certificate var1) {
      return var1 instanceof BCX509Certificate
         ? X500Name.getInstance(var0, notNull(((BCX509Certificate)var1).getSubjectX500Name()))
         : getX500Name(var0, var1.getSubjectX500Principal());
   }

   public static X500Name getX500Name(X500Principal var0) {
      return X500Name.getInstance(getEncoded(var0));
   }

   public static X500Name getX500Name(X500NameStyle var0, X500Principal var1) {
      return X500Name.getInstance(var0, getEncoded(var1));
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

   private static byte[] getEncoded(X500Principal var0) {
      return notNull(var0).getEncoded();
   }
}
