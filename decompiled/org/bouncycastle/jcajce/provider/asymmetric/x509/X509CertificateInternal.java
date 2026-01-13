package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class X509CertificateInternal extends X509CertificateImpl {
   private final byte[] encoding;
   private final CertificateEncodingException exception;

   X509CertificateInternal(
      JcaJceHelper var1, Certificate var2, BasicConstraints var3, boolean[] var4, String var5, byte[] var6, byte[] var7, CertificateEncodingException var8
   ) {
      super(var1, var2, var3, var4, var5, var6);
      this.encoding = var7;
      this.exception = var8;
   }

   @Override
   public byte[] getEncoded() throws CertificateEncodingException {
      if (null != this.exception) {
         throw this.exception;
      } else if (null == this.encoding) {
         throw new CertificateEncodingException();
      } else {
         return this.encoding;
      }
   }
}
