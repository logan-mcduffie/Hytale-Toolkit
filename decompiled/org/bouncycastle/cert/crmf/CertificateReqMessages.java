package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class CertificateReqMessages {
   private final CertReqMsg[] reqs;

   public CertificateReqMessages(CertReqMessages var1) {
      this.reqs = var1.toCertReqMsgArray();
   }

   public static CertificateReqMessages fromPKIBody(PKIBody var0) {
      if (!isCertificateRequestMessages(var0.getType())) {
         throw new IllegalArgumentException("content of PKIBody wrong type: " + var0.getType());
      } else {
         return new CertificateReqMessages(CertReqMessages.getInstance(var0.getContent()));
      }
   }

   public static boolean isCertificateRequestMessages(int var0) {
      switch (var0) {
         case 0:
         case 2:
         case 7:
         case 9:
         case 13:
            return true;
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         case 10:
         case 11:
         case 12:
         default:
            return false;
      }
   }

   public CertificateRequestMessage[] getRequests() {
      CertificateRequestMessage[] var1 = new CertificateRequestMessage[this.reqs.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = new CertificateRequestMessage(this.reqs[var2]);
      }

      return var1;
   }

   public CertReqMessages toASN1Structure() {
      return new CertReqMessages(this.reqs);
   }
}
