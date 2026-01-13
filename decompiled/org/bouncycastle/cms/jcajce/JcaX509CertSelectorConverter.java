package org.bouncycastle.cms.jcajce;

import java.security.cert.X509CertSelector;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.SignerId;

public class JcaX509CertSelectorConverter extends org.bouncycastle.cert.selector.jcajce.JcaX509CertSelectorConverter {
   public X509CertSelector getCertSelector(KeyTransRecipientId var1) {
      return this.doConversion(var1.getIssuer(), var1.getSerialNumber(), var1.getSubjectKeyIdentifier());
   }

   public X509CertSelector getCertSelector(SignerId var1) {
      return this.doConversion(var1.getIssuer(), var1.getSerialNumber(), var1.getSubjectKeyIdentifier());
   }
}
