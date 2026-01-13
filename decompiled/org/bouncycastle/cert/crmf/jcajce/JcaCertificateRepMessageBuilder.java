package org.bouncycastle.cert.crmf.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessageBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaCertificateRepMessageBuilder extends CertificateRepMessageBuilder {
   public JcaCertificateRepMessageBuilder(X509Certificate... var1) throws CertificateEncodingException {
      super(convert(var1));
   }

   private static X509CertificateHolder[] convert(X509Certificate... var0) throws CertificateEncodingException {
      X509CertificateHolder[] var1 = new X509CertificateHolder[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = new JcaX509CertificateHolder(var0[var2]);
      }

      return var1;
   }
}
