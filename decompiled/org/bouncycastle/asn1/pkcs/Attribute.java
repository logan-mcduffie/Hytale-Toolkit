package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;

public class Attribute extends ASN1Object {
   private ASN1ObjectIdentifier attrType;
   private ASN1Set attrValues;

   public static Attribute getInstance(Object var0) {
      if (var0 == null || var0 instanceof Attribute) {
         return (Attribute)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new Attribute((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public Attribute(ASN1Sequence var1) {
      this.attrType = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      this.attrValues = (ASN1Set)var1.getObjectAt(1);
   }

   public Attribute(ASN1ObjectIdentifier var1, ASN1Set var2) {
      this.attrType = var1;
      this.attrValues = var2;
   }

   public ASN1ObjectIdentifier getAttrType() {
      return this.attrType;
   }

   public ASN1Set getAttrValues() {
      return this.attrValues;
   }

   public ASN1Encodable[] getAttributeValues() {
      return this.attrValues.toArray();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.attrType, this.attrValues);
   }
}
