package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ToBeSignedRcaCtl extends CtlFormat {
   public ToBeSignedRcaCtl(Version var1, Time32 var2, ASN1Boolean var3, UINT8 var4, SequenceOfCtlCommand var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected ToBeSignedRcaCtl(ASN1Sequence var1) {
      super(var1);
   }

   public static ToBeSignedRcaCtl getInstance(Object var0) {
      if (var0 instanceof ToBeSignedRcaCtl) {
         return (ToBeSignedRcaCtl)var0;
      } else {
         return var0 != null ? new ToBeSignedRcaCtl(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
