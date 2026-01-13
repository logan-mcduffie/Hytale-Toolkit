package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class RevReqContent extends ASN1Object {
   private final ASN1Sequence content;

   private RevReqContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public RevReqContent(RevDetails var1) {
      this.content = new DERSequence(var1);
   }

   public RevReqContent(RevDetails[] var1) {
      this.content = new DERSequence(var1);
   }

   public static RevReqContent getInstance(Object var0) {
      if (var0 instanceof RevReqContent) {
         return (RevReqContent)var0;
      } else {
         return var0 != null ? new RevReqContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RevDetails[] toRevDetailsArray() {
      RevDetails[] var1 = new RevDetails[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = RevDetails.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
