package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class DeltaCtl extends CtlFormat {
   public DeltaCtl(Version var1, Time32 var2, UINT8 var3, SequenceOfCtlCommand var4) {
      super(var1, var2, ASN1Boolean.FALSE, var3, var4);
   }

   private DeltaCtl(ASN1Sequence var1) {
      super(var1);
   }

   public static DeltaCtl getInstance(Object var0) {
      if (var0 instanceof DeltaCtl) {
         return (DeltaCtl)var0;
      } else {
         return var0 != null ? new DeltaCtl(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
