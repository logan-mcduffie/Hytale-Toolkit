package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class Elevation extends UINT16 {
   public Elevation(UINT16 var1) {
      super(var1.getValue());
   }

   public Elevation(BigInteger var1) {
      super(var1);
   }

   public Elevation(int var1) {
      super(var1);
   }

   public Elevation(long var1) {
      super(var1);
   }

   protected Elevation(ASN1Integer var1) {
      super(var1);
   }

   public static Elevation getInstance(Object var0) {
      if (var0 instanceof Elevation) {
         return (Elevation)var0;
      } else {
         return var0 != null ? new Elevation(UINT16.getInstance(var0)) : null;
      }
   }
}
