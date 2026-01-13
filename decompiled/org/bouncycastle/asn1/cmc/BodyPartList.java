package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class BodyPartList extends ASN1Object {
   private final BodyPartID[] bodyPartIDs;

   public static BodyPartList getInstance(Object var0) {
      if (var0 instanceof BodyPartList) {
         return (BodyPartList)var0;
      } else {
         return var0 != null ? new BodyPartList(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static BodyPartList getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public BodyPartList(BodyPartID var1) {
      this.bodyPartIDs = new BodyPartID[]{var1};
   }

   public BodyPartList(BodyPartID[] var1) {
      this.bodyPartIDs = Utils.clone(var1);
   }

   private BodyPartList(ASN1Sequence var1) {
      this.bodyPartIDs = Utils.toBodyPartIDArray(var1);
   }

   public BodyPartID[] getBodyPartIDs() {
      return Utils.clone(this.bodyPartIDs);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.bodyPartIDs);
   }
}
