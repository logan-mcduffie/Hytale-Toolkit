package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class CrlSeries extends UINT16 {
   public CrlSeries(int var1) {
      super(var1);
   }

   public CrlSeries(BigInteger var1) {
      super(var1);
   }

   public static CrlSeries getInstance(Object var0) {
      if (var0 instanceof CrlSeries) {
         return (CrlSeries)var0;
      } else {
         return var0 != null ? new CrlSeries(ASN1Integer.getInstance(var0).getValue()) : null;
      }
   }
}
