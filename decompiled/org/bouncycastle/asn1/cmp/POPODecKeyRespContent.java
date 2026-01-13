package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class POPODecKeyRespContent extends ASN1Object {
   private final ASN1Sequence content;

   private POPODecKeyRespContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public static POPODecKeyRespContent getInstance(Object var0) {
      if (var0 instanceof POPODecKeyRespContent) {
         return (POPODecKeyRespContent)var0;
      } else {
         return var0 != null ? new POPODecKeyRespContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer[] toASN1IntegerArray() {
      ASN1Integer[] var1 = new ASN1Integer[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = ASN1Integer.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
