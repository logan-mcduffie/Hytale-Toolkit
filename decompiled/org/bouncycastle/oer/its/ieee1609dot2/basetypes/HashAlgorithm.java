package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class HashAlgorithm extends ASN1Enumerated {
   public static final HashAlgorithm sha256 = new HashAlgorithm(BigInteger.ZERO);
   public static final HashAlgorithm sha384 = new HashAlgorithm(BigIntegers.ONE);

   public HashAlgorithm(BigInteger var1) {
      super(var1);
      this.assertValues();
   }

   private HashAlgorithm(ASN1Enumerated var1) {
      this(var1.getValue());
   }

   public static HashAlgorithm getInstance(Object var0) {
      if (var0 instanceof HashAlgorithm) {
         return (HashAlgorithm)var0;
      } else {
         return var0 != null ? new HashAlgorithm(ASN1Enumerated.getInstance(var0)) : null;
      }
   }

   protected void assertValues() {
      switch (BigIntegers.intValueExact(this.getValue())) {
         case 0:
         case 1:
            return;
         default:
            throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
      }
   }
}
