package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;

public class PolicyQualifierInfo extends ASN1Object {
   private ASN1ObjectIdentifier policyQualifierId;
   private ASN1Encodable qualifier;

   public PolicyQualifierInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.policyQualifierId = var1;
      this.qualifier = var2;
   }

   public PolicyQualifierInfo(String var1) {
      this.policyQualifierId = PolicyQualifierId.id_qt_cps;
      this.qualifier = new DERIA5String(var1);
   }

   /** @deprecated */
   public PolicyQualifierInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.policyQualifierId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.qualifier = var1.getObjectAt(1);
      }
   }

   public static PolicyQualifierInfo getInstance(Object var0) {
      if (var0 instanceof PolicyQualifierInfo) {
         return (PolicyQualifierInfo)var0;
      } else {
         return var0 != null ? new PolicyQualifierInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getPolicyQualifierId() {
      return this.policyQualifierId;
   }

   public ASN1Encodable getQualifier() {
      return this.qualifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.policyQualifierId, this.qualifier);
   }

   @Override
   public String toString() {
      return "PolicyQualifierInfo[" + this.policyQualifierId + ", " + this.qualifier + "]";
   }
}
