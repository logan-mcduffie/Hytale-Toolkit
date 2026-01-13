package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class AuthorizationRequestMessage extends EtsiTs103097DataEncryptedUnicast {
   public AuthorizationRequestMessage(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected AuthorizationRequestMessage(ASN1Sequence var1) {
      super(var1);
   }

   public static AuthorizationRequestMessage getInstance(Object var0) {
      if (var0 instanceof AuthorizationRequestMessage) {
         return (AuthorizationRequestMessage)var0;
      } else {
         return var0 != null ? new AuthorizationRequestMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
