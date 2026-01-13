package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherKeyAttribute extends ASN1Object {
   private ASN1ObjectIdentifier keyAttrId;
   private ASN1Encodable keyAttr;

   public static OtherKeyAttribute getInstance(Object var0) {
      if (var0 instanceof OtherKeyAttribute) {
         return (OtherKeyAttribute)var0;
      } else {
         return var0 != null ? new OtherKeyAttribute(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherKeyAttribute(ASN1Sequence var1) {
      this.keyAttrId = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      if (var1.size() > 1) {
         this.keyAttr = var1.getObjectAt(1);
      }
   }

   public OtherKeyAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.keyAttrId = var1;
      this.keyAttr = var2;
   }

   public ASN1ObjectIdentifier getKeyAttrId() {
      return this.keyAttrId;
   }

   public ASN1Encodable getKeyAttr() {
      return this.keyAttr;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.keyAttrId);
      if (this.keyAttr != null) {
         var1.add(this.keyAttr);
      }

      return new DERSequence(var1);
   }
}
