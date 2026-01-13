package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;

public class TimeStampResp extends ASN1Object {
   PKIStatusInfo pkiStatusInfo;
   ContentInfo timeStampToken;

   public static TimeStampResp getInstance(Object var0) {
      if (var0 instanceof TimeStampResp) {
         return (TimeStampResp)var0;
      } else {
         return var0 != null ? new TimeStampResp(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private TimeStampResp(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.pkiStatusInfo = PKIStatusInfo.getInstance(var2.nextElement());
      if (var2.hasMoreElements()) {
         this.timeStampToken = ContentInfo.getInstance(var2.nextElement());
      }
   }

   public TimeStampResp(PKIStatusInfo var1, ContentInfo var2) {
      this.pkiStatusInfo = var1;
      this.timeStampToken = var2;
   }

   public PKIStatusInfo getStatus() {
      return this.pkiStatusInfo;
   }

   public ContentInfo getTimeStampToken() {
      return this.timeStampToken;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.pkiStatusInfo);
      if (this.timeStampToken != null) {
         var1.add(this.timeStampToken);
      }

      return new DERSequence(var1);
   }
}
