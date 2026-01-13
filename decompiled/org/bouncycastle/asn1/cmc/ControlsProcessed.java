package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ControlsProcessed extends ASN1Object {
   private final ASN1Sequence bodyPartReferences;

   public ControlsProcessed(BodyPartReference var1) {
      this.bodyPartReferences = new DERSequence(var1);
   }

   public ControlsProcessed(BodyPartReference[] var1) {
      this.bodyPartReferences = new DERSequence(var1);
   }

   public static ControlsProcessed getInstance(Object var0) {
      if (var0 instanceof ControlsProcessed) {
         return (ControlsProcessed)var0;
      } else {
         return var0 != null ? new ControlsProcessed(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ControlsProcessed(ASN1Sequence var1) {
      if (var1.size() != 1) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.bodyPartReferences = ASN1Sequence.getInstance(var1.getObjectAt(0));
      }
   }

   public BodyPartReference[] getBodyList() {
      BodyPartReference[] var1 = new BodyPartReference[this.bodyPartReferences.size()];

      for (int var2 = 0; var2 != this.bodyPartReferences.size(); var2++) {
         var1[var2] = BodyPartReference.getInstance(this.bodyPartReferences.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.bodyPartReferences);
   }
}
