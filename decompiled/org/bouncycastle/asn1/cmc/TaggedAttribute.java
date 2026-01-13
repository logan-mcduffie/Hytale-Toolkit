package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;

public class TaggedAttribute extends ASN1Object {
   private final BodyPartID bodyPartID;
   private final ASN1ObjectIdentifier attrType;
   private final ASN1Set attrValues;

   public static TaggedAttribute getInstance(Object var0) {
      if (var0 instanceof TaggedAttribute) {
         return (TaggedAttribute)var0;
      } else {
         return var0 != null ? new TaggedAttribute(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private TaggedAttribute(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.bodyPartID = BodyPartID.getInstance(var1.getObjectAt(0));
         this.attrType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(1));
         this.attrValues = ASN1Set.getInstance(var1.getObjectAt(2));
      }
   }

   public TaggedAttribute(BodyPartID var1, ASN1ObjectIdentifier var2, ASN1Set var3) {
      this.bodyPartID = var1;
      this.attrType = var2;
      this.attrValues = var3;
   }

   public BodyPartID getBodyPartID() {
      return this.bodyPartID;
   }

   public ASN1ObjectIdentifier getAttrType() {
      return this.attrType;
   }

   public ASN1Set getAttrValues() {
      return this.attrValues;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.bodyPartID, this.attrType, this.attrValues});
   }
}
