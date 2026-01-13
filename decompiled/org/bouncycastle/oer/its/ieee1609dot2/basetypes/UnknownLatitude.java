package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;

public class UnknownLatitude extends Latitude {
   public static UnknownLatitude INSTANCE = new UnknownLatitude();

   private UnknownLatitude() {
      super(900000001L);
   }

   public static UnknownLatitude getInstance(Object var0) {
      if (var0 instanceof UnknownLatitude) {
         return (UnknownLatitude)var0;
      } else if (var0 != null) {
         ASN1Integer var1 = ASN1Integer.getInstance(var0);
         if (var1.getValue().intValue() != 900000001) {
            throw new IllegalArgumentException("value " + var1.getValue() + " is not unknown value of 900000001");
         } else {
            return INSTANCE;
         }
      } else {
         return null;
      }
   }
}
