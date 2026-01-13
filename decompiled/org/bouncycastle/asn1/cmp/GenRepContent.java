package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class GenRepContent extends ASN1Object {
   private final ASN1Sequence content;

   private GenRepContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public GenRepContent(InfoTypeAndValue var1) {
      this.content = new DERSequence(var1);
   }

   public GenRepContent(InfoTypeAndValue[] var1) {
      this.content = new DERSequence(var1);
   }

   public static GenRepContent getInstance(Object var0) {
      if (var0 instanceof GenRepContent) {
         return (GenRepContent)var0;
      } else {
         return var0 != null ? new GenRepContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public InfoTypeAndValue[] toInfoTypeAndValueArray() {
      InfoTypeAndValue[] var1 = new InfoTypeAndValue[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = InfoTypeAndValue.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
