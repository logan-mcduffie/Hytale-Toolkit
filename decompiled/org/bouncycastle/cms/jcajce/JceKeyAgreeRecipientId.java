package org.bouncycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.KeyAgreeRecipientId;

public class JceKeyAgreeRecipientId extends KeyAgreeRecipientId {
   public JceKeyAgreeRecipientId(X509Certificate var1) {
      this(var1.getIssuerX500Principal(), var1.getSerialNumber());
   }

   public JceKeyAgreeRecipientId(X500Principal var1, BigInteger var2) {
      super(X500Name.getInstance(var1.getEncoded()), var2);
   }
}
