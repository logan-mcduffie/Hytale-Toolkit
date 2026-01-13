package org.bouncycastle.cert.selector.jcajce;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaX509CertificateHolderSelector extends X509CertificateHolderSelector {
   public JcaX509CertificateHolderSelector(X509Certificate var1) {
      super(convertPrincipal(var1.getIssuerX500Principal()), var1.getSerialNumber(), getSubjectKeyId(var1));
   }

   public JcaX509CertificateHolderSelector(X500Principal var1, BigInteger var2) {
      super(convertPrincipal(var1), var2);
   }

   public JcaX509CertificateHolderSelector(X500Principal var1, BigInteger var2, byte[] var3) {
      super(convertPrincipal(var1), var2, var3);
   }

   private static X500Name convertPrincipal(X500Principal var0) {
      return var0 == null ? null : X500Name.getInstance(var0.getEncoded());
   }

   private static byte[] getSubjectKeyId(X509Certificate var0) {
      byte[] var1 = var0.getExtensionValue(Extension.subjectKeyIdentifier.getId());
      return var1 != null ? ASN1OctetString.getInstance(ASN1OctetString.getInstance(var1).getOctets()).getOctets() : null;
   }
}
