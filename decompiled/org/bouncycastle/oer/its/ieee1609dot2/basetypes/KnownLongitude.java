package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class KnownLongitude extends Longitude {
   public KnownLongitude(long var1) {
      super(var1);
   }

   public KnownLongitude(BigInteger var1) {
      super(var1);
   }

   private KnownLongitude(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static KnownLongitude getInstance(Object var0) {
      if (var0 instanceof KnownLongitude) {
         return (KnownLongitude)var0;
      } else {
         return var0 != null ? new KnownLongitude(ASN1Integer.getInstance(var0)) : null;
      }
   }
}
