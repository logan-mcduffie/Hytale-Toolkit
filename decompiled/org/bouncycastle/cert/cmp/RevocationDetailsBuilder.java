package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class RevocationDetailsBuilder {
   private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();

   public RevocationDetailsBuilder setPublicKey(SubjectPublicKeyInfo var1) {
      if (var1 != null) {
         this.templateBuilder.setPublicKey(var1);
      }

      return this;
   }

   public RevocationDetailsBuilder setIssuer(X500Name var1) {
      if (var1 != null) {
         this.templateBuilder.setIssuer(var1);
      }

      return this;
   }

   public RevocationDetailsBuilder setSerialNumber(BigInteger var1) {
      if (var1 != null) {
         this.templateBuilder.setSerialNumber(new ASN1Integer(var1));
      }

      return this;
   }

   public RevocationDetailsBuilder setSubject(X500Name var1) {
      if (var1 != null) {
         this.templateBuilder.setSubject(var1);
      }

      return this;
   }

   public RevocationDetails build() {
      return new RevocationDetails(new RevDetails(this.templateBuilder.build()));
   }
}
