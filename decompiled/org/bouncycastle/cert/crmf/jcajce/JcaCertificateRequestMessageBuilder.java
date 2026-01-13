package org.bouncycastle.cert.crmf.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;

public class JcaCertificateRequestMessageBuilder extends CertificateRequestMessageBuilder {
   public JcaCertificateRequestMessageBuilder(BigInteger var1) {
      super(var1);
   }

   public JcaCertificateRequestMessageBuilder setIssuer(X500Principal var1) {
      if (var1 != null) {
         this.setIssuer(X500Name.getInstance(var1.getEncoded()));
      }

      return this;
   }

   public JcaCertificateRequestMessageBuilder setSubject(X500Principal var1) {
      if (var1 != null) {
         this.setSubject(X500Name.getInstance(var1.getEncoded()));
      }

      return this;
   }

   public JcaCertificateRequestMessageBuilder setAuthInfoSender(X500Principal var1) {
      if (var1 != null) {
         this.setAuthInfoSender(new GeneralName(X500Name.getInstance(var1.getEncoded())));
      }

      return this;
   }

   public JcaCertificateRequestMessageBuilder setPublicKey(PublicKey var1) {
      this.setPublicKey(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
      return this;
   }
}
