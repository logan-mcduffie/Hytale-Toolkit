package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EtsiTs103097DataSignedAndEncryptedUnicast extends EtsiTs103097Data {
   public EtsiTs103097DataSignedAndEncryptedUnicast(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected EtsiTs103097DataSignedAndEncryptedUnicast(ASN1Sequence var1) {
      super(var1);
   }

   public static EtsiTs103097DataSignedAndEncryptedUnicast getInstance(Object var0) {
      if (var0 instanceof EtsiTs103097DataSignedAndEncryptedUnicast) {
         return (EtsiTs103097DataSignedAndEncryptedUnicast)var0;
      } else {
         return var0 != null ? new EtsiTs103097DataSignedAndEncryptedUnicast(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
