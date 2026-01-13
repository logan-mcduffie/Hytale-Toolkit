package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class CertConfirmContent extends ASN1Object {
   private final ASN1Sequence content;

   private CertConfirmContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public static CertConfirmContent getInstance(Object var0) {
      if (var0 instanceof CertConfirmContent) {
         return (CertConfirmContent)var0;
      } else {
         return var0 != null ? new CertConfirmContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertStatus[] toCertStatusArray() {
      CertStatus[] var1 = new CertStatus[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = CertStatus.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
