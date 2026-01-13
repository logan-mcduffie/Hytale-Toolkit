package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class X509CRLInternal extends X509CRLImpl {
   private final byte[] encoding;
   private final CRLException exception;

   X509CRLInternal(JcaJceHelper var1, CertificateList var2, String var3, byte[] var4, boolean var5, byte[] var6, CRLException var7) {
      super(var1, var2, var3, var4, var5);
      this.encoding = var6;
      this.exception = var7;
   }

   @Override
   public byte[] getEncoded() throws CRLException {
      if (null != this.exception) {
         throw this.exception;
      } else if (null == this.encoding) {
         throw new CRLException();
      } else {
         return this.encoding;
      }
   }
}
