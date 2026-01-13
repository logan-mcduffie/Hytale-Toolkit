package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class PreSharedKeyRecipientInfo extends HashedId8 {
   public PreSharedKeyRecipientInfo(byte[] var1) {
      super(var1);
   }

   public static PreSharedKeyRecipientInfo getInstance(Object var0) {
      if (var0 instanceof PreSharedKeyRecipientInfo) {
         return (PreSharedKeyRecipientInfo)var0;
      } else if (var0 != null) {
         return var0 instanceof HashedId
            ? new PreSharedKeyRecipientInfo(((HashedId)var0).getHashBytes())
            : new PreSharedKeyRecipientInfo(ASN1OctetString.getInstance(var0).getOctets());
      } else {
         return null;
      }
   }
}
