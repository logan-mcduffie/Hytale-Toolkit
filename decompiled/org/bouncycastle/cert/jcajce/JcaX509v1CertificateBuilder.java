package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v1CertificateBuilder;

public class JcaX509v1CertificateBuilder extends X509v1CertificateBuilder {
   public JcaX509v1CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, PublicKey var6) {
      super(var1, var2, var3, var4, var5, SubjectPublicKeyInfo.getInstance(var6.getEncoded()));
   }

   public JcaX509v1CertificateBuilder(X500Principal var1, BigInteger var2, Date var3, Date var4, X500Principal var5, PublicKey var6) {
      super(
         X500Name.getInstance(var1.getEncoded()),
         var2,
         var3,
         var4,
         X500Name.getInstance(var5.getEncoded()),
         SubjectPublicKeyInfo.getInstance(var6.getEncoded())
      );
   }
}
