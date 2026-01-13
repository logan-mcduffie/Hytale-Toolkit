package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.cert.X509CertificateHolder;

public class CertificateRepMessageBuilder {
   private final List<CertResponse> responses = new ArrayList<>();
   private final CMPCertificate[] caCerts;

   public CertificateRepMessageBuilder(X509CertificateHolder... var1) {
      this.caCerts = new CMPCertificate[var1.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         this.caCerts[var2] = new CMPCertificate(var1[var2].toASN1Structure());
      }
   }

   public CertificateRepMessageBuilder addCertificateResponse(CertificateResponse var1) {
      this.responses.add(var1.toASN1Structure());
      return this;
   }

   public CertificateRepMessage build() {
      CertRepMessage var1;
      if (this.caCerts.length != 0) {
         var1 = new CertRepMessage(this.caCerts, this.responses.toArray(new CertResponse[0]));
      } else {
         var1 = new CertRepMessage(null, this.responses.toArray(new CertResponse[0]));
      }

      this.responses.clear();
      return new CertificateRepMessage(var1);
   }
}
