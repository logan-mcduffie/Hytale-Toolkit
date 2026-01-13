package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SigPolicyQualifierInfo extends ASN1Object {
   private ASN1ObjectIdentifier sigPolicyQualifierId;
   private ASN1Encodable sigQualifier;

   public SigPolicyQualifierInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.sigPolicyQualifierId = var1;
      this.sigQualifier = var2;
   }

   private SigPolicyQualifierInfo(ASN1Sequence var1) {
      this.sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.sigQualifier = var1.getObjectAt(1);
   }

   public static SigPolicyQualifierInfo getInstance(Object var0) {
      if (var0 instanceof SigPolicyQualifierInfo) {
         return (SigPolicyQualifierInfo)var0;
      } else {
         return var0 != null ? new SigPolicyQualifierInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getSigPolicyQualifierId() {
      return new ASN1ObjectIdentifier(this.sigPolicyQualifierId.getId());
   }

   public ASN1Encodable getSigQualifier() {
      return this.sigQualifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.sigPolicyQualifierId);
      var1.add(this.sigQualifier);
      return new DERSequence(var1);
   }
}
