package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AttCertValidityPeriod extends ASN1Object {
   ASN1GeneralizedTime notBeforeTime;
   ASN1GeneralizedTime notAfterTime;

   public static AttCertValidityPeriod getInstance(Object var0) {
      if (var0 instanceof AttCertValidityPeriod) {
         return (AttCertValidityPeriod)var0;
      } else {
         return var0 != null ? new AttCertValidityPeriod(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private AttCertValidityPeriod(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.notBeforeTime = ASN1GeneralizedTime.getInstance(var1.getObjectAt(0));
         this.notAfterTime = ASN1GeneralizedTime.getInstance(var1.getObjectAt(1));
      }
   }

   public AttCertValidityPeriod(ASN1GeneralizedTime var1, ASN1GeneralizedTime var2) {
      this.notBeforeTime = var1;
      this.notAfterTime = var2;
   }

   public ASN1GeneralizedTime getNotBeforeTime() {
      return this.notBeforeTime;
   }

   public ASN1GeneralizedTime getNotAfterTime() {
      return this.notAfterTime;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.notBeforeTime, this.notAfterTime);
   }
}
