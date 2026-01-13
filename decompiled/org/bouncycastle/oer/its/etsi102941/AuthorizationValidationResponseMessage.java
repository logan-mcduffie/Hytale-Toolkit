package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class AuthorizationValidationResponseMessage extends EtsiTs103097DataSignedAndEncryptedUnicast {
   public AuthorizationValidationResponseMessage(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected AuthorizationValidationResponseMessage(ASN1Sequence var1) {
      super(var1);
   }

   public static AuthorizationValidationResponseMessage getInstance(Object var0) {
      if (var0 instanceof AuthorizationValidationResponseMessage) {
         return (AuthorizationValidationResponseMessage)var0;
      } else {
         return var0 != null ? new AuthorizationValidationResponseMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
