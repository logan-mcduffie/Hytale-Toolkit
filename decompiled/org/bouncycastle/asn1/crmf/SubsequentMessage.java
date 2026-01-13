package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Integer;

public class SubsequentMessage extends ASN1Integer {
   public static final SubsequentMessage encrCert = new SubsequentMessage(0);
   public static final SubsequentMessage challengeResp = new SubsequentMessage(1);

   private SubsequentMessage(int var1) {
      super(var1);
   }

   public static SubsequentMessage valueOf(int var0) {
      if (var0 == 0) {
         return encrCert;
      } else if (var0 == 1) {
         return challengeResp;
      } else {
         throw new IllegalArgumentException("unknown value: " + var0);
      }
   }
}
