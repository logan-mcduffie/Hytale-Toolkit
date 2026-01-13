package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.CertificateList;

public class TimeStampAndCRL extends ASN1Object {
   private ContentInfo timeStamp;
   private CertificateList crl;

   public TimeStampAndCRL(ContentInfo var1) {
      this(var1, null);
   }

   public TimeStampAndCRL(ContentInfo var1, CertificateList var2) {
      this.timeStamp = var1;
      this.crl = var2;
   }

   private TimeStampAndCRL(ASN1Sequence var1) {
      this.timeStamp = ContentInfo.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.crl = CertificateList.getInstance(var1.getObjectAt(1));
      }
   }

   public static TimeStampAndCRL getInstance(Object var0) {
      if (var0 instanceof TimeStampAndCRL) {
         return (TimeStampAndCRL)var0;
      } else {
         return var0 != null ? new TimeStampAndCRL(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ContentInfo getTimeStampToken() {
      return this.timeStamp;
   }

   /** @deprecated */
   public CertificateList getCertificateList() {
      return this.crl;
   }

   public CertificateList getCRL() {
      return this.crl;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.timeStamp);
      if (this.crl != null) {
         var1.add(this.crl);
      }

      return new DERSequence(var1);
   }
}
