package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Data extends Ieee1609Dot2Data {
   public EtsiTs103097Data(Ieee1609Dot2Content var1) {
      super(new UINT8(3), var1);
   }

   public EtsiTs103097Data(UINT8 var1, Ieee1609Dot2Content var2) {
      super(var1, var2);
   }

   protected EtsiTs103097Data(ASN1Sequence var1) {
      super(var1);
   }

   public static EtsiTs103097Data getInstance(Object var0) {
      if (var0 instanceof EtsiTs103097Data) {
         return (EtsiTs103097Data)var0;
      } else {
         return var0 != null ? new EtsiTs103097Data(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
