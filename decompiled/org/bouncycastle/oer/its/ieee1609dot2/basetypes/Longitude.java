package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class Longitude extends OneEightyDegreeInt {
   public Longitude(long var1) {
      super(var1);
   }

   public Longitude(BigInteger var1) {
      super(var1);
   }

   private Longitude(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static Longitude getInstance(Object var0) {
      if (var0 instanceof Longitude) {
         return (Longitude)var0;
      } else {
         return var0 != null ? new Longitude(ASN1Integer.getInstance(var0)) : null;
      }
   }
}
