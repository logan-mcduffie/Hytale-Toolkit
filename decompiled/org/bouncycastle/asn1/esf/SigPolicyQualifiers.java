package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SigPolicyQualifiers extends ASN1Object {
   ASN1Sequence qualifiers;

   public static SigPolicyQualifiers getInstance(Object var0) {
      if (var0 instanceof SigPolicyQualifiers) {
         return (SigPolicyQualifiers)var0;
      } else {
         return var0 instanceof ASN1Sequence ? new SigPolicyQualifiers(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SigPolicyQualifiers(ASN1Sequence var1) {
      this.qualifiers = var1;
   }

   public SigPolicyQualifiers(SigPolicyQualifierInfo[] var1) {
      this.qualifiers = new DERSequence(var1);
   }

   public int size() {
      return this.qualifiers.size();
   }

   public SigPolicyQualifierInfo getInfoAt(int var1) {
      return SigPolicyQualifierInfo.getInstance(this.qualifiers.getObjectAt(var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.qualifiers;
   }
}
