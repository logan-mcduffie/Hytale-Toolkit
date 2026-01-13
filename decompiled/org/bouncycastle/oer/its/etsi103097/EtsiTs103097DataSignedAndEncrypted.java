package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EtsiTs103097DataSignedAndEncrypted extends EtsiTs103097Data {
   public EtsiTs103097DataSignedAndEncrypted(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected EtsiTs103097DataSignedAndEncrypted(ASN1Sequence var1) {
      super(var1);
   }

   public static EtsiTs103097DataSignedAndEncrypted getInstance(Object var0) {
      if (var0 instanceof EtsiTs103097DataSignedAndEncrypted) {
         return (EtsiTs103097DataSignedAndEncrypted)var0;
      } else {
         return var0 != null ? new EtsiTs103097DataSignedAndEncrypted(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
