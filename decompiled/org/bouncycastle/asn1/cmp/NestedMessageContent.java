package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Sequence;

public class NestedMessageContent extends PKIMessages {
   public NestedMessageContent(PKIMessage var1) {
      super(var1);
   }

   public NestedMessageContent(PKIMessage[] var1) {
      super(var1);
   }

   public NestedMessageContent(ASN1Sequence var1) {
      super(var1);
   }

   public static NestedMessageContent getInstance(Object var0) {
      if (var0 instanceof NestedMessageContent) {
         return (NestedMessageContent)var0;
      } else {
         return var0 != null ? new NestedMessageContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
