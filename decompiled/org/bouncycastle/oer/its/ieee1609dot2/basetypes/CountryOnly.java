package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class CountryOnly extends UINT16 implements RegionInterface {
   public CountryOnly(int var1) {
      super(var1);
   }

   public CountryOnly(BigInteger var1) {
      super(var1);
   }

   public static CountryOnly getInstance(Object var0) {
      if (var0 instanceof CountryOnly) {
         return (CountryOnly)var0;
      } else {
         return var0 != null ? new CountryOnly(ASN1Integer.getInstance(var0).getValue()) : null;
      }
   }
}
