package org.bouncycastle.cert.ocsp.jcajce;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.operator.DigestCalculator;

public class JcaCertificateID extends CertificateID {
   public JcaCertificateID(DigestCalculator var1, X509Certificate var2, BigInteger var3) throws OCSPException, CertificateEncodingException {
      super(var1, new JcaX509CertificateHolder(var2), var3);
   }
}
