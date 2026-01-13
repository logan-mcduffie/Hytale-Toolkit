package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.x509.CRLReason;

public class RevokedStatus implements CertificateStatus {
   RevokedInfo info;

   public RevokedStatus(RevokedInfo var1) {
      this.info = var1;
   }

   public RevokedStatus(Date var1) {
      this.info = new RevokedInfo(new ASN1GeneralizedTime(var1));
   }

   public RevokedStatus(Date var1, int var2) {
      this.info = new RevokedInfo(new ASN1GeneralizedTime(var1), CRLReason.lookup(var2));
   }

   public Date getRevocationTime() {
      return OCSPUtils.extractDate(this.info.getRevocationTime());
   }

   public boolean hasRevocationReason() {
      return this.info.getRevocationReason() != null;
   }

   public int getRevocationReason() {
      if (this.info.getRevocationReason() == null) {
         throw new IllegalStateException("attempt to get a reason where none is available");
      } else {
         return this.info.getRevocationReason().getValue().intValue();
      }
   }
}
