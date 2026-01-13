package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class CertificateReqMessagesBuilder {
   private final List<CertReqMsg> requests = new ArrayList<>();

   public void addRequest(CertificateRequestMessage var1) {
      this.requests.add(var1.toASN1Structure());
   }

   public CertificateReqMessages build() {
      CertificateReqMessages var1 = new CertificateReqMessages(new CertReqMessages(this.requests.toArray(new CertReqMsg[0])));
      this.requests.clear();
      return var1;
   }
}
