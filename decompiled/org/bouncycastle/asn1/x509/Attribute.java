package org.bouncycastle.asn1.x509;

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
      if (var0 instanceof Attribute) {
         return (Attribute)var0;
      } else {
         return var0 != null ? new Attribute(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private Attribute(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.attrType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.attrValues = ASN1Set.getInstance(var1.getObjectAt(1));
      }
   }

   public Attribute(ASN1ObjectIdentifier var1, ASN1Set var2) {
      this.attrType = var1;
      this.attrValues = var2;
   }

   public ASN1ObjectIdentifier getAttrType() {
      return new ASN1ObjectIdentifier(this.attrType.getId());
   }

   public ASN1Encodable[] getAttributeValues() {
      return this.attrValues.toArray();
   }

   public ASN1Set getAttrValues() {
      return this.attrValues;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.attrType, this.attrValues);
   }
}
