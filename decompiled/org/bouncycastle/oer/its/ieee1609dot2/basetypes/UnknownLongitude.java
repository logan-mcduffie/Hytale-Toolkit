package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;

public class UnknownLongitude extends Longitude {
   public static final UnknownLongitude INSTANCE = new UnknownLongitude();

   public UnknownLongitude() {
      super(1800000001L);
   }

   public static UnknownLongitude getInstance(Object var0) {
      if (var0 instanceof UnknownLongitude) {
         return (UnknownLongitude)var0;
      } else if (var0 != null) {
         ASN1Integer var1 = ASN1Integer.getInstance(var0);
         if (var1.getValue().intValue() != 1800000001) {
            throw new IllegalArgumentException("value " + var1.getValue() + " is not 1800000001");
         } else {
            return INSTANCE;
         }
      } else {
         return null;
      }
   }
}
