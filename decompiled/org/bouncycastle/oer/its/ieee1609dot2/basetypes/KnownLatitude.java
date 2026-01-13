package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class KnownLatitude extends NinetyDegreeInt {
   public KnownLatitude(long var1) {
      super(var1);
   }

   public KnownLatitude(BigInteger var1) {
      super(var1);
   }

   private KnownLatitude(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static KnownLatitude getInstance(Object var0) {
      if (var0 instanceof KnownLatitude) {
         return (KnownLatitude)var0;
      } else {
         return var0 != null ? new KnownLatitude(ASN1Integer.getInstance(var0)) : null;
      }
   }
}
