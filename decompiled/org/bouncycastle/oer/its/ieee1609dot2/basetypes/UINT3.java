package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class UINT3 extends UintBase {
   private static final BigInteger MAX = BigInteger.valueOf(7L);

   public UINT3(BigInteger var1) {
      super(var1);
   }

   public UINT3(int var1) {
      super(var1);
   }

   public UINT3(long var1) {
      super(var1);
   }

   protected UINT3(ASN1Integer var1) {
      super(var1);
   }

   public static UINT3 getInstance(Object var0) {
      if (var0 instanceof UINT3) {
         return (UINT3)var0;
      } else {
         return var0 != null ? new UINT3(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   protected void assertLimit() {
      if (this.value.signum() < 0) {
         throw new IllegalArgumentException("value must not be negative");
      } else if (this.value.compareTo(MAX) > 0) {
         throw new IllegalArgumentException("value must not exceed " + MAX.toString(16));
      }
   }
}
