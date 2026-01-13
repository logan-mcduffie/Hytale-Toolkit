package org.bouncycastle.cert.jcajce;

import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v2CRLBuilder;

public class JcaX509v2CRLBuilder extends X509v2CRLBuilder {
   public JcaX509v2CRLBuilder(X500Principal var1, Date var2) {
      super(X500Name.getInstance(var1.getEncoded()), var2);
   }

   public JcaX509v2CRLBuilder(X509Certificate var1, Date var2) {
      this(var1.getSubjectX500Principal(), var2);
   }

   public JcaX509v2CRLBuilder(X509CRL var1) throws CRLException {
      super(new JcaX509CRLHolder(var1));
   }
}
