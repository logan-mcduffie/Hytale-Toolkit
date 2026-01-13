package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class SymmAlgorithm extends ASN1Enumerated {
   public static final SymmAlgorithm aes128Ccm = new SymmAlgorithm(BigInteger.ZERO);

   public SymmAlgorithm(BigInteger var1) {
      super(var1);
      this.assertValues();
   }

   private SymmAlgorithm(ASN1Enumerated var1) {
      super(var1.getValue());
      this.assertValues();
   }

   protected void assertValues() {
      switch (BigIntegers.intValueExact(this.getValue())) {
         case 0:
            return;
         default:
            throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
      }
   }

   public static SymmAlgorithm getInstance(Object var0) {
      if (var0 instanceof SymmAlgorithm) {
         return (SymmAlgorithm)var0;
      } else {
         return var0 != null ? new SymmAlgorithm(ASN1Enumerated.getInstance(var0)) : null;
      }
   }
}
