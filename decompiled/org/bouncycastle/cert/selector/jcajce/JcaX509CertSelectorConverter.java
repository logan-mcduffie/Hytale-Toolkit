package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.util.Exceptions;

public class JcaX509CertSelectorConverter {
   protected X509CertSelector doConversion(X500Name var1, BigInteger var2, byte[] var3) {
      X509CertSelector var4 = new X509CertSelector();
      if (var1 != null) {
         try {
            var4.setIssuer(var1.getEncoded());
         } catch (IOException var7) {
            throw Exceptions.illegalArgumentException("unable to convert issuer: " + var7.getMessage(), var7);
         }
      }

      if (var2 != null) {
         var4.setSerialNumber(var2);
      }

      if (var3 != null) {
         try {
            var4.setSubjectKeyIdentifier(new DEROctetString(var3).getEncoded());
         } catch (IOException var6) {
            throw Exceptions.illegalArgumentException("unable to convert subjectKeyIdentifier: " + var6.getMessage(), var6);
         }
      }

      return var4;
   }

   public X509CertSelector getCertSelector(X509CertificateHolderSelector var1) {
      return this.doConversion(var1.getIssuer(), var1.getSerialNumber(), var1.getSubjectKeyIdentifier());
   }
}
