package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaSelectorConverter {
   public X509CertificateHolderSelector getCertificateHolderSelector(X509CertSelector var1) {
      try {
         X500Name var2 = X500Name.getInstance(var1.getIssuerAsBytes());
         BigInteger var3 = var1.getSerialNumber();
         byte[] var4 = null;
         byte[] var5 = var1.getSubjectKeyIdentifier();
         if (var5 != null) {
            var4 = ASN1OctetString.getInstance(var5).getOctets();
         }

         return new X509CertificateHolderSelector(var2, var3, var4);
      } catch (IOException var6) {
         throw new IllegalArgumentException("unable to convert issuer: " + var6.getMessage());
      }
   }
}
