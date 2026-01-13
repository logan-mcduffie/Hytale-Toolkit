package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.cert.X509CertificateHolder;

public class CertificateRepMessage {
   private final CertResponse[] resps;
   private final CMPCertificate[] caCerts;

   public CertificateRepMessage(CertRepMessage var1) {
      this.resps = var1.getResponse();
      this.caCerts = var1.getCaPubs();
   }

   public static CertificateRepMessage fromPKIBody(PKIBody var0) {
      if (!isCertificateRepMessage(var0.getType())) {
         throw new IllegalArgumentException("content of PKIBody wrong type: " + var0.getType());
      } else {
         return new CertificateRepMessage(CertRepMessage.getInstance(var0.getContent()));
      }
   }

   public static boolean isCertificateRepMessage(int var0) {
      switch (var0) {
         case 1:
         case 3:
         case 8:
         case 14:
            return true;
         default:
            return false;
      }
   }

   public CertificateResponse[] getResponses() {
      CertificateResponse[] var1 = new CertificateResponse[this.resps.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = new CertificateResponse(this.resps[var2]);
      }

      return var1;
   }

   public X509CertificateHolder[] getX509Certificates() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 != this.caCerts.length; var2++) {
         if (this.caCerts[var2].isX509v3PKCert()) {
            var1.add(new X509CertificateHolder(this.caCerts[var2].getX509v3PKCert()));
         }
      }

      return var1.toArray(new X509CertificateHolder[0]);
   }

   public boolean isOnlyX509PKCertificates() {
      boolean var1 = true;

      for (int var2 = 0; var2 != this.caCerts.length; var2++) {
         var1 &= this.caCerts[var2].isX509v3PKCert();
      }

      return var1;
   }

   public CMPCertificate[] getCMPCertificates() {
      CMPCertificate[] var1 = new CMPCertificate[this.caCerts.length];
      System.arraycopy(this.caCerts, 0, var1, 0, var1.length);
      return var1;
   }

   public CertRepMessage toASN1Structure() {
      return new CertRepMessage(this.caCerts, this.resps);
   }
}
