package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CounterSignature extends Ieee1609Dot2Data {
   public CounterSignature(UINT8 var1, Ieee1609Dot2Content var2) {
      super(var1, var2);
   }

   protected CounterSignature(ASN1Sequence var1) {
      super(var1);
   }

   public static Ieee1609Dot2Data getInstance(Object var0) {
      if (var0 instanceof Ieee1609Dot2Data) {
         return (Ieee1609Dot2Data)var0;
      } else {
         return var0 != null ? new CounterSignature(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
