package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class UINT16 extends UintBase {
   private static final BigInteger MAX = BigInteger.valueOf(65535L);

   public UINT16(BigInteger var1) {
      super(var1);
   }

   public UINT16(int var1) {
      super(var1);
   }

   public UINT16(long var1) {
      super(var1);
   }

   protected UINT16(ASN1Integer var1) {
      super(var1);
   }

   public static UINT16 getInstance(Object var0) {
      if (var0 instanceof UINT16) {
         return (UINT16)var0;
      } else {
         return var0 != null ? new UINT16(ASN1Integer.getInstance(var0)) : null;
      }
   }

   public static UINT16 valueOf(int var0) {
      return new UINT16(var0);
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
