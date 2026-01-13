package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class EtsiTs102941DeltaCtlRequest extends EtsiTs102941CtlRequest {
   private EtsiTs102941DeltaCtlRequest(ASN1Sequence var1) {
      super(var1);
   }

   public EtsiTs102941DeltaCtlRequest(EtsiTs102941CtlRequest var1) {
      super(var1.getIssuerId(), var1.getLastKnownCtlSequence());
   }

   public EtsiTs102941DeltaCtlRequest(HashedId8 var1, ASN1Integer var2) {
      super(var1, var2);
   }

   public static EtsiTs102941DeltaCtlRequest getInstance(Object var0) {
      if (var0 instanceof EtsiTs102941DeltaCtlRequest) {
         return (EtsiTs102941DeltaCtlRequest)var0;
      } else {
         return var0 != null ? new EtsiTs102941DeltaCtlRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
