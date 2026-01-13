package org.bouncycastle.est.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.util.IPAddress;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class JsseDefaultHostnameAuthorizer implements JsseHostnameAuthorizer {
   private static Logger LOG = Logger.getLogger(JsseDefaultHostnameAuthorizer.class.getName());
   private final Set<String> knownSuffixes;

   public JsseDefaultHostnameAuthorizer(Set<String> var1) {
      this.knownSuffixes = var1;
   }

   @Override
   public boolean verified(String var1, SSLSession var2) throws IOException {
      try {
         CertificateFactory var3 = CertificateFactory.getInstance("X509");
         X509Certificate var4 = (X509Certificate)var3.generateCertificate(new ByteArrayInputStream(var2.getPeerCertificates()[0].getEncoded()));
         return this.verify(var1, var4);
      } catch (Exception var5) {
         if (var5 instanceof ESTException) {
            throw (ESTException)var5;
         } else {
            throw new ESTException(var5.getMessage(), var5);
         }
      }
   }

   public boolean verify(String var1, X509Certificate var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("'name' cannot be null");
      } else {
         boolean var3 = false;
         boolean var4 = IPAddress.isValidIPv4(var1);
         boolean var5 = !var4 && IPAddress.isValidIPv6(var1);
         boolean var6 = var4 || var5;

         try {
            Collection var7 = var2.getSubjectAlternativeNames();
            if (var7 != null) {
               InetAddress var8 = null;

               for (List var10 : var7) {
                  int var11 = (Integer)var10.get(0);
                  switch (var11) {
                     case 2:
                        if (!var6 && isValidNameMatch(var1, (String)var10.get(1), this.knownSuffixes)) {
                           return true;
                        }

                        var3 = true;
                        break;
                     case 7:
                        if (var6) {
                           String var21 = (String)var10.get(1);
                           if (var1.equalsIgnoreCase(var21)) {
                              return true;
                           }

                           if (var5 && IPAddress.isValidIPv6(var21)) {
                              try {
                                 if (var8 == null) {
                                    var8 = InetAddress.getByName(var1);
                                 }

                                 if (var8.equals(InetAddress.getByName(var21))) {
                                    return true;
                                 }
                              } catch (UnknownHostException var14) {
                              }
                           }
                        }
                        break;
                     default:
                        if (LOG.isLoggable(Level.INFO)) {
                           String var12;
                           if (var10.get(1) instanceof byte[]) {
                              var12 = Hex.toHexString((byte[])var10.get(1));
                           } else {
                              var12 = var10.get(1).toString();
                           }

                           LOG.log(Level.INFO, "ignoring type " + var11 + " value = " + var12);
                        }
                  }
               }
            }
         } catch (Exception var15) {
            throw new ESTException(var15.getMessage(), var15);
         }

         if (!var6 && !var3) {
            X500Principal var16 = var2.getSubjectX500Principal();
            if (var16 == null) {
               return false;
            } else {
               RDN[] var17 = X500Name.getInstance(var16.getEncoded()).getRDNs();

               for (int var18 = var17.length - 1; var18 >= 0; var18--) {
                  AttributeTypeAndValue[] var19 = var17[var18].getTypesAndValues();

                  for (int var20 = 0; var20 != var19.length; var20++) {
                     AttributeTypeAndValue var22 = var19[var20];
                     if (BCStyle.CN.equals(var22.getType())) {
                        ASN1Primitive var13 = var22.getValue().toASN1Primitive();
                        return var13 instanceof ASN1String && isValidNameMatch(var1, ((ASN1String)var13).getString(), this.knownSuffixes);
                     }
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static boolean isValidNameMatch(String var0, String var1, Set<String> var2) throws IOException {
      if (var1.contains("*")) {
         int var3 = var1.indexOf(42);
         if (var3 == var1.lastIndexOf("*")) {
            if (!var1.contains("..") && var1.charAt(var1.length() - 1) != '*') {
               int var4 = var1.indexOf(46, var3);
               if (var2 != null && var2.contains(Strings.toLowerCase(var1.substring(var4)))) {
                  throw new IOException("Wildcard `" + var1 + "` matches known public suffix.");
               } else {
                  String var5 = Strings.toLowerCase(var1.substring(var3 + 1));
                  String var6 = Strings.toLowerCase(var0);
                  if (var6.equals(var5)) {
                     return false;
                  } else if (var5.length() > var6.length()) {
                     return false;
                  } else if (var3 <= 0) {
                     String var7 = var6.substring(0, var6.length() - var5.length());
                     return var7.indexOf(46) > 0 ? false : var6.endsWith(var5);
                  } else {
                     return var6.startsWith(var1.substring(0, var3)) && var6.endsWith(var5)
                        ? var6.substring(var3, var6.length() - var5.length()).indexOf(46) < 0
                        : false;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return var0.equalsIgnoreCase(var1);
      }
   }
}
