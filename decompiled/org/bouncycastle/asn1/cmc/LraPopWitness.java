package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class LraPopWitness extends ASN1Object {
   private final BodyPartID pkiDataBodyid;
   private final ASN1Sequence bodyIds;

   public LraPopWitness(BodyPartID var1, ASN1Sequence var2) {
      this.pkiDataBodyid = var1;
      this.bodyIds = var2;
   }

   private LraPopWitness(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.pkiDataBodyid = BodyPartID.getInstance(var1.getObjectAt(0));
         this.bodyIds = ASN1Sequence.getInstance(var1.getObjectAt(1));
      }
   }

   public static LraPopWitness getInstance(Object var0) {
      if (var0 instanceof LraPopWitness) {
         return (LraPopWitness)var0;
      } else {
         return var0 != null ? new LraPopWitness(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public BodyPartID getPkiDataBodyid() {
      return this.pkiDataBodyid;
   }

   public BodyPartID[] getBodyIds() {
      BodyPartID[] var1 = new BodyPartID[this.bodyIds.size()];

      for (int var2 = 0; var2 != this.bodyIds.size(); var2++) {
         var1[var2] = BodyPartID.getInstance(this.bodyIds.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.pkiDataBodyid, this.bodyIds);
   }
}
