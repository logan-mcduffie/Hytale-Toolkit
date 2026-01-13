package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EnrolmentRequestMessage extends EtsiTs103097DataSignedAndEncryptedUnicast {
   public EnrolmentRequestMessage(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected EnrolmentRequestMessage(ASN1Sequence var1) {
      super(var1);
   }

   public static EnrolmentRequestMessage getInstance(Object var0) {
      if (var0 instanceof EnrolmentRequestMessage) {
         return (EnrolmentRequestMessage)var0;
      } else {
         return var0 != null ? new EnrolmentRequestMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
