package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class Latitude extends NinetyDegreeInt {
   public Latitude(long var1) {
      super(var1);
   }

   public Latitude(BigInteger var1) {
      super(var1);
   }

   private Latitude(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static Latitude getInstance(Object var0) {
      if (var0 instanceof Latitude) {
         return (Latitude)var0;
      } else {
         return var0 != null ? new Latitude(ASN1Integer.getInstance(var0)) : null;
      }
   }
}
