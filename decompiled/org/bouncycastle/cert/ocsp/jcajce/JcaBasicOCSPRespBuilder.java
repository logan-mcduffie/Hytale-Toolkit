package org.bouncycastle.cert.ocsp.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;

public class JcaBasicOCSPRespBuilder extends BasicOCSPRespBuilder {
   public JcaBasicOCSPRespBuilder(X500Principal var1) {
      super(new JcaRespID(var1));
   }

   public JcaBasicOCSPRespBuilder(PublicKey var1, DigestCalculator var2) throws OCSPException {
      super(SubjectPublicKeyInfo.getInstance(var1.getEncoded()), var2);
   }
}
