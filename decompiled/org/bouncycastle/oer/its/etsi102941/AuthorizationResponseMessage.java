package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class AuthorizationResponseMessage extends EtsiTs103097DataEncryptedUnicast {
   public AuthorizationResponseMessage(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected AuthorizationResponseMessage(ASN1Sequence var1) {
      super(var1);
   }

   public static AuthorizationResponseMessage getInstance(Object var0) {
      if (var0 instanceof AuthorizationResponseMessage) {
         return (AuthorizationResponseMessage)var0;
      } else {
         return var0 != null ? new AuthorizationResponseMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
