package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.SignerId;

public class JcaSelectorConverter {
   public SignerId getSignerId(X509CertSelector var1) {
      try {
         return var1.getSubjectKeyIdentifier() != null
            ? new SignerId(
               X500Name.getInstance(var1.getIssuerAsBytes()), var1.getSerialNumber(), ASN1OctetString.getInstance(var1.getSubjectKeyIdentifier()).getOctets()
            )
            : new SignerId(X500Name.getInstance(var1.getIssuerAsBytes()), var1.getSerialNumber());
      } catch (IOException var3) {
         throw new IllegalArgumentException("unable to convert issuer: " + var3.getMessage());
      }
   }

   public KeyTransRecipientId getKeyTransRecipientId(X509CertSelector var1) {
      try {
         return var1.getSubjectKeyIdentifier() != null
            ? new KeyTransRecipientId(
               X500Name.getInstance(var1.getIssuerAsBytes()), var1.getSerialNumber(), ASN1OctetString.getInstance(var1.getSubjectKeyIdentifier()).getOctets()
            )
            : new KeyTransRecipientId(X500Name.getInstance(var1.getIssuerAsBytes()), var1.getSerialNumber());
      } catch (IOException var3) {
         throw new IllegalArgumentException("unable to convert issuer: " + var3.getMessage());
      }
   }
}
