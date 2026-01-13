package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.Extensions;

public class RespData {
   private ResponseData data;

   public RespData(ResponseData var1) {
      this.data = var1;
   }

   public int getVersion() {
      return this.data.getVersion().intValueExact() + 1;
   }

   public RespID getResponderId() {
      return new RespID(this.data.getResponderID());
   }

   public Date getProducedAt() {
      return OCSPUtils.extractDate(this.data.getProducedAt());
   }

   public SingleResp[] getResponses() {
      ASN1Sequence var1 = this.data.getResponses();
      SingleResp[] var2 = new SingleResp[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = new SingleResp(SingleResponse.getInstance(var1.getObjectAt(var3)));
      }

      return var2;
   }

   public Extensions getResponseExtensions() {
      return this.data.getResponseExtensions();
   }
}
