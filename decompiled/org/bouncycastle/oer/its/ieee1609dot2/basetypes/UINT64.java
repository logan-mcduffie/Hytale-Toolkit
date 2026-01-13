package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;

public class UINT64 extends UintBase {
   private static final BigInteger MAX = new BigInteger("18446744073709551615");

   public UINT64(BigInteger var1) {
      super(var1);
   }

   public UINT64(int var1) {
      super(var1);
   }

   public UINT64(long var1) {
      super(var1);
   }

   protected UINT64(ASN1Integer var1) {
      super(var1);
   }

   public static UINT64 getInstance(Object var0) {
      if (var0 instanceof UINT64) {
         return (UINT64)var0;
      } else {
         return var0 != null ? new UINT64(ASN1Integer.getInstance(var0)) : null;
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
