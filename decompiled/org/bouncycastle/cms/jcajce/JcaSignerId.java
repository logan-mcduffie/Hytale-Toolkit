package org.bouncycastle.cms.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.SignerId;

public class JcaSignerId extends SignerId {
   public JcaSignerId(X509Certificate var1) {
      super(convertPrincipal(var1.getIssuerX500Principal()), var1.getSerialNumber(), CMSUtils.getSubjectKeyId(var1));
   }

   public JcaSignerId(X500Principal var1, BigInteger var2) {
      super(convertPrincipal(var1), var2);
   }

   public JcaSignerId(X500Principal var1, BigInteger var2, byte[] var3) {
      super(convertPrincipal(var1), var2, var3);
   }

   private static X500Name convertPrincipal(X500Principal var0) {
      return var0 == null ? null : X500Name.getInstance(var0.getEncoded());
   }
}
