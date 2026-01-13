package org.bouncycastle.cert.crmf.jcajce;

import java.security.PrivateKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.crmf.PKIArchiveControlBuilder;

public class JcaPKIArchiveControlBuilder extends PKIArchiveControlBuilder {
   public JcaPKIArchiveControlBuilder(PrivateKey var1, X500Name var2) {
      this(var1, new GeneralName(var2));
   }

   public JcaPKIArchiveControlBuilder(PrivateKey var1, X500Principal var2) {
      this(var1, X500Name.getInstance(var2.getEncoded()));
   }

   public JcaPKIArchiveControlBuilder(PrivateKey var1, GeneralName var2) {
      super(PrivateKeyInfo.getInstance(var1.getEncoded()), var2);
   }
}
