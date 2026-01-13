package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class UINT8 extends UintBase {
   private static final BigInteger MAX = BigInteger.valueOf(255L);

   public UINT8(BigInteger var1) {
      super(var1);
   }

   public UINT8(int var1) {
      super(var1);
   }

   public UINT8(long var1) {
      super(var1);
   }

   protected UINT8(ASN1Integer var1) {
      super(var1);
   }

   public static UINT8 getInstance(Object var0) {
      if (var0 instanceof UINT8) {
         return (UINT8)var0;
      } else {
         return var0 != null ? new UINT8(ASN1Integer.getInstance(var0)) : null;
      }
   }

   @Override
   protected void assertLimit() {
      if (this.value.signum() < 0) {
         throw new IllegalArgumentException("value must not be negative");
      } else if (this.value.compareTo(MAX) > 0) {
         throw new IllegalArgumentException("value 0x" + this.value.toString(16) + "  must not exceed 0x" + MAX.toString(16));
      }
   }
}
